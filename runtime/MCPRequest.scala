package mcp

import mcp.json.*

trait MCPRequest(val method: String):
  self =>
  type In
  type Out

  given ReadWriter[In] = compiletime.deferred
  given ReadWriter[Out] = compiletime.deferred

  def apply(in: In): PreparedRequest[self.type] =
    PreparedRequest(self, in)
end MCPRequest

trait FromClient
trait FromServer

trait MCPNotification(val method: String):
  self =>
  type In

  given ReadWriter[In] = compiletime.deferred

  def apply(in: In): PreparedNotification[self.type] =
    PreparedNotification(self, in)
end MCPNotification
