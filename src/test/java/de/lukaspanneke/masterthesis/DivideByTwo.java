package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
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

public class DivideByTwo {

	Variable
			x = new Variable("x"),
			y = new Variable("y");
	Place
			i = new Place(1, "i"),
			h = new Place(2, "h");
	Transition
			e = new Transition(1, "e", y.times(2).eq(x)),
			o = new Transition(2, "o", y.times(2).plus(1).eq(x));

	{
		link(i, x, e);
		link(i, x, o);
		link(e, y, h);
		link(o, y, h);
	}

	@Test
	void size() {
		Net net = new Net(new Marking(Map.of(i, 3)));
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(1, unfolding.getNumberEvents());
		assertEquals(2, unfolding.getNumberConditions());
	}

	@Test
	void even() {
		Net net = new Net(new Marking(Map.of(i, 6)));
		Unfolding unfolding = Unfolding.unfold(net, Set.of(e));
		renderAndClip(unfolding);
		assertTrue(unfolding.foundTarget().isPresent());
	}

	@Test
	void odd() {
		Net net = new Net(new Marking(Map.of(i, 3)));
		Unfolding unfolding = Unfolding.unfold(net, Set.of(o));
		renderAndClip(unfolding);
		assertTrue(unfolding.foundTarget().isPresent());
	}
}
