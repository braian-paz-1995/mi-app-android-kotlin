package com.ationet.androidterminal.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ationet.androidterminal.core.domain.model.configuration.Ationet
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.configuration.Fusion
import com.ationet.androidterminal.core.domain.model.configuration.Site
import com.ationet.androidterminal.core.domain.model.configuration.TerminalManagement
import com.ationet.androidterminal.core.domain.model.configuration.Ticket
import com.ationet.androidterminal.core.domain.repository.ConfigurationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ConfigurationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ConfigurationRepository() {
    private var configuration: Configuration? = null
    override val currentConfiguration: Configuration
        get() {
            val config = configuration
            if (config != null) {
                return config
            }
            val updatedConfiguration = get()
            return updatedConfiguration
        }

    override fun get(): Configuration {
        val configuration = runBlocking {
            context.configurationDataStore.data.map { preferences ->
                preferences.toConfiguration()
            }.firstOrNull()
        } ?: Configuration()
        this.configuration = configuration
        return configuration
    }

    override fun update(block: (Configuration) -> Configuration): Configuration {
        val updatedConfiguration = runBlocking {
            context.configurationDataStore.edit {
                val configuration = it.toConfiguration()
                val newConfiguration = block(configuration)
                updatePreferences(newConfiguration, it)
            }.toConfiguration()
        }
        this.configuration = updatedConfiguration
        return updatedConfiguration
    }

    private fun updatePreferences(configuration: Configuration, preferences: MutablePreferences) {
        val languageKey = stringPreferencesKey(Configuration.Companion.Keys.LANGUAGE_KEY)
        val controllerTypeKey =
            stringPreferencesKey(Configuration.Companion.Keys.CONTROLLER_TYPE_KEY)
        val selectedModuleKey =
            stringPreferencesKey(Configuration.Companion.Keys.SELECTED_MODULE_KEY)
        val fusionIPKey = stringPreferencesKey(Configuration.Companion.Keys.FUSION_IP_KEY)
        val fusionPortKey = stringPreferencesKey(Configuration.Companion.Keys.FUSION_PORT_KEY)
        val fusionPaymentTypeCodeKey =
            stringPreferencesKey(Configuration.Companion.Keys.FUSION_PAYMENT_TYPE_CODE_KEY)
        val fusionPaymentMethodKey =
            booleanPreferencesKey(Configuration.Companion.Keys.FUSION_PAYMENT_METHOD_KEY)
        val enableFinalizationVarianceKey =
            booleanPreferencesKey(Configuration.Companion.Keys.ENABLE_FINALIZATION_VARIANCE_KEY)


        val nativeUrlKey = stringPreferencesKey(Configuration.Companion.Keys.NATIVE_URL_KEY)
        val ccUserNameKey = stringPreferencesKey(Configuration.Companion.Keys.CC_USERNAME_KEY)
        val ccPasswordKey = stringPreferencesKey(Configuration.Companion.Keys.CC_PASSWORD_KEY)
        val gcUserNameKey = stringPreferencesKey(Configuration.Companion.Keys.GC_USERNAME_KEY)
        val gcPasswordKey = stringPreferencesKey(Configuration.Companion.Keys.GC_PASSWORD_KEY)
        val loyaltyUserNameKey = stringPreferencesKey(Configuration.Companion.Keys.LOYALTY_USERNAME_KEY)
        val loyaltyPasswordKey = stringPreferencesKey(Configuration.Companion.Keys.LOYALTY_PASSWORD_KEY)
        val visionUrlKey = stringPreferencesKey(Configuration.Companion.Keys.VISION_URL_KEY)
        val terminalIdKey = stringPreferencesKey(Configuration.Companion.Keys.TERMINAL_ID_KEY)
        val promptAmountTransactionKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_AMOUNT_TRANSACTION_KEY)
        val promptConsumerCardKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_CONSUMERCARD_KEY)
        val promptGiftCardKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_GIFTCARD_KEY)
        val promptLoyaltyKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_LOYALTY_KEY)
        val promptPaymentMethodEnableKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPTS_PAYMENT_METHOD_ENABLED)
        val promptLoyaltyPaymentMethodEnableKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED)
        val promptsDefaultKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPTS_DEFAULT_KEY)
        val promptAttendantIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_ATTENDANT_IDENTIFICATION_KEY)
        val promptDriverIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_DRIVER_IDENTIFICATION_KEY)
        val promptVehicleIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_VEHICLE_IDENTIFICATION_KEY)
        val promptOdometerKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_ODOMETER_KEY)
        val promptEngineHoursKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_ENGINE_HOURS_KEY)
        val promptTrailerKey = booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_TRAILER_KEY)
        val promptMiscellaneousKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_MISCELLANEOUS_KEY)
        val promptTruckUnitKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_TRUCK_UNIT_KEY)
        val promptSecondaryTrackKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_SECONDARY_TRACK_KEY)
        val promptPrimaryPINKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_PRIMARY_PIN_KEY)
        val promptSecondaryPINKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_SECONDARY_PIN_KEY)
        val localAgentKey = booleanPreferencesKey(Configuration.Companion.Keys.LOCAL_AGENT_KEY)
        val localAgentIpKey = stringPreferencesKey(Configuration.Companion.Keys.LOCAL_AGENT_IP_KEY)
        val localAgentPortKey =
            stringPreferencesKey(Configuration.Companion.Keys.LOCAL_AGENT_PORT_KEY)
        val terminalManagementEnabledKey =
            booleanPreferencesKey(Configuration.Companion.Keys.TERMINAL_MANAGEMENT_ENABLED_KEY)
        val terminalManagementUrlKey =
            stringPreferencesKey(Configuration.Companion.Keys.TERMINAL_MANAGEMENT_URL_KEY)
        val pollIntervalKey = longPreferencesKey(Configuration.Companion.Keys.POLL_INTERVAL_KEY)
        val sendReportAutomaticallyKey =
            booleanPreferencesKey(Configuration.Companion.Keys.SEND_REPORT_AUTOMATICALLY_KEY)
        val levelReportKey = stringPreferencesKey(Configuration.Companion.Keys.LEVEL_REPORT_KEY)
        /*val fileSizeKey = longPreferencesKey(Configuration.Companion.Keys.FILE_SIZE_KEY)*/
        val siteCodeKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_CODE_KEY)
        val siteNameKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_NAME_KEY)
        val siteAddressKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_ADDRESS_KEY)
        val siteCuitKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_CUIT_KEY)
        val driverIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.DRIVER_IDENTIFICATION_KEY)
        val vehicleIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.VEHICLE_IDENTIFICATION_KEY)
        val AtionetVisionvehicleIdKey =
            booleanPreferencesKey(Configuration.Companion.Keys.ATIONET_VISION_VEHICLE_ID_KEY)
        val companyNameKey = booleanPreferencesKey(Configuration.Companion.Keys.COMPANY_NAME_KEY)
        val merchantIdKey = booleanPreferencesKey(Configuration.Companion.Keys.MERCHANT_ID_KEY)
        val primaryIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PRIMARY_IDENTIFICATION_KEY)
        val secondaryIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.SECONDARY_IDENTIFICATION_KEY)
        val transactionDetailsKey =
            booleanPreferencesKey(Configuration.Companion.Keys.TRANSACTION_DETAILS_KEY)
        val ticketTitleKey = stringPreferencesKey(Configuration.Companion.Keys.TICKET_TITLE_KEY)
        val ticketSubtitleKey =
            stringPreferencesKey(Configuration.Companion.Keys.TICKET_SUBTITLE_KEY)
        val ticketFooterKey = stringPreferencesKey(Configuration.Companion.Keys.TICKET_FOOTER_KEY)
        val ticketBottomNoteKey =
            stringPreferencesKey(Configuration.Companion.Keys.TICKET_BOTTOM_NOTE_KEY)
        val invoiceNumberInsteadOfAuthorizationCodeKey =
            booleanPreferencesKey(Configuration.Companion.Keys.INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE_KEY)
        val isDetailInColumnsKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PRODUCT_DETAILS_IN_COLUMNS_KEY)
        val editProductKey = booleanPreferencesKey(Configuration.Companion.Keys.EDIT_PRODUCT_KEY)
        val fuelMeasureUnitKey =
            stringPreferencesKey(Configuration.Companion.Keys.FUEL_MEASURE_UNIT_KEY)
        val gncMeasureUnitKey =
            stringPreferencesKey(Configuration.Companion.Keys.GNC_MEASURE_UNIT_KEY)
        val currencyFormatKey =
            stringPreferencesKey(Configuration.Companion.Keys.CURRENCY_FORMAT_KEY)
        val currencyCodeKey =
            stringPreferencesKey(Configuration.Companion.Keys.CURRENCY_CODE_KEY)
        val supervisorPasswordKey =
            stringPreferencesKey(Configuration.Companion.Keys.SUPERVISOR_PASSWORD_KEY)
        val tsnKey = stringPreferencesKey(Configuration.Companion.Keys.TSN_KEY_DEFAULT)
        val transactionExpirationDaysKey = longPreferencesKey(Configuration.Companion.Keys.TRANSACTION_EXPIRATION_DAYS_KEY)


        with(configuration) {
            preferences[languageKey] = try {
                Configuration.LanguageType.valueOf(language.name).name
            } catch (e: IllegalArgumentException) {
                Configuration.Companion.Defaults.DEFAULT_LANGUAGE.name
            }
            preferences[controllerTypeKey] =
                try {
                    Configuration.ControllerType.valueOf(controllerType.name).name
                } catch (e: IllegalArgumentException) {
                    Configuration.Companion.Defaults.DEFAULT_CONTROLLER_TYPE.name
                }
            preferences[selectedModuleKey] =
                try {
                    Configuration.ModuleType.valueOf(selectedModule.name).name
                } catch (e: IllegalArgumentException) {
                    Configuration.Companion.Defaults.DEFAULT_MODULE_TYPE.name
                }
            with(fusion) {
                preferences[fusionIPKey] = fusionIp
                preferences[fusionPortKey] = fusionPort
                preferences[fusionPaymentTypeCodeKey] = fusionPaymentTypeCode
                preferences[fusionPaymentMethodKey]= fusionPaymentMethod
                preferences[enableFinalizationVarianceKey] = enableFinalizationVariance
            }
            with(ationet) {
                preferences[nativeUrlKey] = nativeUrl
                preferences[visionUrlKey] = visionUrl
                preferences[ccUserNameKey] = ccUsername
                preferences[ccPasswordKey] = ccPassword
                preferences[gcUserNameKey] = gcUsername
                preferences[gcPasswordKey] = gcPassword
                preferences[loyaltyUserNameKey] = loyaltyUsername
                preferences[loyaltyPasswordKey] = loyaltyPassword
                preferences[terminalIdKey] = terminalId
                preferences[promptAmountTransactionKey] = promptAmountTransaction
                preferences[promptConsumerCardKey] = promptConsumerCard
                preferences[promptLoyaltyKey] = promptLoyalty
                preferences[promptGiftCardKey] = promptGiftCard
                preferences[promptPaymentMethodEnableKey] = paymentMethodEnabled
                preferences[promptLoyaltyPaymentMethodEnableKey] = loyaltyPaymentMethodEnabled
                preferences[promptsDefaultKey] = promptsDefault
                preferences[promptAttendantIdentificationKey] = promptAttendantIdentification
                preferences[promptDriverIdentificationKey] = promptDriverIdentification
                preferences[promptVehicleIdentificationKey] = promptVehicleIdentification
                preferences[promptOdometerKey] = promptOdometer
                preferences[promptEngineHoursKey] = promptEngineHours
                preferences[promptTrailerKey] = promptTrailer
                preferences[promptMiscellaneousKey] = promptMiscellaneous
                preferences[promptTruckUnitKey] = promptTruckUnit
                preferences[promptSecondaryTrackKey] = promptSecondaryTrack
                preferences[promptPrimaryPINKey] = promptPrimaryPIN
                preferences[promptSecondaryPINKey] = promptSecondaryPIN
                preferences[localAgentKey] = localAgent
                preferences[localAgentIpKey] = localAgentIp
                preferences[localAgentPortKey] = localAgentPort
            }
            with(terminalManagement) {
                preferences[terminalManagementEnabledKey] = terminalManagementEnabled
                preferences[terminalManagementUrlKey] = terminalManagementUrl
                preferences[pollIntervalKey] = pollInterval.inWholeMinutes
                preferences[sendReportAutomaticallyKey] = sendReportAutomatically
                preferences[levelReportKey] =
                    try {
                        com.ationet.androidterminal.core.domain.model.configuration.TerminalManagement.LevelReport.valueOf(
                            levelReport.name
                        ).name
                    } catch (e: IllegalArgumentException) {
                        Configuration.Companion.Defaults.DEFAULT_LEVEL_REPORT.name
                    }
                /*preferences[fileSizeKey] = fileSize.toLong()*/
            }
            with(site) {
                preferences[siteCodeKey] = siteCode
                preferences[siteNameKey] = siteName
                preferences[siteAddressKey] = siteAddress
                preferences[siteCuitKey] = siteCuit
            }
            with(ticket) {
                preferences[driverIdentificationKey] = driverIdentification
                preferences[vehicleIdentificationKey] = vehicleIdentification
                preferences[companyNameKey] = companyName
                preferences[merchantIdKey] = merchantId
                preferences[primaryIdentificationKey] = primaryIdentification
                preferences[secondaryIdentificationKey] = secondaryIdentification
                preferences[transactionDetailsKey] = transactionDetails
                preferences[ticketTitleKey] = title
                preferences[ticketSubtitleKey] = subtitle
                preferences[ticketFooterKey] = footer
                preferences[ticketBottomNoteKey] = bottomNote
                preferences[invoiceNumberInsteadOfAuthorizationCodeKey] =
                    invoiceNumberInsteadOfAuthorizationCode
                preferences[isDetailInColumnsKey] = isDetailInColumn
            }
            preferences[editProductKey] = editProductInfo
            preferences[fuelMeasureUnitKey] = fuelMeasureUnit
            preferences[gncMeasureUnitKey] = gncMeasureUnit
            preferences[currencyFormatKey] = currencyFormat
            preferences[currencyCodeKey] = currencyCode
            preferences[supervisorPasswordKey] = supervisorPassword
            preferences[tsnKey] = tsn
            preferences[transactionExpirationDaysKey] = transactionExpirationDays.toLong()
        }
    }

    private fun Preferences.toConfiguration(): Configuration {
        val nativeUrlKey = stringPreferencesKey(Configuration.Companion.Keys.NATIVE_URL_KEY)
        val nativeUrl = this[nativeUrlKey] ?: Configuration.Companion.Defaults.DEFAULT_NATIVE_URL

        val visionUrlKey = stringPreferencesKey(Configuration.Companion.Keys.VISION_URL_KEY)
        val visionUrl = this[visionUrlKey] ?: Configuration.Companion.Defaults.DEFAULT_VISION_URL

        val ccUsernameKey = stringPreferencesKey(Configuration.Companion.Keys.CC_USERNAME_KEY)
        val ccUsername = this[ccUsernameKey] ?: Configuration.Companion.Defaults.DEFAULT_USERNAME_CC

        val ccPasswordKey = stringPreferencesKey(Configuration.Companion.Keys.CC_PASSWORD_KEY)
        val ccPassword = this[ccPasswordKey] ?: Configuration.Companion.Defaults.DEFAULT_PASSWORD_CC

        val gcUsernameKey = stringPreferencesKey(Configuration.Companion.Keys.GC_USERNAME_KEY)
        val gcUsername = this[gcUsernameKey] ?: Configuration.Companion.Defaults.DEFAULT_USERNAME_GC

        val gcPasswordKey = stringPreferencesKey(Configuration.Companion.Keys.GC_PASSWORD_KEY)
        val gcPassword = this[gcPasswordKey] ?: Configuration.Companion.Defaults.DEFAULT_PASSWORD_GC

        val loyaltyUsernameKey = stringPreferencesKey(Configuration.Companion.Keys.LOYALTY_USERNAME_KEY)
        val loyaltyUsername = this[loyaltyUsernameKey] ?: Configuration.Companion.Defaults.DEFAULT_USERNAME_LOYALTY

        val loyaltyPasswordKey = stringPreferencesKey(Configuration.Companion.Keys.LOYALTY_PASSWORD_KEY)
        val loyaltyPassword = this[loyaltyPasswordKey] ?: Configuration.Companion.Defaults.DEFAULT_PASSWORD_LOYALTY

        val terminalIdKey = stringPreferencesKey(Configuration.Companion.Keys.TERMINAL_ID_KEY)
        val terminalId = this[terminalIdKey] ?: Configuration.Companion.Defaults.DEFAULT_TERMINAL_ID

        val languageKey = stringPreferencesKey(Configuration.Companion.Keys.LANGUAGE_KEY)
        val language = try {
            Configuration.LanguageType.valueOf(
                this[languageKey] ?: Configuration.Companion.Defaults.DEFAULT_LANGUAGE.name
            )
        } catch (e: IllegalArgumentException) {
            Configuration.Companion.Defaults.DEFAULT_LANGUAGE
        }

        val controllerTypeKey =
            stringPreferencesKey(Configuration.Companion.Keys.CONTROLLER_TYPE_KEY)
        val controllerType = try {
            Configuration.ControllerType.valueOf(
                this[controllerTypeKey] ?: Configuration.Companion.Defaults.DEFAULT_CONTROLLER_TYPE.name
            )
        } catch (e: IllegalArgumentException) {
            Configuration.Companion.Defaults.DEFAULT_CONTROLLER_TYPE
        }

        val fusionIPKey = stringPreferencesKey(Configuration.Companion.Keys.FUSION_IP_KEY)
        val fusionIp = this[fusionIPKey] ?: Configuration.Companion.Defaults.DEFAULT_FUSION_IP

        val fusionPortKey = stringPreferencesKey(Configuration.Companion.Keys.FUSION_PORT_KEY)
        val fusionPort = this[fusionPortKey] ?: Configuration.Companion.Defaults.DEFAULT_FUSION_PORT

        val fusionPaymentTypeCodeKey =
            stringPreferencesKey(Configuration.Companion.Keys.FUSION_PAYMENT_TYPE_CODE_KEY)
        val fusionPaymentTypeCode = this[fusionPaymentTypeCodeKey]
            ?: Configuration.Companion.Defaults.DEFAULT_FUSION_PAYMENT_TYPE_CODE

        val enableFinalizationVarianceKey =
            booleanPreferencesKey(Configuration.Companion.Keys.ENABLE_FINALIZATION_VARIANCE_KEY)
        val enableFinalizationVariance = this[enableFinalizationVarianceKey]
            ?: Configuration.Companion.Defaults.DEFAULT_ENABLE_FINALIZATION_VARIANCE

        val fusionPaymentMethodKey =
            booleanPreferencesKey(Configuration.Companion.Keys.FUSION_PAYMENT_METHOD_KEY)
        val fusionPaymentMethod = this[fusionPaymentMethodKey]
            ?: Configuration.Companion.Defaults.DEFAULT_FUSION_PAYMENT_METHOD

        val fusionAuthorizationCodeTagKey =
            stringPreferencesKey(Configuration.Companion.Keys.FUSION_AUTHORIZATION_CODE_TAG_KEY)
        val fusionAuthorizationCodeTag = this[fusionAuthorizationCodeTagKey]
            ?: Configuration.Companion.Defaults.DEFAULT_FUSION_AUTHORIZATION_CODE_TAG

        val promptAmountTransactionKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_AMOUNT_TRANSACTION_KEY)
        val promptAmountTransaction = this[promptAmountTransactionKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPT_AMOUNT_TRANSACTION

        val promptsConsumercardKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_CONSUMERCARD_KEY)
        val promptConsumerCard =
            this[promptsConsumercardKey] ?: Configuration.Companion.Defaults.DEFAULT_PROMPT_CONSUMER_CARD

        val promptsLoyaltyKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_LOYALTY_KEY)
        val promptLoyalty =
            this[promptsLoyaltyKey] ?: Configuration.Companion.Defaults.DEFAULT_PROMPT_LOYALTY

        val promptsGiftcardKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_GIFTCARD_KEY)
        val promptGiftCard =
            this[promptsGiftcardKey] ?: Configuration.Companion.Defaults.DEFAULT_PROMPT_GIFT_CARD

        val promptsPaymentMethodKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPTS_PAYMENT_METHOD_ENABLED)

        val promptPaymentMethodEnable =
            this[promptsPaymentMethodKey] ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_PAYMENT_METHOD_ENABLED

        val promptsLoyaltyPaymentMethodKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED)

        val promptLoyaltyPaymentMethodEnable =
            this[promptsLoyaltyPaymentMethodKey] ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED

        val promptsDefaultKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPTS_DEFAULT_KEY)
        val promptsDefault =
            this[promptsDefaultKey] ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT



        val promptAttendantIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_ATTENDANT_IDENTIFICATION_KEY)
        val promptAttendantIdentification = this[promptAttendantIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptDriverIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_DRIVER_IDENTIFICATION_KEY)
        val promptDriverIdentification = this[promptDriverIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptVehicleIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_VEHICLE_IDENTIFICATION_KEY)
        val promptVehicleIdentification = this[promptVehicleIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptOdometerKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_ODOMETER_KEY)
        val promptOdometer = this[promptOdometerKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptEngineHoursKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_ENGINE_HOURS_KEY)
        val promptEngineHours = this[promptEngineHoursKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptTrailerKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_TRAILER_KEY)
        val promptTrailer = this[promptTrailerKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptMiscellaneousKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_MISCELLANEOUS_KEY)
        val promptMiscellaneous = this[promptMiscellaneousKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptTruckUnitKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_TRUCK_UNIT_KEY)
        val promptTruckUnit = this[promptTruckUnitKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptSecondaryTrackKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_SECONDARY_TRACK_KEY)
        val promptSecondaryTrack = this[promptSecondaryTrackKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptPrimaryPINKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_PRIMARY_PIN_KEY)
        val promptPrimaryPIN = this[promptPrimaryPINKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val promptSecondaryPINKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PROMPT_SECONDARY_PIN_KEY)
        val promptSecondaryPIN = this[promptSecondaryPINKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PROMPTS_DEFAULT

        val localAgentKey = booleanPreferencesKey(Configuration.Companion.Keys.LOCAL_AGENT_KEY)
        val localAgent = this[localAgentKey] ?: Configuration.Companion.Defaults.DEFAULT_LOCAL_AGENT

        val localAgentIpKey = stringPreferencesKey(Configuration.Companion.Keys.LOCAL_AGENT_IP_KEY)
        val localAgentIp =
            this[localAgentIpKey] ?: Configuration.Companion.Defaults.DEFAULT_LOCAL_AGENT_IP

        val localAgentPortKey =
            stringPreferencesKey(Configuration.Companion.Keys.LOCAL_AGENT_PORT_KEY)
        val localAgentPort =
            this[localAgentPortKey] ?: Configuration.Companion.Defaults.DEFAULT_LOCAL_AGENT_PORT

        val terminalManagementEnabledKey =
            booleanPreferencesKey(Configuration.Companion.Keys.TERMINAL_MANAGEMENT_ENABLED_KEY)
        val terminalManagementEnabled = this[terminalManagementEnabledKey]
            ?: Configuration.Companion.Defaults.DEFAULT_TERMINAL_MANAGEMENT_ENABLED

        val terminalManagementUrlKey =
            stringPreferencesKey(Configuration.Companion.Keys.TERMINAL_MANAGEMENT_URL_KEY)
        val terminalManagementUrl = this[terminalManagementUrlKey]
            ?: Configuration.Companion.Defaults.DEFAULT_TERMINAL_MANAGEMENT_URL

        val pollIntervalKey = longPreferencesKey(Configuration.Companion.Keys.POLL_INTERVAL_KEY)
        val pollInterval = this[pollIntervalKey]?.toDuration(DurationUnit.MINUTES)
            ?: Configuration.Companion.Defaults.DEFAULT_POLL_INTERVAL

        val sendReportAutomaticallyKey =
            booleanPreferencesKey(Configuration.Companion.Keys.SEND_REPORT_AUTOMATICALLY_KEY)
        val sendReportAutomatically = this[sendReportAutomaticallyKey]
            ?: Configuration.Companion.Defaults.DEFAULT_SEND_REPORT_AUTOMATICALLY

        val levelReportKey = stringPreferencesKey(Configuration.Companion.Keys.LEVEL_REPORT_KEY)
        val levelReport = try {
            TerminalManagement.LevelReport.valueOf(
                this[levelReportKey] ?: Configuration.Companion.Defaults.DEFAULT_LEVEL_REPORT.name
            )
        } catch (e: IllegalArgumentException) {
            Configuration.Companion.Defaults.DEFAULT_LEVEL_REPORT
        }

        /*val fileSizeKey = longPreferencesKey(Configuration.Companion.Keys.FILE_SIZE_KEY)*/
        val fileSize = Configuration.Companion.Defaults.DEFAULT_FILE_SIZE

        val siteCodeKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_CODE_KEY)
        val siteCode = this[siteCodeKey] ?: Configuration.Companion.Defaults.DEFAULT_SITE_CODE

        val siteNameKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_NAME_KEY)
        val siteName = this[siteNameKey] ?: Configuration.Companion.Defaults.DEFAULT_SITE_NAME

        val siteAddressKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_ADDRESS_KEY)
        val siteAddress =
            this[siteAddressKey] ?: Configuration.Companion.Defaults.DEFAULT_SITE_ADDRESS

        val siteCuitKey = stringPreferencesKey(Configuration.Companion.Keys.SITE_CUIT_KEY)
        val siteCuit = this[siteCuitKey] ?: Configuration.Companion.Defaults.DEFAULT_SITE_CUIT

        val driverIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.DRIVER_IDENTIFICATION_KEY)
        val driverIdentification = this[driverIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_DRIVER_IDENTIFICATION

        val vehicleIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.VEHICLE_IDENTIFICATION_KEY)
        val vehicleIdentification = this[vehicleIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_VEHICLE_IDENTIFICATION

        val companyNameKey = booleanPreferencesKey(Configuration.Companion.Keys.COMPANY_NAME_KEY)
        val companyName =
            this[companyNameKey] ?: Configuration.Companion.Defaults.DEFAULT_COMPANY_NAME

        val merchantIdKey = booleanPreferencesKey(Configuration.Companion.Keys.MERCHANT_ID_KEY)
        val merchantId = this[merchantIdKey] ?: Configuration.Companion.Defaults.DEFAULT_MERCHANT_ID

        val primaryIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PRIMARY_IDENTIFICATION_KEY)
        val primaryIdentification = this[primaryIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_PRIMARY_IDENTIFICATION

        val secondaryIdentificationKey =
            booleanPreferencesKey(Configuration.Companion.Keys.SECONDARY_IDENTIFICATION_KEY)
        val secondaryIdentification = this[secondaryIdentificationKey]
            ?: Configuration.Companion.Defaults.DEFAULT_SECONDARY_IDENTIFICATION

        val transactionDetailsKey =
            booleanPreferencesKey(Configuration.Companion.Keys.TRANSACTION_DETAILS_KEY)
        val transactionDetails = this[transactionDetailsKey]
            ?: Configuration.Companion.Defaults.DEFAULT_TRANSACTION_DETAILS

        val ticketTitleKey = stringPreferencesKey(Configuration.Companion.Keys.TICKET_TITLE_KEY)
        val ticketTitle =
            this[ticketTitleKey] ?: Configuration.Companion.Defaults.DEFAULT_TICKET_TITLE


        val ticketSubtitleKey =
            stringPreferencesKey(Configuration.Companion.Keys.TICKET_SUBTITLE_KEY)
        val ticketSubtitle =
            this[ticketSubtitleKey] ?: Configuration.Companion.Defaults.DEFAULT_TICKET_SUBTITLE

        val ticketFooterKey = stringPreferencesKey(Configuration.Companion.Keys.TICKET_FOOTER_KEY)
        val ticketFooter =
            this[ticketFooterKey] ?: Configuration.Companion.Defaults.DEFAULT_TICKET_FOOTER

        val ticketBottomNoteKey =
            stringPreferencesKey(Configuration.Companion.Keys.TICKET_BOTTOM_NOTE_KEY)
        val ticketBottomNote =
            this[ticketBottomNoteKey] ?: Configuration.Companion.Defaults.DEFAULT_TICKET_BOTTOM_NOTE

        val invoiceNumberInsteadOfAuthorizationCodeKey =
            booleanPreferencesKey(Configuration.Companion.Keys.INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE_KEY)
        val invoiceNumberInsteadOfAuthorizationCode =
            this[invoiceNumberInsteadOfAuthorizationCodeKey]
                ?: Configuration.Companion.Defaults.DEFAULT_INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE

        val isDetailInColumnsKey =
            booleanPreferencesKey(Configuration.Companion.Keys.PRODUCT_DETAILS_IN_COLUMNS_KEY)
        val isDetailInColumns =
            this[isDetailInColumnsKey]
                ?: Configuration.Companion.Defaults.DEFAULT_TRANSACTION_DETAILS_IN_COLUMNS

        val editProductKey = booleanPreferencesKey(Configuration.Companion.Keys.EDIT_PRODUCT_KEY)
        val editProduct =
            this[editProductKey] ?: Configuration.Companion.Defaults.DEFAULT_EDIT_PRODUCT

        val fuelMeasureUnitKey =
            stringPreferencesKey(Configuration.Companion.Keys.FUEL_MEASURE_UNIT_KEY)
        val fuelMeasureUnit =
            this[fuelMeasureUnitKey] ?: Configuration.Companion.Defaults.DEFAULT_FUEL_MEASURE_UNIT

        val gncMeasureUnitKey =
            stringPreferencesKey(Configuration.Companion.Keys.GNC_MEASURE_UNIT_KEY)
        val gncMeasureUnit =
            this[gncMeasureUnitKey] ?: Configuration.Companion.Defaults.DEFAULT_GNC_MEASURE_UNIT

        val currencyCodeKey =
            stringPreferencesKey(Configuration.Companion.Keys.CURRENCY_CODE_KEY)
        val currencyCode =
            this[currencyCodeKey] ?: Configuration.Companion.Defaults.DEFAULT_CURRENCY_CODE

        val currencyFormatKey =
            stringPreferencesKey(Configuration.Companion.Keys.CURRENCY_FORMAT_KEY)
        val currencyFormat =
            this[currencyFormatKey] ?: Configuration.Companion.Defaults.DEFAULT_CURRENCY_FORMAT

        val supervisorPasswordKey =
            stringPreferencesKey(Configuration.Companion.Keys.SUPERVISOR_PASSWORD_KEY)
        val supervisorPassword = this[supervisorPasswordKey]
            ?: Configuration.Companion.Defaults.DEFAULT_SUPERVISOR_PASSWORD

        val tsnKey = stringPreferencesKey(Configuration.Companion.Keys.TSN_KEY_DEFAULT)
        val tsn = this[tsnKey] ?: Configuration.Companion.Defaults.DEFAULT_TSN

        val transactionExpirationDaysKey = longPreferencesKey(Configuration.Companion.Keys.TRANSACTION_EXPIRATION_DAYS_KEY)
        val transactionExpirationDays = (this[transactionExpirationDaysKey] ?: Configuration.Companion.Defaults.DEFAULT_TRANSACTION_EXPIRATION_DAYS).toInt()


        return Configuration(
            language = language,
            controllerType = controllerType,
            fusion = Fusion(
                fusionIp = fusionIp,
                fusionPort = fusionPort,
                fusionPaymentTypeCode = fusionPaymentTypeCode,
                fusionAuthorizationCode = fusionAuthorizationCodeTag,
                fusionPaymentMethod = fusionPaymentMethod,
                enableFinalizationVariance = enableFinalizationVariance
            ),
            ationet = Ationet(
                nativeUrl = nativeUrl,
                visionUrl = visionUrl,
                ccUsername = ccUsername,
                ccPassword = ccPassword,
                gcUsername = gcUsername,
                gcPassword = gcPassword,
                loyaltyUsername = loyaltyUsername,
                loyaltyPassword = loyaltyPassword,
                terminalId = terminalId,
                promptConsumerCard = promptConsumerCard,
                promptLoyalty = promptLoyalty,
                promptGiftCard = promptGiftCard,
                paymentMethodEnabled = promptPaymentMethodEnable,
                loyaltyPaymentMethodEnabled = promptLoyaltyPaymentMethodEnable,
                promptAmountTransaction = promptAmountTransaction,
                promptsDefault = promptsDefault,
                promptAttendantIdentification = promptAttendantIdentification,
                promptDriverIdentification = promptDriverIdentification,
                promptVehicleIdentification = promptVehicleIdentification,
                promptOdometer = promptOdometer,
                promptEngineHours = promptEngineHours,
                promptTrailer = promptTrailer,
                promptMiscellaneous = promptMiscellaneous,
                promptTruckUnit = promptTruckUnit,
                promptSecondaryTrack = promptSecondaryTrack,
                promptPrimaryPIN = promptPrimaryPIN,
                promptSecondaryPIN = promptSecondaryPIN,
                localAgent = localAgent,
                localAgentIp = localAgentIp,
                localAgentPort = localAgentPort
            ),
            terminalManagement = TerminalManagement(
                terminalManagementEnabled = terminalManagementEnabled,
                terminalManagementUrl = terminalManagementUrl,
                pollInterval = pollInterval,
                sendReportAutomatically = sendReportAutomatically,
                levelReport = levelReport,
                fileSize = fileSize
            ),
            site = Site(
                siteCode = siteCode,
                siteName = siteName,
                siteAddress = siteAddress,
                siteCuit = siteCuit
            ),
            ticket = Ticket(
                driverIdentification = driverIdentification,
                vehicleIdentification = vehicleIdentification,
                companyName = companyName,
                merchantId = merchantId,
                primaryIdentification = primaryIdentification,
                secondaryIdentification = secondaryIdentification,
                transactionDetails = transactionDetails,
                title = ticketTitle,
                subtitle = ticketSubtitle,
                footer = ticketFooter,
                bottomNote = ticketBottomNote,
                invoiceNumberInsteadOfAuthorizationCode = invoiceNumberInsteadOfAuthorizationCode,
                isDetailInColumn = isDetailInColumns
            ),
            editProductInfo = editProduct,
            fuelMeasureUnit = fuelMeasureUnit,
            gncMeasureUnit = gncMeasureUnit,
            currencyCode = currencyCode,
            currencyFormat = currencyFormat,
            supervisorPassword = supervisorPassword,
            tsn = tsn,
            transactionExpirationDays = transactionExpirationDays
        )
    }
}