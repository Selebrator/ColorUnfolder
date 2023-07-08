package de.lukaspanneke.masterthesis.unfolding;

import com.google.common.collect.Sets;
import de.lukaspanneke.masterthesis.TableRenderer;
import de.lukaspanneke.masterthesis.components.Condition;

import java.util.*;

import static de.lukaspanneke.masterthesis.Options.PRINT_CONCURRENCY_INFO;

public class HashConcurrencyMatrix implements ConcurrencyMatrix {
	private final Map<Condition, Set<Condition>> storage = new HashMap<>();

	@Override
	public Set<Condition> get(Condition key) {
		Set<Condition> ans = this.storage.get(key);
		if (ans == null) {
			throw new AssertionError();
		}
		return ans;
	}

	@Override
	public void add(Condition newCondition) {
		Set<Condition> cob = newCondition.prepre().stream()
				.map(this::get)
				.reduce(Sets::intersection)
				.orElseGet(Collections::emptySet);
		Set<Condition> result = new HashSet<>(Sets.union(cob, Sets.intersection(newCondition.preset().postset(), this.storage.keySet())));
		this.storage.put(newCondition, result);
		for (Condition condition : result) {
			this.get(condition).add(newCondition);
		}
		if (PRINT_CONCURRENCY_INFO) {
			System.out.println(newCondition + " (made from " + newCondition.preset() + " " + newCondition.prepre() + ") is concurrent with " + result);
			System.out.println(this);
		}
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
		return TableRenderer.renderTable(this.storage, order, order, Condition::toString, Condition::name);
	}
}