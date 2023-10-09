package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.examples.Examples;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.*;

// In a set of predicates, the conjunction of all pairs can be satisfiable,
// but it does not follow that the conjunction of the entire set is satisfiable.
// "it is possible to have a set {x1, x2, x3} of nodes that are in color conflict,
// but for which every subset of cardinality 2 is not in color conflict." -nick
public class ColorConflictDemonstration {
	int t = 1;
	int p = 1;

	Variable
			x = new Variable("x"),
			y = new Variable("y"),
			z = new Variable("z"),
			v = new Variable("v");
	Place
			p0 = new Place(p++),
			p1 = new Place(p++),
			p2 = new Place(p++),
			p3 = new Place(p++),
			p4 = new Place(p++),
			p5 = new Place(p++),
			p6 = new Place(p++),
			p7 = new Place(p++);

	Transition init = Examples.newTransition(t++, "init",
			Map.of(p0, v),
			Map.of(p1, x, p2, y, p3, z),
			x.geq(0).and(y.leq(0)).and(z.neq(0))
	);

	Transition t12 = Examples.newTransition(t++, "t12",
			Map.of(p1, v, p2, v),
			Map.of(p4, v),
			Formula.top()
	);
	Transition t13 = Examples.newTransition(t++, "t13",
			Map.of(p1, v, p3, v),
			Map.of(p4, v),
			Formula.top()
	);
	Transition t23 = Examples.newTransition(t++, "t23",
			Map.of(p2, v, p3, v),
			Map.of(p4, v),
			Formula.top()
	);
	Transition t123 = Examples.newTransition(t++, "t123",
			Map.of(p1, v, p2, v, p3, v),
			Map.of(p7, v),
			Formula.top()
	);
	Transition t45 = Examples.newTransition(t++, "t45",
			Map.of(p4, v, p5, v),
			Map.of(),
			Formula.top()
	);
	Transition t46 = Examples.newTransition(t++, "t46",
			Map.of(p4, v, p6, v),
			Map.of(),
			Formula.top()
	);
	Transition t56 = Examples.newTransition(t++, "t56",
			Map.of(p5, v, p6, v),
			Map.of(),
			Formula.top()
	);
	Transition t456 = Examples.newTransition(t++, "t456",
			Map.of(p4, v, p5, v, p6, v),
			Map.of(),
			Formula.top()
	);

	Net net = new Net(new Marking(Map.of(p0, 0)));

	@Test
	void test() {
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(unfolding.getNumberEvents(), 4);
		assertEquals(unfolding.getNumberConditions(), 7);
		assertTrue(Unfolding.unfold(net, Set.of(t12)).foundTarget().isPresent());
		assertTrue(Unfolding.unfold(net, Set.of(t13)).foundTarget().isPresent());
		assertTrue(Unfolding.unfold(net, Set.of(t23)).foundTarget().isPresent());
		assertTrue(Unfolding.unfold(net, Set.of(t123)).foundTarget().isEmpty());
		assertTrue(Unfolding.unfold(net, Set.of(t45)).foundTarget().isEmpty());
		assertTrue(Unfolding.unfold(net, Set.of(t46)).foundTarget().isEmpty());
		assertTrue(Unfolding.unfold(net, Set.of(t56)).foundTarget().isEmpty());
		assertTrue(Unfolding.unfold(net, Set.of(t456)).foundTarget().isEmpty());
	}

}
