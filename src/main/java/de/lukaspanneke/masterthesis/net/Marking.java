package de.lukaspanneke.masterthesis.net;

import de.lukaspanneke.masterthesis.components.Place;

import java.util.Map;

public record Marking(Map<Place, Integer> tokens) {
}
