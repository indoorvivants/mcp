package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** Sent from the client to request a list of tools the server has. */
case class ListToolsRequest(
  method: "tools/list",
  params: Option[(cursor: String)] = None,
) derives ReadWriter
