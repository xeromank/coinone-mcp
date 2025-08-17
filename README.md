# Coinone MCP Server

코인원(Coinone) 거래소의 퍼블릭 API를 MCP(Model Context Protocol) 서버로 제공하는 프로젝트입니다.

## 기능

- 전체 거래 종목 조회
- 오더북 조회
- 최근 체결 내역 조회
- 전체 시세 정보 조회
- 특정 종목 시세 정보 조회
- 캔들스틱 차트 데이터 조회

## 설치 및 실행

### 빌드
```bash
./gradlew build
```

### REST API 서버 실행
```bash
./gradlew bootRun
```
서버는 http://localhost:8080 에서 실행됩니다.

### MCP 서버 실행 (STDIO)
```bash
./run-mcp-server.sh
```

## REST API 엔드포인트

### 전체 종목 조회
```
GET /api/coinone/markets
```

### 오더북 조회
```
GET /api/coinone/orderbook/{targetCurrency}?quoteCurrency=KRW
```
예시: `/api/coinone/orderbook/BTC`

### 최근 체결 조회
```
GET /api/coinone/trades/{targetCurrency}?quoteCurrency=KRW
```
예시: `/api/coinone/trades/ETH`

### 전체 시세 정보
```
GET /api/coinone/tickers?quoteCurrency=KRW
```

### 특정 종목 시세 정보
```
GET /api/coinone/ticker/{targetCurrency}?quoteCurrency=KRW
```
예시: `/api/coinone/ticker/BTC`

### 캔들스틱 차트 데이터
```
GET /api/coinone/chart/{targetCurrency}?quoteCurrency=KRW&interval=1h&startTime=&endTime=
```
예시: `/api/coinone/chart/BTC?interval=1d`

지원 interval: 1m, 5m, 15m, 30m, 1h, 4h, 1d, 1w, 1M

## MCP Tools

MCP 서버로 실행 시 다음 도구들을 사용할 수 있습니다:

- `get_markets`: 전체 거래 종목 조회
- `get_orderbook`: 오더북 조회
- `get_recent_orders`: 최근 체결 조회
- `get_tickers`: 전체 시세 정보
- `get_ticker`: 특정 종목 시세 정보
- `get_chart`: 캔들스틱 차트 데이터

## Claude Desktop 설정

Claude Desktop에서 사용하려면 다음과 같이 설정하세요:

```json
{
  "mcpServers": {
    "coinone": {
      "command": "/path/to/coinone-mcp/run-mcp-server.sh"
    }
  }
}
```

## 기술 스택

- Kotlin
- Spring Boot 3.5.4
- Gradle
- Jackson (JSON 처리)