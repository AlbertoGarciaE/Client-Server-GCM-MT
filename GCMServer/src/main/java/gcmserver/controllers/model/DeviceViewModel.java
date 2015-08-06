package gcmserver.controllers.model;

public class DeviceViewModel {

	private String name;
	private String registrationId;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the registrationId
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * @param registrationId
	 *            the registrationId to set
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	@Override
	public boolean equals(Object objeto) {
		// If the registrationId is the same, then it should be the same device
		return this.registrationId.equals(((DeviceViewModel) objeto)
				.getRegistrationId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Device(");
		if (name != null) {
			builder.append("name=").append(name).append(", ");
		}
		if (registrationId != null) {
			builder.append("registrationId=").append(registrationId)
					.append(", ");
		}
		if (builder.charAt(builder.length() - 1) == ' ') {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(")");
		return builder.toString();
	}

}
