package mcp

import mcp.json.*

trait MCPRequest(val method: String):
  self =>
  type In
  type Out

  given ReadWriter[In] = compiletime.deferred
  given ReadWriter[Out] = compiletime.deferred
end MCPRequest

trait FromClient
trait FromServer

trait MCPNotification(val method: String):
  self =>
  type In

  given ReadWriter[In] = compiletime.deferred
end MCPNotification
