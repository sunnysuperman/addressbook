package com.github.sunnysuperman.addressbook;

public class ChinaAwareAddressFinder extends AddressFinder {
	private AddressComponent china = null;
	private String name = null;

	public ChinaAwareAddressFinder(AddressFinderSource source, boolean keepName) {
		super(source, keepName);
		china = regionMap.get("CN");
		if (china == null) {
			throw new RuntimeException("CN not found");
		}
		if (keepName) {
			name = china.localizedName("zh_CN", null);
		}
	}

	public AddressComponent getChina() {
		return china;
	}

	public String getChinaChineseName() {
		return name;
	}

}
