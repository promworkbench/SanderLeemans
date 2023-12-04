package svn48healthcare;

public class DecisionTreeNodeRoot extends DecisionTreeNodeAbstract {

	@Override
	public Type getType() {
		return Type.startTrace;
	}

	@Override
	public String getName() {
		return "[all traces]";
	}

}