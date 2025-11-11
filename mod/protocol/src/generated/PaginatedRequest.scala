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

case class PaginatedRequest(
    method: String,
    params: Option[PaginatedRequest.Params] = None
) derives ReadWriter

object PaginatedRequest:
  case class Params(
      /** An opaque token representing the current pagination position. If
        * provided, the server should return results starting after this cursor.
        */
      cursor: Option[String] = None
  ) derives ReadWriter
end PaginatedRequest
