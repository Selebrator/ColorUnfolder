package de.lukaspanneke.masterthesis.expansion;

import de.lukaspanneke.masterthesis.VariableAssignment;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Expansion {

	private static final Variable VAR = new Variable(".");

	/**
	 * Given a high-level net, this produces the low-level net with the same behaviour
	 */
	public static Net expand(Net hlNet) {
		int[] llPlaceId = {1};
		int[] llTransId = {1};
		Net.Nodes nodes = hlNet.collectNodes();
		Map<Place, Map<Integer, Place>> hlToLlPlaces = nodes.places().stream()
				.collect(Collectors.toMap(
						place -> place,
						place -> VariableAssignment.domainStream(VAR)
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
								hlTrans.postSet().values().stream()))
				.filter(assignment -> {
					Set<Variable> variables = new HashSet<>(hlTrans.guard().support());
					variables.addAll(hlTrans.preSet().values());
					variables.addAll(hlTrans.postSet().values());
					Formula variableDomainConstraints = variables.stream()
							.map(Variable::domainConstraint)
							.collect(Formula.and());
					return hlTrans.guard().and(variableDomainConstraints).evaluate(assignment);
				})
				.forEach(assignment -> {
					Function<Map<Place, Variable>, Map<Place, Variable>> hlToLlFlow = set -> set.entrySet().stream()
							.collect(Collectors.toMap(
									llPlaceVariableEntry -> hlToLlPlaces
											.get(llPlaceVariableEntry.getKey())
											.get(assignment.get(llPlaceVariableEntry.getValue())),
									llPlaceVariableEntry -> VAR));
					Transition llTrans = new Transition(
							llTransId[0],
							"t" + llTransId[0]++ + "_" + hlTrans.name(),
							hlToLlFlow.apply(hlTrans.preSet()),
							hlToLlFlow.apply(hlTrans.postSet()),
							Formula.top()
					);
					llTrans.preSet().keySet().forEach(place -> place.postSet().add(llTrans));
					llTrans.postSet().keySet().forEach(place -> place.preSet().add(llTrans));
				}));
		return new Net(new Marking(initial));
	}
}
