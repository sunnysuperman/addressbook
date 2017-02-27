package com.github.sunnysuperman.addressbook.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.AddressFinder;
import com.github.sunnysuperman.addressbook.GpsUtil;
import com.github.sunnysuperman.addressbook.GpsUtil.Gps;
import com.github.sunnysuperman.commons.utils.CollectionUtil;
import com.github.sunnysuperman.commons.utils.FormatUtil;

public class GoogleMapService extends AbstractLocationService implements MapLocationService {
	private String key;
	private AddressFinder finder;

	public GoogleMapService(LocationServiceOptions options, String key, AddressFinder finder) {
		super(options);
		this.key = key;
		this.finder = finder;
	}

	private AddressComponent parseAddressComponent(Map<?, ?> root) {
		String formatAddress = FormatUtil.parseString(root.get("formatted_address"));
		if (formatAddress == null) {
			return null;
		}
		List<String> hierarchy = new ArrayList<String>(4);

		int addressPointer = -1;
		String countryCode = null;
		List<?> components = (List<?>) root.get("address_components");
		boolean shouldBreak = false;
		AddressComponent country = null;
		for (int i = components.size() - 1; i >= 0; i--) {
			Map<?, ?> component = (Map<?, ?>) components.get(i);
			List<?> types = (List<?>) component.get("types");
			String type = CollectionUtil.isEmpty(types) ? null : types.get(0).toString();
			if (type == null) {
				LOG.warn("address type is null");
				continue;
			}
			if (isDetailAddress(type)) {
				break;
			}
			String name = FormatUtil.parseString(component.get("long_name"));
			if (name == null) {
				LOG.warn("long_name is null");
			}
			if (addressPointer < 0) {
				if (type.equals("country")) {
					addressPointer = 0;
				}
				if (addressPointer < 0) {
					continue;
				}
			}
			switch (addressPointer) {
			case 0:
				countryCode = FormatUtil.parseString(component.get("short_name"));
				country = finder.find(countryCode);
				hierarchy.add(name);
				break;
			case 1:
				hierarchy.add(name);
				break;
			case 2:
				hierarchy.add(name);
				break;
			case 3:
				hierarchy.add(name);
				shouldBreak = true;
				break;
			default:
				throw new RuntimeException("Bad address setter");
			}
			if (shouldBreak) {
				break;
			}
			addressPointer++;
		}
		if (hierarchy.isEmpty()) {
			return null;
		}
		Address address = new Address(hierarchy);
		AddressComponent ac;
		if (country != null) {
			ac = finder.findByName(address.getComponents(), 1, "en", country.addressCode());
		} else {
			ac = finder.findByName(address, "en");
		}
		if (ac == null) {
			error("error to find code of address " + address);
			return null;
		}
		return ac;
	}

	// private boolean isCityOrDistrict(String type) {
	// return type.equals("locality") || type.equals("country") ||
	// type.startsWith("administrative_area_level")
	// || type.startsWith("sublocality_level");
	// }

	private boolean isDetailAddress(String type) {
		return type.equals("street_address") || type.equals("route") || type.startsWith("intersection")
				|| type.equals("neighborhood") || type.equals("premise") || type.equals("subpremise")
				|| type.equals("airport") || type.equals("park");
	}

	@Override
	public AddressComponent lookupAddress(CoordType coordType, double longitude, double latitude) throws Exception {
		if (coordType == CoordType.GCJ02 && !GpsUtil.outOfChina(longitude, latitude)) {
			Gps gps = GpsUtil.gcjToGps84(longitude, latitude);
			longitude = gps.getLng();
			latitude = gps.getLat();
		}
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("language", "en");
		params.put("key", key);
		params.put("latlng", latitude + "," + longitude);
		// params.put("result_type", "street_address");
		// params.put("result_type", "locality");
		Map<String, Object> result = jsonGet("https://maps.googleapis.com/maps/api/geocode/json", params);
		if (result == null) {
			return null;
		}
		List<?> results = (List<?>) result.get("results");
		if (results == null || results.isEmpty()) {
			return null;
		}
		for (Object componentObject : results) {
			Map<?, ?> component = (Map<?, ?>) componentObject;
			List<?> types = (List<?>) component.get("types");
			String type = CollectionUtil.isEmpty(types) ? null : types.get(0).toString();
			if (type == null) {
				continue;
			}
			// if (isCityOrDistrict(type)) {
			return parseAddressComponent(component);
			// }
		}
		return null;
	}

}
