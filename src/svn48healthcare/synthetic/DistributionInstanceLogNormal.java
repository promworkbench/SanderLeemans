package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class DistributionInstanceLogNormal implements Distribution {

	public String getAbbreviation() {
		return "lognorm";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new LogNormalDistribution(Math.log(parameter), 0.5);

			public double sample() {
				return dist.sample();
			}
		};
	}

}
