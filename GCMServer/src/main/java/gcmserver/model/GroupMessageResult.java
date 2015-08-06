package gcmserver.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a GCM group message request .
 */
public final class GroupMessageResult implements Serializable {

	private final int success;
	private final int failure;
	private final List<String> failedRegistrationIds;

	public static final class Builder {

		// optional parameter
		private final List<String> failedRegistrationIds;

		// required parameters
		private final int success;
		private final int failure;

		public Builder(int success, int failure) {
			this.success = success;
			this.failure = failure;
			failedRegistrationIds = new ArrayList<String>();
		}

		public Builder addfailedRegistrationIds(String regId) {
			failedRegistrationIds.add(regId);
			return this;
		}

		public GroupMessageResult build() {
			return new GroupMessageResult(this);
		}
	}

	private GroupMessageResult(Builder builder) {
		success = builder.success;
		failure = builder.failure;
		failedRegistrationIds = Collections
				.unmodifiableList(builder.failedRegistrationIds);
	}

	/**
	 * Gets the number of successful messages.
	 */
	public int getSuccess() {
		return success;
	}

	/**
	 * Gets the total number of messages sent, regardless of the status.
	 */
	public int getTotal() {
		return success + failure;
	}

	/**
	 * Gets the number of failed messages.
	 */
	public int getFailure() {
		return failure;
	}

	/**
	 * Gets the list of registration ids for which the message delivered was failed.
	 */
	public List<String> getfailedRegistrationIds() {
		return failedRegistrationIds;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("GroupMessageResult(");
		builder.append("total=").append(getTotal()).append(", ");
		builder.append("success=").append(success).append(", ");
		builder.append("failure=").append(failure).append(", ");
		if (failedRegistrationIds != null && !failedRegistrationIds.isEmpty()) {
			builder.append(
					"failedRegistrationIds: "
							+ failedRegistrationIds.toString()).append(", ");
		}
		if (builder.charAt(builder.length() - 1) == ' ') {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(")");
		return builder.toString();
	}
}
