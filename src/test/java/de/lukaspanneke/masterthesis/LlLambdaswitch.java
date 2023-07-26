package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.components.Place;
import de.lukaspanneke.masterthesis.components.Transition;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static org.junit.jupiter.api.Assertions.assertEquals;

// https://github.com/giannkas/ecofolder/tree/main/examples/gandalf2022_paper/lambdaswitch
public class LlLambdaswitch {

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
			t1 = new Transition(41, "t1"),
			t2 = new Transition(40, "t2"),
			t3 = new Transition(39, "t3"),
			t4 = new Transition(38, "t4"),
			t5 = new Transition(37, "t5"),
			t6 = new Transition(36, "t6"),
			t7 = new Transition(35, "t7"),
			t8 = new Transition(34, "t8"),
			t9 = new Transition(33, "t9"),
			t10 = new Transition(32, "t10"),
			t11 = new Transition(31, "t11"),
			t12 = new Transition(30, "t12"),
			t13 = new Transition(29, "t13"),
			t14 = new Transition(28, "t14"),
			t15 = new Transition(27, "t15"),
			t16 = new Transition(26, "t16"),
			t17 = new Transition(25, "t17"),
			t18 = new Transition(24, "t18"),
			t19 = new Transition(23, "t19"),
			t20 = new Transition(22, "t20"),
			t21 = new Transition(21, "t21"),
			t22 = new Transition(20, "t22"),
			t23 = new Transition(19, "t23"),
			t24 = new Transition(18, "t24"),
			t25 = new Transition(17, "t25"),
			t26 = new Transition(16, "t26"),
			t27 = new Transition(15, "t27"),
			t28 = new Transition(14, "t28"),
			t29 = new Transition(13, "t29"),
			t30 = new Transition(12, "t30"),
			t31 = new Transition(11, "t31"),
			t32 = new Transition(10, "t32"),
			t33 = new Transition(9, "t33"),
			t34 = new Transition(8, "t34"),
			t35 = new Transition(7, "t35"),
			t36 = new Transition(6, "t36"),
			t37 = new Transition(5, "t37"),
			t38 = new Transition(4, "t38"),
			t39 = new Transition(3, "t39"),
			t40 = new Transition(2, "t40"),
			t41 = new Transition(1, "t41");

	{
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
	}

	Net net = new Net(new Marking(Map.of(p1, 1, p7, 1, p8, 1, p10, 1)));

	@Test
	void size() throws IOException {
		Unfolding unfolding = Unfolding.unfold(net);
		assertEquals(126, unfolding.getNumberEvents());
		assertEquals(295, unfolding.getNumberConditions());
		try (StringWriter out = new StringWriter()) {
			unfolding.render(out);
			long size = out.toString().lines().count() - 2;
			long expected = Options.RENDER_DEBUG ? 1018 : 1005;
			assertEquals(expected, size);
		}
	}
}
