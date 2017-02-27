package com.github.sunnysuperman.addressbook.lookup;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.ChinaAwareAddressFinder;

public class ChinaAddressLookup {

	public static AddressComponent lookup(ChinaAwareAddressFinder finder, Address address) {
		if (address.getComponents().size() > 1) {
			String province = address.getComponents().get(1);
			if (province.indexOf("香港") >= 0) {
				return finder.findByName(address.getComponents(), 2, "zh", finder.find("HK").addressCode());
			} else if (province.indexOf("澳门") >= 0) {
				return finder.findByName(address.getComponents(), 2, "zh", finder.find("MO").addressCode());
			} else if (province.indexOf("台湾") >= 0) {
				return finder.findByName(address.getComponents(), 2, "zh", finder.find("TW").addressCode());
			}
		}
		return finder.findByName(address.getComponents(), 1, "zh", finder.getChina().addressCode());
	}
}
