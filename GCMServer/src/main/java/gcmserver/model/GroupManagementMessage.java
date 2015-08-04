package gcmserver.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class GroupManagementMessage {

	private String operation;
	private String notificationKeyName;
	private String notificationKey;

	private final Map<String, String> registrationIds;

	public static final class Builder {

		private String operation;
		private String notificationKeyName;
		private String notificationKey;

		private final Map<String, String> registrationIds;

		public Builder() {
			this.registrationIds = new LinkedHashMap<String, String>();

		}

		/**
		 * Sets the operation property.
		 */
		public Builder operation(String value) {
			operation = value;
			return this;
		}

		/**
		 * Sets the notificationKeyName property.
		 */
		public Builder notificationKeyName(String value) {
			notificationKeyName = value;
			return this;
		}

		/**
		 * Sets the notificationKey property.
		 */
		public Builder notificationKey(String value) {
			notificationKey = value;
			return this;
		}

		public Builder addRegId(String name, String regId) {
			registrationIds.put(name, regId);
			return this;
		}

		public GroupManagementMessage build() {
			return new GroupManagementMessage(this);
		}

	}

	private GroupManagementMessage(Builder builder) {
		operation = builder.operation;
		notificationKeyName = builder.notificationKeyName;
		notificationKey = builder.notificationKey;
		registrationIds = Collections.unmodifiableMap(builder.registrationIds);
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @return the notificationKeyName
	 */
	public String getNotificationKeyName() {
		return notificationKeyName;
	}

	/**
	 * @return the notificationKey
	 */
	public String getNotificationKey() {
		return notificationKey;
	}

	/**
	 * @return the registrationIds
	 */
	public Map<String, String> getRegistrationIds() {
		return registrationIds;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Message(");
		if (operation != null) {
			builder.append("operation=").append(operation).append(", ");
		}
		if (notificationKeyName != null) {
			builder.append("notificationKeyName=").append(notificationKeyName)
					.append(", ");
		}
		if (notificationKey != null) {
			builder.append("notificationKey=").append(notificationKey)
					.append(", ");
		}

		if (!registrationIds.isEmpty()) {
			builder.append("registrationIds: {");
			for (Map.Entry<String, String> entry : registrationIds.entrySet()) {
				builder.append(entry.getKey()).append("=")
						.append(entry.getValue()).append(",");
			}
			builder.delete(builder.length() - 1, builder.length());
			builder.append("}, ");
		}
		if (builder.charAt(builder.length() - 1) == ' ') {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(")");
		return builder.toString();
	}

}
