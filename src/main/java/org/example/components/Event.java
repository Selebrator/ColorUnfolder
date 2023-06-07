package org.example.components;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/* Transition (t, X, pred) in E */
public final class Event implements Comparable<Event> {
	private final int index;
	private final String name;
	private final Transition transition;
	private final Set<Condition> preset;
	private final Set<Condition> postset = new HashSet<>();
	private final Predicate predicate;
	private final int depth;
	private boolean isCutoff = false;
	private final Configuration coneConfiguration;
	//private final Set<Condition> conePreset;
	//private final Set<Condition> conePostset;
	//private final Set<Condition> coneCut;

	public Event(int index, Transition transition, List<Condition> preset, Predicate predicate) {
		this.index = index;
		this.name = "e" + index;
		this.transition = transition;
		this.preset = Set.copyOf(preset);
		this.predicate = predicate;
		this.depth = 1 + preset.stream()
				.mapToInt(condition -> condition.preset().map(Event::depth).orElse(0))
				.max().orElse(0);
		Set<Event> cone = new HashSet<>();
		cone.add(this);
		for (Condition inputCondition : preset) {
			inputCondition.preset().ifPresent(event -> cone.addAll(event.coneConfiguration.events()));
		}
		this.coneConfiguration = new Configuration(cone);
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

	public Set<Condition> postset() {
		return postset;
	}

	public Predicate predicate() {
		return predicate;
	}

	public int depth() {
		return depth;
	}

	public boolean isCutoff() {
		return isCutoff;
	}

	public void setCutoff() {
		this.isCutoff = true;
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
