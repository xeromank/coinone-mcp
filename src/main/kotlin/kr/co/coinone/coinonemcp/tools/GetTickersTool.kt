package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.CoinoneApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class GetTickersTool : Function<GetTickersRequest, GetTickersResponse> {

    @Autowired
    private lateinit var apiClient: CoinoneApiClient

    override fun apply(request: GetTickersRequest): GetTickersResponse {
        val tickers = apiClient.getTickers(request.quoteCurrency)
        return GetTickersResponse(
            tickers = tickers.tickers.map { ticker ->
                TickerSummary(
                    quoteCurrency = ticker.quoteCurrency,
                    targetCurrency = ticker.targetCurrency,
                    pair = "${ticker.targetCurrency}/${ticker.quoteCurrency}",
                    timestamp = ticker.timestamp,
                    quoteVolume = ticker.quoteVolume,
                    targetVolume = ticker.targetVolume,
                    high = ticker.high,
                    low = ticker.low,
                    first = ticker.first,
                    last = ticker.last,
                    changeRate = calculateChangeRate(ticker.first, ticker.last),
                    bestAskPrice = ticker.bestAsks.firstOrNull()?.price,
                    bestBidPrice = ticker.bestBids.firstOrNull()?.price
                )
            }
        )
    }

    private fun calculateChangeRate(first: String, last: String): String {
        return try {
            val firstPrice = first.toDouble()
            val lastPrice = last.toDouble()
            val rate = ((lastPrice - firstPrice) / firstPrice * 100)
            String.format("%.2f", rate)
        } catch (e: Exception) {
            "0.00"
        }
    }
}

data class GetTickersRequest(
    val quoteCurrency: String = "KRW"
)

data class GetTickersResponse(
    val tickers: List<TickerSummary>
)

data class TickerSummary(
    val quoteCurrency: String,
    val targetCurrency: String,
    val pair: String,
    val timestamp: Long,
    val quoteVolume: String,
    val targetVolume: String,
    val high: String,
    val low: String,
    val first: String,
    val last: String,
    val changeRate: String,
    val bestAskPrice: String?,
    val bestBidPrice: String?
)
