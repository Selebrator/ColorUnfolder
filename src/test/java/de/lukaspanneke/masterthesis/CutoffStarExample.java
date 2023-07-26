package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.components.Place;
import de.lukaspanneke.masterthesis.components.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;

// infinite size with cut-off. finite with cut-off*
public class CutoffStarExample {

	Variable
			x = new Variable("x"),
			y = new Variable("y"),
			z = new Variable("z");
	Place
			a = new Place(1, "a"),
			b = new Place(2, "b"),
			c = new Place(3, "c"),
			d = new Place(4, "d"),
			p = new Place(5, "p");
	Transition
			alpha = new Transition(1, "α"),
			beta = new Transition(2, "β"),
			gamma = new Transition(3, "γ"),
			delta = new Transition(4, "δ"),
			epsilon = new Transition(5, "ε"),
			t = new Transition(6, "t", y.eq(x.plus(1)));

	{
		link(a, x, alpha);
		link(alpha, x, b);
		link(b, x, beta);
		link(beta, x, a);

		link(c, x, gamma);
		link(gamma, x, d);
		link(d, x, delta);
		link(delta, x, c);

		link(b, z, epsilon);
		link(d, z, epsilon);
		link(p, x, epsilon);
		link(epsilon, z, b);
		link(epsilon, z, d);
		link(epsilon, y, p);
		link(p, x, t);
		link(t, y, p);
	}

	Net net = new Net(new Marking(Map.of(a, 0, c, 0, p, 0)));

}
