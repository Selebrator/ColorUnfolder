package org.example;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class CartesianProduct<T> implements Iterable<T[]> {

	private final Iterable<T>[] iterables;
	private final Function<Integer, T[]> arrayConstructor;

	public CartesianProduct(Function<Integer, T[]> arrayConstructor, List<? extends Iterable<T>> iterables) {
		this.arrayConstructor = arrayConstructor;
		this.iterables = iterables.toArray(Iterable[]::new);
	}

	@Override
	public Iterator<T[]> iterator() {
		return new CartesianIterator();
	}

	private class CartesianIterator implements Iterator<T[]> {
		private final Iterator<T>[] iterators;
		private T[] values;
		private int size;
		private boolean empty;

		public CartesianIterator() {
			this.size = iterables.length;
			this.iterators = new Iterator[size];

			// Initialize iterators
			for (int i = 0; i < size; i++) {
				iterators[i] = iterables[i].iterator();
				// If one of the iterators is empty then the whole Cartesian product is empty
				if (!iterators[i].hasNext()) {
					empty = true;
					break;
				}
			}

			// Initialize the tuple of the iteration values except the last one
			if (!empty) {
				values = arrayConstructor.apply(size);
				for (int i = 0; i < size - 1; i++) {
					setNextValue(i);
				}
			}
		}

		@Override
		public boolean hasNext() {
			if (empty) return false;
			for (int i = 0; i < size; i++) {
				if (iterators[i].hasNext()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public T[] next() {
			// Find first in reverse order iterator that has a next element
			int cursor;
			for (cursor = size - 1; cursor >= 0; cursor--) {
				if (iterators[cursor].hasNext()) break;
			}
			// Initialize iterators next from the current one
			for (int i = cursor + 1; i < size; i++) {
				iterators[i] = iterables[i].iterator();
			}

			// Get the next value from the current iterator and all the next ones
			for (int i = cursor; i < size; i++) {
				setNextValue(i);
			}
			return values;
		}

		/**
		 * Gets the next value provided there is one from the iterator at the given index.
		 */
		private void setNextValue(int index) {
			Iterator<T> it = iterators[index];
			if (it.hasNext()) {
				values[index] = it.next();
			}
		}
	}
}