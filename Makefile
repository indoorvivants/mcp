# TODO: convert this to an SBT task and remove Makefile
download-schema:
	curl -Lo schema.json https://raw.githubusercontent.com/modelcontextprotocol/modelcontextprotocol/refs/heads/main/schema/2025-06-18/schema.json
