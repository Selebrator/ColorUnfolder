package de.lukaspanneke.masterthesis.ui;

import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.parser.HlLolaParser;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	public static void main(String[] args) throws IOException {
		Net net = new HlLolaParser().parse(Files.newInputStream(Path.of(args[0])));
		Unfolding unfolding = Unfolding.unfold(net);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
		unfolding.render(writer);
		writer.flush();
	}
}
