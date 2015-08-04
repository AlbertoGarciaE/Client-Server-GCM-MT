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
package gcmserver.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a GCM multicast message request .
 */
public final class GroupMessageResult implements Serializable {

	private final int success;
	private final int failure;
	private final List<String> failedRegistrationIds;

	public static final class Builder {

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
	 * Gets the results of each individual message, which is immutable.
	 */
	public List<String> getfailedRegistrationIds() {
		return failedRegistrationIds;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("GroupMessageResult(")
				.append("total=").append(getTotal()).append(", ")
				.append("success=").append(success).append(", ")
				.append("failure=").append(failure).append(", ");
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
