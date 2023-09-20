package de.lukaspanneke.masterthesis.expansion;

public record ExpansionRange(int lowerIncl, int upperIncl) {
	/**
	 * Parse {@code 0..5} as {@code new ExpansionRange(0, 5)}
	 */
	public static ExpansionRange parse(String expansionRange) {
		int lowerIncl;
		int upperIncl;
		try {
			String[] split = expansionRange.split("\\.\\.", 2);
			lowerIncl = Integer.parseInt(split[0]);
			upperIncl = Integer.parseInt(split[1]);
		} catch (Exception e) {
			throw new IllegalArgumentException("range must be of form \"lowerIncl..upperIncl\". For example -1..1", e);
		}
		return new ExpansionRange(lowerIncl, upperIncl);
	}
}
