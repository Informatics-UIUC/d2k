package ncsa.d2k.modules.core.prediction.decisiontree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.*;

/*
	DecisionTreeVis
*/
public class DecisionTreeVis extends ncsa.d2k.infrastructure.modules.VisModule {

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "ViewableDTModel";
			default: return "No such input";
		}
	}

	public String[] getInputTypes() {
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	public String getModuleInfo() {
		return "Draws a decision tree.";
	}

	public void doit() throws Exception {
	}

	protected UserView createUserView() {
		return new DecisionTreeUserView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}
}

/*
	DecisionTreeUserView
*/
class DecisionTreeUserView extends ncsa.d2k.controller.userviews.swing.JUserPane implements ActionListener{

	BrushPanel brushpanel;
	TreeScrollPane treescrollpane;
	NavigatorPanel navigatorpanel;

	JCheckBox labelbox;

	public void initView(ViewModule module) {
	}

	public void setInput(Object object, int index) {
		ViewableDTModel model = (ViewableDTModel) object;

		brushpanel = new BrushPanel(model);
		brushpanel.setBorder(new RectangleBorder("Brushing"));

		treescrollpane = new TreeScrollPane(model, brushpanel);

		navigatorpanel = new NavigatorPanel(model, treescrollpane);
		navigatorpanel.setBorder(new RectangleBorder("Navigator"));

		labelbox = new JCheckBox("Hide labels");
		labelbox.setFont(DecisionTreeScheme.componentbuttonfont);
		labelbox.setBackground(DecisionTreeScheme.backgroundcolor);
		labelbox.setRequestFocusEnabled(false);
		labelbox.addActionListener(this);

		JPanel sidepanel = new JPanel();
		sidepanel.setBackground(DecisionTreeScheme.backgroundcolor);
		sidepanel.setLayout(new GridBagLayout());
		Constrain.setConstraints(sidepanel, navigatorpanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(10, 10, 10, 10));
		Constrain.setConstraints(sidepanel, brushpanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(0, 10, 10, 10));
		Constrain.setConstraints(sidepanel, labelbox, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST, 1, 1, new Insets(0, 10, 10, 10));

		setBackground(DecisionTreeScheme.backgroundcolor);
		setLayout(new GridBagLayout());
		Constrain.setConstraints(this, sidepanel, 0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 0, 0);
		Constrain.setConstraints(this, treescrollpane, 1, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == labelbox)
			treescrollpane.toggleLabels();
	}
}
