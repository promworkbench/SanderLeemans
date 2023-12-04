package svn48healthcare;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DecisionTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 2317118761985680948L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
				hasFocus);

		DecisionTreeNode treeNode = ((DecisionTreeNode) ((DefaultMutableTreeNode) value).getUserObject());

		result.setText(treeNode.getName() + " (" + treeNode.getOccurrences() + ")");

		return result;
	}

}
