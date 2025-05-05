package mcp

import mcp.json.*

/**
 * This notification is sent from the client to the server after initialization has finished.
 */
case class InitializedNotification(
   method: "notifications/initialized" = "notifications/initialized",
   params: Option[InitializedNotification.Params] = None,
) derives ReadWriter

object InitializedNotification:
   case class Params(
      /**
       * This parameter name is reserved by MCP to allow clients and servers to attach additional metadata to their notifications.
       */
      _meta: Option[ujson.Value] = None,
   ) derives ReadWriter


