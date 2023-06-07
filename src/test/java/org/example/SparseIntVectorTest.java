package org.example;

import org.example.deprecated.SparseIntVector;
import org.junit.jupiter.api.Test;

class SparseIntVectorTest {

    @Test
    void fromSortedMultisetArray() {
        int[] multiset = {1, 2, 2, 3};
        SparseIntVector v = SparseIntVector.fromSortedMultisetArray(multiset);
        System.out.println(v);
    }

}