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

package mcp

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

import com.sun.net.httpserver.HttpServer

import mcp.json.*

class HttpTransportTest extends munit.FunSuite:

  private val ACCEPT = "application/json, text/event-stream"

  private def endpoints: ServerEndpoints[Id] =
    new ServerEndpoints[Id]:
      def requestHandlers = Map(
        "initialize" -> (
          (_: ServerToClient[Id]) ?=>
            (params: ujson.Value) =>
              val req = read[InitializeRequestParams](params)
              writeJs(
                InitializeResult(
                  capabilities = ServerCapabilities(),
                  protocolVersion = req.protocolVersion,
                  serverInfo = Implementation("test-server", "0.0.1")
                )
              )
        ),
        "ping" -> (
          (_: ServerToClient[Id]) ?=>
            (_: ujson.Value) => writeJs(PingResult())
        )
      )
      def notificationHandlers = Map(
        "notifications/initialized" -> (
          (_: ServerToClient[Id]) ?=> (_: ujson.Value) => ()
        )
      )

  private def withServer[A](f: (HttpClient, URI) => A): A =
    val transport = HttpTransport().port(0).host("127.0.0.1")
    val server: HttpServer = transport.start(endpoints)
    try
      val port = server.getAddress.getPort
      val uri = URI.create(s"http://127.0.0.1:$port/mcp")
      val client = HttpClient.newHttpClient()
      f(client, uri)
    finally server.stop(0)

  private def initReq(): String =
    write(
      ujson.Obj(
        "jsonrpc" -> "2.0",
        "id" -> 1,
        "method" -> "initialize",
        "params" -> writeJs(
          InitializeRequestParams(
            capabilities = ClientCapabilities(),
            clientInfo = Implementation("test-client", "0.0.1"),
            protocolVersion = mcp.internal.BuildInfo.mcpProtocolVersion
          )
        )
      )
    )

  test("initialize returns 200 with Mcp-Session-Id"):
    withServer: (client, uri) =>
      val req = HttpRequest
        .newBuilder(uri)
        .header("Accept", ACCEPT)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(initReq()))
        .build()
      val resp = client.send(req, BodyHandlers.ofString())
      assertEquals(resp.statusCode(), 200)
      assert(resp.headers().firstValue("Mcp-Session-Id").isPresent)
      val body = read[ujson.Obj](resp.body())
      assertEquals(body("id").num.toInt, 1)
      assert(body.value.contains("result"))

  test("ping without session returns 400"):
    withServer: (client, uri) =>
      val body = write(ujson.Obj("jsonrpc" -> "2.0", "id" -> 2, "method" -> "ping"))
      val req = HttpRequest
        .newBuilder(uri)
        .header("Accept", ACCEPT)
        .header("Content-Type", "application/json")
        .header("MCP-Protocol-Version", mcp.internal.BuildInfo.mcpProtocolVersion)
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()
      val resp = client.send(req, BodyHandlers.ofString())
      assertEquals(resp.statusCode(), 400)

  test("ping with unknown session returns 404"):
    withServer: (client, uri) =>
      val body = write(ujson.Obj("jsonrpc" -> "2.0", "id" -> 2, "method" -> "ping"))
      val req = HttpRequest
        .newBuilder(uri)
        .header("Accept", ACCEPT)
        .header("Content-Type", "application/json")
        .header("MCP-Protocol-Version", mcp.internal.BuildInfo.mcpProtocolVersion)
        .header("Mcp-Session-Id", "not-a-real-session")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()
      val resp = client.send(req, BodyHandlers.ofString())
      assertEquals(resp.statusCode(), 404)

  test("full initialize -> ping -> delete flow"):
    withServer: (client, uri) =>
      val initReqHttp = HttpRequest
        .newBuilder(uri)
        .header("Accept", ACCEPT)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(initReq()))
        .build()
      val initResp = client.send(initReqHttp, BodyHandlers.ofString())
      assertEquals(initResp.statusCode(), 200)
      val sid = initResp.headers().firstValue("Mcp-Session-Id").get()

      val pingBody = write(ujson.Obj("jsonrpc" -> "2.0", "id" -> 3, "method" -> "ping"))
      val pingReq = HttpRequest
        .newBuilder(uri)
        .header("Accept", ACCEPT)
        .header("Content-Type", "application/json")
        .header("MCP-Protocol-Version", mcp.internal.BuildInfo.mcpProtocolVersion)
        .header("Mcp-Session-Id", sid)
        .POST(HttpRequest.BodyPublishers.ofString(pingBody))
        .build()
      val pingResp = client.send(pingReq, BodyHandlers.ofString())
      assertEquals(pingResp.statusCode(), 200)
      val pingJson = read[ujson.Obj](pingResp.body())
      assertEquals(pingJson("id").num.toInt, 3)
      assert(pingJson.value.contains("result"))

      // notification -> 202
      val notifBody = write(
        ujson.Obj(
          "jsonrpc" -> "2.0",
          "method" -> "notifications/initialized",
          "params" -> ujson.Obj()
        )
      )
      val notifReq = HttpRequest
        .newBuilder(uri)
        .header("Accept", ACCEPT)
        .header("Content-Type", "application/json")
        .header("MCP-Protocol-Version", mcp.internal.BuildInfo.mcpProtocolVersion)
        .header("Mcp-Session-Id", sid)
        .POST(HttpRequest.BodyPublishers.ofString(notifBody))
        .build()
      val notifResp = client.send(notifReq, BodyHandlers.ofString())
      assertEquals(notifResp.statusCode(), 202)

      // DELETE session -> 204
      val delReq = HttpRequest
        .newBuilder(uri)
        .header("Mcp-Session-Id", sid)
        .DELETE()
        .build()
      val delResp = client.send(delReq, BodyHandlers.ofString())
      assertEquals(delResp.statusCode(), 204)

      // Second DELETE -> 404
      val delResp2 = client.send(delReq, BodyHandlers.ofString())
      assertEquals(delResp2.statusCode(), 404)

  test("bad Accept header returns 406"):
    withServer: (client, uri) =>
      val req = HttpRequest
        .newBuilder(uri)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(initReq()))
        .build()
      val resp = client.send(req, BodyHandlers.ofString())
      assertEquals(resp.statusCode(), 406)

  test("GET returns 405 (SSE not supported)"):
    withServer: (client, uri) =>
      val req = HttpRequest
        .newBuilder(uri)
        .header("Accept", "text/event-stream")
        .GET()
        .build()
      val resp = client.send(req, BodyHandlers.ofString())
      assertEquals(resp.statusCode(), 405)
end HttpTransportTest
