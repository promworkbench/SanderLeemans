package svn44systemprecision;

import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;

import gnu.trove.list.array.TIntArrayList;

public class DirectlyFollowsModelSemanticsImpl implements DirectlyFollowsModelSemantics {

	private int startNode;
	private int endNode;
	private String[] transitionIndex2name;
	private int[][] outgoingEdges;

	private int state = -1;

	public DirectlyFollowsModelSemanticsImpl(DirectlyFollowsModel dfm) {
		startNode = dfm.getNumberOfNodes();
		endNode = dfm.getNumberOfNodes() + 1;

		transitionIndex2name = new String[dfm.getNumberOfNodes()];
		for (int node : dfm.getNodeIndices()) {
			transitionIndex2name[node] = dfm.getNodeOfIndex(node);
		}

		outgoingEdges = new int[dfm.getNumberOfNodes() + 2][];
		for (int node : dfm.getNodeIndices()) {
			TIntArrayList x = new TIntArrayList();
			if (dfm.getEndNodes().contains(node)) {
				x.add(endNode);
			}
			for (long edge : dfm.getEdges()) {
				if (dfm.getEdgeSource(edge) == node) {
					x.add(dfm.getEdgeTarget(edge));
				}
			}
			outgoingEdges[node] = x.toArray();
		}

		if (dfm.isEmptyTraces()) {
			TIntArrayList x = new TIntArrayList(dfm.getStartNodes());
			x.add(endNode);
			outgoingEdges[startNode] = x.toArray();
		} else {
			outgoingEdges[startNode] = dfm.getStartNodes().toArray();
		}

		outgoingEdges[endNode] = new int[0];
	}

	public void executeTransition(int transitionIndex) {
		state = transitionIndex;
	}

	public int[] getEnabledTransitions() {
		return outgoingEdges[state];
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getInitialState() {
		return startNode;
	}

	public boolean isFinalState() {
		return state == endNode;
	}

	public String getLabel(int transitionIndex) {
		return transitionIndex2name[transitionIndex];
	}

	public DirectlyFollowsModelSemanticsImpl clone() {
		DirectlyFollowsModelSemanticsImpl result;
		try {
			result = (DirectlyFollowsModelSemanticsImpl) super.clone();
			result.startNode = startNode;
			result.endNode = endNode;
			result.transitionIndex2name = transitionIndex2name;
			result.outgoingEdges = outgoingEdges;
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
