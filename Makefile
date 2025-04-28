generate:
	scala-cli run generator runtime mcp_generated -M mcp.generator

sample-native:
	scala-cli package mcp_generated runtime sample --native -f -o ./sample-native
