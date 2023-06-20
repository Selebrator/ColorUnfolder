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
		renderAndClip(Unfolding.unfold(colorConflict(), 5));
		//Solver solver = new Solver();
		//solver.setOption("produce-models", "true"); // Produce Models
		//Sort integer = solver.getIntegerSort();
		//Term x = solver.mkConst(integer, "x");
		//Term y = solver.mkConst(integer, "y");
		//Term z = solver.mkConst(integer, "z");
		//Term zero = solver.mkInteger(0);
		//Term yG0 = solver.mkTerm(Kind.GT, y, zero);
		//Term zL0 = solver.mkTerm(Kind.LT, z, zero);
		//Term xEy = solver.mkTerm(Kind.EQUAL, x, y);
		//Term xEz = solver.mkTerm(Kind.EQUAL, x, z);
		//Term term = solver.mkTerm(Kind.AND, new Term[]{yG0, zL0, xEy, xEz});
		//System.out.println(term);
		//solver.assertFormula(term);
		//Result result = solver.checkSat();
		//System.out.println(result);
		//System.out.println("x = " + solver.getValue(x));
		//System.out.println("y = " + solver.getValue(y));
		//System.out.println("z = " + solver.getValue(z));

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
				p6 = new Place(6),
				p7 = new Place(7),
				p8 = new Place(8);
		Transition
				t1 = new Transition(1, new Predicate(ComparisonFormula.of(y, GREATER_THEN, ConstantExpression.of(0))
				.and(ComparisonFormula.of(z, LESS_THEN, ConstantExpression.of(0))))),
				t2 = new Transition(2),
				t3 = new Transition(3),
				t4 = new Transition(4),
				t5 = new Transition(5),
				t6 = new Transition(6);
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
		return new Net(new Marking(Map.of(p1, 0)));
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
