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

/** Capabilities a client may support. Known capabilities are defined here, in
  * this schema, but this is not a closed set: any client can define its own,
  * additional capabilities.
  */
case class ClientCapabilities(
    /** Experimental, non-standard capabilities that the client supports.
      */
    experimental: Option[ujson.Obj] = None,
    /** Present if the client supports listing roots.
      */
    roots: Option[ClientCapabilities.Roots] = None,
    /** Present if the client supports sampling from an LLM.
      */
    sampling: Option[ujson.Obj] = None
) derives ReadWriter

object ClientCapabilities:
  case class Roots(
      listChanged: Option[Boolean] = None
  ) derives ReadWriter
