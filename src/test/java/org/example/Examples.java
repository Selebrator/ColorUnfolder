package org.example;

import org.example.components.Place;
import org.example.components.Predicate;
import org.example.components.Transition;
import org.example.components.Variable;
import org.example.logic.generic.expression.CalculationExpression;
import org.example.logic.generic.expression.ConstantExpression;
import org.example.logic.generic.formula.ComparisonFormula;
import org.example.net.Marking;
import org.example.net.Net;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.example.logic.generic.CalculationOperator.MINUS;
import static org.example.logic.generic.CalculationOperator.PLUS;
import static org.example.logic.generic.ComparisonOperator.*;

public class Examples {

	static Variable VAR = new Variable("⊻");

	@Test
	void example() throws IOException {
		//Variable
		//		x = new Variable("x"),
		//		y = new Variable("y"),
		//		z = new Variable("z");
		//Place
		//		p1 = new Place(1),
		//		p2 = new Place(2),
		//		p3 = new Place(3);
		//Transition
		//		t = new Transition(1, new Predicate(ComparisonFormula.of(x, EQUALS, ConstantExpression.of(1)).and(ComparisonFormula.of(z, EQUALS, y))));
		//link(p1, t, x);
		//link(p2, t, y);
		//link(t, p3, z);
		//Net net = new Net(new Marking(Map.of(p1, 1, p2, 0)));
		Net net = colorConflict();
		renderAndClip(net);
		renderAndClip(Unfolding.unfold(net, 7, false));
		renderAndClip(Unfolding.unfold(net, 7, true));
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
		Transition
				t1 = new Transition(1),
				t2 = new Transition(2),
				t3 = new Transition(3),
				t4 = new Transition(4),
				t5 = new Transition(5),
				t6 = new Transition(6),
				t7 = new Transition(7),
				t8 = new Transition(8),
				t9 = new Transition(9),
				t10 = new Transition(10),
				t11 = new Transition(11),
				t12 = new Transition(12),
				t13 = new Transition(13),
				t14 = new Transition(14),
				t15 = new Transition(15),
				t16 = new Transition(16),
				t17 = new Transition(17),
				t18 = new Transition(18),
				t19 = new Transition(19),
				t20 = new Transition(20),
				t21 = new Transition(21),
				t22 = new Transition(22),
				t23 = new Transition(23),
				t24 = new Transition(24),
				t25 = new Transition(25),
				t26 = new Transition(26),
				t27 = new Transition(27),
				t28 = new Transition(28),
				t29 = new Transition(29),
				t30 = new Transition(30),
				t31 = new Transition(31),
				t32 = new Transition(32),
				t33 = new Transition(33),
				t34 = new Transition(34),
				t35 = new Transition(35),
				t36 = new Transition(36),
				t37 = new Transition(37),
				t38 = new Transition(38),
				t39 = new Transition(39),
				t40 = new Transition(40),
				t41 = new Transition(41);

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

		return new Net(new Marking(Map.of(p4, 1, p6, 1, p9, 1, p11, 1)));
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

	Net wurdemann_example_1() {
		Variable
				k = new Variable("k"),
				l = new Variable("l"),
				ll = new Variable("l'");
		Place
				p1 = new Place(1),
				p2 = new Place(2),
				p3 = new Place(3);
		Transition
				t1 = new Transition(1, new Predicate(ComparisonFormula.of(l, EQUALS, ConstantExpression.of(1)))),
				t2 = new Transition(2, new Predicate(ComparisonFormula.of(ll, NOT_EQUALS, ConstantExpression.of(0)))),
				t3 = new Transition(3, new Predicate(ComparisonFormula.of(k, EQUALS, ConstantExpression.of(0))));

		link(p1, t1, k);
		link(t1, p2, l);
		link(p2, t2, l);
		link(p2, t3, l);
		link(t2, p2, ll);
		link(t3, p3, k);
		return new Net(new Marking(Map.of(p1, 0)));
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
				t1 = new Transition(1, new Predicate(ComparisonFormula.of(y, GREATER_THEN, ConstantExpression.of(0))
				.and(ComparisonFormula.of(z, LESS_THEN, ConstantExpression.of(0))))),
				t2 = new Transition(2, new Predicate(ComparisonFormula.of(x, NOT_EQUALS, ConstantExpression.of(0)))),
				t3 = new Transition(3),
				t4 = new Transition(4);
		link(p1, t1, x);
		link(t1, p2, y);
		link(t1, p3, z);
		link(p2, t2, x);
		link(t2, p4, x);
		link(p3, t4, x);
		link(t4, p6, x);
		link(p2, t3, x);
		link(p3, t3, x);
		link(t3, p5, x);
		return new Net(new Marking(Map.of(p1, 1)));
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
		ConstantExpression<Variable> _1 = ConstantExpression.of(1);
		ConstantExpression<Variable> _2 = ConstantExpression.of(2);
		ConstantExpression<Variable> _3 = ConstantExpression.of(3);
		ConstantExpression<Variable> _4 = ConstantExpression.of(4);
		ConstantExpression<Variable> _7 = ConstantExpression.of(7);
		ConstantExpression<Variable> _14 = ConstantExpression.of(14);
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
				t = new Transition(1, "t", new Predicate(
				ComparisonFormula.of(o_, EQUALS, CalculationExpression.of(o, PLUS, O))
						.and(ComparisonFormula.of(c_, EQUALS, CalculationExpression.of(c, PLUS, C)))
						.and(ComparisonFormula.of(s_, EQUALS, CalculationExpression.of(s, PLUS, S)))
						.and(ComparisonFormula.of(g_, EQUALS, CalculationExpression.of(g, PLUS, G)))
		)),
				mo = new Transition(2, "MakeOreMiner", new Predicate(
						ComparisonFormula.of(O_, EQUALS, CalculationExpression.of(O, PLUS, _1))
								.and(ComparisonFormula.of(o, GREATER_EQUALS, _4))
								.and(ComparisonFormula.of(o_, EQUALS, CalculationExpression.of(o, MINUS, _4)))
				)),
				mc = new Transition(3, "MakeClayMiner", new Predicate(
						ComparisonFormula.of(C_, EQUALS, CalculationExpression.of(C, PLUS, _1))
								.and(ComparisonFormula.of(o, GREATER_EQUALS, _2))
								.and(ComparisonFormula.of(o_, EQUALS, CalculationExpression.of(o, MINUS, _2)))
				)),
				ms = new Transition(4, "MakeObsidianMiner", new Predicate(
						ComparisonFormula.of(S_, EQUALS, CalculationExpression.of(S, PLUS, _1))
								.and(ComparisonFormula.of(o, GREATER_EQUALS, _3))
								.and(ComparisonFormula.of(o_, EQUALS, CalculationExpression.of(o, MINUS, _3)))
								.and(ComparisonFormula.of(c, GREATER_EQUALS, _14))
								.and(ComparisonFormula.of(c_, EQUALS, CalculationExpression.of(c, MINUS, _14)))
				)),
				mg = new Transition(5, "MakeGeodeMiner", new Predicate(
						ComparisonFormula.of(G_, EQUALS, CalculationExpression.of(G, PLUS, _1))
								.and(ComparisonFormula.of(o, GREATER_EQUALS, _2))
								.and(ComparisonFormula.of(o_, EQUALS, CalculationExpression.of(o, MINUS, _2)))
								.and(ComparisonFormula.of(s, GREATER_EQUALS, _7))
								.and(ComparisonFormula.of(s_, EQUALS, CalculationExpression.of(s, MINUS, _7)))
				)),
				skip = new Transition(6, "Skip");
		// t
		link(oreMiner, t, O);
		link(t, oreMiner, O);
		link(ore, t, o);
		link(t, ore, o_);

		link(playMiner, t, C);
		link(t, playMiner, C);
		link(clay, t, c);
		link(t, clay, c_);

		link(obsidianMiner, t, S);
		link(t, obsidianMiner, S);
		link(obsidian, t, s);
		link(t, obsidian, s_);

		link(geodeMiner, t, G);
		link(t, geodeMiner, G);
		link(geodes, t, g);
		link(t, geodes, g_);

		// Make Ore Miner
		link(ore, mo, o);
		link(mo, ore, o_);
		link(oreMiner, mo, O);
		link(mo, oreMiner, O_);

		// Make Clay Miner
		link(ore, mc, o);
		link(mc, ore, o_);
		link(playMiner, mc, C);
		link(mc, playMiner, C_);

		// Make Obsidian Miner
		link(ore, ms, o);
		link(ms, ore, o_);
		link(clay, ms, c);
		link(ms, clay, c_);
		link(obsidianMiner, ms, S);
		link(ms, obsidianMiner, S_);

		// Make Geode Miner
		link(ore, mg, o);
		link(mg, ore, o_);
		link(obsidian, mg, s);
		link(mg, obsidian, s_);
		link(geodeMiner, mg, G);
		link(mg, geodeMiner, G_);

		// clock
		link(r, mo, v);
		link(r, mc, v);
		link(r, ms, v);
		link(r, mg, v);
		link(r, skip, v);

		link(mo, i, v);
		link(mc, i, v);
		link(ms, i, v);
		link(mg, i, v);
		link(skip, i, v);

		link(i, t, v);
		link(t, r, v);

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

	private static void link(Place p, Transition t, Variable variable) {
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

	private static void link(Transition t, Place p, Variable variable) {
		t.postSet().put(p, variable);
		p.preSet().add(t);
	}

	private static void link(Transition t, Place... pp) {
		for (Place p : pp) {
			link(t, p);
		}
	}
}
