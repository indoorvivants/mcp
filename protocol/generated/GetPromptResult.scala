package mcp

import mcp.json.*

/**
 * The server's response to a prompts/get request from the client.
 */
case class GetPromptResult(
   messages: Seq[mcp.PromptMessage],
   /**
    * This result property is reserved by the protocol to allow clients and servers to attach additional metadata to their responses.
    */
   _meta: Option[ujson.Value] = None,
   /**
    * An optional description for the prompt.
    */
   description: Option[String] = None,
) derives ReadWriter
