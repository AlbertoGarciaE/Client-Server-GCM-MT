package gcmserver.core;

public class Device {

	private String name;
	private String registrationId;

	/**
	 * 
	 */
	public Device() {

	}

	/**
	 * 
	 */
	public Device(Device device) {
		this.name = new String(device.getName());
		this.registrationId = new String(device.getRegistrationId());
	}

	/**
	 * @param name
	 * @param registrationId
	 */
	public Device(String name, String registrationId) {
		this.name = new String(name);
		this.registrationId = new String(registrationId);
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object objeto) {
		// If the registrationId is the same, then it should be the same device
		return this.registrationId
				.equals(((Device) objeto).getRegistrationId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Device[");
		if (name != null) {
			builder.append(" name=").append(name);
		}
		if (registrationId != null) {
			builder.append(" registrationId=").append(registrationId);
		}

		builder.append(" ]");
		return builder.toString();
	}

}
