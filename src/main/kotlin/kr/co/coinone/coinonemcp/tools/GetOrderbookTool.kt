package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.CoinoneApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class GetOrderbookTool : Function<GetOrderbookRequest, GetOrderbookResponse> {

    @Autowired
    private lateinit var apiClient: CoinoneApiClient

    override fun apply(request: GetOrderbookRequest): GetOrderbookResponse {
        val orderbook = apiClient.getOrderbook(request.quoteCurrency, request.targetCurrency)
        return GetOrderbookResponse(
            timestamp = orderbook.timestamp,
            quoteCurrency = orderbook.quoteCurrency,
            targetCurrency = orderbook.targetCurrency,
            asks = orderbook.asks.map {
                OrderInfo(price = it.price, qty = it.qty)
            },
            bids = orderbook.bids.map {
                OrderInfo(price = it.price, qty = it.qty)
            }
        )
    }
}

data class GetOrderbookRequest(
    val quoteCurrency: String = "KRW",
    val targetCurrency: String
)

data class GetOrderbookResponse(
    val timestamp: Long,
    val quoteCurrency: String,
    val targetCurrency: String,
    val asks: List<OrderInfo>,
    val bids: List<OrderInfo>
)

data class OrderInfo(
    val price: String,
    val qty: String
)
