package mcp

import mcp.json.*

/** A uniquely identifying ID for a request in JSON-RPC.
  */
val RequestId = Builder[String]("String")
  .orElse[Int]("Int")

type RequestId = RequestId.BuilderType
