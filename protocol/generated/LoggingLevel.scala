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
