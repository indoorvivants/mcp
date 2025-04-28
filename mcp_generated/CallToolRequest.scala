package mcp

import upickle.default.*

/** Used by the client to invoke a tool provided by the server. */
case class CallToolRequest(
  method: "tools/call",
  params: CallToolRequest.Params,
) derives ReadWriter

object CallToolRequest:
  case class Params(
    arguments: Option[ujson.Value] = None,
    name: String,
  ) derives ReadWriter


