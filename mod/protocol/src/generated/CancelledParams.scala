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

/** This notification can be sent by either side to indicate that it is
  * cancelling a previously-issued request.
  *
  * The request SHOULD still be in-flight, but due to communication latency, it
  * is always possible that this notification MAY arrive after the request has
  * already finished.
  *
  * This notification indicates that the result will be unused, so any
  * associated processing SHOULD cease.
  *
  * A client MUST NOT attempt to cancel its `initialize` request.
  */
case class CancelledParams(
    /** The ID of the request to cancel.
      *
      * This MUST correspond to the ID of a request previously issued in the
      * same direction.
      */
    requestId: mcp.RequestId,
    /** An optional string describing the reason for the cancellation. This MAY
      * be logged or presented to the user.
      */
    reason: Option[String] = None
) derives ReadWriter
