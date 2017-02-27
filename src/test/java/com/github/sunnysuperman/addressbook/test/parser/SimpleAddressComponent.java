package com.github.sunnysuperman.addressbook.test.parser;

import java.util.List;

public class SimpleAddressComponent {
	private String code;
	private String name;
	private List<SimpleAddressComponent> children;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SimpleAddressComponent> getChildren() {
		return children;
	}

	public void setChildren(List<SimpleAddressComponent> children) {
		this.children = children;
	}

}
