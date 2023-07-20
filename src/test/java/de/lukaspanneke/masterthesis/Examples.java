package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.components.Place;
import de.lukaspanneke.masterthesis.components.Transition;
import de.lukaspanneke.masterthesis.logic.Domain;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 1, unit = TimeUnit.MINUTES) // because a bug is likely to result in an infinite loop
public class Examples {

	static Variable VAR = new Variable(".");

	@Test
	void example() throws IOException {
		Net net = two_ways_to_reset();
		renderAndClip(net);
		renderAndClip(Unfolding.unfold(net, 11));
	}

	@Test
	void runMany() {
		Unfolding.unfold(div_by_two());
		Unfolding.unfold(lots_of_concurrency());
		Unfolding.unfold(mutex());
		Unfolding.unfold(romer_example_2_6());
		Unfolding.unfold(romer_example_3_4());
	}

	Net isqrt(int i) {
		// https://en.wikipedia.org/wiki/Integer_square_root
		if (i <= 1) {
			throw new IllegalArgumentException("i=" + i + " must be at least 2");
		}
		int p = 0;
		Place
				s0 = new Place(p++, "s0"),
				s1 = new Place(p++, "s1"),
				s2 = new Place(p++, "s2"),
				x01 = new Place(p++, "x01"),
				x02 = new Place(p++, "x02"),
				x03 = new Place(p++, "x03"),
				p3 = new Place(p++),
				p4 = new Place(p++),
				x11 = new Place(p++, "x11"),
				x12 = new Place(p++, "x12"),
				x13 = new Place(p++, "x13"),
				p6 = new Place(p++, "ans"),
				p7 = new Place(p++),
				p8 = new Place(p++);
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

	Net cutoff_star_example() {
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

		return new Net(new Marking(Map.of(a, 0, c, 0, p, 0)));
	}

	Net failed_attempt_cutoff_star_example() {
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

		return new Net(new Marking(Map.of(p1, 0, p3, 0, p2, 0, p4, 0)));
	}

	Net infinite_markings() {
		// can not have cut-off events
		Variable
				x = new Variable("x"),
				y = new Variable("y");
		Place
				p = new Place(1, "p");
		Transition
				t = new Transition(1, "t", y.leq(x.plus(1)));
		link(p, x, t);
		link(t, y, p);
		return new Net(new Marking(Map.of(p, 0)));
	}

	@Test
	void counting_up_finite() {
		int max = 5;
		Domain domain = variable -> variable.geq(0).and(variable.leq(max));
		// can not have cut-off events
		Variable
				x = new Variable("x", domain),
				y = new Variable("y", domain);
		Place
				p = new Place(1, "p");
		Transition
				t = new Transition(1, "t", y.eq(x.plus(1)));
		link(p, x, t);
		link(t, y, p);
		Net net = new Net(new Marking(Map.of(p, 0)));

		Unfolding unfolding = Unfolding.unfold(net);
		//renderAndClip(unfolding);
		assertEquals(max, unfolding.getNumberEvents());
		assertEquals(max + 1, unfolding.getNumberConditions());
	}

	Net div_by_two() {
		Variable
				x = new Variable("x"),
				y = new Variable("y");
		Place
				i = new Place(1, "i"),
				h = new Place(2, "h");
		Transition
				e = new Transition(1, "e", y.times(2).eq(x)),
				o = new Transition(2, "o", y.times(2).plus(1).eq(x));
		link(i, x, e);
		link(i, x, o);
		link(e, y, h);
		link(o, y, h);
		return new Net(new Marking(Map.of(i, 3)));
	}

	Net two_ways_to_reset() {
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
		link(s, x, i);
		link(i, l, p);
		link(p, l, a);
		link(p, l, b);
		link(a, x, q);
		link(b, x, q);
		link(q, x, o);
		link(o, l, p);
		return new Net(new Marking(Map.of(s, 0)));
	}

	Net lots_of_concurrency() {
		Place
				p1 = new Place(1),
				p2 = new Place(2),
				p3 = new Place(3),
				p4 = new Place(4),
				p5 = new Place(5),
				p6 = new Place(6),
				p7 = new Place(7),
				p8 = new Place(8),
				p9 = new Place(9),
				p10 = new Place(10),
				p11 = new Place(11),
				p12 = new Place(12),
				p13 = new Place(13);
		Transition
				t1 = new Transition(1),
				t2 = new Transition(2),
				t3 = new Transition(3),
				t4 = new Transition(4),
				t5 = new Transition(5),
				t6 = new Transition(6),
				t7 = new Transition(7),
				t8 = new Transition(8),
				t9 = new Transition(9);
		link(p1, t1, p2);
		link(p2, t2, p3);
		link(p3, t3, p4);
		link(p4, t9, p13);
		link(p5, t4, p6);
		link(p6, t6, p7);
		link(p7, t7, p8);
		link(p9, t5, p10);
		link(p8, t9, p13);
		link(p10, t6, p11);
		link(p11, t8, p12);
		link(p12, t9, p13);
		return new Net(new Marking(Map.of(p1, 1, p5, 1, p9, 1)));
	}

	@Test
	void lambdaswitch_size() throws IOException {
		try (StringWriter out = new StringWriter()) {
			Unfolding.unfold(lambdaswitch()).render(out);
			long size = out.toString().lines().count() - 2;
			long expected = Options.RENDER_DEBUG ? 1018 : 1005;
			assertEquals(expected, size);
		}
	}

	Net lambdaswitch() {
		// https://github.com/giannkas/ecofolder/tree/main/examples/gandalf2022_paper/lambdaswitch
		Place
				p1 = new Place(1),
				p2 = new Place(2),
				p3 = new Place(3),
				p4 = new Place(4),
				p5 = new Place(5),
				p6 = new Place(6),
				p7 = new Place(7),
				p8 = new Place(8),
				p9 = new Place(9),
				p10 = new Place(10),
				p11 = new Place(11);
		int i = 41;
		Transition
				t1 = new Transition(i--, "t1"),
				t2 = new Transition(i--, "t2"),
				t3 = new Transition(i--, "t3"),
				t4 = new Transition(i--, "t4"),
				t5 = new Transition(i--, "t5"),
				t6 = new Transition(i--, "t6"),
				t7 = new Transition(i--, "t7"),
				t8 = new Transition(i--, "t8"),
				t9 = new Transition(i--, "t9"),
				t10 = new Transition(i--, "t10"),
				t11 = new Transition(i--, "t11"),
				t12 = new Transition(i--, "t12"),
				t13 = new Transition(i--, "t13"),
				t14 = new Transition(i--, "t14"),
				t15 = new Transition(i--, "t15"),
				t16 = new Transition(i--, "t16"),
				t17 = new Transition(i--, "t17"),
				t18 = new Transition(i--, "t18"),
				t19 = new Transition(i--, "t19"),
				t20 = new Transition(i--, "t20"),
				t21 = new Transition(i--, "t21"),
				t22 = new Transition(i--, "t22"),
				t23 = new Transition(i--, "t23"),
				t24 = new Transition(i--, "t24"),
				t25 = new Transition(i--, "t25"),
				t26 = new Transition(i--, "t26"),
				t27 = new Transition(i--, "t27"),
				t28 = new Transition(i--, "t28"),
				t29 = new Transition(i--, "t29"),
				t30 = new Transition(i--, "t30"),
				t31 = new Transition(i--, "t31"),
				t32 = new Transition(i--, "t32"),
				t33 = new Transition(i--, "t33"),
				t34 = new Transition(i--, "t34"),
				t35 = new Transition(i--, "t35"),
				t36 = new Transition(i--, "t36"),
				t37 = new Transition(i--, "t37"),
				t38 = new Transition(i--, "t38"),
				t39 = new Transition(i--, "t39"),
				t40 = new Transition(i--, "t40"),
				t41 = new Transition(i--, "t41");

		link(t41, p8);
		link(t40, p8);
		link(t39, p8);
		link(t38, p8);
		link(t37, p8);
		link(t36, p8);
		link(t35, p8);
		link(t34, p8);
		link(t33, p8);
		link(t32, p8);
		link(t31, p4);
		link(t30, p4);
		link(t29, p1);
		link(t28, p1);
		link(t27, p1);
		link(t26, p5);
		link(t25, p5);
		link(t24, p7);
		link(t23, p2);
		link(t22, p2);
		link(t21, p4);
		link(t20, p4);
		link(t19, p4);
		link(t18, p10);
		link(t17, p10);
		link(t16, p10);
		link(t15, p2);
		link(t14, p2);
		link(t13, p6);
		link(t12, p6);
		link(t11, p11);
		link(t10, p11);
		link(t9, p11);
		link(t8, p11);
		link(t7, p11);
		link(t6, p11);
		link(t5, p9);
		link(t4, p9);
		link(t3, p6);
		link(t2, p3);
		link(t1, p3);
		link(t1, p1);
		link(t2, p4);
		link(t3, p5);
		link(t4, p7);
		link(t4, p1);
		link(t5, p6);
		link(t5, p1);
		link(t6, p7);
		link(t6, p1);
		link(t6, p9);
		link(t7, p6);
		link(t7, p1);
		link(t7, p9);
		link(t8, p2);
		link(t8, p1);
		link(t8, p9);
		link(t9, p7);
		link(t9, p4);
		link(t9, p9);
		link(t10, p6);
		link(t10, p4);
		link(t10, p9);
		link(t11, p2);
		link(t11, p4);
		link(t11, p9);
		link(t12, p1);
		link(t13, p4);
		link(t15, p5);
		link(t16, p8);
		link(t17, p3);
		link(t18, p5);
		link(t19, p6);
		link(t19, p10);
		link(t20, p2);
		link(t20, p10);
		link(t21, p3);
		link(t21, p10);
		link(t22, p1);
		link(t23, p4);
		link(t24, p5);
		link(t25, p7);
		link(t26, p11);
		link(t27, p6);
		link(t27, p10);
		link(t28, p2);
		link(t28, p10);
		link(t29, p3);
		link(t29, p10);
		link(t30, p7);
		link(t31, p11);
		link(t32, p2);
		link(t32, p5);
		link(t33, p2);
		link(t33, p4);
		link(t34, p3);
		link(t34, p5);
		link(t35, p3);
		link(t35, p4);
		link(t36, p7);
		link(t36, p5);
		link(t37, p7);
		link(t37, p4);
		link(t38, p6);
		link(t38, p5);
		link(t39, p6);
		link(t39, p4);
		link(t40, p2);
		link(t40, p1);
		link(t41, p3);
		link(t41, p1);
		link(p9, t41);
		link(p9, t40);
		link(p9, t39);
		link(p9, t38);
		link(p9, t37);
		link(p9, t36);
		link(p9, t35);
		link(p9, t34);
		link(p9, t33);
		link(p9, t32);
		link(p1, t31);
		link(p1, t30);
		link(p4, t29);
		link(p4, t28);
		link(p4, t27);
		link(p4, t26);
		link(p4, t25);
		link(p6, t24);
		link(p6, t23);
		link(p6, t22);
		link(p5, t21);
		link(p5, t20);
		link(p5, t19);
		link(p11, t18);
		link(p11, t17);
		link(p11, t16);
		link(p3, t15);
		link(p3, t14);
		link(p7, t13);
		link(p7, t12);
		link(p10, t11);
		link(p10, t10);
		link(p10, t9);
		link(p10, t8);
		link(p10, t7);
		link(p10, t6);
		link(p8, t5);
		link(p8, t4);
		link(p2, t3);
		link(p2, t2);
		link(p2, t1);
		link(p1, t1);
		link(p4, t2);
		link(p5, t3);
		link(p7, t4);
		link(p1, t4);
		link(p6, t5);
		link(p1, t5);
		link(p7, t6);
		link(p1, t6);
		link(p9, t6);
		link(p6, t7);
		link(p1, t7);
		link(p9, t7);
		link(p2, t8);
		link(p1, t8);
		link(p9, t8);
		link(p7, t9);
		link(p4, t9);
		link(p9, t9);
		link(p6, t10);
		link(p4, t10);
		link(p9, t10);
		link(p2, t11);
		link(p4, t11);
		link(p9, t11);
		link(p1, t12);
		link(p4, t13);
		link(p5, t15);
		link(p8, t16);
		link(p3, t17);
		link(p5, t18);
		link(p6, t19);
		link(p10, t19);
		link(p2, t20);
		link(p10, t20);
		link(p3, t21);
		link(p10, t21);
		link(p1, t22);
		link(p4, t23);
		link(p5, t24);
		link(p7, t25);
		link(p11, t26);
		link(p6, t27);
		link(p10, t27);
		link(p2, t28);
		link(p10, t28);
		link(p3, t29);
		link(p10, t29);
		link(p7, t30);
		link(p11, t31);
		link(p2, t32);
		link(p5, t32);
		link(p2, t33);
		link(p4, t33);
		link(p3, t34);
		link(p5, t34);
		link(p3, t35);
		link(p4, t35);
		link(p7, t36);
		link(p5, t36);
		link(p7, t37);
		link(p4, t37);
		link(p6, t38);
		link(p5, t38);
		link(p6, t39);
		link(p4, t39);
		link(p2, t40);
		link(p1, t40);
		link(p3, t41);
		link(p1, t41);

		return new Net(new Marking(Map.of(p1, 1, p7, 1, p8, 1, p10, 1)));
	}

	Net mutex() {
		var m = new Place(0, "mutex");
		var s1 = new Place(1, "safe1");
		var s2 = new Place(2, "safe2");
		var w1 = new Place(3, "wait1");
		var w2 = new Place(4, "wait2");
		var c1 = new Place(5, "crit1");
		var c2 = new Place(6, "crit2");
		var d1 = new Transition(1, "dngr1");
		var d2 = new Transition(2, "dngr2");
		var e1 = new Transition(3, "entr1");
		var e2 = new Transition(4, "entr2");
		var l1 = new Transition(5, "leav1");
		var l2 = new Transition(6, "leav2");
		link(s1, d1);
		link(d1, w1);
		link(w1, e1);
		link(e1, c1);
		link(c1, l1);
		link(l1, s1, m);
		link(s2, d2);
		link(d2, w2);
		link(w2, e2);
		link(e2, c2);
		link(c2, l2);
		link(l2, s2, m);
		link(m, e1, e2);
		return new Net(new Marking(Map.of(s1, 1, s2, 1, m, 1)));
	}

	@Test
	void mutexCanEnterCriticalSection() {
		Net net = mutex();
		Set<Transition> enter = net.collectNodes().transitions().stream()
				.filter(transition -> transition.name().startsWith("entr"))
				.collect(Collectors.toUnmodifiableSet());
		assertEquals(2, enter.size(), enter.toString());

		Unfolding unfolding = Unfolding.unfold(net, Integer.MAX_VALUE, enter);
		//renderAndClip(unfolding);
		System.out.println("target event: " + unfolding.foundTarget());
		assertTrue(unfolding.foundTarget().isPresent());
	}

	Net romer_example_2_6() {
		Place
				p1 = new Place(1),
				p2 = new Place(2),
				p3 = new Place(3),
				p4 = new Place(4),
				p5 = new Place(5),
				p6 = new Place(6),
				p7 = new Place(7);
		Transition
				t1 = new Transition(1),
				t2 = new Transition(2),
				t3 = new Transition(3),
				t4 = new Transition(4),
				t5 = new Transition(5),
				t6 = new Transition(6);
		link(p1, t1, t2);
		link(p2, t2, t3, t4);
		link(p3, t4, t5);
		link(p4, t6);
		link(p5, t6);
		link(p6, t6);
		link(p7);
		link(t1, p4);
		link(t2, p4, p5);
		link(t3, p5);
		link(t4, p5, p6);
		link(t5, p6);
		link(t6, p7);
		return new Net(new Marking(Map.of(p1, 1, p2, 1, p3, 1)));
	}

	Net romer_example_3_4() {
		Place
				p1 = new Place(1),
				p2 = new Place(2),
				p3 = new Place(3),
				p4 = new Place(4),
				p5 = new Place(5),
				p6 = new Place(6),
				p7 = new Place(7),
				p8 = new Place(8);
		Transition
				t1 = new Transition(1),
				t2 = new Transition(2),
				t3 = new Transition(3),
				t4 = new Transition(4),
				t5 = new Transition(5),
				t6 = new Transition(6);
		link(p1, t1);
		link(t1, p2);
		link(p2, t2);
		link(t2, p3, p5);
		link(p3, t3);
		link(t3, p1, p4);
		link(p8, t6);
		link(t6, p7);
		link(p7, t5);
		link(t5, p6, p5);
		link(p6, t4);
		link(t4, p8, p4);
		link(p4, t2, t5);
		link(p5, t3, t4);
		return new Net(new Marking(Map.of(p2, 1, p7, 1, p4, 1)));
	}

	@Test
	void wurdemann_example_1() {
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

		link(p1, k, t1);
		link(t1, l, p2);
		link(p2, l, t2);
		link(p2, l, t3);
		link(t2, ll, p2);
		link(t3, k, p3);
		Net net = new Net(new Marking(Map.of(p1, 0)));

		Unfolding unfolding = Unfolding.unfold(net);
		//renderAndClip(unfolding);
		assertEquals(5, unfolding.getNumberEvents());
		assertEquals(6, unfolding.getNumberConditions());
	}

	Net colorConflict() {
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
		return new Net(new Marking(Map.of(p1, 1)));
	}

	@Test
	void colorConflictNeverFires() {
		Net net = colorConflict();
		Transition t3 = net.collectNodes().transitions().stream()
				.filter(transition -> transition.index() == 3)
				.findAny().orElseThrow(() -> new AssertionError("could not get reference of t3"));

		Unfolding unfolding = Unfolding.unfold(net, Integer.MAX_VALUE, Set.of(t3));
		//renderAndClip(unfolding);
		System.out.println("target event: " + unfolding.foundTarget());
		assertTrue(unfolding.foundTarget().isEmpty());
		assertEquals(3, unfolding.getNumberEvents());
		assertEquals(5, unfolding.getNumberConditions());
	}

	//Net xiang() {
	//	var p0 = new Place(0, Set.of());
	//	var p1 = new Place(1, Set.of());
	//	var p2 = new Place(2, Set.of());
	//	var t0 = new Transition(0, Set.of(p0, p1));
	//	var t1 = new Transition(1, Set.of(p1, p2));
	//	var p3 = new Place(3, Set.of(t0));
	//	var p4 = new Place(4, Set.of(t0));
	//	var p5 = new Place(5, Set.of(t1));
	//	var t2 = new Transition(2, Set.of(p3));
	//	var t3 = new Transition(3, Set.of(p4));
	//	var t4 = new Transition(4, Set.of(p5));
	//	var p6 = new Place(6, Set.of(t2));
	//	var p7 = new Place(7, Set.of(t3));
	//	var p8 = new Place(8, Set.of(t4));
	//	var t5 = new Transition(5, Set.of(p6, p7));
	//	var t6 = new Transition(6, Set.of(p8));
	//	var p9 = new Place(9, Set.of(t5));
	//	t6.postSet().add(p5);
	//	p5.preSet().add(t6);
	//	return new Net(new Marking(Map.of(p0, 1, p1, 1, p2, 1)));
	//}

	Net aoc22_19() {
		Variable
				O = new Variable("O"),
				C = new Variable("C"),
				S = new Variable("S"),
				G = new Variable("G"),
				O_ = new Variable("O'"),
				C_ = new Variable("C'"),
				S_ = new Variable("S'"),
				G_ = new Variable("G'"),
				o = new Variable("o"),
				c = new Variable("c"),
				s = new Variable("s"),
				g = new Variable("g"),
				o_ = new Variable("o'"),
				c_ = new Variable("c'"),
				s_ = new Variable("s'"),
				g_ = new Variable("g'"),
				x = new Variable("x"),
				v = new Variable("v");
		Place
				oreMiner = new Place(1, "OreMiner"),
				playMiner = new Place(2, "ClayMiner"),
				obsidianMiner = new Place(3, "ObsidianMiner"),
				geodeMiner = new Place(4, "GeodeMiner"),
				ore = new Place(5, "Ore"),
				clay = new Place(6, "Clay"),
				obsidian = new Place(7, "Obsidian"),
				geodes = new Place(8, "Geode"),
				r = new Place(9, "ready"),
				i = new Place(10, "inter");
		Transition
				t = new Transition(1, "t",
				o_.eq(o.plus(O))
						.and(c_.eq(c.plus(C)))
						.and(s_.eq(s.plus(S)))
						.and(g_.eq(g.plus(G)))
		),
				mo = new Transition(2, "MakeOreMiner",
						O_.eq(O.plus(1))
								.and(o.geq(4))
								.and(o_.eq(o.plus(4)))
				),
				mc = new Transition(3, "MakeClayMiner",
						C_.eq(C.plus(1))
								.and(o.geq(2))
								.and(o_.eq(o.minus(2)))
				),
				ms = new Transition(4, "MakeObsidianMiner",
						S_.eq(S.plus(1))
								.and(o.geq(3))
								.and(o_.eq(o.minus(3)))
								.and(c.geq(14))
								.and(c_.eq(c.minus(14)))
				),
				mg = new Transition(5, "MakeGeodeMiner",
						G_.eq(G.plus(1))
								.and(o.geq(2))
								.and(o_.eq(o.minus(2)))
								.and(s.geq(7))
								.and(s_.eq(s.minus(7)))
				),
				skip = new Transition(6, "Skip");
		// t
		link(oreMiner, O, t);
		link(t, O, oreMiner);
		link(ore, o, t);
		link(t, o_, ore);

		link(playMiner, C, t);
		link(t, C, playMiner);
		link(clay, c, t);
		link(t, c_, clay);

		link(obsidianMiner, S, t);
		link(t, S, obsidianMiner);
		link(obsidian, s, t);
		link(t, s_, obsidian);

		link(geodeMiner, G, t);
		link(t, G, geodeMiner);
		link(geodes, g, t);
		link(t, g_, geodes);

		// Make Ore Miner
		link(ore, o, mo);
		link(mo, o_, ore);
		link(oreMiner, O, mo);
		link(mo, O_, oreMiner);

		// Make Clay Miner
		link(ore, o, mc);
		link(mc, o_, ore);
		link(playMiner, C, mc);
		link(mc, C_, playMiner);

		// Make Obsidian Miner
		link(ore, o, ms);
		link(ms, o_, ore);
		link(clay, c, ms);
		link(ms, c_, clay);
		link(obsidianMiner, S, ms);
		link(ms, S_, obsidianMiner);

		// Make Geode Miner
		link(ore, o, mg);
		link(mg, o_, ore);
		link(obsidian, s, mg);
		link(mg, s_, obsidian);
		link(geodeMiner, G, mg);
		link(mg, G_, geodeMiner);

		// clock
		link(r, v, mo);
		link(r, v, mc);
		link(r, v, ms);
		link(r, v, mg);
		link(r, v, skip);

		link(mo, v, i);
		link(mc, v, i);
		link(ms, v, i);
		link(mg, v, i);
		link(skip, v, i);

		link(i, v, t);
		link(t, v, r);

		return new Net(new Marking(Map.of(
				oreMiner, 1,
				playMiner, 0,
				obsidianMiner, 0,
				geodeMiner, 0,
				ore, 0,
				clay, 0,
				obsidian, 0,
				geodes, 0,
				r, 1
		)));
	}

	void renderAndClip(Net net) throws IOException {
		try (StringWriter stringWriter = new StringWriter()) {
			net.render(stringWriter);
			System.out.println(stringWriter);
			Process clip = new ProcessBuilder("xclip", "-sel", "clip").start();
			try (var out = clip.outputWriter()) {
				out.append(stringWriter.toString()).flush();
			}
		}
	}

	void renderAndClip(Unfolding unf) throws IOException {
		try (StringWriter stringWriter = new StringWriter()) {
			unf.render(stringWriter);
			System.out.println(stringWriter);
			System.out.println(stringWriter.toString().lines().count() - 2 + " lines");
			Process clip = new ProcessBuilder("xclip", "-sel", "clip").start();
			try (var out = clip.outputWriter()) {
				out.append(stringWriter.toString()).flush();
			}
		}
	}

	private static void link(Place p, Transition t) {
		p.postSet().add(t);
		t.preSet().put(p, VAR);
	}

	private static void link(Place p, Variable variable, Transition t) {
		p.postSet().add(t);
		t.preSet().put(p, variable);
	}

	private static void link(Place p, Transition... tt) {
		for (Transition t : tt) {
			link(p, t);
		}
	}

	private static void link(Transition t, Place p) {
		t.postSet().put(p, VAR);
		p.preSet().add(t);
	}

	private static void link(Transition t, Variable variable, Place p) {
		t.postSet().put(p, variable);
		p.preSet().add(t);
	}

	private static void link(Transition t, Place... pp) {
		for (Place p : pp) {
			link(t, p);
		}
	}

	private static void link(Place from, Transition over, Place to) {
		link(from, over);
		link(over, to);
	}

	private static void link(Transition from, Place over, Transition to) {
		link(from, over);
		link(over, to);
	}
}
