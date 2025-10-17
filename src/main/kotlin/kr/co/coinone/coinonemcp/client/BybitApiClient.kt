package kr.co.coinone.coinonemcp.client

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class BybitApiClient {
    private val restTemplate = RestTemplate()
    private val baseUrl = "https://api.bybit.com/v5"

    fun getKline(
        category: String = "linear",
        symbol: String,
        interval: String,
        start: Long? = null,
        end: Long? = null,
        limit: Int = 200
    ): BybitKlineResponse {
        val url = buildString {
            append("$baseUrl/market/kline?category=$category&symbol=$symbol&interval=$interval&limit=$limit")
            start?.let { append("&start=$it") }
            end?.let { append("&end=$it") }
        }

        return restTemplate.getForObject(
            url,
            BybitKlineResponse::class.java
        ) ?: throw RuntimeException("Failed to get kline data")
    }
}

data class BybitKlineResponse(
    @JsonProperty("retCode") val retCode: Int,
    @JsonProperty("retMsg") val retMsg: String,
    val result: BybitKlineResult,
    @JsonProperty("retExtInfo") val retExtInfo: Map<String, Any>? = null,
    val time: Long
)

data class BybitKlineResult(
    val category: String,
    val symbol: String,
    val list: List<List<String>>
)

data class BybitKlineData(
    val startTime: String,
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val closePrice: String,
    val volume: String,
    val turnover: String
) {
    companion object {
        fun fromList(data: List<String>): BybitKlineData {
            return BybitKlineData(
                startTime = data[0],
                openPrice = data[1],
                highPrice = data[2],
                lowPrice = data[3],
                closePrice = data[4],
                volume = data[5],
                turnover = data[6]
            )
        }
    }
}