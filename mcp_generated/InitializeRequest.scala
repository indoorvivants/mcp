package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** This request is sent from the client to the server when it first connects, asking it to begin initialization. */
case class InitializeRequest(
  method: "initialize",
  params: InitializeRequest.Params,
) derives ReadWriter

object InitializeRequest:
  case class Params(
    capabilities: mcp.ClientCapabilities,
    clientInfo: mcp.Implementation,
    protocolVersion: String,
  ) derives ReadWriter

