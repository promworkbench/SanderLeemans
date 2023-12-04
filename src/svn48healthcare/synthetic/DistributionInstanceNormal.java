package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class DistributionInstanceNormal implements Distribution {

	public String getAbbreviation() {
		return "norm";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new NormalDistribution(parameter, 10);

			public double sample() {
				return dist.sample();
			}
		};
	}

}
