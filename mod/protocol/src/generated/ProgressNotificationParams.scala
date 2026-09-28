/*
 * Copyright 2025 Anton Sviridov
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

/** Parameters for a `notifications/progress` notification.
  */
case class ProgressNotificationParams(
    /** The progress thus far. This should increase every time progress is made,
      * even if the total is unknown.
      */
    progress: Double,
    /** The progress token which was given in the initial request, used to
      * associate this notification with the request that is proceeding.
      */
    progressToken: mcp.ProgressToken,
    /** See [General fields:
      * `_meta`](/specification/2025-11-25/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    /** An optional message describing the current progress.
      */
    message: Option[String] = None,
    /** Total number of items to process (or total progress required), if known.
      */
    total: Option[Double] = None
) derives ReadWriter
