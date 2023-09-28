package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.TableRenderer;

import java.util.*;
import java.util.stream.Collectors;

import static de.lukaspanneke.masterthesis.Options.PRINT_CONCURRENCY_INFO;

public class HashConcurrencyMatrix implements ConcurrencyMatrix {
	private final Map<Condition, Set<Condition>> storage = new HashMap<>();

	@Override
	public Set<Condition> get(Condition key) {
		Set<Condition> ans = this.storage.get(key);
		assert ans != null : "cannot get " + key + " in " + this.storage;
		return ans;
	}

	@Override
	public void add(Condition newCondition) {
		Set<Condition> result;
		Iterator<Condition> iterator = newCondition.prepre().iterator();
		Set<Condition> post = newCondition.preset().postset().stream()
				.filter(this.storage::containsKey)
				.collect(Collectors.toSet());
		if (iterator.hasNext()) {
			result = new HashSet<>(get(iterator.next()));
			while (iterator.hasNext()) {
				result.retainAll(get(iterator.next()));
			}
			result.addAll(post);
		} else {
			result = post;
		}
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