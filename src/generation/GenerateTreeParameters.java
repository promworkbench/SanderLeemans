package generation;

public class GenerateTreeParameters {
	private long seed;
	private boolean startEndDisjointInLoop;
	private int numberOfActivities;
	private int maxNumberOfChildren;

	public GenerateTreeParameters() {
		seed = System.currentTimeMillis();
		startEndDisjointInLoop = true;
		numberOfActivities = 15;
		maxNumberOfChildren = 3;
	}

	public GenerateTreeParameters(long seed, boolean startEndDisjointInLoop, int numberOfActivities,
			int maxNumberOfChildren) {
		this.seed = seed;
		this.startEndDisjointInLoop = startEndDisjointInLoop;
		setNumberOfActivities(numberOfActivities);
		setMaxNumberOfChildren(maxNumberOfChildren);
	}

	public String toString() {
		String s = seed + "-" + numberOfActivities + "-" + maxNumberOfChildren;
		if (!startEndDisjointInLoop) {
			return s;
		} else {
			return s + "-SE";
		}
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public boolean isStartEndDisjointInLoop() {
		return startEndDisjointInLoop;
	}

	public void setStartEndDisjointInLoop(boolean startEndDisjointInLoop) {
		this.startEndDisjointInLoop = startEndDisjointInLoop;
	}

	public int getNumberOfActivities() {
		return numberOfActivities;
	}

	public void setNumberOfActivities(int numberOfActivities) {
		if (numberOfActivities >= 1) {
			this.numberOfActivities = numberOfActivities;
		}
	}

	public int getMaxNumberOfChildren() {
		return maxNumberOfChildren;
	}

	public void setMaxNumberOfChildren(int maxNumberOfChildren) {
		if (maxNumberOfChildren >= 2) {
			this.maxNumberOfChildren = maxNumberOfChildren;
		}
	}

}