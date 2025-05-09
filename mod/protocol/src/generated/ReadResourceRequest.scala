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

/** Sent from the client to the server, to read a specific resource URI.
  */
case class ReadResourceRequest(
    params: ReadResourceRequest.Params,
    method: "resources/read" = "resources/read"
) derives ReadWriter

object ReadResourceRequest:
  case class Params(
      /** The URI of the resource to read. The URI can use any protocol; it is
        * up to the server how to interpret it.
        */
      uri: String
  ) derives ReadWriter
end ReadResourceRequest
