package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class DistributionInstanceGamma implements Distribution {

	public String getAbbreviation() {
		return "gamm";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new GammaDistribution(5, 1 + parameter / 500);

			public double sample() {
				return 50 * dist.sample();
			}
		};
	}

}
