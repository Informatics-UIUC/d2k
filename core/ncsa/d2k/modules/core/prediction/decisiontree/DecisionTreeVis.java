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

	private static final String zoomicon = File.separator + "images" + File.separator + "zoom.gif";
	private static final String searchicon = File.separator + "images" + File.separator + "search.gif";
	private static final String printicon = File.separator + "images" + File.separator + "printit.gif";
	private static final String homeicon = File.separator + "images" + File.separator + "home.gif";

	private static final Dimension buttonsize = new Dimension(25, 25);

	private static final int MENUITEMS = 15;

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

	/*
		DecisionTreeUserView
	*/
	class DecisionTreeUserView extends ncsa.d2k.controller.userviews.swing.JUserPane implements ActionListener, Printable {

		BrushPanel brushpanel;
		TreeScrollPane treescrollpane;
		NavigatorPanel navigatorpanel;
		JD2KFrame depthframe;
		DepthPanel depthpanel;
		JD2KFrame spacingframe;
		SpacingPanel spacingpanel;
		JD2KFrame searchframe;
		SearchPanel searchpanel;

		JMenuBar menubar;
		JMenuItem printtree, printwindow;
		JMenuItem depth;
		JMenuItem spacing;
		JCheckBoxMenuItem zoom;
		JCheckBoxMenuItem showlabels;
		JMenuItem search;

		JPanel sidepanel;
		JScrollPane sidescrollpane;

		JPanel toolpanel;
		JButton resetbutton, printbutton, searchbutton;
		JToggleButton zoombutton;

		Hashtable colortable;
		Hashtable ordertable;
		ColorMenuItem[] coloritems;
		DecisionTreeScheme scheme;


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
			JMenu toolsmenu = new JMenu("Tools");

			menubar.add(optionsmenu);
			menubar.add(viewsmenu);
			menubar.add(toolsmenu);

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

			depth = new JMenuItem("Maximum Depth...");
			depth.addActionListener(this);

			spacing = new JMenuItem("Node Spacing...");
			spacing.addActionListener(this);

			zoom = new JCheckBoxMenuItem("Zoom");
			zoom.addActionListener(this);

			showlabels = new JCheckBoxMenuItem("Show Labels");
			showlabels.setState(true);
			showlabels.addActionListener(this);

			viewsmenu.add(depth);
			viewsmenu.add(spacing);
			viewsmenu.addSeparator();
			viewsmenu.add(zoom);
			viewsmenu.add(showlabels);

			search = new JMenuItem("Search...");
			search.addActionListener(this);

			toolsmenu.add(search);

			// Tool panel
			toolpanel = new JPanel();

			Image image = getImage(homeicon);
			ImageIcon icon = null;
			if (image != null)
				icon = new ImageIcon(image);
			if (icon != null) {
				resetbutton = new JButton(icon);
				resetbutton.setMaximumSize(buttonsize);
				resetbutton.setPreferredSize(buttonsize);
			}
			else
				resetbutton = new JButton("Reset");
			resetbutton.addActionListener(this);
			resetbutton.setToolTipText("Reset");

			image = getImage(printicon);
			icon = null;
			if (image != null)
				icon = new ImageIcon(image);
			if (icon != null) {
				printbutton = new JButton(icon);
				printbutton.setMaximumSize(buttonsize);
				printbutton.setPreferredSize(buttonsize);
			}
			else
				printbutton = new JButton("Print");
			printbutton.addActionListener(this);
			printbutton.setToolTipText("Print Tree");

			image = getImage(searchicon);
			icon = null;
			if (image != null)
				icon = new ImageIcon(image);
			if (icon != null) {
				searchbutton = new JButton(icon);
				searchbutton.setMaximumSize(buttonsize);
				searchbutton.setPreferredSize(buttonsize);
			}
			else
				searchbutton = new JButton("Search");
			searchbutton.addActionListener(this);
			searchbutton.setToolTipText("Search");

			image = getImage(zoomicon);
			icon = null;
			if (image != null)
				icon = new ImageIcon(image);
			if (icon != null) {
				zoombutton = new JToggleButton(icon);
				zoombutton.setMaximumSize(buttonsize);
				zoombutton.setPreferredSize(buttonsize);
			}
			else
				zoombutton = new JToggleButton("Zoom");
			zoombutton.addActionListener(this);
			zoombutton.setToolTipText("Zoom");

			toolpanel.setLayout(new GridBagLayout());
			Constrain.setConstraints(toolpanel, new JPanel(), 0, 0, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.NORTHWEST, 1, 1);
			Constrain.setConstraints(toolpanel, resetbutton, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0);
			Constrain.setConstraints(toolpanel, printbutton, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0);
			Constrain.setConstraints(toolpanel, zoombutton, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0);
			Constrain.setConstraints(toolpanel, searchbutton, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0);

			// Split pane
			brushpanel = new BrushPanel(model);
			brushpanel.setBorder(new RectangleBorder("Node Info"));

			treescrollpane = new TreeScrollPane(model, brushpanel);

			navigatorpanel = new NavigatorPanel(model, treescrollpane);
			navigatorpanel.setBorder(new RectangleBorder("Navigator"));

			depthframe = new JD2KFrame("Maximum Depth");

			spacingframe = new JD2KFrame("Node Spacing");

			searchframe = new JD2KFrame("Search");
			searchpanel = new SearchPanel(treescrollpane, searchframe);

			sidepanel = new JPanel();
			sidepanel.setMinimumSize(new Dimension(0, 0));

			sidepanel.setBackground(DecisionTreeScheme.backgroundcolor);
			sidepanel.setLayout(new GridBagLayout());
			Constrain.setConstraints(sidepanel, navigatorpanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(10, 10, 10, 10));
			Constrain.setConstraints(sidepanel, brushpanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(10, 10, 10, 10));
			Constrain.setConstraints(sidepanel, new JPanel(), 0, 2, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.NORTHWEST, 1, 1, new Insets(10, 10, 10, 10));

			sidescrollpane = new JScrollPane(sidepanel);

			JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidescrollpane, treescrollpane);
			splitpane.setOneTouchExpandable(true);
			splitpane.setDividerLocation(260);

			add(toolpanel, BorderLayout.NORTH);
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

			else if (source == showlabels)
				treescrollpane.toggleLabels();

			else if (source == zoom) {
				if (zoom.isSelected())
					zoombutton.setSelected(true);
				else
					zoombutton.setSelected(false);

				treescrollpane.toggleZoom();
			}

			else if (source == zoombutton) {
				if (zoombutton.isSelected())
					zoom.setSelected(true);
				else
					zoom.setSelected(false);

				treescrollpane.toggleZoom();
			}

			else if (source == depth) {
				depthframe.getContentPane().removeAll();
				depthframe.getContentPane().add(new DepthPanel(depthframe, treescrollpane));
				depthframe.pack();
				depthframe.setVisible(true);
			}

			else if (source == spacing) {
				spacingframe.getContentPane().removeAll();
				spacingframe.getContentPane().add(new SpacingPanel(spacingframe, treescrollpane, navigatorpanel));
				spacingframe.pack();
				spacingframe.setVisible(true);
			}

			else if (source == printtree || source == printbutton) {
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

			else if (source == resetbutton) {
				treescrollpane.reset();
			}

			else if (source == search || source == searchbutton) {
				searchframe.getContentPane().add(searchpanel);
				searchframe.pack();
				searchframe.setVisible(true);
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

	class SpacingPanel extends JPanel implements ActionListener {
		JD2KFrame spacingframe;
		TreeScrollPane treescrollpane;
		NavigatorPanel navigatorpanel;
		ViewNode viewroot;

		JLabel hlabel, vlabel;
		JTextField hfield, vfield;
		JButton apply, close, cancel;

		double xspace, yspace;

		SpacingPanel(JD2KFrame frame, TreeScrollPane scrollpane, NavigatorPanel navigator) {
			spacingframe = frame;
			treescrollpane = scrollpane;
			navigatorpanel = navigator;

			viewroot = treescrollpane.getViewRoot();
			xspace = viewroot.xspace;
			yspace = viewroot.yspace;

			hlabel = new JLabel("Horizontal Spacing:");
			vlabel = new JLabel("Vertical Spacing:");

			hfield = new JTextField(Double.toString(xspace), 5);
			vfield = new JTextField(Double.toString(yspace), 5);

			apply = new JButton("Apply");
			apply.addActionListener(this);

			close = new JButton("Close");
			close.addActionListener(this);

			cancel = new JButton("Cancel");
			cancel.addActionListener(this);

			JPanel buttonpanel = new JPanel();
			buttonpanel.add(cancel);
			buttonpanel.add(close);
			buttonpanel.add(apply);

			setLayout(new GridBagLayout());
			Constrain.setConstraints(this, hlabel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(this, hfield, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(this, vlabel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(this, vfield, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(this, buttonpanel, 0, 3, 2, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 1, 1, new Insets(5, 5, 5, 5));

		}

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();

			if (source == close)
				spacingframe.setVisible(false);

			if (source == cancel) {
				viewroot.xspace = xspace;
				viewroot.yspace = yspace;

				treescrollpane.rebuildTree();
				navigatorpanel.rebuildTree();

				spacingframe.setVisible(false);
			}

			if (source == apply) {
				String hsvalue = hfield.getText();
				String vsvalue = vfield.getText();

				try {
					double hdvalue = Double.parseDouble(hsvalue);
					double vdvalue = Double.parseDouble(vsvalue);

					viewroot.xspace = hdvalue;
					viewroot.yspace = vdvalue;

					treescrollpane.rebuildTree();
					navigatorpanel.rebuildTree();

				} catch (Exception exception) {
				}
			}
		}
	}

	class DepthPanel extends JPanel implements ActionListener {
		JD2KFrame depthframe;
		TreeScrollPane treescrollpane;

		JLabel dlabel;
		JTextField dfield;
		JButton apply, close, cancel;

		int depth;

		DepthPanel(JD2KFrame frame, TreeScrollPane scrollpane) {
			depthframe = frame;
			treescrollpane = scrollpane;

			depth = treescrollpane.getDepth();

			dlabel = new JLabel("Maximum Depth:");

			dfield = new JTextField(Integer.toString(depth), 5);

			apply = new JButton("Apply");
			apply.addActionListener(this);

			close = new JButton("Close");
			close.addActionListener(this);

			cancel = new JButton("Cancel");
			cancel.addActionListener(this);

			JPanel buttonpanel = new JPanel();
			buttonpanel.add(cancel);
			buttonpanel.add(close);
			buttonpanel.add(apply);

			setLayout(new GridBagLayout());
			Constrain.setConstraints(this, dlabel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(this, dfield, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
			Constrain.setConstraints(this, buttonpanel, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.NORTHWEST, 1, 1, new Insets(5, 5, 5, 5));

		}

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();

			if (source == close)
				depthframe.setVisible(false);

			if (source == cancel) {
				treescrollpane.setDepth(depth);

				depthframe.setVisible(false);
			}

			if (source == apply) {
				String svalue = dfield.getText();

				try {
					int ivalue = Integer.parseInt(svalue);

					treescrollpane.setDepth(ivalue);

				} catch (Exception exception) {
				}
			}
		}
	}
}
