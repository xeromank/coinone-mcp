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
class GetBybitChartTool : Function<GetBybitChartRequest, GetBybitChartResponse> {

    @Autowired
    private lateinit var apiClient: BybitApiClient

    override fun apply(request: GetBybitChartRequest): GetBybitChartResponse {
        // Fetch more data than requested to calculate RSI properly
        val dataLimit = maxOf(request.limit, 50)

        val klineResponse = apiClient.getKline(
            category = request.category,
            symbol = request.symbol,
            interval = request.interval,
            start = request.start,
            end = request.end,
            limit = dataLimit
        )

        // Extract close prices in chronological order for RSI calculation
        val closePrices = klineResponse.result.list
            .reversed()
            .map { dataList ->
                BybitKlineData.fromList(dataList).closePrice.toDouble()
            }

        // Calculate RSI values
        val rsi6Values = TechnicalIndicators.calculateRSI(closePrices, 6)
        val rsi12Values = TechnicalIndicators.calculateRSI(closePrices, 12)

        // Build candle data with RSI values
        val candlesWithRsi = klineResponse.result.list
            .reversed()
            .mapIndexed { index, dataList ->
                val klineData = BybitKlineData.fromList(dataList)
                BybitCandleInfo(
                    timestamp = klineData.startTime.toLong(),
                    datetime = formatTimestamp(klineData.startTime.toLong()),
                    open = klineData.openPrice,
                    high = klineData.highPrice,
                    low = klineData.lowPrice,
                    close = klineData.closePrice,
                    volume = klineData.volume,
                    turnover = klineData.turnover,
                    changeRate = calculateChangeRate(klineData.openPrice, klineData.closePrice),
                    rsi6 = rsi6Values.getOrNull(index),
                    rsi6Signal = rsi6Values.getOrNull(index)?.let { TechnicalIndicators.getRSISignal(it) },
                    rsi12 = rsi12Values.getOrNull(index),
                    rsi12Signal = rsi12Values.getOrNull(index)?.let { TechnicalIndicators.getRSISignal(it) }
                )
            }
            .takeLast(request.limit)
            .reversed() // Back to newest first

        val currentRsi6 = candlesWithRsi.firstOrNull()?.rsi6
        val currentRsi12 = candlesWithRsi.firstOrNull()?.rsi12

        return GetBybitChartResponse(
            symbol = klineResponse.result.symbol,
            category = klineResponse.result.category,
            interval = request.interval,
            currentRsi6 = currentRsi6,
            currentRsi6Signal = currentRsi6?.let { TechnicalIndicators.getRSISignal(it) },
            currentRsi12 = currentRsi12,
            currentRsi12Signal = currentRsi12?.let { TechnicalIndicators.getRSISignal(it) },
            candles = candlesWithRsi
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    private fun calculateChangeRate(open: String, close: String): String {
        return try {
            val openPrice = open.toDouble()
            val closePrice = close.toDouble()
            val rate = ((closePrice - openPrice) / openPrice * 100)
            String.format("%.2f", rate)
        } catch (e: Exception) {
            "0.00"
        }
    }
}

data class GetBybitChartRequest(
    val symbol: String,
    val category: String = "linear",
    val interval: String = "1",
    val start: Long? = null,
    val end: Long? = null,
    val limit: Int = 200
)

data class GetBybitChartResponse(
    val symbol: String,
    val category: String,
    val interval: String,
    val currentRsi6: Double?,
    val currentRsi6Signal: String?,
    val currentRsi12: Double?,
    val currentRsi12Signal: String?,
    val candles: List<BybitCandleInfo>
)

data class BybitCandleInfo(
    val timestamp: Long,
    val datetime: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val turnover: String,
    val changeRate: String,
    val rsi6: Double?,
    val rsi6Signal: String?,
    val rsi12: Double?,
    val rsi12Signal: String?
)