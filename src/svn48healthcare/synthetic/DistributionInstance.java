package svn48healthcare.synthetic;

public abstract class DistributionInstance {

	private final double parameter;

	public DistributionInstance(double parameter) {
		this.parameter = parameter;
	}

	public double getParameter() {
		return parameter;
	}

	public abstract double sample();
}