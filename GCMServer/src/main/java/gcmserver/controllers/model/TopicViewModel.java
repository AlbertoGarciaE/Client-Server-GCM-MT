package gcmserver.controllers.model;

public class TopicViewModel {

	private String name;
	private String url;

	/**
	 * 
	 */
	public TopicViewModel() {

	}

	/**
	 * 
	 */
	public TopicViewModel(TopicViewModel topicViewModel) {
		this.name = new String(topicViewModel.getName());
		this.url = new String(topicViewModel.getUrl());
	}

	/**
	 * @param name
	 * @param url
	 */
	public TopicViewModel(String name, String url) {
		this.name = new String(name);
		this.url = new String(url);
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the registrationId to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Topic[");
		if (name != null) {
			builder.append(" name=").append(name);
		}
		if (url != null) {
			builder.append(" url=").append(url);
		}

		builder.append(" ]");
		return builder.toString();
	}

}
