package de.lukaspanneke.masterthesis.unfolding;

import java.util.Set;

/*
 * Some properties of this matrix, that could be exploited.
 *  - sparse symmetric matrix with boolean cells.
 *  - fast logical and of previous rows forms new rows.
 *  - diagonal entries are always false
 */

/**
 * Data-structure for finding co-sets.
 */
public interface ConcurrencyMatrix {

	/**
	 * Insert the condition.
	 */
	void add(Condition newCondition);

	/**
	 * Get all conditions concurrent to the input.
	 */
	Set<Condition> get(Condition key);

	/**
	 * Check if the input is a co-set, i.e., all elements are pairwise concurrent.
	 */
	boolean isCoset(Condition[] candidate);
}
