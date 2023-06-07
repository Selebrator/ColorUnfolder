package org.example.components;

import java.util.Set;

public record EventContext(Set<Event> localConfiguration, Set<Condition> preset, Set<Condition> postset,
						   Set<Condition> cut) {
}
