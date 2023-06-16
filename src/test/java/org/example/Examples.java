package org.example;

import org.example.components.Place;
import org.example.components.Transition;
import org.example.net.Marking;
import org.example.net.Net;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

public class Examples {

	@Test
	void example() throws IOException {
		renderAndClip(Unfolding.unfold(romer_example_2_6(), 20));
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
		var p1 = new Place(1, Set.of());
		var t1 = new Transition(1, Set.of(p1));
		var p2 = new Place(2, Set.of(t1));
		var t2 = new Transition(2, Set.of(p2));
		t2.postSet().add(p2);
		p2.preSet().add(t2);
		var t3 = new Transition(3, Set.of(p2));
		var p3 = new Place(3, Set.of(t3));
		return new Net(new Marking(Map.of(p1, 0)));
	}

	Net xiang() {
		var p0 = new Place(0, Set.of());
		var p1 = new Place(1, Set.of());
		var p2 = new Place(2, Set.of());
		var t0 = new Transition(0, Set.of(p0, p1));
		var t1 = new Transition(1, Set.of(p1, p2));
		var p3 = new Place(3, Set.of(t0));
		var p4 = new Place(4, Set.of(t0));
		var p5 = new Place(5, Set.of(t1));
		var t2 = new Transition(2, Set.of(p3));
		var t3 = new Transition(3, Set.of(p4));
		var t4 = new Transition(4, Set.of(p5));
		var p6 = new Place(6, Set.of(t2));
		var p7 = new Place(7, Set.of(t3));
		var p8 = new Place(8, Set.of(t4));
		var t5 = new Transition(5, Set.of(p6, p7));
		var t6 = new Transition(6, Set.of(p8));
		var p9 = new Place(9, Set.of(t5));
		t6.postSet().add(p5);
		p5.preSet().add(t6);
		return new Net(new Marking(Map.of(p0, 1, p1, 1, p2, 1)));
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
		t.preSet().add(p);
	}

	private static void link(Place p, Transition... tt) {
		for (Transition t : tt) {
			link(p, t);
		}
	}

	private static void link(Transition t, Place p) {
		t.postSet().add(p);
		p.preSet().add(t);
	}

	private static void link(Transition t, Place... pp) {
		for (Place p : pp) {
			link(t, p);
		}
	}
}
