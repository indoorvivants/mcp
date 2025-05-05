generate:
	scala-cli run generator runtime -M mcp.generator

watch-generate:
	scala-cli run -w generator runtime -M mcp.generator

sample-native:
	mkdir -p out
	scala-cli package protocol runtime sample --native -f -o ./out/sample-native

sample-jar:
	mkdir -p out
	scala-cli package protocol runtime sample --assembly -f -o ./out/sample-jar

run-sample:
	@scala-cli run protocol runtime sample

check-docs:
	scala-cli compile README.md protocol runtime 

publish-local:
	scala-cli publish local protocol runtime project.scala --signer none --workspace .
	scala-cli publish local protocol runtime project.scala --native --signer none --workspace .
	scala-cli publish local protocol runtime project.scala --js --signer none --workspace .

publish-snapshot:
	scala-cli config publish.credentials oss.sonatype.org env:SONATYPE_USERNAME env:SONATYPE_PASSWORD
	scala-cli publish protocol runtime project.scala --signer none --workspace .
	scala-cli publish protocol runtime project.scala --native --signer none --workspace .
	scala-cli publish protocol runtime project.scala --js --signer none --workspace .

publish:
	scala-cli config publish.credentials oss.sonatype.org env:SONATYPE_USERNAME env:SONATYPE_PASSWORD
	./.github/workflows/import-gpg.sh
	scala-cli publish protocol runtime project.scala --signer gpg --gpg-key 9D8EF0F74E5D78A3 --workspace .
	scala-cli publish protocol runtime project.scala --js --signer gpg --gpg-key 9D8EF0F74E5D78A3 --workspace .
	scala-cli publish protocol runtime project.scala --native --signer gpg --gpg-key 9D8EF0F74E5D78A3 --workspace .

download-schema:
	curl -Lo schema.json https://raw.githubusercontent.com/modelcontextprotocol/modelcontextprotocol/refs/heads/main/schema/2025-03-26/schema.json
