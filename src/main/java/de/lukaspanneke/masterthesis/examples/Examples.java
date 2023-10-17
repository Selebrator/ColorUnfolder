package de.lukaspanneke.masterthesis.examples;

import de.lukaspanneke.masterthesis.logic.*;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.lukaspanneke.masterthesis.logic.QuantifiedFormula.Quantifier.EXISTS;

public class Examples {

	public static Transition newTransition(
			int index,
			String name,
			Map<Place, Variable> preSet,
			Map<Place, Variable> postSet,
			Formula guard
	) {
		Transition trans = new Transition(index, name, preSet, postSet, guard);
		preSet.forEach((place, variable) -> place.postSet().add(trans));
		postSet.forEach((place, variable) -> place.preSet().add(trans));
		trans.validate();
		return trans;
	}

	public static void link(Place p, Variable variable, Transition t) {
		p.postSet().add(t);
		t.preSet().put(p, variable);
	}

	public static void link(Transition t, Variable variable, Place p) {
		t.postSet().put(p, variable);
		p.preSet().add(t);
	}

	public static Net isqrt(int i) {
		int p = 1;
		Place
				p1 = new Place(p++, "p1"),
				p2 = new Place(p++, "p2"),
				p3 = new Place(p++, "p3"),
				p_s = new Place(p++, "p_s"),
				p_x0 = new Place(p++, "p_x0"),
				p_x1 = new Place(p++, "p_x1"),
				p_x = new Place(p++, "p_x"),
				p_z = new Place(p++, "p_z");

		Variable
				s = new Variable("s", FiniteDomain.fullRange(i, i)),
				x0 = new Variable("x0", FiniteDomain.fullRange(0, i)),
				x1 = new Variable("x1", FiniteDomain.fullRange(0, i)),
				r = new Variable("r", FiniteDomain.fullRange(0, i)),
				x = new Variable("x", FiniteDomain.fullRange(0, i)),
				z = new Variable("z"),
				token = new Variable("token", FiniteDomain.fullRange(1, 1));
		int t = 1;
		newTransition(t++, "guess",
				Map.of(
						p1, token,
						p_s, s
				),
				Map.of(
						p2, token,
						p_s, s,
						p_x0, x0
				),
				QuantifiedFormula.of(EXISTS, Set.of(r), r.geq(0).and(r.lt(2)).and(s.eq(x0.times(2).plus(r))))
		);
		newTransition(t++, "update1",
				Map.of(
						p2, token,
						p_s, s,
						p_x0, x0
				),
				Map.of(
						p_s, s,
						p_x0, x0,
						p_x, x
				),
				QuantifiedFormula.of(EXISTS, Set.of(r), r.geq(0).and(r.lt(x0)).and(x0.times(x.minus(x0)).plus(r).eq(s)))
		);
		newTransition(t++, "update2",
				Map.of(
						p_x, x
				),
				Map.of(
						p3, token,
						p_x1, x1
				),
				QuantifiedFormula.of(EXISTS, Set.of(r), r.geq(0).and(r.lt(2)).and(x1.times(2).plus(r).eq(x)))
		);
		newTransition(t++, "check",
				Map.of(
						p3, token,
						p_x0, x0,
						p_x1, x1
				),
				Map.of(
						p_x0, x1, // not a typo: x0 gets the value of x1.
						p_z, z
				),
				z.eq(x0.minus(x1))
		);
		newTransition(t++, "loop",
				Map.of(p_z, z),
				Map.of(p2, token),
				z.gt(0)
		);
		newTransition(t++, "return_x0",
				Map.of(
						p_z, z,
						p_x0, x0
				),
				Map.of(),
				z.leq(0)
		);

		return new Net(new Marking(Map.of(p1, 1, p_s, i)));
	}

	public static Net gcd(int a0, int b0) {
		Place p1 = new Place(1);
		Place p2 = new Place(2);
		Place p3 = new Place(3);
		Variable a = new Variable("a", FiniteDomain.fullRange(0, a0));
		Variable A = new Variable("a'", FiniteDomain.fullRange(0, a0));
		Variable b = new Variable("b", FiniteDomain.fullRange(0, b0));
		Variable B = new Variable("b'", FiniteDomain.fullRange(0, b0));
		Variable q = new Variable("q", FiniteDomain.fullRange(0, a0));
		newTransition(1, "step",
				Map.of(p1, a, p2, b),
				Map.of(p1, A, p2, B),
				a.neq(0).and(A.eq(b)).and(B.geq(0)).and(B.lt(b)).and(QuantifiedFormula.of(EXISTS, Set.of(q), b.times(q).plus(B).eq(a)))
		);
		newTransition(2, "end",
				Map.of(p1, a, p2, b),
				Map.of(p3, a),
				b.eq(0)
		);
		return new Net(new Marking(Map.of(p1, a0, p2, b0)));
	}

	public static Net restaurant() {
		int p = 1;
		Place
				pendingOder = new Place(p++, "pending_order"),
				timer = new Place(p++, "timer"),
				cooking = new Place(p++, "cooking"),
				foodReady = new Place(p++, "food_ready"),
				emptyTable = new Place(p++, "empty_table"),
				preparingToOrder = new Place(p++, "preparing_to_order"),
				waitingForFood = new Place(p++, "waiting_for_food"),
				doneEating = new Place(p++, "done_eating");

		Variable
				time = new Variable("t"),
				nextTime = new Variable("t'"),
				foodItem = new Variable("o", FiniteDomain.fullRange(1, 3)),
				token = new Variable("token", FiniteDomain.fullRange(1, 1));

		int t = 1;
		newTransition(t++, "forward_oder_to_kitchen",
				Map.of(pendingOder, foodItem),
				Map.of(
						timer, time,
						cooking, foodItem
				),
				foodItem.eq(1).and(time.eq(20))
						.or(foodItem.eq(2).and(time.eq(15)))
						.or(foodItem.eq(3).and(time.eq(15)))
		);
		newTransition(t++, "wait_cooking",
				Map.of(timer, time),
				Map.of(timer, nextTime),
				time.gt(0).and(nextTime.lt(time).and(nextTime.geq(0)))
		);
		newTransition(t++, "done_cooking",
				Map.of(
						timer, time,
						cooking, foodItem
				),
				Map.of(foodReady, foodItem),
				time.eq(0)
		);
		newTransition(t++, "enter",
				Map.of(emptyTable, token),
				Map.of(preparingToOrder, token),
				Formula.top()
		);
		newTransition(t++, "order",
				Map.of(preparingToOrder, token),
				Map.of(
						pendingOder, foodItem,
						waitingForFood, token
				),
				Formula.top()
		);
		newTransition(t++, "eat",
				Map.of(waitingForFood, token, foodReady, foodItem),
				Map.of(doneEating, token),
				Formula.top()
		);
		newTransition(t++, "leave",
				Map.of(doneEating, token),
				Map.of(emptyTable, token),
				Formula.top()
		);
		return new Net(new Marking(Map.of(emptyTable, 1)));
	}

	public static Net fastGrowing(int n, int m) {
		Domain domain = FiniteDomain.fullRange(1, m);
		Variable x = new Variable("x", domain);
		Variable y = new Variable("y", domain);
		Transition t = new Transition(0);
		Map<Place, Integer> initial = new HashMap<>();
		int p_idx = 1;
		for (int t_idx = 1; t_idx <= n; t_idx++) {
			Transition transition = new Transition(t_idx);
			Place pre = new Place(p_idx++, "a" + t_idx);
			initial.put(pre, 1);
			Place post = new Place(p_idx++, "b" + t_idx);
			Variable in = new Variable("x" + t_idx);
			Variable out = new Variable("y" + t_idx);
			link(pre, x, transition);
			link(transition, y, post);
			link(post, in, t);
			link(t, out, post);
		}
		return new Net(new Marking(initial));
	}

	public static Net parallelAmnesia(int n) {
		int p = 1;
		int t = 1;
		Variable v = new Variable("v");
		Variable x = new Variable("x");
		Variable y = new Variable("y");
		Place s = new Place(p++);
		Transition init = new Transition(t++);
		link(s, v, init);
		for (int i = 1; i <= n; i++) {
			Place before = new Place(p++);
			Place after = new Place(p++);
			Transition trans = new Transition(t++);
			Variable variable = new Variable("v" + i);
			link(init, variable, before);
			link(before, x, trans);
			link(trans, y, after);
		}
		return new Net(new Marking(Map.of(s, 1)));
	}

	public static Net independentDiamond(int n) {
		int p = 1;
		int t = 1;
		Variable v = new Variable("v");
		Place s = new Place(p++);
		Transition init = new Transition(t++);
		Transition after = new Transition(t++);
		link(s, v, init);
		for (int i = 1; i <= n; i++) {
			Place pi = new Place(p++);
			Variable vi = new Variable("v" + i);
			link(init, vi, pi);
			link(pi, vi, after);
		}
		return new Net(new Marking(Map.of(s, 1)));
	}

	/*
	 * A riddle: you have two buckets. They can hold 3 units and 5 units.
	 * Fill one bucket with 4 Units.
	 * Input for that version is max = { 3, 5 }, goal = 4.
	 *
	 * Allows any number of buckets of any size.
	 */
	public static Net buckets(int[] max, int goal) {
		Place[] bucket = new Place[max.length];
		Domain[] domain = new Domain[max.length];
		Variable[] in = new Variable[max.length];
		Variable[] out = new Variable[max.length];

		int p = 1;
		int t = 1;
		for (int i = 0; i < max.length; i++) {
			bucket[i] = new Place(p++, "b" + i);
			domain[i] = FiniteDomain.fullRange(0, max[i]);
			in[i] = new Variable("i" + i, domain[i]);
			out[i] = new Variable("o" + i, domain[i]);
			newTransition(t++, "tap_b" + i, Map.of(bucket[i], in[i]), Map.of(bucket[i], out[i]), out[i].eq(max[i]));
			newTransition(t++, "drain_b" + i, Map.of(bucket[i], in[i]), Map.of(bucket[i], out[i]), out[i].eq(0));
		}

		int[] indices = IntStream.range(0, max.length).toArray();
		for (int from : indices) {
			for (int to : indices) {
				if (from == to) {
					continue;
				}
				newTransition(t++, "b" + from + "_to_b" + to,
						Map.of(bucket[from], in[from], bucket[to], in[to]),
						Map.of(bucket[from], out[from], bucket[to], out[to]),
						out[from].eq(0).and(out[to].eq(in[from].plus(in[to])))
								.or(out[to].eq(max[to]).and(out[from].eq(in[from].minus(out[to].minus(in[to]))))));
			}
		}

		newTransition(0, "goal",
				IntStream.range(0, max.length).boxed().collect(Collectors.toMap(i -> bucket[i], i -> in[i])),
				Map.of(),
				IntStream.range(0, max.length).mapToObj(i -> in[i].eq(goal)).collect(Formula.or()));

		return new Net(new Marking(IntStream.range(0, max.length).boxed().collect(Collectors.toMap(i -> bucket[i], i -> 0))));
	}

	/**
	 * This version of the mastermind net is takes a code and a guess and computes the judgement (red and white pins).
	 * This net can handle the same color used in multiple positions.
	 *
	 * @param c code
	 * @param g guess
	 */
	public static Net mastermind(int[] c, int[] g) {
		if (c.length != g.length) {
			throw new IllegalArgumentException();
		}
		int n = c.length;
		int p = 1;
		int t = 1;
		Place[] original_code = new Place[n];
		Place[] code = new Place[n];
		Place[] guess = new Place[n];
		Place[] pre_red = new Place[n];
		Place[] pre_white = new Place[n];
		Place[] pos_red = new Place[n];
		Place[] pos_white = new Place[n];
		Place done = new Place(p++, "done");
		Place red_pins = new Place(p++, "red_pins");
		Place white_pins = new Place(p++, "white_pins");
		Variable token = new Variable("t", FiniteDomain.fullRange(0, 0));
		Variable vx = new Variable("x");
		Variable vy = new Variable("y");
		Variable[] x = new Variable[n];
		Variable vi = new Variable("i", FiniteDomain.fullRange(0, n));
		Variable v_i = new Variable("i'", FiniteDomain.fullRange(0, n));

		for (int i = 0; i < n; i++) {
			original_code[i] = new Place(p++, "original_code_" + i);
			guess[i] = new Place(p++, "guess_" + i);
			code[i] = new Place(p++, "code_" + i);
			pre_red[i] = new Place(p++, "pre_red_" + i);
			pre_white[i] = new Place(p++, "pre_white_" + i);
			pos_red[i] = new Place(p++, "pos_red_" + i);
			pos_white[i] = new Place(p++, "pos_white_" + i);
			x[i] = new Variable("x_" + i, FiniteDomain.fullRange(0, 8));
		}

		{
			Transition copy = new Transition(t++, "copy");
			for (int i = 0; i < n; i++) {
				link(original_code[i], x[i], copy);
				link(copy, x[i], code[i]);
			}
		}

		for (int i = 0; i < n; i++) {
			newTransition(t++, "match_" + i,
					Map.of(pre_red[i], token, red_pins, vi, code[i], vx, guess[i], vx),
					Map.of(pos_red[i], token, red_pins, v_i, code[i], vy),
					v_i.eq(vi.plus(1)).and(vy.eq(0)));
			newTransition(t++, "no_match_" + i,
					Map.of(pre_red[i], token, code[i], vx, guess[i], vy),
					Map.of(pos_red[i], token, code[i], vx, guess[i], vy),
					vx.neq(vy));
		}

		newTransition(t++, "phase_red_white",
				IntStream.range(0, n).boxed().collect(Collectors.toMap(i -> pos_red[i], i -> token)),
				IntStream.range(0, n).boxed().collect(Collectors.toMap(i -> pre_white[i], i -> token)),
				Formula.top());

		for (int ii = 0; ii < n; ii++) {
			int i = ii;
			for (int j = 0; j < n; j++) {
				newTransition(t++, "partial_" + i + "_" + j,
						Map.of(pre_white[i], token, white_pins, vi, code[j], vx, guess[i], vx),
						Map.of(pos_white[i], token, white_pins, v_i, code[j], vy),
						v_i.eq(vi.plus(1)).and(vy.eq(0)));
			}

			Map<Place, Variable> pre = IntStream.range(0, n).filter(j -> j != i).boxed().collect(Collectors.toMap(j -> code[j], j -> x[j]));
			pre.put(pre_white[i], token);
			pre.put(guess[i], vy);

			Map<Place, Variable> post = IntStream.range(0, n).filter(j -> j != i).boxed().collect(Collectors.toMap(j -> code[j], j -> x[j]));
			post.put(pos_white[i], token);

			newTransition(t++, "no_partial_" + i,
					pre,
					post,
					IntStream.range(0, n).filter(j -> j != i).mapToObj(j -> x[j].neq(vy)).collect(Formula.and()));

			newTransition(t++, "skip_" + i,
					Map.of(pre_white[i], token, code[i], vx),
					Map.of(pos_white[i], token, code[i], vx),
					vx.eq(0));
		}

		newTransition(t++, "phase_white_end",
				IntStream.range(0, n).boxed().collect(Collectors.toMap(i -> pos_white[i], i -> token)),
				Map.of(done, token),
				Formula.top());

		for (int r = 0; r <= n; r++) {
			for (int w = 0; w <= n; w++) {
				newTransition(t++, "goal_" + r + "_" + w, Map.of(red_pins, vx, white_pins, vy, done, token), Map.of(), vx.eq(r).and(vy.eq(w)));
			}
		}

		Map<Place, Integer> marking = new HashMap<>();
		for (int i = 0; i < n; i++) {
			marking.put(original_code[i], c[i]);
			marking.put(guess[i], g[i]);
			marking.put(pre_red[i], 0);
		}
		marking.put(red_pins, 0);
		marking.put(white_pins, 0);

		return new Net(new Marking(marking));
	}

	/**
	 * This version of the mastermind net chooses all codes
	 * and then plays many rounds as the guesser, taking all guesses every turn.
	 * In this net all positions (must) have distinct colors.
	 */
	public static Net mastermindNoDuplicateColors(int codeLength, int colors, int guesses) {
		int p = 1;
		int t = 1;

		Domain colorDomain = FiniteDomain.fullRange(1, colors);
		Domain judgeDomain = FiniteDomain.fullRange(0, codeLength);

		Place preCode = new Place(p++, "preCode");
		Place preGuess = new Place(p++, "preGuess");

		Place remainingGuesses = new Place(p++, "remaining_guesses");
		Variable remaining = new Variable("remaining");
		Variable remainingNext = new Variable("remaining'");

		Place pointsExact = new Place(p++, "red");
		Place pointsPartial = new Place(p++, "white");
		Variable exact = new Variable("red", judgeDomain);
		Variable partial = new Variable("white", judgeDomain);

		Variable token = new Variable("token", FiniteDomain.fullRange(1, 1));

		Variable[] c = new Variable[codeLength];
		Variable[] g = new Variable[codeLength];
		Place[] code = new Place[codeLength];
		Place[] guess = new Place[codeLength];
		for (int i = 0; i < codeLength; i++) {
			c[i] = new Variable("c" + (i + 1), colorDomain);
			g[i] = new Variable("g" + (i + 1), colorDomain);
			code[i] = new Place(p++, "code" + (i + 1));
			guess[i] = new Place(p++, "guess" + (i + 1));
		}

		newTransition(t++, "pick_code",
				Map.of(preCode, token),
				IntStream.range(0, codeLength)
						.boxed()
						.collect(Collectors.toMap(i -> code[i], i -> c[i])),
				IntStream.range(0, codeLength)
						.boxed()
						.flatMap(i -> IntStream.range(i + 1, codeLength)
								.mapToObj(j -> c[i].neq(c[j])))
						.collect(Formula.and())
		);

		newTransition(t++, "pick_guess",
				Map.of(preGuess, token),
				IntStream.range(0, codeLength)
						.boxed()
						.collect(Collectors.toMap(i -> guess[i], i -> g[i])),
				IntStream.range(0, codeLength)
						.boxed()
						.flatMap(i -> IntStream.range(i + 1, codeLength)
								.mapToObj(j -> g[i].neq(g[j])))
						.collect(Formula.and())
		);

		{
			Map<Place, Variable> post = IntStream.range(0, codeLength)
					.boxed()
					.collect(Collectors.toMap(i -> code[i], i -> c[i]));
			post.put(pointsExact, exact);
			post.put(pointsPartial, partial);
			Formula sumExact = IntStream.range(0, codeLength)
					.mapToObj(i -> c[i].eq(g[i]).asArithmetic())
					.reduce(ArithmeticExpression::plus)
					.map(exact::eq)
					.orElseGet(Formula::top);
			Formula sumPartial = IntStream.range(0, codeLength)
					.boxed()
					.flatMap(i -> IntStream.range(0, codeLength)
							.filter(j -> i != j)
							.mapToObj(j -> c[i].eq(g[j]).asArithmetic()))
					.reduce(ArithmeticExpression::plus)
					.map(partial::eq)
					.orElseGet(Formula::top);
			newTransition(t++, "judge",
					IntStream.range(0, codeLength)
							.boxed()
							.flatMap(i -> Stream.of(Map.entry(code[i], c[i]), Map.entry(guess[i], g[i])))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
					post,
					sumExact.and(sumPartial)
			);
		}

		newTransition(t++, "next",
				Map.of(pointsExact, exact, pointsPartial, partial, remainingGuesses, remaining),
				Map.of(remainingGuesses, remainingNext, preGuess, token),
				exact.neq(codeLength).and(remaining.gt(0).and(remainingNext.eq(remaining.minus(1))))
		);

		return new Net(new Marking((Map.of(preCode, 1, preGuess, 1, remainingGuesses, guesses))));
	}

	public static Net hobbitsAndOrcs(int groupSize, int boatCapacity, int nIslands) {
		// https://en.wikipedia.org/wiki/Missionaries_and_cannibals_problem
		int nHobbits = groupSize;
		int nOrcs = nHobbits;
		int p = 1;
		int t = 1;

		Variable token = new Variable("token", FiniteDomain.fullRange(1, 1));
		Variable landHobbits = new Variable("a", FiniteDomain.fullRange(0, nHobbits));
		Variable landOrcs = new Variable("b", FiniteDomain.fullRange(0, nOrcs));
		Variable landHobbitsNew = new Variable("a'", FiniteDomain.fullRange(0, nHobbits));
		Variable landOrcsNew = new Variable("b'", FiniteDomain.fullRange(0, nOrcs));
		Variable vBoatHobbits = new Variable("x", FiniteDomain.fullRange(0, nHobbits));
		Variable vBoatOrcs = new Variable("y", FiniteDomain.fullRange(0, nOrcs));

		Place[] boatHobbits = new Place[nIslands];
		Place[] boatOrcs = new Place[nIslands];
		Place[] boatEmpty = new Place[nIslands];
		Place[] hobbits = new Place[nIslands];
		Place[] orcs = new Place[nIslands];
		for (int i = 0; i < nIslands; i++) {
			boatHobbits[i] = new Place(p++, "boatHobbits_" + i);
			boatOrcs[i] = new Place(p++, "boatOrcs_" + i);
			boatEmpty[i] = new Place(p++, "boat_empty_" + i);
			hobbits[i] = new Place(p++, "hobbits_" + i);
			orcs[i] = new Place(p++, "orcs_" + i);
		}

		for (int b = 0; b < nIslands - 1; b++) {
			for (int i = 0; i <= 1; i++) {
				int from = b + i;
				int to = b + (1 - i);
				newTransition(t++, "load_" + from + "_" + to,
						Map.of(boatEmpty[from], token, hobbits[from], landHobbits, orcs[from], landOrcs),
						Map.of(hobbits[from], landHobbitsNew, orcs[from], landOrcsNew, boatHobbits[from], vBoatHobbits, boatOrcs[from], vBoatOrcs),
						landHobbitsNew.eq(landHobbits.minus(vBoatHobbits))
								.and(landOrcsNew.eq(landOrcs.minus(vBoatOrcs)))
								.and(vBoatHobbits.plus(vBoatOrcs).geq(1))
								.and(vBoatHobbits.plus(vBoatOrcs).leq(boatCapacity))
								.and(landHobbitsNew.eq(0).or(landHobbitsNew.geq(landOrcsNew)))
								.and(vBoatHobbits.eq(0).or(vBoatHobbits.geq(vBoatOrcs)))
				);
				newTransition(t++, "unload_" + from + "_" + to,
						Map.of(hobbits[to], landHobbits, orcs[to], landOrcs, boatHobbits[from], vBoatHobbits, boatOrcs[from], vBoatOrcs),
						Map.of(boatEmpty[to], token, hobbits[to], landHobbitsNew, orcs[to], landOrcsNew),
						landHobbitsNew.eq(landHobbits.plus(vBoatHobbits))
								.and(landOrcsNew.eq(landOrcs.plus(vBoatOrcs)))
								.and(landHobbitsNew.eq(0).or(landHobbitsNew.geq(landOrcsNew)))
				);
			}
		}

		newTransition(t++, "goal",
				Map.of(hobbits[nIslands - 1], landHobbits, orcs[nIslands - 1], landOrcs),
				Map.of(),
				landHobbits.eq(nHobbits).and(landOrcs.eq(nOrcs)));

		Map<Place, Integer> initialMarking = IntStream.range(1, nIslands)
				.boxed()
				.flatMap(i -> Stream.of(hobbits[i], orcs[i]))
				.collect(Collectors.toMap(Function.identity(), place -> 0));
		initialMarking.put(hobbits[0], nHobbits);
		initialMarking.put(orcs[0], nOrcs);
		initialMarking.put(boatEmpty[0], 1);
		return new Net(new Marking(initialMarking));
	}

	public static Net hobbitsAndOrcsAlternative(int groupSize, int boatCapacity, int nIslands) {
		// https://en.wikipedia.org/wiki/Missionaries_and_cannibals_problem
		int p = 1;
		int t = 1;

		Variable h = new Variable("h", FiniteDomain.fullRange(0, groupSize));
		Variable H = new Variable("h'", FiniteDomain.fullRange(0, groupSize));
		Variable o = new Variable("o", FiniteDomain.fullRange(0, groupSize));
		Variable O = new Variable("o'", FiniteDomain.fullRange(0, groupSize));
		Variable x = new Variable("x", FiniteDomain.fullRange(0, boatCapacity));
		Variable X = new Variable("x'", FiniteDomain.fullRange(0, boatCapacity));
		Variable y = new Variable("y", FiniteDomain.fullRange(0, boatCapacity));
		Variable Y = new Variable("y'", FiniteDomain.fullRange(0, boatCapacity));

		Place lhShore = new Place(p++, "shore_hobbits_l");
		Place lhBoat = new Place(p++, "boat_hobbits_l");
		Place loShore = new Place(p++, "shore_orcs_l");
		Place loBoat = new Place(p++, "boat_orcs_l");
		Place rhShore = new Place(p++, "shore_hobbits_r");
		Place rhBoat = new Place(p++, "boat_hobbits_r");
		Place roShore = new Place(p++, "shore_orcs_r");
		Place roBoat = new Place(p++, "boat_orcs_r");

		newTransition(t++, "embark_l",
				Map.of(lhShore, h, loShore, o, lhBoat, x, loBoat, y),
				Map.of(lhShore, H, loShore, O, lhBoat, X, loBoat, Y),
				Formula.top()
						.and(h.plus(x).eq(H.plus(X)))
						.and(o.plus(y).eq(O.plus(Y)))
						.and(H.gt(0).implies(H.geq(O)))
						.and(X.gt(0).implies(X.geq(Y)))
						.and(X.plus(Y).leq(boatCapacity))
						.and(H.plus(X).gt(0).implies(H.plus(X).geq(O.plus(Y))))
		);
		newTransition(t++, "embark_r",
				Map.of(rhShore, h, roShore, o, rhBoat, x, roBoat, y),
				Map.of(rhShore, H, roShore, O, rhBoat, X, roBoat, Y),
				Formula.top()
						.and(h.plus(x).eq(H.plus(X)))
						.and(o.plus(y).eq(O.plus(Y)))
						.and(H.gt(0).implies(H.geq(O)))
						.and(X.gt(0).implies(X.geq(Y)))
						.and(X.plus(Y).leq(boatCapacity))
						.and(H.plus(X).gt(0).implies(H.plus(X).geq(O.plus(Y))))
		);

		newTransition(t++, "cross_l",
				Map.of(lhBoat, x, loBoat, y),
				Map.of(rhBoat, x, roBoat, y),
				x.plus(y).gt(0));
		newTransition(t++, "cross_r",
				Map.of(rhBoat, x, roBoat, y),
				Map.of(lhBoat, x, loBoat, y),
				x.plus(y).gt(0));

		newTransition(t++, "goal",
				Map.of(rhShore, h, roShore, o),
				Map.of(),
				h.geq(groupSize).and(o.geq(groupSize)));
		return new Net(new Marking(Map.of(lhShore, groupSize, loShore, groupSize, rhShore, 0, roShore, 0, lhBoat, 0, loBoat, 0)));
	}
}
