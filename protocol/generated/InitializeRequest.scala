package mcp

import upickle.default.*

/** This request is sent from the client to the server when it first connects, asking it to begin initialization. */
@upickle.implicits.serializeDefaults(true)
case class InitializeRequest(
  method: "initialize" = "initialize",
  params: InitializeRequest.Params,
) derives ReadWriter

object InitializeRequest:
  @upickle.implicits.serializeDefaults(true)
  case class Params(
    capabilities: mcp.ClientCapabilities,
    clientInfo: mcp.Implementation,
    protocolVersion: String,
  ) derives ReadWriter


