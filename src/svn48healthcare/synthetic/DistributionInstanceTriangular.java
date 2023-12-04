package svn48healthcare.synthetic;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;

public class DistributionInstanceTriangular implements Distribution {

	public String getAbbreviation() {
		return "trian";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {

			RealDistribution dist = new TriangularDistribution(0, parameter, 1000);
			
			

			public double sample() {
				return dist.sample();
			}
		};
	}

}
