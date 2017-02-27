package com.github.sunnysuperman.addressbook.lookup;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.utils.HttpClient;
import com.github.sunnysuperman.commons.utils.JSONUtil;

public abstract class AbstractLocationService {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractLocationService.class);

	public static class LocationServiceOptions {
		private int maxTry = 1;
		private int connectTimeoutInSeconds = 15;
		private int readTimeoutInSeconds = 15;

		public int getMaxTry() {
			return maxTry;
		}

		public LocationServiceOptions setMaxTry(int maxTry) {
			if (maxTry <= 0) {
				throw new IllegalArgumentException("maxTry");
			}
			this.maxTry = maxTry;
			return this;
		}

		public int getConnectTimeoutInSeconds() {
			return connectTimeoutInSeconds;
		}

		public LocationServiceOptions setConnectTimeoutInSeconds(int connectTimeoutInSeconds) {
			if (connectTimeoutInSeconds <= 0) {
				throw new IllegalArgumentException("connectTimeoutInSeconds");
			}
			this.connectTimeoutInSeconds = connectTimeoutInSeconds;
			return this;
		}

		public int getReadTimeoutInSeconds() {
			return readTimeoutInSeconds;
		}

		public LocationServiceOptions setReadTimeoutInSeconds(int readTimeoutInSeconds) {
			if (readTimeoutInSeconds <= 0) {
				throw new IllegalArgumentException("readTimeoutInSeconds");
			}
			this.readTimeoutInSeconds = readTimeoutInSeconds;
			return this;
		}

	}

	protected void error(Throwable t) {
		LOG.error(null, t);
	}

	protected void error(String message) {
		LOG.error(message);
	}

	protected void error(String message, Throwable t) {
		LOG.error(message, t);
	}

	private LocationServiceOptions options;

	public AbstractLocationService(LocationServiceOptions options) {
		super();
		if (options == null) {
			throw new NullPointerException("options");
		}
		this.options = options;
	}

	public LocationServiceOptions getOptions() {
		return options;
	}

	protected String httpGet(String url, Map<String, Object> params) throws Exception {
		HttpClient client = new HttpClient();
		client.setConnectTimeout(options.connectTimeoutInSeconds * 1000);
		client.setReadTimeout(options.readTimeoutInSeconds * 1000);
		int trial = 0;
		while (trial < options.maxTry) {
			trial++;
			boolean success = client.doGet(url, params, null);
			if (!success) {
				continue;
			}
			if (LOG.isInfoEnabled()) {
				LOG.info("Location api call result: " + url + ", " + client.getResponseBody());
			}
			return client.getResponseBody();
		}
		return null;
	}

	protected Map<String, Object> jsonGet(String url, Map<String, Object> params) throws Exception {
		String s = httpGet(url, params);
		return JSONUtil.parseJSONObject(s);
	}

	protected String formatAddressComponent(Object obj) {
		String s = (obj == null) ? null : obj.toString();
		if (s == null || s.length() == 0) {
			return null;
		}
		return s;
	}

}
