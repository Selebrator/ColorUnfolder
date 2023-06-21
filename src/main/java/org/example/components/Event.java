package org.example.components;

import com.google.common.collect.Sets;
import org.example.logic.generic.expression.Atom;
import org.example.logic.generic.formula.StateFormula;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* Transition (t, X, pred) in E */
public final class Event implements Comparable<Event> {
	private final int index;
	private final String name;
	private final Transition transition;
	private final Map<Condition, Variable> preset;
	private final Set<Condition> postset = new HashSet<>();
	private final Predicate localPred;
	private final Predicate conePred;
	private final int depth;
	public Event cutoffReason = null;
	private final Configuration coneConfiguration;
	private Set<Condition> conePreset;
	private Set<Condition> conePostset;
	private Set<Condition> coneCut;

	public Event(int index, Transition transition, Map<Condition, Variable> preset) {
		this.index = index;
		this.name = "e" + index;
		this.transition = transition;
		this.preset = Map.copyOf(preset);
		this.depth = 1 + preset.keySet().stream()
				.mapToInt(condition -> condition.preset().map(Event::depth).orElse(0))
				.max().orElse(0);
		this.coneConfiguration = new Configuration(Stream.concat(
				Stream.of(this),
				preset.keySet().stream().flatMap(condition -> condition.preset().stream())
		).collect(Collectors.toUnmodifiableSet()));
		this.localPred = new Predicate(guard(name, transition, preset));
		this.conePred = preset.keySet().stream()
				.flatMap(condition -> condition.preset().stream())
				.distinct()
				.map(Event::conePredicate)
				.reduce(Predicate::and).orElse(Predicate.TRUE)
				.and(this.localPred);
	}

	private static StateFormula<Variable> guard(String name, Transition transition, Map<Condition, Variable> conditionPreset) {
		Map<Atom<Variable>, Atom<Variable>> guardSubstitution = transition.guard().formula().support().stream()
				.collect(Collectors.toMap(variable -> variable, variable -> variable.local(name)));
		return transition.preSet().entrySet().stream()
				.collect(Collectors.groupingBy(
						Map.Entry::getValue,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())
				))
				.entrySet().stream()
				.map(originalVariableToPresetPlaces -> {
					Variable variable = originalVariableToPresetPlaces.getKey();
					List<Place> transitionPreset = originalVariableToPresetPlaces.getValue();
					List<Atom<Variable>> mustEqVariables = conditionPreset.entrySet().stream()
							.filter(conditionVariableEntry -> transitionPreset.contains(conditionVariableEntry.getKey().place()))
							.map(Map.Entry::getValue)
							.distinct()
							.collect(Collectors.toList());
					Variable representative = mustEqVariables.get(0).value();
					guardSubstitution.put(variable, representative);
					return mustEqVariables;
				})
				.map(StateFormula::allEquals)
				.reduce(StateFormula::and)
				.orElseGet(StateFormula::top)
				.and(transition.guard().formula().substitute(guardSubstitution));
	}

	public void calcContext(Set<Condition> initialConditions) {
		List<Event> prepre = preset.keySet().stream().flatMap(condition -> condition.preset().stream()).toList();
		this.conePreset = Set.copyOf(Sets.union(this.preset.keySet(), prepre.stream().map(event -> event.conePreset).reduce(Sets::union).orElseGet(Collections::emptySet)));
		this.conePostset = Sets.union(this.postset, prepre.stream().map(event -> event.conePostset).reduce(Sets::union).orElseGet(Collections::emptySet));
		this.coneCut = Sets.difference(Sets.union(initialConditions, conePostset), conePreset);
	}

	public String name() {
		return name;
	}

	public Transition transition() {
		return transition;
	}

	public Map<Condition, Variable> preset() {
		return preset;
	}

	public Set<Condition> postset() {
		return postset;
	}

	public Set<Condition> coneCut() {
		return coneCut;
	}

	public Predicate localPredicate() {
		return localPred;
	}

	public Predicate conePredicate() {
		return conePred;
	}

	public int depth() {
		return depth;
	}

	public boolean isCutoff() {
		return cutoffReason != null;
	}

	public void setCutoff(Event reason) {
		this.cutoffReason = reason;
	}

	public Configuration coneConfiguration() {
		return coneConfiguration;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Event event = (Event) o;
		return index == event.index;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}

	@Override
	public String toString() {
		return name + "(" + transition.name() + ")";
	}

	@Override
	public int compareTo(Event that) {
		return Integer.compare(this.transition.index(), that.transition.index());
	}
}
