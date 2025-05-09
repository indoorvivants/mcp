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

/** Sent from the server to request a list of root URIs from the client. Roots
  * allow servers to ask for specific directories or files to operate on. A
  * common example for roots is providing a set of repositories or directories a
  * server should operate on.
  *
  * This request is typically used when the server needs to understand the file
  * system structure or access specific locations that the client has permission
  * to read from.
  */
case class ListRootsRequest(
    method: "roots/list" = "roots/list",
    params: Option[ListRootsRequest.Params] = None
) derives ReadWriter

object ListRootsRequest:
  case class Params(
      _meta: Option[Params._meta] = None
  ) derives ReadWriter

  object Params:
    case class _meta(
        /** If specified, the caller is requesting out-of-band progress
          * notifications for this request (as represented by
          * notifications/progress). The value of this parameter is an opaque
          * token that will be attached to any subsequent notifications. The
          * receiver is not obligated to provide these notifications.
          */
        progressToken: Option[mcp.ProgressToken] = None
    ) derives ReadWriter
  end Params
end ListRootsRequest
