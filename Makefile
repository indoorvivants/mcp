generate:
	scala-cli run generator runtime -M mcp.generator

watch-generate:
	scala-cli run -w generator runtime -M mcp.generator

sample-native:
	scala-cli package protocol runtime sample --native -f -o ./sample-native

sample-jar:
	scala-cli package protocol runtime sample --assembly -f -o ./sample-jar

run-sample:
	@scala-cli run protocol runtime sample

publish-local:
	scala-cli publish local protocol runtime project.scala --signer none --workspace .
	scala-cli publish local protocol runtime project.scala --native --signer none --workspace .
	scala-cli publish local protocol runtime project.scala --js --signer none --workspace .
