package mcp

import scala.concurrent.duration.*

class RequestOptions private (opts: RequestOptions.Opts):
  import RequestOptions.*
  private def copy(f: Opts => Opts) = new RequestOptions(f(opts))

  def timeout = opts.timeout

  def withTimeout(dur: Duration): RequestOptions = copy(
    _.copy(timeout = Some(dur))
  )
  def noTimeout: RequestOptions = copy(_.copy(timeout = None))
end RequestOptions

object RequestOptions:
  val default = apply().withTimeout(10.seconds)

  def apply(): RequestOptions = new RequestOptions(Opts())

  private[mcp] case class Opts(
      timeout: Option[Duration] = Some(10.seconds)
  )
end RequestOptions
