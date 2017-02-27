package com.github.sunnysuperman.addressbook.test;

import java.util.HashMap;
import java.util.Map;

import com.github.sunnysuperman.addressbook.Address;
import com.github.sunnysuperman.addressbook.AddressComponent;
import com.github.sunnysuperman.addressbook.lookup.GoogleMapService;
import com.github.sunnysuperman.addressbook.lookup.AbstractLocationService.LocationServiceOptions;
import com.github.sunnysuperman.addressbook.lookup.MapLocationService.CoordType;
import com.github.sunnysuperman.commons.utils.HttpClient;

public class GoogleMapLocationServiceTest extends AddressBaseTest {

	private String getKey() {
		return SecureConfigs.get().getString("location.google.key");
	}

	private void test(double lng, double lat) throws Exception {
		GoogleMapService service = new GoogleMapService(new LocationServiceOptions(), getKey(), getAddressFinder());
		AddressComponent ac = service.lookupAddress(CoordType.WGS84, lng, lat);
		assertTrue(ac != null);
		System.out.println("//" + ac);
		{
			Address addr = ac.toAddress("zh", new String[] { "zh", "en" });
			assertTrue(addr != null);
			System.out.println(addr);
		}
		{
			Address addr = ac.toAddress("en", new String[] { "en", "zh" });
			assertTrue(addr != null);
			System.out.println(addr);
		}
	}

	public void test() throws Exception {
		for (int id = 1; id <= 7; id++) {
			double[] point = null;
			switch (id) {
			case 1:
				// 萨摩亚
				point = new double[] { -13.669448, -172.533418 };
				break;
			case 5:
				// 上海
				point = new double[] { 31.211390, 121.591943 };
				break;
			case 6:
				// 美国
				point = new double[] { 38.031842, -100.808931 };
				break;
			case 7:
				// 日本
				point = new double[] { 35.189633, 137.015656 };
				break;
			}
			test(point[1], point[0]);
		}
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

	public void test2() throws Exception {
		// Av Castro del Río, 27
		// 14850 Baena, Córdoba, 西班牙
		test(-4.326585d, 37.617425d);
	}

	public void test3() throws Exception {
		// Barriada el Sardinero, 87
		// 51002 Ceuta, 西班牙
		test(-5.326377, 35.890844);
	}

	public void test4() throws Exception {
		// Calle Capitán Guiloche, 1
		// 52003 Melilla, 西班牙
		test(-2.945925, 35.292275);
	}

	public void test5() throws Exception {
		// 圣马丁萨斯加约拉斯
		// 西班牙巴塞罗纳
		test(1.501091, 41.700152);
	}

	public void test6() throws Exception {
		// 埃斯科尔卡
		// 西班牙巴利阿里群岛
		test(2.842854, 39.802254);
	}

	public void test7() throws Exception {
		// Avinguda Font Menor, 21
		// 46750 Simat de la Valldigna, Valencia, 西班牙
		test(-0.312585, 39.044477);
	}

	public void test8() throws Exception {
		// El Brillante
		// Córdoba, 西班牙
		test(-4.791813, 37.910323);
	}

	public void test9() throws Exception {
		// Calle Galea, 3A
		// 33770 Vegadeo, Asturias, 西班牙
		test(-7.046012, 43.468089);
	}

	public void test10() throws Exception {
		// [Spain, Madrid, Madrid, Madrid]
		test(-3.591793, 40.360755);
	}

	public void test_geocode() throws Exception {
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("language", "en");
		params.put("key", getKey());
		params.put("address", "Balearic Islands");
		HttpClient client = new HttpClient();
		client.doGet("https://maps.googleapis.com/maps/api/geocode/json", params, null);
		System.out.println(client.getResponseBody());
	}

}
