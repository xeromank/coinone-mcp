package kr.co.coinone.coinonemcp.utils

import kotlin.math.abs

object TechnicalIndicators {

    fun calculateRSI(prices: List<Double>, period: Int = 14): List<Double?> {
        if (prices.size < period + 1) {
            return List(prices.size) { null }
        }

        val rsiValues = mutableListOf<Double?>()

        // Not enough data for first 'period' values
        repeat(period) {
            rsiValues.add(null)
        }

        // Calculate price changes
        val changes = mutableListOf<Double>()
        for (i in 1 until prices.size) {
            changes.add(prices[i] - prices[i - 1])
        }

        // Calculate initial average gain and loss
        var avgGain = 0.0
        var avgLoss = 0.0

        for (i in 0 until period) {
            val change = changes[i]
            if (change > 0) {
                avgGain += change
            } else {
                avgLoss += abs(change)
            }
        }

        avgGain /= period
        avgLoss /= period

        // Calculate first RSI value
        val firstRS = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
        val firstRSI = 100 - (100 / (1 + firstRS))
        rsiValues.add(firstRSI)

        // Calculate subsequent RSI values using smoothed average
        for (i in period until changes.size) {
            val change = changes[i]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0

            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period

            val rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
            val rsi = 100 - (100 / (1 + rs))
            rsiValues.add(rsi)
        }

        return rsiValues
    }

    fun getRSISignal(rsi: Double): String {
        return when {
            rsi >= 70 -> "Overbought"
            rsi <= 30 -> "Oversold"
            else -> "Neutral"
        }
    }
}