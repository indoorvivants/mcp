package mcp

import mcp.json.*

/** Notification of a log message passed from server to client. If no
  * logging/setLevel request has been sent from the client, the server MAY
  * decide which messages to send automatically.
  */
case class LoggingMessageNotification(
    params: LoggingMessageNotification.Params,
    method: "notifications/message" = "notifications/message"
) derives ReadWriter

object LoggingMessageNotification:
  case class Params(
      data: ujson.Value,
      /** The severity of this log message.
        */
      level: mcp.LoggingLevel,
      /** An optional name of the logger issuing this message.
        */
      logger: Option[String] = None
  ) derives ReadWriter
end LoggingMessageNotification
