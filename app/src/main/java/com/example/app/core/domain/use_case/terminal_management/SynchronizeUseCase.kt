package com.ationet.androidterminal.core.domain.use_case.terminal_management

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import com.atio.log.Logger
import com.atio.log.util.d
import com.atio.log.util.e
import com.atio.log.util.i
import com.atio.log.util.w
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.local.configurationDataStore
import com.ationet.androidterminal.core.domain.exception.HostUrlNotConfiguredException
import com.ationet.androidterminal.core.domain.exception.TerminalIdNotConfiguredException
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.domain.use_case.configuration.UpdateConfiguration
import com.ationet.androidterminal.core.domain.util.validateUrlString
import com.ationet.androidterminal.maintenance.settings.domain.use_case.ValidateAtionetInputsUseCase
import com.ationet.androidterminal.maintenance.settings.domain.use_case.ValidateTerminalManagementInputsUseCase
import com.ationet.androidterminal.maintenance.settings.domain.util.AtionetInputResult
import com.ationet.androidterminal.maintenance.settings.domain.util.TerminalManagementInputResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.zip.ZipFile
import javax.inject.Inject
import kotlin.time.DurationUnit

sealed interface SynchronizationResult {
    data object Ok : SynchronizationResult
    data class ValidationError(@StringRes val message: Int) : SynchronizationResult
    data object Failed : SynchronizationResult
}

class SynchronizeUseCase @Inject constructor(
    private val pushParameters: PushParametersUseCase,
    private val pushLog: PushLogUseCase,
    private val getNews: GetNewsUseCase,
    private val keepAlive: KeepAliveUseCase,
    private val getResource: GetResourceUseCase,
    private val getConfiguration: GetConfiguration,
    private val updateConfiguration: UpdateConfiguration,
    @ApplicationContext private val context: Context,
    private val validateAtionetInputsUseCase: ValidateAtionetInputsUseCase,
    private val validateTerminalManagementInputsUseCase: ValidateTerminalManagementInputsUseCase
) {
    suspend operator fun invoke(): SynchronizationResult {
        return synchronize()
    }

    private suspend fun synchronize(): SynchronizationResult {
        var scheduleId: String? = null
        val coroutineContext = currentCoroutineContext()
        val configuration: Configuration = getConfiguration()
        var result: SynchronizationResult = SynchronizationResult.Ok
        var hasConfigurationErrors = false

        Logger.d(
            TAG,
            "Starting synchronization. TM URL='${configuration.terminalManagement.terminalManagementUrl}', " +
                    "TerminalId='${configuration.ationet.terminalId}'"
        )

        do {
            Logger.d(TAG, "Requesting keep alive with schedule id: '$scheduleId'")
            val keepAliveResult = try {
                keepAlive.invoke(
                    host = configuration.terminalManagement.terminalManagementUrl,
                    terminalId = configuration.ationet.terminalId,
                    scheduleId = scheduleId
                )
            } catch (e: CancellationException) {
                Logger.d(TAG, "Synchronization cancelled")
                throw e
            } catch (e: HostUrlNotConfiguredException) {
                Logger.d(TAG, "Host url not configured")
                return SynchronizationResult.ValidationError(message = R.string.url_is_empty)
            } catch (e: TerminalIdNotConfiguredException) {
                Logger.d(TAG, "Terminal id not configured")
                return SynchronizationResult.ValidationError(message = R.string.terminal_id_id_empty)
            } catch (e: Throwable) {
                Logger.e(TAG, "Keep alive error", e)
                return SynchronizationResult.Failed
            }

            Logger.d(TAG, "KeepAlive raw result: $keepAliveResult")

            if (keepAliveResult == null) {
                Logger.w(TAG, "Keep alive failed (null result)")
                return SynchronizationResult.Failed
            }

            val hasSchedule = !keepAliveResult.idSchedule.isNullOrBlank()
            val scheduleIsEqual = keepAliveResult.idSchedule == scheduleId

            Logger.d(
                TAG,
                "KeepAlive parsed: hasSchedule=$hasSchedule, scheduleIsEqual=$scheduleIsEqual, idSchedule='${keepAliveResult.idSchedule}'"
            )

            if (!hasSchedule) {
                Log.d(TAG, "Keep alive succeeded with no schedule")

                /* Push parameters*/
                pushParameters(
                    host = configuration.terminalManagement.terminalManagementUrl,
                    terminalId = configuration.ationet.terminalId,
                )

                if (configuration.terminalManagement.sendReportAutomatically) {
                    /* Push and remove logs */
                    pushLog.invoke(
                        host = configuration.terminalManagement.terminalManagementUrl,
                        terminalId = configuration.ationet.terminalId,
                        archiveCurrent = false
                    )
                }
                return if (hasConfigurationErrors) {
                    if (result is SynchronizationResult.ValidationError) {
                        SynchronizationResult.ValidationError(result.message)
                    } else {
                        SynchronizationResult.Failed
                    }
                } else {
                    SynchronizationResult.Ok
                }
            }

            Logger.d(
                TAG,
                "Keep alive succeeded with new schedule. Schedule id: '${keepAliveResult.idSchedule?.uppercase()}'"
            )

            scheduleId = keepAliveResult.idSchedule!!
            if (scheduleIsEqual) {
                Logger.w(TAG, "Schedule id already processed: '$scheduleId'")

                /* Push parameters*/
                pushParameters(
                    host = configuration.terminalManagement.terminalManagementUrl,
                    terminalId = configuration.ationet.terminalId,
                )

                if (configuration.terminalManagement.sendReportAutomatically) {
                    /* Push and remove logs */
                    pushLog.invoke(
                        host = configuration.terminalManagement.terminalManagementUrl,
                        terminalId = configuration.ationet.terminalId,
                        archiveCurrent = false
                    )
                }

                return if (hasConfigurationErrors) {
                    if (result is SynchronizationResult.ValidationError) {
                        SynchronizationResult.ValidationError(result.message)
                    } else {
                        SynchronizationResult.Failed
                    }
                } else {
                    SynchronizationResult.Ok
                }
            } else {
                Logger.d(TAG, "Getting news for schedule id '${scheduleId.uppercase()}'")
                val getNewsResult = try {
                    getNews.invoke(
                        host = configuration.terminalManagement.terminalManagementUrl,
                        terminalId = configuration.ationet.terminalId,
                        scheduleId = scheduleId
                    )
                } catch (e: CancellationException) {
                    Logger.d(TAG, "Synchronization cancelled")
                    throw e
                } catch (e: Throwable) {
                    Logger.e(TAG, "Get news error", e)
                    return SynchronizationResult.Failed
                }

                Logger.d(TAG, "GetNews raw result: $getNewsResult")

                if (getNewsResult == null) {
                    Logger.w(TAG, "Schedule empty (getNewsResult is null)")
                } else {
                    val configurations = getNewsResult.configurations
                    Logger.d(TAG, "GetNews configurations payload: '$configurations'")

                    if (!configurations.isNullOrBlank()) {

                        try {
                            val configurationValues =
                                Json.decodeFromString<Map<String, String?>>(configurations)
                            if (configurationValues.isNotEmpty()) {
                                Logger.d(
                                    TAG,
                                    "There are ${configurationValues.size} configuration entries to be applied in schedule id '${scheduleId.uppercase()}'"
                                )

                                result = withContext(Dispatchers.IO) {
                                    val configurationFormatted = validateConfiguration(configurationValues)
                                    val changed = configurationFormatted != configurationValues
                                    Logger.d(TAG, "Configuration validation changed=$changed")

                                    if (changed) {
                                        updateConfiguration.invoke(parameters = configurationFormatted)
                                        hasConfigurationErrors = true
                                        SynchronizationResult.ValidationError(R.string.all_the_changes_received_via_the_last_synchronization)
                                    } else {
                                        updateConfiguration.invoke(parameters = configurationValues)
                                        SynchronizationResult.Ok
                                    }
                                }
                            } else {
                                Logger.d(TAG, "Configuration values empty in schedule: '${scheduleId.uppercase()}'")
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Throwable) {
                            Logger.e(TAG, "Failed to apply configuration", e)
                        }
                    }

                    val resourcesUrl = getNewsResult.resourcesUrl
                    val firmwareUrl = getNewsResult.firmwareUrl
                    Logger.d(TAG, "GetNews firmwareUrl='$firmwareUrl'")

                    if (!resourcesUrl.isNullOrBlank()) {
                        Logger.d(
                            TAG,
                            "There are resources to be downloaded in schedule id '${scheduleId.uppercase()}'"
                        )
                        getResources(resourcesUrl)
                    }

                    if (!firmwareUrl.isNullOrBlank()) {
                        Logger.d(
                            TAG,
                            "There is a firmware package to be downloaded in schedule id '${scheduleId.uppercase()}'"
                        )
                        downloadFirmwarePackage(firmwareUrl)
                    }
                    Logger.d(TAG, "GetNews resourcesUrl='$resourcesUrl'")
                    if (!resourcesUrl.isNullOrBlank()) {
                        Logger.d(
                            TAG,
                            "There are resources to be downloaded in schedule id '${scheduleId.uppercase()}'"
                        )
                        getResources(resourcesUrl)
                    }
                }

            }
        } while (coroutineContext.isActive)

        Logger.w(TAG, "Synchronization loop finished because coroutine is no longer active")
        return SynchronizationResult.Failed
    }

    private suspend fun downloadFirmwarePackage(firmwareUrlString: String) {
        Logger.d(TAG, "downloadFirmwarePackage() called with URL='$firmwareUrlString'")

        try {
            val isValidUrl = Patterns.WEB_URL.matcher(firmwareUrlString).matches()
            if (!isValidUrl) {
                Logger.w(TAG, "Invalid firmware URL: '$firmwareUrlString'")
                return
            }

            // Carpeta accesible: /sdcard/Android/data/<paquete>/files
            val externalDir = context.getExternalFilesDir(null)
            if (externalDir == null) {
                Logger.e(TAG, "Cannot download firmware: externalDir is null")
                return
            }

            val zipFile = File(externalDir, "RevoposUpdate.zip")
            Logger.d(TAG, "Downloading firmware directly to: '${zipFile.absolutePath}'")

            // Descargar directo el ZIP
            withContext(Dispatchers.IO) {
                URL(firmwareUrlString).openStream().use { input ->
                    zipFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val zipSize = zipFile.length()

            if (zipSize <= 0) {
                Logger.e(
                    TAG,
                    "✘ Firmware download FAILED: file saved but size is 0 bytes ('${zipFile.absolutePath}')"
                )
                return
            }

            Logger.i(
                TAG,
                "✔ Firmware ZIP downloaded SUCCESSFULLY to '${zipFile.absolutePath}', size=${zipSize} bytes"
            )

            // 👉 Ahora descomprimir y extraer el APK con nombre fijo
            val apkFile = unzipFirmwareApk(zipFile, externalDir, context)

            if (apkFile != null && apkFile.length() > 0) {
                Logger.i(
                    TAG,
                    "✔ Firmware APK extracted to '${apkFile.absolutePath}', size=${apkFile.length()} bytes"
                )
                Logger.i(
                    TAG,
                    "To install from a connected PC, run:\nadb install -r \"${apkFile.absolutePath}\""
                )
                promptInstallApk(apkFile)
            } else {
                Logger.e(TAG, "✘ Failed to extract APK from firmware ZIP '${zipFile.absolutePath}'")
            }

        } catch (e: Throwable) {
            Logger.e(TAG, "✘ Failed to download firmware package", e)
        }
    }



    /**
     * Descomprime el ZIP de firmware, busca un .apk adentro
     * y lo copia con nombre fijo (por ejemplo 'RevoposUpdate.apk')
     * en la misma carpeta accesible (externalDir).
     */
    private suspend fun unzipFirmwareApk(
        zipFile: File,
        externalDir: File,
        context: Context
    ): File? = withContext(Dispatchers.IO) {
        Logger.d(TAG, "unzipFirmwareApk() called with zip='${zipFile.absolutePath}'")

        if (!zipFile.exists()) {
            Logger.e(TAG, "unzipFirmwareApk: zip file does not exist '${zipFile.absolutePath}'")
            return@withContext null
        }

        val targetApk = File(externalDir, "RevoposUpdate.apk")

        // 👉 Directorio de descargas PÚBLICO de la terminal: /sdcard/Download
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadsDir != null && !downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val downloadsApk = downloadsDir?.let { File(it, "RevoposUpdate.apk") }

        try {
            ZipFile(zipFile).use { zip ->
                val entries = zip.entries()

                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    Logger.d(
                        TAG,
                        "Inspecting ZIP entry: name='${entry.name}', isDirectory=${entry.isDirectory}"
                    )

                    if (entry.isDirectory) continue
                    if (!entry.name.endsWith(".apk", ignoreCase = true)) continue

                    Logger.d(
                        TAG,
                        "APK entry found in ZIP: '${entry.name}', extracting to '${targetApk.absolutePath}'"
                    )

                    // 1) Extraemos al directorio de la app
                    zip.getInputStream(entry).use { input ->
                        targetApk.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    Logger.d(
                        TAG,
                        "APK extracted, path='${targetApk.absolutePath}', size=${targetApk.length()} bytes"
                    )

                    // 2) Lo copiamos también a /sdcard/Download (si existe)
                    downloadsApk?.let { dest ->
                        try {
                            targetApk.copyTo(dest, overwrite = true)
                            Logger.d(
                                TAG,
                                "APK copied to public Downloads: '${dest.absolutePath}', size=${dest.length()} bytes"
                            )
                        } catch (e: Throwable) {
                            Logger.e(TAG, "Error copying APK to public Downloads '${dest.absolutePath}'", e)
                        }
                    }

                    return@withContext targetApk
                }
            }

            Logger.w(TAG, "No .apk entry found inside firmware ZIP '${zipFile.absolutePath}'")
            null
        } catch (e: Throwable) {
            Logger.e(TAG, "Error while unzipping firmware APK from '${zipFile.absolutePath}'", e)
            null
        }
    }
    private fun promptInstallApk(apkFile: File) {
        try {
            Logger.d(TAG, "promptInstallApk() called with '${apkFile.absolutePath}'")

            // 1) Validar nombre del archivo (opcional, pero vos lo querías fijo)
            if (!apkFile.name.equals("RevoposUpdate.apk", ignoreCase = true)) {
                Logger.w(
                    TAG,
                    "Skipping install: APK name '${apkFile.name}' does not match 'RevoposUpdate.apk'"
                )
                return
            }

            // 2) Validar existencia
            if (!apkFile.exists()) {
                Logger.e(TAG, "Cannot install: APK file does not exist: '${apkFile.absolutePath}'")
                return
            }

            // 3) Construir Uri con FileProvider
            val authority = "${context.packageName}.provider"
            Logger.d(TAG, "Using FileProvider authority='$authority'")

            val apkUri = FileProvider.getUriForFile(
                context,
                authority,
                apkFile
            )

            Logger.d(TAG, "APK Uri = '$apkUri'")

            // 4) Intent principal: ACTION_VIEW
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // 5) Loguear qué actividades (si hay) pueden manejar ACTION_VIEW
            val pm = context.packageManager
            val viewHandlers = pm.queryIntentActivities(viewIntent, 0)

            if (viewHandlers.isEmpty()) {
                Logger.e(
                    TAG,
                    "No Activity found to handle APK install via ACTION_VIEW " +
                            "(application/vnd.android.package-archive). " +
                            "The device may not expose a package installer for third-party apps."
                )
            } else {
                Logger.d(TAG, "Handlers for ACTION_VIEW (APK):")
                viewHandlers.forEach {
                    Logger.d(
                        TAG,
                        "  • ${it.activityInfo.packageName}/${it.activityInfo.name}"
                    )
                }
            }

            // 6) Intent alternativo: ACTION_INSTALL_PACKAGE
            val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                data = apkUri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            }

            val installHandlers = pm.queryIntentActivities(installIntent, 0)

            if (installHandlers.isEmpty()) {
                Logger.e(
                    TAG,
                    "No Activity found to handle APK install via ACTION_INSTALL_PACKAGE either. " +
                            "Automatic installation from the app is likely not allowed on this device."
                )
            } else {
                Logger.d(TAG, "Handlers for ACTION_INSTALL_PACKAGE (APK):")
                installHandlers.forEach {
                    Logger.d(
                        TAG,
                        "  • ${it.activityInfo.packageName}/${it.activityInfo.name}"
                    )
                }
            }

            // 7) Decidir qué intent usar
            val intentToUse: Intent? = when {
                viewHandlers.isNotEmpty() -> {
                    Logger.i(TAG, "Using ACTION_VIEW to launch APK installer")
                    viewIntent
                }

                installHandlers.isNotEmpty() -> {
                    Logger.i(TAG, "Using ACTION_INSTALL_PACKAGE to launch APK installer")
                    installIntent
                }

                else -> {
                    Logger.e(
                        TAG,
                        "No available installer Activity for either ACTION_VIEW or ACTION_INSTALL_PACKAGE. " +
                                "Installation cannot be started from the app on this device.\n" +
                                "You can still install via ADB:\n" +
                                "  adb install -r \"${apkFile.absolutePath}\""
                    )
                    null
                }
            }

            if (intentToUse == null) return

            Logger.i(TAG, "Launching installer for APK with intent: $intentToUse")
            context.startActivity(intentToUse)

        } catch (e: android.content.ActivityNotFoundException) {
            Logger.e(
                TAG,
                "ActivityNotFoundException while trying to launch APK installer. " +
                        "No system component is able to handle the install intent on this device.",
                e
            )
            Logger.e(
                TAG,
                "Fallback: install via ADB with:\nadb install -r \"${apkFile.absolutePath}\""
            )
        } catch (e: Throwable) {
            Logger.e(TAG, "Failed to launch APK installer due to unexpected error", e)
        }
    }

    private suspend fun pushParameters(
        host: String,
        terminalId: String?,
    ) {
        Logger.d(TAG, "Pushing parameters to host='$host', terminalId='$terminalId'")
        val pushParametersResult = try {
            val configurationValues = context.configurationDataStore.data.map { preferences ->
                preferences.asMap().map {
                    it.key.name to it.value.toString()
                }.toMap()
            }.first()

            Logger.d(TAG, "Parameters to push (keys): ${configurationValues.keys.joinToString()}")

            pushParameters.invoke(
                host = host,
                terminalId = terminalId,
                parameters = configurationValues
            )
        } catch (e: CancellationException) {
            Logger.d(TAG, "Synchronization cancelled while pushing parameters")
            throw e
        } catch (e: Throwable) {
            Logger.e(TAG, "Push parameters error", e)
            return
        }

        if (pushParametersResult == null) {
            Logger.w(TAG, "Failed to push parameters (result is null)")
        } else {
            Logger.d(TAG, "Push parameters completed successfully")
        }
    }

    // region Resources
    private suspend fun getResources(resourcesUrlString: String): SynchronizationResult {
        val configEntryName = "aat.config"

        Logger.d(TAG, "getResources() called with URL='$resourcesUrlString'")

        try {
            val isValidUrl = Patterns.WEB_URL.matcher(resourcesUrlString).matches()
            if (!isValidUrl) {
                Logger.w(TAG, "Invalid URL: '$resourcesUrlString'")
                return SynchronizationResult.ValidationError(message = R.string.invalid_url)
            }

            Logger.d(TAG, "Requesting resource package from URL='$resourcesUrlString'")
            val resourcesPackage = getResource.invoke(resourcesUrlString)

            Logger.d(
                TAG,
                "Resource package downloaded to: '${resourcesPackage?.absolutePath}', size=${resourcesPackage?.length()} bytes"
            )

            val resourcesZip = withContext(Dispatchers.IO) {
                ZipFile(resourcesPackage)
            }

            Logger.d(TAG, "Opening ZIP file: '${resourcesPackage?.absolutePath}'")

            var hasValidationErrors = false
            /* Extract resources file */
            resourcesZip.use { file ->
                val entries = file.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    Logger.d(
                        TAG,
                        "Processing ZIP entry: name='${entry.name}', isDirectory=${entry.isDirectory}, " +
                                "size=${entry.size}, compressedSize=${entry.compressedSize}"
                    )

                    val resource = getResourceFile(context.applicationContext, entry.name)
                    Logger.d(TAG, "Mapped entry '${entry.name}' to file='${resource.absolutePath}'")

                    /* This zip entry is just a directory. Create it */
                    if (entry.isDirectory) {
                        withContext(Dispatchers.IO) {
                            val created = resource.mkdirs()
                            Logger.d(
                                TAG,
                                "Directory entry: '${entry.name}', mkdirs=$created, exists=${resource.exists()}"
                            )
                        }

                        continue
                    }

                    /* Zip entry is a file */
                    val fileStream = resourcesZip.getInputStream(entry)
                    /* Entry is NAT configuration file */
                    if (entry.name == configEntryName) {
                        Logger.d(TAG, "Entry '${entry.name}' identified as configuration file")
                        val result = processConfigurationResource(fileStream)
                        fileStream.close()

                        if (result is SynchronizationResult.ValidationError) {
                            Logger.w(TAG, "Configuration resource produced validation errors")
                            hasValidationErrors = true
                        } else {
                            Logger.d(TAG, "Configuration resource applied successfully")
                        }
                        continue
                    }

                    Logger.d(TAG, "Entry '${entry.name}' is a regular resource file, extracting...")
                    processResourceEntry(fileStream, resource)
                    fileStream.close()
                }
            }

            val finalResult =
                if (hasValidationErrors) SynchronizationResult.Failed else SynchronizationResult.Ok
            Logger.d(
                TAG,
                "getResources() finished. hasValidationErrors=$hasValidationErrors, result=$finalResult"
            )
            return finalResult
        } catch (e: Throwable) {
            Logger.e(TAG, "Failed to get resources", e)
            return SynchronizationResult.Failed
        }
    }

    private fun getResourceFile(
        context: Context,
        resourceName: String
    ): File {
        val baseDir = context.filesDir
        val file = File(baseDir, resourceName)
        Logger.d(
            TAG,
            "getResourceFile(): baseDir='${baseDir.absolutePath}', resourceName='$resourceName', file='${file.absolutePath}'"
        )
        return file
    }

    private suspend fun processConfigurationResource(stream: InputStream): SynchronizationResult {
        try {
            val configurationMap = readConfigurationValues(stream)

            Logger.i(TAG, "Applying configuration from configuration file (${configurationMap.size} entries)")
            Logger.d(TAG, "Configuration keys from aat.config: ${configurationMap.keys.joinToString()}")

            return withContext(Dispatchers.IO) {
                val configurationFormatted = validateConfiguration(configurationMap)
                val changed = configurationFormatted != configurationMap
                Logger.d(TAG, "Configuration validation changed=$changed")

                if (changed) {
                    Logger.d(TAG, "Updating configuration with formatted values")
                    updateConfiguration.invoke(parameters = configurationFormatted)
                    SynchronizationResult.ValidationError(R.string.all_the_changes_received_via_the_last_synchronization)
                } else {
                    Logger.d(TAG, "Updating configuration with original values (no changes needed)")
                    updateConfiguration.invoke(parameters = configurationMap)
                    SynchronizationResult.Ok
                }
            }
        } catch (e: Throwable) {
            Logger.e(TAG, "Failed to process configuration resource", e)
            return SynchronizationResult.Failed
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun readConfigurationValues(reader: InputStream): Map<String, String?> {
        Logger.d(TAG, "Reading configuration values from aat.config stream...")
        val map = resourceJson.decodeFromStream(FlatMapSerializer, reader)
        Logger.d(TAG, "Configuration values parsed from aat.config: size=${map.size}")
        return map
    }

    private suspend fun processResourceEntry(inputStream: InputStream, file: File) {
        /**
         * Ensures parent directories exists
         */
        val parent = file.parentFile
        if (parent != null) {
            val created = parent.mkdirs()
            Logger.d(
                TAG,
                "Ensuring parent dir exists for file='${file.path}'. parent='${parent.path}', mkdirs=$created, exists=${parent.exists()}"
            )
        }

        /**
         * Ensures file exists. createNewFile() creates atomically a file if and only if it doesn't
         * already exists.
         */
        val fileCreated = withContext(Dispatchers.IO) {
            file.createNewFile()
        }

        if (fileCreated) {
            Logger.d(TAG, "Created resource file ${file.path}")
        } else {
            Logger.d(TAG, "Resource file already existed: ${file.path}")
        }

        file.outputStream().buffered().use { stream ->
            var totalBytes = 0L
            val copied = inputStream.copyTo(stream, bufferSize = 4096).also { totalBytes = it }
            stream.flush()
            Logger.d(TAG, "Wrote $totalBytes bytes to '${file.path}' (copyTo returned $copied)")
        }
    }
    //endregion

    private object FlatMapSerializer : KSerializer<Map<String, String?>> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FlattenedMap") {
            element<Map<String, String?>>("map")
        }

        override fun deserialize(decoder: Decoder): Map<String, String?> {
            val jsonEncoder = decoder as JsonDecoder
            val element = jsonEncoder.decodeJsonElement()
            return flattenJson(element)
        }

        private fun flattenJson(
            element: JsonElement,
            prefix: String = ""
        ): Map<String, String?> {
            val map = mutableMapOf<String, String?>()

            when (element) {
                is kotlinx.serialization.json.JsonArray -> {
                    element.forEachIndexed { index, jsonElement ->
                        val arrayKey = "${prefix}__$index"
                        map.putAll(flattenJson(jsonElement, arrayKey))
                    }
                }

                is kotlinx.serialization.json.JsonObject -> {
                    for ((key, value) in element) {
                        val nestedKey = if (prefix.isEmpty()) key else "${prefix}__$key"
                        map.putAll(flattenJson(value, nestedKey))
                    }
                }

                is kotlinx.serialization.json.JsonPrimitive -> {
                    map[prefix] = element.contentOrNull
                }

                kotlinx.serialization.json.JsonNull -> {
                    map[prefix] = null
                }
            }

            return map
        }

        override fun serialize(encoder: Encoder, value: Map<String, String?>) {
            throw NotImplementedError()
        }
    }

    private fun validateConfiguration(configuration: Map<String, String?>): Map<String, String?> {
        val mutableConfiguration = configuration.toMutableMap()
        with(Configuration.Companion.Keys) {
            /* Validate Ationet */
            val nativeUrl = configuration[NATIVE_URL_KEY]
            if (nativeUrl != null) {
                if (nativeUrl.isBlank() || !validateUrlString(nativeUrl) || (!nativeUrl.startsWith("https://") && !nativeUrl.startsWith("http://"))) {
                    mutableConfiguration[NATIVE_URL_KEY] = Configuration.Companion.Defaults.DEFAULT_NATIVE_URL
                }
            }
            val visionUrl = configuration[VISION_URL_KEY]
            if (visionUrl != null) {
                if (visionUrl.isBlank() || !validateUrlString(visionUrl) || (!visionUrl.startsWith("https://") && !visionUrl.startsWith("http://"))) {
                    mutableConfiguration[VISION_URL_KEY] = Configuration.Companion.Defaults.DEFAULT_VISION_URL
                }
            }
            val ccUsername = configuration[CC_USERNAME_KEY]
            if (ccUsername != null) {
                if (ccUsername.isBlank()) {
                    mutableConfiguration[CC_USERNAME_KEY] = Configuration.Companion.Defaults.DEFAULT_USERNAME_CC
                }
            }
            val ccPassword = configuration[CC_PASSWORD_KEY]
            if (ccPassword != null) {
                if (ccPassword.isBlank()) {
                    mutableConfiguration[CC_PASSWORD_KEY] = Configuration.Companion.Defaults.DEFAULT_PASSWORD_CC
                }
            }
            val gcUsername = configuration[GC_USERNAME_KEY]
            if (gcUsername != null) {
                if (gcUsername.isBlank()) {
                    mutableConfiguration[GC_USERNAME_KEY] = Configuration.Companion.Defaults.DEFAULT_USERNAME_GC
                }
            }
            val gcPassword = configuration[GC_PASSWORD_KEY]
            if (gcPassword != null) {
                if (gcPassword.isBlank()) {
                    mutableConfiguration[GC_PASSWORD_KEY] = Configuration.Companion.Defaults.DEFAULT_PASSWORD_GC
                }
            }
            val loyaltyUsername = configuration[LOYALTY_USERNAME_KEY]
            if (loyaltyUsername != null) {
                if (loyaltyUsername.isBlank()) {
                    mutableConfiguration[LOYALTY_USERNAME_KEY] = Configuration.Companion.Defaults.DEFAULT_USERNAME_LOYALTY
                }
            }
            val loyaltyPassword = configuration[LOYALTY_PASSWORD_KEY]
            if (loyaltyPassword != null) {
                if (loyaltyPassword.isBlank()) {
                    mutableConfiguration[LOYALTY_PASSWORD_KEY] = Configuration.Companion.Defaults.DEFAULT_PASSWORD_LOYALTY
                }
            }
            val terminalIdentification = configuration[TERMINAL_ID_KEY]
            if (terminalIdentification != null) {
                if (terminalIdentification.isBlank()) {
                    mutableConfiguration[TERMINAL_ID_KEY] = Configuration.Companion.Defaults.DEFAULT_TERMINAL_ID
                }
            }

            val localAgentEnabled = configuration[LOCAL_AGENT_KEY]
            if (localAgentEnabled != null) {
                if (localAgentEnabled.toBoolean()) {
                    val localAgentIp = configuration[LOCAL_AGENT_IP_KEY]
                    if (localAgentIp != null) {
                        if (validateAtionetInputsUseCase.validateLocalAgentIp(localAgentIp) is AtionetInputResult.Failure) {
                            mutableConfiguration[LOCAL_AGENT_IP_KEY] = Configuration.Companion.Defaults.DEFAULT_LOCAL_AGENT_IP
                        }
                    }
                    val localAgentPort = configuration[LOCAL_AGENT_PORT_KEY]
                    if (localAgentPort != null) {
                        if (validateAtionetInputsUseCase.validateLocalAgentPort(localAgentPort) is AtionetInputResult.Failure) {
                            mutableConfiguration[LOCAL_AGENT_PORT_KEY] = Configuration.Companion.Defaults.DEFAULT_LOCAL_AGENT_PORT
                        }
                    }
                }
            }

            /* Validate TM */
            val terminalManagementEnabled = configuration[TERMINAL_MANAGEMENT_ENABLED_KEY]
            if (terminalManagementEnabled != null) {
                if (terminalManagementEnabled.toBoolean()) {
                    val terminalManagementUrl = configuration[TERMINAL_MANAGEMENT_URL_KEY]
                    if (terminalManagementUrl != null) {
                        if (validateTerminalManagementInputsUseCase.validateUrl(terminalManagementUrl) is TerminalManagementInputResult.Failure) {
                            mutableConfiguration[TERMINAL_MANAGEMENT_URL_KEY] = Configuration.Companion.Defaults.DEFAULT_TERMINAL_MANAGEMENT_URL
                        }
                    }
                    val terminalManagementFrequency = configuration[POLL_INTERVAL_KEY]
                    if (terminalManagementFrequency != null) {
                        if (validateTerminalManagementInputsUseCase.validateFrequency(configuration[POLL_INTERVAL_KEY].orEmpty()) is TerminalManagementInputResult.Failure) {
                            mutableConfiguration[POLL_INTERVAL_KEY] = Configuration.Companion.Defaults.DEFAULT_POLL_INTERVAL.toLong(DurationUnit.MINUTES).toString()
                        }
                    }
                }
            }
        }
        return mutableConfiguration.toMap()
    }

    companion object {
        private const val TAG = "SynchronizationUseCase"

        @OptIn(ExperimentalSerializationApi::class)
        private val resourceJson: Json = Json {
            isLenient = true
            allowTrailingComma = true
            ignoreUnknownKeys = true
        }
    }
}
