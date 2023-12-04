package is2020;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModelImpl;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa.ComputeReallocationMatrix2;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage2String;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class Aplus {
	@Plugin(name = "IS2020 create GSPN a+", returnLabels = { "Stochastic Petri net a+" }, returnTypes = {
			StochasticNet.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic language.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public StochasticNet convert(final PluginContext context) {
		return createAPlus();
	}

	public static void main(String[] args) throws IllegalTransitionException, InterruptedException {
		experiment2();
	}

	public static void experiment2() throws InterruptedException {
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		EMSCParametersLogLogDefault parameters = new EMSCParametersLogLogDefault();

		XLog logA = createAAa();
		Activity2IndexKey activityKey = new Activity2IndexKey();
		StochasticLanguage<TotalOrder> languageLogA = XLog2StochasticLanguage.convert(logA, parameters.getClassifierA(),
				activityKey, canceller);

		for (int traces = 1; traces <= 20; traces += 1) {
			StochasticPathLanguage<TotalOrder> languageModel = createAAa(traces);

			Triple<ReallocationMatrix, Double, DistanceMatrix<TotalOrder, TotalOrder>> r = ComputeReallocationMatrix2
					.compute(languageLogA, languageModel, parameters.getDistanceMatrix(), parameters, canceller);

			String language = StochasticLanguage2String.toString(languageModel, true);

			System.out.println(traces + " " + r.getB() + " " + language);
		}
	}

	public static void experiment() throws InterruptedException {
		XLog log = createAAa();
		StochasticNet net = createAPlus();
		Marking initialMarking = EarthMoversStochasticConformancePlugin.getInitialMarking(net);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		for (int m = 1; m <= 99; m += 1) {
			EMSCParametersLogModelDefault parameters = new EMSCParametersLogModelDefault();
			parameters.setModelTerminationStrategy(
					new LanguageGenerationStrategyFromModelImpl(Long.MAX_VALUE, m / 100.0, Integer.MAX_VALUE));
			StochasticTraceAlignmentsLogModel r = EarthMoversStochasticConformancePlugin.measureLogModel(log, net,
					initialMarking, parameters, canceller);
			//			String language = StochasticLanguageModel2String
			//					.convert(EarthMoversStochasticConformancePlugin.lastModelLanguage);

			//			System.out.println(m + " " + r.getSimilarity() + " " + language);
			System.out.println(m + " " + r.getSimilarity());
		}
	}

	public static StochasticPathLanguage<TotalOrder> createAAa(final int traces) {
		Activity2IndexKey activityKey = new Activity2IndexKey();
		final int activityIndex = activityKey.feed("a");

		return new StochasticPathLanguage<TotalOrder>() {
			public int size() {
				return traces;
			}

			public StochasticPathIterator<TotalOrder> iterator() {
				return new StochasticPathIterator<TotalOrder>() {
					int t = 0;

					public boolean hasNext() {
						return t < traces;
					}

					public int getTraceIndex() {
						return t;
					}

					public String[] get() {
						String[] result = new String[t];
						for (int i = 0; i < t; i++) {
							result[i] = "a";
						}
						return result;
					}

					//					public String[] next() {
					//						t++;
					//						return get();
					//					}

					public double getProbability() {
						return 1 / Math.pow(2, t);
					}

					public int[] nextPath() {
						t++;
						return getPath();
					}

					public int[] getPath() {
						int[] result = new int[t];
						for (int i = 0; i < t; i++) {
							result[i] = activityIndex;
						}
						return result;
					}

					public int[] next() {
						t++;
						int[] result = new int[t];
						for (int i = 0; i < t; i++) {
							result[i] = activityIndex;
						}
						return result;
					}
				};
			}

			public String getTraceString(int traceIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public int[] getTrace(int traceIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public Activity2IndexKey getActivityKey() {
				// TODO Auto-generated method stub
				return null;
			}

			public int[] getPath(int pathIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public StochasticTransition2IndexKey getTransitionKey() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public static XLog createAAa() {
		XLog result = new XLogImpl(new XAttributeMapImpl());

		{
			XTrace a = new XTraceImpl(new XAttributeMapImpl());
			XEvent e = new XEventImpl();
			e.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e);
			result.add(a);
		}

		{
			XTrace a = new XTraceImpl(new XAttributeMapImpl());
			XEvent e = new XEventImpl();
			e.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e);
			XEvent e2 = new XEventImpl();
			e2.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e2);
			result.add(a);
		}

		{
			XTrace a = new XTraceImpl(new XAttributeMapImpl());
			XEvent e = new XEventImpl();
			e.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e);
			XEvent e2 = new XEventImpl();
			e2.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e2);
			result.add(a);
		}

		{
			XTrace a = new XTraceImpl(new XAttributeMapImpl());
			XEvent e = new XEventImpl();
			e.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e);
			XEvent e2 = new XEventImpl();
			e2.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "a"));
			a.add(e2);
			result.add(a);
		}

		return result;
	}

	public static StochasticNet createAPlus() {
		StochasticNetImpl result = new StochasticNetImpl("a+");
		result.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		result.setTimeUnit(TimeUnit.HOURS);

		Place source = result.addPlace("source");

		TimedTransition t1 = result.addImmediateTransition("silent start", 1);
		t1.setInvisible(true);
		result.addArc(source, t1);

		Place p1 = result.addPlace("p1");
		result.addArc(t1, p1);

		TimedTransition a1 = result.addImmediateTransition("a", 0.5);
		result.addArc(p1, a1);
		result.addArc(a1, p1);

		TimedTransition a2 = result.addImmediateTransition("a", 0.5);
		result.addArc(p1, a2);

		Place sink = result.addPlace("sink");
		result.addArc(a2, sink);

		return result;
	}
}
