package mcp

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
