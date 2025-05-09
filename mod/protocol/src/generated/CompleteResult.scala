package mcp

import mcp.json.*

/** The server's response to a completion/complete request
  */
case class CompleteResult(
    completion: CompleteResult.Completion,
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None
) derives ReadWriter

object CompleteResult:
  case class Completion(
      values: Seq[String],
      hasMore: Option[Boolean] = None,
      /** The total number of completion options available. This can exceed the
        * number of values actually sent in the response.
        */
      total: Option[Int] = None
  ) derives ReadWriter
end CompleteResult
