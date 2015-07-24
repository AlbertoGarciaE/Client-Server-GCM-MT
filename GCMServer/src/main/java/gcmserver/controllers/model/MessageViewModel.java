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
package gcmserver.controllers.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GCM message.
 *
 */
public final class MessageViewModel implements Serializable {
	private String target;
	private List<String> listTargets;
	// optional parameters
	private String collapseKey;
	private Integer priority;
	private Boolean contentAvailable;
	private Boolean delayWhileIdle;
	private Integer timeToLive;
	private Boolean deliveryReceiptRequested;
	private String restrictedPackageName;
	private Boolean dryRun;

	private final Map<String, String> data;
	private final Map<String, String> notification;

	public MessageViewModel() {
		this.data = new LinkedHashMap<String, String>();
		this.notification = new LinkedHashMap<String, String>();
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the collapseKey
	 */
	public String getCollapseKey() {
		return collapseKey;
	}

	/**
	 * @param collapseKey
	 *            the collapseKey to set
	 */
	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the contentAvailable
	 */
	public Boolean getContentAvailable() {
		return contentAvailable;
	}

	/**
	 * @param contentAvailable
	 *            the contentAvailable to set
	 */
	public void setContentAvailable(Boolean contentAvailable) {
		this.contentAvailable = contentAvailable;
	}

	/**
	 * @return the delayWhileIdle
	 */
	public Boolean getDelayWhileIdle() {
		return delayWhileIdle;
	}

	/**
	 * @param delayWhileIdle
	 *            the delayWhileIdle to set
	 */
	public void setDelayWhileIdle(Boolean delayWhileIdle) {
		this.delayWhileIdle = delayWhileIdle;
	}

	/**
	 * @return the timeToLive
	 */
	public Integer getTimeToLive() {
		return timeToLive;
	}

	/**
	 * @param timeToLive
	 *            the timeToLive to set
	 */
	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	/**
	 * @return the deliveryReceiptRequested
	 */
	public Boolean getDeliveryReceiptRequested() {
		return deliveryReceiptRequested;
	}

	/**
	 * @param deliveryReceiptRequested
	 *            the deliveryReceiptRequested to set
	 */
	public void setDeliveryReceiptRequested(Boolean deliveryReceiptRquested) {
		this.deliveryReceiptRequested = deliveryReceiptRquested;
	}

	/**
	 * @return the restrictedPackageName
	 */
	public String getRestrictedPackageName() {
		return restrictedPackageName;
	}

	/**
	 * @param restrictedPackageName
	 *            the restrictedPackageName to set
	 */
	public void setRestrictedPackageName(String restrictedPackageName) {
		this.restrictedPackageName = restrictedPackageName;
	}

	/**
	 * @return the dryRun
	 */
	public Boolean getDryRun() {
		return dryRun;
	}

	/**
	 * @param dryRun
	 *            the dryRun to set
	 */
	public void setDryRun(Boolean dryRun) {
		this.dryRun = dryRun;
	}

	/**
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @return the notification
	 */
	public Map<String, String> getNotification() {
		return notification;
	}
//TODO add target a el metodo to string
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Message(");
		if (collapseKey != null) {
			builder.append("collapseKey=").append(collapseKey).append(", ");
		}
		if (priority != null) {
			builder.append("priority=").append(priority).append(", ");
		}
		if (contentAvailable != null) {
			builder.append("contentAvailable=").append(contentAvailable)
					.append(", ");
		}
		if (timeToLive != null) {
			builder.append("timeToLive=").append(timeToLive).append(", ");
		}
		if (deliveryReceiptRequested != null) {
			builder.append("deliveryReceiptRquested=")
					.append(deliveryReceiptRequested).append(", ");
		}
		if (delayWhileIdle != null) {
			builder.append("delayWhileIdle=").append(delayWhileIdle)
					.append(", ");
		}
		if (restrictedPackageName != null) {
			builder.append("restrictedPackageName=")
					.append(restrictedPackageName).append(", ");
		}
		if (dryRun != null) {
			builder.append("dryRun=").append(dryRun).append(", ");
		}
		if (!data.isEmpty()) {
			builder.append("data: {");
			for (Map.Entry<String, String> entry : data.entrySet()) {
				builder.append(entry.getKey()).append("=")
						.append(entry.getValue()).append(",");
			}
			builder.delete(builder.length() - 1, builder.length());
			builder.append("}, ");
		}
		if (!notification.isEmpty()) {
			builder.append("notification: {");
			for (Map.Entry<String, String> entry : notification.entrySet()) {
				builder.append(entry.getKey()).append("=")
						.append(entry.getValue()).append(",");
			}
			builder.delete(builder.length() - 1, builder.length());
			builder.append("}");
		}
		if (builder.charAt(builder.length() - 1) == ' ') {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(")");
		return builder.toString();
	}

	/**
	 * @return the listTargets
	 */
	public List<String> getListTargets() {
		return listTargets;
	}

	/**
	 * @param listTargets the listTargets to set
	 */
	public void setListTargets(List<String> listTargets) {
		this.listTargets = listTargets;
	}

}
