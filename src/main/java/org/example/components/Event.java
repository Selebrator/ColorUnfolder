package org.example.components;

import com.google.common.collect.Sets;
import org.example.Unfolding;
import org.example.logic.Formula;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/* Transition (t, X, pred) in E */
public final class Event implements IEvent {
	private final int index;
	private final String name;
	private final Transition transition;
	private final Set<Condition> preset;
	private final Set<Condition> postset = new HashSet<>();
	private final Formula<Variable> localPred;
	private final Formula<Variable> conePred;
	private final int depth;
	private CutoffReason cutoffReason = null;
	private final Configuration coneConfiguration;
	private Set<Condition> conePreset;
	private Set<Condition> conePostset;
	private Set<Condition> coneCut;

	public Event(int index, PossibleExtension extension) {
		this(index, "e" + index, extension);
	}

	public Event(int index, String name, PossibleExtension extension) {
		this.index = index;
		this.name = name;
		this.transition = extension.transition();
		this.preset = Set.copyOf(extension.preset());
		this.depth = extension.depth();
		this.coneConfiguration = extension.coneConfiguration();
		this.localPred = Unfolding.guard(name(), transition(), preset());
		this.conePred = this.localPred.and(Unfolding.history(preset()));
	}

	public void calcContext(Set<Condition> initialConditions) {
		Set<Event> prepre = prepre();
		this.conePreset = Set.copyOf(Sets.union(this.preset(), prepre.stream().map(Event::conePreset).reduce(Sets::union).orElseGet(Set::of)));
		this.conePostset = Sets.union(this.postset(), prepre.stream().map(Event::conePostset).reduce(Sets::union).orElseGet(Set::of));
		this.coneCut = Sets.difference(Sets.union(initialConditions, conePostset()), conePreset());
	}

	public String name() {
		return name;
	}

	public Transition transition() {
		return transition;
	}

	public Set<Condition> preset() {
		return preset;
	}

	public Set<Event> prepre() {
		return preset().stream().map(Condition::preset).collect(Collectors.toUnmodifiableSet());
	}

	public Set<Condition> postset() {
		return postset;
	}

	private Set<Condition> conePreset() {
		return conePreset;
	}

	private Set<Condition> conePostset() {
		return conePostset;
	}

	public boolean hasContext() {
		return coneCut != null;
	}

	public Set<Condition> coneCut() {
		if (coneCut == null) {
			throw new IllegalStateException("tried to access " + name() + ".coneCut before calling calcContext");
		}
		return coneCut;
	}

	public Formula<Variable> guard() {
		return localPred;
	}

	public Formula<Variable> conePredicate() {
		return conePred;
	}

	public int depth() {
		return depth;
	}

	public boolean isCutoff() {
		return cutoffReason != null;
	}

	public Optional<CutoffReason> cutoffReason() {
		return Optional.ofNullable(cutoffReason);
	}

	public void setCutoff(CutoffReason reason) {
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
		return name() + "(" + transition().name() + ")";
	}
}
