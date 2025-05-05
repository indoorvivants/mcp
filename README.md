# Model Context Protocol for Scala 3

This is a WIP library implementing the [Model Context Protocol](https://modelcontextprotocol.io/introduction).

At the moment the library consists of three parts:

1. Code generator that uses JSON schema from MCP to implement a subset of the protocol
2. Minimal runtime to help serialising structures in JSON
3. Minimal jsonrpc runtime implementing JSONRPC 2.0 protocol

Note that the jsonrpc implementation is blocking, does not support cancellation, and is generally not designed for serious usage – but it's great to get things off the ground quickly! In the future, this library will provide an integration with [jsonrpclib](https://github.com/neandertech/jsonrpclib/), once that library is published for Scala Native 0.5 (or may be earlier, if someone works on that integration).

## Getting started 

You can run the provided  [sample](./sample/main.scala) using [MCP inspector](https://github.com/modelcontextprotocol/inspector) as long as you have [Scala CLI](https://scala-cli.virtuslab.org/) and [npx](https://docs.npmjs.com/cli/v9/commands/npx?v=true) installed:

```bash
npx @modelcontextprotocol/inspector make run-sample
```



