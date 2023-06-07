package org.example.components;

import com.google.common.collect.Multiset;

/* Marking K */
public record Cut(Multiset<Condition> conditions) {
}
