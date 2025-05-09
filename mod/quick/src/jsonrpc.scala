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
import mcp.{*, given}

case class Request(
    id: ujson.Value,
    method: String,
    params: Option[ujson.Value] = None
) derives json.Reader

enum ErrorCode(val value: Int):
  // Standard JSON-RPC error codes
  case ParseError extends ErrorCode(-32700)
  case InvalidRequest extends ErrorCode(-32600)
  case MethodNotFound extends ErrorCode(-32601)
  case InvalidParams extends ErrorCode(-32602)
  case InternalError extends ErrorCode(-32603)
end ErrorCode

object ErrorCode:
  private val mapping = ErrorCode.values.map(ec => ec.value -> ec).toMap

  given ReadWriter[ErrorCode] =
    summon[ReadWriter[Int]].bimap(
      _.value,
      code =>
        mapping.getOrElse(
          code,
          throw new IllegalArgumentException(s"Unknown error code $code")
        )
    )
end ErrorCode

case class Error(
    code: ErrorCode,
    message: String,
    data: Option[ujson.Value] = None
) derives json.ReadWriter

case class Response(
    id: ujson.Value,
    result: Option[ujson.Value] = None,
    error: Option[Error] = None
) derives json.ReadWriter

case class Notification(
    method: String,
    params: Option[ujson.Value] = None
) derives json.ReadWriter
