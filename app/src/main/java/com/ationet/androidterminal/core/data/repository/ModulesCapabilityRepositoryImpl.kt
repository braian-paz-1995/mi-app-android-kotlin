// File: ModulesCapabilityRepositoryImpl.kt
package com.ationet.androidterminal.core.data.repository

import com.ationet.androidterminal.core.data.local.room.entity.tm.modules.ModulesCapabilityEntity
import com.ationet.androidterminal.core.data.local.room.tm.ModulesCapabilityDao
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.repository.ModulesCapabilityRepository
import com.ationet.androidterminal.core.domain.use_case.ationet.RequestModules
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KProperty1
import kotlin.time.Duration.Companion.hours

@Singleton
class ModulesCapabilityRepositoryImpl @Inject constructor(
    private val dao: ModulesCapabilityDao,
    private val configurationUseCase: ConfigurationUseCase,
    private val requestModules: RequestModules
) : ModulesCapabilityRepository {

    data class ModulesStatus(
        val responseCode: String?,
        val responseText: String?
    )

    companion object {
        private val TTL_OK = 24.hours
        private val TTL_FAIL = 1.hours
    }

    // Expuesto para que el VM pueda leer lo último guardado
    suspend fun getLastStatus(): ModulesStatus? {
        val e = dao.getOnce() ?: return null
        return ModulesStatus(
            responseCode = e.responseCode,
            responseText = e.responseText
        )
    }

    // -------------------------
    // API
    // -------------------------
    override fun observeEnabledModules(): Flow<Set<Configuration.ModuleType>> =
        dao.observe().map { mapEnabledModules(it) }

    override suspend fun getEnabledModulesOnce(): Set<Configuration.ModuleType> =
        mapEnabledModules(dao.getOnce())

    override suspend fun refreshIfNeeded(force: Boolean): Result<Set<Configuration.ModuleType>> {
        val now = System.currentTimeMillis()
        val cfg = configurationUseCase.getConfiguration()

        val cached = dao.getOnce()
        val currentTerminalId = cfg.ationet.terminalId.trim()
        val currentNativeUrl = cfg.ationet.nativeUrl.trim().removeSuffix("/")

        val terminalChanged = cached?.terminalId?.isNotBlank() == true &&
                cached.terminalId.trim() != currentTerminalId
        val urlChanged = cached?.nativeUrl?.isNotBlank() == true &&
                cached.nativeUrl.trim().removeSuffix("/") != currentNativeUrl

        val effectiveCached = if (terminalChanged || urlChanged) null else cached
        val needsRefresh = force || effectiveCached == null || effectiveCached.expiresAtMillis <= now

        if (!needsRefresh) {
            return Result.success(mapEnabledModules(effectiveCached))
        }

        val configReady = currentTerminalId.isNotBlank() && currentNativeUrl.isNotBlank()
        if (!configReady) {
            return effectiveCached?.let { Result.success(mapEnabledModules(it)) }
                ?: Result.failure(IllegalStateException("CONFIG_NOT_READY"))
        }

        // ----------- Llamada remota -----------
        val call = requestModules()

        return call.fold(
            onSuccess = { dto ->
                val raw = dto.rawResponseJson?.takeIf { it.isNotBlank() }

                val ok = isOk(raw, dto.response)
                val (b2c, gift, fleet, loy, off) = extractFlags(dto.response, raw)

                val code = extractResponseCodeFromJson(raw)
                    ?: extractStringProp(dto.response, "responseCode", "code")
                val text = extractResponseTextFromJson(raw)
                    ?: getMessageFromDto(dto.response)
                    ?: code

                val ttl = if (ok) TTL_OK else TTL_FAIL
                val entity = ModulesCapabilityEntity(
                    id = ModulesCapabilityEntity.SINGLETON_ID,
                    terminalId = currentTerminalId,
                    supportsCardsB2C = b2c,
                    supportsGiftCard = gift,
                    supportsFleet = fleet,
                    supportsLoyalty = loy,
                    supportsOffline = off,
                    lastVersion = 0,
                    fetchedAtMillis = now,
                    expiresAtMillis = now + ttl.inWholeMilliseconds,
                    rawRequestJson = dto.rawRequestJson.orEmpty(),
                    nativeUrl = currentNativeUrl,
                    rawResponseJson = raw,
                    lastAttemptOk = ok,
                    lastHttpStatus = if (ok) 200 else null,
                    responseCode = code,
                    responseText = text,
                    lastErrorMessage = if (ok) null else text
                )

                dao.upsert(entity)
                Result.success(mapEnabledModules(entity))
            },
            onFailure = { e ->
                val prev = effectiveCached
                val ttl = TTL_FAIL
                val msg = e.message
                val entity = ModulesCapabilityEntity(
                    id = ModulesCapabilityEntity.SINGLETON_ID,
                    terminalId = currentTerminalId,
                    supportsCardsB2C = prev?.supportsCardsB2C ?: false,
                    supportsGiftCard = prev?.supportsGiftCard ?: false,
                    supportsFleet = prev?.supportsFleet ?: false,
                    supportsLoyalty = prev?.supportsLoyalty ?: false,
                    supportsOffline = prev?.supportsOffline ?: false,
                    lastVersion = 0,
                    fetchedAtMillis = now,
                    expiresAtMillis = now + ttl.inWholeMilliseconds,
                    rawRequestJson = "",
                    nativeUrl = currentNativeUrl,
                    rawResponseJson = null,
                    lastAttemptOk = false,
                    lastHttpStatus = null,
                    responseCode = null,
                    responseText = msg,
                    lastErrorMessage = msg
                )
                dao.upsert(entity)
                Result.success(mapEnabledModules(entity))
            }
        )
    }

    // -------------------------
    // Helpers: OK / Flags
    // -------------------------
    private fun isOk(rawJson: String?, resp: NativeRequest?): Boolean {
        val okJson = runCatching {
            val o = rawJson?.let { JSONObject(it) } ?: return@runCatching false
            val code = o.optString("ResponseCode", o.opt("code")?.toString() ?: "")
            val tx = o.optString("TransactionCode")
            isZeroLike(code) || tx == "516"
        }.getOrDefault(false)

        val okDto = (resp?.transactionCode?.toString() == "516")
                || listOf(
            resp?.supportsCardsB2C,
            resp?.supportsGiftCard,
            resp?.supportsFleet,
            resp?.supportsLoyalty,
            resp?.supportsOffline
        ).any { it == true }

        return okJson || okDto
    }

    private fun isZeroLike(code: String?): Boolean {
        if (code.isNullOrBlank()) return false
        val t = code.trim()
        if (t.all { it == '0' }) return true
        return t.toIntOrNull() == 0
    }

    private data class Quad(val b2c: Boolean, val gift: Boolean, val fleet: Boolean, val loy: Boolean, val off: Boolean)

    private fun extractFlags(resp: NativeRequest?, rawJson: String?): Quad {
        val b2cDto = resp?.supportsCardsB2C == true
        val giftDto = resp?.supportsGiftCard == true
        val fleetDto = resp?.supportsFleet == true
        val loyDto = resp?.supportsLoyalty == true
        val offDto = resp?.supportsOffline == true
        if (b2cDto || giftDto || fleetDto || loyDto || offDto) {
            return Quad(b2cDto, giftDto, fleetDto, loyDto, offDto)
        }

        val o = runCatching { rawJson?.let { JSONObject(it) } }.getOrNull()
        fun flag(vararg keys: String): Boolean {
            if (o == null) return false
            for (k in keys) {
                val v = o.opt(k)
                if ((v as? Boolean) == true) return true
                if (v?.toString()?.equals("true", true) == true) return true
            }
            return false
        }
        val b2c = flag("supportsCardsB2C", "SupportsCardsB2C")
        val gift = flag("supportsGiftCard", "SupportsGiftCard")
        val fleet = flag("supportsFleet", "SupportsFleet")
        val loy = flag("SupportsLoyalty", "supportsLoyalty")
        val off = flag("SupportsOffline", "supportsOffline")
        return Quad(b2c, gift, fleet, loy, off)
    }

    // -------------------------
    // Helpers: code / text
    // -------------------------
    private fun extractResponseCodeFromJson(rawJson: String?): String? = runCatching {
        val o = rawJson?.let { JSONObject(it) } ?: return null
        (o.opt("ResponseCode") ?: o.opt("code"))?.toString()?.takeIf { it.isNotBlank() }
    }.getOrNull()

    private fun extractResponseTextFromJson(rawJson: String?): String? = runCatching {
        val o = rawJson?.let { JSONObject(it) } ?: return null
        listOf("ResponseText", "ResponseMessage", "message", "error", "ResponseError")
            .asSequence()
            .mapNotNull { k -> o.opt(k)?.toString() }
            .firstOrNull { it.isNotBlank() }
            ?.trim()
    }.getOrNull()

    // Busca una propiedad String en el DTO (sin usar toString()).
    private fun extractStringProp(obj: Any?, vararg names: String): String? {
        if (obj == null) return null
        // KProperty
        runCatching {
            val props = obj::class.members.filterIsInstance<KProperty1<Any, *>>()
            for (n in names) {
                props.firstOrNull { it.name == n }?.get(obj)?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
            }
        }
        // getters Java
        for (n in names) {
            val getter = "get" + n.replaceFirstChar { it.titlecase() }
            obj.javaClass.methods.firstOrNull { it.name == getter && it.parameterCount == 0 }?.let { m ->
                val v = m.invoke(obj)?.toString()
                if (!v.isNullOrBlank()) return v
            }
        }
        return null
    }

    // Mensaje del DTO: priorizamos "message" y variantes, sin concatenar campos.
    private fun getMessageFromDto(resp: NativeRequest?): String? =
        extractStringProp(
            resp,
            "message", "responseText", "responseMessage", "responseError", "longResponseText"
        )?.trim()

    // -------------------------
    // Map enabled
    // -------------------------
    private fun mapEnabledModules(entity: ModulesCapabilityEntity?): Set<Configuration.ModuleType> {
        val enabled = mutableSetOf(Configuration.ModuleType.OTHER)
        if (entity == null) return enabled
        if (entity.supportsCardsB2C) enabled += Configuration.ModuleType.CONSUMER_CARD
        if (entity.supportsGiftCard) enabled += Configuration.ModuleType.GIFT_CARD
        if (entity.supportsFleet) enabled += Configuration.ModuleType.FLEET
        if (entity.supportsLoyalty) enabled += Configuration.ModuleType.LOYALTY
        if (entity.supportsOffline) enabled += Configuration.ModuleType.LOCAL_AGENT
        return enabled
    }
}
