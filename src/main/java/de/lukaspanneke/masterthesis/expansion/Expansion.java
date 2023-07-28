package de.lukaspanneke.masterthesis.expansion;

import de.lukaspanneke.masterthesis.CartesianProduct;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.*;

public class Expansion {

	private static final Variable VAR = new Variable(".");

	/**
	 * Given a high-level net, this produces the low-level net with the same behaviour
	 */
	public static Net expand(Net hlNet, int lowerIncl, int upperExcl) {
		int[] llPlaceId = {1};
		int[] llTransId = {1};
		Net.Nodes nodes = hlNet.collectNodes();
		Map<Place, Map<Integer, Place>> hlToLlPlaces = nodes.places().stream()
				.collect(Collectors.toMap(
						place -> place,
						place -> IntStream.range(lowerIncl, upperExcl)
								.boxed()
								.collect(Collectors.toMap(
										i -> i,
										i -> new Place(llPlaceId[0]++, place.name() + "#" + i)
								))
				));
		Map<Place, Integer> initial = hlNet.initialMarking().tokens().entrySet().stream()
				.collect(Collectors.toMap(
						placeIntegerEntry -> hlToLlPlaces.get(placeIntegerEntry.getKey()).get(placeIntegerEntry.getValue()),
						placeIntegerEntry -> 1
				));
		nodes.transitions().forEach(hlTrans -> VariableAssignment.itr(
						Stream.concat(
								hlTrans.preSet().values().stream(),
								hlTrans.postSet().values().stream()),
						lowerIncl, upperExcl)
				.filter(assignment -> hlTrans.guard().evaluate(assignment))
				.forEach(assignment -> {
					Function<Map<Place, Variable>, Map<Place, Variable>> hlToLlFlow = set -> set.entrySet().stream()
							.collect(Collectors.toMap(
									llPlaceVariableEntry -> hlToLlPlaces
											.get(llPlaceVariableEntry.getKey())
											.get(assignment.get(llPlaceVariableEntry.getValue())),
									llPlaceVariableEntry -> VAR));
					Transition llTrans = new Transition(
							llTransId[0],
							"t" + llTransId[0]++,
							hlToLlFlow.apply(hlTrans.preSet()),
							hlToLlFlow.apply(hlTrans.postSet()),
							Formula.top()
					);
					llTrans.preSet().keySet().forEach(place -> place.postSet().add(llTrans));
					llTrans.postSet().keySet().forEach(place -> place.preSet().add(llTrans));
				}));
		return new Net(new Marking(initial));
	}

	record VariableAssignment(Variable variable, int assignment) {
		static Stream<Map<Variable, Integer>> itr(Stream<Variable> variables, int lowerIncl, int upperExcl) {
			CartesianProduct<VariableAssignment> assignments = new CartesianProduct<>(VariableAssignment[]::new,
					variables.distinct()
							.<Iterable<VariableAssignment>>map(variable -> () -> IntStream.range(lowerIncl, upperExcl)
									.mapToObj(i -> new VariableAssignment(variable, i)).iterator())
							.toList());
			return StreamSupport.stream(assignments.spliterator(), false)
					.map(assignment -> Arrays.stream(assignment)
							.collect(Collectors.toMap(
									VariableAssignment::variable,
									VariableAssignment::assignment)));
		}
	}

}