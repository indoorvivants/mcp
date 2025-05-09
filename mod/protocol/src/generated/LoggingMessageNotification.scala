/*
 * Copyright 2020 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
