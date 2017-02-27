package com.github.sunnysuperman.addressbook.test;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.lookup.SinaIpLocationService;
import com.github.sunnysuperman.addressbook.lookup.AbstractLocationService.LocationServiceOptions;

public class SinaIpLocationServiceTest extends AddressBaseTest {

	private void test(String ip) throws Exception {
		AddressComponent ac = new SinaIpLocationService(new LocationServiceOptions(), getAddressFinder())
				.lookupAddress(ip);
		assertTrue(ac != null);
		System.out.println(ac);
		Address addr = ac.toAddress("zh", new String[] { "zh", "en" });
		assertTrue(addr != null);
		System.out.println(addr);
	}

	public void test1() throws Exception {
		// JP 日本
		test("182.169.150.117");
	}

	public void test_hk() throws Exception {
		// CN-810000 香港
		test("1.65.132.153");
	}

	public void test_mo() throws Exception {
		// CN-820000 澳门
		test("202.75.251.141");
	}

	public void test_tw() throws Exception {
		// CN-710000 台湾
		test("61.30.72.95");
	}

	public void test5() throws Exception {
		// US 美国
		test("17.207.229.167");
	}

	public void test6() throws Exception {
		// CN-310000-310100 上海
		test("222.73.202.211");
	}

	public void test7() throws Exception {
		// CN-120000-120100 中国-天津市-天津市
		test("125.36.96.233");
	}

	public void test8() throws Exception {
		// CN-500000-500100 中国-重庆市-重庆市
		test("123.147.244.25");
	}

	public void test9() throws Exception {
		// CN-110000-110100 中国-北京市-北京市
		test("221.219.114.126");
	}

	public void test10() throws Exception {
		// CN-530000-530900 中国-云南省-临沧市
		test("182.244.245.18");
	}

	public void test11() throws Exception {
		// CN-460000-460100 中国-海南省-海口市
		test("223.198.212.201");
	}

	public void test12() throws Exception {
		// [西班牙, Valencian Community]
		test("83.49.210.0");
	}

	public void test13() throws Exception {
		// [中国, 山东省, 聊城市]
		test("223.96.145.14");
	}

}
