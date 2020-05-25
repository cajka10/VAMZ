package com.example.vamzapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast


abstract class NetworkChangedReceiver : BroadcastReceiver() {
    var isConnected = true

    override fun onReceive(context: Context, intent: Intent?) {
        val broadcastIntent = Intent()
        val connMgr = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connMgr
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connMgr
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifi.isConnected || mobile.isConnected) {
            Toast.makeText(context, "Aktivne pripojenie na internet", Toast.LENGTH_LONG).show()
//            broadcastIntent.putExtra("conn", true)
            isConnected = true

        }else{
            Toast.makeText(context, "Žiadne pripojenie na internet. Nebude možné pridať obrázok.", Toast.LENGTH_LONG).show()
//            broadcastIntent.putExtra("conn", false)

            isConnected = false
        }

        broadcastResult(isConnected)
    }

    protected abstract fun broadcastResult(connected: Boolean)

}