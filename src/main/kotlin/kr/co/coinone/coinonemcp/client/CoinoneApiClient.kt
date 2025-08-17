package kr.co.coinone.coinonemcp.client

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class CoinoneApiClient {
    private val restTemplate = RestTemplate()
    private val baseUrl = "https://api.coinone.co.kr/public/v2"

    fun getMarkets(quoteCurrency: String = "KRW"): MarketsResponse {
        return restTemplate.getForObject(
            "$baseUrl/markets/{quote_currency}", 
            MarketsResponse::class.java,
            quoteCurrency
        ) ?: throw RuntimeException("Failed to get markets")
    }

    fun getOrderbook(quoteCurrency: String, targetCurrency: String): OrderbookResponse {
        return restTemplate.getForObject(
            "$baseUrl/orderbook/{quote_currency}/{target_currency}",
            OrderbookResponse::class.java,
            quoteCurrency, targetCurrency
        ) ?: throw RuntimeException("Failed to get orderbook")
    }

    fun getRecentOrders(quoteCurrency: String, targetCurrency: String): RecentOrdersResponse {
        return restTemplate.getForObject(
            "$baseUrl/trades/{quote_currency}/{target_currency}",
            RecentOrdersResponse::class.java,
            quoteCurrency, targetCurrency
        ) ?: throw RuntimeException("Failed to get recent orders")
    }

    fun getTickers(quoteCurrency: String): TickersResponse {
        return restTemplate.getForObject(
            "$baseUrl/ticker_new/{quote_currency}",
            TickersResponse::class.java,
            quoteCurrency
        ) ?: throw RuntimeException("Failed to get tickers")
    }

    fun getTicker(quoteCurrency: String, targetCurrency: String): TickerResponse {
        return restTemplate.getForObject(
            "$baseUrl/ticker_new/{quote_currency}/{target_currency}",
            TickerResponse::class.java,
            quoteCurrency, targetCurrency
        ) ?: throw RuntimeException("Failed to get ticker")
    }

    fun getChart(
        quoteCurrency: String,
        targetCurrency: String,
        interval: String,
        startTime: Long? = null,
        endTime: Long? = null
    ): ChartResponse {
        val url = buildString {
            append("$baseUrl/chart/{quote_currency}/{target_currency}?interval={interval}")
            startTime?.let { append("&start_time=$it") }
            endTime?.let { append("&end_time=$it") }
        }

        return restTemplate.getForObject(
            url,
            ChartResponse::class.java,
            quoteCurrency, targetCurrency, interval
        ) ?: throw RuntimeException("Failed to get chart")
    }
}

data class MarketsResponse(
    val result: String,
    @JsonProperty("error_code") val errorCode: String,
    @JsonProperty("server_time") val serverTime: Long,
    val markets: List<Market>
)

data class Market(
    @JsonProperty("quote_currency") val quoteCurrency: String,
    @JsonProperty("target_currency") val targetCurrency: String,
    @JsonProperty("price_unit") val priceUnit: String,
    @JsonProperty("qty_unit") val qtyUnit: String,
    @JsonProperty("max_order_amount") val maxOrderAmount: String,
    @JsonProperty("max_price") val maxPrice: String,
    @JsonProperty("max_qty") val maxQty: String,
    @JsonProperty("min_order_amount") val minOrderAmount: String,
    @JsonProperty("min_price") val minPrice: String,
    @JsonProperty("min_qty") val minQty: String,
    @JsonProperty("order_book_units") val orderBookUnits: List<String>,
    @JsonProperty("maintenance_status") val maintenanceStatus: Int,
    @JsonProperty("trade_status") val tradeStatus: Int,
    @JsonProperty("order_types") val orderTypes: List<String>
)

data class OrderbookResponse(
    val result: String,
    @JsonProperty("error_code") val errorCode: String,
    val timestamp: Long,
    val id: String,
    @JsonProperty("quote_currency") val quoteCurrency: String,
    @JsonProperty("target_currency") val targetCurrency: String,
    @JsonProperty("order_book_unit") val orderBookUnit: String,
    val bids: List<Order>,
    val asks: List<Order>
)

data class Order(
    val price: String,
    val qty: String
)

data class RecentOrdersResponse(
    val result: String,
    @JsonProperty("error_code") val errorCode: String,
    @JsonProperty("server_time") val serverTime: Long,
    @JsonProperty("quote_currency") val quoteCurrency: String,
    @JsonProperty("target_currency") val targetCurrency: String,
    val transactions: List<Trade>
)

data class Trade(
    val id: String,
    val timestamp: Long,
    val price: String,
    val qty: String,
    @JsonProperty("is_seller_maker") val isSellerMaker: Boolean
)

data class TickersResponse(
    val result: String,
    @JsonProperty("error_code") val errorCode: String,
    @JsonProperty("server_time") val serverTime: Long,
    val tickers: List<TickerData>
)

data class TickerResponse(
    val result: String,
    @JsonProperty("error_code") val errorCode: String,
    @JsonProperty("server_time") val serverTime: Long,
    val tickers: List<TickerData>
)

data class TickerData(
    @JsonProperty("quote_currency") val quoteCurrency: String,
    @JsonProperty("target_currency") val targetCurrency: String,
    val timestamp: Long,
    @JsonProperty("quote_volume") val quoteVolume: String,
    @JsonProperty("target_volume") val targetVolume: String,
    val high: String,
    val low: String,
    val first: String,
    val last: String,
    @JsonProperty("best_asks") val bestAsks: List<Order>,
    @JsonProperty("best_bids") val bestBids: List<Order>,
    val id: String
)

data class ChartResponse(
    val result: String,
    @JsonProperty("error_code") val errorCode: String,
    @JsonProperty("is_last") val isLast: Boolean,
    val chart: List<ChartData>
)

data class ChartData(
    val timestamp: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    @JsonProperty("target_volume") val targetVolume: String,
    @JsonProperty("quote_volume") val quoteVolume: String
)
