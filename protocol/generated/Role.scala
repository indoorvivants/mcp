package mcp

import upickle.default.*

/** The sender or recipient of messages and data in a conversation. */
enum Role:
  case assistant
  case user
end Role

object Role:
  private val mapping = Role.values.map(r => r.toString -> r).toMap
  given ReadWriter[Role] = 
    summon[ReadWriter[String]].bimap(
      s => s.toString,
      r => mapping.getOrElse(r, throw new IllegalArgumentException(s"Invalid role: $r"))
    )
  

