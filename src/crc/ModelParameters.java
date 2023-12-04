package crc;

public class ModelParameters {

	private double[] parameters = new double[] { 0.5, 0.5, 0.5, 0.33333, 0.5};

	public double getCoarseMiddleGrinding() {
		return parameters[0];
	}

	public double getMiddleFineGrinding() {
		return parameters[1];
	}

	/**
	 * The proportion of mass of the content that is feed.
	 * 
	 * @return
	 */
	public double getContentFeedMassFraction() {
		return parameters[2];
	}

	public double getProductCoarse() {
		return parameters[3];
	}

	public double getProductMiddleFine() {
		return parameters[4];
	}

}
