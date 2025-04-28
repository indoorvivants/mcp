package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** Capabilities a client may support. Known capabilities are defined here, in this schema, but this is not a closed set: any client can define its own, additional capabilities. */
case class ClientCapabilities(
  /** Experimental, non-standard capabilities that the client supports. */
  experimental: Option[ujson.Value] = None,
  /** Present if the client supports listing roots. */
  roots: Option[ClientCapabilities.Roots] = None,
  /** Present if the client supports sampling from an LLM. */
  sampling: Option[ujson.Value] = None,
) derives ReadWriter

object ClientCapabilities:
  case class Roots(
    listChanged: Option[Boolean] = None,
  ) derives ReadWriter

