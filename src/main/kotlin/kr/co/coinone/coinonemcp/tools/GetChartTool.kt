package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.CoinoneApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class GetChartTool : Function<GetChartRequest, GetChartResponse> {

    @Autowired
    private lateinit var apiClient: CoinoneApiClient

    override fun apply(request: GetChartRequest): GetChartResponse {
        val chart = apiClient.getChart(
            request.quoteCurrency,
            request.targetCurrency,
            request.interval,
            request.startTime,
            request.endTime
        )

        return GetChartResponse(
            interval = request.interval,
            candles = chart.chart.map { candle ->
                CandleInfo(
                    timestamp = candle.timestamp,
                    datetime = formatTimestamp(candle.timestamp),
                    open = candle.open,
                    high = candle.high,
                    low = candle.low,
                    close = candle.close,
                    quoteVolume = candle.quoteVolume,
                    targetVolume = candle.targetVolume,
                    changeRate = calculateCandleChangeRate(candle.open, candle.close)
                )
            }
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"))
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    private fun calculateCandleChangeRate(open: String, close: String): String {
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

data class GetChartRequest(
    val quoteCurrency: String = "KRW",
    val targetCurrency: String,
    val interval: String = "1h",
    val startTime: Long? = null,
    val endTime: Long? = null
)

data class GetChartResponse(
    val interval: String,
    val candles: List<CandleInfo>
)

data class CandleInfo(
    val timestamp: Long,
    val datetime: String,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val quoteVolume: String,
    val targetVolume: String,
    val changeRate: String
)
