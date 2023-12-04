package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;

public class DistributionInstanceWeibull implements Distribution {

	public String getAbbreviation() {
		return "weib";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new WeibullDistribution(5, parameter);

			public double sample() {
				return dist.sample();
			}
		};
	}

}
