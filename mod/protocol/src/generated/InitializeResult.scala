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

/** After receiving an initialize request from the client, the server sends this
  * response.
  */
case class InitializeResult(
    capabilities: mcp.ServerCapabilities,
    /** The version of the Model Context Protocol that the server wants to use.
      * This may not match the version that the client requested. If the client
      * cannot support this version, it MUST disconnect.
      */
    protocolVersion: String,
    serverInfo: mcp.Implementation,
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None,
    /** Instructions describing how to use the server and its features.
      *
      * This can be used by clients to improve the LLM's understanding of
      * available tools, resources, etc. It can be thought of like a "hint" to
      * the model. For example, this information MAY be added to the system
      * prompt.
      */
    instructions: Option[String] = None
) derives ReadWriter
