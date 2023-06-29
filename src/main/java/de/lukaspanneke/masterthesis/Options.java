package de.lukaspanneke.masterthesis;

public class Options {
	/** Print top level progress */
	public static final boolean PRINT_PROGRESS = false;

	/** Print concurrency matrix */
	public static final boolean PRINT_CONCURRENCY_INFO = false;

	/** Print cut-off checking (high level part only) */
	public static final boolean PRINT_COLOR_CUTOFF_INFO = false;

	/** Print color conflict checking */
	public static final boolean PRINT_COLOR_CONFLICT_INFO = false;

	/** Print formulas given to the smt solver */
	public static final boolean SHOW_FORMULAS = false;

	/** Print model produced by the smt solver */
	public static final boolean SHOW_MODEL = false;

	/** Render the unfolding with debug info */
	public static final boolean RENDER_DEBUG = false;
}
