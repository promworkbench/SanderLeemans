package batch;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import batch.miners.Miner;

public class CompareMinersParameters {
	
	//input
	//private File folder = new File("d:\\datasets\\selected - BPI - artificialStartEnd");
	private File folder = new File("d:\\datasets\\selected");
	private Set<String> extensions = new HashSet<String>(Arrays.asList(".xes", ".xml", ".mxml"));
	
	//processing
	private List<Miner> miners = new LinkedList<Miner>(Arrays.asList(
			//new batch.miners.Alpha()
			new batch.miners.IM()
			//new batch.miners.Heuristics()
			//new batch.miners.ILP()
			//new batch.miners.Genetic()
			//new batch.miners.ETM()
			//new batch.miners.Flower()
			//new batch.miners.Trace()
			));
	
	private int maxMiningTime = 60 * 60 * 2; //seconds
	//private int maxMiningTime = 60 * 10; //seconds
	
	private int maxReplayTime = 60 * 10; //seconds
	//private int maxReplayTime = 0; //seconds
	
	private boolean checkSoundness = false;
	private boolean replayLog = true;
	
	//output
	private File resultsFile = new File("d:\\output\\comparingResults.html");
	private File petrinetOutputFolder = new File("d:\\output");
	private File petrinetInputFolder = new File("\\\\pcwin1210\\output\\minedPetrinets");

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public List<Miner> getMiners() {
		return miners;
	}

	public void setMiners(List<Miner> miners) {
		this.miners = miners;
	}

	public File getResultsFile() {
		return resultsFile;
	}

	public void setResultsFile(File resultsFile) {
		this.resultsFile = resultsFile;
	}

	public boolean getCheckSoundness() {
		return checkSoundness;
	}

	public void setCheckSoundness(boolean checkSoundness) {
		this.checkSoundness = checkSoundness;
	}

	public boolean getReplayLog() {
		return replayLog;
	}

	public void setReplayLog(boolean replayLog) {
		this.replayLog = replayLog;
	}

	public File getPetrinetOutputFolder() {
		return petrinetOutputFolder;
	}

	public void setPetrinetOutputFolder(File petrinetOutputFolder) {
		this.petrinetOutputFolder = petrinetOutputFolder;
	}

	public int getMaxMiningTime() {
		return maxMiningTime;
	}

	public void setMaxMiningTime(int maxMiningTime) {
		this.maxMiningTime = maxMiningTime;
	}

	public int getMaxReplayTime() {
		return maxReplayTime;
	}

	public void setMaxReplayTime(int maxReplayTime) {
		this.maxReplayTime = maxReplayTime;
	}

	public File getPetrinetInputFolder() {
		return petrinetInputFolder;
	}

	public void setPetrinetInputFolder(File petrinetInputFolder) {
		this.petrinetInputFolder = petrinetInputFolder;
	}
}
