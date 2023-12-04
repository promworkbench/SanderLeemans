package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class DistributionInstanceExponential implements Distribution {

	public String getAbbreviation() {
		return "exp";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new ExponentialDistribution(parameter);

			public double sample() {
				return dist.sample();
			}
		};
	}

}
