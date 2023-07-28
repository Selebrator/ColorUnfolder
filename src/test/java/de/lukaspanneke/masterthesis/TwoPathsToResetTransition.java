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

public class TwoPathsToResetTransition {

	Variable
			l = new Variable("l"),
			x = new Variable("x");
	Place
			s = new Place(0, "s"),
			p = new Place(1, "p"),
			q = new Place(2, "q");
	Transition
			i = new Transition(0, "ι"),
			a = new Transition(1, "α", l.eq(1)),
			b = new Transition(2, "β", l.eq(2)),
			o = new Transition(3, "ω");

	{
		link(s, x, i);
		link(i, l, p);
		link(p, l, a);
		link(p, l, b);
		link(a, x, q);
		link(b, x, q);
		link(q, x, o);
		link(o, l, p);
	}

	Net net = new Net(new Marking(Map.of(s, 0)));

	@Test
	void size() {
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(4, unfolding.getNumberEvents());
		assertEquals(5, unfolding.getNumberConditions());
	}
}
