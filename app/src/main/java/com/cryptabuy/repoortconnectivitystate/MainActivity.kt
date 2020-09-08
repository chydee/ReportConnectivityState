package com.cryptabuy.repoortconnectivitystate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.coroutines.CoroutineContext


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var networkManager: ConnectivityManager
    private lateinit var stateText: TextView
    private lateinit var networkState: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stateText = findViewById(R.id.state)
        networkState = findViewById(R.id.networkState)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        networkManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (wifiManager.isWifiEnabled) {
            stateText.text = getString(R.string.connectivity_state) + """ Wifi is on"""
        }

    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        val intentFilterNW = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(wifiStateReceiver, intentFilter)
        registerReceiver(connectivityReceiver, intentFilterNW)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(wifiStateReceiver)
        unregisterReceiver(connectivityReceiver)
    }

    private val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val wifiStateExtra = p1?.getIntExtra(
                WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN
            )
            when (wifiStateExtra) {
                WifiManager.WIFI_STATE_ENABLED -> {
                    stateText.text = getString(R.string.connectivity_state) + """ Wifi is on"""
                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    stateText.text = getString(R.string.connectivity_state) + """ Wifi is off"""
                }
                else -> {
                    stateText.text =
                        getString(R.string.connectivity_state) + """ Wifi state is unknown"""
                }
            }
        }
    }

    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val cm = p0?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                cm.registerDefaultNetworkCallback(object : NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        runOnUiThread {
                            kotlin.run {
                                networkState.text = "You are online" // Global Static Variable
                            }
                        }
                    }

                    override fun onLost(network: Network) {
                        runOnUiThread {
                            kotlin.run {
                                networkState.text = "You are offline" // Global Static Variable
                            }
                        }
                    }
                }
                )
            } else {
                val nwInfo = cm.activeNetworkInfo
                val isConnected = nwInfo != null && nwInfo.isConnectedOrConnecting
                if (isConnected) {
                    networkState.text = "You are online"
                } else {
                    networkState.text = "You are offline"
                }
            }

        }
    }
}