package com.github.sunnysuperman.addressbook.test;

import com.github.sunnysuperman.commons.config.Config;
import com.github.sunnysuperman.commons.config.PropertiesConfig;

public class SecureConfigs {
	private static PropertiesConfig config;
	static {
		config = new PropertiesConfig(SecureConfigs.class.getResourceAsStream("resources/secure.properties"));
	}

	public static Config get() {
		return config;
	}
}
