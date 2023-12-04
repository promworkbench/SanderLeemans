package svn45crimes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGen;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGenRes;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import nl.tue.astar.AStarException;
import thesis.helperClasses.FakeContext;

public class MeasureAryaStandAlone implements Measure {

	public static void main(String... args) throws Exception {
		File logFile = new File(args[0]);
		File modelFile = new File(args[1]);
		File outFile = new File(args[2]);

		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

		AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
		aNet.importFromStream(new FakeContext(), new FileInputStream(modelFile));
		Triple<Double, TransEvClassMapping, PNRepResult> t = MeasureArya.AryaFitness(aNet, log);
		double fitness = t.getA();
		TransEvClassMapping mapping = t.getB();
		PNRepResult result = t.getC();

		AlignmentPrecGen precisionMeasurer2 = new AlignmentPrecGen();
		AlignmentPrecGenRes precisionGeneralisation = precisionMeasurer2.measureConformanceAssumingCorrectAlignment(
				context, mapping, result, aNet.getNet(), aNet.getInitialMarking(), true);
		double precision = precisionGeneralisation.getPrecision();
		double generalisation = precisionGeneralisation.getGeneralization();

		PrintWriter writer = new PrintWriter(outFile);
		writer.write(fitness + "");
		writer.write("Z");
		writer.write(precision + "");
		writer.write("Z");
		writer.write(generalisation + "");
		writer.close();//
	}

	public String getTitle() {
		return "align";
	}

	public String getLatexTitle() {
		return "alignment-based";
	}

	public String[] getMeasureNames() {
		return new String[] { "fitness", "precision", "generalisation" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "fitness", "precision", "generalisation" };
	}

	public boolean isSupportsTrees() {
		return false;
	}

	public boolean printTime() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 3;
	}

	public double[] compute(File logFile, XLog log, EfficientTree model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		return null;
	}

	public double[] compute(File logFile, XLog log, AcceptingPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {

		PluginContext context = new FakeContext();

		File modelFile = File.createTempFile("model", ".pnml");
		model.exportToFile(context, modelFile);

		File outFile = File.createTempFile("arya", ".txt");

		boolean file = true;

		int wf = 0;
		StringBuilder error = new StringBuilder();
		if (file) {
			ProcessBuilder pb = new ProcessBuilder("java",
					"-Djava.library.path=/home/sander/eclipse-workspace/SanderLeemans/lib/ux64", "-jar",
					"/home/sander/eclipse-workspace/SanderLeemans/src/svn45crimes/aryastandalone.jar",
					logFile.getAbsolutePath(), modelFile.getAbsolutePath(), outFile.getAbsolutePath());
			System.out.println("\"" + String.join("\" \"", pb.command()) + "\"");

			Process p = pb.start();
			error = AlgorithmSplit.capture(p.getErrorStream());
			AlgorithmSplit.out2std(p.getInputStream());

			wf = p.waitFor();
		} else {
			main(logFile.getAbsolutePath(), modelFile.getAbsolutePath(), outFile.getAbsolutePath());
		}

		modelFile.delete();
		outFile.deleteOnExit();

		if (!error.toString().isEmpty()) {
			throw new Exception(error.toString());
		} else if (wf != 0) {
			throw new Exception("exit code " + wf);
		} else {
			BufferedReader r = new BufferedReader(new FileReader(outFile));
			String[] x = r.readLine().split("Z");
			r.close();
			return new double[] { Double.valueOf(x[0]), Double.valueOf(x[1]), Double.valueOf(x[2]) };
		}
	}

}
