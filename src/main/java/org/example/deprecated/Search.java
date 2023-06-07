package org.example.deprecated;

import java.util.Arrays;

public class Search {

	public static int search(int[] array, int value, int lo, int hi) {
		if (hi - lo < 48) {
			return linearSearch(array, value, lo, hi);
		} else {
			return binarySearch(array, value, lo, hi);
		}
	}

	public static int linearSearch(int[] array, int value, int lo, int hi) {
		if (array.length == 0) {
			return ~0;
		}
		if (lo > hi) {
			return ~lo;
		}
		if (value < array[lo]) {
			return ~lo;
		}
		if (array[hi] < value) {
			return ~(hi + 1);
		}
		int i;
		for (i = lo; i <= hi; i++) {
			if (array[i] == value) {
				return i;
			} else if (array[i] > value) {
				return ~i;
			}
		}
		throw new IllegalStateException("array=" + Arrays.toString(array) + ", value=" + value + ", lo=" + lo + ", hi=" + hi + ", i=" + i);
	}

	/**
	 * Return the first index i such that array[i] == value if it exists,
	 * otherwise return i such that ~i is the index where value would be inserted in array.
	 *
	 * @param array
	 * @param value
	 * @param lo    inclusive
	 * @param hi    exclusive
	 * @return
	 */
	public static int binarySearch(int[] array, int value, int lo, int hi) {
		if (array.length == 0) {
			return ~0;
		}
		while (lo <= hi) {
			final int mid = lo + ((hi - lo) >>> 1);
			final int midVal = array[mid];
			if (midVal < value) {
				lo = mid + 1;
			} else if (midVal > value) {
				hi = mid - 1;
			} else {
				return mid;  // value found
			}
		}
		return ~lo;  // value not present
	}

	public static int bisect_left(int[] array, int value, int lo, int hi) {
		if (array.length == 0) {
			return ~0;
		}
		while (lo < hi) {
			final int mid = lo + ((hi - lo) >>> 1);
			if (array[mid] < value) {
				lo = mid + 1;
			} else {
				hi = mid;
			}
		}
		if (array[lo] == value) {
			return lo;
		} else if (array[lo] < value) {
			return ~(lo + 1);
		} else {
			return ~lo;
		}
	}
}
