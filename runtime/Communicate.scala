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

  // def request[X <: MCPRequest & FromServer](
  //     req: PreparedRequest[X],
  //     options: RequestOptions = RequestOptions.default
  // ): F[req.Out | Error] =
  //   this.request[X](req.x, req.in, options)

  // def notification[X <: MCPNotification & FromServer](
  //     req: PreparedNotification[X]
  // ): F[Unit] =
  //   this.notification[X](req.x, req.in)
end Communicate
