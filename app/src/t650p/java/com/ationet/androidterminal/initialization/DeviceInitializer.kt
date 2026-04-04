package com.ationet.androidterminal.initialization

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.priv.verifone.psdk.sdiapplication.tms.TmsService
import com.priv.verifone.psdk.sdiapplication.tms.TmsServiceCallback
import com.verifone.updateservicelib.UpdateStatus

class DeviceInitializer : Initializer<Unit> {

    companion object {
        private const val TAG = "DeviceInitializer"
        private const val ZIP_FILE_NAME = "ATIO.tgz.zip" // Asegúrate que este es el nombre correcto en assets
    }

    override fun create(context: Context) {
        if (true) return
        Log.i(TAG, "Verificando si se requiere instalar el SuperPackage...")
//solo se instala la primera vez que se ejecuta luego entra por isInstalled
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isInstalled = prefs.getBoolean("isSuperPackageInstalled", false)

        if (isInstalled) {
            Log.i(TAG, "🚫 El SuperPackage ya fue instalado anteriormente. No se ejecuta la instalación.")
            return
        }

       // val tmsService = TmsService(context.applicationContext as android.app.Application)

//        tmsService.setCallback(object : TmsServiceCallback {
//            override fun onStatus(status: Int) {
//                when (status) {
//                    UpdateStatus.STATUS_SUCCESS,
//                    UpdateStatus.STATUS_SUCCESS_REBOOTING -> {
//                        Log.i(TAG, "✅ Instalación exitosa del paquete $ZIP_FILE_NAME")
//                        prefs.edit().putBoolean("isSuperPackageInstalled", true).apply() // Guardar estado
//                    }
//                    UpdateStatus.STATUS_PENDING -> {
//                        Log.i(TAG, "⏳ Instalación pendiente del paquete $ZIP_FILE_NAME")
//                    }
//                    else -> {
//                        Log.e(TAG, "❌ Falló la instalación del paquete $ZIP_FILE_NAME (status: $status)")
//                    }
//                }
//            }
//        })

//        Thread {
//            Log.i(TAG, "Iniciando instalación de $ZIP_FILE_NAME desde assets")
//            val result = tmsService.installSuperPackage(ZIP_FILE_NAME)
//            Log.i(TAG, "Resultado de instalación: $result")
//        }.start()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
