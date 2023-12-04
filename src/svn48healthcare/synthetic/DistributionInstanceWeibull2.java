package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;

public class DistributionInstanceWeibull2 implements Distribution {

	public String getAbbreviation() {
		return "weib0.5";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new WeibullDistribution(0.5, parameter);

			public double sample() {
				return dist.sample();
			}
		};
	}

}
