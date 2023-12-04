package batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.Pair;

import batch.miners.Miner;

public class CompareMinersResult implements HTMLToString {
	private HashMap<File, String> result = new HashMap<File, String>();
	private CompareMinersParameters parameters;
	private CompareMinersTimers timers;
	private List<File> files;
	
	private String format = "%5.3f";
	
	private HashMap<Pair<File, Miner>, String> fitnessWithFinalMarking;
	private HashMap<Pair<File, Miner>, String> precisionWithFinalMarking;
	private HashMap<Pair<File, Miner>, String> generalisationWithFinalMarking;
	private HashMap<Pair<File, Miner>, String> fitnessWithoutFinalMarking;
	private HashMap<Pair<File, Miner>, String> precisionWithoutFinalMarking;
	private HashMap<Pair<File, Miner>, String> generalisationWithoutFinalMarking;
	private HashMap<Pair<File, Miner>, String> miningTime;
	private HashMap<Pair<File, Miner>, String> numberOfArcs;
	private HashMap<Pair<File, Miner>, String> numberOfPlaces;
	private HashMap<Pair<File, Miner>, String> numberOfTransitions;
	
	public CompareMinersResult(CompareMinersParameters parameters, List<File> files) {
		this.parameters = parameters;
		this.files = files;
		timers = new CompareMinersTimers(parameters.getMiners(), this);
		
		fitnessWithFinalMarking = new HashMap<Pair<File,Miner>, String>();
		precisionWithFinalMarking = new HashMap<Pair<File,Miner>, String>();
		generalisationWithFinalMarking = new HashMap<Pair<File,Miner>, String>();
		fitnessWithoutFinalMarking = new HashMap<Pair<File,Miner>, String>();
		precisionWithoutFinalMarking = new HashMap<Pair<File,Miner>, String>();
		generalisationWithoutFinalMarking = new HashMap<Pair<File,Miner>, String>();
		miningTime = new HashMap<Pair<File,Miner>, String>();
		numberOfArcs = new HashMap<Pair<File,Miner>, String>();
		numberOfPlaces = new HashMap<Pair<File,Miner>, String>();
		numberOfTransitions = new HashMap<Pair<File,Miner>, String>();
		
		for (File file : files) {
			result.put(file, "");
			
			for (Miner miner : parameters.getMiners()) {
				fitnessWithFinalMarking.put(new Pair<File, Miner>(file, miner), "");
				precisionWithFinalMarking.put(new Pair<File, Miner>(file, miner), "");
				generalisationWithFinalMarking.put(new Pair<File, Miner>(file, miner), "");
				fitnessWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), "");
				precisionWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), "");
				generalisationWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), "");
				miningTime.put(new Pair<File, Miner>(file, miner), "");
				numberOfArcs.put(new Pair<File, Miner>(file, miner), "");
				numberOfPlaces.put(new Pair<File, Miner>(file, miner), "");
				numberOfTransitions.put(new Pair<File, Miner>(file, miner), "");
			}
		}
	}
	
	public synchronized void append(File file, String comment) {
		result.put(file, result.get(file) + comment);
		writeToDisk();
	}
	
	private synchronized void writeToDisk() {
		//write the results to disk
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(parameters.getResultsFile());
			out = new BufferedWriter(fstream);
			out.write(toHTMLString(true));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void reportMiningTime(File file, Miner miner, float time) {
		miningTime.put(new Pair<File, Miner>(file, miner), String.valueOf((int) time));
	}
	
	public synchronized void reportComplexity(File file, Miner miner, int numberOfArcs, int numberOfPlaces, int numberOfTransitions) {
		this.numberOfArcs.put(new Pair<File, Miner>(file, miner), "("+numberOfArcs+","+numberOfPlaces+","+numberOfTransitions+")");
		//this.numberOfPlaces.put(new Pair<File, Miner>(file, miner), String.valueOf(numberOfPlaces));
		//this.numberOfTransitions.put(new Pair<File, Miner>(file, miner), String.valueOf(numberOfTransitions));
		writeToDisk();
	}
	
	public synchronized void reportFitness(File file, Miner miner, boolean withFinalMarking, double fitness) {
		if (withFinalMarking) {
			fitnessWithFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, fitness));
		} else {
			fitnessWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, fitness));
		}
		
		String withWithoutFinalMarking;
		if (withFinalMarking) {
			withWithoutFinalMarking = "with final marking ";
		} else {
			withWithoutFinalMarking = "without final marking ";
		}
		
		append(file, "&nbsp;&nbsp;&nbsp;&nbsp;fitness " + withWithoutFinalMarking + fitness + "<br>");
	}
	
	public synchronized void reportPrecision(File file, Miner miner, boolean withFinalMarking, double precision) {
		if (withFinalMarking) {
			precisionWithFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, precision));
		} else {
			precisionWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, precision));
		}
		
		String withWithoutFinalMarking;
		if (withFinalMarking) {
			withWithoutFinalMarking = "with final marking ";
		} else {
			withWithoutFinalMarking = "without final marking ";
		}
		
		append(file, "&nbsp;&nbsp;&nbsp;&nbsp;precision " + withWithoutFinalMarking + precision + "<br>");
	}
	
	public synchronized void reportReplay(File file, Miner miner, boolean withFinalMarking, double fitness, double precision, double generalisation) {
		if (withFinalMarking) {
			fitnessWithFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, fitness));
			precisionWithFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, precision));
			generalisationWithFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, generalisation));
		} else {
			fitnessWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, fitness));
			precisionWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, precision));
			generalisationWithoutFinalMarking.put(new Pair<File, Miner>(file, miner), String.format(format, generalisation));
		}
		
		String withWithoutFinalMarking;
		if (withFinalMarking) {
			withWithoutFinalMarking = "with final marking ";
		} else {
			withWithoutFinalMarking = "without final marking ";
		}
		
		append(file, "&nbsp;&nbsp;&nbsp;&nbsp;fitness " + withWithoutFinalMarking + fitness + "<br>");
		append(file, "&nbsp;&nbsp;&nbsp;&nbsp;precision " + withWithoutFinalMarking + precision + "<br>");
		append(file, "&nbsp;&nbsp;&nbsp;&nbsp;generalisation " + withWithoutFinalMarking + generalisation + "<br>");
	}
	
	public synchronized String toHTMLString(boolean includeHTMLTags) {
		String result1 = "";
		String result2 = "";
		String result3 = "";
		String latex1 = "";
		String latex2 = "";
		String latex3 = "";
		
		//table
		int c = parameters.getMiners().size();
		result1 += "<table border=1>";
		latex1 += "\\begin{tabular}{p{2cm}" +String.format(String.format("%%0%dd", 3), 0).replace("0", "|"+String.format(String.format("%%0%dd", c), 0).replace("0","l"))+"|}";
		result2 += "<table border=1>";
		latex2 += "\\begin{tabular}{p{2cm}" +String.format(String.format("%%0%dd", 3), 0).replace("0", "|"+String.format(String.format("%%0%dd", c), 0).replace("0","l"))+"|}";
		result3 += "<table border=1>";
		latex3 += "\\begin{tabular}{p{2cm}" +String.format(String.format("%%0%dd", 2), 0).replace("0", "|"+String.format(String.format("%%0%dd", c), 0).replace("0","r"))+"|}";
		//result += "<tr><td rowspan=3></td><td colspan="+(c*3)+">with final marking</td><td colspan="+(c*3)+">without final marking</td><td colspan="+c+">mining time (ms)</td><td colspan="+c+">complexity (#arcs,#places,#transitions)</td></tr>";
		//latex += "&\\multicolumn{"+(c*3)+"}{|l|}{with final marking}&\\multicolumn{"+c+"}{|l|}{mining time (ms)}";
		//header
		result1 += "<tr><td>with final marking</td><td colspan="+c+">fitness</td><td colspan="+c+">precision</td><td colspan="+c+">generalisation</td>";
		latex1 += "\\\\with final marking&\\multicolumn{"+c+"}{|l|}{fitness}&\\multicolumn{"+c+"}{|l|}{precision}&\\multicolumn{"+c+"}{|l|}{generalisation}";
		result2 += "<tr><td>without final marking</td><td colspan="+c+">fitness</td><td colspan="+c+">precision</td><td colspan="+c+">generalisation</td>";
		latex2 += "\\\\without final marking&\\multicolumn{"+c+"}{|l|}{fitness}&\\multicolumn{"+c+"}{|l|}{precision}&\\multicolumn{"+c+"}{|l|}{generalisation}";
		result3 += "<tr><td></td><td colspan="+c+">simplicity (#arcs,#places,#transitions)</td><td colspan="+c+">mining time (ms)</td>";
		latex3 += "\\\\&\\multicolumn{"+c+"}{|l|}{simplicity (\\#arcs,\\#places,\\#transitions)}&\\multicolumn{"+c+"}{|l|}{mining time (ms)}";
		result1 += "<tr><td></td>";
		latex1 += "\\\\";
		result2 += "<tr><td></td>";
		latex2 += "\\\\";
		result3 += "<tr><td></td>";
		latex3 += "\\\\";
		for (int i=0;i<3;i++) {
			for (Miner miner : parameters.getMiners()) {
				result1 += "<td>" + miner.getIdentificationShort() + "</td>";
				latex1 += "&" + miner.getIdentificationShort();
				result2 += "<td>" + miner.getIdentificationShort() + "</td>";
				latex2 += "&" + miner.getIdentificationShort();
				if (i < 2) {
					result3 += "<td>" + miner.getIdentificationShort() + "</td>";
					latex3 += "&" + miner.getIdentificationShort();
				}
			}
		}
		result1 += "</tr>";
		result2 += "</tr>";
		result3 += "</tr>";
		//table body
		for (File file : files) {
			result1 += "<tr><td><div style='height:19px;width:150px;overflow:hidden;'>" + file.getName() + "</div></td>";
			latex1 += "\\\\\\hline "+file.getName();
			for (Miner miner : parameters.getMiners()) {
				result1 += "<td>"+fitnessWithFinalMarking.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex1 += "&"+fitnessWithFinalMarking.get(new Pair<File, Miner>(file, miner));
			}
			for (Miner miner : parameters.getMiners()) {
				result1 += "<td>"+precisionWithFinalMarking.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex1 += "&"+precisionWithFinalMarking.get(new Pair<File, Miner>(file, miner));
			}
			for (Miner miner : parameters.getMiners()) {
				result1 += "<td>"+generalisationWithFinalMarking.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex1 += "&"+generalisationWithFinalMarking.get(new Pair<File, Miner>(file, miner));
			}
			result1 += "</tr>";
			
			result2 += "<tr><td><div style='height:19px;width:150px;overflow:hidden;'>" + file.getName() + "</div></td>";
			latex2 += "\\\\\\hline "+file.getName();
			for (Miner miner : parameters.getMiners()) {
				result2 += "<td>"+fitnessWithoutFinalMarking.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex2 += "&"+fitnessWithoutFinalMarking.get(new Pair<File, Miner>(file, miner));
			}
			for (Miner miner : parameters.getMiners()) {
				result2 += "<td>"+precisionWithoutFinalMarking.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex2 += "&"+precisionWithoutFinalMarking.get(new Pair<File, Miner>(file, miner));
			}
			for (Miner miner : parameters.getMiners()) {
				result2 += "<td>"+generalisationWithoutFinalMarking.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex2 += "&"+generalisationWithoutFinalMarking.get(new Pair<File, Miner>(file, miner));
			}
			result2 += "</tr>";
			
			result3 += "<tr><td><div style='height:19px;width:150px;overflow:hidden;'>" + file.getName() + "</div></td>";
			latex3 += "\\\\\\hline "+file.getName();
			for (Miner miner : parameters.getMiners()) {
				result3 += "<td>"+numberOfArcs.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex3 += "&"+numberOfArcs.get(new Pair<File, Miner>(file, miner));
			}
			for (Miner miner : parameters.getMiners()) {
				result3 += "<td>"+miningTime.get(new Pair<File, Miner>(file, miner))+"</td>";
				latex3 += "&"+miningTime.get(new Pair<File, Miner>(file, miner));
			}
			result3 += "</tr>";
		}
		result1 += "</table>";
		latex1 += "\\end{tabular}";
		result2 += "</table>";
		latex2 += "\\end{tabular}";
		result3 += "</table>";
		latex3 += "\\end{tabular}";
		
		//start HTML
		String result = "";
		if (includeHTMLTags) {
			result += "<html>";
		}
		
		//configuration
		result += "Soundness check " + toString(parameters.getCheckSoundness()) + "<br>";
		result += "Replay of log " + toString(parameters.getReplayLog()) + "<br>";
		
		//insert tables
		result += result1 + "<br>" + result2 + "<br>" + result3 + "<br>";
		
		//timing information
		result += "Total time spent " + timers.getGlobalTime() + " ms <br>";
		for (Miner miner : parameters.getMiners()) {
			result += "&nbsp;&nbsp;in " + miner.getIdentification() + " " + timers.getTimeOfMiner(miner) + " ms <br>"; 
		}
		
		for (String s : this.result.values()) {
			result += s + "<br>";
		}
		result += latex1 + "\n\\\\" + latex2 + "\n\\\\" + latex3;
		if (includeHTMLTags) {
			result += "</html>";
		}
		return result;
	}
	
	public CompareMinersTimers getTimers() {
		return timers;
	}
	
	private String toString(boolean x) {
		if (x) {
			return "enabled";
		}
		return "disabled";
	}
}
