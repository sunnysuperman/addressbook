package com.github.sunnysuperman.addressbook;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.sunnysuperman.commons.locale.LocaleUtil;
import com.github.sunnysuperman.commons.utils.CollectionUtil;

public class AddressComponent implements java.io.Serializable {
	private String code;
	private Map<String, String> name;
	private List<AddressComponent> children;
	private AddressComponent parent;
	private String addressCode;
	private boolean frozen;

	public AddressComponent(String code, Map<String, String> name) {
		super();
		this.code = code;
		this.name = name;
	}

	private void validateFrozen() {
		if (frozen) {
			throw new RuntimeException("Already frozen");
		}
	}

	public String makeAddressCode() {
		if (parent == null) {
			addressCode = code;
		} else {
			addressCode = rootAncestor().code + '_' + code;
		}
		return addressCode;
	}

	public void froze() {
		validateFrozen();
		frozen = true;
		makeAddressCode();
		if (name != null) {
			name = Collections.unmodifiableMap(name);
		}
	}

	public void setChildren(List<AddressComponent> children) {
		validateFrozen();
		if (CollectionUtil.isEmpty(children)) {
			throw new RuntimeException("Children should not be empty");
		}
		for (AddressComponent child : children) {
			child.parent = this;
		}
		this.children = Collections.unmodifiableList(children);
	}

//	public void setName(Map<String, String> name) {
//		validateFrozen();
//		this.name = name;
//	}

	public String code() {
		return code;
	}

	public String addressCode() {
		return addressCode;
	}

	public Map<String, String> name() {
		return name;
	}

	public List<AddressComponent> children() {
		return children;
	}

	public AddressComponent parent() {
		return parent;
	}

	public List<AddressComponent> ancestors() {
		AddressComponent p = parent;
		if (p == null) {
			return null;
		}
		LinkedList<AddressComponent> ancestors = new LinkedList<AddressComponent>();
		while (p != null) {
			ancestors.addFirst(p);
			p = p.parent;
		}
		return ancestors;
	}

	public AddressComponent rootAncestor() {
		if (parent == null) {
			return this;
		}
		AddressComponent p = parent;
		while (p.parent != null) {
			p = p.parent;
		}
		return p;
	}

	public List<AddressComponent> cascade() {
		LinkedList<AddressComponent> cascade = new LinkedList<AddressComponent>();
		cascade.addFirst(this);
		AddressComponent p = parent;
		while (p != null) {
			cascade.addFirst(p);
			p = p.parent;
		}
		return cascade;
	}

	public boolean parentOf(AddressComponent another) {
		AddressComponent p = another.parent;
		while (p != null) {
			if (p == this) {
				return true;
			}
			p = p.parent;
		}
		return false;
	}

	public String localizedName(String locale, String[] preferredLocales) {
		String supportedLocale = LocaleUtil.findSupportLocale(locale, name.keySet());
		if (supportedLocale != null) {
			return name.get(supportedLocale);
		}
		if (preferredLocales != null) {
			for (String prefLocale : preferredLocales) {
				String v = name.get(prefLocale);
				if (v != null) {
					return v;
				}
			}
			return name.values().iterator().next();
		}
		return null;
	}

	public List<String> cascadeLocalizedNames(String locale, String[] preferredLocales) {
		LinkedList<String> names = new LinkedList<String>();
		String name = localizedName(locale, preferredLocales);
		if (name == null) {
			return null;
		}
		names.addFirst(name);
		AddressComponent p = this.parent;
		while (p != null) {
			name = p.localizedName(locale, preferredLocales);
			if (name == null) {
				return null;
			}
			names.addFirst(name);
			p = p.parent;
		}
		return names;
	}

	public Address toAddress(String locale, String[] preferredLocales) {
		if (locale == null) {
			throw new NullPointerException("locale");
		}
		List<String> hierarchy = cascadeLocalizedNames(locale, preferredLocales);
		if (hierarchy == null) {
			return null;
		}
		return new Address(hierarchy);
	}

	public String toString() {
		Map<String, Object> doc = new HashMap<String, Object>(2);
		doc.put("code", code);
		doc.put("name", name);
		return doc.toString();
	}

}
