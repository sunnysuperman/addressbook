package com.github.sunnysuperman.addressbook.lookup;

import com.github.sunnysuperman.addressbook.AddressComponent;

public interface MapLocationService {
	public static enum CoordType {
		WGS84, GCJ02
	}

	public abstract AddressComponent lookupAddress(CoordType coordType, double longitude, double latitude)
			throws Exception;

}
