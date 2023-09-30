package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.expansion.Expansion;
import de.lukaspanneke.masterthesis.logic.Domain;
import de.lukaspanneke.masterthesis.logic.FiniteDomain;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static de.lukaspanneke.masterthesis.NetConstruction.link;
import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;

// I think I got the definition of expansion incorrect when making this example.
// dead transition should also be included.
public class FastGrowingExpansion {

	Net net(int n, int m) {
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

	Net expansion(int n, int m) {
		List<Set<Place>> allPost = new ArrayList<>(m * n);
		Map<Place, Integer> initial = new HashMap<>();
		int t_idx = 1;
		int p_idx = 1;
		for (int strand = 1; strand <= n; strand++) {
			Set<Place> thisPost = new HashSet<>();
			String preName = "a" + strand + ".1";
			Place pre = new Place(p_idx++);
			initial.put(pre, 1);
			for (int i = 1; i <= m; i++) {
				Transition transition = new Transition(t_idx++);
				link(pre, transition);
				String postName = "b" + transition.index() + "." + i;
				Place post = new Place(p_idx++);
				thisPost.add(post);
				link(transition, post);
			}
			allPost.add(thisPost);
		}
		for (Place[] inPlaces : new CartesianProduct<>(Place[]::new, allPost)) {
			for (Place[] outPlaces : new CartesianProduct<>(Place[]::new, allPost)) {
				int trans_index = t_idx++;
				String transitionName = "t" + trans_index + Arrays.toString(inPlaces) + Arrays.toString(outPlaces);
				Transition transition = new Transition(trans_index);
				for (Place inPlace : inPlaces) {
					link(inPlace, transition);
				}
				for (Place outPlace : outPlaces) {
					link(transition, outPlace);
				}
			}
		}
		return new Net(new Marking(initial));
	}

	@Test
	void hl_unfolding() {
		Net net = net(2, 2);
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
	}

	@Test
	void expansion_unfolding() {
		Net net = expansion(2, 3);
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
	}

	void ll_net(int n, int m) {
		Net expansion = expansion(n, m);
		//renderAndClip(expansion);
		Net.Nodes nodes = expansion.collectNodes();
		assertEquals(n + n * m, nodes.places().size());
		assertEquals(n * m + Math.pow(m, 2 * n), nodes.transitions().size());

		try (Writer out = Files.newBufferedWriter(Path.of("/home/lukas/ma/mole-140428", "expan-" + n + "-" + m + ".ll"))) {
			expansion.renderLlPep(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void speed(int n, int m) {
		Net expansion = expansion(n, m);
		long before = System.currentTimeMillis();
		Unfolding unfolding = Unfolding.unfold(expansion);
		long after = System.currentTimeMillis();
		System.out.println(n + "," + m + " took " + (after - before) + " ms");
		try (var dot = new StringWriter()) {
			unfolding.render(dot);
			System.out.println(dot.toString().lines().count());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// time to compute unfolding of expansion / size of expansion

	//@Test
	void expansion() {
		int n = 3;
		int m = 6;
		Net.Nodes manual = expansion(n, m).collectNodes();
		Net.Nodes algorithm = Expansion.expand(net(n, m)).collectNodes();
		assertEquals(manual.places().size(), algorithm.places().size());
		assertEquals(manual.transitions().size(), algorithm.transitions().size());
	}
}
