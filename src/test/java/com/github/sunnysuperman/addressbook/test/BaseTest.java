package com.github.sunnysuperman.addressbook.test;

import junit.framework.TestCase;

public class BaseTest extends TestCase {

	static {
		System.setProperty("logback.configurationFile", BaseTest.class.getResource("resources/logback.xml").toString());
	}

}
