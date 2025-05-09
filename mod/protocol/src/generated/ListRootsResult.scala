package mcp

import mcp.json.*

/** The client's response to a roots/list request from the server. This result
  * contains an array of Root objects, each representing a root directory or
  * file that the server can operate on.
  */
case class ListRootsResult(
    roots: Seq[mcp.Root],
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None
) derives ReadWriter
