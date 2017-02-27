package com.github.sunnysuperman.addressbook.lookup;

import com.github.sunnysuperman.addressbook.AddressComponent;

public interface IpLocationService {

	AddressComponent lookupAddress(String ip) throws Exception;

}
