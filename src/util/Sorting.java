package util;

import java.util.ArrayList;

public class Sorting {
	/**
	 * "aa","zz"
	 */
	public static final int INCREASING = 10;
	/**
	 * "zz", "aa"
	 */
	public static final int DECREASING = 11;

	public static long[] mergeSort(long[] inp, int sortingType) {
		long[] stuff = inp.clone();
		if (stuff.length == 1 || stuff.length == 0)
			return stuff;
		int length = stuff.length;

		ArrayList<ArrayList<Long>> base = new ArrayList<ArrayList<Long>>();
		for (int i = 0; i < length; i++) {
			ArrayList<Long> temp = new ArrayList<Long>();
			temp.add(stuff[i]);
			base.add(temp);
		}
		Long[] objlist = combineAllLists(base, sortingType).get(0).toArray(new Long[0]);

		long[] result = new long[objlist.length];
		for (int i = 0; i < objlist.length; i++) {
			result[i] = objlist[i];
		}

		return result;
	}

	private static ArrayList<ArrayList<Long>> combineAllLists(ArrayList<ArrayList<Long>> bases, int sortingType) {
		if (bases.size() == 1)
			return bases;

		if (bases.size() % 2 == 1) {
			bases.set(0, combineLists(bases.get(0), bases.get(1), sortingType));
			bases.remove(1);
		}

		for (int i = 0; i < bases.size() / 2; i++) {
			bases.set(i, combineLists(bases.get(i), bases.get(i + 1), sortingType));
			bases.remove(i + 1);
		}

		return combineAllLists(bases, sortingType);
	}

	private static ArrayList<Long> combineLists(ArrayList<Long> first, ArrayList<Long> second, int sortingType) {
		ArrayList<Long> result = new ArrayList<Long>();
		int firindex = 0, secindex = 0;

		if (sortingType == INCREASING) {
			while (firindex != first.size() && secindex != second.size()) {
				if (first.get(firindex) <= (second.get(secindex))) {
					result.add(first.get(firindex));
					firindex++;
				} else if (first.get(firindex) > (second.get(secindex))) {
					result.add(second.get(secindex));
					secindex++;
				}
			}
		} else if (sortingType == DECREASING) {
			while (firindex != first.size() && secindex != second.size()) {
				if (first.get(firindex) >= (second.get(secindex))) {
					result.add(first.get(firindex));
					firindex++;
				} else if (first.get(firindex) < (second.get(secindex))) {
					result.add(second.get(secindex));
					secindex++;
				}
			}
		}

		if (firindex != first.size()) {
			for (; firindex < first.size(); firindex++) {
				result.add(first.get(firindex));
			}
		} else if (secindex != second.size()) {
			for (; secindex < second.size(); secindex++) {
				result.add(second.get(secindex));
			}
		}
		return result;
	}

}
