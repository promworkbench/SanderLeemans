package generalisation2;

import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.fitness.TreeFitnessAbstract;
import org.processmining.plugins.etm.fitness.TreeFitnessInfo;
import org.processmining.plugins.etm.fitness.TreeFitnessInfo.Dimension;
import org.processmining.plugins.etm.fitness.metrics.FitnessReplay;
import org.processmining.plugins.etm.fitness.metrics.PrecisionEscEdges;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.termination.ProMCancelTerminationCondition;

public class Precision2 extends TreeFitnessAbstract {

	//	@Plugin(name = "Compute generalisation2", returnLabels = { "Generalisation2" }, returnTypes = { Object.class }, parameterLabels = { "Aligned Log" }, userAccessible = true)
	//	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	//	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	//	public Object generalisation2(PluginContext context, AlignedLog aLog) {
	//		long syncMoves = 0;
	//		long allMoves = 0;
	//		for (AlignedTrace trace : aLog) {
	//			for (Move move : trace) {
	//				if (!(move.getUnode().getNode() instanceof Automatic)) {
	//					if (move.isSyncMove()) {
	//						syncMoves += aLog.getCardinalityOf(trace);
	//					}
	//					allMoves += aLog.getCardinalityOf(trace);
	//				}
	//			}
	//		}
	//
	//		System.out.println("synchronous moves " + syncMoves);
	//		System.out.println("total moves " + allMoves);
	//		System.out.println("generalisation2 " + (syncMoves / (allMoves * 1.0)));
	//
	//		return new Object();
	//	}

	public static TreeFitnessInfo info = new TreeFitnessInfo(Precision2.class, "P2", "Precision measured on test log",
			"Precision measured on test log", Dimension.PRECISION, true);

	private final CentralRegistry registry;

	private PrecisionEscEdges precisionReplayTestLog = null;

	private FitnessReplay fitnessReplayTestLog;

	public Precision2(CentralRegistry registry) {
		System.out.println("construct " + this);
		this.registry = registry;
	}

	public Precision2(Precision2 generalisation2) {
		this.registry = generalisation2.registry;
		System.out.println("construct " + this);
	}

	public synchronized void setTestLog(XLog testLog) {
		CentralRegistry registryTestLog = new CentralRegistry(testLog, registry.getEventClasses().getClassifier(),
				new Random());
		fitnessReplayTestLog = new FitnessReplay(registryTestLog, ProMCancelTerminationCondition.buildDummyCanceller());
		precisionReplayTestLog = new PrecisionEscEdges(registryTestLog);
		notifyAll();
		System.out.println(" set log " + this);
	}

	public double getFitness(NAryTree candidate, List<? extends NAryTree> population) {
		while (precisionReplayTestLog == null) {
			synchronized (this) {
				System.out.println("wait for log " + this);
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		fitnessReplayTestLog.getFitness(candidate, null);
		return precisionReplayTestLog.getFitness(candidate, null);
	}

	public TreeFitnessInfo getInfo() {
		return info;
	}

}
