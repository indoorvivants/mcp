package mcp

import mcp.json.*

/**
 * Sent from the client to request a list of prompts and prompt templates the server has.
 */
case class ListPromptsRequest(
   method: "prompts/list" = "prompts/list",
   params: Option[ListPromptsRequest.Params] = None,
) derives ReadWriter

object ListPromptsRequest:
   case class Params(
      /**
       * An opaque token representing the current pagination position.
       * If provided, the server should return results starting after this cursor.
       */
      cursor: Option[String] = None,
   ) derives ReadWriter


