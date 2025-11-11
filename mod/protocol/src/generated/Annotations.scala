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

/** Optional annotations for the client. The client can use annotations to
  * inform how objects are used or displayed
  */
case class Annotations(
    /** Describes who the intended customer of this object or data is.
      *
      * It can include multiple entries to indicate content useful for multiple
      * audiences (e.g., `["user", "assistant"]`).
      */
    audience: Option[Seq[mcp.Role]],
    /** The moment the resource was last modified, as an ISO 8601 formatted
      * string.
      *
      * Should be an ISO 8601 formatted string (e.g., "2025-01-12T15:00:58Z").
      *
      * Examples: last activity timestamp in an open file, timestamp when the
      * resource was attached, etc.
      */
    lastModified: Option[String] = None,
    /** Describes how important this data is for operating the server.
      *
      * A value of 1 means "most important," and indicates that the data is
      * effectively required, while 0 means "least important," and indicates
      * that the data is entirely optional.
      */
    priority: Option[Double] = None
) derives ReadWriter
