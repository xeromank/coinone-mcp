package kr.co.coinone.coinonemcp.controller

import kr.co.coinone.coinonemcp.tools.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coinone")
class CoinoneController {

    @Autowired
    private lateinit var getMarketsTool: GetMarketsTool

    @Autowired
    private lateinit var getOrderbookTool: GetOrderbookTool

    @Autowired
    private lateinit var getRecentOrdersTool: GetRecentOrdersTool

    @Autowired
    private lateinit var getTickersTool: GetTickersTool

    @Autowired
    private lateinit var getTickerTool: GetTickerTool

    @Autowired
    private lateinit var getChartTool: GetChartTool

    @GetMapping("/markets")
    fun getMarkets(): GetMarketsResponse {
        return getMarketsTool.apply(GetMarketsRequest())
    }

    @GetMapping("/orderbook/{targetCurrency}")
    fun getOrderbook(
        @PathVariable targetCurrency: String,
        @RequestParam(defaultValue = "KRW") quoteCurrency: String
    ): GetOrderbookResponse {
        return getOrderbookTool.apply(GetOrderbookRequest(quoteCurrency, targetCurrency))
    }

    @GetMapping("/trades/{targetCurrency}")
    fun getRecentOrders(
        @PathVariable targetCurrency: String,
        @RequestParam(defaultValue = "KRW") quoteCurrency: String
    ): GetRecentOrdersResponse {
        return getRecentOrdersTool.apply(GetRecentOrdersRequest(quoteCurrency, targetCurrency))
    }

    @GetMapping("/tickers")
    fun getTickers(
        @RequestParam(defaultValue = "KRW") quoteCurrency: String
    ): GetTickersResponse {
        return getTickersTool.apply(GetTickersRequest(quoteCurrency))
    }

    @GetMapping("/ticker/{targetCurrency}")
    fun getTicker(
        @PathVariable targetCurrency: String,
        @RequestParam(defaultValue = "KRW") quoteCurrency: String
    ): GetTickerResponse {
        return getTickerTool.apply(GetTickerRequest(quoteCurrency, targetCurrency))
    }

    @GetMapping("/chart/{targetCurrency}")
    fun getChart(
        @PathVariable targetCurrency: String,
        @RequestParam(defaultValue = "KRW") quoteCurrency: String,
        @RequestParam(defaultValue = "1h") interval: String,
        @RequestParam(required = false) startTime: Long?,
        @RequestParam(required = false) endTime: Long?
    ): GetChartResponse {
        return getChartTool.apply(
            GetChartRequest(quoteCurrency, targetCurrency, interval, startTime, endTime)
        )
    }
}
