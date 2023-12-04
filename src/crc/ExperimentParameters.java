package crc;

import java.io.File;

public class ExperimentParameters {

	public int getFieldOutCoarse() {
		return 5;
	}

	public int getFieldOutMiddle() {
		return 6;
	}

	public int getFieldOutFine() {
		return 7;
	}

	public File getDataOutFile() {
		return new File("V:\\sander\\14 - specific model calibration using Davids data\\04 - fake timestamps.csv");
	}

	public int getWindowSize() {
		return 100;
	}

}
