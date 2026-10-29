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

/** Parameters for a `tools/call` request.
  */
case class CallToolRequestParams(
    /** The name of the tool.
      */
    name: String,
    /** See [General fields:
      * `_meta`](/specification/2025-11-25/basic/index#meta) for notes on
      * `_meta` usage.
      */
    _meta: Option[CallToolRequestParams._meta] = None,
    /** Arguments to use for the tool call.
      */
    arguments: Option[ujson.Obj] = None,
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

object CallToolRequestParams:
  case class _meta(
      /** If specified, the caller is requesting out-of-band progress
        * notifications for this request (as represented by
        * notifications/progress). The value of this parameter is an opaque
        * token that will be attached to any subsequent notifications. The
        * receiver is not obligated to provide these notifications.
        */
      progressToken: Option[mcp.ProgressToken] = None
  ) derives ReadWriter
end CallToolRequestParams
