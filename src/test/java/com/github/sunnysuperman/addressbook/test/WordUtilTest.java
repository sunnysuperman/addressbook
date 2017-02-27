package com.github.sunnysuperman.addressbook.test;

import com.github.sunnysuperman.addressbook.AddressNameUtil;

public class WordUtilTest extends BaseTest {

	public void test() {
		double r1 = AddressNameUtil.findLikelyScore("Valencia", "Valensiya");
		double r2 = AddressNameUtil.findLikelyScore("Valencia", "Valencian");
		double r3 = AddressNameUtil.findLikelyScore("Valencia", "c Valenct");
		System.out.println(r1);
		System.out.println(r2);
		System.out.println(r3);
	}

	public void test2() {
		double r1 = AddressNameUtil.findLikelyScore("Valencian community", "Commudad Valencia");
		System.out.println(r1);
	}

	public void test3() {
		System.out.println(AddressNameUtil.findLikelyScore("a", ""));
	}

}
