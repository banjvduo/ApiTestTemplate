package com.banjvduo.apitest.common;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * 一个简单的服务
 *
 * User: stagry@gmail.com
 * Date: 18/4/9
 * Time: 14:17
 * Created by IntelliJ IDEA.
 */
public class ServiceTest {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/test", new TestHandler());
        server.start();
    }

}

class TestHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "{\"SayHi\":\"Hello World\"}";
        final Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type","application/json;charset=UTF-8");
        httpExchange.sendResponseHeaders(200,0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}