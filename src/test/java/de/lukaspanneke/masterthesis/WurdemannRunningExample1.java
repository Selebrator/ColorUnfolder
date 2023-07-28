package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WurdemannRunningExample1 {

	Variable
			k = new Variable("k"),
			l = new Variable("l"),
			ll = new Variable("l'");
	Place
			p1 = new Place(1),
			p2 = new Place(2),
			p3 = new Place(3);
	Transition
			t1 = new Transition(1, l.eq(1)),
			t2 = new Transition(2, ll.neq(0)),
			t3 = new Transition(3, k.eq(0));

	{
		link(p1, k, t1);
		link(t1, l, p2);
		link(p2, l, t2);
		link(p2, l, t3);
		link(t2, ll, p2);
		link(t3, k, p3);
	}

	Net net = new Net(new Marking(Map.of(p1, 0)));

	@Test
	void size() {
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(5, unfolding.getNumberEvents());
		assertEquals(6, unfolding.getNumberConditions());
	}
}
