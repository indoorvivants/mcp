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

/** A notification from the client to the server, informing it that the list of
  * roots has changed. This notification should be sent whenever the client
  * adds, removes, or modifies any root. The server should then request an
  * updated list of roots using the ListRootsRequest.
  */
case class RootsListChangedNotification(
    method: "notifications/roots/list_changed" =
      "notifications/roots/list_changed",
    params: Option[RootsListChangedNotification.Params] = None
) derives ReadWriter

object RootsListChangedNotification:
  case class Params(
      /** This parameter name is reserved by MCP to allow clients and servers to
        * attach additional metadata to their notifications.
        */
      _meta: Option[ujson.Obj] = None
  ) derives ReadWriter
end RootsListChangedNotification
