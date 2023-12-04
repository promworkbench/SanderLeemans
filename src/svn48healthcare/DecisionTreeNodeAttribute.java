package svn48healthcare;

public class DecisionTreeNodeAttribute extends DecisionTreeNodeAbstract {

	private final String attribute;
	private final String value;

	public DecisionTreeNodeAttribute(String attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}

	public Type getType() {
		return Type.attributeValue;
	}

	public String getName() {
		return value;
	}

}
