package com.github.sunnysuperman.addressbook;

import java.util.List;

public class Address extends AddressBase implements Cloneable {
	private String formatAddress;

	public Address(List<String> hierarchy) {
		super(hierarchy);
	}

	public Address clone() throws CloneNotSupportedException {
		return (Address) super.clone();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int i = 0;
		for (; i < components.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(components.get(i));
		}
		if (formatAddress != null) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(formatAddress);
		}
		sb.append(']');
		return sb.toString();
	}

	public String getFormatAddress() {
		return formatAddress;
	}

	public void setFormatAddress(String formatAddress) {
		this.formatAddress = formatAddress;
	}

}
