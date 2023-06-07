package org.example.deprecated;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.*;
import java.util.stream.IntStream;

public class IntMatrixSparseColumns {
	private int columnCount;
	private int rowCount;
	private final List<SparseIntVector> columns;
	private final LoadingCache<Integer, SparseIntVector> cachedRows = CacheBuilder.newBuilder().build(CacheLoader.from(this::getRow_copy));

	public IntMatrixSparseColumns(int rows, int columns) {
		this.rowCount = rows;
		this.columnCount = columns;
		this.columns = new ArrayList<>(this.columnCount);

		for (int column = 0; column < this.columnCount; column++) {
			this.columns.add(new SparseIntVector(0));
		}
	}

	public IntMatrixSparseColumns(IntMatrixSparseColumns original) {
		this.rowCount = original.rowCount;
		this.columnCount = original.columnCount;
		this.columns = new ArrayList<>(this.columnCount);
		for (SparseIntVector column : original.columns) {
			this.columns.add(new SparseIntVector(column));
		}
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int get(int row, int column) {
		return this.columns.get(column).get(row);
	}

	public SparseIntVector getColumn(int column) {
		return this.columns.get(column);
	}

	public SparseIntVector getRow_copy(int row) {
		SparseIntVector ret = new SparseIntVector(0);
		for (int column = 0; column < this.columnCount; column++) {
			ret.set(column, this.get(row, column));
		}
		ret.makeImmutable();
		return ret;
	}

	public Optional<SparseIntVector> getRow_copy(int row, int minSizeInclusive, int maxSizeInclusive) {
		SparseIntVector ret = new SparseIntVector(maxSizeInclusive);
		for (int column = 0; column < this.columnCount; column++) {
			int value = this.get(row, column);
			if (value != 0) {
				if (ret.size() >= maxSizeInclusive) {
					return Optional.empty();
				}
				ret.set(column, value);
			}
		}
		ret.makeImmutable();
		if (ret.size() < minSizeInclusive) {
			return Optional.empty();
		}
		return Optional.of(ret);
	}

	public SparseIntVector getRow_copy_cached(int row) {
		return this.cachedRows.getUnchecked(row);
	}

	public void set(int row, int column, int value) {
		this.checkBounds(row, column);
		if (value != 0) {
			this.columns.get(column).set(row, value);
		} else {
			this.columns.get(column).remove(row);
		}
		this.cachedRows.invalidate(row);
	}

	public void plusAssign(int row, int column, int toAdd) {
		if (toAdd == 0) {
			return;
		}
		this.columns.get(column).plusAssign(row, toAdd);
		this.cachedRows.invalidate(row);
	}

	public List<SparseIntVector> getColumns() {
		return this.columns;
	}

	public void addRow() {
		this.cachedRows.put(this.rowCount, new SparseIntVector(0));
		this.rowCount++;
	}

	public void appendColumn(SparseIntVector column) {
		OptionalInt maxRow = column.maxKey();
		if (maxRow.isPresent() && (this.rowCount == 0 || maxRow.getAsInt() > this.rowCount - 1)) {
			throw new IllegalArgumentException();
		}
		this.columns.add(column);
		this.columnCount++;
		for (int i = 0; i < column.size(); i++) {
			int row = column.keyAt(i);
			this.cachedRows.invalidate(row);
		}
	}

	public void appendRow(SparseIntVector row) {
		OptionalInt maxColumn = row.maxKey();
		if (maxColumn.isPresent() && (this.columnCount == 0 || maxColumn.getAsInt() > this.columnCount - 1)) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < row.size(); i++) {
			this.columns.get(row.keyAt(i)).set(this.rowCount, row.valueAt(i));
		}
		this.rowCount++;
		this.cachedRows.put(this.rowCount, new SparseIntVector(row));
	}

	public void removeColumn(int column) {
		SparseIntVector removed = this.columns.remove(column);
		this.columnCount--;
		if (removed != null) {
			for (int i = 0; i < removed.size(); i++) {
				int row = removed.keyAt(i);
				this.cachedRows.invalidate(row);
			}
		}
	}

	public void removeRow(int row) {
		for (SparseIntVector column : this.columns) {
			column.removeAndDecrementFollowingKeys(row);
		}
		this.cachedRows.invalidateAll(IntStream.range(row, this.rowCount).boxed().toList());
		this.rowCount--;
	}

	private void checkBounds(int row, int column) throws IllegalArgumentException {
		if (column < 0 || row < 0 || column >= this.columnCount || row >= this.rowCount) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(columnCount, rowCount, columns);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IntMatrixSparseColumns)) {
			return false;
		}

		IntMatrixSparseColumns other = (IntMatrixSparseColumns) obj;
		if (this.columnCount != other.columnCount) {
			return false;
		}
		if (this.rowCount != other.rowCount) {
			return false;
		}
		if (!this.columns.equals(other.columns)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Matrix{" + columns + '}';
	}
}
