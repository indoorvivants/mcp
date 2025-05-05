package mcp

import mcp.json.*

/** Capabilities that a server may support. Known capabilities are defined here,
  * in this schema, but this is not a closed set: any server can define its own,
  * additional capabilities.
  */
case class ServerCapabilities(
    /** Present if the server supports argument autocompletion suggestions.
      */
    completions: Option[ujson.Obj] = None,
    /** Experimental, non-standard capabilities that the server supports.
      */
    experimental: Option[ujson.Obj] = None,
    /** Present if the server supports sending log messages to the client.
      */
    logging: Option[ujson.Obj] = None,
    /** Present if the server offers any prompt templates.
      */
    prompts: Option[ServerCapabilities.Prompts] = None,
    /** Present if the server offers any resources to read.
      */
    resources: Option[ServerCapabilities.Resources] = None,
    /** Present if the server offers any tools to call.
      */
    tools: Option[ServerCapabilities.Tools] = None
) derives ReadWriter

object ServerCapabilities:
  case class Prompts(
      listChanged: Option[Boolean] = None
  ) derives ReadWriter
  case class Resources(
      listChanged: Option[Boolean] = None,
      subscribe: Option[Boolean] = None
  ) derives ReadWriter
  case class Tools(
      listChanged: Option[Boolean] = None
  ) derives ReadWriter
end ServerCapabilities
