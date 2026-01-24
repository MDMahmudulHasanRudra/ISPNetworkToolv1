package com.rudra.ispnetworktool.domain.logic

import com.rudra.ispnetworktool.domain.model.BandwidthResult
import com.rudra.ispnetworktool.domain.model.PackageInput
import com.rudra.ispnetworktool.domain.model.ServiceSelection

class BandwidthCalculator {

    fun calculate(
        packages: List<PackageInput>,
        services: ServiceSelection,
        bufferPercent: Int,
        contentionRatio: Int = 10
    ): BandwidthResult {
        var totalAccessBandwidth = 0.0
        var totalSubscribers = 0

        packages.forEach { pkg ->
            totalAccessBandwidth += (pkg.speedMbps.toDouble() * pkg.users) / contentionRatio
            totalSubscribers += pkg.users
        }

        // Apply buffer
        val baseBandwidth = totalAccessBandwidth * (1 + bufferPercent / 100.0)

        // Estimated Traffic Distribution (Typical ISP Profile)
        // These ratios can be adjusted based on real-world peering/cache performance
        val iptRatio = if (services.ipt) 0.30 else 0.0
        val ggcRatio = if (services.ggc) 0.25 else 0.0
        val fnaRatio = if (services.fna) 0.15 else 0.0
        val cdnRatio = if (services.cdn) 0.15 else 0.0
        val bdixRatio = if (services.bdix) 0.10 else 0.0
        val baishanRatio = if (services.baishan) 0.05 else 0.0

        val totalRatio = iptRatio + ggcRatio + fnaRatio + cdnRatio + bdixRatio + baishanRatio
        
        // Normalize ratios if they don't add up to 1 (e.g. some services disabled)
        val factor = if (totalRatio > 0) 1.0 / totalRatio else 0.0

        return BandwidthResult(
            ipt = baseBandwidth * iptRatio * factor,
            ggc = baseBandwidth * ggcRatio * factor,
            fna = baseBandwidth * fnaRatio * factor,
            cdn = baseBandwidth * cdnRatio * factor,
            bdix = baseBandwidth * bdixRatio * factor,
            baishan = baseBandwidth * baishanRatio * factor,
            total = baseBandwidth,
            totalSubscribers = totalSubscribers
        )
    }
}
