package com.github.sunnysuperman.addressbook.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.ChinaAwareAddressFinder;

public class BaiduMapService extends AbstractLocationService implements MapLocationService {
	private static final String ADDRESS_FIND_API = "http://api.map.baidu.com/geocoder/v2/";
	private String key;
	private ChinaAwareAddressFinder finder;

	public BaiduMapService(LocationServiceOptions options, String key, ChinaAwareAddressFinder finder) {
		super(options);
		this.key = key;
		this.finder = finder;
	}

	@Override
	public AddressComponent lookupAddress(CoordType coordType, double longitude, double latitude) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ak", key);
		params.put("location", latitude + "," + longitude);
		params.put("coordtype", coordType == CoordType.WGS84 ? "wgs84ll" : "gcj02ll");
		params.put("output", "json");

		Map<String, Object> root = jsonGet(ADDRESS_FIND_API, params);
		if (root == null) {
			return null;
		}
		Number status = (Number) root.get("status");
		if (status == null) {
			return null;
		}
		if (status.intValue() != 0) {
			// return UNKNOWN_ADDRESS;
			return null;
		}
		// {"city":"上海市","district":"闸北区","province":"上海市","street":"天潼路","street_number":"619号"}
		Map<?, ?> result = (Map<?, ?>) root.get("result");
		Map<?, ?> componentResult = (Map<?, ?>) result.get("addressComponent");

		List<String> components = new ArrayList<String>(4);
		components.add(formatAddressComponent(componentResult.get("country")));
		components.add(formatAddressComponent(componentResult.get("province")));
		components.add(formatAddressComponent(componentResult.get("city")));
		components.add(formatAddressComponent(componentResult.get("district")));
		components = Address.formatComponents(components);
		if (components == null) {
			return null;
		}
		Address address = new Address(components);
		AddressComponent ac;
		if (address.getCountry().equals(finder.getChinaChineseName())) {
			ac = ChinaAddressLookup.lookup(finder, address);
		} else {
			ac = finder.findByName(address, "en");
		}
		if (ac == null) {
			error("error to find code of address " + address);
			return null;
		}
		return ac;
	}

}
