package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;

// no complete finite prefix can exist, even with cut-off*
public class CutoffStarFailedAttempt {

	Variable
			x = new Variable("x"),
			y = new Variable("y");
	Place
			p1 = new Place(1),
			p2 = new Place(2),
			p3 = new Place(3),
			p4 = new Place(4),
			p5 = new Place(5),
			p6 = new Place(6);
	Transition
			t = new Transition(1, "t", y.eq(x.plus(1))),
			u = new Transition(2, "u"),
			a = new Transition(3, "a"),
			b = new Transition(4, "b");

	{
		link(p1, x, t);
		link(t, y, p1);
		link(p1, x, a);
		link(a, y, p1);
		link(p3, t);
		link(t, p3);
		link(p3, a);
		link(p3, u);
		link(a, p5);
		link(u, p5);

		link(p2, x, t);
		link(t, y, p2);
		link(p2, x, b);
		link(b, y, p2);
		link(p4, t);
		link(t, p4);
		link(p4, b);
		link(p4, u);
		link(b, p6);
		link(u, p6);
	}

	Net net = new Net(new Marking(Map.of(p1, 0, p3, 0, p2, 0, p4, 0)));
}
