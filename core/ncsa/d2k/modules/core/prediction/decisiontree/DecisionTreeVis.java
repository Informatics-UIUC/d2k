package ncsa.d2k.modules.core.prediction.decisiontree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
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
			case 0: return "ViewableDTModel.";
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
class DecisionTreeUserView extends ncsa.d2k.controller.userviews.swing.JUserPane implements ActionListener, Printable {

	static final int MENUITEMS = 15;

	BrushPanel brushpanel;
	TreeScrollPane treescrollpane;
	NavigatorPanel navigatorpanel;

	JMenuBar menubar;
	JMenuItem printtree, printwindow;
	JCheckBoxMenuItem hidelabels;

	Hashtable colortable;
	Hashtable ordertable;
	ColorMenuItem[] coloritems;
	DecisionTreeScheme scheme;

	JCheckBox zoominbox, zoomoutbox;

	public void initView(ViewModule module) {
		menubar = new JMenuBar();
	}

	public Object getMenu() {
		return menubar;
	}

	public void setInput(Object object, int index) {
		ViewableDTModel model = (ViewableDTModel) object;

		String[] outputs = model.getUniqueOutputValues();
		scheme = new DecisionTreeScheme(outputs.length);

		colortable = new Hashtable(outputs.length);
		ordertable = new Hashtable(outputs.length);

		for (int outindex = 0; outindex < outputs.length; outindex++) {
			colortable.put(outputs[outindex], scheme.getNextColor());
			ordertable.put(outputs[outindex], new Integer(outindex));
		}

		// Menu
		JMenu optionsmenu = new JMenu("Options");
		JMenu viewsmenu = new JMenu("Views");

		menubar.add(optionsmenu);
		menubar.add(viewsmenu);

		JMenu colorsmenu = new JMenu("Set Colors");
		coloritems = new ColorMenuItem[outputs.length];

		JMenu currentmenu = colorsmenu;
		int items = 0;

		for (int outindex = 0; outindex < coloritems.length; outindex++) {
			coloritems[outindex] = new ColorMenuItem(outputs[outindex]);
			coloritems[outindex].addActionListener(this);

			if (items == MENUITEMS) {
				JMenu nextmenu = new JMenu("More...");
				currentmenu.insert(nextmenu, 0);
				nextmenu.add(coloritems[outindex]);
				currentmenu = nextmenu;
				items = 1;
			}
			else {
				currentmenu.add(coloritems[outindex]);
				items++;
			}
		}

		printtree = new JMenuItem("Print Tree...");
		printtree.addActionListener(this);

		printwindow = new JMenuItem("Print Window...");
		printwindow.addActionListener(this);

		optionsmenu.add(colorsmenu);
		optionsmenu.addSeparator();
		optionsmenu.add(printtree);
		optionsmenu.add(printwindow);

		hidelabels = new JCheckBoxMenuItem("Hide Labels");
		hidelabels.addActionListener(this);

		viewsmenu.add(hidelabels);

		// Split pane
		brushpanel = new BrushPanel(model);
		brushpanel.setBorder(new RectangleBorder("Node Info"));

		treescrollpane = new TreeScrollPane(model, brushpanel);

		navigatorpanel = new NavigatorPanel(model, treescrollpane);
		navigatorpanel.setBorder(new RectangleBorder("Navigator"));

		zoominbox = new JCheckBox("Zoom in");
		zoominbox.setFont(DecisionTreeScheme.componentbuttonfont);
		zoominbox.setBackground(DecisionTreeScheme.backgroundcolor);
		zoominbox.setRequestFocusEnabled(false);
		zoominbox.addActionListener(this);

		zoomoutbox = new JCheckBox("Zoom out");
		zoomoutbox.setFont(DecisionTreeScheme.componentbuttonfont);
		zoomoutbox.setBackground(DecisionTreeScheme.backgroundcolor);
		zoomoutbox.setRequestFocusEnabled(false);
		zoomoutbox.addActionListener(this);

		JPanel sidepanel = new JPanel();
		sidepanel.setMinimumSize(new Dimension(0, 0));

		sidepanel.setBackground(DecisionTreeScheme.backgroundcolor);
		sidepanel.setLayout(new GridBagLayout());
		Constrain.setConstraints(sidepanel, navigatorpanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(10, 10, 10, 10));
		Constrain.setConstraints(sidepanel, brushpanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(0, 10, 10, 10));
		Constrain.setConstraints(sidepanel, zoominbox, 0, 2, 1, 1, GridBagConstraints.NONE,
			GridBagConstraints.NORTHWEST, 0, 0, new Insets(0, 10, 10, 10));
		Constrain.setConstraints(sidepanel, zoomoutbox, 0, 3, 1, 1, GridBagConstraints.NONE,
			GridBagConstraints.NORTHWEST, 1, 1, new Insets(0, 10, 10, 10));

		JScrollPane sidescrollpane = new JScrollPane(sidepanel);

		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidescrollpane, treescrollpane);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerLocation(260);

		add(splitpane, BorderLayout.CENTER);
		setBackground(DecisionTreeScheme.backgroundcolor);
	}

	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
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

		if (pi >= 1)
			return Printable.NO_SUCH_PAGE;

		Graphics2D g2 = (Graphics2D) g;
		g2.translate(pf.getImageableX(), pf.getImageableY());
		g2.scale(scale, scale);
		print(g2);
		return Printable.PAGE_EXISTS;
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source instanceof ColorMenuItem) {
			ColorMenuItem coloritem = (ColorMenuItem) source;
			Color oldcolor = getColor(coloritem.getText());
			Color newcolor = JColorChooser.showDialog(this, "Choose Color", oldcolor);

			if (newcolor != null) {
				colortable.put(coloritem.getText(), newcolor);

				Enumeration keys = colortable.keys();
				Color[] colors = new Color[colortable.size()];
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					Integer index = (Integer) ordertable.get(key);
					colors[index.intValue()] = (Color) colortable.get(key);
				}

				scheme.setColors(colors);
				brushpanel.repaint();
				treescrollpane.repaint();
			}
		}

		else if (source == hidelabels)
			treescrollpane.toggleLabels();

		else if (source == zoominbox)
			treescrollpane.toggleZoomin();

		else if (source == zoomoutbox)
			treescrollpane.toggleZoomout();

		else if (source == printtree) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable(treescrollpane.getPrintable());
			if (pj.printDialog()) {
				try {
					pj.print();
				}
				catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		else if (source == printwindow) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable(this);
			if (pj.printDialog()) {
				try {
					pj.print();
				}
				catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	private class ColorMenuItem extends JMenuItem {
		ColorMenuItem(String s) {
			super(s);
		}
	}

	public Color getColor(String string) {
		return (Color) colortable.get(string);
	}

}
