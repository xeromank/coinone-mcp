package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.CoinoneApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class GetRecentOrdersTool : Function<GetRecentOrdersRequest, GetRecentOrdersResponse> {

    @Autowired
    private lateinit var apiClient: CoinoneApiClient

    override fun apply(request: GetRecentOrdersRequest): GetRecentOrdersResponse {
        val recentOrders = apiClient.getRecentOrders(request.quoteCurrency, request.targetCurrency)
        return GetRecentOrdersResponse(
            transactions = recentOrders.transactions.map { trade ->
                TradeInfo(
                    id = trade.id,
                    timestamp = trade.timestamp,
                    price = trade.price,
                    qty = trade.qty,
                    isSellerMaker = trade.isSellerMaker,
                    type = if (trade.isSellerMaker) "BUY" else "SELL"
                )
            }
        )
    }
}

data class GetRecentOrdersRequest(
    val quoteCurrency: String = "KRW",
    val targetCurrency: String
)

data class GetRecentOrdersResponse(
    val transactions: List<TradeInfo>
)

data class TradeInfo(
    val id: String,
    val timestamp: Long,
    val price: String,
    val qty: String,
    val isSellerMaker: Boolean,
    val type: String
)
