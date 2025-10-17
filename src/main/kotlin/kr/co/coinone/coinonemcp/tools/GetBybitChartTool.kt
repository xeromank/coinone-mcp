package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.BybitApiClient
import kr.co.coinone.coinonemcp.client.BybitKlineData
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
        val klineResponse = apiClient.getKline(
            category = request.category,
            symbol = request.symbol,
            interval = request.interval,
            start = request.start,
            end = request.end,
            limit = request.limit
        )

        return GetBybitChartResponse(
            symbol = klineResponse.result.symbol,
            category = klineResponse.result.category,
            interval = request.interval,
            candles = klineResponse.result.list.map { dataList ->
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
                    changeRate = calculateChangeRate(klineData.openPrice, klineData.closePrice)
                )
            }
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
    val changeRate: String
)