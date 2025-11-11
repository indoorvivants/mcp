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

/** A request from the server to elicit additional information from the user via
  * the client.
  */
case class ElicitParams(
    /** The message to present to the user.
      */
    message: String,
    /** A restricted subset of JSON Schema. Only top-level properties are
      * allowed, without nesting.
      */
    requestedSchema: ElicitParams.RequestedSchema
) derives ReadWriter

object ElicitParams:
  case class RequestedSchema(
      properties: ujson.Obj,
      required: Option[Seq[String]] = None,
      `type`: "object" = "object"
  ) derives ReadWriter
end ElicitParams
