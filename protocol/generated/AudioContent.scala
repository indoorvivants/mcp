package mcp

import upickle.default.*

/** Audio provided to or from an LLM. */
@upickle.implicits.serializeDefaults(true)
case class AudioContent(
  /** The base64-encoded audio data. */
  data: String,
  /** The MIME type of the audio. Different providers may support different audio types. */
  mimeType: String,
  `type`: "audio" = "audio",
  /** Optional annotations for the client. */
  annotations: Option[mcp.Annotations] = None,
) derives ReadWriter
