package com.github.sunnysuperman.addressbook;

import java.math.BigDecimal;

/**
 * 各地图API坐标系统比较与转换;
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系,
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外）;
 * GCJ02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。
 * 谷歌中国地图和搜搜中国地图采用的是GCJ02地理坐标系; BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系;
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。 chenhua
 */
public class GpsUtil {
	// private static final String BAIDU_LBS_TYPE = "bd09ll";
	private static final double EARTH_RADIUS = 6378.137;

	private static final double pi = 3.1415926535897932384626;
	private static final double a = 6378245.0;
	private static final double ee = 0.00669342162296594323;

	public static class Gps {
		private double lng;
		private double lat;

		public Gps() {
			super();
		}

		public static double castLngLat(double d) {
			BigDecimal bd = new BigDecimal(d);
			return bd.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		}

		public Gps(double lng, double lat, boolean cast) {
			this.lng = cast ? castLngLat(lng) : lng;
			this.lat = cast ? castLngLat(lat) : lat;
		}

		public Gps(double lng, double lat) {
			this.lng = lng;
			this.lat = lat;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLng() {
			return lng;
		}

		public void setLng(double lng) {
			this.lng = lng;
		}

		@Override
		public String toString() {
			return lng + "," + lat;
		}
	}

	/**
	 * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static Gps gps84ToGcj02(double lng, double lat) {
		double dLat = transformLat(lng - 105.0, lat - 35.0);
		double dLon = transformLon(lng - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lng + dLon;
		return new Gps(mgLon, mgLat, true);
	}

	/**
	 * * 火星坐标系 (GCJ-02) to 84 * * @param lon * @param lat * @return
	 */
	public static Gps gcjToGps84(double lng, double lat) {
		Gps gps = transform(lng, lat);
		double lontitude = lng * 2 - gps.getLng();
		double latitude = lat * 2 - gps.getLat();
		return new Gps(lontitude, latitude, true);
	}

	public static boolean outOfChina(double lng, double lat) {
		if (lng < 72.004 || lng > 137.8347) {
			return true;
		}
		if (lat < 0.8293 || lat > 55.8271) {
			return true;
		}
		return false;
	}

	public static boolean isValidLongitude(double longitude) {
		double d = Math.abs(longitude);
		return d >= 0 && d <= 180;
	}

	public static boolean isValidLatitude(double latitude) {
		double d = Math.abs(latitude);
		return d >= 0 && d <= 90;
	}

	public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	private static Gps transform(double lng, double lat) {
		if (outOfChina(lng, lat)) {
			return new Gps(lng, lat);
		}
		double dLat = transformLat(lng - 105.0, lat - 35.0);
		double dLon = transformLon(lng - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lng + dLon;
		return new Gps(mgLon, mgLat);
	}

	private static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
		return ret;
	}
}
