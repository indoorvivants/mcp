package mcp

import mcp.json.*

/** Used by the client to invoke a tool provided by the server.
  */
case class CallToolRequest(
    params: CallToolRequest.Params,
    method: "tools/call" = "tools/call"
) derives ReadWriter

object CallToolRequest:
  case class Params(
      name: String,
      arguments: Option[ujson.Obj] = None
  ) derives ReadWriter
end CallToolRequest
