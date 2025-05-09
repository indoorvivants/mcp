package mcp

import mcp.json.*

/** An out-of-band notification used to inform the receiver of a progress update
  * for a long-running request.
  */
case class ProgressNotification(
    params: ProgressNotification.Params,
    method: "notifications/progress" = "notifications/progress"
) derives ReadWriter

object ProgressNotification:
  case class Params(
      /** The progress thus far. This should increase every time progress is
        * made, even if the total is unknown.
        */
      progress: Double,
      /** The progress token which was given in the initial request, used to
        * associate this notification with the request that is proceeding.
        */
      progressToken: mcp.ProgressToken,
      /** An optional message describing the current progress.
        */
      message: Option[String] = None,
      /** Total number of items to process (or total progress required), if
        * known.
        */
      total: Option[Double] = None
  ) derives ReadWriter
end ProgressNotification
