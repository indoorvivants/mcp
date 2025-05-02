package mcp

import upickle.default.*

/** Sent from the client to request a list of tools the server has. */
@upickle.implicits.serializeDefaults(true)
case class ListToolsRequest(
  method: "tools/list" = "tools/list",
  params: Option[ListToolsRequest.Params] = None,
) derives ReadWriter

object ListToolsRequest:
  @upickle.implicits.serializeDefaults(true)
  case class Params(
    cursor: Option[String],
  ) derives ReadWriter


