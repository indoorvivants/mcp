package mcp

import mcp.json.*

/** The server's response to a resources/read request from the client.
  */
case class ReadResourceResult(
    contents: Seq[ReadResourceResult.Contents],
    /** This result property is reserved by the protocol to allow clients and
      * servers to attach additional metadata to their responses.
      */
    _meta: Option[ujson.Obj] = None
) derives ReadWriter

object ReadResourceResult:
  val Contents =
    Builder[mcp.TextResourceContents]("TextResourceContents")
      .orElse[mcp.BlobResourceContents]("BlobResourceContents")

  type Contents = Contents.BuilderType
end ReadResourceResult
