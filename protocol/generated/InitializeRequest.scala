package mcp

import mcp.json.*

/** This request is sent from the client to the server when it first connects,
  * asking it to begin initialization.
  */
case class InitializeRequest(
    params: InitializeRequest.Params,
    method: "initialize" = "initialize"
) derives ReadWriter

object InitializeRequest:
  // given ReadWriter[InitializeResult] = json.readwriter[InitializeResult].
  case class Params(
      capabilities: mcp.ClientCapabilities,
      clientInfo: mcp.Implementation,
      /** The latest version of the Model Context Protocol that the client
        * supports. The client MAY decide to support older versions as well.
        */
      protocolVersion: String
  ) derives ReadWriter
end InitializeRequest
