/*
* Copyright (c) 2021 by VeriFone, Inc.
* All Rights Reserved.
* THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
* AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
*
* Use, disclosure, or reproduction is prohibited
* without prior written approval from VeriFone, Inc.
*/

package com.priv.verifone.psdk.sdiapplication.tms

import android.app.Application
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.util.Log
import com.verifone.updateservicelib.IUpdateServiceCallback
import com.verifone.updateservicelib.RecoveryLogStatus
import com.verifone.updateservicelib.UpdateServiceApi
import com.verifone.updateservicelib.UpdateStatus
import java.io.File
import java.io.FileOutputStream

// This is responsible for handling Update Service apis as per UI request call
class TmsService(private val app: Application) {

    companion object {
        private const val TAG = "TmsService"
    }

    private var result = UpdateStatus.STATUS_FAILURE
    private lateinit var updateService: UpdateServiceApi
    private lateinit var callback:TmsServiceCallback
    // POS app receives the update status through this event
    private val updateServiceCallback = object : IUpdateServiceCallback.Stub() {
        override fun onStatus(status: Int) {
            Log.i(TAG, "onStatus(): $status")
            updateService.unbind()
            callback.onStatus(status)
        }
    }

    @Volatile
    private var isServiceReady = false

    init {
        Thread {
            updateService = UpdateServiceApi.getInstance(app)
            isServiceReady = true
            Log.d(TAG, "UpdateService inicializado correctamente")
        }.start()
    }


    fun setCallback(callback: TmsServiceCallback) {
        this.callback = callback
    }

    // Install a package that contains multiple parts - params, APK, Engage OTA, Engage pkg, and Android OTA
    fun installSuperPackage(fileName: String): Int {
        while (!isServiceReady) {
            Thread.sleep(50) // Esperar a que el servicio esté listo
        }

        try {
            updateService.registerCallback(updateServiceCallback)
            copyTestFiles(fileName)

            val file = File(app.cacheDir, fileName)
            val fileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            result = updateService.installPackage(fileName, fileDescriptor)
            Log.d(TAG, "installPackage: $result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun copyTestFiles(fileName: String) {
        try {
            app.assets.open(fileName).use { `in` ->
                FileOutputStream(File(app.cacheDir, fileName)).use { out ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (`in`.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
