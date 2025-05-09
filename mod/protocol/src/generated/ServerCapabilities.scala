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

/** Capabilities that a server may support. Known capabilities are defined here,
  * in this schema, but this is not a closed set: any server can define its own,
  * additional capabilities.
  */
case class ServerCapabilities(
    /** Present if the server supports argument autocompletion suggestions.
      */
    completions: Option[ujson.Obj] = None,
    /** Experimental, non-standard capabilities that the server supports.
      */
    experimental: Option[ujson.Obj] = None,
    /** Present if the server supports sending log messages to the client.
      */
    logging: Option[ujson.Obj] = None,
    /** Present if the server offers any prompt templates.
      */
    prompts: Option[ServerCapabilities.Prompts] = None,
    /** Present if the server offers any resources to read.
      */
    resources: Option[ServerCapabilities.Resources] = None,
    /** Present if the server offers any tools to call.
      */
    tools: Option[ServerCapabilities.Tools] = None
) derives ReadWriter

object ServerCapabilities:
  case class Prompts(
      listChanged: Option[Boolean] = None
  ) derives ReadWriter
  case class Resources(
      listChanged: Option[Boolean] = None,
      subscribe: Option[Boolean] = None
  ) derives ReadWriter
  case class Tools(
      listChanged: Option[Boolean] = None
  ) derives ReadWriter
end ServerCapabilities
