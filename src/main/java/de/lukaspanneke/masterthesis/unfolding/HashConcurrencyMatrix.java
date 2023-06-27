package de.lukaspanneke.masterthesis.unfolding;

import com.google.common.collect.Sets;
import de.lukaspanneke.masterthesis.TableRenderer;
import de.lukaspanneke.masterthesis.components.Condition;

import java.util.*;

public class HashConcurrencyMatrix implements ConcurrencyMatrix {
	private final Map<Condition, Set<Condition>> storage = new HashMap<>();

	@Override
	public Set<Condition> get(Condition key) {
		return this.storage.computeIfAbsent(key, k -> new HashSet<>());
	}

	@Override
	public void add(Condition newCondition) {
		Set<Condition> cob = newCondition.prepre().stream()
				.map(this::get)
				.reduce(Sets::intersection)
				.orElseGet(Collections::emptySet);
		Set<Condition> result = new HashSet<>(Sets.union(cob, newCondition.preset().postset()));
		this.storage.put(newCondition, result);
		for (Condition condition : result) {
			this.get(condition).add(newCondition);
		}
		//System.out.println(this);
	}

	@Override
	public boolean isCoset(Condition[] candidate) {
		for (int i = candidate.length - 1; i >= 1; i--) {
			Set<Condition> co = get(candidate[i]);
			for (int j = 0; j < i; j++) {
				if (!co.contains(candidate[j])) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		List<Condition> order = this.storage.keySet().stream().sorted().toList();
		return TableRenderer.renderTable(this.storage, order, order, Condition::name, Condition::name);
	}
}