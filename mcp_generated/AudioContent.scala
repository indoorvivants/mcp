package mcp

import upickle.default.*
import upicklex.namedTuples.Macros.Implicits.given

/** Audio provided to or from an LLM. */
case class AudioContent(
  /** The base64-encoded audio data. */
  data: String,
  /** The MIME type of the audio. Different providers may support different audio types. */
  mimeType: String,
  `type`: "audio",
  /** Optional annotations for the client. */
  annotations: Option[mcp.Annotations] = None,
) derives ReadWriter
