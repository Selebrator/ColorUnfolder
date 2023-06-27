package de.lukaspanneke.masterthesis.components;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Precursor of an event.
 */
public final class PossibleExtension implements IEvent {

	private final Transition transition;
	private final Set<Condition> preset;
	private final int depth;
	private final Configuration coneConfiguration;

	public PossibleExtension(
			Transition transition,
			Set<Condition> preset
	) {
		this.transition = transition;
		this.preset = Set.copyOf(preset);
		this.depth = 1 + preset.stream()
				.mapToInt(condition -> condition.preset().depth())
				.max().orElse(0);
		this.coneConfiguration = new Configuration(Stream.concat(
				Stream.of(this),
				preset.stream().map(Condition::preset)
		).collect(Collectors.toUnmodifiableSet()));
	}

	public Transition transition() {
		return transition;
	}

	@Override
	public int depth() {
		return depth;
	}

	public Set<Condition> preset() {
		return preset;
	}

	public Set<Event> prepre() {
		return preset().stream().map(Condition::preset).collect(Collectors.toUnmodifiableSet());
	}

	public Configuration coneConfiguration() {
		return coneConfiguration;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (PossibleExtension) obj;
		return Objects.equals(this.transition, that.transition) &&
				Objects.equals(this.preset, that.preset);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transition, preset);
	}

	@Override
	public String toString() {
		return "(" + transition + "," + preset() + ")";
	}
}
