package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;

import java.io.IOException;
import java.io.StringWriter;

public class NetConstruction {

	static Variable VAR = new Variable(".");

	public static void renderAndClip(Net net) {
		try (StringWriter stringWriter = new StringWriter()) {
			net.renderDot(stringWriter);
			System.out.println(stringWriter);
			Process clip;
			try {
				clip = new ProcessBuilder("xclip", "-sel", "clip").start();
			} catch (IOException e) {
				return;
			}
			try (var out = clip.outputWriter()) {
				out.append(stringWriter.toString()).flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void renderAndClip(Unfolding unf) {
		try (StringWriter stringWriter = new StringWriter()) {
			unf.render(stringWriter);
			System.out.println(stringWriter);
			System.out.println(stringWriter.toString().lines().count() - 2 + " lines");
			Process clip;
			try {
				clip = new ProcessBuilder("xclip", "-sel", "clip").start();
			} catch (IOException e) {
				return;
			}
			try (var out = clip.outputWriter()) {
				out.append(stringWriter.toString()).flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void link(Place p, Transition t) {
		p.postSet().add(t);
		t.preSet().put(p, VAR);
	}

	public static void link(Place p, Variable variable, Transition t) {
		p.postSet().add(t);
		t.preSet().put(p, variable);
	}

	public static void link(Place p, Transition... tt) {
		for (Transition t : tt) {
			link(p, t);
		}
	}

	public static void link(Transition t, Place p) {
		t.postSet().put(p, VAR);
		p.preSet().add(t);
	}

	public static void link(Transition t, Variable variable, Place p) {
		t.postSet().put(p, variable);
		p.preSet().add(t);
	}

	public static void link(Transition t, Place... pp) {
		for (Place p : pp) {
			link(t, p);
		}
	}

	public static void link(Place from, Transition over, Place to) {
		link(from, over);
		link(over, to);
	}

	public static void link(Transition from, Place over, Transition to) {
		link(from, over);
		link(over, to);
	}
}
