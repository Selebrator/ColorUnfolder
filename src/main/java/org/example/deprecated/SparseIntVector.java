package org.example.deprecated;

import com.google.common.hash.PrimitiveSink;

import java.util.*;

import static org.example.deprecated.Hash.hash;
import static org.example.deprecated.Search.search;

public class SparseIntVector {

	private static final int DEFAULT_VALUE = 0;
	private static final int[] EMPTY_INT_ARRAY = new int[0];

	private int[] keys;
	private int[] values;
	private int size;
	private boolean immutable;

	public SparseIntVector(int initialCapacity) {
		if (initialCapacity == 0) {
			this.keys = EMPTY_INT_ARRAY;
			this.values = EMPTY_INT_ARRAY;
		} else {
			this.keys = new int[initialCapacity];
			this.values = new int[initialCapacity];
		}
		this.size = 0;
	}

	public SparseIntVector(int[] from) {
		this((int) Arrays.stream(from).filter(value -> value > 0).count());
		for (int key = 0; key < from.length; key++) {
			final int value = from[key];
			if (value != DEFAULT_VALUE) {
				this.set(key, value);
			}
		}
	}

	public SparseIntVector(List<Integer> from) {
		this((int) from.stream().filter(value -> value > 0).count());
		for (int key = 0; key < from.size(); key++) {
			final int value = from.get(key);
			if (value != DEFAULT_VALUE) {
				this.set(key, value);
			}
		}
	}

	public SparseIntVector(Set<Integer> from) {
		this(from.size());
		int[] keys = from.stream().mapToInt(i -> i).sorted().toArray();
		Arrays.sort(keys);
		for (int key : keys) {
			this.set(key, 1);
		}
	}

	public SparseIntVector(Map<Integer, Integer> from) {
		this(from.size());
		List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(from.entrySet());
		entries.sort(Map.Entry.comparingByKey());
		for (Map.Entry<Integer, Integer> entry : entries) {
			final int value = entry.getValue();
			if (value != DEFAULT_VALUE) {
				this.set(entry.getKey(), value);
			}
		}
	}

	public SparseIntVector(SparseIntVector from) {
		this(from.size);
		System.arraycopy(from.keys, 0, this.keys, 0, from.size);
		System.arraycopy(from.values, 0, this.values, 0, from.size);
		this.size = from.size;
	}

	/**
	 * {@code int[] { 1, 2, 2, 3 } -> vector [1:1, 2:2, 3:1]}
	 */
	public static SparseIntVector fromSortedMultisetArray(int[] from) {
		int unique = 0;
		for (int i = 0; i < from.length; i++) {
			// Move the index ahead while there are duplicates
			while (i < from.length - 1 && from[i] == from[i + 1]) {
				i++;
			}

			unique++;
		}
		SparseIntVector ans = new SparseIntVector(unique);
		for (int i = 0; i < from.length; i++) {
			ans.keys[ans.size] = from[i];
			int value = 1;
			while (i < from.length - 1 && from[i] == from[i + 1]) {
				i++;
				value++;
			}
			ans.values[ans.size] = value;
			ans.size++;
		}
		return ans;
	}

	public int indexOfKey(int key) {
		if (this.size == 0) {
			return ~0;
		}
		return search(this.keys, key, 0, this.size - 1);
	}

	public int size() {
		return this.size;
	}

	public int keyAt(int index) {
		return this.keys[index];
	}

	public int valueAt(int index) {
		return this.values[index];
	}

	public OptionalInt maxKey() {
		return this.size == 0 ? OptionalInt.empty() : OptionalInt.of(this.keys[this.size - 1]);
	}

	public boolean containsKey(int key) {
		return indexOfKey(key) >= 0;
	}

	public int get(int key) {
		return this.get(key, DEFAULT_VALUE);
	}

	public int get(int key, int valueIfNotFound) {
		int i = indexOfKey(key);
		if (i < 0) {
			return valueIfNotFound;
		} else {
			return this.values[i];
		}
	}

	/* return true iff for all i: this[i] >= that[i] */
	public boolean greaterEquals(SparseIntVector that) {
		return this.contains(that, true);
	}

	/* return true iff for all keys k in that: k is key in this */
	public boolean containsKeys(SparseIntVector that) {
		return this.contains(that, false);
	}

	private boolean contains(SparseIntVector that, boolean checkValues) {
		if (that.size == 0) {
			return true;
		}
		if (this.size < that.size) {
			return false;
		}

		for (int i = 0, k = 0; i < this.size && k < that.size; ) {
			int k1 = this.keys[i];
			int k2 = that.keys[k];
			if (k1 == k2) {
				if (checkValues && this.values[i] < that.values[k]) {
					return false;
				}
				i++;
				k++;
			} else if (k1 < k2) {
				/* this is behind, it has to catch up to that. */
				i = search(this.keys, k2, i + 1, this.size - 1);
				if (i < 0) {
					return false;
				}
				/* if that has more remaining elements than this,
				 * there must be a key where this is 0 and that is greater 0.
				 */
				if (this.size - i < that.size - k) {
					return false;
				}
			} else /* k1 > k2 */ {
				/* there was a key where this is 0 and that is greater 0. */
				return false;
			}
		}
		return true;
	}

	/* return k if (this == k * that), or empty otherwise. return 1 if both vectors are empty. */
	public OptionalInt scalar(SparseIntVector that) {
		if (this.size != that.size) {
			return OptionalInt.empty();
		}
		if (this.size == 0) {
			return OptionalInt.of(1);
		}
		if (!rangeEquals(this.keys, that.keys, 0, this.size)) {
			return OptionalInt.empty();
		}

		if (this.values[0] % that.values[0] != 0) {
			return OptionalInt.empty();
		}
		int k = this.values[0] / that.values[0];
		for (int i = 1; i < this.size; i++) {
			if (this.values[i] % that.values[i] != 0 || this.values[i] / that.values[i] != k) {
				return OptionalInt.empty();
			}
		}
		return OptionalInt.of(k);
	}

	public int remove(int key) {
		this.checkMutable();
		int i = indexOfKey(key);
		if (i >= 0) {
			this.removeAt(i);
		}
		return i;
	}

	public void removeAndDecrementFollowingKeys(int key) {
		this.checkMutable();
		if (this.size == 0 || key > this.keys[this.size - 1]) {
			return;
		}
		int k;
		for (k = this.size - 1; k >= 0 && this.keys[k] > key; k--) {
			this.keys[k]--;
		}
		if (k >= 0 && this.keys[k] == key) {
			this.removeAt(k);
		}
	}

	public void clear() {
		this.checkMutable();
		this.size = 0;
	}

	public void removeAt(int index) {
		this.checkMutable();
		System.arraycopy(this.keys, index + 1, this.keys, index, this.size - (index + 1));
		System.arraycopy(this.values, index + 1, this.values, index, this.size - (index + 1));
		this.size--;
	}

	public void set(int key, int value) {
		this.checkMutable();
		if (value != DEFAULT_VALUE && this.canAppend(key)) {
			this.append0(key, value);
		} else {
			this.put0(indexOfKey(key), key, value);
		}
	}

	public void plusAssign(int key, int toAdd) {
		this.checkMutable();
		if (toAdd == 0) {
			return;
		}
		int i = indexOfKey(key);
		if (i >= 0) {
			int newValue = this.values[i] + toAdd;
			if (newValue != DEFAULT_VALUE) {
				this.values[i] = newValue;
			} else {
				this.removeAt(i);
			}
		} else {
			if (this.canAppend(key)) {
				this.append0(key, toAdd);
			} else {
				this.insert0(~i, key, toAdd);
			}
		}
	}

//	/* this += weight * toAdd */
//	public void plusAssign(int weight, SparseIntVector toAdd) {
//		this.checkMutable();
//		if (toAdd.size == 0 || weight == 0) {
//			return;
//		}
//		if (this.size == 0) {
//			this.replaceWith(toAdd);
//			if (weight != 1) {
//				this.timesAssign(weight);
//			}
//		}
//
//		int i = 0, k = 0;
//		while (i < this.size && k < toAdd.size) {
//
//		}
//	}

	public void timesAssign(int factor) {
		this.checkMutable();
		if (factor == 0) {
			this.clear();
		}
		for (int i = 0; i < this.size; i++) {
			this.values[i] *= factor;
		}
	}

	public static SparseIntVector weightedSum(int w1, SparseIntVector v1, int w2, SparseIntVector v2) {
		if (v2.size == 0 || w2 == 0) {
			return new SparseIntVector(v1);
		}
		if (v1.size == 0 || w1 == 0) {
			return new SparseIntVector(v2);
		}

		SparseIntVector ret = new SparseIntVector(v1.size + v2.size);
		int i = 0, k = 0;
		while (i < v1.size && k < v2.size) {
			int k1 = v1.keys[i];
			int k2 = v2.keys[k];
			if (k1 == k2) {
				int sum = w1 * v1.values[i] + w2 * v2.values[k];
				if (sum != DEFAULT_VALUE) {
					ret.append0(k1, sum);
				}
				i++;
				k++;
			} else if (k1 < k2) {
				ret.append0(k1, w1 * v1.values[i]);
				i++;
			} else /* k1 > k2 */ {
				ret.append0(k2, w2 * v2.values[k]);
				k++;
			}
		}

		int remainingI = v1.size - i;
		if (remainingI > 0) {
			System.arraycopy(v1.keys, i, ret.keys, ret.size, remainingI);
			while (i < v1.size) {
				ret.values[ret.size++] = w1 * v1.values[i++];
			}
		}

		int remainingK = v2.size - k;
		if (remainingK > 0) {
			System.arraycopy(v2.keys, k, ret.keys, ret.size, remainingK);
			while (k < v2.size) {
				ret.values[ret.size++] = w2 * v2.values[k++];
			}
		}

		return ret;
	}

	public static Optional<SparseIntVector> weightedSumNonNegative(int w1, SparseIntVector v1, int w2, SparseIntVector v2) {
		if (v2.size == 0 || w2 == 0) {
			if (v1.isNonNegative()) {
				return Optional.of(new SparseIntVector(v1));
			} else {
				return Optional.empty();
			}
		}
		if (v1.size == 0 || w1 == 0) {
			if (v2.isNonNegative()) {
				return Optional.of(new SparseIntVector(v2));
			} else {
				return Optional.empty();
			}
		}

		SparseIntVector ret = new SparseIntVector(v1.size + v2.size);
		int i = 0, k = 0;
		while (i < v1.size && k < v2.size) {
			int k1 = v1.keys[i];
			int k2 = v2.keys[k];
			if (k1 == k2) {
				int sum = w1 * v1.values[i] + w2 * v2.values[k];
				if (sum < 0) {
					return Optional.empty();
				}
				if (sum != DEFAULT_VALUE) {
					ret.append0(k1, sum);
				}
				i++;
				k++;
			} else if (k1 < k2) {
				int sum = w1 * v1.values[i];
				if (sum < 0) {
					return Optional.empty();
				}
				ret.append0(k1, sum);
				i++;
			} else /* k1 > k2 */ {
				int sum = w2 * v2.values[k];
				if (sum < 0) {
					return Optional.empty();
				}
				ret.append0(k2, sum);
				k++;
			}
		}

		int remainingI = v1.size - i;
		if (remainingI > 0) {
			System.arraycopy(v1.keys, i, ret.keys, ret.size, remainingI);
			while (i < v1.size) {
				int sum = w1 * v1.values[i++];
				if (sum < 0) {
					return Optional.empty();
				}
				ret.values[ret.size++] = sum;
			}
		}

		int remainingK = v2.size - k;
		if (remainingK > 0) {
			System.arraycopy(v2.keys, k, ret.keys, ret.size, remainingK);
			while (k < v2.size) {
				int sum = w2 * v2.values[k++];
				if (sum < 0) {
					return Optional.empty();
				}
				ret.values[ret.size++] = sum;
			}
		}

		return Optional.of(ret);
	}

	/**
	 * Compare with lexicographic order.
	 */
	public int compareLexicographic(SparseIntVector that) {
		int i = 0;
		while (i < this.size && i < that.size && this.keys[i] == that.keys[i] && this.values[i] == that.values[i]) {
			i++;
		}
		if (i == this.size && i == that.size) {
			return 0;
		}
		if (this.keys[i] == that.keys[i]) {
			return this.values[i] - that.values[i];
		} else {
			return this.keys[i] - that.keys[i];
		}
	}

	private boolean isNonNegative() {
		for (int i = 0; i < this.size; i++) {
			if (this.values[i] < 0) {
				return false;
			}
		}
		return true;
	}

	public void replaceWith(SparseIntVector that) {
		this.checkMutable();
		this.keys = that.keys.clone();
		this.values = that.values.clone();
		this.size = that.size;
	}

	private boolean canAppend(int key) {
		return this.size == 0 || key > this.keys[this.size - 1];
	}

	private void append0(int key, int value) {
		this.keys = append(this.keys, this.size, key);
		this.values = append(this.values, this.size, value);
		this.size++;
	}

	private void insert0(int index, int key, int value) {
		this.keys = insert(this.keys, this.size, index, key);
		this.values = insert(this.values, this.size, index, value);
		this.size++;
	}

	private void put0(int index, int key, int value) {
		if (value == DEFAULT_VALUE) {
			if (index >= 0) {
				this.removeAt(index);
			}
		} else {
			if (index >= 0) {
				this.values[index] = value;
			} else {
				this.insert0(~index, key, value);
			}
		}
	}

	@Override
	public int hashCode() {
		int result = 1;
		for (int i = 0; i < this.size && i < this.keys.length; i++) {
			result = 31 * result + hash(this.keys[i]);
			result = 31 * result + hash(this.values[i]);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SparseIntVector)) {
			return false;
		}

		SparseIntVector other = (SparseIntVector) obj;
		if (this.size != other.size) {
			return false;
		}
		if (!rangeEquals(this.keys, other.keys, 0, this.size)) {
			return false;
		}
		if (!rangeEquals(this.values, other.values, 0, this.size)) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		if (size <= 0) {
			return "[]";
		}
		StringBuilder buffer = new StringBuilder(this.size * 28);
		buffer.append('[');
		for (int i = 0; i < this.size; i++) {
			if (i > 0) {
				buffer.append(", ");
			}
			buffer.append(this.keys[i])
					.append(':')
					.append(this.values[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	private static boolean rangeEquals(int[] a, int[] b, int startInclusive, int endExclusive) {
		if (a == b) {
			return true;
		}
		for (int i = startInclusive; i < endExclusive; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	private static int[] insert(int[] array, int currentSize, int index, int value) {
		assert currentSize <= array.length;
		if (currentSize + 1 <= array.length) {
			System.arraycopy(array, index, array, index + 1, currentSize - index);
			array[index] = value;
			return array;
		} else {
			int[] newArray = new int[growSize(currentSize)];
			System.arraycopy(array, 0, newArray, 0, index);
			newArray[index] = value;
			System.arraycopy(array, index, newArray, index + 1, array.length - index);
			return newArray;
		}
	}

	private static int[] append(int[] array, int currentSize, int value) {
		assert currentSize <= array.length;
		if (currentSize + 1 > array.length) {
			int[] newArray = new int[growSize(currentSize)];
			System.arraycopy(array, 0, newArray, 0, currentSize);
			array = newArray;
		}
		array[currentSize] = value;
		return array;
	}

	private static int growSize(int currentSize) {
		return currentSize <= 2 ? 4 : currentSize * 2;
	}

	public void makeImmutable() {
		this.immutable = true;
	}

	private void checkMutable() {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	public static void funnel(SparseIntVector from, PrimitiveSink into) {
		for (int i = 0; i < from.size; i++) {
			into.putInt(from.keys[i]);
		}
		for (int i = 0; i < from.size; i++) {
			into.putInt(from.values[i]);
		}
	}
}
