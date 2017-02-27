package com.github.sunnysuperman.addressbook.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.ChinaAwareAddressFinder;

public class SinaIpLocationService extends AbstractLocationService implements IpLocationService {
	private ChinaAwareAddressFinder finder;

	public SinaIpLocationService(LocationServiceOptions options, ChinaAwareAddressFinder finder) {
		super(options);
		this.finder = finder;
	}

	@Override
	public AddressComponent lookupAddress(String ip) throws Exception {
		if (ip == null) {
			return null;
		}
		if (ip.indexOf("127.0.0.1") == 0 || ip.indexOf("192.168") == 0 || ip.indexOf("10.") == 0) {
			return null;
		}
		Map<String, Object> map = jsonGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip,
				null);
		if (map == null) {
			error("failed to find address of ip " + ip);
			return null;
		}
		List<String> components = new ArrayList<String>(4);
		components.add(formatAddressComponent(map.get("country")));
		components.add(formatAddressComponent(map.get("province")));
		components.add(formatAddressComponent(map.get("city")));
		components.add(formatAddressComponent(map.get("district")));
		components = Address.formatComponents(components);
		if (components == null) {
			error("failed to find address of ip " + ip);
			return null;
		}
		Address address = new Address(components);
		AddressComponent ac;
		if (address.getCountry().equals(finder.getChinaChineseName())) {
			ac = ChinaAddressLookup.lookup(finder, address);
		} else {
			ac = finder.findByName(address, null);
		}
		if (ac == null) {
			error("error to find code of address " + address);
			return null;
		}
		return ac;
	}

}
