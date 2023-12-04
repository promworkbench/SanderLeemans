package caise2020isextension;

import java.io.File;

import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class TestLog {

	public static void main(String[] args) throws Exception {
		//		File logFile = new File(
		//				"/home/sander/Documents/svn/20 - stochastic conformance checking - Artem/05 - IS paper invited/experiment/exampleLog.xes.gz");
		//		XLogWriterIncremental writer = new XLogWriterIncremental(logFile);
		//
		//		//empty trace
		//		writer.startTrace();
		//		writer.endTrace();
		//
		//		//a
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//
		//		//aa
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//
		//		//aaa
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//
		//		//aaaa
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//		writer.startTrace();
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.writeEvent("a", "complete");
		//		writer.endTrace();
		//
		//		writer.close();
		//
		//		PluginContext context = new FakeContext();
		//		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);
		//		StochasticDeterministicFiniteAutomatonMapped logAutomaton = Log2StochasticDeterministicFiniteAutomaton
		//				.convert(log, new XEventNameClassifier(), new ProMCanceller() {
		//					public boolean isCancelled() {
		//						return false;
		//					}
		//				});
		//
		//		double logEntropy = Entropy.entropy(logAutomaton);
		//		System.out.println("log entropy " + logEntropy);
		//
		//		StochasticDeterministicFiniteAutomatonMappedImpl modelAutomaton = new StochasticDeterministicFiniteAutomatonMappedImpl();
		//
		//		int state = modelAutomaton.getInitialState();
		//		short a = modelAutomaton.transform("a");
		//
		//		modelAutomaton.addEdge(state, a, state, 0.8);
		//		modelAutomaton.addEdge(state, a, state, 0.5);
		//
		//		double modelEntropy = Entropy.entropy(modelAutomaton);
		//		System.out.println("model entropy " + modelEntropy);
		//
		//		StochasticLanguageLog languageLog = XLog2StochasticLanguage.convert(log, new XEventNameClassifier(),
		//				new ProMCanceller() {
		//					public boolean isCancelled() {
		//						return false;
		//					}
		//				});
		//
		//		BigDecimal ent = GainEntropy.conjunctiveEntropy(languageLog, modelAutomaton);
		//		System.out.println("conjunctive entropy " + ent);
		//
		//		System.out.println("gain precision " + (ent.doubleValue() / modelEntropy));
		//		System.out.println("gain recall " + (ent.doubleValue() / logEntropy));

		//another test log
		{
			//make the Petri net
			StochasticNet net = new StochasticNetImpl("det trace model");
			net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
			net.setTimeUnit(TimeUnit.HOURS);
			Place source = net.addPlace("source");
			Marking marking = new Marking();
			marking.add(source);

			//a
			{
				TimedTransition a = net.addImmediateTransition("a", 1);
				net.addArc(source, a);
				Place pa = net.addPlace("pa");
				net.addArc(a, pa);

				//b
				{
					TimedTransition ab = net.addImmediateTransition("b", 0.1);
					net.addArc(pa, ab);
					Place pab = net.addPlace("pab");
					net.addArc(ab, pab);

					//c
					{
						TimedTransition abc = net.addImmediateTransition("c", 1);
						net.addArc(pab, abc);
						Place pabc = net.addPlace("pabc");
						net.addArc(abc, pabc);
					}
				}

				//c
				{
					TimedTransition ac = net.addImmediateTransition("c", 0.15);
					net.addArc(pa, ac);
					Place pac = net.addPlace("pac");
					net.addArc(ac, pac);

					//b
					{
						TimedTransition acb = net.addImmediateTransition("b", 1);
						net.addArc(pac, acb);
						Place pacb = net.addPlace("pacb");
						net.addArc(acb, pacb);
					}
				}

				//d
				{
					TimedTransition ad = net.addImmediateTransition("d", 0.75);
					net.addArc(pa, ad);
					Place pad = net.addPlace("pad");
					net.addArc(ad, pad);

					//stop
					{
						TimedTransition adt = net.addImmediateTransition("t", 0.3);
						adt.setInvisible(true);
						net.addArc(pad, adt);
						Place padt = net.addPlace("padt");
						net.addArc(adt, padt);
					}

					//e
					{
						TimedTransition ade = net.addImmediateTransition("e", 0.45);
						net.addArc(pad, ade);
						Place pade = net.addPlace("pade");
						net.addArc(ade, pade);

						//d
						{
							TimedTransition aded = net.addImmediateTransition("d", 1);
							net.addArc(pade, aded);
							Place paded = net.addPlace("paded");
							net.addArc(aded, paded);

							//stop
							{
								TimedTransition adedt = net.addImmediateTransition("t", 0.2);
								adedt.setInvisible(true);
								net.addArc(paded, adedt);
								Place padedt = net.addPlace("padedt");
								net.addArc(adedt, padedt);
							}

							//e
							{
								TimedTransition adede = net.addImmediateTransition("e", 0.25);
								net.addArc(paded, adede);
								Place padede = net.addPlace("padede");
								net.addArc(adede, padede);

								//d
								{
									TimedTransition adeded = net.addImmediateTransition("d", 1);
									net.addArc(padede, adeded);
									Place padeded = net.addPlace("padeded");
									net.addArc(adeded, padeded);

									//stop
									{
										TimedTransition adededt = net.addImmediateTransition("t", 0.15);
										adededt.setInvisible(true);
										net.addArc(padeded, adededt);
										Place padededt = net.addPlace("padededt");
										net.addArc(adededt, padededt);
									}

									//e
									{
										TimedTransition adedede = net.addImmediateTransition("e", 0.1);
										net.addArc(padeded, adedede);
										Place padedede = net.addPlace("padedede");
										net.addArc(adedede, padedede);

										//d
										{
											TimedTransition adededed = net.addImmediateTransition("d", 1);
											net.addArc(padedede, adededed);
											Place padededed = net.addPlace("padededed");
											net.addArc(adededed, padededed);
										}
									}
								}
							}
						}
					}
				}
			}

			//store
			File modelFile = new File(
					"/home/sander/Documents/svn/20 - stochastic conformance checking - Artem/05 - IS paper invited/experiment/illustration log trace model.pnml");
			PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking,
					new FakeGraphLayoutConnection(net));
			Serializer serializer = new Persister();
			serializer.write(root, modelFile);
		}

		//smart flower 
		{
			//make the Petri net
			StochasticNet net = new StochasticNetImpl("det trace model");
			net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
			net.setTimeUnit(TimeUnit.HOURS);
			Place source = net.addPlace("source");
			Marking marking = new Marking();
			marking.add(source);

			//start
			TimedTransition start = net.addImmediateTransition("t", 1);
			start.setInvisible(true);
			net.addArc(source, start);
			Place flower = net.addPlace("flower");
			net.addArc(start, flower);

			//a
			TimedTransition a = net.addImmediateTransition("a", 0.23);
			net.addArc(flower, a);
			net.addArc(a, flower);

			//b
			TimedTransition b = net.addImmediateTransition("b", 0.06);
			net.addArc(flower, b);
			net.addArc(b, flower);

			//c
			TimedTransition c = net.addImmediateTransition("c", 0.06);
			net.addArc(flower, c);
			net.addArc(c, flower);

			//d
			TimedTransition d = net.addImmediateTransition("d", 0.29);
			net.addArc(flower, d);
			net.addArc(d, flower);

			//e
			TimedTransition e = net.addImmediateTransition("e", 0.12);
			net.addArc(flower, e);
			net.addArc(e, flower);

			//end
			TimedTransition end = net.addImmediateTransition("t", 0.24);
			end.setInvisible(true);
			net.addArc(flower, end);
			Place sink = net.addPlace("sink");
			net.addArc(end, sink);

			//store
			File modelFile = new File(
					"/home/sander/Documents/svn/20 - stochastic conformance checking - Artem/05 - IS paper invited/experiment/illustration log flower model.pnml");
			PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking,
					new FakeGraphLayoutConnection(net));
			Serializer serializer = new Persister();
			serializer.write(root, modelFile);
		}
	}
}