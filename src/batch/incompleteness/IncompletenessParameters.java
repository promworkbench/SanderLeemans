package batch.incompleteness;

import generation.GenerateTreeParameters;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import batch.incompleteness.miners.MinerIM;
import batch.incompleteness.miners.MinerIMd;
import batch.incompleteness.miners.MinerIMi01;
import batch.incompleteness.miners.MinerIMi05;
import batch.incompleteness.miners.MinerIMi20;
import batch.incompleteness.miners.MinerIMi80;
import batch.incompleteness.miners.MinerIMiD01;
import batch.incompleteness.miners.MinerIMiD05;
import batch.incompleteness.miners.MinerIMiD20;
import batch.incompleteness.miners.MinerIMiD80;
import batch.incompleteness.miners.MinerIMin;
import batch.incompleteness.miners.MinerIMinD;
import batch.incompleteness.miners.MinerOption;

public class IncompletenessParameters {
	
	private List<MinerOption> minerOptions = new LinkedList<MinerOption>(Arrays.asList(
			//new MinerWithout(),
			//new MinerEKS(),
			//new MinerSatUnitWithShortLoops(),
			//new MinerSatUnit(),
			//new MinerSatEstimatedWithShortLoops(),
			//new MinerSatEstimated(),
			//new MinerSatEstimatedZ(),
			//new MinerSatNoise()
			new MinerIM(),
			new MinerIMd(),
			new MinerIMi01(),
			new MinerIMiD01(),
			new MinerIMi05(),
			new MinerIMiD05(),
			new MinerIMi20(),
			new MinerIMiD20(),
			new MinerIMi80(),
			new MinerIMiD80(),
			new MinerIMin(),
			new MinerIMinD()
			));
	
	private List<GenerateTreeParameters> treeSeeds = new LinkedList<GenerateTreeParameters>(Arrays.asList(
			
			/*new GenerateTreeParameters(123, true, 5, 3),
			new GenerateTreeParameters(55555, true, 5, 3),
			new GenerateTreeParameters(112334, true, 5, 3),
			new GenerateTreeParameters(234, true, 5, 3),
			new GenerateTreeParameters(5651, true, 5, 3),
			new GenerateTreeParameters(88888, true, 5, 3),
			new GenerateTreeParameters(18, true, 5, 3),
			new GenerateTreeParameters(2, true, 5, 3),
			new GenerateTreeParameters(25112013, true, 5, 3),
			new GenerateTreeParameters(522, true, 5, 3),
			new GenerateTreeParameters(2312, true, 5, 3),
			new GenerateTreeParameters(5282, true, 5, 3),
			new GenerateTreeParameters(3, true, 5, 3),
			new GenerateTreeParameters(56666666, true, 5, 3),
			new GenerateTreeParameters(472321841, true, 5, 3),
			new GenerateTreeParameters(9118919, true, 5, 3),
			new GenerateTreeParameters(521, true, 5, 3),
			new GenerateTreeParameters(50205, true, 5, 3),
			new GenerateTreeParameters(50050, true, 5, 3),
			new GenerateTreeParameters(305555, true, 5, 3),
			new GenerateTreeParameters(525, true, 5, 3),
			new GenerateTreeParameters(9607, true, 5, 3),
			new GenerateTreeParameters(5114, true, 5, 3),
			new GenerateTreeParameters(671700, true, 5, 3),
			new GenerateTreeParameters(552, true, 5, 3),
			new GenerateTreeParameters(1230, true, 5, 3),
			new GenerateTreeParameters(555550, true, 5, 3),
			new GenerateTreeParameters(1123340, true, 5, 3),
			new GenerateTreeParameters(2340, true, 5, 3),
			new GenerateTreeParameters(56510, true, 5, 3),
			new GenerateTreeParameters(888880, true, 5, 3),
			new GenerateTreeParameters(180, true, 5, 3),
			new GenerateTreeParameters(20, true, 5, 3),
			new GenerateTreeParameters(251120130, true, 5, 3),
			new GenerateTreeParameters(5220, true, 5, 3),
			new GenerateTreeParameters(23120, true, 5, 3),
			new GenerateTreeParameters(52820, true, 5, 3),
			new GenerateTreeParameters(30, true, 5, 3),
			new GenerateTreeParameters(566666660, true, 5, 3),
			new GenerateTreeParameters(472321810, true, 5, 3),
			new GenerateTreeParameters(91189190, true, 5, 3),
			new GenerateTreeParameters(5210, true, 5, 3),
			new GenerateTreeParameters(502050, true, 5, 3),
			new GenerateTreeParameters(500500, true, 5, 3),
			new GenerateTreeParameters(1305555, true, 5, 3),
			new GenerateTreeParameters(1525, true, 5, 3),
			new GenerateTreeParameters(19607, true, 5, 3),
			new GenerateTreeParameters(15114, true, 5, 3),
			new GenerateTreeParameters(1671700, true, 5, 3),
			new GenerateTreeParameters(1552, true, 5, 3)/*,*/
			
			new GenerateTreeParameters(123, true, 15, 3),
			new GenerateTreeParameters(55555, true, 15, 3),
			new GenerateTreeParameters(112334, true, 15, 3),
			new GenerateTreeParameters(234, true, 15, 3),
			new GenerateTreeParameters(10651, true, 15, 3),
			new GenerateTreeParameters(88888, true, 15, 3),
			new GenerateTreeParameters(18, true, 15, 3),
			new GenerateTreeParameters(2, true, 15, 3),
			new GenerateTreeParameters(25112013, true, 15, 3),
			new GenerateTreeParameters(1022, true, 15, 3),
			new GenerateTreeParameters(2312, true, 15, 3),
			new GenerateTreeParameters(5282, true, 15, 3),
			new GenerateTreeParameters(3, true, 15, 3),
			new GenerateTreeParameters(56666666, true, 15, 3),
			new GenerateTreeParameters(472321841, true, 15, 3),
			new GenerateTreeParameters(9118919, true, 15, 3),
			new GenerateTreeParameters(521, true, 15, 3),
			new GenerateTreeParameters(502010, true, 15, 3),
			new GenerateTreeParameters(500100, true, 15, 3),
			new GenerateTreeParameters(30101055, true, 15, 3),
			new GenerateTreeParameters(5210, true, 15, 3),
			new GenerateTreeParameters(9607, true, 15, 3),
			new GenerateTreeParameters(5114, true, 15, 3),
			new GenerateTreeParameters(671700, true, 15, 3),
			new GenerateTreeParameters(1052, true, 15, 3)/*,
			
			new GenerateTreeParameters(2500001, true, 20, 4),
			new GenerateTreeParameters(181988, true, 20, 4),
			new GenerateTreeParameters(34342, true, 20, 4),
			new GenerateTreeParameters(9156999, true, 20, 4),
			new GenerateTreeParameters(2664813, true, 20, 4),
			new GenerateTreeParameters(7523548, true, 20, 4),
			new GenerateTreeParameters(196, true, 20, 4),
			new GenerateTreeParameters(52190, true, 20, 4),
			new GenerateTreeParameters(10532, true, 20, 4),
			new GenerateTreeParameters(7, true, 20, 4),
			new GenerateTreeParameters(768, true, 20, 4),
			new GenerateTreeParameters(451, true, 20, 4),
			new GenerateTreeParameters(796, true, 20, 4),
			new GenerateTreeParameters(2478, true, 20, 4),
			new GenerateTreeParameters(319, true, 20, 4)/*,
			
			new GenerateTreeParameters(65461, true, 25, 5),
			new GenerateTreeParameters(59098, true, 25, 5),
			new GenerateTreeParameters(1022231, true, 25, 5),
			new GenerateTreeParameters(854965, true, 25, 5),
			new GenerateTreeParameters(6874413, true, 25, 5),
			new GenerateTreeParameters(907, true, 25, 5),
			new GenerateTreeParameters(185, true, 25, 5),
			new GenerateTreeParameters(537, true, 25, 5),
			new GenerateTreeParameters(26, true, 25, 5),
			new GenerateTreeParameters(508, true, 25, 5)*/
	));
	
	private long startLogSeed = 1001;
	private long endLogSeed = 1020;
	
	private int maxLogSize = 16384;
	
	private File resultsFile = new File("d:\\output\\incompletenessResults.html");

	public List<MinerOption> getMinerOptions() {
		return minerOptions;
	}

	public void setMinerOptions(List<MinerOption> minerOptions) {
		this.minerOptions = minerOptions;
	}

	public List<GenerateTreeParameters> getTreeSeeds() {
		return treeSeeds;
	}

	public void setTreeSeeds(List<GenerateTreeParameters> treeSeeds) {
		this.treeSeeds = treeSeeds;
	}

	public long getStartLogSeed() {
		return startLogSeed;
	}

	public void setStartLogSeed(long startLogSeed) {
		this.startLogSeed = startLogSeed;
	}

	public long getEndLogSeed() {
		return endLogSeed;
	}

	public void setEndLogSeed(long endLogSeed) {
		this.endLogSeed = endLogSeed;
	}

	public int getMaxLogSize() {
		return maxLogSize;
	}

	public void setMaxLogSize(int maxLogSize) {
		this.maxLogSize = maxLogSize;
	}

	public File getResultsFile() {
		return resultsFile;
	}

	public void setResultsFile(File resultsFile) {
		this.resultsFile = resultsFile;
	}
	
}
