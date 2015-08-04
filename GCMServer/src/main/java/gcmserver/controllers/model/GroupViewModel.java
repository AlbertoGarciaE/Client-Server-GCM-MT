package gcmserver.controllers.model;

import java.util.ArrayList;
import java.util.Map;

public class GroupViewModel {

	private String notificationKeyName;
	private String notificationKey;
	private ArrayList<String> regIds;

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
	 * @return the regIds
	 */
	public ArrayList<String> getRegIds() {
		return regIds;
	}

	/**
	 * @param notificationKeyName
	 *            the notificationKeyName to set
	 */
	public void setNotificationKeyName(String notificationKeyName) {
		this.notificationKeyName = notificationKeyName;
	}

	/**
	 * @param notificationKey
	 *            the notificationKey to set
	 */
	public void setNotificationKey(String notificationKey) {
		this.notificationKey = notificationKey;
	}

	/**
	 * @param regIds
	 *            the regIds to set
	 */
	public void setRegIds(ArrayList<String> regIds) {
		this.regIds = regIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Group(");
		if (notificationKeyName != null && !notificationKeyName.isEmpty()) {
			builder.append("notificationKeyName=").append(notificationKeyName)
					.append(", ");
		}
		if (notificationKey != null && !notificationKey.isEmpty()) {
			builder.append("notificationKey=").append(notificationKey)
					.append(", ");
		}
		if (regIds != null && !regIds.isEmpty()) {
			builder.append("registrationIds=").append(regIds.toString())
					.append(", ");
		}

		if (builder.charAt(builder.length() - 1) == ' ') {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(")");
		return builder.toString();
	}
}
