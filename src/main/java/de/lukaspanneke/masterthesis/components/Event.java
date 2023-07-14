package de.lukaspanneke.masterthesis.components;

import com.google.common.collect.Sets;
import de.lukaspanneke.masterthesis.Options;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/* Transition (t, X, pred) in E */
public final class Event implements Comparable<Event> {

	private final int index;
	private final String name;
	private final Transition transition;
	private final Set<Condition> preset;
	private Set<Condition> postset = new HashSet<>();
	private Formula<Variable> localPred;
	private Formula<Variable> conePred;
	private final int depth;
	private CutoffReason cutoffReason = null;
	private Configuration coneConfiguration;
	private Set<Condition> conePreset;
	private Set<Condition> conePostset;
	private Set<Condition> coneCut;

	public Event(int index, String name, Transition transition, Set<Condition> preset, Formula<Variable> localPred, Formula<Variable> conePred) {
		this.index = index;
		this.name = name;
		this.transition = transition;
		this.preset = Set.copyOf(preset);
		this.depth = 1 + preset.stream()
				.mapToInt(condition -> condition.preset().depth())
				.max().orElse(0);
		this.coneConfiguration = Configuration.newConeConfiguration(this, preset);
		this.localPred = localPred;
		this.conePred = conePred;
	}

	public void calcContext() {
		Set<Event> prepre = prepre();
		this.postset = Set.copyOf(this.postset);
		this.conePreset = Set.copyOf(Sets.union(this.preset(), prepre.stream().map(Event::conePreset).reduce(Sets::union).orElseGet(Set::of)));
		this.conePostset = Set.copyOf(Sets.union(this.postset(), prepre.stream().map(Event::conePostset).reduce(Sets::union).orElseGet(Set::of)));
		this.coneCut = Set.copyOf(Sets.difference(conePostset(), conePreset()));
	}

	public void dropMemoryOfCutoff() {
		if (!Options.RENDER_DEBUG) {
			this.localPred = null;
		}
		this.conePred = null;
		this.coneConfiguration = null;
		this.conePreset = null;
		this.conePostset = null;
		this.coneCut = null;
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
		return Objects.requireNonNull(conePreset);
	}

	private Set<Condition> conePostset() {
		return Objects.requireNonNull(conePostset);
	}

	public boolean hasContext() {
		return coneCut != null;
	}

	public Set<Condition> coneCut() {
		if (coneCut == null) {
			throw new IllegalStateException("tried to access " + name() + ".coneCut before calling calcContext or after calling dropMemoryOfCutoff");
		}
		return coneCut;
	}

	public Formula<Variable> guard() {
		return Objects.requireNonNull(localPred);
	}

	public Formula<Variable> conePredicate() {
		return Objects.requireNonNull(conePred);
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
		return Objects.requireNonNull(coneConfiguration);
	}

	@Override
	public int compareTo(Event that) {
		return Integer.compare(this.transition().index(), that.transition().index());
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
