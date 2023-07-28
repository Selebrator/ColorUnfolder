package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;

// https://en.wikipedia.org/wiki/Integer_square_root
public class IntegerSquareRoot {

	Net isqrt(int i) {
		if (i <= 1) {
			throw new IllegalArgumentException("i=" + i + " must be at least 2");
		}
		int p = 0;
		Place
				s0 = new Place(1, "s0"),
				s1 = new Place(2, "s1"),
				s2 = new Place(3, "s2"),
				x01 = new Place(4, "x01"),
				x02 = new Place(5, "x02"),
				x03 = new Place(6, "x03"),
				p3 = new Place(7),
				p4 = new Place(8),
				x11 = new Place(9, "x11"),
				x12 = new Place(10, "x12"),
				x13 = new Place(11, "x13"),
				p6 = new Place(12, "ans"),
				p7 = new Place(13),
				p8 = new Place(14);
		Variable
				s = new Variable("s"),
				x0 = new Variable("x0"),
				x1 = new Variable("x1"),
				q = new Variable("q"),
				r = new Variable("r"),
				y = new Variable("y");
		Transition
				t1 = new Transition(1, "init_x0", x0.times(2).plus(r).eq(s).and(x0.geq(0)).and(r.geq(0)).and(r.lt(2))),
				t2 = new Transition(2, "init_x1_div_x0", x0.times(q).plus(r).eq(s).and(q.geq(0)).and(r.geq(0)).and(r.lt(x0))),
				t3 = new Transition(3, "init_x1_add", y.eq(x0.plus(q))),
				t4 = new Transition(4, "init_x1_div2", q.times(2).plus(r).eq(y).and(q.geq(0)).and(r.geq(0)).and(r.lt(2))),
				t5 = new Transition(5, "done", x1.geq(x0)),
				t6 = new Transition(6, "cont", x1.lt(x0)),
				t7 = new Transition(7, "updt_x1_div", x1.times(q).plus(r).eq(s).and(q.geq(0)).and(r.geq(0)).and(r.lt(x1))),
				t8 = new Transition(8, "updt_x1_add", y.eq(x1.plus(q))),
				t9 = new Transition(9, "updt_x1_div2", q.times(2).plus(r).eq(y).and(q.geq(0)).and(r.geq(0)).and(r.lt(2)));

		link(s0, s, t1);
		link(t1, s, s1);
		link(t1, x0, x01);
		link(s1, s, t2);
		link(x01, x0, t2);
		link(t2, s, s2);
		link(t2, x0, x02);
		link(t2, q, p3);
		link(x02, x0, t3);
		link(p3, q, t3);
		link(t3, x0, x03);
		link(t3, y, p4);
		link(p4, y, t4);
		link(t4, q, x11);

		link(x03, x0, t5);
		link(x11, x1, t5);
		link(x03, x0, t6);
		link(x11, x1, t6);
		link(t5, x0, p6);
		link(t6, x1, x12);
		link(s2, s, t7);
		link(x12, x1, t7);
		link(t7, s, s2);
		link(t7, x1, x13);
		link(t7, q, p7);
		link(x13, x1, t8);
		link(p7, q, t8);
		link(t8, x1, x03);
		link(t8, y, p8);
		link(p8, y, t9);
		link(t9, q, x11);

		return new Net(new Marking(Map.of(s0, i)));
	}
}
