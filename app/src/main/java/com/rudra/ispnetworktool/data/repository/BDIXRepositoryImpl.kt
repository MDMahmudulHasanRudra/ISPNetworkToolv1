package com.rudra.ispnetworktool.data.repository

import com.rudra.ispnetworktool.domain.model.BDIXNode
import com.rudra.ispnetworktool.domain.model.BDIXPingResult
import com.rudra.ispnetworktool.domain.model.HealthMetrics
import com.rudra.ispnetworktool.domain.model.ISPInfo
import com.rudra.ispnetworktool.domain.repository.PingRepository
import javax.inject.Inject
import com.rudra.ispnetworktool.data.models.PingResult as DataPingResult

class BDIXRepositoryImpl @Inject constructor(
    private val pingRepository: PingRepository
) {
    val bdixNodes = listOf(
        // ==================== DHAKA REGION (Core Nodes) ====================
        BDIXNode("1", "BDIX Main (BTCL)", "103.6.156.1", "Kawran Bazar, Dhaka", 23.7545, 90.3845, 320, "300Gbps", "Core"),
        BDIXNode("2", "BDIX Mirpur", "103.6.156.2", "Mirpur-10, Dhaka", 23.8067, 90.3683, 220, "150Gbps", "Core"),
        BDIXNode("3", "BDIX Mohakhali", "103.6.156.3", "Mohakhali DOHS, Dhaka", 23.7785, 90.4056, 250, "200Gbps", "Core"),
        BDIXNode("4", "BDIX Uttara", "103.6.156.4", "Uttara Sector-7, Dhaka", 23.8715, 90.3984, 180, "100Gbps", "Regional"),
        BDIXNode("5", "BDIX Gulshan", "103.6.156.5", "Gulshan-1, Dhaka", 23.7806, 90.4169, 200, "120Gbps", "Core"),
        BDIXNode("6", "BDIX Dhanmondi", "103.6.156.6", "Dhanmondi-32, Dhaka", 23.7465, 90.3760, 150, "80Gbps", "Regional"),
        BDIXNode("7", "BDIX Motijheel", "103.6.156.7", "Motijheel C/A, Dhaka", 23.7335, 90.4172, 190, "100Gbps", "Core"),
        BDIXNode("8", "BDIX Banani", "103.6.156.8", "Banani-11, Dhaka", 23.7936, 90.4043, 170, "90Gbps", "Core"),
        BDIXNode("9", "BDIX Tejgaon", "103.6.156.9", "Tejgaon I/A, Dhaka", 23.7639, 90.4065, 210, "150Gbps", "Core"),
        BDIXNode("10", "BDIX Bashundhara", "103.6.156.10", "Bashundhara R/A, Dhaka", 23.8134, 90.4243, 140, "70Gbps", "Regional"),
        BDIXNode("11", "BDIX Mohammadpur", "103.6.156.11", "Mohammadpur, Dhaka", 23.7644, 90.3549, 130, "60Gbps", "Regional"),
        BDIXNode("12", "BDIX Malibagh", "103.6.156.12", "Malibagh Chowdhurypara, Dhaka", 23.7432, 90.4273, 110, "50Gbps", "Regional"),
        BDIXNode("13", "BDIX Farmgate", "103.6.156.13", "Farmgate, Dhaka", 23.7552, 90.3899, 160, "80Gbps", "Core"),
        BDIXNode("14", "BDIX Jatrabari", "103.6.156.14", "Jatrabari, Dhaka", 23.7103, 90.4423, 90, "40Gbps", "Regional"),
        BDIXNode("15", "BDIX Keraniganj", "103.6.156.15", "Keraniganj, Dhaka", 23.6469, 90.3469, 70, "30Gbps", "Regional"),

        // ==================== CHATTOGRAM REGION ====================
        BDIXNode("16", "BDIX Chattogram Main", "103.204.81.1", "Agrabad C/A, Chattogram", 22.3242, 91.8123, 160, "100Gbps", "Core"),
        BDIXNode("17", "BDIX Khulshi", "103.204.81.2", "Khulshi, Chattogram", 22.3425, 91.8293, 120, "60Gbps", "Regional"),
        BDIXNode("18", "BDIX Nasirabad", "103.204.81.3", "Nasirabad, Chattogram", 22.3528, 91.8249, 110, "50Gbps", "Regional"),
        BDIXNode("19", "BDIX Chawkbazar", "103.204.81.4", "Chawkbazar, Chattogram", 22.3371, 91.8396, 95, "40Gbps", "Regional"),
        BDIXNode("20", "BDIX EPZ", "103.204.81.5", "CEPZ, Chattogram", 22.2974, 91.8348, 85, "50Gbps", "Industrial"),
        BDIXNode("21", "BDIX Patenga", "103.204.81.6", "Patenga, Chattogram", 22.2500, 91.8167, 70, "30Gbps", "Regional"),

        // ==================== SYLHET REGION ====================
        BDIXNode("22", "BDIX Sylhet Main", "103.204.82.1", "Zindabazar, Sylhet", 24.8949, 91.8687, 90, "40Gbps", "Core"),
        BDIXNode("23", "BDIX Bandarbazar", "103.204.82.2", "Bandarbazar, Sylhet", 24.9032, 91.8736, 75, "30Gbps", "Regional"),
        BDIXNode("24", "BDIX Subhanighat", "103.204.82.3", "Subhanighat, Sylhet", 24.9071, 91.8764, 65, "25Gbps", "Regional"),
        BDIXNode("25", "BDIX Mirabazar", "103.204.82.4", "Mirabazar, Sylhet", 24.8882, 91.8753, 55, "20Gbps", "Regional"),

        // ==================== RAJSHAHI REGION ====================
        BDIXNode("26", "BDIX Rajshahi Main", "103.204.83.1", "Shaheb Bazar, Rajshahi", 24.3745, 88.6042, 85, "30Gbps", "Core"),
        BDIXNode("27", "BDIX Kazla", "103.204.83.2", "Kazla, Rajshahi", 24.3684, 88.6213, 60, "20Gbps", "Regional"),
        BDIXNode("28", "BDIX Binodpur", "103.204.83.3", "Binodpur, Rajshahi", 24.3762, 88.5953, 50, "15Gbps", "Regional"),
        BDIXNode("29", "BDIX RU Campus", "103.204.83.4", "Rajshahi University", 24.3639, 88.6283, 40, "10Gbps", "Education"),

        // ==================== KHULNA REGION ====================
        BDIXNode("30", "BDIX Khulna Main", "103.204.84.1", "Sonadanga, Khulna", 22.8456, 89.5403, 110, "40Gbps", "Core"),
        BDIXNode("31", "BDIX Moylapota", "103.204.84.2", "Moylapota, Khulna", 22.8342, 89.5497, 80, "25Gbps", "Regional"),
        BDIXNode("32", "BDIX Khalishpur", "103.204.84.3", "Khalishpur, Khulna", 22.8197, 89.5536, 70, "20Gbps", "Regional"),
        BDIXNode("33", "BDIX Daulatpur", "103.204.84.4", "Daulatpur, Khulna", 22.8561, 89.5342, 60, "15Gbps", "Regional"),

        // ==================== BARISHAL REGION ====================
        BDIXNode("34", "BDIX Barishal Main", "103.204.85.1", "Nathullabad, Barishal", 22.7010, 90.3535, 60, "20Gbps", "Core"),
        BDIXNode("35", "BDIX Alekanda", "103.204.85.2", "Alekanda, Barishal", 22.6989, 90.3654, 45, "15Gbps", "Regional"),
        BDIXNode("36", "BDIX Rupatali", "103.204.85.3", "Rupatali, Barishal", 22.6893, 90.3682, 40, "10Gbps", "Regional"),

        // ==================== RANGPUR REGION ====================
        BDIXNode("37", "BDIX Rangpur Main", "103.204.86.1", "Jahaj Company More, Rangpur", 25.7439, 89.2752, 70, "25Gbps", "Core"),
        BDIXNode("38", "BDIX Medical", "103.204.86.2", "Medical Road, Rangpur", 25.7465, 89.2587, 50, "15Gbps", "Regional"),
        BDIXNode("39", "BDIX RUET", "103.204.86.3", "RUET Campus, Rangpur", 25.7484, 89.2708, 35, "10Gbps", "Education"),

        // ==================== MYMENSINGH REGION ====================
        BDIXNode("40", "BDIX Mymensingh Main", "103.204.87.1", "Ganginarpar, Mymensingh", 24.7471, 90.4203, 80, "30Gbps", "Core"),
        BDIXNode("41", "BDIX Choto Bazar", "103.204.87.2", "Choto Bazar, Mymensingh", 24.7562, 90.4073, 55, "20Gbps", "Regional"),
        BDIXNode("42", "BDIX BAU Campus", "103.204.87.3", "BAU Campus, Mymensingh", 24.7275, 90.4242, 45, "15Gbps", "Education"),

        // ==================== COMMILLA REGION ====================
        BDIXNode("43", "BDIX Cumilla Main", "103.204.88.1", "Kandirpar, Cumilla", 23.4571, 91.1879, 75, "25Gbps", "Core"),
        BDIXNode("44", "BDIX Station Road", "103.204.88.2", "Station Road, Cumilla", 23.4659, 91.1793, 60, "20Gbps", "Regional"),

        // ==================== NARAYANGANJ REGION ====================
        BDIXNode("45", "BDIX Narayanganj Main", "103.204.89.1", "Chashara, Narayanganj", 23.6238, 90.5000, 95, "40Gbps", "Core"),
        BDIXNode("46", "BDIX Signboard", "103.204.89.2", "Signboard, Narayanganj", 23.6135, 90.5032, 80, "30Gbps", "Regional"),

        // ==================== GAZIPUR REGION ====================
        BDIXNode("47", "BDIX Gazipur Main", "103.204.90.1", "Konabari, Gazipur", 23.9921, 90.4193, 120, "60Gbps", "Core"),
        BDIXNode("48", "BDIX Tongi", "103.204.90.2", "Tongi Bazar, Gazipur", 23.8979, 90.4031, 90, "40Gbps", "Industrial"),
        BDIXNode("49", "BDIX Chandra", "103.204.90.3", "Chandra, Gazipur", 24.0571, 90.4864, 70, "30Gbps", "Regional"),

        // ==================== BDIX-PTX (Peering Exchange) NODES ====================
        BDIXNode("50", "BDIX-PTX Dhaka 1", "103.246.246.1", "Gulshan-2, Dhaka", 23.7949, 90.4148, 180, "200Gbps", "PTX"),
        BDIXNode("51", "BDIX-PTX Dhaka 2", "103.246.247.1", "Banani-11, Dhaka", 23.7941, 90.4097, 160, "150Gbps", "PTX"),
        BDIXNode("52", "BDIX-PTX Uttara", "103.246.248.1", "Uttara Sector-3, Dhaka", 23.8759, 90.3795, 130, "100Gbps", "PTX"),
        BDIXNode("53", "BDIX-PTX Motijheel", "103.246.249.1", "Motijheel C/A, Dhaka", 23.7335, 90.4172, 150, "120Gbps", "PTX"),
        BDIXNode("54", "BDIX-PTX Tejgaon", "103.246.250.1", "Tejgaon I/A, Dhaka", 23.7596, 90.3972, 170, "150Gbps", "PTX"),
        BDIXNode("55", "BDIX-PTX Chattogram", "103.246.251.1", "Agrabad, Chattogram", 22.3242, 91.8123, 110, "80Gbps", "PTX"),
        BDIXNode("56", "BDIX-PTX Sylhet", "103.246.252.1", "Zindabazar, Sylhet", 24.8949, 91.8687, 80, "50Gbps", "PTX"),
        BDIXNode("57", "BDIX-PTX Khulna", "103.246.253.1", "Sonadanga, Khulna", 22.8456, 89.5403, 70, "40Gbps", "PTX"),
        BDIXNode("58", "BDIX-PTX Rajshahi", "103.246.254.1", "Shaheb Bazar, Rajshahi", 24.3745, 88.6042, 65, "30Gbps", "PTX"),

        // ==================== MAJOR ISP BDIX NODES ====================
        BDIXNode("59", "Fiber@Home BDIX", "103.91.144.1", "Tejgaon I/A, Dhaka", 23.7639, 90.4065, 250, "250Gbps", "ISP"),
        BDIXNode("60", "Summit BDIX", "103.91.145.1", "Banani-11, Dhaka", 23.7936, 90.4043, 210, "200Gbps", "ISP"),
        BDIXNode("61", "Aamra Networks BDIX", "103.91.146.1", "Mohammadpur, Dhaka", 23.7644, 90.3549, 190, "150Gbps", "ISP"),
        BDIXNode("62", "Link3 BDIX", "103.91.147.1", "Kawran Bazar, Dhaka", 23.7545, 90.3845, 280, "220Gbps", "ISP"),
        BDIXNode("63", "BDCOM BDIX", "103.91.148.1", "Mirpur-10, Dhaka", 23.8067, 90.3683, 230, "180Gbps", "ISP"),
        BDIXNode("64", "Agni Systems BDIX", "103.91.149.1", "Gulshan-1, Dhaka", 23.7806, 90.4169, 200, "160Gbps", "ISP"),
        BDIXNode("65", "Banglalion BDIX", "103.91.150.1", "Mohakhali DOHS, Dhaka", 23.7785, 90.4056, 170, "120Gbps", "ISP"),
        BDIXNode("66", "Bohubrihi BDIX", "103.91.151.1", "Dhanmondi-32, Dhaka", 23.7465, 90.3760, 140, "100Gbps", "ISP"),

        // ==================== EDUCATIONAL NETWORK NODES ====================
        BDIXNode("67", "BDIX UGC Network", "103.204.91.1", "UGC Building, Dhaka", 23.7329, 90.4022, 95, "50Gbps", "Education"),
        BDIXNode("68", "BDIX BUET Campus", "103.204.91.2", "BUET Campus, Dhaka", 23.7269, 90.3879, 85, "40Gbps", "Education"),
        BDIXNode("69", "BDIX DU Campus", "103.204.91.3", "DU Campus, Dhaka", 23.7334, 90.3947, 90, "45Gbps", "Education"),
        BDIXNode("70", "BDIX CUET Campus", "103.204.91.4", "CUET Campus, Chattogram", 22.4608, 91.9725, 65, "30Gbps", "Education"),
        BDIXNode("71", "BDIX RU Campus", "103.204.91.5", "RU Campus, Rajshahi", 24.3639, 88.6283, 70, "35Gbps", "Education"),
        BDIXNode("72", "BDIX KUET Campus", "103.204.91.6", "KUET Campus, Khulna", 22.8974, 89.5014, 60, "25Gbps", "Education"),

        // ==================== GOVERNMENT & BANKING NODES ====================
        BDIXNode("73", "BDIX Secretariat", "103.204.92.1", "Secretariat, Dhaka", 23.7260, 90.4127, 80, "60Gbps", "Government"),
        BDIXNode("74", "BDIX Shere Bangla Nagar", "103.204.92.2", "Shere Bangla Nagar, Dhaka", 23.7772, 90.3716, 70, "50Gbps", "Government"),
        BDIXNode("75", "BDIX Bangladesh Bank", "103.204.92.3", "Bangladesh Bank, Dhaka", 23.7300, 90.4110, 75, "55Gbps", "Banking"),
        BDIXNode("76", "BDIX Sonali Bank", "103.204.92.4", "Sonali Bank HQ, Dhaka", 23.7292, 90.4118, 65, "40Gbps", "Banking"),

        // ==================== INTERNATIONAL GATEWAYS ====================
        BDIXNode("77", "BDIX Submarine Gateway", "103.6.157.1", "Cox's Bazar", 21.4272, 91.9758, 90, "500Gbps", "International"),
        BDIXNode("78", "BDIX Terrestrial Gateway", "103.6.158.1", "Banglabandha, Panchagarh", 26.3246, 88.5508, 70, "200Gbps", "International"),
        BDIXNode("79", "BDIX Satellite Gateway", "103.6.159.1", "Betbunia, Rangamati", 22.8314, 92.3806, 40, "100Gbps", "International"),

        // ==================== SPECIAL ECONOMIC ZONES ====================
        BDIXNode("80", "BDIX BEPZA", "103.204.93.1", "BEPZA HQ, Dhaka", 23.7991, 90.4153, 85, "70Gbps", "Industrial"),
        BDIXNode("81", "BDIX Savar EPZ", "103.204.93.2", "Savar EPZ, Dhaka", 23.8517, 90.2658, 75, "60Gbps", "Industrial"),
        BDIXNode("82", "BDIX Adamjee EPZ", "103.204.93.3", "Adamjee EPZ, Narayanganj", 23.6278, 90.4989, 80, "65Gbps", "Industrial"),

        // ==================== DATA CENTER NODES ====================
        BDIXNode("83", "BDIX GP Data Center", "103.204.94.1", "Bashundhara, Dhaka", 23.8134, 90.4243, 110, "150Gbps", "DataCenter"),
        BDIXNode("84", "BDIX BTCL Data Center", "103.204.94.2", "Motijheel, Dhaka", 23.7335, 90.4172, 120, "160Gbps", "DataCenter"),
        BDIXNode("85", "BDIX Summit Data Center", "103.204.94.3", "Banani-11, Dhaka", 23.7936, 90.4043, 100, "120Gbps", "DataCenter"),
        BDIXNode("86", "BDIX F@H Data Center", "103.204.94.4", "Tejgaon, Dhaka", 23.7639, 90.4065, 130, "180Gbps", "DataCenter"),

        // ==================== MEDIA & CONTENT NODES ====================
        BDIXNode("87", "BDIX BTV Network", "103.204.95.1", "Rampura, Dhaka", 23.7635, 90.4241, 60, "40Gbps", "Media"),
        BDIXNode("88", "BDIX ATN Network", "103.204.95.2", "Kawran Bazar, Dhaka", 23.7545, 90.3845, 55, "35Gbps", "Media"),
        BDIXNode("89", "BDIX Ekattor TV", "103.204.95.3", "Tejgaon, Dhaka", 23.7639, 90.4065, 50, "30Gbps", "Media"),

        // ==================== HEALTHCARE NETWORK ====================
        BDIXNode("90", "BDIX DGHS Network", "103.204.96.1", "Mohakhali, Dhaka", 23.7785, 90.4056, 70, "50Gbps", "Healthcare"),
        BDIXNode("91", "BDIX Dhaka Medical", "103.204.96.2", "Dhaka Medical College", 23.7286, 90.3984, 65, "45Gbps", "Healthcare"),

        // ==================== SPECIALIZED NODES ====================
        BDIXNode("92", "BDIX IoT Network", "103.204.97.1", "BASIS, Dhaka", 23.7961, 90.4139, 45, "30Gbps", "Special"),
        BDIXNode("93", "BDIX Startup Network", "103.204.97.2", "GP House, Dhaka", 23.7822, 90.4194, 40, "25Gbps", "Special"),
        BDIXNode("94", "BDIX E-Commerce", "103.204.97.3", "Gulshan-1, Dhaka", 23.7806, 90.4169, 55, "35Gbps", "Special"),

        // ==================== NEW ADDITIONS (2024) ====================
        BDIXNode("95", "BDIX Purbachal", "103.6.160.1", "Purbachal, Dhaka", 23.8475, 90.4542, 50, "40Gbps", "Regional"),
        BDIXNode("96", "BDIX Airport", "103.6.160.2", "Hazrat Shahjalal Airport", 23.8433, 90.3978, 65, "50Gbps", "Transport"),
        BDIXNode("97", "BDIX Jamuna Bridge", "103.6.160.3", "Jamuna Multipurpose Bridge", 24.3725, 89.8258, 40, "30Gbps", "Infrastructure"),
        BDIXNode("98", "BDIX Padma Bridge", "103.6.160.4", "Padma Multipurpose Bridge", 23.4461, 90.2622, 45, "35Gbps", "Infrastructure"),
        BDIXNode("99", "BDIX Matarbari", "103.6.160.5", "Matarbari, Cox's Bazar", 21.5664, 91.9328, 30, "25Gbps", "Energy"),
        BDIXNode("100", "BDIX Rooppur Nuclear", "103.6.160.6", "Rooppur, Pabna", 24.0631, 89.0431, 35, "30Gbps", "Energy")
    )

    suspend fun pingNode(nodeId: String): BDIXPingResult {
        val node = bdixNodes.find { it.id == nodeId } ?: return BDIXPingResult(nodeId, 0, 0.0, 100.0, 0.0, false)
        
        var totalRtt = 0f
        var count = 0
        var success = false
        
        try {
            pingRepository.ping(node.ipAddress, 4).collect { result ->
                if (result is DataPingResult.Success) {
                    result.rtt?.let { rtt ->
                        totalRtt += rtt
                        count++
                        success = true
                    }
                }
            }
        } catch (e: Exception) {
            success = false
        }

        return BDIXPingResult(
            nodeId = nodeId,
            timestamp = System.currentTimeMillis(),
            latencyMs = if (count > 0) (totalRtt / count).toDouble() else 0.0,
            packetLoss = if (count > 0) 0.0 else 100.0,
            jitter = 0.0,
            isSuccess = success
        )
    }

    fun getHealthMetrics(nodeId: String): HealthMetrics {
        return HealthMetrics(nodeId, 0.5, 5.0, 99.9, 95)
    }

    fun getISPConnectivity(ispName: String): ISPInfo? {
        return null
    }
}
