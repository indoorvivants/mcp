package mcp.quick

import scala.concurrent.duration.*

class RequestOptions private (opts: RequestOptions.Opts):
  import RequestOptions.*
  private def copy(f: Opts => Opts) = new RequestOptions(f(opts))

  /** Returns the current timeout setting */
  def timeout: Option[Duration] = opts.timeout

  /** Sets a timeout duration for the request
    *
    * @param dur
    *   the duration to set as timeout
    * @return
    *   a new RequestOptions with the timeout set
    */
  def withTimeout(dur: Duration): RequestOptions = copy(
    _.copy(timeout = Some(dur))
  )

  /** Removes any timeout setting from the request
    *
    * @return
    *   a new RequestOptions with no timeout set
    */
  def noTimeout: RequestOptions = copy(_.copy(timeout = None))
end RequestOptions
object RequestOptions:
  val default = apply().withTimeout(10.seconds)

  def apply(): RequestOptions = new RequestOptions(Opts())

  private[mcp] case class Opts(
      timeout: Option[Duration] = Some(10.seconds)
  )
end RequestOptions
