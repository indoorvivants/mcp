package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** Capabilities that a server may support. Known capabilities are defined here, in this schema, but this is not a closed set: any server can define its own, additional capabilities. */
case class ServerCapabilities(
  /** Present if the server supports argument autocompletion suggestions. */
  completions: Option[ujson.Value] = None,
  /** Experimental, non-standard capabilities that the server supports. */
  experimental: Option[ujson.Value] = None,
  /** Present if the server supports sending log messages to the client. */
  logging: Option[ujson.Value] = None,
  /** Present if the server offers any prompt templates. */
  prompts: Option[(listChanged: Boolean)] = None,
  /** Present if the server offers any resources to read. */
  resources: Option[(listChanged: Boolean, subscribe: Boolean)] = None,
  /** Present if the server offers any tools to call. */
  tools: Option[(listChanged: Boolean)] = None,
) derives ReadWriter
