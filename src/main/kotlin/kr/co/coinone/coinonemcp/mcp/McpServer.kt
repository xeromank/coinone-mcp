package kr.co.coinone.coinonemcp.mcp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kr.co.coinone.coinonemcp.tools.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

@Component
@ConditionalOnProperty(name = ["mcp.server.enabled"], havingValue = "true", matchIfMissing = false)
class McpServer : CommandLineRunner {

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

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun run(vararg args: String?) {
        val reader = BufferedReader(InputStreamReader(System.`in`))

        // Main loop to process incoming messages
        while (true) {
            try {
                val line = reader.readLine() ?: break
                processMessage(line)
            } catch (e: Exception) {
                // Silently handle errors or use proper logging framework
                e.printStackTrace(System.err)
            }
        }
    }

    private fun handleInitialize(id: Any?, params: Map<String, Any>?): Map<String, Any?> {
        val protocolVersion = params?.get("protocolVersion") as? String ?: "2025-06-18"
        
        return mapOf(
            "jsonrpc" to "2.0",
            "id" to id,
            "result" to mapOf(
                "protocolVersion" to "2025-06-18",
                "capabilities" to mapOf(
                    "tools" to mapOf<String, Any>()
                ),
                "serverInfo" to mapOf(
                    "name" to "coinone-mcp-server",
                    "version" to "0.0.1"
                )
            )
        )
    }

    private fun processMessage(message: String) {
        val request = objectMapper.readValue(message, Map::class.java) as Map<String, Any>
        val method = request["method"] as? String
        val id = request["id"]
        val params = request["params"] as? Map<String, Any>

        when (method) {
            "initialize" -> {
                val response = handleInitialize(id, params)
                println(objectMapper.writeValueAsString(response))
            }
            "notifications/initialized" -> {
                // This is a notification from client, no response needed
            }
            "tools/list" -> sendToolsList(id)
            "tools/call" -> handleToolCall(id, params)
            "resources/list" -> sendResourcesList(id)
            "prompts/list" -> sendPromptsList(id)
            else -> {
                sendError("Unknown method: $method", id)
            }
        }
    }

    private fun sendToolsList(id: Any?) {
        val tools = listOf(
            mapOf(
                "name" to "get_markets",
                "description" to "Get all available trading markets from Coinone",
                "inputSchema" to mapOf(
                    "type" to "object",
                    "properties" to mapOf<String, Any>()
                )
            ),
            mapOf(
                "name" to "get_orderbook",
                "description" to "Get orderbook data for a specific market",
                "inputSchema" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "quoteCurrency" to mapOf(
                            "type" to "string",
                            "default" to "KRW",
                            "description" to "Quote currency (default: KRW)"
                        ),
                        "targetCurrency" to mapOf(
                            "type" to "string",
                            "description" to "Target currency (e.g., BTC, ETH)"
                        )
                    ),
                    "required" to listOf("targetCurrency")
                )
            ),
            mapOf(
                "name" to "get_recent_orders",
                "description" to "Get recent completed orders for a specific market",
                "inputSchema" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "quoteCurrency" to mapOf(
                            "type" to "string",
                            "default" to "KRW",
                            "description" to "Quote currency (default: KRW)"
                        ),
                        "targetCurrency" to mapOf(
                            "type" to "string",
                            "description" to "Target currency (e.g., BTC, ETH)"
                        )
                    ),
                    "required" to listOf("targetCurrency")
                )
            ),
            mapOf(
                "name" to "get_tickers",
                "description" to "Get all tickers with price and volume information",
                "inputSchema" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "quoteCurrency" to mapOf(
                            "type" to "string",
                            "default" to "KRW",
                            "description" to "Quote currency (default: KRW)"
                        )
                    )
                )
            ),
            mapOf(
                "name" to "get_ticker",
                "description" to "Get ticker information for a specific market",
                "inputSchema" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "quoteCurrency" to mapOf(
                            "type" to "string",
                            "default" to "KRW",
                            "description" to "Quote currency (default: KRW)"
                        ),
                        "targetCurrency" to mapOf(
                            "type" to "string",
                            "description" to "Target currency (e.g., BTC, ETH)"
                        )
                    ),
                    "required" to listOf("targetCurrency")
                )
            ),
            mapOf(
                "name" to "get_chart",
                "description" to "Get candlestick chart data for a specific market",
                "inputSchema" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "quoteCurrency" to mapOf(
                            "type" to "string",
                            "default" to "KRW",
                            "description" to "Quote currency (default: KRW)"
                        ),
                        "targetCurrency" to mapOf(
                            "type" to "string",
                            "description" to "Target currency (e.g., BTC, ETH)"
                        ),
                        "interval" to mapOf(
                            "type" to "string",
                            "default" to "1h",
                            "description" to "Chart interval (1m, 5m, 15m, 30m, 1h, 4h, 1d, 1w, 1M)"
                        ),
                        "startTime" to mapOf(
                            "type" to "number",
                            "description" to "Start time in milliseconds (optional)"
                        ),
                        "endTime" to mapOf(
                            "type" to "number",
                            "description" to "End time in milliseconds (optional)"
                        )
                    ),
                    "required" to listOf("targetCurrency")
                )
            )
        )

        val response = mapOf(
            "jsonrpc" to "2.0",
            "id" to id,
            "result" to mapOf(
                "tools" to tools
            )
        )
        println(objectMapper.writeValueAsString(response))
    }

    private fun handleToolCall(id: Any?, params: Map<String, Any>?) {
        val toolName = params?.get("name") as? String
        val arguments = params?.get("arguments") as? Map<String, Any> ?: emptyMap()

        try {
            val result = when (toolName) {
                "get_markets" -> {
                    val request = GetMarketsRequest()
                    getMarketsTool.apply(request)
                }

                "get_orderbook" -> {
                    val request = GetOrderbookRequest(
                        quoteCurrency = arguments["quoteCurrency"] as? String ?: "KRW",
                        targetCurrency = arguments["targetCurrency"] as String
                    )
                    getOrderbookTool.apply(request)
                }

                "get_recent_orders" -> {
                    val request = GetRecentOrdersRequest(
                        quoteCurrency = arguments["quoteCurrency"] as? String ?: "KRW",
                        targetCurrency = arguments["targetCurrency"] as String
                    )
                    getRecentOrdersTool.apply(request)
                }

                "get_tickers" -> {
                    val request = GetTickersRequest(
                        quoteCurrency = arguments["quoteCurrency"] as? String ?: "KRW"
                    )
                    getTickersTool.apply(request)
                }

                "get_ticker" -> {
                    val request = GetTickerRequest(
                        quoteCurrency = arguments["quoteCurrency"] as? String ?: "KRW",
                        targetCurrency = arguments["targetCurrency"] as String
                    )
                    getTickerTool.apply(request)
                }

                "get_chart" -> {
                    val request = GetChartRequest(
                        quoteCurrency = arguments["quoteCurrency"] as? String ?: "KRW",
                        targetCurrency = arguments["targetCurrency"] as String,
                        interval = arguments["interval"] as? String ?: "1h",
                        startTime = (arguments["startTime"] as? Number)?.toLong(),
                        endTime = (arguments["endTime"] as? Number)?.toLong()
                    )
                    getChartTool.apply(request)
                }

                else -> throw IllegalArgumentException("Unknown tool: $toolName")
            }

            val response = mapOf(
                "jsonrpc" to "2.0",
                "id" to id,
                "result" to mapOf(
                    "content" to listOf(
                        mapOf(
                            "type" to "text",
                            "text" to objectMapper.writeValueAsString(result)
                        )
                    )
                )
            )
            println(objectMapper.writeValueAsString(response))
        } catch (e: Exception) {
            sendError("Tool execution failed: ${e.message}", id)
        }
    }

    private fun sendError(message: String, id: Any? = null) {
        val error = mapOf(
            "jsonrpc" to "2.0",
            "id" to id,
            "error" to mapOf(
                "code" to -32603,
                "message" to message
            )
        )
        println(objectMapper.writeValueAsString(error))
    }

    private fun sendResourcesList(id: Any?) {
        val response = mapOf(
            "jsonrpc" to "2.0",
            "id" to id,
            "result" to mapOf(
                "resources" to emptyList<Map<String, Any>>()
            )
        )
        println(objectMapper.writeValueAsString(response))
    }

    private fun sendPromptsList(id: Any?) {
        val response = mapOf(
            "jsonrpc" to "2.0",
            "id" to id,
            "result" to mapOf(
                "prompts" to emptyList<Map<String, Any>>()
            )
        )
        println(objectMapper.writeValueAsString(response))
    }
}
