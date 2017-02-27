package com.github.sunnysuperman.addressbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.addressbook.AddressComponentScore.AddressComponentScoreComparator;

public class AddressFinder {
	private static final Logger LOG = LoggerFactory.getLogger(AddressFinder.class);
	protected final boolean keepName;
	protected Map<String, AddressComponent> regionMap;
	protected List<AddressComponent> regions = null;

	public static class AddressFinderSourceInput {
		private String jsonString;
		private String locale;

		public String getJsonString() {
			return jsonString;
		}

		public void setJsonString(String jsonString) {
			this.jsonString = jsonString;
		}

		public String getLocale() {
			return locale;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}

	}

	public static interface AddressFinderSource {

		AddressFinderSourceInput loadRegions();

		AddressFinderSourceInput loadRegion(String region);

	}

	public AddressFinder(AddressFinderSource source, final boolean keepName) {
		this.keepName = keepName;
		try {
			AddressFinderSourceInput regionsInput = source.loadRegions();
			List<AddressComponent> regions = AddressComponentParser.parse(regionsInput.getJsonString(), keepName,
					regionsInput.getLocale());
			for (AddressComponent region : regions) {
				AddressFinderSourceInput input = source.loadRegion(region.code());
				if (input == null) {
					continue;
				}
				List<AddressComponent> children = AddressComponentParser.parse(input.getJsonString(), keepName,
						input.getLocale());
				region.setChildren(children);
			}
			int counter = frozeComponents(regions);
			Map<String, AddressComponent> regionMap = new HashMap<String, AddressComponent>(counter);
			wrapInMap(regions, regionMap);
			this.regions = regions;
			this.regionMap = regionMap;
			LOG.info("AddressComponent count: " + counter);
		} catch (RuntimeException re) {
			throw (RuntimeException) re;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void wrapInMap(List<AddressComponent> components, Map<String, AddressComponent> map) {
		for (AddressComponent component : components) {
			String addressCode = component.addressCode();
			if (map.containsKey(addressCode)) {
				throw new RuntimeException("Duplicate address code: " + addressCode);
			}
			map.put(addressCode, component);
			if (component.children() != null) {
				wrapInMap(component.children(), map);
			}
		}
	}

	private static int frozeComponents(List<AddressComponent> components) {
		int counter = 0;
		for (AddressComponent component : components) {
			counter++;
			component.froze();
			if (component.children() != null) {
				counter += frozeComponents(component.children());
			}
		}
		return counter;
	}

	public List<AddressComponent> getRoot() {
		return regions;
	}

	public AddressComponent find(String addressCode) {
		return regionMap.get(addressCode);
	}

	public AddressComponent findByName(Address address, String locale) {
		return findByName(address.getComponents(), 0, locale, null);
	}

	public AddressComponent findByName(List<String> addrs, int offset, String locale, String parentCode) {
		List<AddressComponent> components;
		AddressComponent result = null;
		if (parentCode == null) {
			components = regions;
		} else {
			result = find(parentCode);
			if (result == null) {
				return null;
			}
			components = result.children();
			if (components == null) {
				return result;
			}
		}
		for (int i = offset; i < addrs.size(); i++) {
			if (components == null) {
				break;
			}
			if (components.size() == 1) {
				// potential risk?
				result = components.get(0);
				components = result.children();
				continue;
			}
			String name = addrs.get(i);
			if (name == null) {
				break;
			}
			AddressComponent component = findAddressComponentByName(components, name, locale, i == 0);
			if (component == null) {
				break;
			} else {
				result = component;
				components = result.children();
			}
		}
		if (result == null) {
			return null;
		}
		components = result.children();
		if (components != null && components.size() == 1) {
			result = components.get(0);
		}
		return result;
	}

	private static AddressComponent findAddressComponentByName(final List<AddressComponent> components,
			final String name, final String locale, final boolean accurate) {
		if (components == null) {
			return null;
		}
		for (AddressComponent child : components) {
			if (locale != null) {
				String localizedName = child.localizedName(locale, null);
				if (localizedName == null) {
					continue;
				}
				if (AddressNameUtil.possibleSame(localizedName, name, accurate)) {
					return child;
				}
			} else {
				for (Entry<String, String> entry : child.name().entrySet()) {
					String localizedName = entry.getValue();
					if (AddressNameUtil.possibleSame(localizedName, name, accurate)) {
						return child;
					}
				}
			}
		}
		// 计算相似度（中文不适用）
		if (locale != null && locale.startsWith("zh")) {
			return null;
		}
		List<AddressComponentScore> scores = new ArrayList<AddressComponentScore>();
		for (AddressComponent child : components) {
			double score = 0;
			if (locale != null) {
				String localizedName = child.localizedName(locale, null);
				if (localizedName == null) {
					continue;
				}
				score = AddressNameUtil.findLikelyScore(localizedName, name);
			} else {
				for (Entry<String, String> entry : child.name().entrySet()) {
					String localizedName = entry.getValue();
					score = Math.max(AddressNameUtil.findLikelyScore(localizedName, name), score);
				}
			}
			if (score < 0.1d) {
				continue;
			}
			AddressComponentScore addrScore = new AddressComponentScore(child, score);
			scores.add(addrScore);
		}
		int size = scores.size();
		if (size == 0) {
			return null;
		}
		if (size == 1) {
			return scores.get(0).getComponent();
		}
		Collections.sort(scores, AddressComponentScoreComparator.getInstance());
		return scores.get(0).getComponent();
	}

}
