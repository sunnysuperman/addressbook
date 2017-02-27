package com.github.sunnysuperman.addressbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.sunnysuperman.commons.locale.Locales;
import com.github.sunnysuperman.commons.utils.CollectionUtil;
import com.github.sunnysuperman.commons.utils.JSONUtil;

public class AddressComponentParser {

	public static List<AddressComponent> parse(String jsonString, boolean keepName, String defaultLocale) {
		List<?> items = JSONUtil.parseJSONArray(jsonString);
		return parse(items, keepName, defaultLocale);
	}

	private static List<AddressComponent> parse(List<?> items, boolean keepName, String defaultLocale) {
		if (items == null || items.isEmpty()) {
			return null;
		}
		List<AddressComponent> components = new ArrayList<AddressComponent>(items.size());
		for (Object item : items) {
			Map<?, ?> object = (Map<?, ?>) item;
			String code = object.get("code").toString();
			Map<String, String> nameAsMap = null;
			if (keepName) {
				Object name = object.get("name");
				if (name != null) {
					if (name instanceof Map) {
						Map<?, ?> rawMap = (Map<?, ?>) name;
						nameAsMap = new HashMap<String, String>(rawMap.size());
						for (Entry<?, ?> entry : rawMap.entrySet()) {
							String locale = Locales.find(entry.getKey().toString()).getKey();
							String locality = entry.getValue().toString();
							nameAsMap.put(locale, locality);
						}
					} else {
						if (defaultLocale == null) {
							throw new RuntimeException("No default locale set of " + name);
						}
						defaultLocale = Locales.find(defaultLocale).getKey();
						nameAsMap = new HashMap<String, String>(1);
						nameAsMap.put(defaultLocale, name.toString());
					}
				}
			}
			List<?> children = (List<?>) object.get("children");
			AddressComponent component = new AddressComponent(code, nameAsMap);
			if (CollectionUtil.isNotEmpty(children)) {
				component.setChildren(parse(children, keepName, defaultLocale));
			}
			components.add(component);
		}
		return components;
	}

}
