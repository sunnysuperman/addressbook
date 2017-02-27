package com.github.sunnysuperman.addressbook;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.sunnysuperman.commons.utils.CollectionUtil;

public class AddressBase {
	protected List<String> components;

	public AddressBase(List<String> components) {
		components = formatComponents(components);
		if (components == null) {
			throw new IllegalArgumentException("Bad hierarchy");
		}
		this.components = Collections.unmodifiableList(components);
	}

	public static List<String> formatComponents(List<String> components) {
		if (CollectionUtil.isEmpty(components)) {
			return null;
		}
		boolean removeNext = false;
		for (Iterator<String> iter = components.iterator(); iter.hasNext();) {
			String v = iter.next();
			if (removeNext || v == null) {
				removeNext = true;
				iter.remove();
			}
		}
		if (components.isEmpty()) {
			return null;
		}
		return components;
	}

	public List<String> getComponents() {
		return components;
	}

	public String getCountry() {
		return components.get(0);
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
		sb.append(']');
		return sb.toString();
	}
}
