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

/** The severity of a log message.
  *
  * These map to syslog message severities, as specified in RFC-5424:
  * https://datatracker.ietf.org/doc/html/rfc5424#section-6.2.1
  */
enum LoggingLevel:
  case alert
  case critical
  case debug
  case emergency
  case error
  case info
  case notice
  case warning
end LoggingLevel

object LoggingLevel:
  private val mapping = LoggingLevel.values.map(r => r.toString -> r).toMap
  given ReadWriter[LoggingLevel] =
    summon[ReadWriter[String]].bimap(
      s => s.toString,
      r =>
        mapping.getOrElse(
          r,
          throw new IllegalArgumentException(s"Invalid role: $r")
        )
    )
end LoggingLevel
