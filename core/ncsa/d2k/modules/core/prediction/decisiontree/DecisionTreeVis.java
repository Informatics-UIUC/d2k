package ncsa.d2k.modules.core.prediction.decisiontree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.print.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.gui.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.prediction.decisiontree.widgets.*;

/*
	DecisionTreeVis
*/
public final class DecisionTreeVis extends VisModule implements HasNames {

	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "A ViewableDTModel.";
			default: return "No such input";
		}
	}

    public String getInputName(int index) {
        return "DTModel";
    }

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel"};
		return types;
	}

	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

    public String getOutputName(int index) {
        return "";
    }

	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	public String getModuleInfo() {
		return "Visualizes a decision tree.";
	}

    public String getModuleName() {
        return "DTVis";
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
class DecisionTreeUserView extends ncsa.d2k.controller.userviews.swing.JUserPane
	implements ActionListener, Printable {

	BrushPanel brushpanel;
	TreeScrollPane treescrollpane;
	NavigatorPanel navigatorpanel;

	JCheckBox labelbox;
	JMenuBar menuBar;
	JMenuItem miPrintWindow;
	JMenuItem miPrintCanvas;
	JMenuItem miHelp;
	JFrame helpWindow;

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	public void initView(ViewModule module) {
		menuBar = new JMenuBar();
		JMenu m1 = new JMenu("File");
		miPrintWindow = new JMenuItem("Print Window...");
		miPrintWindow.addActionListener(this);
		miPrintCanvas = new JMenuItem("Print Canvas...");
		miPrintCanvas.addActionListener(this);
		m1.add(miPrintWindow);
		m1.add(miPrintCanvas);
		JMenu m2 = new JMenu("Help");
		miHelp = new JMenuItem("About DecisionTreeVis...");
		miHelp.addActionListener(this);
		m2.add(miHelp);
		menuBar.add(m1);
		menuBar.add(m2);
		helpWindow = new HelpWindow();
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

	/**
	 * Print this component.
	 */
	public int print(Graphics g, PageFormat pf, int pi)
		throws PrinterException {

		double pageHeight = pf.getImageableHeight();
		double pageWidth = pf.getImageableWidth();

		double cWidth = getWidth();
		double cHeight = getHeight();

		double scale = 1;
		if(cWidth >= pageWidth)
			scale = pageWidth/cWidth;
		if(cHeight >= pageHeight)
			scale = Math.min(scale, pageHeight/cHeight);

		double cWidthOnPage = cWidth*scale;
		double cHeightOnPage = cHeight*scale;

		if(pi >= 1)
			return Printable.NO_SUCH_PAGE;

		Graphics2D g2 = (Graphics2D)g;
		g2.translate(pf.getImageableX(), pf.getImageableY());
		g2.scale(scale, scale);
		print(g2);
		return Printable.PAGE_EXISTS;
	}

	public Object getMenu() {
		return menuBar;
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == labelbox)
			treescrollpane.toggleLabels();
		else if(event.getSource() == miPrintWindow) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable(this);
			if(pj.printDialog()) {
				try {
					pj.print();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		else if(event.getSource() == miPrintCanvas) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable(treescrollpane.getPrintable());
			if(pj.printDialog()) {
				try {
					pj.print();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		else if(event.getSource() == miHelp)
			helpWindow.setVisible(true);
	}

	private final class HelpWindow extends JD2KFrame {
		HelpWindow() {
			super("About DecisionTreeVis");
			JEditorPane jep = new JEditorPane("text/html", getHelpString());
			getContentPane().add(new JScrollPane(jep));
			setSize(400, 400);
		}
	}

	private static final String getHelpString() {
		StringBuffer s = new StringBuffer("<html>");
		s.append("<h2>DecisionTreeVis</h2>");
		s.append("The DecisionTreeVis is used to display the results of decision ");
		s.append("tree modeling.  The window has two panes.  The Navigator pane in ");
		s.append("the left pane illustrates the full decision tree, the viewable window ");
		s.append("is shown with a black box outlin.  This box can be dragged around the ");
		s.append("tree to display different parts of the decision tree.  The viewable ");
		s.append("tree is shown in the right pane.  The Brushing pane shows the percentage ");
		s.append("of the examples in each class when the mouse brushes a chart in the ");
		s.append("decision tree.  The Hide Labels checkbox turns the labels for each ");
		s.append("split point on and off.  Clicking on a chart in the right pane ");
		s.append("brings up a bar chart that shows visaully, the percentage of the ");
		s.append("examples in each class at this split point.  The label of this window ");
		s.append("shows the most recent split attribute and value.");
		s.append("</body></html>");
		return s.toString();
	}
}
