package de.lukaspanneke.masterthesis;

import de.lukaspanneke.masterthesis.unfolding.Configuration;

import java.util.Comparator;

public class Options {
	/** Print top level progress */
	public static boolean PRINT_PROGRESS = false;

	/** Print concurrency matrix */
	public static boolean PRINT_CONCURRENCY_INFO = false;

	/** Print cut-off checking (high level part only) */
	public static boolean PRINT_COLOR_CUTOFF_INFO = false;

	/** Print color conflict checking */
	public static boolean PRINT_COLOR_CONFLICT_INFO = false;

	/** Print formulas given to the smt solver */
	public static boolean SHOW_FORMULAS = false;

	/** Print model produced by the smt solver */
	public static boolean SHOW_MODEL = false;

	/** Render the unfolding with debug info */
	public static boolean RENDER_DEBUG = false;

	/**
	 * true if the cutoff criterion should be applied.
	 * false if all event up to the depth bound should be explored.
	 */
	public static boolean CUTOFF = true;

	/**
	 * false if the net is low-level.
	 */
	public static boolean COLORED = true;

	/**
	 * The order to use to compare configurations.
	 */
	public static Comparator<Configuration> ORDER = Configuration.AdequateOrder.ESPARZA.comparator();
}
