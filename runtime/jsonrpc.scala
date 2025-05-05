package mcp

import mcp.{*, given}

import java.io.{BufferedReader, InputStreamReader}
import mcp.json.*

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
