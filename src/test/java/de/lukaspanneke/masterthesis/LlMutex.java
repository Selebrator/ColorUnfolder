package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LlMutex {

	Place
			m = new Place(0, "mutex"),
			s1 = new Place(1, "safe1"),
			s2 = new Place(2, "safe2"),
			w1 = new Place(3, "wait1"),
			w2 = new Place(4, "wait2"),
			c1 = new Place(5, "crit1"),
			c2 = new Place(6, "crit2");
	Transition
			d1 = new Transition(1, "dngr1"),
			d2 = new Transition(2, "dngr2"),
			e1 = new Transition(3, "entr1"),
			e2 = new Transition(4, "entr2"),
			l1 = new Transition(5, "leav1"),
			l2 = new Transition(6, "leav2");

	{
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
	}

	Net net = new Net(new Marking(Map.of(s1, 1, s2, 1, m, 1)));

	@Test
	void canEnterCriticalSection() {
		Unfolding unfolding = Unfolding.unfold(net, Set.of(e1, e2));
		renderAndClip(unfolding);
		System.out.println("target event: " + unfolding.foundTarget());
		assertTrue(unfolding.foundTarget().isPresent());
	}
}
