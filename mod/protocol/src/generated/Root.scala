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

/** Represents a root directory or file that the server can operate on.
  */
case class Root(
    /** The URI identifying the root. This *must* start with file:// for now.
      * This restriction may be relaxed in future versions of the protocol to
      * allow other URI schemes.
      */
    uri: String,
    /** An optional name for the root. This can be used to provide a
      * human-readable identifier for the root, which may be useful for display
      * purposes or for referencing the root in other parts of the application.
      */
    name: Option[String] = None
) derives ReadWriter
