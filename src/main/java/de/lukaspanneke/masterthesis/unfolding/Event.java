package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.Options;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Transition;

import java.util.*;
import java.util.stream.Collectors;

/* Transition (t, X, pred) in E */
public final class Event implements Comparable<Event> {

	private final int index;
	private final String name;
	private final Transition transition;
	private final Set<Condition> preset;
	private Set<Condition> postset = new HashSet<>();
	private Formula localPred;
	private Formula conePred;
	private final int depth;
	private CutoffReason cutoffReason = null;
	private Configuration coneConfiguration;
	private Set<Condition> conePreset;
	private Set<Condition> conePostset;
	private Set<Condition> coneCut;
	private Map<Variable, Variable> postVariableSubstitution;

	public Event(int index, String name, Transition transition, Set<Condition> preset, Formula localPred, Formula conePred, Map<Variable, Variable> postVariableSubstitution) {
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
		this.postVariableSubstitution = Map.copyOf(postVariableSubstitution);
	}

	public Map<Variable, Variable> postVariableSubstitution() {
		return Objects.requireNonNull(postVariableSubstitution);
	}

	public void finalizePostset() {
		this.postVariableSubstitution = null;
		this.postset = Set.copyOf(this.postset);
	}

	public void calcContext() {
		Collection<Condition> pre;
		{
			int conePresetSize = this.preset.stream()
					.mapToInt(condition -> condition.preset().conePreset.size())
					.sum() + this.preset.size();
			pre = new ArrayList<>(conePresetSize);
			pre.addAll(this.preset);
			for (Condition condition : this.preset) {
				pre.addAll(condition.preset().conePreset);
			}
			assert conePresetSize == pre.size() : conePresetSize + " != " + pre.size();
			this.conePreset = Set.copyOf(pre);
		}
		Collection<Condition> post;
		{
			int conePostsetSize = this.preset.stream()
					.mapToInt(condition -> condition.preset().conePostset.size())
					.sum() + this.postset.size();
			post = new ArrayList<>(conePostsetSize);
			post.addAll(this.postset);
			for (Condition condition : this.preset) {
				post.addAll(condition.preset().conePostset);
			}
			assert conePostsetSize == post.size() : conePostsetSize + " != " + post.size();
			this.conePostset = Set.copyOf(post);
		}
		post.removeAll(pre);
		this.coneCut = Set.copyOf(post);
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

	public boolean hasContext() {
		return coneCut != null;
	}

	public Set<Condition> coneCut() {
		if (coneCut == null) {
			throw new IllegalStateException("tried to access " + name() + ".coneCut before calling calcContext or after calling dropMemoryOfCutoff");
		}
		return coneCut;
	}

	public Formula guard() {
		return localPred;
	}

	public Formula conePredicate() {
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
