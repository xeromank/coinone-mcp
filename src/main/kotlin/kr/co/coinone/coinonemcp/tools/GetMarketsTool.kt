package kr.co.coinone.coinonemcp.tools

import kr.co.coinone.coinonemcp.client.CoinoneApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class GetMarketsTool : Function<GetMarketsRequest, GetMarketsResponse> {

    @Autowired
    private lateinit var apiClient: CoinoneApiClient

    override fun apply(request: GetMarketsRequest): GetMarketsResponse {
        val markets = apiClient.getMarkets("KRW")
        return GetMarketsResponse(
            markets = markets.markets.map { market ->
                MarketInfo(
                    quoteCurrency = market.quoteCurrency,
                    targetCurrency = market.targetCurrency,
                    tradeStatus = market.tradeStatus,
                    maintenanceStatus = market.maintenanceStatus,
                    pair = "${market.targetCurrency}/${market.quoteCurrency}"
                )
            }
        )
    }
}

data class GetMarketsRequest(
    val dummy: String? = null
)

data class GetMarketsResponse(
    val markets: List<MarketInfo>
)

data class MarketInfo(
    val quoteCurrency: String,
    val targetCurrency: String,
    val tradeStatus: Int,
    val maintenanceStatus: Int,
    val pair: String
)
