package svn48healthcare.synthetic;

public interface Distribution {
	DistributionInstance instantiate(double parameter);

	public String getAbbreviation();

}