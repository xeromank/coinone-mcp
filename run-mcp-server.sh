#!/bin/bash

# Run MCP Server with STDIO transport
java -jar coinone-mcp-0.0.1.jar \
  --mcp.server.enabled=true \
  --spring.main.web-application-type=none
