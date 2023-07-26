package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.components.Place;
import de.lukaspanneke.masterthesis.components.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;

// can not have cut-off events, and has infinite markings
public class InfiniteCountUp {

	Variable
			x = new Variable("x"),
			y = new Variable("y");
	Place
			p = new Place(1, "p");
	Transition
			t = new Transition(1, "t", y.leq(x.plus(1)));

	{
		link(p, x, t);
		link(t, y, p);
	}

	Net net = new Net(new Marking(Map.of(p, 0)));

}
