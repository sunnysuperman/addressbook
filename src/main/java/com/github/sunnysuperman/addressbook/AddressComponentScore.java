package com.github.sunnysuperman.addressbook;

import java.util.Comparator;

public class AddressComponentScore {

	public static class AddressComponentScoreComparator implements Comparator<AddressComponentScore> {

		private AddressComponentScoreComparator() {

		}

		public static AddressComponentScoreComparator getInstance() {
			return new AddressComponentScoreComparator();
		}

		@Override
		public int compare(AddressComponentScore o1, AddressComponentScore o2) {
			if (o1.score > o2.score) {
				return -1;
			}
			if (o1.score < o2.score) {
				return 1;
			}
			return 0;
		}

	}

	private AddressComponent component;
	private double score;

	public AddressComponentScore(AddressComponent component, double score) {
		super();
		this.component = component;
		this.score = score;
	}

	public AddressComponent getComponent() {
		return component;
	}

	public double getScore() {
		return score;
	}

}
