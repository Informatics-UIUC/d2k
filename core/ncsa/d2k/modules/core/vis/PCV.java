package ncsa.d2k.modules.core.vis;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.text.NumberFormat;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.util.datatype.*;

import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;
import ncsa.gui.DisposeOnCloseListener;

import ncsa.d2k.gui.JD2KFrame;

import ncsa.d2k.modules.core.vis.widgets.*;

/**
 *
 */
public class PCV extends VisModule {

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
		String[] i = {"ncsa.d2k.util.datatype.VerticalTable"};
		return i;
	}

	public String[] getOutputTypes() {
		return null;
	}

	public UserView createUserView() {
		return new PCView();
	}

	public String[] getFieldNameMapping() {
		return null;
	}

	/** The length of the line used for the gradient paint */
	private static final int LINE_LENGTH = 250;

	/** color wheel */
	private static final Color[] colors = {new Color(71, 74, 98),
		new Color(191, 191, 115), new Color(111, 142, 116),
		new Color(178, 198, 181), new Color(153, 185, 216),
		new Color(96, 93, 71), new Color(146, 205, 163),
		new Color(203, 84, 84), new Color(217, 183, 170),
		new Color(140, 54, 57), new Color(203, 136, 76)};

	private static final String GREATER_THAN = ">";
	private static final String LESS_THAN = "<";
	private static final String GREATER_THAN_EQUAL_TO = ">=";
	private static final String LESS_THAN_EQUAL_TO = "<=";
	private static final String NOT_EQUAL_TO = "!=";
	private static final String EQUAL_TO = "==";

	private static final int MAX_MENU_ITEMS = 15;
	private static final String MORE = "More..";
	private static final int BAR_WIDTH = 20;
	private static final int HALF_WIDTH = 10;
	private static final String HIGH = "High";
	private static final String LOW = "Low";

	private static final Color defaultHighColor = Color.red;//colors[0];
	private static final Color defaultLowColor = Color.yellow;//colors[colors.length-1];
	private static final Color yellowish = new Color(255, 255, 240);

	private static final float TOLERANCE = 2.005f;

	private static final HashMap uniqueValues(SimpleColumn sc) {
		HashMap hm = new HashMap();
		for(int k = 0; k < sc.getNumRows(); k++) {
			String s = sc.getString(k);
			if(!hm.containsKey(s)) {
				int sz = hm.size();
				sz++;
				hm.put(s, new Integer(sz));
			}
		}
		return hm;
	}

	/**
	 *
	 */
	class PCView extends JUserPane implements ActionListener, Printable  {
		/** the indices of the columns to show */
		volatile int[] columnorder;
		/** the locations of the columns */
		volatile float[] columnlocations;

		int imagewidth;

		boolean zoomin;

		/** the table with the data */
		VerticalTable table;
		JMenuBar menuBar;

		/** the choose colors menu */
		JMenu chooseColors;
		/** the area that shows the lines */
		MainArea ma;

		JToggleButton zoom;
		JButton showTable;
		JButton printButton;
		JCheckBoxMenuItem useAntialias;
		JMenuItem miClearSelected;
		ColorMenuItem miSelectedColor;
		Color selectedColor = Color.red;

		int currentKeyColumn;

		Filter filter;
		JMenuItem miFilter;
		JMenuItem miShowTable;
		JMenuItem miPrint;

		Color highColor;
		Color lowColor;
		HashMap [] colorsLookup;
		/** true if the row should be drawn */
		boolean [] linemap;
		boolean [] selectedlines;
		InfoTableModel infoModel;
		JTable infoTable;
		Legend legend;

		double[] mins;
		double[] maxes;

  		public void initView(ViewModule m) {}
		NumberFormat nf;

		/**
		 * Input arrived
		 */
		public void setInput(Object o, int i) {
			table = (VerticalTable)o;
			selectedlines = new boolean[table.getNumRows()];
			legend = new Legend();
			nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);

			// create the menus
			menuBar = new JMenuBar();
			JMenu opt = new JMenu("Options");
			menuBar.add(opt);
			JMenu displaycols = new JMenu("Display Columns");

			int numItems = 0;
			JMenu curMenu = displaycols;
			for(int j = 0; j < table.getNumColumns(); j++) {
				DisplayColumnMenuItem dcmi = new
					DisplayColumnMenuItem(table.getColumnLabel(j),
					j);
				dcmi.addActionListener(this);
				dcmi.setSelected(true);
				if(numItems == MAX_MENU_ITEMS) {
					JMenu nextMenu = new JMenu(MORE);
					curMenu.insert(nextMenu, 0);
					nextMenu.add(dcmi);
					curMenu = nextMenu;
					numItems = 1;
				}
				else {
					curMenu.add(dcmi);
					numItems++;
				}
			}
			opt.add(displaycols);

			int keycol = 4;
			JMenu keyMenu = new JMenu("Key Column");
			curMenu = keyMenu;
			numItems = 0;
			ButtonGroup bg = new ButtonGroup();
			for(int j = 0; j < table.getNumColumns(); j++) {
				KeyColumnMenuItem dcmi = new
					KeyColumnMenuItem(table.getColumnLabel(j),
					j);
				if(j == keycol)
					dcmi.setSelected(true);
				bg.add(dcmi);
				dcmi.addActionListener(this);
				if(numItems == MAX_MENU_ITEMS) {
					JMenu nextMenu = new JMenu(MORE);
					curMenu.insert(nextMenu, 0);
					nextMenu.add(dcmi);
					curMenu = nextMenu;
					numItems = 1;
				}
				else {
					curMenu.add(dcmi);
					numItems++;
				}
			}
			opt.add(keyMenu);

			chooseColors = new JMenu("Choose Colors");
			opt.add(chooseColors);
			opt.addSeparator();
			useAntialias = new JCheckBoxMenuItem("Use Antialiasing", false);
			useAntialias.addActionListener(this);
			opt.add(useAntialias);
			miFilter = new JMenuItem("Filters..");
			miFilter.addActionListener(this);
			opt.add(miFilter);
			miClearSelected = new JMenuItem("Clear Selected Lines");
			miClearSelected.addActionListener(this);
			opt.add(miClearSelected);
			ImageIcon hi = new ImageIcon(new ColorComponent(selectedColor).getImage());
			miSelectedColor = new ColorMenuItem("Selected Line Color", hi, selectedColor);
			miSelectedColor.addActionListener(this);
			opt.add(miSelectedColor);
			miShowTable = new JMenuItem("Show Table");
			miShowTable.addActionListener(this);
			opt.add(miShowTable);
			miPrint = new JMenuItem("Print..");
			miPrint.addActionListener(this);
			opt.add(miPrint);

			ma = new MainArea(this);
			ma.setKeyColumn(keycol);
			ma.setPreferredSize(new Dimension(500, 400));
			JScrollPane jsp = new JScrollPane(ma);
			//jsp.setPreferredSize(new Dimension(500, 400));

			zoom = new JToggleButton("Z");
			zoom.addActionListener(this);
			showTable = new JButton("T");
			showTable.addActionListener(this);
			printButton = new JButton("P");
			printButton.addActionListener(this);

			JPanel bp = new JPanel();
			bp.add(printButton);
			bp.add(zoom);
			bp.add(showTable);

			JPanel bq = new JPanel();
			bq.setLayout(new BoxLayout(bq, BoxLayout.Y_AXIS));
			bq.add(Box.createGlue());
			bq.add(bp);
			bq.add(Box.createGlue());
			JPanel header = new JPanel();
			header.setLayout(new BorderLayout());
			header.add(new JPanel(), BorderLayout.CENTER);
			header.add(bq, BorderLayout.EAST);

			setLayout(new BorderLayout());
			//add(jsp, BorderLayout.CENTER);
			//add(bp, BorderLayout.NORTH);
			infoModel = new InfoTableModel();
			infoTable = new JTable(infoModel);
			JScrollPane tableScroll = new JScrollPane(infoTable);
			tableScroll.setPreferredSize(new Dimension(350, 75));
			//legend.setMinimumSize(new Dimension(100, 75));
			JScrollPane legendScroll = new JScrollPane(legend);
			JLabel llabel = new JLabel("Legend");
			llabel.setFont(infoTable.getFont());
			JViewport jv = new JViewport();
			jv.setView(llabel);
			legendScroll.setColumnHeader(jv);
			//legendScroll.setPreferredSize(new Dimension(100, 75));

			/*JPanel bk = new JPanel();
			bk.setLayout(new BorderLayout());
			bk.add(tableScroll, BorderLayout.CENTER);
			bk.add(legendScroll, BorderLayout.EAST);
			*/
			JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				tableScroll, legendScroll);
			split2.setDividerSize(4);
			//split2.setDividerLocation(.6d);

			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				jsp, split2);
			split.setOneTouchExpandable(true);
			split.setDividerSize(8);
			split.setResizeWeight(1);
			//split.setDividerLocation(.9d);
			//add(ma, BorderLayout.CENTER);
			add(header, BorderLayout.NORTH);
			add(split, BorderLayout.CENTER);
			filter = new Filter(ma);
		}

		public Object getMenu() {
			return menuBar;
		}

		/**
			Add a line to the selected set.
		*/
		private void addSelection(int row) {
			infoModel.addInfoRow(row);
		}

		/**
			Remove a line from the selected set.
		*/
		private void removeSelection(int row) {
			infoModel.removeInfoRow(row);
			infoTable.validate();
			//scrollPane.validate();
		}

		/**
		 * Identifies which columns to display
		 */
		class DisplayColumnMenuItem extends JCheckBoxMenuItem {
			int id;
			DisplayColumnMenuItem(String s, int i) {
				super(s);
				id = i;
			}
		}

		/**
		 * Identifies which is the key column
		 */
		class KeyColumnMenuItem extends JCheckBoxMenuItem {
			int id;
			KeyColumnMenuItem(String s, int i) {
				super(s);
				id = i;
			}
		}

		/**
		 * Identifies which colors to use
		 */
		class ColorMenuItem extends JMenuItem {
			Color c;
			ColorMenuItem(String s, Icon i, Color c) {
				super(s, i);
			}
		}

		/**
		 * Action listener for menu items
		 */
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			// update the key column
			if(src instanceof KeyColumnMenuItem) {
				int id = ((KeyColumnMenuItem)src).id;
				ma.setKeyColumn(id);
				ma.updateImage();
			}
			// update the columns to display
			else if(src instanceof DisplayColumnMenuItem) {
				DisplayColumnMenuItem dcmi = (DisplayColumnMenuItem)src;
				int id = dcmi.id;
				// if selected, add it to the list of columns
				if(dcmi.getState()) {
					int[] newcolorder = new int[columnorder.length+1];
					float[] newcolloc = new float[columnlocations.length+1];
					for(int i = 0; i < columnorder.length; i++) {
						newcolorder[i] = columnorder[i];
						newcolloc[i] = columnlocations[i];
					}
					newcolorder[newcolorder.length-1] = id;
					newcolloc[newcolorder.length-1] = imagewidth;

					// find a better way to set column locations
					for(int i = 0; i < newcolloc.length-1; i++) {
						newcolloc[i] *= ((float)columnlocations.length)/((float)newcolloc.length);
					}
					columnorder = newcolorder;
					columnlocations = newcolloc;
				}

				// if not selected, remove it from the list
				else {
					int[] newcolorder = new int[columnorder.length-1];
					float[] newcolloc = new float[columnlocations.length-1];
					int idx = 0;
					for(int i = 0; i < columnorder.length; i++) {
						if(columnorder[i] != id) {
							newcolorder[idx] = columnorder[i];
							newcolloc[idx] = columnlocations[i];
							idx++;
						}
					}
					columnorder = newcolorder;
					columnlocations = newcolloc;
				}
				ma.updateImage();
			}
			// zooming
			else if(src == zoom) {
				if(zoomin)
					zoomin = false;
				else
					zoomin = true;
			}
			// change the colors
			else if(src instanceof ColorMenuItem) {
				ColorMenuItem cmi = (ColorMenuItem)src;
				String text = cmi.getText();
				Color oldColor = cmi.c;
				Color newColor = JColorChooser.showDialog(this,
					"Choose", oldColor);

				if(cmi == miSelectedColor) {
					ImageIcon hi = new ImageIcon(new ColorComponent(newColor).getImage());
					cmi.setIcon(hi);
					selectedColor = newColor;
					ma.updateImage();
					return;
				}
				if(newColor != null) {
					colorsLookup[currentKeyColumn].put(text, newColor);
					ma.setKeyColumn(currentKeyColumn);
					ma.updateImage();
				}
			}
			// antialias the lines
			else if(src == useAntialias) {
				ma.image = null;
				ma.repaint();
				ma.updateImage();
			}
			// show the filter
			else if(src == miFilter) {
				filter.setState(Frame.NORMAL);
				filter.setVisible(true);
				//ma.updateImage();
			}
			else if(src == miClearSelected) {
				boolean found = false;
				for(int i = 0; i < selectedlines.length; i++) {
					if(selectedlines[i]) {
						found = true;
						removeSelection(i);
					}
					selectedlines[i] = false;
				}
				// only redraw buffer if we actually cleared any lines
				if(found)
					ma.updateImage();
			}
			else if(src == miShowTable || src == showTable) {
				JD2KFrame frame = new JD2KFrame("Table");
				VerticalTableMatrix vtm = new VerticalTableMatrix(table);
				frame.getContentPane().add(vtm);
				frame.addWindowListener(new DisposeOnCloseListener(frame));
				frame.pack();
				frame.show();
			}
			// print
			else if(src == miPrint || src == printButton) {
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

		/**
		 * Draw the lines as a buffered image.  Everything else is painted
		 * directly on this.
		 */
		class MainArea extends JPanel implements MouseListener, MouseMotionListener {
			PCView app;
			int selectedcol;
			boolean mousedrag;
			int mousex;
			int mousey;
			boolean firsttime;

			// the weights of the lines, scaled to between the
			// max and min for a column
			float [][] weights;
			// the actual heights of the lines
			float [][] heights;

			// the color of a row
			Color [] linecolor;

			int leftoffset;
			int topoffset;
			int imageheight;

			int imagebottom;
			int barheight;
			int imagetop;

			BufferedImage image;

			MainArea(PCView a) {
				app = a;
				firsttime = true;
				mousedrag = false;
				columnorder = new int[table.getNumColumns()];
				columnlocations = new float[table.getNumColumns()];
				weights = new float[table.getNumColumns()][table.getNumRows()];
				heights = new float[table.getNumColumns()][table.getNumRows()];
				mins = new double[table.getNumColumns()];
				maxes = new double[table.getNumColumns()];
				for(int j = 0; j < columnorder.length; j++) {
					columnorder[j] = j;
					Column c = table.getColumn(j);
					if(c instanceof NumericColumn) {
						NumericColumn nc = (NumericColumn)c;
						float max = (float)nc.getMax();
						float min = (float)nc.getMin();
						mins[j] = min;
						maxes[j] = max;
						for(int k = 0; k < nc.getNumRows(); k++)
							weights[j][k] = (max-nc.getFloat(k))/(max-min);
					}
					else {
						SimpleColumn sc = (SimpleColumn)c;
						HashMap hm = uniqueValues(sc);
						for(int k = 0; k < sc.getNumRows(); k++) {
							Integer ii = (Integer)hm.get(sc.getString(k));
							weights[j][k] = ii.floatValue()/(hm.size()+1);
						}
					}
				}
				linemap = new boolean[table.getNumRows()];
				for(int j = 0; j < linemap.length; j++)
					linemap[j] = true;
				linecolor = new Color[table.getNumRows()];
				colorsLookup = new HashMap[table.getNumColumns()];
				addMouseListener(this);
				addMouseMotionListener(this);
			}

			int oldwidth;
			int oldheight;
			int sidebuffer;

			/**
			 * Set the size of this.
			 */
			public void setBounds(int x, int y, int w, int h) {
				int oldimagewidth = imagewidth;

				if(w != oldwidth || h != oldheight) {
					oldwidth = w;
					oldheight = h;
					sidebuffer = (int)(.025*w);
					leftoffset = (int)(.075*w);
					topoffset = (int)(.05*h);
					imagetop = (int)(.1*h);
					imageheight = (int)(.55*h);
					imagewidth = (int)(.85*w);
					imagebottom = imagetop+imageheight+topoffset;
					barheight = (int)(.25*h);

					float newratio = (float)imagewidth/(float)oldimagewidth;

					if(firsttime) {
						int wid = (int)(imagewidth/(columnlocations.length-1));
						for(int i = 0; i < columnlocations.length; i++) {
							columnlocations[i] = (float)i*wid;
						}
						firsttime = false;
					}
					// scale the current columnlocations
					else {
						for(int i = 0; i < columnlocations.length; i++)
							columnlocations[i] *= newratio;
					}

					for(int j = 0; j < columnorder.length; j++) {
						for(int k = 0; k < table.getNumRows(); k++) {
							heights[j][k] = weights[j][k]*imageheight;
						}
					}
					updateImage();
				}

				super.setBounds(x, y, w, h);
			}

			/**
			 * Create a buffered image used for the gradient paint.
			 */
			BufferedImage getGradientImage(Color low, Color high) {
				BufferedImage bi = new BufferedImage(2, 101,
					BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = (Graphics2D)bi.getGraphics();
				g2.setPaint(new GradientPaint(0, 0, low,
					2, 101, high));
				g2.fill(new Rectangle(0, 0, 2, 101));
				return bi;
			}

			/**
			 * Set the column that determines the line colors.
			 */
			void setKeyColumn(int i) {
				currentKeyColumn = i;
				// update the linecolors
				Column c = table.getColumn(i);

				HashMap lookup = colorsLookup[i];
				if(lookup == null)
					lookup = new HashMap();

				if(c instanceof NumericColumn) {
					NumericColumn nc = (NumericColumn)c;
					double max = nc.getMax();
					double min = nc.getMin();

					Color h = (Color)lookup.get(HIGH);
					if(h == null)
						h = defaultHighColor;
					Color l = (Color)lookup.get(LOW);
					if(l == null)
						l = defaultLowColor;

					// get the high and low color here
					BufferedImage gradient = getGradientImage(l, h);
					for(int j = 0; j < table.getNumRows(); j++) {
						// now create a new GradientPaint
						// based on the shadecolumn get the shader value
						double shaderVal = nc.getDouble(j);
						// find where the shader value lies between min and max
						double percent = (max-shaderVal)/(max-min);
						Color col = new Color(gradient.getRGB(1,
							100-(int)Math.abs(percent*100)));
						linecolor[j] = col;
					}
					chooseColors.removeAll();
					lookup.put(HIGH, h);
					ImageIcon hi = new ImageIcon(new ColorComponent(h).getImage());
					ColorMenuItem hm = new ColorMenuItem(HIGH, hi, h);
					hm.addActionListener(app);
					lookup.put(LOW, l);
					ImageIcon li = new ImageIcon(new ColorComponent(l).getImage());
					ColorMenuItem lm = new ColorMenuItem(LOW, li, l);
					lm.addActionListener(app);
					chooseColors.add(hm);
					chooseColors.add(lm);
				}
				else {
					SimpleColumn sc = (SimpleColumn)c;

					int idx = 0;
					//linecolor = new Color[table.getNumRows()];
					for(int j = 0; j < sc.getNumRows(); j++) {
						String s = sc.getString(j);
						if(lookup.containsKey(s)) {
							Color col = (Color)lookup.get(s);
							linecolor[j] = col;
						}
						else {
							lookup.put(s, colors[idx%colors.length]);
							linecolor[j] = colors[idx%colors.length];
							idx++;
						}
					}
					chooseColors.removeAll();
					int numItems = 0;
					JMenu curMenu = chooseColors;
					Iterator iter = lookup.keySet().iterator();
					while(iter.hasNext()) {
						String text = (String)iter.next();
						Color col = (Color)lookup.get(text);
						ImageIcon li = new ImageIcon(new ColorComponent(col).getImage());
						ColorMenuItem cmi = new ColorMenuItem(text, li, col);
						cmi.addActionListener(app);
						if(numItems == MAX_MENU_ITEMS) {
							JMenu nextMenu = new JMenu(MORE);
							curMenu.insert(nextMenu, 0);
							nextMenu.add(cmi);
							curMenu = nextMenu;
							numItems = 1;
						}
						else {
							curMenu.add(cmi);
							numItems++;
						}
					}
				}
				legend.updateLegend(lookup);
				colorsLookup[i] = lookup;
			}

			/**
			 * Redraw the lines.
			 */
			void updateImage() {
				image = null;
				new CreateImageThread(this).start();
			}

			/**
			 * Draw the lines on a buffered image in a separate thread
			 * and then paint the image on the screen.
			 */
			class CreateImageThread extends Thread {
				MainArea app;

				CreateImageThread(MainArea a) {
					app = a;
				}

				public void run() {
					BufferedImage img = new BufferedImage(imagewidth,
						imageheight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = (Graphics2D)img.getGraphics();
					if(useAntialias.getState())
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setPaint(yellowish);
					g2.fill(new Rectangle(0, 0, imagewidth, imageheight));

					// draw the lines
					for(int i = 0; i < columnorder.length-1; i++) {
						for(int j = 0; j < table.getNumRows(); j++) {
							if(linemap[j] && !selectedlines[j]) {
								g2.setPaint(linecolor[j]);
								g2.draw(new Line2D.Float(
									columnlocations[i],
									heights[columnorder[i]][j],
									columnlocations[i+1],
									heights[columnorder[i+1]][j]));
							}
							g2.setPaint(Color.black);
							g2.draw(new Line2D.Float(
								columnlocations[i],
								0,
								columnlocations[i],
								imageheight));
						}
					}

					// loop through again to draw the selected lines
					// this is done so that the selected lines will be
					// drawn on top of the other lines
					g2.setPaint(selectedColor);
					for(int i = 0; i < columnorder.length-1; i++) {
						for(int j = 0; j < table.getNumRows(); j++) {
							if(linemap[j] && selectedlines[j]) {
								g2.draw(new Line2D.Float(
									columnlocations[i],
									heights[columnorder[i]][j],
									columnlocations[i+1],
									heights[columnorder[i+1]][j]));
							}
						}
					}

					g2.setPaint(Color.black);
					// get the last one
					g2.draw(new Line2D.Float(
						columnlocations[columnlocations.length-1],
						0,
						columnlocations[columnlocations.length-1],
						imageheight));

					image = img;
					app.repaint();
				}
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setPaint(yellowish);
				g2.fill(new Rectangle(leftoffset-sidebuffer, topoffset,
					imagewidth+2*sidebuffer, imageheight+2*topoffset));

				g2.setPaint(Color.black);
				if(image != null)
					g2.drawImage(image, leftoffset, imagetop, this);
				// draw something else while the image is being drawn
				else {
					g2.setPaint(yellowish);
					g2.fill(new Rectangle(leftoffset, imagetop,
						imagewidth, imageheight));
				}

				g2.setPaint(Color.black);
				AffineTransform fontRotate = new AffineTransform();
				fontRotate.setToRotation(300);
				Font f = g2.getFont();
				Font rotatedFont = f.deriveFont(fontRotate);
				g2.setFont(rotatedFont);
				for(int i = 0; i < columnlocations.length; i++) {
					drawBar(g2, columnlocations[i], table.getColumnLabel(columnorder[i]));
				}
				g2.setFont(f);

				if(mousedrag) {
					g2.setPaint(Color.green);
					g2.draw(new Line2D.Float(
						columnlocations[selectedcol]+leftoffset,
						topoffset, columnlocations[selectedcol]+leftoffset,
						imagebottom));
				}

				g2.setPaint(Color.black);
				FontMetrics fm = g2.getFontMetrics();
				int ascent = fm.getAscent();
				for(int i = 0; i < columnorder.length; i++) {
					if(table.getColumn(columnorder[i]) instanceof NumericColumn) {
						String mx = nf.format(maxes[columnorder[i]]);
						g2.drawString(mx,
							columnlocations[i]+leftoffset-fm.stringWidth(mx)/2,
							2*topoffset-(topoffset-ascent)/2);
					}
				}
				for(int i = 0; i < columnorder.length; i++) {
					if(table.getColumn(columnorder[i]) instanceof NumericColumn) {
						String mx = nf.format(mins[columnorder[i]]);
						g2.drawString(mx,
							columnlocations[i]+leftoffset-fm.stringWidth(mx)/2,
							imagebottom-(topoffset-ascent)/2);
					}
				}
				g2.draw(new Rectangle(leftoffset-sidebuffer, topoffset,
					imagewidth+2*sidebuffer, imageheight+2*topoffset));
				g2.draw(new Line2D.Double(leftoffset-sidebuffer, 2*topoffset,
					leftoffset+imagewidth+sidebuffer, 2*topoffset));
				g2.draw(new Line2D.Double(leftoffset-sidebuffer,
					imagebottom-topoffset, leftoffset+imagewidth+sidebuffer,
					imagebottom-topoffset));
			}

			void drawBar(Graphics2D g2, float location, String name) {
				int buffer = (int)(.1*barheight);

				Rectangle r = new Rectangle(
					leftoffset+((int)location-HALF_WIDTH),
					imagebottom+buffer, BAR_WIDTH, barheight-buffer);
				g2.draw(r);

				FontMetrics fm = g2.getFontMetrics();
				int wid = fm.getHeight();

				int xloc = (int)(leftoffset+location+HALF_WIDTH-(wid/2));
				int yloc = (int)(imagebottom+barheight);

				g2.drawString(name, (int)xloc, (int)yloc);
			}

			public void mousePressed(MouseEvent e) {
				int cx = e.getX();
				int cy = e.getY();

				// if it is inside the image, calc location
				if(cx >= leftoffset && cx <= (leftoffset+imagewidth) &&
					cy >= 2*topoffset && cy <= imagebottom-topoffset)
					calcLocation(cx-leftoffset, cy-2*topoffset);

				boolean found = false;
				for(int i = 0; i < columnlocations.length; i++) {

					if(cx >= (columnlocations[i]-HALF_WIDTH+leftoffset) &&
						cx <= (columnlocations[i]+HALF_WIDTH+leftoffset)) {

						if(cy >= imagebottom && cy <= imagebottom+barheight) {
							found = true;
							selectedcol = i;
							break;
						}
					}
				}
				if(found) {
					mousedrag = true;
					mousex = cx;
					mousey = cy;
				}
				else {
					if(zoomin) {
						if(!e.isMetaDown()) {
							Dimension d = this.getPreferredSize();
							this.setPreferredSize(new Dimension((int)(d.width*1.1),
								(int)(d.height*1.1)));
							this.revalidate();
						}
						else {
							Dimension d = this.getPreferredSize();
							this.setPreferredSize(new Dimension((int)(d.width*.9),
								(int)(d.height*.9)));
							this.revalidate();
						}
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if(mousedrag) {
					updateImage();
					mousedrag = false;
				}
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseMoved(MouseEvent e) {}
			public void mouseDragged(MouseEvent e) {
				if(mousedrag) {
					float newloc = e.getX()-leftoffset;
					//if(newloc < leftoffset)
					if(newloc < 0)
						newloc = 0;
					if(newloc+leftoffset > getWidth()-leftoffset)
						newloc = imagewidth-2;

					// now loop through the columnlocations and swap if necessary

					//moving right
					if(newloc > columnlocations[selectedcol]) {
						for(int i = selectedcol+1; i < columnlocations.length; i++) {
							if(newloc > columnlocations[i]) {
								swap(selectedcol, i);
								selectedcol = i;
							}
						}
					}

					// moving left
					else {
						for(int i = selectedcol-1; i >= 0; i--) {
							if(newloc < columnlocations[i]) {
								swap(selectedcol, i);
								selectedcol = i;
							}
						}
					}

					columnlocations[selectedcol] = newloc;
					repaint();
				}
			}

			void swap(int one, int two) {
				int temp = columnorder[one];
				float temploc = columnlocations[one];
				columnorder[one] = columnorder[two];
				columnlocations[one] = columnlocations[two];
				columnorder[two] = temp;
				columnlocations[two] = temploc;
			}

			/**
			 * Calculate the lines that contain the point (x1, y1).
			 * If a line is found, the lines are redrawn and the selected
			 * lines are painted the selected color.
			 */
			private void calcLocation(float x1, float y1) {
				// find the two columns that this point
				// lies between

				int col1 = -1;
				int col2 = -1;
				// find the two columns that the point lies between
				for(int i = 0; i < columnlocations.length-1; i++) {
					if(columnlocations[i] <= x1
						&& columnlocations[i+1] >= x1) {
						col1 = i;
						col2 = 	i+1;
						break;
					}
				}

				boolean found = false;
				if(col1 != -1 && col2 != -1) {
					float xl = columnlocations[col1];
					float xr = columnlocations[col2];
					for(int i = 0; i < table.getNumRows(); i++) {
						float yl =
							heights[columnorder[col1]][i];
						float yr =
							heights[columnorder[col2]][i];

						if( (y1 < (yl+TOLERANCE)) && (y1 > (yr-TOLERANCE)) ||
							(y1 < (yr+TOLERANCE)) && (y1 > (yl-TOLERANCE))) {

							float m = (yr-yl)/(xr-xl);
							float b = yr-m*xr;
							if( Math.abs((y1-(m*x1)-b)) <= TOLERANCE && linemap[i]) {
								selectedlines[i] = !selectedlines[i];
								if(selectedlines[i])
									addSelection(i);
								else
									removeSelection(i);
								found = true;
							}
						}
					}
				}
				if(found)
					ma.updateImage();
			}
		}

		/**
		 * A small square with a black outline.  The color of the
		 * square is given in the constructor.
		 */
		class ColorComponent extends JComponent {
			private final int DIM = 12;
			Color bkgrd;

			ColorComponent(Color c) {
				super();
				setOpaque(true);
				bkgrd = c;
			}

			public Dimension getPreferredSize() {
				return new Dimension(DIM, DIM);
			}

			public Dimension getMinimumSize() {
				return new Dimension(DIM, DIM);
			}

			public void paint (Graphics g) {
				g.setColor (bkgrd);
				g.fillRect (0, 0, DIM-1, DIM-1);
				g.setColor (Color.black);
				g.drawRect (0, 0, DIM-1, DIM-1);
			}

			void setBkgrd(Color c) {
				bkgrd = c;
			}

			BufferedImage getImage() {
				BufferedImage image = new BufferedImage(DIM, DIM,
					BufferedImage.TYPE_INT_RGB);
				Graphics g = image.getGraphics();
				paint(g);
				return image;
			}
		}

		/**
		 * Filtering lines.
		 */
		class Filter extends JD2KFrame implements ActionListener {
			HashMap numericColumnLookup;
			HashMap stringColumnLookup;

			JComboBox numColumns;
			JComboBox strColumns;
			JComboBox numOps;
			JComboBox strOps;
			JTextField numValue;
			JTextField strValue;
			JButton numAdd;
			JButton strAdd;

			JButton cancel;
			JButton done;
			JButton update;

			JList filterList;
			JButton remove;

			DefaultListModel listModel;

			MainArea ma;

			Filter(MainArea m) {
				super("Filter");
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				ma = m;
				//setSize(300, 200);
				//JPanel bg = new JPanel();
				//showLines = new boolean[table.getNumRows()];
				//for(int i = 0; i < linemap.length; i++)
				//	showLines[i] = true;

				numericColumnLookup = new HashMap();
				stringColumnLookup = new HashMap();

				LinkedList numCols = new LinkedList();
				LinkedList strCols = new LinkedList();

				for(int i = 0; i < table.getNumColumns(); i++) {
					Column c = table.getColumn(i);
					if(c instanceof NumericColumn) {
						numericColumnLookup.put(c.getLabel(), new Integer(i));
						numCols.add(c.getLabel());
					}
					else {
						stringColumnLookup.put(c.getLabel(), new Integer(i));
						strCols.add(c.getLabel());
					}
				}

				JOutlinePanel num = new JOutlinePanel("Numeric");
				//num.setLayout(new GridLayout(2, 3));
				num.setLayout(new GridBagLayout());

				numColumns = new JComboBox();
				Iterator i = numCols.iterator();
				while(i.hasNext())
					numColumns.addItem(i.next());
				numCols.clear();
				numOps = new JComboBox();
				numOps.addItem(GREATER_THAN);
				numOps.addItem(LESS_THAN);
				numOps.addItem(GREATER_THAN_EQUAL_TO);
				numOps.addItem(LESS_THAN_EQUAL_TO);
				numOps.addItem(EQUAL_TO);
				numOps.addItem(NOT_EQUAL_TO);
				numValue = new JTextField(5);
				numAdd = new JButton("Add");
				numAdd.addActionListener(this);

				/*num.add(numColumns);
				num.add(numOps);
				num.add(numValue);
				num.add(new JPanel());
				num.add(numAdd);
				num.add(new JPanel());
				*/
				Constrain.setConstraints(num, numColumns, 0, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(num, numOps, 1, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(num, numValue, 2, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(num, numAdd, 1, 1, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);

				JPanel str = new JOutlinePanel("String");
				//str.setLayout(new GridLayout(2, 3));
				str.setLayout(new GridBagLayout());

				strColumns = new JComboBox();
				i = strCols.iterator();
				while(i.hasNext())
					strColumns.addItem(i.next());
				strCols.clear();
				strOps = new JComboBox();
				strOps.addItem(EQUAL_TO);
				strOps.addItem(NOT_EQUAL_TO);
				strValue = new JTextField(5);
				strAdd = new JButton("Add");
				strAdd.addActionListener(this);

				/*str.add(strColumns);
				str.add(strOps);
				str.add(strValue);
				str.add(new JPanel());
				str.add(strAdd);
				str.add(new JPanel());
				*/
				Constrain.setConstraints(str, strColumns, 0, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(str, strOps, 1, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(str, strValue, 2, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(str, strAdd, 1, 1, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);

				JPanel one = new JPanel();
		//one.setLayout(new GridLayout(2, 1));
				/*one.setLayout(new BoxLayout(one, BoxLayout.Y_AXIS));
				one.add(num);
				one.add(Box.createGlue());
				one.add(str);
				*/
				one.setLayout(new GridBagLayout());
				Constrain.setConstraints(one, num, 0, 0, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(one, new JPanel(), 0, 1, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				Constrain.setConstraints(one, str, 0, 2, 1, 1,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
					1, 1);
				JScrollPane jsp = new JScrollPane(one);
				jsp.setMinimumSize(jsp.getPreferredSize());
				//bg.setLayout(new BorderLayout());
				//bg.add(one, BorderLayout.CENTER);

				filterList = new JList();
				listModel = new DefaultListModel();
				JLabel lbl = new JLabel("Current Filters");
				Dimension d = lbl.getPreferredSize();
				//filterList.setFixedCellWidth(d.width);
				filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				filterList.setModel(listModel);
				JScrollPane jsp1 = new JScrollPane(filterList);
				JViewport jview = new JViewport();
				jview.setView(lbl);
				jsp1.setColumnHeader(jview);
				JPanel two = new JPanel();
				two.setLayout(new BorderLayout());
				two.add(jsp1, BorderLayout.CENTER);
				remove = new JButton("Remove");
				remove.addActionListener(this);
				JPanel rp = new JPanel();
				rp.add(remove);
				two.add(rp, BorderLayout.SOUTH);
				//bg.add(two, BorderLayout.EAST);
				JSplitPane bg = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					/*one*/jsp, two);

				cancel = new JButton("Cancel");
				cancel.addActionListener(this);
				done = new JButton("Done");
				done.addActionListener(this);
				update = new JButton("Update");
				update.addActionListener(this);

				JPanel buttonPanel = new JPanel();
				buttonPanel.add(cancel);
				buttonPanel.add(done);
				buttonPanel.add(update);

				JPanel whole = new JPanel();
				whole.setLayout(new BorderLayout());
				whole.add(bg, BorderLayout.CENTER);
				whole.add(buttonPanel, BorderLayout.SOUTH);

				getContentPane().add(whole);
				pack();
				setVisible(false);
			}

			/**
			 * Update the lines to show based on the current filteritems.
			 */
			void updateLineMap() {
				Object[] filters = listModel.toArray();
				if(filters.length == 0) {
					for(int i = 0; i < table.getNumRows(); i++)
						linemap[i] = true;
					return;
				}
				for(int i = 0; i < table.getNumRows(); i++) {
					boolean start = true;
					for(int j = 0; j < filters.length; j++) {
						FilterItem fi = (FilterItem)filters[j];
						int col = fi.colNum;
						boolean retVal;
						if(table.getColumn(col) instanceof NumericColumn)
							retVal = fi.evaluate(table.getDouble(i, col));
						else
							retVal = fi.evaluate(table.getString(i, col));
						start = start && retVal;
					}
					linemap[i] = start;
				}
			}

			/**
			 * Listen for button presses.
			 */
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if(src == strAdd) {
					String colLabel = strColumns.getSelectedItem().toString();
					String op = strOps.getSelectedItem().toString();
					String value = strValue.getText();
					int colNum = ((Integer)stringColumnLookup.get(colLabel)).intValue();
					FilterItem fi = new StringFilterItem(colLabel, colNum,
						op, value);
					listModel.addElement(fi);
					strValue.setText("");
				}
				else if(src == numAdd) {
					String colLabel = numColumns.getSelectedItem().toString();
					String op = numOps.getSelectedItem().toString();
					String value = numValue.getText();
					int colNum = ((Integer)numericColumnLookup.get(colLabel)).intValue();
					double d = 0;
					try {
						d = Double.parseDouble(value);
					}
					catch(Exception ex) {
					}
					FilterItem fi = new NumericFilterItem(colLabel, colNum,
						op, d);
					listModel.addElement(fi);
					numValue.setText("");
				}
				else if(src == remove) {
					int selected = filterList.getSelectedIndex();
					if(selected != -1)
						listModel.remove(selected);
				}
				else if(src == cancel) {
					listModel.removeAllElements();
					for(int i = 0; i < oldFilters.length; i++) {
						listModel.addElement(oldFilters[i]);
					}
					oldFilters = null;
					setVisible(false);
				}
				else if(src == done) {
					updateLineMap();
					ma.updateImage();
					oldFilters = null;
					setVisible(false);
				}
				else if(src == update) {
					updateLineMap();
					ma.updateImage();
					oldFilters = listModel.toArray();
				}
			}

			Object[] oldFilters;

			public void setVisible(boolean f) {
				if(f)
					oldFilters = listModel.toArray();
				super.setVisible(f);
			}

			/**
			 * Base class for filters.
			 */
			abstract class FilterItem {
				String label;
				int colNum;
				String op;

				abstract boolean evaluate(String s);
				abstract boolean evaluate(double d);
			}

			/**
			 * Filter out items for numeric columns
			 */
			class NumericFilterItem extends FilterItem {
				double value;

				NumericFilterItem(String l, int c, String o, double v) {
					label = l;
					colNum = c;
					op = o;
					value = v;
				}

				boolean evaluate(String s) {
					double d = 0;
					try {
						d = Double.parseDouble(s);
					}
					catch(Exception e) {
						return false;
					}
					return evaluate(d);
				}

				boolean evaluate(double d) {
					if(op == GREATER_THAN)
						//return value > d;
						return value < d;
					else if(op == GREATER_THAN_EQUAL_TO)
						//return value >= d;
						return value <= d;
					else if(op == LESS_THAN)
						//return value < d;
						return value > d;
					else if(op == LESS_THAN_EQUAL_TO)
						//return value <= d;
						return value >= d;
					else if(op == EQUAL_TO)
						//return value == d;
						return value != d;
					else if(op == NOT_EQUAL_TO)
						//return value != d;
						return value == d;
					return false;
				}

				/**
				*/
				public String toString() {
					return label+" "+op+" "+value;
				}
			}

			/**
			 * Filter out items for non-numeric columns.
			 * String equality and inequality is used
			 */
			class StringFilterItem extends FilterItem {
				String value;

				StringFilterItem(String l, int c, String o, String v) {
					label = l;
					colNum = c;
					op = o;
					value = v;
				}

				/**
					Return true if the item should be shown
				*/
				boolean evaluate(String s) {
					if(op == EQUAL_TO)
						return value.trim().equals(s.trim());
					else if(op == NOT_EQUAL_TO)
						return !value.trim().equals(s.trim());
					return false;
				}

				boolean evaluate(double d) {
					String s;
					try {
						s = Double.toString(d);
					}
					catch(Exception e) {
						return false;
					}
					return evaluate(s);
				}

				public String toString() {
					return label+" "+op+" "+value;
				}
			}
		}

		/**
			A small window into the table we are visualizing.
			This only shows the rows that are selected.
		*/
		class InfoTableModel extends DefaultTableModel {
			ArrayList rows;

			InfoTableModel() {
				rows = new ArrayList();
			}

			void addInfoRow(int i) {
				rows.add(new Integer(i));
				fireTableDataChanged();
			}

			void removeInfoRow(int i) {
				rows.remove(new Integer(i));
				fireTableDataChanged();
			}

			public int getColumnCount() {
				return table.getNumColumns();
			}

			public int getRowCount() {
				if(rows == null)
					return 0;
				return rows.size();
			}

			public String getColumnName(int i) {
				return table.getColumnLabel(i);
			}

			public Object getValueAt(int row, int col) {
				Integer i = (Integer)rows.get(row);
				return table.getString(i.intValue(), col);
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		}

		/**
		 * Show the colors for each class name and its percentage of
		 * the composite
		 */
		class Legend extends JPanel {
			Legend() {
				setLayout(new GridBagLayout());
				setBackground(yellowish);
			}

			void updateLegend(HashMap lk) {
				removeAll();
				/*JLabel leg = new JLabel("LEGEND");//new AALabel("LEGEND");
				leg.setBackground(yellowish);
				Constrain.setConstraints (this, leg, 1, 0, 1, 1,
					GridBagConstraints.HORIZONTAL,
					GridBagConstraints.NORTH, 1.0, 0.0,
					new Insets(2, 4, 2, 0));
				*/
				Iterator it = lk.keySet().iterator();

				int i = 0;
				while(it.hasNext()) {
					String text = (String)it.next();
					Color c = (Color)lk.get(text);

					Insets ii = new Insets(4, 8, 4, 0);
					Insets i2 = new Insets(4, 8, 4, 0);

					JLabel ll = new JLabel(text);
					ColorComponent cc = new ColorComponent(c);
					Constrain.setConstraints (this, cc, 0, i, 1, 1,
						GridBagConstraints.NONE,
						GridBagConstraints.NORTH, 0.0, 0.0, ii);
					Constrain.setConstraints (this, ll, 1, i, 1, 1,
						GridBagConstraints.HORIZONTAL,
						GridBagConstraints.NORTH, 1.0, 0.0, i2);
					i++;
				}
				revalidate();
				repaint();
			}
		}
	}
}