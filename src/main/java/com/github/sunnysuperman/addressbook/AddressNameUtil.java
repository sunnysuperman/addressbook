package com.github.sunnysuperman.addressbook;

import com.github.sunnysuperman.commons.utils.StringUtil;

public class AddressNameUtil {

	public static boolean possibleSame(String localizedName, String name, boolean accurate) {
		if (accurate) {
			// 是否完全一样
			if (localizedName.equalsIgnoreCase(name)) {
				return true;
			}
		} else {
			// 基本匹配
			if (maybeTheSame(localizedName, name)) {
				return true;
			}
		}
		return false;
	}

	private static boolean maybeTheSame(String name1, String name2) {
		if (name1.equalsIgnoreCase(name2)) {
			return true;
		}
		if (!StringUtil.isChineseChar(name1.charAt(0))) {
			return false;
		}
		int l1 = name1.length();
		int l2 = name2.length();
		if (l1 < 2 || l2 < 2) {
			return false;
		}
		String longer = name1, shorter = name2;
		if (l1 < l2) {
			longer = name2;
			shorter = name1;
		}
		boolean theSame = longer.indexOf(shorter) >= 0;
		if (theSame) {
			return true;
		}
		if (name1.substring(0, 2).equals(name2.substring(0, 2))) {
			return true;
		}
		return false;
	}

	public static double findLikelyScore(String base, String compare) {
		// Valencia , Valensiya
		// Valencian community , Commudad Valencia
		base = base.toLowerCase();
		compare = compare.toLowerCase();
		final int len = base.length();
		final int compareLen = compare.length();
		char c, c2;
		int matchCounter = 0;
		for (int i = 0; i < len; i++) {
			c = base.charAt(i);
			if (c == ' ') {
				continue;
			}
			int matchIndex = -1;
			for (int j = 0; j < compareLen; j++) {
				c2 = compare.charAt(j);
				if (c2 == c) {
					matchIndex = j;
					break;
				}
			}
			if (matchIndex < 0) {
				continue;
			}
			int counter = 1;
			for (int k = 1; i + k < len; k++) {
				char d = base.charAt(i + k);
				int compareIndex = matchIndex + k;
				if (compareIndex >= compareLen) {
					break;
				}
				char d2 = compare.charAt(compareIndex);
				if (d2 != d) {
					break;
				}
				counter++;
			}
			if (counter < 3) {
				continue;
			}
			matchCounter += counter;
			i += counter - 1;
		}
		return ((double) matchCounter) / Math.min(len, compareLen);
	}

}
