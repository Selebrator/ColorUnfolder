package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.components.Place;
import de.lukaspanneke.masterthesis.components.Transition;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LlRomerExample2_6 {

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

	{
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
	}

	Net net = new Net(new Marking(Map.of(p1, 1, p2, 1, p3, 1)));

	@Test
	void size() {
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(8, unfolding.getNumberEvents());
		assertEquals(13, unfolding.getNumberConditions());
	}
}
