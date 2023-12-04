package svn48healthcare.synthetic;

public class DistributionInstanceConstant implements Distribution {

	public String getAbbreviation() {
		return "const";
	}

	public DistributionInstance instantiate(double parameter) {
		return new DistributionInstance(parameter) {
			public double sample() {
				return parameter;
			}
		};
	}

}