//package com.ationet.androidterminal.hal.card_reader.config
//
//import android.util.Log
//import com.verifone.payment_sdk.PaymentSdk
//import com.verifone.payment_sdk.SdiEmvConf
//import javax.inject.Inject
//
//class CustomContactConfig @Inject constructor(
//    sdk: PaymentSdk,
//    private val ctConfig: EmvContactConfig
//) : CtConfig(sdk, ctConfig) {
//
//    companion object {
//        const val TAG = "CustomContactConfig"
//    }
//
//    override fun getCtTerminalConfig(): SdiEmvConf {
//        Log.d(TAG, "Contact Terminal Config")
//        return SdiEmvConf.create()
//    }
//
//    override fun getCtApplicationConfig(): ArrayList<SdiEmvConf> {
//        Log.d(TAG, "Contact AID Config")
//        val sdiAidConfList = ArrayList<SdiEmvConf>()
//        for (application in ctConfig.applications) {
//            val sdiEmvConf = SdiEmvConf.create()
//            sdiAidConfList.add(sdiEmvConf)
//        }
//        return sdiAidConfList
//    }
//
//    override fun getCtCapks(): List<EmvContactConfig.Capk> {
//        Log.d(TAG, "Contactless AID Config")
//        return ctConfig.capks.map { capk ->
//            EmvContactConfig.Capk(
//                certificateRevocationListDF0E = "",
//                exponentDF0D = capk.exponentDF0D,
//                rid = capk.rid,
//                hashDF0C = capk.hashDF0C,
//                indexDF09 = capk.indexDF09,
//                keyDF0B = capk.keyDF0B
//            )
//        }
//    }
//}
