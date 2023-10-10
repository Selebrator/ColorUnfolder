package de.lukaspanneke.masterthesis.ui;

import de.lukaspanneke.masterthesis.Options;
import de.lukaspanneke.masterthesis.examples.Examples;
import de.lukaspanneke.masterthesis.expansion.Expansion;
import de.lukaspanneke.masterthesis.expansion.ExpansionRange;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Transition;
import de.lukaspanneke.masterthesis.parser.HlLolaParser;
import de.lukaspanneke.masterthesis.unfolding.Configuration;
import de.lukaspanneke.masterthesis.unfolding.Event;
import de.lukaspanneke.masterthesis.unfolding.Unfolding;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
			description = "Name of the output format. high-level: DOT, none; low-level: PEP, DOT, none.")
	private String outputFormat;

	@Option(names = {"-E", "--expand"}, defaultValue = "false",
			description = "Expand the high-level net to it's low-level representation. Implies --no-color.")
	private boolean expand;

	@Option(names = {"-R", "--expand-with"}, paramLabel = "range",
			description = "The default range to use for expansion in cases where the net has no builtin bounds. For the domain {-2, 0, 2} you would pass range=\"-2..2\". Implies --expand.")
	private String expansionRange;

	@Option(names = {"--no-jit-expand"}, defaultValue = "false",
			description = "Build the entire expansion.")
	private boolean noJitExpand;

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

	@Option(names = {"--time"}, defaultValue = "false",
			description = "Measure time spent expanding and unfolding.")
	private boolean time;


	@Override
	public Integer call() throws IOException {
		Options.RENDER_DEBUG = internal;
		Options.ORDER = order.comparator();
		Options.CUTOFF = !noCutoff;
		Options.COLORED = !noColor;
		if (verbose.length >= 2) {
			Options.PRINT_PROGRESS = true;
		}
		if (verbose.length >= 3) {
			Options.PRINT_COLOR_CUTOFF_INFO = true;
			Options.PRINT_COLOR_CONFLICT_INFO = true;
		}
		Net net;

		if (this.inputFile.startsWith("isqrt#")) {
			net = Examples.isqrt(Integer.parseInt(this.inputFile.substring("isqrt#".length())));
		} else if (this.inputFile.startsWith("gcd#")) {
			String[] split = this.inputFile.split("#");
			net = Examples.gcd(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		} else if (this.inputFile.equals("restaurant")) {
			net = Examples.restaurant();
		} else if (this.inputFile.startsWith("mastermind-judge#")) {
			//                 code guess
			//                 |    |
			//mastermind-judge#4182#6123
			String[] split = this.inputFile.split("#");
			net = Examples.mastermind(split[1].chars().map(c -> c - '0').toArray(), split[2].chars().map(c -> c - '0').toArray());

		} else if (this.inputFile.startsWith("mastermind-game#")) {
			//                code length
			//                | available colors
			//                | | number of guesses
			//                | | |
			//mastermind-game#4#8#12
			String[] split = this.inputFile.split("#");
			net = Examples.mastermindNoDuplicateColors(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
		} else if (this.inputFile.startsWith("buckets")) {
			String[] split = this.inputFile.split("#");
			int goal = Integer.parseInt(split[1]);
			int[] buckets = IntStream.range(2, split.length)
					.map(i -> Integer.parseInt(split[i]))
					.toArray();
			net = Examples.buckets(buckets, goal);
		} else if (this.inputFile.startsWith("parallelAmnesia#")) {
			net = Examples.parallelAmnesia(Integer.parseInt(this.inputFile.substring("parallelAmnesia#".length())));
		} else if (this.inputFile.startsWith("fast-growing#")) {
			String params = this.inputFile.substring("fast-growing#".length());
			String[] split = params.split("#");
			net = Examples.fastGrowing(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		} else if (this.inputFile.startsWith("hobbitsAndOrcsAlternative")) {
			String[] split = this.inputFile.split("#");
			int groupSize = Integer.parseInt(split[1]);
			int boatCapacity = Integer.parseInt(split[2]);
			net = Examples.hobbitsAndOrcsAlternative(groupSize, boatCapacity, 2);
		} else if (this.inputFile.startsWith("hobbitsAndOrcs")) {
			String[] split = this.inputFile.split("#");
			int groupSize = Integer.parseInt(split[1]);
			int boatCapacity = Integer.parseInt(split[2]);
			int islands = Integer.parseInt(split[3]);
			net = Examples.hobbitsAndOrcs(groupSize, boatCapacity, islands);
		} else {
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
			expand = true;
			Options.EXPANSION_RANGE = ExpansionRange.parse(this.expansionRange);
		}
		if (expand) {
			Options.COLORED = false;
			Options.EXPAND = true;
		}
		if (expand && noJitExpand) {
			long before = System.currentTimeMillis();
			net = Expansion.expand(net);
			Options.EXPAND = false;
			long after = System.currentTimeMillis();
			if (time) {
				System.err.println("Expanding took " + (after - before) + " ms");
			}
		}
		if (noUnfold) {
			return renderOutput(net);
		}
		int depth = this.depth != null ? this.depth : Integer.MAX_VALUE;
		Set<Transition> targetTransitions;
		if (targetTransition != null) {
			List<Pattern> targetTransitionPatterns = Arrays.stream(targetTransition)
					.map(pattern -> {
						if (expand && noJitExpand) {
							return "t\\d+_" + pattern;
						} else {
							return pattern;
						}
					})
					.map(Pattern::compile)
					.toList();
			targetTransitions = net.collectNodes().transitions().stream()
					.filter(transition -> targetTransitionPatterns.stream().anyMatch(pattern -> pattern.matcher(transition.name()).matches()))
					.collect(Collectors.toSet());

			if (verbose.length >= 1) {
				System.err.println("Checking reachability of target transitions: " + targetTransitions);
			}
		} else {
			targetTransitions = Set.of();
		}

		long before = System.currentTimeMillis();
		Unfolding unfolding = Unfolding.unfold(net, depth, targetTransitions);
		long after = System.currentTimeMillis();
		if (time) {
			System.err.println("Unfolding took " + (after - before) + " ms");
		}
		renderOutput(unfolding);
		if (targetTransition != null) {
			Optional<Event> target = unfolding.foundTarget();
			if (target.isPresent()) {
				if (verbose.length >= 1) {
					System.err.println("Target transition " + target.get().transition().name() + " can fire:");
				}
				System.err.println(target.get().coneConfiguration().firingSequenceString());
				return 0;
			} else {
				if (verbose.length >= 1) {
					System.err.println("No target transition can fire");
				}
				return 3;
			}
		}
		return 0;
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
			} else if (outputFormat.equalsIgnoreCase("none")) {
				// no-op
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
			} else if (outputFormat.equalsIgnoreCase("none")) {
				// no-op
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
