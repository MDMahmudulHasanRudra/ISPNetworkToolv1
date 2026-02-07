package com.rudra.ispnetworktool.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.rudra.ispnetworktool.data.models.IpInfo
import com.rudra.ispnetworktool.domain.repository.IpInfoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.Serializable

class IpInfoRepositoryImpl(
    private val client: HttpClient,
    private val context: Context
) : IpInfoRepository {

    override fun getIpInfo(): Flow<IpInfo> = flow {
        val publicIpInfo = fetchPublicIpInfo()
        val localIpInfo = getLocalIpInfo()

        val combinedInfo = publicIpInfo.copy(
            localIp = localIpInfo.localIp,
            gateway = localIpInfo.gateway,
            dnsServers = localIpInfo.dnsServers
        )
        emit(combinedInfo)
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchPublicIpInfo(): IpInfo {
        return try {
            // Using HTTPS for Play Store compliance
            val response: PublicIpResponse = client.get("https://ip-api.com/json").body()
            IpInfo(
                publicIp = response.query,
                isp = response.isp,
                city = response.city,
                country = response.country
            )
        } catch (e: Exception) {
            IpInfo() // Return default values on error
        }
    }

    private fun getLocalIpInfo(): IpInfo {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val activeNetwork = connectivityManager.activeNetwork
        val linkProperties = connectivityManager.getLinkProperties(activeNetwork)

        val localIp = linkProperties?.linkAddresses?.firstOrNull { it.address is java.net.Inet4Address }?.address?.hostAddress ?: "N/A"
        val gateway = linkProperties?.routes?.firstOrNull { it.isDefaultRoute }?.gateway?.hostAddress ?: "N/A"
        val dnsServers = linkProperties?.dnsServers?.mapNotNull { it.hostAddress } ?: emptyList()

        return IpInfo(localIp = localIp, gateway = gateway, dnsServers = dnsServers)
    }
}

@Serializable
data class PublicIpResponse(
    val query: String,
    val isp: String,
    val city: String,
    val country: String
)
