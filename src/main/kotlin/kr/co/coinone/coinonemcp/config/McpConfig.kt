package kr.co.coinone.coinonemcp.config

import kr.co.coinone.coinonemcp.tools.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class McpConfig {

    @Bean
    fun getMarketsTool() = GetMarketsTool()

    @Bean
    fun getOrderbookTool() = GetOrderbookTool()

    @Bean
    fun getRecentOrdersTool() = GetRecentOrdersTool()

    @Bean
    fun getTickersTool() = GetTickersTool()

    @Bean
    fun getTickerTool() = GetTickerTool()

    @Bean
    fun getChartTool() = GetChartTool()
}
