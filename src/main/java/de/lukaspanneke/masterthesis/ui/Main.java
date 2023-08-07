package de.lukaspanneke.masterthesis.ui;

import de.lukaspanneke.masterthesis.Options;
import de.lukaspanneke.masterthesis.expansion.Expansion;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.parser.HlLolaParser;
import de.lukaspanneke.masterthesis.unfolding.Configuration;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Command(
		name = "color-unfolder"
		, version = "0.1-dev"
		, mixinStandardHelpOptions = true
		, showDefaultValues = true
		, usageHelpAutoWidth = true
		, sortOptions = false
)
public class Main implements Callable<Integer> {

	@Parameters(paramLabel = "input_file", arity = "0..1", defaultValue = "-",
			description = "File-path to the net to process. Defaults to stdin.")
	private String inputFile;

	@Parameters(paramLabel = "output_file", arity = "0..1", defaultValue = "-",
			description = "File-path to the output file. Defaults to stdout.")
	private String outputFile;

	@Option(names = {"-T", "--output"}, paramLabel = "format", defaultValue = "DOT",
			description = "Name of the output format. high-level: DOT; low-level: PEP, DOT.")
	private String outputFormat;

	@Option(names = {"-E", "--expand"}, paramLabel = "range",
			description = "Expand the high-level net to it's low-level representation. For the domain {-2, 0, 2} you would pass range=\"-2..2\".")
	private String expansionRange;

	@Option(names = {"--no-unfold"}, defaultValue = "false",
			description = "Don't unfold. Output the net or expansion.")
	private boolean noUnfold;

	@Option(names = {"--no-color"}, defaultValue = "false",
			description = "Unfold without looking at colors. Used for low-level nets.")
	private boolean noColor;

	@Option(names = {"--no-cutoff"}, defaultValue = "false",
			description = "Unfold without checking cut-offs. Depth bound is still applied.")
	private boolean noCutoff;

	@Option(names = {"-d", "--depth"}, paramLabel = "depth",
			description = "Unfold up to the depth bound.")
	private Integer depth;

	@Option(names = {"-t", "--target"}, paramLabel = "regex",
			description = "Unfold until firing a transition with matching name (reachability analysis).")
	private String[] targetTransition;

	@Option(names = {"-o", "--order"}, paramLabel = "order", defaultValue = "ESPARZA",
			description = "Name of the adequate order to use for unfolding. ESPARZA or MC_MILLAN.")
	private Configuration.AdequateOrder order;

	@Option(names = {"-D", "--show-internal"}, defaultValue = "false",
			description = "Render the internal structure of the unfolding.")
	private boolean internal;

	@Option(names = {"-v", "--verbose"}, showDefaultValue = CommandLine.Help.Visibility.NEVER,
			description = "Verbose mode.")
	private boolean[] verbose = new boolean[0];


	@Override
	public Integer call() throws IOException {
		Options.RENDER_DEBUG = internal;
		Options.ORDER = order.comparator();
		Options.CUTOFF = !noCutoff;
		Options.COLORED = !noColor;
		if (verbose.length >= 1) {
			Options.PRINT_PROGRESS = true;
		}
		Net net;
		{
			InputStream is;
			if (this.inputFile.equals("-")) {
				is = System.in;
			} else {
				try {
					is = new FileInputStream(inputFile);
				} catch (FileNotFoundException e) {
					System.err.println("no such file or directory: " + this.inputFile);
					return 2;
				}
				if (!this.inputFile.endsWith(".hllola")) {
					System.err.println("encountered possibly unsupported input format (determined by file name extension). Assuming HlLoLA format.");
				}
			}
			try (is) {
				net = new HlLolaParser().parse(is);
			}
		}
		if (expansionRange != null) {
			int lowerIncl;
			int upperIncl;
			try {
				String[] split = expansionRange.split("\\.\\.", 2);
				lowerIncl = Integer.parseInt(split[0]);
				upperIncl = Integer.parseInt(split[1]);
			} catch (Exception e) {
				throw new IllegalArgumentException("range must be of form \"lowerIncl..upperIncl\". For example -1..1", e);
			}
			net = Expansion.expand(net, lowerIncl, upperIncl);
			Options.COLORED = false;
		}
		if (noUnfold) {
			return renderOutput(net);
		}
		int depth = this.depth != null ? this.depth : Integer.MAX_VALUE;
		Set<Transition> targetTransitions;
		if (targetTransition != null) {
			List<Pattern> targetTransitionPatterns = Arrays.stream(targetTransition)
					.map(Pattern::compile)
					.toList();
			targetTransitions = net.collectNodes().transitions().stream()
					.filter(transition -> targetTransitionPatterns.stream().anyMatch(pattern -> pattern.matcher(transition.name()).matches()))
					.collect(Collectors.toSet());

			System.err.println("target transitions: " + targetTransitions);
		} else {
			targetTransitions = Set.of();
		}

		Unfolding unfolding = Unfolding.unfold(net, depth, targetTransitions);
		return renderOutput(unfolding);
	}

	private int renderOutput(Net net) throws IOException {
		OutputStream os;
		if (outputFile.equals("-")) {
			os = System.out;
		} else {
			os = new FileOutputStream(outputFile);
		}
		try (Writer out = new BufferedWriter(new OutputStreamWriter(os))) {
			if (outputFormat.equalsIgnoreCase("PEP")) {
				net.renderLlPep(out);
			} else if (outputFormat.equalsIgnoreCase("DOT") || outputFormat.equalsIgnoreCase("GRAPHVIZ")) {
				net.renderDot(out);
			} else {
				System.err.println("unsupported low-level output format: " + outputFormat);
				return 1;
			}
		}
		return 0;
	}

	private int renderOutput(Unfolding unfolding) throws IOException {
		OutputStream os;
		if (outputFile.equals("-")) {
			os = System.out;
		} else {
			os = new FileOutputStream(outputFile);
		}
		try (Writer out = new BufferedWriter(new OutputStreamWriter(os))) {
			if (outputFormat.equalsIgnoreCase("DOT") || outputFormat.equalsIgnoreCase("GRAPHVIZ")) {
				unfolding.render(out);
			} else {
				System.err.println("unsupported low-level output format: " + outputFormat);
				return 1;
			}
		}
		return 0;
	}

	public static void main(String[] args) {
		int returnCode = new CommandLine(new Main()).execute(args);
		System.exit(returnCode);
	}
}
