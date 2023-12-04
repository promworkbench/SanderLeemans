package svn48healthcare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.deckfour.xes.model.XTrace;

public abstract class DecisionTreeNodeAbstract implements DecisionTreeNode {
	private final List<XTrace> traces;
	private final String id = UUID.randomUUID().toString();
	private DecisionTreeNodeView view;

	public DecisionTreeNodeAbstract() {
		traces = new ArrayList<>();
	}

	@Override
	public void addTrace(XTrace trace) {
		traces.add(trace);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getOccurrences() {
		return traces.size();
	}

	@Override
	public Collection<XTrace> getTraces() {
		return traces;
	}

	public void setView(DecisionTreeNodeView view) {
		this.view = view;
	}

	@Override
	public DecisionTreeNodeView getView() {
		return view;
	}
}