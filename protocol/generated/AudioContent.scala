package mcp

import mcp.json.*

/**
 * Audio provided to or from an LLM.
 */
case class AudioContent(
   /**
    * The base64-encoded audio data.
    */
   data: String,
   /**
    * The MIME type of the audio. Different providers may support different audio types.
    */
   mimeType: String,
   /**
    * Optional annotations for the client.
    */
   annotations: Option[mcp.Annotations] = None,
   `type`: "audio" = "audio",
) derives ReadWriter
