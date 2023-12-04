package svn48healthcare;

import java.util.Collection;

import org.deckfour.xes.model.XTrace;

public interface DecisionTreeNode {

	public static enum Type {
		startTrace, activity, endTrace, attributeValue
	}

	public Type getType();

	public String getName();

	public String getId();

	public int getOccurrences();

	public Collection<XTrace> getTraces();

	public void addTrace(XTrace trace);

	public DecisionTreeNodeView getView();
}