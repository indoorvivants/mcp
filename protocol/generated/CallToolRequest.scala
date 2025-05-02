package mcp

import upickle.default.*

/** Used by the client to invoke a tool provided by the server. */
@upickle.implicits.serializeDefaults(true)
case class CallToolRequest(
  method: "tools/call" = "tools/call",
  params: CallToolRequest.Params,
) derives ReadWriter

object CallToolRequest:
  @upickle.implicits.serializeDefaults(true)
  case class Params(
    arguments: Option[ujson.Value] = None,
    name: String,
  ) derives ReadWriter


