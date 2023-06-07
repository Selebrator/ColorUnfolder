package org.example.net;

import org.example.components.Place;

import java.util.Map;

public record Marking(Map<Place, Object> tokens) {
}
