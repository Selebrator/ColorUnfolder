package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;

// infinite size
public class Aoc22D19 {

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

	{
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
	}

	Net net = new Net(new Marking(Map.of(
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
