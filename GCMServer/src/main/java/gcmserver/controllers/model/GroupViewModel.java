package gcmserver.controllers.model;

import java.util.ArrayList;

public class GroupViewModel {

	private String id;
	private ArrayList<String> regIds;

	/**
	 * @return the regIds
	 */
	public ArrayList<String> getRegIds() {
		return regIds;
	}

	/**
	 * @param regIds
	 *            the regIds to set
	 */
	public void setRegIds(ArrayList<String> regIds) {
		this.regIds = regIds;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Group(");
		if (id != null) {
			builder.append(" id=").append(id);
		}
		if (regIds != null) {
			builder.append(" registrationIds=").append(
					regIds.toString());
		}

		builder.append(" )");
		return builder.toString();
	}
}
