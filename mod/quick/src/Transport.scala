package mcp.quick

trait ServerEndpoints[F[_]]:
  def requestHandlers: Map[
    String,
    ServerToClient[F] ?=> (ujson.Value) => F[ujson.Value | Error]
  ]

  def notificationHandlers: Map[
    String,
    ServerToClient[F] ?=> (ujson.Value) => F[Unit]
  ]
end ServerEndpoints

trait Transport[A]:
  type F[A]

  def run(endpoints: ServerEndpoints[F]): A
end Transport
