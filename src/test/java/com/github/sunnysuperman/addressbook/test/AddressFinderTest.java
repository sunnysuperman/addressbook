package com.github.sunnysuperman.addressbook.test;

import java.util.Arrays;
import java.util.List;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.AddressFinder;
import com.github.sunnysuperman.commons.utils.StringUtil;

public class AddressFinderTest extends AddressBaseTest {

	public void test1() {
		getAddressFinder();
	}

	public void test2() {
		AddressFinder finder = getAddressFinder();
		assertTrue(finder.find("CN").parentOf(finder.find("CN_120101")));
		assertTrue(finder.find("CN_120000").parentOf(finder.find("CN_120101")));
		assertTrue(!finder.find("CN_310000").parentOf(finder.find("CN_120101")));
		assertTrue(!finder.find("CN_120101").parentOf(finder.find("CN_120101")));
		assertTrue(!finder.find("CN_120102").parentOf(finder.find("CN_120101")));
	}

	public void test3() {
		AddressFinder finder = getAddressFinder();
		{
			List<AddressComponent> acs = finder.find("CN_120101").cascade();
			assertTrue(acs.size() == 4);
			System.out.println(StringUtil.join(acs, ","));
		}
		{
			List<AddressComponent> acs = finder.find("CN").cascade();
			assertTrue(acs.size() == 1);
			System.out.println(StringUtil.join(acs, ","));
		}
	}

	public void test4() {
		AddressFinder finder = getAddressFinder();
		{
			List<AddressComponent> acs = finder.find("CN_120101").ancestors();
			assertTrue(acs.size() == 3);
			System.out.println(StringUtil.join(acs, ","));
		}
		{
			List<AddressComponent> acs = finder.find("CN").ancestors();
			assertTrue(acs == null);
		}
	}

	public void test5() {
		AddressFinder finder = getAddressFinder();

		{
			AddressComponent ac = finder.find("CN_120101").rootAncestor();
			assertTrue(ac != null);
			System.out.println(ac);
		}

		{
			AddressComponent ac = finder.find("CN").rootAncestor();
			assertTrue(ac != null);
			System.out.println(ac);
		}
	}

	public void test6() {
		AddressFinder finder = getAddressFinder();
		AddressComponent component = finder.find("ES");
		System.out.println("共有: " + component.children().size() + "个自治区");
		int provinces = 0;
		int towns = 0;
		for (AddressComponent community : component.children()) {
			if (community.children() != null) {
				provinces += community.children().size();
				for (AddressComponent province : community.children()) {
					if (province.children() != null) {
						towns += province.children().size();
					}
				}
			}
		}
		System.out.println("共有: " + provinces + "个省");
		System.out.println("共有: " + towns + "个市镇");
	}

	public void test_findByName() throws Exception {
		Address address = new Address(Arrays.asList("Spain", "Comunidad de Madrid", "Madrid", "Madrid"));
		AddressFinder finder = getAddressFinder();
		AddressComponent ac = finder.findByName(address.getComponents(), 1, "en", "ES");
		System.out.println("//" + ac);
		Address addr = ac.toAddress("zh", new String[] { "zh", "en" });
		System.out.println("//" + addr);
	}

}
