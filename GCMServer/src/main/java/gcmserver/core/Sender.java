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

import static gcmserver.core.Constants.GCM_SEND_ENDPOINT;
import static gcmserver.core.Constants.JSON_CANONICAL_IDS;
import static gcmserver.core.Constants.JSON_CONTENT_AVAILABLE;
import static gcmserver.core.Constants.JSON_DELIVERY_RECEIPT_REQUESTED;
import static gcmserver.core.Constants.JSON_ERROR;
import static gcmserver.core.Constants.JSON_FAILURE;
import static gcmserver.core.Constants.JSON_MESSAGE_ID;
import static gcmserver.core.Constants.JSON_MULTICAST_ID;
import static gcmserver.core.Constants.JSON_PAYLOAD_DATA;
import static gcmserver.core.Constants.JSON_PAYLOAD_NOTIFICATION;
import static gcmserver.core.Constants.JSON_PRIORITY;
import static gcmserver.core.Constants.JSON_REGISTRATION_IDS;
import static gcmserver.core.Constants.JSON_RESULTS;
import static gcmserver.core.Constants.JSON_SUCCESS;
import static gcmserver.core.Constants.JSON_TO;
import static gcmserver.core.Constants.PARAM_COLLAPSE_KEY;
import static gcmserver.core.Constants.PARAM_DELAY_WHILE_IDLE;
import static gcmserver.core.Constants.PARAM_DRY_RUN;
import static gcmserver.core.Constants.PARAM_PAYLOAD_PREFIX;
import static gcmserver.core.Constants.PARAM_REGISTRATION_ID;
import static gcmserver.core.Constants.PARAM_RESTRICTED_PACKAGE_NAME;
import static gcmserver.core.Constants.PARAM_TIME_TO_LIVE;
import static gcmserver.core.Constants.TOKEN_CANONICAL_REG_ID;
import static gcmserver.core.Constants.TOKEN_ERROR;
import static gcmserver.core.Constants.TOKEN_MESSAGE_ID;
import gcmserver.controllers.model.MessageViewModel;
import gcmserver.model.Message;
import gcmserver.model.MulticastResult;
import gcmserver.model.Result;
import gcmserver.model.Result.Builder;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Helper class to send messages to the GCM service using an API Key.
 */
public class Sender {

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
			.getLogger(Sender.class);

	private final String key;

	// TODO add group message functions
	/**
	 * Default constructor.
	 *
	 * @param key
	 *            API key obtained through the Google API Console.
	 */
	public Sender(String key) {
		this.key = nonNull(key);
	}

	/**
	 * Sends a message to one device, retrying in case of unavailability.
	 *
	 * <p>
	 * <strong>Note: </strong> this method uses exponential back-off to retry in
	 * case of service unavailability and hence could block the calling thread
	 * for many seconds.
	 *
	 * @param message
	 *            message to be sent, including the device's registration id.
	 * @param registrationId
	 *            device where the message will be sent.
	 * @param retries
	 *            number of retries in case of service unavailability errors.
	 *
	 * @return result of the request (see its javadoc for more details).
	 *
	 * @throws IllegalArgumentException
	 *             if registrationId is {@literal null}.
	 * @throws InvalidRequestException
	 *             if GCM didn't returned a 200 or 5xx status.
	 * @throws IOException
	 *             if message could not be sent.
	 */
	/*
	 * Implements the exponential backoff retry and the result interpretation
	 */
	public Result sendHttpPlainText(Message message, String registrationId,
			int retries) throws IOException {
		int attempt = 0;
		Result result = null;
		int backoff = BACKOFF_INITIAL_DELAY;
		boolean tryAgain;
		do {
			attempt++;
			logger.info("Attempt #" + attempt + " to send message " + message
					+ " to regIds " + registrationId);
			result = sendNoRetryHttpPlainText(message, registrationId);
			tryAgain = result == null && attempt <= retries;
			if (tryAgain) {
				int sleepTime = backoff / 2 + random.nextInt(backoff);
				sleep(sleepTime);
				if (2 * backoff < MAX_BACKOFF_DELAY) {
					backoff *= 2;
				}
			}
		} while (tryAgain);
		if (result == null) {
			throw new IOException("Could not send message after " + attempt
					+ " attempts");
		}
		return result;
	}

	/**
	 * Sends a message without retrying in case of service unavailability. See
	 * {@link #sendPlainText(MessageViewModel, String, int)} for more info.
	 *
	 * @return result of the post, or {@literal null} if the GCM service was
	 *         unavailable or any network exception caused the request to fail.
	 *
	 * @throws InvalidRequestException
	 *             if GCM didn't returned a 200 or 5xx status.
	 * @throws IllegalArgumentException
	 *             if registrationId is {@literal null}.
	 */
	/*
	 * Implements the message send process using HTTP plain text
	 */
	public Result sendNoRetryHttpPlainText(Message message,
			String registrationId) throws IOException {
		StringBuilder body = newBody(PARAM_REGISTRATION_ID, registrationId);
		addOptParameter(body, PARAM_DELAY_WHILE_IDLE,
				message.isDelayWhileIdle());
		addOptParameter(body, PARAM_DRY_RUN, message.isDryRun());
		addOptParameter(body, PARAM_COLLAPSE_KEY, message.getCollapseKey());
		addOptParameter(body, PARAM_RESTRICTED_PACKAGE_NAME,
				message.getRestrictedPackageName());
		addOptParameter(body, PARAM_TIME_TO_LIVE, message.getTimeToLive());

		for (Entry<String, String> entry : message.getData().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key == null) {
				logger.warn("Ignoring payload entry thas has null: " + entry);
			} else {
				key = PARAM_PAYLOAD_PREFIX + key;
				addOptParameter(body, key, URLEncoder.encode(value, UTF8));
			}
		}
		String requestBody = body.toString();
		logger.debug("Request body: " + requestBody);
		HttpURLConnection conn;
		int status;
		try {
			conn = post(GCM_SEND_ENDPOINT, requestBody);
			status = conn.getResponseCode();
		} catch (IOException e) {
			logger.error("IOException posting to GCM", e);
			return null;
		}
		if (status / 100 == 5) {
			logger.warn("GCM service is unavailable (status " + status + ")");
			return null;
		}
		String responseBody;
		if (status != 200) {
			try {
				responseBody = getAndClose(conn.getErrorStream());
				logger.error("Plain post error response: " + responseBody);
			} catch (IOException e) {
				// ignore the exception since it will thrown an
				// InvalidRequestException
				// anyways
				responseBody = "N/A";
				logger.error("Exception reading response: ", e);
			}
			throw new InvalidRequestException(status, responseBody);
		} else {
			try {
				responseBody = getAndClose(conn.getInputStream());
			} catch (IOException e) {
				logger.error("Exception reading response: ", e);
				// return null so it can retry
				return null;
			}
		}
		String[] lines = responseBody.split("\n");
		if (lines.length == 0 || lines[0].equals("")) {
			throw new IOException("Received empty response from GCM service.");
		}
		String firstLine = lines[0];
		String[] responseParts = split(firstLine);
		String token = responseParts[0];
		String value = responseParts[1];
		if (token.equals(TOKEN_MESSAGE_ID)) {
			Builder builder = new Result.Builder().messageId(value);
			// check for canonical registration id
			if (lines.length > 1) {
				String secondLine = lines[1];
				responseParts = split(secondLine);
				token = responseParts[0];
				value = responseParts[1];
				if (token.equals(TOKEN_CANONICAL_REG_ID)) {
					builder.canonicalRegistrationId(value);
				} else {
					logger.warn("Invalid response from GCM: " + responseBody);
				}
			}
			Result result = builder.build();

			logger.info("Message created succesfully (" + result + ")");

			return result;
		} else if (token.equals(TOKEN_ERROR)) {
			return new Result.Builder().errorCode(value).build();
		} else {
			throw new IOException("Invalid response from GCM: " + responseBody);
		}
	}

	/**
	 * Sends a message to many devices, retrying in case of unavailability.
	 *
	 * <p>
	 * <strong>Note: </strong> this method uses exponential back-off to retry in
	 * case of service unavailability and hence could block the calling thread
	 * for many seconds.
	 *
	 * @param messageViewModel
	 *            message to be sent.
	 * @param regIds
	 *            registration id of the devices that will receive the message.
	 * @param retries
	 *            number of retries in case of service unavailability errors.
	 *
	 * @return combined result of all requests made.
	 *
	 * @throws IllegalArgumentException
	 *             if registrationIds is {@literal null} or empty.
	 * @throws InvalidRequestException
	 *             if GCM didn't returned a 200 or 503 status.
	 * @throws IOException
	 *             if message could not be sent.
	 */
	public MulticastResult sendHttpJson(MessageViewModel messageViewModel,
			List<String> regIds, int retries) throws IOException {
		int attempt = 0;
		MulticastResult multicastResult;
		int backoff = BACKOFF_INITIAL_DELAY;
		// Map of results by registration id, it will be updated after each
		// attempt
		// to send the messages
		Map<String, Result> results = new HashMap<String, Result>();
		List<String> unsentRegIds = new ArrayList<String>(regIds);
		boolean tryAgain;
		List<Long> multicastIds = new ArrayList<Long>();
		do {
			multicastResult = null;
			attempt++;

			logger.info("Attempt #" + attempt + " to send message "
					+ messageViewModel + " to regIds " + unsentRegIds);

			try {
				multicastResult = sendNoRetryHttpJson(messageViewModel,
						unsentRegIds);
			} catch (IOException e) {
				// no need for WARNING since exception might be already logged
				logger.error("IOException on attempt " + attempt, e);
			}
			if (multicastResult != null) {
				long multicastId = multicastResult.getMulticastId();
				logger.info("multicast_id on attempt # " + attempt + ": "
						+ multicastId);
				multicastIds.add(multicastId);
				unsentRegIds = updateStatus(unsentRegIds, results,
						multicastResult);
				tryAgain = !unsentRegIds.isEmpty() && attempt <= retries;
			} else {
				tryAgain = attempt <= retries;
			}
			if (tryAgain) {
				int sleepTime = backoff / 2 + random.nextInt(backoff);
				sleep(sleepTime);
				if (2 * backoff < MAX_BACKOFF_DELAY) {
					backoff *= 2;
				}
			}
		} while (tryAgain);
		if (multicastIds.isEmpty()) {
			// all JSON posts failed due to GCM unavailability
			throw new IOException("Could not post JSON requests to GCM after "
					+ attempt + " attempts");
		}
		// calculate summary
		int success = 0, failure = 0, canonicalIds = 0;
		for (Result result : results.values()) {
			if (result.getMessageId() != null) {
				success++;
				if (result.getCanonicalRegistrationId() != null) {
					canonicalIds++;
				}
			} else {
				failure++;
			}
		}
		// build a new object with the overall result
		long multicastId = multicastIds.remove(0);
		MulticastResult.Builder builder = new MulticastResult.Builder(success,
				failure, canonicalIds, multicastId)
				.retryMulticastIds(multicastIds);
		// add results, in the same order as the input
		for (String regId : regIds) {
			Result result = results.get(regId);
			builder.addResult(result);
		}
		return builder.build();
	}

	/**
	 * Updates the status of the messages sent to devices and the list of
	 * devices that should be retried.
	 *
	 * @param unsentRegIds
	 *            list of devices that are still pending an update.
	 * @param allResults
	 *            map of status that will be updated.
	 * @param multicastResult
	 *            result of the last multicast sent.
	 *
	 * @return updated version of devices that should be retried.
	 */
	private List<String> updateStatus(List<String> unsentRegIds,
			Map<String, Result> allResults, MulticastResult multicastResult) {
		List<Result> results = multicastResult.getResults();
		if (results.size() != unsentRegIds.size()) {
			// should never happen, unless there is a flaw in the algorithm
			throw new RuntimeException("Internal error: sizes do not match. "
					+ "currentResults: " + results + "; unsentRegIds: "
					+ unsentRegIds);
		}
		List<String> newUnsentRegIds = new ArrayList<String>();
		for (int i = 0; i < unsentRegIds.size(); i++) {
			String regId = unsentRegIds.get(i);
			Result result = results.get(i);
			allResults.put(regId, result);
			String error = result.getErrorCodeName();
			if (error != null
					&& (error.equals(Constants.ERROR_UNAVAILABLE) || error
							.equals(Constants.ERROR_INTERNAL_SERVER_ERROR))) {
				newUnsentRegIds.add(regId);
			}
		}
		return newUnsentRegIds;
	}

	/**
	 * Sends a message without retrying in case of service unavailability. See
	 * {@link #sendHttpJson(MessageViewModel, List, int)} for more info.
	 *
	 * @return multicast results if the message was sent successfully,
	 *         {@literal null} if it failed but could be retried.
	 *
	 * @throws IllegalArgumentException
	 *             if registrationIds is {@literal null} or empty.
	 * @throws InvalidRequestException
	 *             if GCM didn't returned a 200 status.
	 * @throws IOException
	 *             if there was a JSON parsing error
	 */
	public MulticastResult sendNoRetryHttpJson(
			MessageViewModel messageViewModel, List<String> registrationIds)
			throws IOException {
		if (nonNull(registrationIds).isEmpty()) {
			throw new IllegalArgumentException(
					"Field RegistrationIds/To cannot be empty");
		}
		Map<Object, Object> jsonRequest = new HashMap<Object, Object>();
		// Set the options field fields
		setJsonField(jsonRequest, PARAM_COLLAPSE_KEY,
				messageViewModel.getCollapseKey());
		setJsonField(jsonRequest, JSON_PRIORITY, messageViewModel.getPriority());
		setJsonField(jsonRequest, JSON_CONTENT_AVAILABLE,
				messageViewModel.getContentAvailable());
		setJsonField(jsonRequest, PARAM_DELAY_WHILE_IDLE,
				messageViewModel.getDelayWhileIdle());
		setJsonField(jsonRequest, PARAM_TIME_TO_LIVE,
				messageViewModel.getTimeToLive());
		setJsonField(jsonRequest, JSON_DELIVERY_RECEIPT_REQUESTED,
				messageViewModel.getDeliveryReceiptRequested());
		setJsonField(jsonRequest, PARAM_RESTRICTED_PACKAGE_NAME,
				messageViewModel.getRestrictedPackageName());
		setJsonField(jsonRequest, PARAM_DRY_RUN, messageViewModel.getDryRun());
		// Set the target or targets of the message
		if (registrationIds.size() > 1) {
			// Is a multicast message, we should use the field
			// JSON_REGISTRATION_IDS
			jsonRequest.put(JSON_REGISTRATION_IDS, registrationIds);
		} else {
			// Is a single message, we should use the field JSON_TO
			jsonRequest.put(JSON_TO, registrationIds);
		}
		Map<String, String> payload_data = messageViewModel.getData();
		if (!payload_data.isEmpty()) {
			jsonRequest.put(JSON_PAYLOAD_DATA, payload_data);
		}
		Map<String, String> payload_notification = messageViewModel
				.getNotification();
		if (!payload_notification.isEmpty()) {
			jsonRequest.put(JSON_PAYLOAD_NOTIFICATION, payload_notification);
		}
		// Generate request body and send
		String requestBody = JSONValue.toJSONString(jsonRequest);
		logger.info("JSON request: " + requestBody);
		HttpURLConnection conn;
		int status;
		try {
			conn = post(GCM_SEND_ENDPOINT, "application/json", requestBody);
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
			int success = getNumber(jsonResponse, JSON_SUCCESS).intValue();
			int failure = getNumber(jsonResponse, JSON_FAILURE).intValue();
			int canonicalIds = getNumber(jsonResponse, JSON_CANONICAL_IDS)
					.intValue();
			long multicastId = getNumber(jsonResponse, JSON_MULTICAST_ID)
					.longValue();
			MulticastResult.Builder builder = new MulticastResult.Builder(
					success, failure, canonicalIds, multicastId);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> results = (List<Map<String, Object>>) jsonResponse
					.get(JSON_RESULTS);
			if (results != null) {
				for (Map<String, Object> jsonResult : results) {
					String messageId = (String) jsonResult.get(JSON_MESSAGE_ID);
					String canonicalRegId = (String) jsonResult
							.get(TOKEN_CANONICAL_REG_ID);
					String error = (String) jsonResult.get(JSON_ERROR);
					Result result = new Result.Builder().messageId(messageId)
							.canonicalRegistrationId(canonicalRegId)
							.errorCode(error).build();
					builder.addResult(result);
				}
			}
			MulticastResult multicastResult = builder.build();
			return multicastResult;
		} catch (ParseException e) {
			throw newIoException(responseBody, e);
		} catch (CustomParserException e) {
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

	private Number getNumber(Map<?, ?> json, String field) {
		Object value = json.get(field);
		if (value == null) {
			throw new CustomParserException("Missing field: " + field);
		}
		if (!(value instanceof Number)) {
			throw new CustomParserException("Field " + field
					+ " does not contain a number: " + value);
		}
		return (Number) value;
	}

	class CustomParserException extends RuntimeException {
		CustomParserException(String message) {
			super(message);
		}
	}

	private String[] split(String line) throws IOException {
		String[] split = line.split("=", 2);
		if (split.length != 2) {
			throw new IOException("Received invalid response line from GCM: "
					+ line);
		}
		return split;
	}

	/**
	 * Make an HTTP post to a given URL.
	 *
	 * @return HTTP response.
	 */
	protected HttpURLConnection post(String url, String body)
			throws IOException {
		return post(url, "application/x-www-form-urlencoded;charset=UTF-8",
				body);
	}

	/**
	 * Makes an HTTP POST request to a given endpoint.
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
		conn.setRequestProperty("Content-Type", contentType);
		conn.setRequestProperty("Authorization", "key=" + key);
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
	 * Adds a new parameter to the HTTP POST body only if is not {@literal null}
	 * or empty {@link String}. {@link Boolean} values are formated as a
	 * character 1 or 0
	 *
	 * @param body
	 *            HTTP POST body.
	 * @param name
	 *            parameter's name.
	 * @param value
	 *            parameter's value.
	 */
	private static void addOptParameter(StringBuilder body, String name,
			Object value) {
		if (value != null) {
			String encodedValue = value.toString();
			if (value instanceof Boolean) {
				encodedValue = ((Boolean) value) ? "1" : "0";
			}
			if (value instanceof String && ((String) value).isEmpty()) {
				// Do not add and empty string for parameter
				return;
			}
			nonNull(body).append('&').append(nonNull(name)).append('=')
					.append(nonNull(encodedValue));
		}
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
