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

/** The parameters for a request to elicit information from the user via a URL
  * in the client.
  */
case class ElicitRequestURLParams(
    /** The ID of the elicitation, which must be unique within the context of
      * the server. The client MUST treat this ID as an opaque value.
      */
    elicitationId: String,
    /** The message to present to the user explaining why the interaction is
      * needed.
      */
    message: String,
    /** The URL that the user should navigate to.
      */
    url: String,
    /** See [General fields:
      * `_meta`](/specification/2025-11-25/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[ElicitRequestURLParams._meta] = None,
    /** The elicitation mode.
      */
    mode: "url" = "url",
    /** If specified, the caller is requesting task-augmented execution for this
      * request. The request will return a CreateTaskResult immediately, and the
      * actual result can be retrieved later via tasks/result.
      *
      * Task augmentation is subject to capability negotiation - receivers MUST
      * declare support for task augmentation of specific request types in their
      * capabilities.
      */
    task: Option[mcp.TaskMetadata] = None
) derives ReadWriter

object ElicitRequestURLParams:
  case class _meta(
      /** If specified, the caller is requesting out-of-band progress
        * notifications for this request (as represented by
        * notifications/progress). The value of this parameter is an opaque
        * token that will be attached to any subsequent notifications. The
        * receiver is not obligated to provide these notifications.
        */
      progressToken: Option[mcp.ProgressToken] = None
  ) derives ReadWriter
end ElicitRequestURLParams
