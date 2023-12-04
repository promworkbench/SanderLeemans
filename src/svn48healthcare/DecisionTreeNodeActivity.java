package svn48healthcare;

public class DecisionTreeNodeActivity extends DecisionTreeNodeAbstract {

	private final String activity;

	public DecisionTreeNodeActivity(String activity) {
		this.activity = activity;
	}

	@Override
	public Type getType() {
		return Type.activity;
	}

	@Override
	public String getName() {
		return activity;
	}

}