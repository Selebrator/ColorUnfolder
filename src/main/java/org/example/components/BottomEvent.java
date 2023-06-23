package org.example.components;

import org.example.logic.Formula;
import org.example.net.Marking;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BottomEvent extends Event {

	public BottomEvent(Marking initialMarking) {
		super(
				Integer.MIN_VALUE,
				new Transition(
						Integer.MIN_VALUE,
						"⊥",
						Map.of(),
						initialMarking.tokens().keySet().stream()
								.collect(Collectors.toMap(Function.identity(), place -> new Variable(place.name()))),
						initialMarking.tokens().entrySet().stream()
								.map(e -> new Variable(e.getKey().name()).eq(e.getValue()))
								.collect(Formula.and())
				),
				Map.of()
		);
	}

	@Override
	public String toString() {
		return "⊥";
	}

	@Override
	public String name() {
		return "⊥";
	}
}
