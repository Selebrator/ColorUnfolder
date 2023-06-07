package org.example.components;

import org.example.Order;

import java.util.Objects;
import java.util.Set;

public final class Configuration {
	private final Set<Event> events;
	private Order.Parikh parikh;
	private Order.Foata foata;

	public Configuration(Set<Event> events) {
		this.events = Set.copyOf(events);
	}

	public int size() {
		return events.size();
	}

	public Set<Event> events() {
		return events;
	}

	public Order.Parikh parikh() {
		if (this.parikh == null) {
			this.parikh = new Order.Parikh(this.events);
		}
		return parikh;
	}

	public Order.Foata foata() {
		if (this.foata == null) {
			this.foata = new Order.Foata(this.events);
		}
		return foata;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Configuration) obj;
		return Objects.equals(this.events, that.events);
	}

	@Override
	public int hashCode() {
		return Objects.hash(events);
	}

	@Override
	public String toString() {
		return "Configuration[" +
				"events=" + events + ']';
	}

}
