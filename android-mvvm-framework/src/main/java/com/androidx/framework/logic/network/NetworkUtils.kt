package com.androidx.framework.logic.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager

interface NetworkType{
    companion object{
        const val NONE:Int =0
        const val WIFI :Int= 1
        const val PHONE :Int= 2

    }
}
interface NetworkState{
    companion object{
        const val UNCONNECTED:Int = 0
        const val CONNECTED:Int = 1
        const val LOST:Int = 2
    }
}
object NetworkUtils {

    private var mConnectivityManager: ConnectivityManager? = null
    private val mNetworkObservers:MutableList<NetworkObserver> by lazy {
        mutableListOf<NetworkObserver>()
    }

    fun addNetworkObserver(networkObserver: NetworkObserver){
        mNetworkObservers.add(networkObserver)
    }

    private fun broadcastNetworkState(state: Int,type: Int,name:String?){
        mNetworkObservers.forEach {
            it.onNetworkStateChanged(state,type,name)
        }
    }
    private var mWifiManager:WifiManager?=null
    private var mTelephonyManager:TelephonyManager?=null
    fun init(application: Application) {
        mConnectivityManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mWifiManager = application.getSystemService(WifiManager::class.java)
            mTelephonyManager = application.getSystemService(TelephonyManager::class.java)
            application.getSystemService(ConnectivityManager::class.java)
        } else {
            mWifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
            mTelephonyManager = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        mConnectivityManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        val networkCapabilities = mConnectivityManager?.getNetworkCapabilities(network)
                        val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        if (isWifi !=null && isWifi){
                            broadcastNetworkState(NetworkState.CONNECTED, NetworkType.WIFI, mWifiManager?.connectionInfo?.ssid?:"unknown")
                            return
                        }
                        val isCellular = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        if (isCellular!=null && isCellular){

                            broadcastNetworkState(NetworkState.CONNECTED, NetworkType.PHONE, null)
                            return
                        }


                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        broadcastNetworkState(NetworkState.UNCONNECTED,NetworkType.NONE,null)
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        broadcastNetworkState(NetworkState.LOST,NetworkType.NONE,null)
                    }
                })
            }
        }
    }

}
interface NetworkObserver{
    fun onNetworkStateChanged(state:Int,type:Int,name:String?)
}
/**
 * 是否连接网上
 */
inline val <reified T:Application> T.isNetworkConnected:Boolean get() {
    val connectivityManager= (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getSystemService(ConnectivityManager::class.java)
    } else {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }) ?: return false
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
   return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

}