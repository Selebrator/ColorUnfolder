package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.logic.Variable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record VariableAssignment(Variable variable, int assignment) {
	public static Stream<Map<Variable, Integer>> itr(Stream<Variable> variables, int lowerIncl, int upperIncl) {
		CartesianProduct<VariableAssignment> assignments = new CartesianProduct<>(VariableAssignment[]::new,
				variables.distinct()
						.<Iterable<VariableAssignment>>map(variable -> () -> IntStream.rangeClosed(lowerIncl, upperIncl)
								.mapToObj(i -> new VariableAssignment(variable, i)).iterator())
						.toList());
		return StreamSupport.stream(assignments.spliterator(), false)
				.map(assignment -> Arrays.stream(assignment)
						.collect(Collectors.toMap(
								VariableAssignment::variable,
								VariableAssignment::assignment)));
	}
}
