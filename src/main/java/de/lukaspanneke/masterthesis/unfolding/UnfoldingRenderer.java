package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.components.Condition;
import de.lukaspanneke.masterthesis.components.Event;
import de.lukaspanneke.masterthesis.logic.Formula;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class UnfoldingRenderer {
	private final boolean SHOW_DEBUG;

	private final Event initialEvent;
	private final Set<Condition> conditions = new LinkedHashSet<>();
	private final Set<Event> events = new LinkedHashSet<>();

	public UnfoldingRenderer(Event initialEvent, boolean debug) {
		this.initialEvent = initialEvent;
		this.SHOW_DEBUG = debug;
	}

	private void collectNodes(Condition condition) {
		conditions.add(condition);
		for (Event event : condition.postset()) {
			collectNodes(event);
		}
	}

	private void collectNodes(Event event) {
		events.add(event);
		for (Condition condition : event.postset()) {
			collectNodes(condition);
		}
	}

	private String nodeName(Condition node) {
		return node.toString();
	}

	private String nodeName(Event node) {
		return node.toString();
	}

	private String displayName(Condition node) {
		return SHOW_DEBUG ? node.toString() : node.place().name();
	}

	private String displayName(Event node) {
		return SHOW_DEBUG ? node.toString() : node.transition().name();
	}

	public void render(Writer writer) throws IOException {
		collectNodes(initialEvent);
		if (!SHOW_DEBUG) {
			events.remove(initialEvent);
		}
		writer.append("digraph ").append("net").append(" {\n");
		if (!conditions.isEmpty()) {
			writer.append("node[shape=ellipse];\n");
			for (var node : conditions) {
				writer
						.append("\"").append(nodeName(node)).append("\"")
						.append("[label=\"").append(displayName(node)).append("\"]")
						.append("\n");
			}
		}
		if (!events.isEmpty()) {
			writer.append("node[shape=box];\n");
			for (var node : events) {
				writer
						.append("\"").append(nodeName(node)).append("\"");
				Map<String, String> options = new HashMap<>();
				options.put("label", displayName(node));
				node.cutoffReason().ifPresent(cutoffReason -> {
					switch (cutoffReason) {
						case CUT_OFF_CONDITION -> options.put("color", "red");
						case DEPTH -> options.put("color", "darkred");
					}
				});
				StringJoiner xlabel = new StringJoiner("\n");
				if (SHOW_DEBUG) {
					if (node.hasContext()) {
						xlabel.add("h(cut) = " + Unfolding.markingPlaces(node));
					}
					if (!Formula.top().equals(node.guard())) {
						xlabel.add(node.guard().toString());
					}
				} else {
					if (!Formula.top().equals(node.transition().guard())) {
						xlabel.add(node.transition().guard().toString());
					}
				}

				options.put("xlabel", xlabel.toString());
				if (!options.isEmpty()) {
					writer.append(options.entrySet().stream()
							.map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
							.collect(Collectors.joining(",", "[", "]")));
				}
				writer.append("\n");
			}
		}

		for (var to : conditions) {
			var from = to.preset();
			if (!SHOW_DEBUG && from == initialEvent) {
				continue;
			}
			writer.append("\"").append(nodeName(from))
					.append("\" -> \"")
					.append(nodeName(to)).append("\"")
					.append(" [label=\"");
			if (SHOW_DEBUG) {
				writer.append(to.preVariable().name());
			} else {
				writer.append(from.transition().postSet().get(to.place()).name());
			}
			writer.append("\"]")
					.append("\n");
		}
		for (var to : events) {
			for (var from : to.preset()) {
				writer.append("\"").append(nodeName(from))
						.append("\" -> \"")
						.append(nodeName(to)).append("\"")
						.append(" [label=\"");
				if (SHOW_DEBUG) {
					writer.append(from.preVariable().name());
				} else {
					writer.append(to.transition().preSet().get(from.place()).name());
				}
				writer
						.append("\"]")
						.append("\n");
			}
		}
		writer.append("}\n");
	}

}
