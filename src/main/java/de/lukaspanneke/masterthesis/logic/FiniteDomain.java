package de.lukaspanneke.masterthesis.logic;

public record FiniteDomain(Domain domain, int lowerIncl, int upperIncl) implements Domain {
	@Override
	public Formula constraint(Variable variable) {
		return domain.constraint(variable);
	}

	public static FiniteDomain fullRange(int lowerIncl, int upperIncl) {
		return new FiniteDomain(v -> v.geq(lowerIncl).and(v.leq(upperIncl)), lowerIncl, upperIncl);
	}
}
