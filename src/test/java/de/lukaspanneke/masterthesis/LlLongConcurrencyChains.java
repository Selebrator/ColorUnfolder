package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LlLongConcurrencyChains {

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

	{
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
	}

	Net net = new Net(new Marking(Map.of(p1, 1, p5, 1, p9, 1)));

	@Test
	void size() {
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(9, unfolding.getNumberEvents());
		assertEquals(13, unfolding.getNumberConditions());
	}
}
