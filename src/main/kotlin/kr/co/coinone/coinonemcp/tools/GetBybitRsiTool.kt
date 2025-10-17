package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.BybitApiClient
import kr.co.coinone.coinonemcp.client.BybitKlineData
import kr.co.coinone.coinonemcp.utils.TechnicalIndicators
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class GetBybitRsiTool : Function<GetBybitRsiRequest, GetBybitRsiResponse> {

    @Autowired
    private lateinit var apiClient: BybitApiClient

    override fun apply(request: GetBybitRsiRequest): GetBybitRsiResponse {
        // Get more data than RSI period to calculate RSI properly
        val dataLimit = maxOf(request.limit, request.rsiPeriod * 3)

        val klineResponse = apiClient.getKline(
            category = request.category,
            symbol = request.symbol,
            interval = request.interval,
            start = request.start,
            end = request.end,
            limit = dataLimit
        )

        // Extract close prices and convert to Double
        val closePrices = klineResponse.result.list
            .reversed() // Reverse to get chronological order
            .map { dataList ->
                BybitKlineData.fromList(dataList).closePrice.toDouble()
            }

        // Calculate RSI
        val rsiValues = TechnicalIndicators.calculateRSI(closePrices, request.rsiPeriod)

        // Prepare RSI data with candles
        val candles = klineResponse.result.list
            .reversed()
            .mapIndexed { index, dataList ->
                val klineData = BybitKlineData.fromList(dataList)
                BybitRsiCandleInfo(
                    timestamp = klineData.startTime.toLong(),
                    datetime = formatTimestamp(klineData.startTime.toLong()),
                    open = klineData.openPrice,
                    high = klineData.highPrice,
                    low = klineData.lowPrice,
                    close = klineData.closePrice,
                    volume = klineData.volume,
                    turnover = klineData.turnover,
                    rsi = rsiValues.getOrNull(index),
                    rsiSignal = rsiValues.getOrNull(index)?.let { TechnicalIndicators.getRSISignal(it) }
                )
            }
            .takeLast(request.limit) // Return only requested number of candles
            .reversed() // Reverse back to match original order (newest first)

        val currentRsi = candles.firstOrNull()?.rsi
        val currentSignal = currentRsi?.let { TechnicalIndicators.getRSISignal(it) }

        return GetBybitRsiResponse(
            symbol = klineResponse.result.symbol,
            category = klineResponse.result.category,
            interval = request.interval,
            rsiPeriod = request.rsiPeriod,
            currentRsi = currentRsi,
            currentSignal = currentSignal,
            candles = candles
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}

data class GetBybitRsiRequest(
    val symbol: String,
    val category: String = "linear",
    val interval: String = "1",
    val rsiPeriod: Int = 14,
    val start: Long? = null,
    val end: Long? = null,
    val limit: Int = 50
)

data class GetBybitRsiResponse(
    val symbol: String,
    val category: String,
    val interval: String,
    val rsiPeriod: Int,
    val currentRsi: Double?,
    val currentSignal: String?,
    val candles: List<BybitRsiCandleInfo>
)

data class BybitRsiCandleInfo(
    val timestamp: Long,
    val datetime: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val turnover: String,
    val rsi: Double?,
    val rsiSignal: String?
)