package com.github.sunnysuperman.addressbook.test;

import java.io.IOException;
import java.net.URL;

import com.github.sunnysuperman.addressbook.AddressFinder.AddressFinderSource;
import com.github.sunnysuperman.addressbook.AddressFinder.AddressFinderSourceInput;
import com.github.sunnysuperman.addressbook.ChinaAwareAddressFinder;
import com.github.sunnysuperman.commons.utils.FileUtil;

public class AddressBaseTest extends BaseTest {
	private static ChinaAwareAddressFinder finder;
	static {
		AddressFinderSource source = new AddressFinderSource() {

			@Override
			public AddressFinderSourceInput loadRegions() {
				AddressFinderSourceInput input = new AddressFinderSourceInput();
				try {
					input.setJsonString(FileUtil.read(AddressBaseTest.class
							.getResourceAsStream("resources/regions.json")));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return input;
			}

			@Override
			public AddressFinderSourceInput loadRegion(String region) {
				try {
					URL url = AddressBaseTest.class.getResource("resources/region_" + region + ".json");
					if (url == null) {
						return null;
					}
					AddressFinderSourceInput input = new AddressFinderSourceInput();
					input.setJsonString(FileUtil.read(url.openStream()));
					input.setLocale(region.equals("CN") ? "zh_CN" : "en_US");
					return input;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		};
		finder = new ChinaAwareAddressFinder(source, true);
	}

	protected ChinaAwareAddressFinder getAddressFinder() {
		return finder;
	}

}
