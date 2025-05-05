package mcp

import mcp.json.*

/**
 * Used by the client to get a prompt provided by the server.
 */
case class GetPromptRequest(
   params: GetPromptRequest.Params,
   method: "prompts/get" = "prompts/get",
) derives ReadWriter

object GetPromptRequest:
   case class Params(
      /**
       * The name of the prompt or prompt template.
       */
      name: String,
      /**
       * Arguments to use for templating the prompt.
       */
      arguments: Option[ujson.Value] = None,
   ) derives ReadWriter


