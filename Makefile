generate:
	scala-cli run generator runtime protocol -M mcp.generator

sample-native:
	scala-cli package protocol runtime sample --native -f -o ./sample-native

run-sample:
	scala-cli run protocol runtime sample

publish-local:
	scala-cli publish local protocol runtime project.scala --signer none --workspace .
	scala-cli publish local protocol runtime project.scala --native --signer none --workspace .
	scala-cli publish local protocol runtime project.scala --js --signer none --workspace .
