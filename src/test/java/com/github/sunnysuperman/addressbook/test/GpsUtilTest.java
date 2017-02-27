package com.github.sunnysuperman.addressbook.test;

import com.github.sunnysuperman.addressbook.GpsUtil;

public class GpsUtilTest extends BaseTest {

	public void test_gcjToGps84() {
		System.out.println(GpsUtil.gcjToGps84(118.011597, 36.831478));
	}

	public void test_gps84ToGcj02() {
		System.out.println(GpsUtil.gps84ToGcj02(121.59870244, 31.20291149));
	}

	public void test_distance() {
		System.out.println(GpsUtil.getDistance(121.610024, 31.206906, 121.610024, 31.206905) * 1000);
	}
}
