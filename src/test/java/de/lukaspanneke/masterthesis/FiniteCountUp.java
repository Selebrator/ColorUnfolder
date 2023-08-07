package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.logic.Domain;
import de.lukaspanneke.masterthesis.logic.FiniteDomain;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * One place, initially 0. Every transition firing increments the token by one.
 * Can not have cut-off events.
 */
public class FiniteCountUp {

	int max = 5;
	Domain
			domain = FiniteDomain.fullRange(0, max);
	Variable
			x = new Variable("x", domain),
			y = new Variable("y", domain);
	Place
			p = new Place(1, "p");
	Transition
			t = new Transition(1, "t", y.eq(x.plus(1)));

	{
		link(p, x, t);
		link(t, y, p);
	}

	Net net = new Net(new Marking(Map.of(p, 0)));

	@Test
	void size() {
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(max, unfolding.getNumberEvents());
		assertEquals(max + 1, unfolding.getNumberConditions());
	}
}
