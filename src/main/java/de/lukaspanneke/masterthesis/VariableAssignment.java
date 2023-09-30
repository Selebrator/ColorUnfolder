package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.logic.FiniteDomain;
import de.lukaspanneke.masterthesis.logic.Variable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record VariableAssignment(Variable variable, int assignment) {
	public static Stream<Map<Variable, Integer>> itr(Stream<Variable> variables) {
		CartesianProduct<VariableAssignment> assignments = new CartesianProduct<>(VariableAssignment[]::new,
				variables.distinct()
						.<Iterable<VariableAssignment>>map(variable -> () -> domainStream(variable)
								.mapToObj(i -> new VariableAssignment(variable, i)).iterator())
						.toList());
		return StreamSupport.stream(assignments.spliterator(), false)
				.map(assignment -> Arrays.stream(assignment)
						.collect(Collectors.toMap(
								VariableAssignment::variable,
								VariableAssignment::assignment)));
	}

	public static IntStream domainStream(Variable variable) {
		int lowerIncl;
		int upperIncl;
		if (variable.domain() instanceof FiniteDomain finiteDomain) {
			lowerIncl = finiteDomain.lowerIncl();
			upperIncl = finiteDomain.upperIncl();
		} else if (Options.EXPANSION_RANGE != null) {
			lowerIncl = Options.EXPANSION_RANGE.lowerIncl();
			upperIncl = Options.EXPANSION_RANGE.upperIncl();
		} else {
			throw new IllegalStateException("can not expand, because the domain of " + variable
					+ " is infinite and no default bounds are given.");
		}
		return IntStream.rangeClosed(lowerIncl, upperIncl);
	}
}
