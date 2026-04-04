package com.ationet.androidterminal.core.domain.use_case.configuration

import android.content.Context
import com.ationet.androidterminal.core.data.local.util.TsnHelper
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.repository.ConfigurationRepository
import com.ationet.androidterminal.core.domain.worker.ClearOldTransactionsWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class UpdateConfiguration @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configurationRepository: ConfigurationRepository
) {
    operator fun invoke(block: (Configuration) -> Configuration) = configurationRepository.update(block)

    operator fun invoke(parameters: Map<String, String?>): Configuration {
        parameters.forEach { parameter ->
            val value = parameter.value ?: ""
            when (parameter.key) {
                Configuration.Companion.Keys.LANGUAGE_KEY -> configurationRepository.update {
                    it.copy(
                        language = try {
                            Configuration.LanguageType.valueOf(value)
                        } catch (e: IllegalArgumentException) {
                            Configuration.LanguageType.EN
                        }
                    )
                }

                Configuration.Companion.Keys.CONTROLLER_TYPE_KEY -> configurationRepository.update {
                    it.copy(
                        controllerType = try {
                            Configuration.ControllerType.valueOf(value)
                        } catch (e: IllegalArgumentException) {
                            Configuration.ControllerType.STAND_ALONE
                        }
                    )
                }

                Configuration.Companion.Keys.FUSION_IP_KEY -> configurationRepository.update { it.copy(fusion = it.fusion.copy(fusionIp = value)) }
                Configuration.Companion.Keys.FUSION_PORT_KEY -> configurationRepository.update { it.copy(fusion = it.fusion.copy(fusionPort = value)) }
                Configuration.Companion.Keys.FUSION_PAYMENT_TYPE_CODE_KEY -> configurationRepository.update { it.copy(fusion = it.fusion.copy(fusionPaymentTypeCode = value)) }
                Configuration.Companion.Keys.ENABLE_FINALIZATION_VARIANCE_KEY -> configurationRepository.update { it.copy(fusion = it.fusion.copy(enableFinalizationVariance = value.toBoolean())) }

                Configuration.Companion.Keys.NATIVE_URL_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(nativeUrl = value)) }
                Configuration.Companion.Keys.VISION_URL_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(visionUrl = value)) }
                Configuration.Companion.Keys.CC_USERNAME_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(ccUsername = value)) }
                Configuration.Companion.Keys.CC_PASSWORD_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(ccPassword = value)) }
                Configuration.Companion.Keys.GC_USERNAME_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(gcUsername = value)) }
                Configuration.Companion.Keys.GC_PASSWORD_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(gcPassword = value)) }
                Configuration.Companion.Keys.LOYALTY_USERNAME_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(loyaltyUsername = value)) }
                Configuration.Companion.Keys.LOYALTY_PASSWORD_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(loyaltyPassword = value)) }
                Configuration.Companion.Keys.TERMINAL_ID_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(terminalId = value)) }
                Configuration.Companion.Keys.PROMPT_AMOUNT_TRANSACTION_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptAmountTransaction = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_CONSUMERCARD_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptConsumerCard = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_LOYALTY_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptLoyalty = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_GIFTCARD_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptGiftCard = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPTS_PAYMENT_METHOD_ENABLED -> configurationRepository.update { it.copy(ationet = it.ationet.copy(paymentMethodEnabled = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED -> configurationRepository.update { it.copy(ationet = it.ationet.copy(loyaltyPaymentMethodEnabled = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPTS_DEFAULT_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptsDefault = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_ATTENDANT_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptAttendantIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_DRIVER_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptDriverIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_VEHICLE_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptVehicleIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_ODOMETER_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptOdometer = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_ENGINE_HOURS_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptEngineHours = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_TRAILER_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptTrailer = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_MISCELLANEOUS_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptMiscellaneous = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_TRUCK_UNIT_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptTruckUnit = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_SECONDARY_TRACK_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptSecondaryTrack = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_PRIMARY_PIN_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptPrimaryPIN = value.toBoolean())) }
                Configuration.Companion.Keys.PROMPT_SECONDARY_PIN_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(promptSecondaryPIN = value.toBoolean())) }
                Configuration.Companion.Keys.LOCAL_AGENT_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(localAgent = value.toBoolean())) }
                Configuration.Companion.Keys.LOCAL_AGENT_IP_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(localAgentIp = value)) }
                Configuration.Companion.Keys.LOCAL_AGENT_PORT_KEY -> configurationRepository.update { it.copy(ationet = it.ationet.copy(localAgentPort = value)) }

                Configuration.Companion.Keys.TERMINAL_MANAGEMENT_ENABLED_KEY -> configurationRepository.update { it.copy(terminalManagement = it.terminalManagement.copy(terminalManagementEnabled = value.toBoolean())) }
                Configuration.Companion.Keys.TERMINAL_MANAGEMENT_URL_KEY -> configurationRepository.update { it.copy(terminalManagement = it.terminalManagement.copy(terminalManagementUrl = value)) }
                Configuration.Companion.Keys.POLL_INTERVAL_KEY -> configurationRepository.update { it.copy(terminalManagement = it.terminalManagement.copy(pollInterval = value.toInt().minutes)) }
                Configuration.Companion.Keys.SEND_REPORT_AUTOMATICALLY_KEY -> configurationRepository.update { it.copy(terminalManagement = it.terminalManagement.copy(sendReportAutomatically = value.toBoolean())) }
                Configuration.Companion.Keys.LEVEL_REPORT_KEY -> configurationRepository.update {
                    it.copy(
                        terminalManagement = it.terminalManagement.copy(
                            levelReport = try {
                                com.ationet.androidterminal.core.domain.model.configuration.TerminalManagement.LevelReport.valueOf(
                                    value
                                )
                            } catch (e: IllegalArgumentException) {
                                com.ationet.androidterminal.core.domain.model.configuration.TerminalManagement.LevelReport.VERY_DETAILED
                            }
                        )
                    )
                }

                /*Configuration.Companion.Keys.FILE_SIZE_KEY -> configurationRepository.update { it.copy(terminalManagement = it.terminalManagement.copy(fileSize = value.toInt())) }*/

                Configuration.Companion.Keys.SITE_CODE_KEY -> configurationRepository.update { it.copy(site = it.site.copy(siteCode = value)) }
                Configuration.Companion.Keys.SITE_NAME_KEY -> configurationRepository.update { it.copy(site = it.site.copy(siteName = value)) }
                Configuration.Companion.Keys.SITE_ADDRESS_KEY -> configurationRepository.update { it.copy(site = it.site.copy(siteAddress = value)) }
                Configuration.Companion.Keys.SITE_CUIT_KEY -> configurationRepository.update { it.copy(site = it.site.copy(siteCuit = value)) }

                Configuration.Companion.Keys.DRIVER_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(driverIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.VEHICLE_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(vehicleIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.COMPANY_NAME_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(companyName = value.toBoolean())) }
                Configuration.Companion.Keys.MERCHANT_ID_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(merchantId = value.toBoolean())) }
                Configuration.Companion.Keys.PRIMARY_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(primaryIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.SECONDARY_IDENTIFICATION_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(secondaryIdentification = value.toBoolean())) }
                Configuration.Companion.Keys.TRANSACTION_DETAILS_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(transactionDetails = value.toBoolean())) }
                Configuration.Companion.Keys.TICKET_TITLE_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(title = value)) }
                Configuration.Companion.Keys.TICKET_SUBTITLE_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(subtitle = value)) }
                Configuration.Companion.Keys.TICKET_FOOTER_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(footer = value)) }
                Configuration.Companion.Keys.TICKET_BOTTOM_NOTE_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(bottomNote = value)) }
                Configuration.Companion.Keys.INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE_KEY -> configurationRepository.update {
                    it.copy(
                        ticket = it.ticket.copy(
                            invoiceNumberInsteadOfAuthorizationCode = value.toBoolean()
                        )
                    )
                }

                Configuration.Companion.Keys.PRODUCT_DETAILS_IN_COLUMNS_KEY -> configurationRepository.update { it.copy(ticket = it.ticket.copy(isDetailInColumn = value.toBoolean())) }

                Configuration.Companion.Keys.EDIT_PRODUCT_KEY -> configurationRepository.update { it.copy(editProductInfo = value.toBoolean()) }
                Configuration.Companion.Keys.FUEL_MEASURE_UNIT_KEY -> configurationRepository.update { it.copy(fuelMeasureUnit = value) }
                Configuration.Companion.Keys.GNC_MEASURE_UNIT_KEY -> configurationRepository.update { it.copy(gncMeasureUnit = value) }
                Configuration.Companion.Keys.CURRENCY_FORMAT_KEY -> configurationRepository.update { it.copy(currencyFormat = value) }
                Configuration.Companion.Keys.CURRENCY_CODE_KEY -> configurationRepository.update { it.copy(currencyCode = value) }
                Configuration.Companion.Keys.SUPERVISOR_PASSWORD_KEY -> configurationRepository.update { it.copy(supervisorPassword = value) }
                Configuration.Companion.Keys.TSN_KEY_DEFAULT -> {
                    configurationRepository.update { it.copy(tsn = value) }
                    runBlocking { TsnHelper.setTransactionSequenceNumber(context, value.toLongOrNull() ?: 0) }
                }

                Configuration.Companion.Keys.TRANSACTION_EXPIRATION_DAYS_KEY -> {
                    val transactionExpirationDays = value.toIntOrNull()
                    if (transactionExpirationDays != null && transactionExpirationDays != Configuration.Companion.Defaults.DEFAULT_TRANSACTION_EXPIRATION_DAYS) {
                        configurationRepository.update { it.copy(transactionExpirationDays = transactionExpirationDays) }
                        ClearOldTransactionsWorker.enqueue(context)
                    }
                }
            }
        }
        return configurationRepository.get()
    }
}