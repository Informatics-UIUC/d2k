package ncsa.d2k.modules.core.vis;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.d2k.modules.core.vis.widgets.*;

public class SimpleDTVis extends VisModule {

	public String getModuleInfo() {
		return "";
	}

	public String getInputInfo(int i) {
		return "";
	}

	public String getOutputInfo(int i) {
		return "";
	}

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return in;
	}

	public String[] getOutputTypes() {
		return null;
	}

	public UserView createUserView() {
		return new DTView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	class DTView extends JUserPane implements TreeSelectionListener {
		DecisionTreeModel model;
		SimpleTreeScrollPane treepane;

		public void initView(ViewModule m) {}

		public void setInput(Object o, int i) {
			model = (DecisionTreeModel)o;
			DefaultMutableTreeNode n = makeTree(model.getRoot());
			DTTree dt = new DTTree(n);
			dt.addTreeSelectionListener(this);
			JScrollPane jsp = new JScrollPane(dt);
			treepane = new SimpleTreeScrollPane(model, model.getRoot());
			setLayout(new BorderLayout());
			add(jsp, BorderLayout.WEST);
			add(treepane, BorderLayout.CENTER);
		}

		DefaultMutableTreeNode makeTree(DecisionTreeNode root) {
			DefaultMutableTreeNode node =
				new DefaultMutableTreeNode(root);
			for(int i = 0; i < root.getNumChildren(); i++) {
				node.add(makeTree(root.getChild(i)));
			}
			return node;
		}

		public void valueChanged(TreeSelectionEvent e) {
			TreePath []paths = e.getPaths();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				paths[0].getLastPathComponent();
			DecisionTreeNode dtn = (DecisionTreeNode)node.getUserObject();
			treepane.setTree(dtn);
		}
	}

	class DTTree extends JTree {
		public DTTree(TreeNode node) {
			super(node);
		}

		public String convertValueToText(Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {

			if(value != null) {
				Object obj = ((DefaultMutableTreeNode)value).getUserObject();
				if(obj != null) {
					if(obj instanceof DecisionTreeNode) {
						return ((DecisionTreeNode)obj).getLabel();
					}
				}
			}
			return "";
		}
	}
}
