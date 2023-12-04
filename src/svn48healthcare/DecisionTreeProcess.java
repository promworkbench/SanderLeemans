package svn48healthcare;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class DecisionTreeProcess extends JFrame {

	private static final long serialVersionUID = 2134302750644603287L;

	private JPanel nodeViews;
	private final CardLayout nodeViewsLayout;
	private CostModel costModel;

	public static void main(String[] args) throws Exception {
		File logFile = new File(
				"/home/sander/Documents/svn/48 - stochastic process mining in healthcare - Andrew Partington/03 - an additional experiment/Sepsis.ftree-filterTree result/Sepsis.xes.gz1.xes.gz");
		//"/home/sander/Documents/svn/48 - stochastic process mining in healthcare - Andrew Partington/03 - an additional experiment/TRANSFERS.ftree-filterTree result/TRANSFERS.csv1.xes.gz");
		//"/home/sander/Documents/svn/48 - stochastic process mining in healthcare - Andrew Partington/filtertreetest.ftree-filterTree result/COPY_Eventlog_2020Census_210705_07.csv2.xes.gz");
		//"/home/sander/Desktop/BPIC'12 a_activities.xes");

		System.out.println("loading log " + LocalDateTime.now());

		//load log
		XLog log;
		{
			PluginContext context = new FakeContext();
			log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

			System.out.println("log loaded " + LocalDateTime.now());
		}

		//		//expand button
		//		{
		//			JButton expandButton = new JButton("expand all");
		//			panel.add(expandButton, BorderLayout.PAGE_END);
		//			expandButton.addActionListener(new ActionListener() {
		//
		//				public void actionPerformed(ActionEvent arg0) {
		//					expandAllNodes(tree);
		//				}
		//
		//			});
		//		}
		new DecisionTreeProcess(log);
	}

	public DecisionTreeProcess(XLog log) throws Exception {
		super("PMDA View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container body = getContentPane();
		body.setLayout(new GridBagLayout());

		//node views
		{
			nodeViews = new JPanel();
			nodeViewsLayout = new CardLayout();
			nodeViews.setLayout(nodeViewsLayout);
			nodeViews.setOpaque(false);

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			body.add(nodeViews, c);
		}

		//tree view
		JScrollPane treeScrollPanel;
		final JTree tree;
		{
			//create tree
			System.out.println("creating nodes " + LocalDateTime.now());
			DefaultMutableTreeNode root = createNodes(log, new XEventNameClassifier());
			System.out.println("nodes created " + LocalDateTime.now());
			tree = new JTree(root);
			tree.setCellRenderer(new DecisionTreeCellRenderer());
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					//new selection
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (node == null) {
						return;
					}

					Object nodeInfo = node.getUserObject();
					if (nodeInfo != null) {
						assert nodeInfo instanceof DecisionTreeNode;
						DecisionTreeNode tNode = (DecisionTreeNode) nodeInfo;

						//show the view
						{
							DecisionTreeNodeView nodeView = tNode.getView();
							String id = tNode.getId();

							nodeViews.removeAll();
							nodeViews.add(nodeView.getPanel(), id);
							nodeViews.revalidate();
							nodeViewsLayout.show(nodeViews, id);
						}

						//update the view
						((DecisionTreeNode) nodeInfo).getView().fillTable();

						//update the view with the cost model
						if (costModel != null) {
							((DecisionTreeNode) nodeInfo).getView().fillTableCostModel(costModel);
						}
					}
				}
			});

			treeScrollPanel = new JScrollPane(tree);
			treeScrollPanel.setOpaque(false);
			treeScrollPanel.getViewport().setOpaque(false);
			treeScrollPanel.setPreferredSize(new Dimension(200, 1));
			treeScrollPanel.setMinimumSize(new Dimension(200, 1));
			treeScrollPanel.setMaximumSize(null);

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0.5;
			c.weighty = 1;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			body.add(treeScrollPanel, c);
		}

		pack();
		setSize(500, 500);
		setVisible(true);

		tree.setSelectionInterval(0, 0);
	}

	public List<DecisionTreeNodeAbstract> getAllTreeNodes(DefaultMutableTreeNode root) {
		ArrayList<DecisionTreeNodeAbstract> result = new ArrayList<>();
		getAllTreeNodes(result, root);
		return result;
	}

	public void createNodeView(DecisionTreeNodeAbstract node) {
		DecisionTreeNodeView view = new DecisionTreeNodeView(node);
		node.setView(view);
	}

	private void getAllTreeNodes(List<DecisionTreeNodeAbstract> result, DefaultMutableTreeNode node) {
		result.add((DecisionTreeNodeAbstract) node.getUserObject());
		for (@SuppressWarnings("unchecked")
		Enumeration<TreeNode> it = node.children(); it.hasMoreElements();) {
			getAllTreeNodes(result, (DefaultMutableTreeNode) it.nextElement());
		}
	}

	public static void expandAllNodes(final JTree tree) {
		int j = tree.getRowCount();
		int i = 0;
		while (i < j) {
			tree.expandRow(i);
			i += 1;
			j = tree.getRowCount();
		}
	}

	public DefaultMutableTreeNode createNodes(XLog log, XEventClassifier classifier) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new DecisionTreeNodeRoot());

		//compute the cost model asynchronously
		(new Thread("cost model thread") {
			public void run() {
				try {
					System.out.println("compute cost model " + LocalDateTime.now());
					costModel = DecisionTree2CostModel.computeModel(log, classifier).getA();
					System.out.println("cost model done    " + LocalDateTime.now());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		CostModel costModel = null;

		for (XTrace trace : log) {
			((DecisionTreeNodeAbstract) root.getUserObject()).addTrace(trace);
			addTrace(trace, root, classifier, costModel);
		}

		sortAllNodes(root);

		for (DecisionTreeNodeAbstract node : getAllTreeNodes(root)) {
			createNodeView(node);
		}

		return root;
	}

	public void addTrace(XTrace trace, DefaultMutableTreeNode node, XEventClassifier classifier, CostModel costModel) {
		//first node is the diagnosis
		{
			String attribute = "diag_code_trunc_d";
			XAttribute valueA = trace.getAttributes().get(attribute);
			String value;
			if (valueA != null) {
				value = valueA.toString();
			} else {
				value = "[not present]";
			}
			DefaultMutableTreeNode child = findChild(node, value, svn48healthcare.DecisionTreeNode.Type.attributeValue);
			if (child == null) {
				child = new DefaultMutableTreeNode(new DecisionTreeNodeAttribute(attribute, value));
				node.add(child);
			}

			((DecisionTreeNodeAbstract) child.getUserObject()).addTrace(trace);

			node = child;
		}

		Iterator<XEvent> it = trace.iterator();
		while (it.hasNext()) {
			String activity = classifier.getClassIdentity(it.next());
			DefaultMutableTreeNode child = findChild(node, activity, svn48healthcare.DecisionTreeNode.Type.activity);

			if (child == null) {
				child = new DefaultMutableTreeNode(new DecisionTreeNodeActivity(activity));
				node.add(child);
			}

			((DecisionTreeNodeAbstract) child.getUserObject()).addTrace(trace);

			node = child;
		}

		DefaultMutableTreeNode endChild = findChild(node, null, svn48healthcare.DecisionTreeNode.Type.endTrace);
		if (endChild == null) {
			endChild = new DefaultMutableTreeNode(new DecisionTreeNodeEndTrace());
			node.add(endChild);
		}
		((DecisionTreeNodeAbstract) endChild.getUserObject()).addTrace(trace);
	}

	private static DefaultMutableTreeNode findChild(DefaultMutableTreeNode parent, String activity,
			svn48healthcare.DecisionTreeNode.Type type) {
		for (@SuppressWarnings("unchecked")
		Enumeration<TreeNode> it = parent.children(); it.hasMoreElements();) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) it.nextElement();
			DecisionTreeNode treeNodeChild = (DecisionTreeNode) child.getUserObject();
			if (type == treeNodeChild.getType() && (activity == null || treeNodeChild.getName().equals(activity))) {
				return child;
			}
		}
		return null;
	}

	private static void sortAllNodes(DefaultMutableTreeNode node) {

		//sort alphabetically
		for (int i = 0; i < node.getChildCount() - 1; i++) {
			//find maximum
			int max = Integer.MIN_VALUE;
			int maxAt = -1;
			for (int j = i; j < node.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
				int occurrences = ((DecisionTreeNodeAbstract) child.getUserObject()).getOccurrences();

				if (occurrences > max) {
					max = occurrences;
					maxAt = j;
				}
			}

			//put the maximum in position i
			if (i != maxAt) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
				DefaultMutableTreeNode childMax = (DefaultMutableTreeNode) node.getChildAt(maxAt);
				node.insert(child, maxAt);
				node.insert(childMax, i);
			}

		}

		for (int i = 0; i < node.getChildCount(); i++) {
			sortAllNodes((DefaultMutableTreeNode) node.getChildAt(i));
		}

	}
}