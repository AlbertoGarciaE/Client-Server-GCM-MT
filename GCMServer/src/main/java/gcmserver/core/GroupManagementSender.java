/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gcmserver.core;

import static gcmserver.core.Constants.*;
import gcmserver.model.GroupManagementMessage;
import gcmserver.model.GroupResult;
import gcmserver.model.GroupResult.Builder;
import gcmserver.model.Message;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Helper class to send messages to the GCM service using an API Key.
 */
public class GroupManagementSender {

	protected static final String UTF8 = "UTF-8";

	/**
	 * Initial delay before first retry, without jitter.
	 */
	protected static final int BACKOFF_INITIAL_DELAY = 1000;
	/**
	 * Maximum delay before a retry.
	 */
	protected static final int MAX_BACKOFF_DELAY = 1024000;

	protected final Random random = new Random();
	protected static final Logger logger = LoggerFactory
			.getLogger(GroupManagementSender.class);

	private final String key;
	private final String senderId;

	// TODO add group message functions
	/**
	 * Default constructor.
	 *
	 * @param key
	 *            API key obtained through the Google API Console.
	 */
	public GroupManagementSender(String key, String senderId) {
		this.key = nonNull(key);
		this.senderId = nonNull(senderId);
	}

	/**
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	/*
	 * Implements the message send process using HTTP JSON
	 */
	public GroupResult sendCreateGroup(GroupManagementMessage message)
			throws IOException {
		return sendOperationGroup(message, GROUP_OPERATION_CREATE);

	}

	public GroupResult sendAddToGroup(GroupManagementMessage message)
			throws IOException {
		return sendOperationGroup(message, GROUP_OPERATION_ADD);
	}

	public GroupResult sendRemoveFromGroup(GroupManagementMessage message)
			throws IOException {
		return sendOperationGroup(message, GROUP_OPERATION_REMOVE);
	}

	/**
	 * Sends a message without retrying in case of service unavailability. See
	 * {@link #sendTopic(Message, List, int)} for more info.
	 *
	 * @return result if the message was sent successfully, {@literal null} if
	 *         it failed but could be retried.
	 *
	 * @throws IllegalArgumentException
	 *             if registrationIds is {@literal null} or empty.
	 * @throws InvalidRequestException
	 *             if GCM didn't returned a 200 status.
	 * @throws IOException
	 *             if there was a JSON parsing error
	 */
	/*
	 * Implements the message send process using HTTP JSON
	 */
	private GroupResult sendOperationGroup(GroupManagementMessage message,
			String operation) throws IOException {
		if (nonNull(message.getNotificationKeyName()).isEmpty()) {
			throw new IllegalArgumentException(
					"Field notification_key_name cannot be empty");
		}
		Map<Object, Object> jsonRequest = new HashMap<Object, Object>();
		// Set the options field fields
		setJsonField(jsonRequest, GROUP_JSON_OPERATION, operation);
		setJsonField(jsonRequest, GROUP_JSON_NOTIFICATION_KEY_NAME,
				message.getNotificationKeyName());
		setJsonField(jsonRequest, GROUP_JSON_NOTIFICATION_KEY,
				message.getNotificationKey());
		if (!message.getRegistrationIds().isEmpty()) {

			JSONArray array = new JSONArray();
			ArrayList<String> aux = new ArrayList<String>(message
					.getRegistrationIds().values());
			for (int i = 0; i < aux.size(); i++) {
				array.add(aux.get(i));
			}
			// String jsonString = JSONValue.toJSONString(array);
			setJsonField(jsonRequest, GROUP_JSON_REGISTRATION_IDS, array);
		}

		// Generate request body and send
		String requestBody = JSONValue.toJSONString(jsonRequest);
		logger.info("JSON request: " + requestBody);
		HttpURLConnection conn;
		int status;
		try {
			conn = post(GROUP_OPERATION_END_POINT, "application/json",
					requestBody);
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.error("IOException posting to GCM", e);
			return null;
		}
		// Process the response
		String responseBody;
		if (status != 200) {
			try {
				responseBody = getAndClose(conn.getErrorStream());
				logger.info("JSON error response: " + responseBody);
			} catch (IOException e) {
				// ignore the exception since it will thrown an
				// InvalidRequestException
				// anyways
				responseBody = "N/A";
				logger.error("Exception reading response: ", e);
			}
			throw new InvalidRequestException(status, responseBody);
		}
		// The status is OK 200 so retrieve the body of the response
		try {
			responseBody = getAndClose(conn.getInputStream());
		} catch (IOException e) {
			logger.error("IOException reading response", e);
			return null;
		}
		logger.info("JSON response: " + responseBody);
		// Analyze the response
		JSONParser parser = new JSONParser();
		JSONObject jsonResponse;

		try {
			jsonResponse = (JSONObject) parser.parse(responseBody);
			Builder result = new GroupResult.Builder();
			result.notificationKeyName(message.getNotificationKeyName());
			String notificationKey = (String) jsonResponse
					.get(GROUP_JSON_NOTIFICATION_KEY);
			result.notificationKey(notificationKey);
			result.operation(operation);

			HashMap<String, String> regIds = new HashMap<String, String>(
					message.getRegistrationIds());
			for (Map.Entry<String, String> entry : regIds.entrySet()) {
				if (entry.getValue() != "") {
					result.addRegId(entry.getKey(), entry.getValue());
				}
			}

			// TODO: save notification key
			return result.build();
		} catch (ParseException e) {
			throw newIoException(responseBody, e);
		}

	}

	private IOException newIoException(String responseBody, Exception e) {
		// log exception, as IOException constructor that takes a message and
		// cause
		// is only available on Java 6
		String msg = "Error parsing JSON response (" + responseBody + ")";
		logger.error(msg, e);
		return new IOException(msg + ":" + e);
	}

	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore error
				logger.error("IOException closing stream", e);
			}
		}
	}

	/**
	 * Sets a JSON field, but only if the value is not {@literal null} or is and
	 * empty {@link String}.
	 */
	private void setJsonField(Map<Object, Object> json, String field,
			Object value) {
		if (value != null) {
			if (value instanceof String) {
				if (((String) value).isEmpty()) {
					// Do not put a empty string as message field
					return;
				}
			}
			json.put(field, value);
		}
	}

	/**
	 * Makes an HTTP POST request to a given endpoint including the sender ID.
	 *
	 * <p>
	 * <strong>Note: </strong> the returned connected should not be
	 * disconnected, otherwise it would kill persistent connections made using
	 * Keep-Alive.
	 *
	 * @param url
	 *            endpoint to post the request.
	 * @param contentType
	 *            type of request.
	 * @param senderId
	 *            project id in the google developer console.
	 * @param body
	 *            body of the request.
	 *
	 * @return the underlying connection.
	 *
	 * @throws IOException
	 *             propagated from underlying methods.
	 */
	protected HttpURLConnection post(String url, String contentType, String body)
			throws IOException {
		if (url == null || body == null) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if (!url.startsWith("https://")) {
			logger.warn("URL does not use https: " + url);
		}
		logger.info("Sending POST to " + url);
		logger.info("POST body: " + body);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = getConnection(url);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setFixedLengthStreamingMode(bytes.length);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "key=" + key);
		conn.setRequestProperty("project_id", senderId);
		OutputStream out = conn.getOutputStream();
		try {
			out.write(bytes);
		} finally {
			close(out);
		}
		return conn;
	}

	/**
	 * Creates a map with just one key-value pair.
	 */
	protected static final Map<String, String> newKeyValues(String key,
			String value) {
		Map<String, String> keyValues = new HashMap<String, String>(1);
		keyValues.put(nonNull(key), nonNull(value));
		return keyValues;
	}

	/**
	 * Creates a {@link StringBuilder} to be used as the body of an HTTP POST.
	 *
	 * @param name
	 *            initial parameter for the POST.
	 * @param value
	 *            initial value for that parameter.
	 * @return StringBuilder to be used an HTTP POST body.
	 */
	protected static StringBuilder newBody(String name, String value) {
		return new StringBuilder(nonNull(name)).append('=').append(
				nonNull(value));
	}

	/**
	 * Gets an {@link HttpURLConnection} given an URL.
	 */
	protected HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		return conn;
	}

	/**
	 * Convenience method to convert an InputStream to a String.
	 * <p>
	 * If the stream ends in a newline character, it will be stripped.
	 * <p>
	 * If the stream is {@literal null}, returns an empty string.
	 */
	protected static String getString(InputStream stream) throws IOException {
		if (stream == null) {
			return "";
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		StringBuilder content = new StringBuilder();
		String newLine;
		do {
			newLine = reader.readLine();
			if (newLine != null) {
				content.append(newLine).append('\n');
			}
		} while (newLine != null);
		if (content.length() > 0) {
			// strip last newline
			content.setLength(content.length() - 1);
		}
		return content.toString();
	}

	private static String getAndClose(InputStream stream) throws IOException {
		try {
			return getString(stream);
		} finally {
			if (stream != null) {
				close(stream);
			}
		}
	}

	static <T> T nonNull(T argument) {
		if (argument == null) {
			throw new IllegalArgumentException("argument cannot be null");
		}
		return argument;
	}

	void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
