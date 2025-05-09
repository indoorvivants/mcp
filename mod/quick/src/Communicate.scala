package mcp.quick

import mcp.*, json.*

inline def communicate[F[_], Origin <: FromServer | FromClient](using
    comm: Communicate[F, Origin]
) = comm

trait Communicate[F[_], Origin <: (FromServer | FromClient)]:
  def notification[X <: MCPNotification & Origin](notif: X)(
      in: notif.In
  ): F[Unit]

  def request[X <: MCPRequest & Origin](
      req: X,
      options: RequestOptions = RequestOptions.default
  )(
      in: req.In
  ): F[req.Out | Error]
end Communicate

trait ServerToClient[F[_]] extends Communicate[F, FromServer]
trait ClientToServer[F[_]] extends Communicate[F, FromClient]
