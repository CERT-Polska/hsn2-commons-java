/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.concurrent.Executors;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;
import pl.nask.hsn2.protobuff.DataStore.DataResponse.ResponseType;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DataStoreConnectionTest {
	private HttpServer server;
	private int port = 5560;
	private DataStoreConnector connector;

	@BeforeClass
	public void beforeClass() throws MalformedURLException, IOException{
		InetSocketAddress addr = new InetSocketAddress(port);
		try {
			server = HttpServer.create(addr, 0);
		} catch (IOException e) {
			throw new RuntimeException("Server error.",e);
		}
		server.createContext("/", new HttpHandler(){

			@Override
			public void handle(HttpExchange exchange) throws IOException {
				String requestMethod = exchange.getRequestMethod();
				exchange.getResponseHeaders().set("Content-Type", "text/plain");
				OutputStream responseBody = exchange.getResponseBody();
				String msg = "";
				int code = 500;
                if (requestMethod.equalsIgnoreCase("GET")) {
					msg = "GET";
					code = 200;
				}
				else if (requestMethod.equalsIgnoreCase("POST")) {
					msg = "POST";
					exchange.getResponseHeaders().set("Content-ID", "0");
					code = 201;
				}
				exchange.sendResponseHeaders(code, msg.length());
				responseBody.write(msg.getBytes());
				responseBody.close();
			}
		});

	    server.setExecutor(Executors.newCachedThreadPool());
	    server.start();

	    connector = new DataStoreConnectorImpl("http://127.0.0.1:" + port + "/");
	}

	@Test
	public void sendGet() throws IOException{
		Assert.assertEquals(connector.sendGet(0, 0).getType(),ResponseType.DATA);
	}

	@Test
	public void sendPost() throws IOException{
		Assert.assertEquals(connector.sendPost(new byte[0], 0).getType(),ResponseType.OK);

	}

	@AfterClass
	public void afterClass(){
		server.stop(0);
	}
}
