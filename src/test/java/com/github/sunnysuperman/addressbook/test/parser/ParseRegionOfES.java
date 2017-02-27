package com.github.sunnysuperman.addressbook.test.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.sunnysuperman.addressbook.test.BaseTest;
import com.github.sunnysuperman.commons.utils.BeanUtil;
import com.github.sunnysuperman.commons.utils.FileUtil;
import com.github.sunnysuperman.commons.utils.FileUtil.ReadLineHandler;
import com.github.sunnysuperman.commons.utils.HttpClient;
import com.github.sunnysuperman.commons.utils.JSONUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;

public class ParseRegionOfES extends BaseTest {

	private static class PareseRegionOfESHandler implements ReadLineHandler {
		boolean isAutonomousCommunity = true;
		List<SimpleAddressComponent> root = new ArrayList<SimpleAddressComponent>();
		SimpleAddressComponent parent;

		private void setCodeAndAdd(SimpleAddressComponent component, List<SimpleAddressComponent> parent, int base) {
			int code = base + parent.size() + 1;
			component.setCode(String.valueOf(code));
			parent.add(component);
		}

		@Override
		public boolean handle(String s, int line) throws Exception {
			if (s.indexOf(' ') == 0) {
				isAutonomousCommunity = true;
			} else {
				isAutonomousCommunity = false;
			}
			String name = s.substring(0, s.indexOf("Municipalities", 1)).trim();
			if (name.charAt(name.length() - 1) == '*') {
				name = name.substring(0, name.length() - 1);
			} else if (name.endsWith(" province")) {
				name = name.substring(0, name.indexOf(" province"));
			}
			SimpleAddressComponent component = new SimpleAddressComponent();
			component.setName(name);
			if (isAutonomousCommunity) {
				setCodeAndAdd(component, root, 0);// 1- 17
				parent = component;
			} else {
				if (parent.getChildren() == null) {
					parent.setChildren(new ArrayList<SimpleAddressComponent>());
				}
				// 100 - 1700
				setCodeAndAdd(component, parent.getChildren(), Integer.parseInt(parent.getCode()) * 100);
			}
			return true;
		}

	}

	private void error(String s) {
		System.err.println(s);
	}

	private List<String> parseProvinceText(String provinceName, String html) throws Exception {
		final List<String> names = new ArrayList<String>();
		FileUtil.read(new ByteArrayInputStream(html.getBytes()), null, new ReadLineHandler() {

			@Override
			public boolean handle(String s, int line) throws Exception {
				s = s.trim();
				if (s.isEmpty()) {
					return true;
				}
				int offset = s.indexOf('\t');
				if (offset < 0) {
					throw new RuntimeException("Bad line: " + s);
				}
				String name = s.substring(0, offset);
				names.add(name);
				return true;
			}

		});
		return names;
	}

	private List<String> parseProvinceHtml(String provinceName, String html) throws Exception {
		int index = html.indexOf("<table class=\"wikitable");
		if (index < 0) {
			return parseProvinceText(provinceName, html);
		}
		int index2 = html.indexOf("</table>", index);
		if (index2 < 0) {
			error("No province found: " + provinceName);
			return null;
		}
		html = html.substring(index, index2);
		Pattern pattern = Pattern.compile("<td><a.*>(.+?)</a></td>");
		Matcher matcher = pattern.matcher(html);
		List<String> names = new ArrayList<String>();
		while (matcher.find()) {
			String name = matcher.group(1);
			int commaIndex = name.indexOf(',');
			if (commaIndex > 0) {
				name = name.substring(0, commaIndex);
			}
			names.add(name);
		}
		return names;
	}

	private String botProvince(SimpleAddressComponent province) throws Exception {
		HttpClient client = new HttpClient();
		boolean ok = false;
		while (!ok) {
			ok = client.doGet("https://en.wikipedia.org/wiki/List_of_municipalities_in_" + province.getName(), null,
					null);
		}
		String html = client.getResponseBody();
		// List<String> towns = parseProvinceHtml(province.getName(), html);
		// return towns;
		return html;
	}

	public void test_parse1() throws Exception {
		PareseRegionOfESHandler handler = new PareseRegionOfESHandler();
		FileUtil.read(BaseTest.class.getResourceAsStream("resources/es/region-ES.source"), null, handler);
		for (SimpleAddressComponent component : handler.root) {
			if (component.getChildren() == null) {
				if (component.getName().equals("Ceuta") || component.getName().equals("Melilla")) {
					continue;
				}
				SimpleAddressComponent clone = new SimpleAddressComponent();
				int code = Integer.parseInt(component.getCode()) * 100 + 1;
				clone.setCode(String.valueOf(code));
				clone.setName(component.getName());
				component.setChildren(Collections.singletonList(clone));
			}
		}
		System.out.println(JSONUtil.toJSONString(handler.root));
	}

	public void test_parse_municipalities_of_province() throws Exception {
		String html = FileUtil.read(BaseTest.class
				.getResourceAsStream("resources/es/List_of_municipalities_in_Seville"));
		List<String> towns = parseProvinceHtml("Seville", html);
		System.out.println(StringUtil.join(towns, "\n"));
	}

	public void test_bot_provinces() throws Exception {
		List<?> autonomousCommunities = JSONUtil.parseJSONArray(FileUtil.read(BaseTest.class
				.getResourceAsStream("resources/es/region-ES-province.json")));
		for (Object autonomousCommunity : autonomousCommunities) {
			SimpleAddressComponent community = BeanUtil.map2bean((Map<?, ?>) autonomousCommunity,
					new SimpleAddressComponent());
			if (community.getChildren() == null) {
				continue;
			}
			for (SimpleAddressComponent province : community.getChildren()) {
				String html = botProvince(province);
				FileUtil.write(new File("/data/tmp/region-ES-province-detail/" + province.getName()), html);
			}
		}
	}

	public void test_parse_municipalities_of_provinces() throws Exception {
		List<?> autonomousCommunities = JSONUtil.parseJSONArray(FileUtil.read(BaseTest.class
				.getResourceAsStream("resources/es/region-ES-province.json")));
		List<SimpleAddressComponent> root = new ArrayList<SimpleAddressComponent>(autonomousCommunities.size());
		for (Object autonomousCommunity : autonomousCommunities) {
			SimpleAddressComponent community = BeanUtil.map2bean((Map<?, ?>) autonomousCommunity,
					new SimpleAddressComponent());
			root.add(community);
			if (community.getChildren() == null) {
				continue;
			}
			for (SimpleAddressComponent province : community.getChildren()) {
				String html = FileUtil.read(new File("/data/tmp/region-ES-province-detail/" + province.getName()));
				List<String> towns = parseProvinceHtml(province.getName(), html);
				List<SimpleAddressComponent> cities = new ArrayList<SimpleAddressComponent>(towns.size());
				int townIndex = 1;
				for (String town : towns) {
					if (townIndex >= 1000) {
						throw new RuntimeException("Exceed max number of " + province.getName());
					}
					int code = Integer.parseInt(province.getCode()) * 1000 + townIndex;
					SimpleAddressComponent city = new SimpleAddressComponent();
					city.setName(town);
					city.setCode(String.valueOf(code));
					cities.add(city);
					townIndex++;
				}
				province.setChildren(cities);
			}
		}
		String jsonString = JSON.toJSONString(root, SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.PrettyFormat);
		FileUtil.write(new File("/data/tmp/region-ES-all.json"), jsonString);
	}
}
