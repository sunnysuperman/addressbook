package com.github.sunnysuperman.addressbook.test;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.GpsUtil;
import com.github.sunnysuperman.addressbook.GpsUtil.Gps;
import com.github.sunnysuperman.addressbook.lookup.BaiduMapService;
import com.github.sunnysuperman.addressbook.lookup.AbstractLocationService.LocationServiceOptions;
import com.github.sunnysuperman.addressbook.lookup.MapLocationService.CoordType;

public class BaiduMapLocationServiceTest extends AddressBaseTest {

	private void test(CoordType coordType, double lng, double lat) throws Exception {
		BaiduMapService service = new BaiduMapService(new LocationServiceOptions(),
				SecureConfigs.get().getString("location.baidu.key"), getAddressFinder());
		AddressComponent ac = service.lookupAddress(coordType, lng, lat);
		assertTrue(ac != null);
		System.out.println("//" + ac);
		Address addr = ac.toAddress("zh", new String[] { "zh", "en" });
		assertTrue(addr != null);
		System.out.println("//" + addr);
	}

	private void test(double lng, double lat) throws Exception {
		test(CoordType.WGS84, lng, lat);
	}

	public void test_WGS84() throws Exception {
		test(CoordType.WGS84, 121.59870244, 31.20291149);
	}

	public void test_WGS84_002() throws Exception {
		Gps gps = GpsUtil.gcjToGps84(121.60290123, 31.20069579);
		System.out.println(gps);
		// 121.598703,31.202912
		test(CoordType.WGS84, gps.getLng(), gps.getLat());
	}

	public void test_GCJ02() throws Exception {
		test(CoordType.GCJ02, 121.60290123, 31.20069579);
	}

	public void test_hk() throws Exception {
		test(114.204744, 22.272317);
	}

	public void test_mo() throws Exception {
		test(113.545902, 22.202993);
	}

	public void test_tw() throws Exception {
		test(120.445004, 23.468719);
	}

	public void test1() throws Exception {
		// CN-310000-310100-310115
		// 中国-上海市-上海市-浦东新区
		test(121.59870244, 31.20291149);
	}

	public void test2() throws Exception {
		// CN-460000-460100-460108
		// 中国-海南省-海口市-美兰区
		test(110.319484, 20.068603);
	}

	public void test3() throws Exception {
		// CN-440000-441900
		// 中国-广东省-东莞市
		test(114.00775, 22.982254);
	}

	public void test4() throws Exception {
		// US
		// 美国
		test(-122.122399, 37.391857);
	}

	public void test5() throws Exception {
		// AU
		// 澳大利亚
		test(144.964175, -37.822819);
	}

	public void test6() throws Exception {
		test(-4.791813, 37.910323);
	}

	public void test7() throws Exception {
		// Plaza Era de San Antón, 1-5
		// 14850 Baena, Córdoba, 西班牙
		test(-4.326739, 37.619010);
	}

	public void test8() throws Exception {
		// Avinguda Font Menor, 21
		// 46750 Simat de la Valldigna, Valencia, 西班牙
		test(-0.312585, 39.044477);
	}

	public void test9() throws Exception {
		// Calle Galea, 3A
		// 33770 Vegadeo, Asturias, 西班牙
		test(-7.046012, 43.468089);
	}

	public void test10() throws Exception {
//		test(118.011597, 36.831478);
		test(118.01145,36.831567);
		
	}

}
