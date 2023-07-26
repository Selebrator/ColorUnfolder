package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.components.Place;
import de.lukaspanneke.masterthesis.components.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleColorConflict {

	Variable
			x = new Variable("x"),
			y = new Variable("y"),
			z = new Variable("z");
	Place
			p1 = new Place(1),
			p2 = new Place(2),
			p3 = new Place(3),
			p4 = new Place(4),
			p5 = new Place(5),
			p6 = new Place(6);
	Transition
			t1 = new Transition(1, y.gt(0).and(z.lt(0))),
			t2 = new Transition(2, x.neq(0)),
			t3 = new Transition(3), // the transition that can never fire
			t4 = new Transition(4);

	{
		link(p1, x, t1);
		link(t1, y, p2);
		link(t1, z, p3);
		link(p2, x, t2);
		link(t2, x, p4);
		link(p3, x, t4);
		link(t4, x, p6);
		link(p2, x, t3);
		link(p3, x, t3);
		link(t3, x, p5);
	}

	Net net = new Net(new Marking(Map.of(p1, 1)));

	@Test
	void colorConflictNeverFires() {
		Unfolding unfolding = Unfolding.unfold(net, Set.of(t3));
		renderAndClip(unfolding);
		System.out.println("target event: " + unfolding.foundTarget());
		assertTrue(unfolding.foundTarget().isEmpty());
		assertEquals(3, unfolding.getNumberEvents());
		assertEquals(5, unfolding.getNumberConditions());
	}
}
