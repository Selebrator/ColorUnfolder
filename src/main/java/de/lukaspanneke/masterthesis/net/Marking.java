package de.lukaspanneke.masterthesis.net;

import java.util.Map;

public record Marking(Map<Place, Integer> tokens) {
}
