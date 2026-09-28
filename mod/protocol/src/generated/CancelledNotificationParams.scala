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

/** Parameters for a `notifications/cancelled` notification.
  */
case class CancelledNotificationParams(
    /** See [General fields:
      * `_meta`](/specification/2025-11-25/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ujson.Obj] = None,
    /** An optional string describing the reason for the cancellation. This MAY
      * be logged or presented to the user.
      */
    reason: Option[String] = None,
    /** The ID of the request to cancel.
      *
      * This MUST correspond to the ID of a request previously issued in the
      * same direction. This MUST be provided for cancelling non-task requests.
      * This MUST NOT be used for cancelling tasks (use the `tasks/cancel`
      * request instead).
      */
    requestId: Option[mcp.RequestId] = None
) derives ReadWriter
