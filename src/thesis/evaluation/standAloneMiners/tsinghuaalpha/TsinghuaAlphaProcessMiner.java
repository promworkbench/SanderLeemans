package thesis.evaluation.standAloneMiners.tsinghuaalpha;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.processmining.framework.log.AuditTrailEntry;
import org.processmining.framework.log.AuditTrailEntryList;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.LogSummary;
import org.processmining.framework.log.ProcessInstance;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.mining.MiningPlugin;
import org.processmining.mining.MiningResult;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.processmining.mining.petrinetmining.TsinghuaAlphaProcessMinerUI;

public class TsinghuaAlphaProcessMiner implements MiningPlugin {
	private SuccSeqs SuccessiveSequences = new SuccSeqs();
	private InterSeqs IntersectionalSequences = new InterSeqs();
	private OrderRelation OrderingRelations = new OrderRelation();
	public ArrayList Transitions = new ArrayList();
	public ArrayList FirstTransitions = new ArrayList();
	public ArrayList LastTransitions = new ArrayList();
	public ArrayList PlacesArcs = new ArrayList();
	private TsinghuaAlphaProcessMinerUI ui = null;

	public MiningResult mine(LogReader log) {
		//Progress progress = new Progress("Mining " + log.getFile().getShortName() + " using " + this.getName());
		//progress.setMinMax(0, 4);
		MiningResult result = this.mineIt(log);
		//progress.close();
		return result;
	}

	public JPanel getOptionsPanel(LogSummary summary) {
		if (this.ui == null) {
			this.ui = new TsinghuaAlphaProcessMinerUI(summary);
		}

		return this.ui;
	}

	public String getName() {
		return "Tsinghua-alpha algorithm plugin";
	}

	private MiningResult mineIt(LogReader log) {

		this.counting(log);

		this.ordering();

		this.mining3();

		PetriNet petrinet = new PetriNet();

		for (int psrc = 0; psrc < this.Transitions.size(); ++psrc) {
			petrinet.addTransition(new Transition(new LogEvent((String) this.Transitions.get(psrc), ""), petrinet));
		}

		Place arg9 = petrinet.addPlace("psource");

		for (int psink = 0; psink < this.FirstTransitions.size(); ++psink) {
			petrinet.addEdge(arg9,
					petrinet.findRandomTransition(new LogEvent((String) this.FirstTransitions.get(psink), "")));
		}

		Place arg10 = petrinet.addPlace("psink");

		int i;
		for (i = 0; i < this.LastTransitions.size(); ++i) {
			petrinet.addEdge(petrinet.findRandomTransition(new LogEvent((String) this.LastTransitions.get(i), "")),
					arg10);
		}

		for (i = 0; i < this.PlacesArcs.size(); ++i) {
			PredSucc2 ps = (PredSucc2) this.PlacesArcs.get(i);
			Place p = petrinet.addPlace("p" + (i + 1));

			int j;
			for (j = 0; j < ps.predecessor.size(); ++j) {
				petrinet.addEdge(petrinet.findRandomTransition(new LogEvent((String) ps.predecessor.get(j), "")), p);
			}

			for (j = 0; j < ps.successor.size(); ++j) {
				petrinet.addEdge(p, petrinet.findRandomTransition(new LogEvent((String) ps.successor.get(j), "")));
			}
		}

		petrinet.makeClusters();
		return new PetriNetResult(log, petrinet);
	}

	private void counting(LogReader log) {
		this.SuccessiveSequences.clear();
		this.IntersectionalSequences.clear();
		this.Transitions.clear();
		this.FirstTransitions.clear();
		this.LastTransitions.clear();
		String task = "";
		String type = "";
		ArrayList tasks_started = new ArrayList();
		ArrayList tasks_completed = new ArrayList();
		ArrayList tasks_ps = new ArrayList();
		int np = log.numberOfInstances();

		for (int i = 0; i < np; ++i) {
			ProcessInstance pi = log.getInstance(i);
			AuditTrailEntryList atel = pi.getAuditTrailEntryList();
			tasks_started.clear();
			tasks_completed.clear();
			tasks_ps.clear();
			boolean isFirst = true;

			for (int j = 0; j < atel.size(); ++j) {
				try {
					AuditTrailEntry ex = atel.get(j);
					task = ex.getElement();
					type = ex.getType();
					if (!this.Transitions.contains(task)) {
						this.Transitions.add(task);
					}

					if (isFirst && !this.FirstTransitions.contains(task)) {
						this.FirstTransitions.add(task);
					}

					isFirst = false;
					if (j == atel.size() - 1 && !this.LastTransitions.contains(task)) {
						this.LastTransitions.add(task);
					}

					int k;
					PredSucc2 arg19;
					if (!type.equalsIgnoreCase("start")) {
						if (type.equalsIgnoreCase("complete")) {
							tasks_started.remove(task);
							tasks_completed.add(task);

							for (k = tasks_ps.size() - 1; k >= 0; --k) {
								arg19 = (PredSucc2) tasks_ps.get(k);
								if (arg19.successor.contains(task)) {
									tasks_ps.remove(k);
								}
							}
						}
					} else {
						for (k = 0; k < tasks_started.size(); ++k) {
							String ps = (String) tasks_started.get(k);
							this.IntersectionalSequences.addCount(ps, task);
						}

						tasks_started.add(task);
						if (tasks_completed.size() > 0) {
							tasks_ps.add(new PredSucc2(tasks_completed));
							tasks_completed.clear();
						}

						for (k = 0; k < tasks_ps.size(); ++k) {
							arg19 = (PredSucc2) tasks_ps.get(k);
							arg19.successor.add(task);

							for (int l = 0; l < arg19.predecessor.size(); ++l) {
								String t = (String) arg19.predecessor.get(l);
								this.SuccessiveSequences.addCount(t, task);
							}
						}
					}
				} catch (IOException arg17) {
					;
				} catch (IndexOutOfBoundsException arg18) {
					;
				}
			}
		}

	}

	private void ordering() {
		this.OrderingRelations.clear();

		for (int i = 0; i < this.Transitions.size(); ++i) {
			String task_pred = (String) this.Transitions.get(i);

			for (int j = 0; j < this.Transitions.size(); ++j) {
				byte rel = 0;
				String task_succ = (String) this.Transitions.get(j);
				if (this.IntersectionalSequences.getCount(task_pred, task_succ) > 0) {
					rel = 2;
				} else if (this.SuccessiveSequences.getCount(task_pred, task_succ) > 0) {
					rel = 1;
				}

				this.OrderingRelations.setRel(task_pred, task_succ, rel);
			}
		}

	}

	private void mining3() {
		ArrayList pa_temp = new ArrayList();
		PredSuccUtil psu = new PredSuccUtil(this.OrderingRelations);

		for (int i = 0; i < this.Transitions.size(); ++i) {
			String task_pred = (String) this.Transitions.get(i);

			for (int j = 0; j < this.Transitions.size(); ++j) {
				String task_succ = (String) this.Transitions.get(j);
				int rel = this.OrderingRelations.getRel(task_pred, task_succ);
				if (rel == 1) {
					pa_temp.clear();
					boolean isInserted = false;

					int n;
					PredSucc2 ps;
					for (n = this.PlacesArcs.size() - 1; n >= 0; --n) {
						PredSucc2 m = new PredSucc2();
						m.predecessor.add(task_pred);
						m.successor.add(task_succ);
						ps = (PredSucc2) this.PlacesArcs.get(n);
						int m1;
						String t;
						if (ps.predecessor.contains(task_pred)) {
							if (ps.successor.contains(task_succ)) {
								isInserted = true;
							} else {
								for (m1 = 0; m1 < ps.predecessor.size(); ++m1) {
									t = (String) ps.predecessor.get(m1);
									if (!t.equals(task_pred) && this.OrderingRelations.getRel(t, task_succ) == 1) {
										m.predecessor.add(t);
									}
								}

								for (m1 = 0; m1 < ps.successor.size(); ++m1) {
									t = (String) ps.successor.get(m1);
									if (this.OrderingRelations.getRel(t, task_succ) != 2) {
										m.successor.add(t);
									}
								}

								if (m.predecessor.size() == ps.predecessor.size()
										&& m.successor.size() == ps.successor.size() + 1) {
									ps.successor.add(task_succ);
									isInserted = true;
								} else if ((m.predecessor.size() != 1 || m.successor.size() != 1)
										&& !psu.isContained(m, pa_temp)) {
									pa_temp.add(m);
								}
							}
						} else if (ps.successor.contains(task_succ)) {
							for (m1 = 0; m1 < ps.successor.size(); ++m1) {
								t = (String) ps.successor.get(m1);
								if (!t.equals(task_succ) && this.OrderingRelations.getRel(task_pred, t) == 1) {
									m.successor.add(t);
								}
							}

							for (m1 = 0; m1 < ps.predecessor.size(); ++m1) {
								t = (String) ps.predecessor.get(m1);
								if (this.OrderingRelations.getRel(t, task_pred) != 2) {
									m.predecessor.add(t);
								}
							}

							if (m.predecessor.size() == ps.predecessor.size() + 1
									&& m.successor.size() == ps.successor.size()) {
								ps.predecessor.add(task_pred);
								isInserted = true;
							} else if ((m.predecessor.size() != 1 || m.successor.size() != 1)
									&& !psu.isContained(m, pa_temp)) {
								pa_temp.add(m);
							}
						} else {
							for (m1 = 0; m1 < ps.successor.size(); ++m1) {
								t = (String) ps.successor.get(m1);
								if (this.OrderingRelations.getRel(task_pred, t) == 1) {
									m.successor.add(t);
								}
							}

							for (m1 = 0; m1 < ps.predecessor.size(); ++m1) {
								t = (String) ps.predecessor.get(m1);
								if (this.OrderingRelations.getRel(t, task_pred) != 2) {
									m.predecessor.add(t);
								}
							}

							for (m1 = 0; m1 < ps.predecessor.size(); ++m1) {
								t = (String) ps.predecessor.get(m1);
								if (this.OrderingRelations.getRel(t, task_succ) != 1) {
									m.predecessor.remove(t);
								}
							}

							for (m1 = 0; m1 < ps.successor.size(); ++m1) {
								t = (String) ps.successor.get(m1);
								if (this.OrderingRelations.getRel(t, task_succ) == 2) {
									m.successor.remove(t);
								}
							}

							if (m.predecessor.size() == ps.predecessor.size() + 1
									&& m.successor.size() == ps.successor.size() + 1) {
								ps.predecessor.add(task_pred);
								ps.successor.add(task_succ);
								isInserted = true;
							} else if ((m.predecessor.size() != 1 || m.successor.size() != 1)
									&& !psu.isContained(m, pa_temp)) {
								pa_temp.add(m);
							}
						}
					}

					if (!isInserted) {
						PredSucc2 arg13 = new PredSucc2();
						arg13.predecessor.add(task_pred);
						arg13.successor.add(task_succ);
						if (!psu.isContained(arg13, pa_temp)) {
							pa_temp.add(arg13);
						}
					}

					n = pa_temp.size();
					if (n > 0) {
						for (int arg14 = 0; arg14 < n; ++arg14) {
							ps = (PredSucc2) pa_temp.get(arg14);
							if (!psu.isContained(ps, this.PlacesArcs)) {
								this.PlacesArcs.add(ps);
							}
						}
					}
				}
			}
		}

	}

	public String getHtmlDescription() {
		return "<h1>" + this.getName() + "</h1>";
	}
}