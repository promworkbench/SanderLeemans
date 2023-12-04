package svn27emscpartialordersbounds;

import java.math.BigInteger;

import com.google.common.math.BigIntegerMath;

/**
 * if it doesn't run locally, ask Wolfram
 * 
 * @author sander
 *
 */
public class SymbolicNumber {
	private static final BigInteger factorialThreshold = BigInteger.valueOf(50000);
	private final BigInteger value;
	private final String string;
	private final boolean valueIsFactorial;

	public SymbolicNumber(BigInteger value) {
		assert value != null;
		this.value = value;
		this.string = null;
		valueIsFactorial = false;
	}

	private SymbolicNumber(String string) {
		assert string != null;
		this.value = null;
		this.string = string;
		valueIsFactorial = false;
	}

	private SymbolicNumber(BigInteger value, Object bla) {
		assert value != null;
		this.value = value;
		this.string = null;
		valueIsFactorial = true;
	}

	public boolean isNumber() {
		return value != null && !valueIsFactorial;
	}

	public String toString() {
		if (isNumber()) {
			return value.toString();
		}
		if (valueIsFactorial) {
			return "(" + value.toString() + "!)";
		}
		return string;
	}

	public SymbolicNumber multiply(SymbolicNumber with) {
		if (isNumber() && value.equals(BigInteger.ONE)) {
			return with;
		}
		if (with.isNumber() && with.value.equals(BigInteger.ONE)) {
			return this;
		}
		if (isNumber() && with.isNumber()) {
			return new SymbolicNumber(value.multiply(with.value));
		}
		return new SymbolicNumber("(" + toString() + "*" + with.toString() + ")");
	}

	public SymbolicNumber add(SymbolicNumber n) {
		if (isNumber() && n.isNumber()) {
			return new SymbolicNumber(value.add(n.value));
		}
		return new SymbolicNumber("(" + toString() + "+" + n.toString() + ")");
	}

	public SymbolicNumber factorial() {
		if (isNumber() && value.compareTo(factorialThreshold) <= 0) {
			return new SymbolicNumber(BigIntegerMath.factorial(value.intValue()));
		}
		if (isNumber()) {
			//this number is too big to compute, but we might use while dividing
			return new SymbolicNumber(value, null);
		}
		return new SymbolicNumber("(" + toString() + "!)");
	}

	public SymbolicNumber divide(SymbolicNumber by) {
		if (isNumber() && by.isNumber()) {
			return new SymbolicNumber(value.divide(by.value));
		}
		if (valueIsFactorial && by.valueIsFactorial) {
			//try to get the division of factorials by simplification
			BigInteger difference = value.subtract(by.value);
			if (difference.compareTo(factorialThreshold) <= 0 && difference.compareTo(BigInteger.ZERO) >= 0) {

				System.out.println("  compute " + toString() + "/" + by.toString());
				//we are larger than by, so we can obtain the division by expanding the top factorial
				int diff = difference.intValue();
				BigInteger result = BigInteger.ONE;
				BigInteger factor = value;
				for (int n = 1; n <= diff; n++) {
					System.out.println("   " + factor);
					result.multiply(factor);
					factor = factor.subtract(BigInteger.ONE);
				}
			}
			System.out.println("can do it here");
		}
		return new SymbolicNumber("(" + toString() + "/" + by.toString() + ")");
	}

	/**
	 * 
	 * @return the value of the number if it fits, or Long.MIN_VALUE if it was
	 *         not computed.
	 */
	public long longValue() {
		if (isNumber()) {
			long i = value.longValue();
			if (value.equals(BigInteger.valueOf(i))) {
				return i;
			}
		}
		return Long.MIN_VALUE;
	}

	/**
	 * 
	 * @return the value of the number if it has been computed; otherwise null.
	 */
	public BigInteger bigIntegerValue() {
		return value;
	}

}
