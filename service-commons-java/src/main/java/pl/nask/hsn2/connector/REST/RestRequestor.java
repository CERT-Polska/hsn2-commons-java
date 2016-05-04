/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
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

package pl.nask.hsn2.connector.REST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class RestRequestor {

	private String address;
	private HttpURLConnection connection;

	private RestRequestor(String fullAddress) {
		this.address = fullAddress;
	}

	public static RestRequestor get(String fullAddress) throws IOException {
		RestRequestor client = new RestRequestor(fullAddress);
		client.performGet();
		return client;
	}

	public static RestRequestor put(String address, String contentType, byte[] is) throws IOException {
		RestRequestor client = new RestRequestor(address);
		client.performPut(contentType, is);
		return client;
	}

	public static RestRequestor post(String fullAddress, InputStream dataInputStream) throws IOException {
		RestRequestor client = new RestRequestor(fullAddress);
		client.performPost(dataInputStream);
		return client;
	}

	public static RestRequestor head(String address) throws IOException {
		RestRequestor client = new RestRequestor(address);
		client.performHead();
		return client;
	}

	private void performGet() throws IOException {
		URL url = new URL(address);
		connection = (HttpURLConnection) url.openConnection();
	}

	private void performPut(String contentType, byte[] is) throws IOException {
		try {
			URL url = new URL(address);

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Content-Length", Integer.toString(is.length));

			IOUtils.copy(new ByteArrayInputStream(is), connection.getOutputStream());
			connection.getOutputStream().flush();
		} finally {
			if (connection != null)
				IOUtils.closeQuietly(connection.getOutputStream());
		}
	}

	private void performPost(InputStream dataInputStream) throws IOException {

		OutputStream outputStream = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			outputStream = connection.getOutputStream();

			IOUtils.copy(dataInputStream, outputStream);
			outputStream.flush();
		} finally {
			if (connection != null)
				IOUtils.closeQuietly(outputStream);
		}
	}

	/**
	 * Performs HEAD request
	 * 
	 * @throws IOException
	 */
	private void performHead() throws IOException {
		URL url = new URL(address);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("HEAD");
	}

	public String getResponseMessage() throws IOException {
		return connection.getResponseMessage();
	}

	public int getResponseCode() throws IOException {
		return connection.getResponseCode();
	}

	public InputStream getInputStream() throws IOException {
		return connection.getInputStream();
	}

	public InputStream getErrorStream() throws IOException {
		return connection.getErrorStream();
	}

	public String getHeaderField(String id) {
		return connection.getHeaderField(id);
	}

	public void close() {
		if (connection == null)
			return;

		try {
			IOUtils.closeQuietly(connection.getInputStream());
		} catch (IOException e) {
			// ignore
		}

		try {
			IOUtils.closeQuietly(connection.getOutputStream());
		} catch (IOException e) {
			// ignore
		}

		IOUtils.closeQuietly(connection.getErrorStream());

		connection.disconnect();
	}

}
