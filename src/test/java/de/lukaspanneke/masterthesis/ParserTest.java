package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.parser.HlLolaParser;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.lukaspanneke.masterthesis.NetConstruction.renderAndClip;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

	@Test
	void finiteCountUpSize() throws IOException {
		Net net = new HlLolaParser().parse(Files.newInputStream(Path.of("src/test/resources/FiniteCountUp.hllola")));
		renderAndClip(net);
		Net.Nodes nodes = net.collectNodes();
		assertEquals(1, nodes.transitions().size());
		assertEquals(1, nodes.places().size());
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(3, unfolding.getNumberEvents());
		assertEquals(4, unfolding.getNumberConditions());
	}

	@Test
	void simpleColorConflictSize() throws IOException {
		Net net = new HlLolaParser().parse(Files.newInputStream(Path.of("src/test/resources/SimpleColorConflict.hllola")));
		renderAndClip(net);
		Net.Nodes nodes = net.collectNodes();
		assertEquals(4, nodes.transitions().size());
		assertEquals(6, nodes.places().size());
		Unfolding unfolding = Unfolding.unfold(net);
		renderAndClip(unfolding);
		assertEquals(3, unfolding.getNumberEvents());
		assertEquals(5, unfolding.getNumberConditions());
	}
}
