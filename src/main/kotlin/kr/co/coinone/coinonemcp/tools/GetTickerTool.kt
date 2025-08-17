package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.CoinoneApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class GetTickerTool : Function<GetTickerRequest, GetTickerResponse> {

    @Autowired
    private lateinit var apiClient: CoinoneApiClient

    override fun apply(request: GetTickerRequest): GetTickerResponse {
        val response = apiClient.getTicker(request.quoteCurrency, request.targetCurrency)
        val ticker = response.tickers.firstOrNull() 
            ?: throw RuntimeException("No ticker data found")
        
        return GetTickerResponse(
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
            changeAmount = calculateChangeAmount(ticker.first, ticker.last),
            bestAsks = ticker.bestAsks.map {
                OrderDetail(price = it.price, qty = it.qty)
            },
            bestBids = ticker.bestBids.map {
                OrderDetail(price = it.price, qty = it.qty)
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

    private fun calculateChangeAmount(first: String, last: String): String {
        return try {
            val firstPrice = first.toDouble()
            val lastPrice = last.toDouble()
            val change = lastPrice - firstPrice
            String.format("%.0f", change)
        } catch (e: Exception) {
            "0"
        }
    }
}

data class GetTickerRequest(
    val quoteCurrency: String = "KRW",
    val targetCurrency: String
)

data class GetTickerResponse(
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
    val changeAmount: String,
    val bestAsks: List<OrderDetail>,
    val bestBids: List<OrderDetail>
)

data class OrderDetail(
    val price: String,
    val qty: String
)
