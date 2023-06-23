package org.example.components;

public interface IEvent extends Comparable<IEvent> {

	Transition transition();

	int depth();

	@Override
	default int compareTo(IEvent that) {
		return Integer.compare(this.transition().index(), that.transition().index());
	}
}
