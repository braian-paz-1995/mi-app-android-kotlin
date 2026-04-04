package com.ationet.androidterminal.core.domain.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.os.Build
import androidx.annotation.RequiresApi
import com.atio.log.Logger
import com.atio.log.util.e
import com.atio.log.util.w
import java.net.Inet4Address

object NetworkUtils {
    @RequiresApi(Build.VERSION_CODES.M)
    fun getIpAddress(context: Context): String? {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)

            if (connectivityManager is ConnectivityManager) {
                val link: LinkProperties? = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
                if(link == null) {
                    Logger.w("NetworkUtils", "No active network")
                    return null
                }

                for (linkAddress in link.linkAddresses.filter { it.address is Inet4Address }) {
                    val address = linkAddress.address as Inet4Address

                    return address.hostAddress
                }
            }

            null
        } catch (e: Throwable) {
            Logger.e("NetworkUtils", "Failed to get ip address", e)
            null
        }
    }
}