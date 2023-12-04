package svn45crimes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.processmining.plugins.InductiveMiner.Pair;

public class SimpleLinearRegression {

	private static MathContext mc = new MathContext(40, RoundingMode.HALF_UP);

	public static Pair<BigDecimal, BigDecimal> regress(List<Pair<Double, Double>> input) {
		
		if (input.size() == 0) {
			return null;
		}

		final BigDecimal averageX;
		final BigDecimal averageY;
		{
			BigDecimal sumX = BigDecimal.ZERO;
			BigDecimal sumY = BigDecimal.ZERO;
			for (Pair<Double, Double> p : input) {
				sumX = sumX.add(BigDecimal.valueOf(p.getA()));
				sumY = sumY.add(BigDecimal.valueOf(p.getB()));
			}

			averageX = sumX.divide(BigDecimal.valueOf(input.size()), mc);
			averageY = sumY.divide(BigDecimal.valueOf(input.size()), mc);
		}

		BigDecimal sumProducts = BigDecimal.ZERO;
		{
			for (Pair<Double, Double> p : input) {
				sumProducts = sumProducts.add(BigDecimal.valueOf(p.getA()).subtract(averageX)
						.multiply(BigDecimal.valueOf(p.getB()).subtract(averageY)));
			}
		}

		BigDecimal sumSquares = BigDecimal.ZERO;
		{
			for (Pair<Double, Double> p : input) {
				sumSquares = sumSquares.add(BigDecimal.valueOf(p.getA()).subtract(averageX).pow(2));
			}
		}
		
		if (sumSquares.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		BigDecimal slope = sumProducts.divide(sumSquares, mc);

		BigDecimal start = averageY.subtract(slope.multiply(averageX));

		return Pair.of(start, slope);
	}
}
