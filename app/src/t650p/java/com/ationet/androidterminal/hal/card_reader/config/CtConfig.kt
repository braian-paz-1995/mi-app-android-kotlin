package com.ationet.androidterminal.hal.card_reader.config

import com.ationet.androidterminal.hal.card_reader.config.Utils.Companion.hexStringToByteArray
import com.verifone.payment_sdk.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

// This is mapped to emv contact configuration and respective operations
open class CtConfig @Inject constructor(
    private val sdk: PaymentSdk,
    private val ctConfig: EmvContactConfig
){
    internal open fun getCtTerminalConfig(): SdiEmvConf {

        val sdiEmvConf = SdiEmvConf.create()
        sdiEmvConf.terminalType = ctConfig.terminal.terminalType.toShort(radix = 16)
        sdiEmvConf.terminalCountryCode = ctConfig.terminal.terminalCountryCode.toInt(radix = 16)
        sdiEmvConf.terminalCapabilities =
            ctConfig.terminal.terminalCapabilities.hexStringToByteArray()
        sdiEmvConf.additionalCapabilities =
            ctConfig.terminal.additionalTerminalCapabilities.hexStringToByteArray()
        sdiEmvConf.transactionCurrency = SdiCurrency.valueOf(ctConfig.terminal.transactionCurrency)
        sdiEmvConf.transactionCurrencyExp = ctConfig.terminal.transactionCurrencyExp.toShort();
        return sdiEmvConf
    }

    internal open fun getCtApplicationConfig(): ArrayList<SdiEmvConf> {

        val sdiAidConfList = ArrayList<SdiEmvConf>()
        for (application in ctConfig.applications) {
            val sdiEmvConf = SdiEmvConf.create();
            val appVersionNumber: ArrayList<Int> = ArrayList()
            appVersionNumber.add(0x0000008D)
            sdiEmvConf.aid = application.aid.hexStringToByteArray()
            sdiEmvConf.chipAppVersionNumber =
                arrayListOf(application.appVersionNumber.toInt(radix = 16))
            sdiEmvConf.defaultAppName = application.defaultAppName
            sdiEmvConf.asi = application.asi.toShort(radix = 16)
            sdiEmvConf.merchantCategory = application.merchantCategoryCode.hexStringToByteArray()
            sdiEmvConf.floorLimit = application.floorLimit.toLong(radix = 16)
            sdiEmvConf.securityLimit = application.securityLimit.toLong(16)
            sdiEmvConf.capabilitiesBelowLimit =
                application.belowLimitTerminalCapabilities.hexStringToByteArray()
            sdiEmvConf.threshold = application.threshold.toLong(radix = 16)
            sdiEmvConf.riskManagementTargetPercentage = application.targetPercentage.toInt()
            sdiEmvConf.riskManagementMaxTargetPercentage = application.maxTargetPercentage.toInt()
            sdiEmvConf.tacDenial = application.tacDenial.hexStringToByteArray()
            sdiEmvConf.tacOnline = application.tacOnline.hexStringToByteArray()
            sdiEmvConf.tacDefault = application.tacDefault.hexStringToByteArray()
            sdiEmvConf.emvApplication = 0x01.toShort()
            sdiEmvConf.defaultTDOL = byteArrayOf()
            sdiEmvConf.defaultDDOL = application.defaultDDOL.hexStringToByteArray()
            sdiEmvConf.cdaProcessing = application.cdaProcessing.toShort(radix = 16)
            sdiEmvConf.offlineOnly = false
            sdiEmvConf.aipNoCVM = application.aipCvmNotSupported.toShort(radix = 16)
            sdiEmvConf.posEntryMode = application.posEntryMode.toShort(radix = 16)
            sdiEmvConf.ctAppFlowCapabilities =
                EnumSet.of(SdiEmvCtAppFlowCapabilities.CASHBACK_SUPPORT, SdiEmvCtAppFlowCapabilities.DOMESTIC_CHECK)
            sdiEmvConf.terminalCapabilities = application.appTermCap.hexStringToByteArray()
            sdiEmvConf.terminalCountryCode = application.countryCode.toInt(radix = 16)
            sdiEmvConf.additionalCapabilities =
                application.appTermAddCap.hexStringToByteArray()
            sdiEmvConf.terminalType = application.appTerminalType.toShort(radix = 16)
            sdiAidConfList.add(sdiEmvConf)
        }
        return sdiAidConfList
    }

    internal open fun getCtCapks(): List<EmvContactConfig.Capk> {
        return ctConfig.capks
    }

    companion object {
        private const val TAG = "EMVCTConfig"
    }
}