package com.example.vamzapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast


class NetworkChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val connMgr = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connMgr
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connMgr
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifi.isConnected || mobile.isConnected) { // do stuff
            Toast.makeText(context, "Aktivne pripojenie na internet", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context, "Žiadne pripojenie na internet.", Toast.LENGTH_LONG).show()

        }
    }

}