/*
 * Copyright 2025 Anton Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.concurrent.Executors

import mcp.*
import sttp.client4.*

val backend = DefaultSyncBackend()

def weather(city: String) =
  basicRequest
    .get(uri"https://wttr.in/$city?format=4")
    .send(backend)
    .body
    .right
    .get

@main def getWeatherScalaMCP =
  MCPBuilder
    .create()
    .handle(ping)(_ => PingResult())
    .handle(initialize): req =>
      InitializeResult(
        capabilities =
          ServerCapabilities(tools = Some(ServerCapabilities.Tools())),
        protocolVersion = req.params.protocolVersion,
        serverInfo = Implementation("get-weather-scala-mcp", "0.0.1")
      )
    .handle(notifications.initialized): _ =>
      // Send notifications to the client
      communicate.notification(notifications.tools.list_changed)(
        ToolListChangedNotification()
      )
      // Send requests and receive responses from the client
      System.err.println(
        communicate.request(sampling.createMessage)(
          CreateMessageRequest(
            params = CreateMessageRequest.Params(
              maxTokens = 500,
              messages = Seq(
                SamplingMessage(
                  TextContent("what is the meaning of life?"),
                  role = Role.user
                )
              )
            )
          )
        )
      )
    .handle(tools.list): _ =>
      ListToolsResult(
        Seq(
          Tool(
            name = "get_weather",
            description =
              Some("Get weather in concise form in a given location"),
            inputSchema = Tool.InputSchema(
              Some(
                ujson.Obj(
                  "location" -> ujson.Obj("type" -> ujson.Str("string"))
                )
              ),
              required = Some(Seq("location"))
            )
          )
        )
      )
    .handle(tools.call): req =>
      val location = req.params.arguments.get.obj("location").str
      CallToolResult(content =
        Seq(
          TextContent(text = weather(location), `type` = "text")
        )
      )
    .run(
      SyncTransport.default.verbose
    )
end getWeatherScalaMCP
