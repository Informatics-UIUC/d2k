package ncsa.d2k.modules.core.vis;

import java.io.*;
import java.awt.*;
import java.awt.event.*; // needed for PCVisMain
import java.awt.image.*; // ""
import javax.swing.*;    // ""
import javax.swing.table.*;
import java.awt.geom.*;  // "" & Control
import java.text.NumberFormat;
import ncsa.d2k.util.datatype.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.UserView;
import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.gui.JD2KFrame;

import java.util.*;

import ncsa.gui.Constrain;

/**
	Given a VerticalTable, plot its data in parallel.  If an ExampleTable
	is used, only its inputs and outputs will be plotted.  Columns may be
	added or removed using the menu.  The columns displayed may be swapped
	by clicking on the column's name and dragging it to the new location.
	In addition, lines may be highlighted by clicking on them.  The
	highlighted line will appear in red.  The row that corresponds to
	the selected line can be viewed in the InfoFrame, which is accesible
	via the menu.
*/
public class ParallelCoordinateVis extends VisModule {

	static final int INT_TYPE = 0;
	static final int DOUBLE_TYPE = 1;
	static final int STRING_TYPE = 2;
	static final float TOLERANCE = (float)2.005;

   public String getModuleInfo() {
	   StringBuffer sb = new StringBuffer("Given a VerticalTable, plot its data");
	   sb.append(" in parallel.  If an ExampleTable is used, only its inputs and");
	   sb.append(" outputs will be plotted.  Columns may be added or removed using");
	   sb.append(" the menu.  The columns displayed may be swapped by clicking on");
	   sb.append(" the column's name and dragging it to the new location.  In");
	   sb.append(" addition, lines may be highlighted by clicking on them.  The");
	   sb.append(" highlighted line will appear in red.  The row that corresponds");
	   sb.append(" to the selected line can be viewed in the InfoFrame, which is");
	   sb.append(" accesible via the menu.");
	   return sb.toString();
   }

   public String[] getInputTypes() {
      String[] inputTypes = {"ncsa.d2k.util.datatype.VerticalTable"};
      return inputTypes;
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The VerticalTable to be visualized.";
      else
         return "There is no such input.";
   }

   public String[] getOutputTypes() { return null; }
   public String getOutputInfo(int index) { return null; }
   public String[] getFieldNameMapping() { return null; }

   protected UserView createUserView() {
      return new PCVis();
   }

    // PCVisMain & PCVisControl?
   protected class PCVis extends JUserPane implements Serializable {

      //PCVisControl visleft;
      PCVisMain visright;

		JMenuBar menu;

      public void initView(ViewModule m) {
      }

		public Object getMenu() {
			return menu;
		}

      public void setInput(Object o, int i) {
         if (i == 0) {
			VerticalTable t = (VerticalTable)o;
         	visright = new PCVisMain(t);
            //visright.initialize((VerticalTable) o);
			setLayout(new BorderLayout());
			add(visright, BorderLayout.CENTER);
         }
      }
   //}

/*public class PCVisControl extends JPanel
   implements Serializable {

   Font font_default = new Font("Helvetica", Font.PLAIN, 14);

   public void update(Graphics g) { }

   public void paint(Graphics g) {

      Graphics2D g2 = (Graphics2D) g;

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);

      Dimension d = getSize();
      int w = d.width, h = d.height;

      g2.setFont(font_default);
      FontMetrics m = g2.getFontMetrics();

      g2.setPaint(new Color(32, 32, 192));
      g2.fill(new Rectangle2D.Double(0, 0, w, h));

      g2.setPaint(Color.white);
      g2.drawString("...", 25, 50);

   }

   public Dimension getMinimumSize() {
      return new Dimension(200, 400);
   }

   public Dimension getPreferredSize() {
      return getMinimumSize();
   }

}*/

	/**
	*/
	class PCVisMain extends JPanel
		implements MouseListener, MouseMotionListener, ActionListener, Serializable {

		// the number of rows and columns in the table
		int rows, cols;
		// the heights of the lines, zoom heights of the lines, and bounds of the columns
		float[][] heights, zheights, bounds;

		// the location of the lines at the current size
		float[][] ylocations;

		// the index of the column that is the key
		int keycol;
		// the number of unique items in the key column
		int numUnique;
		// a table with the unique items from the key column and their uids,
		// which are the indices into the color table
		HashMap uniqueColorMap;
		// the indices of the columns to show, and the types of columns
		int[] order, types;
		// most of the vis is kept as a buffered image
		BufferedImage buffer;
		// if this is set to true, the buffer will be redrawn
		boolean redrawBuffer = false;

		// the color id for each row of the table
		int[] colorid;
		// a boolean matrix with a location for each line.  If it is set to false,
		// the line will not be drawn
		boolean[][] linemap;
		// has one spot for each row.  if it is true for a row, the lines for that row will
		// be painted red, to show selection
		boolean[] selectedlines;

		// the legend
		JFrame legend;

		// the menu items
		IdCheckBoxMenuItem[] colNames;
		UniqueMenuItem[] uniqueColors;
		KeyCheckBoxMenuItem[] keys;
		JCheckBoxMenuItem useAntiAlias;
		JMenuItem showLegend;
		JMenuItem showInfo;
		JMenuItem clearSelected;

		boolean initialized = false, painted = false, zoomed = false,
			mouseband = false, mousedrag = false;
		int grid, ascent, descent, colw, colh, lastw = -1, lasth = -1,
			mousex, mousey, lastx, lasty, selectedcol,
			zgrid, zx, zy, zw, zh, leftof, leftamt, rightof, rightamt, pad;
		float factx, facty;

		// the table to show
		VerticalTable vt;

		// the color table
		Color[] ctable = {new Color(253, 204, 138), new Color(148, 212, 161),
			new Color(153, 185, 216), new Color(189, 163, 177),
			new Color(213, 213, 157), new Color(193,  70,  72),
			new Color( 29, 136, 161), new Color(187, 116, 130),
			new Color(200, 143,  93), new Color(127, 162, 133)};

		Font font_default = new Font("Helvetica", Font.PLAIN, 14);

		JFrame infoFrame;
		JTable infoTable;
		InfoTableModel infoModel;
		JScrollPane scrollPane;

		/**
		*/
		class IdCheckBoxMenuItem extends JCheckBoxMenuItem {
			int id;

			IdCheckBoxMenuItem(String s, boolean b, int i) {
				super(s, b);
				id = i;
			}
		}

		/**
			Used to choose the key column
		*/
		class KeyCheckBoxMenuItem extends JCheckBoxMenuItem {
			int id;

			KeyCheckBoxMenuItem(String s, boolean b, int i) {
				super(s, b);
				id = i;
			}
		}

		/**
		*/
		class UniqueMenuItem extends JMenuItem {
			int id;
			UniqueMenuItem(String s, int i) {
				super(s);
				id = i;
			}
		}

		/**
			Constructor
		*/
		PCVisMain(VerticalTable t) {
			addMouseListener(this);
			addMouseMotionListener(this);
			setDoubleBuffered(false);
			initialize(t);
		}

		/**
			Initialize
		*/
		void initialize(VerticalTable table) {
			vt = table;

			// if it is an ExampleTable, we only show the inputs and outputs
			// by default
			if(vt instanceof ExampleTable) {
				int[] ins = ((ExampleTable)vt).getInputFeatures();
				int[] outs = ((ExampleTable)vt).getOutputFeatures();

				if(ins == null)
					ins = new int[0];
				if(outs == null)
					outs = new int[0];
				order = new int[ins.length+outs.length];

				// copy the inputs and outputs to the order array
				// these are the columns that will be displayed by default
				int idx = 0;
				for(int id = 0; id < ins.length; id++) {
					order[idx] = ins[id];
					idx++;
				}
				for(int id = 0; id < outs.length; id++) {
					order[idx] = outs[id];
					idx++;
				}

				// the key column is the first output column
				if(outs.length > 0)
					keycol = outs[0];
				// or the last column of the table if there are no outputs
				else
					keycol = vt.getNumColumns()-1;
			}
			// otherwise it's just a plain VerticalTable
			// use all the columns and use the last column as the key
			else {
				order = new int[vt.getNumColumns()];
				for(int id = 0; id < order.length; id++)
					order[id] = id;
				keycol = vt.getNumColumns() - 1;
			}

			cols = table.getNumColumns();
			rows = table.getNumRows();

			heights = new float[cols][rows];
			ylocations = new float[cols][rows];
			zheights = new float[cols][rows];
			bounds = new float[cols][2];
			types = new int[cols];
			linemap = new boolean[order.length-1][rows];
			selectedlines = new boolean[rows];
			colorid = new int[rows];

			// get the type of each column
			for (int i = 0; i < cols; i++) {
				Column col = table.getColumn(i);

				if (col instanceof IntColumn) {
					types[i] = INT_TYPE;

					bounds[i][0] = (float) ((IntColumn)col).getMin();
					bounds[i][1] = (float) ((IntColumn)col).getMax();
				}
				else if (col instanceof DoubleColumn) {
					types[i] = DOUBLE_TYPE;

					bounds[i][0] = (float) ((DoubleColumn)col).getMin();
					bounds[i][1] = (float) ((DoubleColumn)col).getMax();
				}
				else {
					types[i] = STRING_TYPE;

					bounds[i][0] = (float)-9999.2;
					bounds[i][1] = (float)-9999.1;
				}
			}

			// now find all the unique items in the key column
			findUniqueColors();

			// calculate the heights of all items
			for (int i = 0; i < order.length; i++)
				calcHeights(order[i]);

			// determine which lines are overlapping
			calcLineMap();

			// create the menus
			menu = new JMenuBar();
			JMenu m1 = new JMenu("Options");
			JMenu m2 = new JMenu("Display Columns");
			colNames = new IdCheckBoxMenuItem[vt.getNumColumns()];

			// make a menu item for each column
			// this allows the user to show/hide a column
			for(int j = 0; j < vt.getNumColumns(); j++) {

				colNames[j] = new IdCheckBoxMenuItem(vt.getColumnLabel(j),
					orderContains(j), j);
				colNames[j].addActionListener(this);
				m2.add(colNames[j]);
			}
			m1.add(m2);

			// make a menu item for each unique item in the key column
			// this will allow the user to change the color of a class of lines
			JMenu m3 = new JMenu("Choose Colors");
			uniqueColors = new UniqueMenuItem[numUnique];
			Iterator it = uniqueColorMap.keySet().iterator();
			int idx = 0;
			while(it.hasNext()) {
				String s = (String)it.next();
				Integer in = (Integer)uniqueColorMap.get(s);
				uniqueColors[idx] = new UniqueMenuItem(s, in.intValue());
				uniqueColors[idx].addActionListener(this);
				m3.add(uniqueColors[idx]);
			}
	   		m1.add(m3);

			// make a menu for each column
			// this allows the user to choose the key column
			JMenu m4 = new JMenu("Choose Key Column");
			keys = new KeyCheckBoxMenuItem[vt.getNumColumns()];
			for(int j = 0; j < vt.getNumColumns(); j++) {
				keys[j] = new KeyCheckBoxMenuItem(vt.getColumnLabel(j), j==keycol, j);
				keys[j].addActionListener(this);
				m4.add(keys[j]);
			}
			m1.add(m4);

			// a checkbox item to toggle antialiasing
	   		m1.addSeparator();
	   		useAntiAlias = new JCheckBoxMenuItem("Use Antialiasing", false);
	   		m1.add(useAntiAlias);
	   		useAntiAlias.addActionListener(this);
			showLegend = new JMenuItem("Show Legend");
			showLegend.addActionListener(this);
			m1.add(showLegend);
			showInfo = new JMenuItem("Show Selection Info");
			m1.add(showInfo);
			showInfo.addActionListener(this);
			clearSelected = new JMenuItem("Clear Selected Lines");
			clearSelected.addActionListener(this);
			m1.add(clearSelected);
			menu.add(m1);

			// create the info frame, which shows the selected rows
			createInfoFrame();
		}

		boolean orderContains(int i) {
			for(int j = 0; j < order.length; j++)
				if(order[j] == i)
					return true;
			return false;
		}

		/**
			Find all the unique entries in the key column.  Assign each
			one an id, starting from 0. This will be the index into the color table.
		*/
		private void findUniqueColors() {
			// now find all the unique items in the key column
			uniqueColorMap = new HashMap();
			int uid = 0;
			for(int j = 0; j < rows; j++) {
				String s = vt.getString(j, keycol);
				Integer in = (Integer)uniqueColorMap.get(s);
				if(in == null) {
					uniqueColorMap.put(s, new Integer(uid));
					colorid[j] = uid;
					uid++;
				}
				else
					colorid[j] = in.intValue();
			}
			numUnique = uniqueColorMap.size();
		}

		/**
			Calculate the heights of the lines in a given column
		*/
		private void calcHeights(int x) {
			switch (types[x]) {

				case INT_TYPE:
					for (int i = 0; i < rows; i++)
						heights[x][i] = (vt.getFloat(i, x) - bounds[x][0]) /
						(bounds[x][1] - bounds[x][0]);
					break;

				case DOUBLE_TYPE:
					for (int i = 0; i < rows; i++)
						heights[x][i] = (vt.getFloat(i, x) - bounds[x][0]) /
						(bounds[x][1] - bounds[x][0]);
					break;

				case STRING_TYPE:
					// if it is the key column, we already know the number of unique items
					if(x == keycol) {
						for (int i = 0; i < rows; i++)
							heights[x][i] = 1 -
							(float) (colorid[i] + 1) /
							(float) (numUnique + 1);
					}
					else {
						// find the number unique in this column
						HashMap hm = new HashMap();
						int idx = 0;
						for(int i = 0; i < rows; i++)
						if(hm.get(vt.getString(i, x)) == null) {
							hm.put(vt.getString(i, x), new Integer(idx));
							idx++;
						}
						int uni = hm.size();

						for(int i = 0; i < rows; i++) {
							heights[x][i] = 1 -
							(float) (((Integer)hm.get(vt.getString(i, x))).floatValue()+1) /
							(float) (uni + 1);
						}
					}
					break;
			}
		}

		/**
			Calculate the zoom heights
		*/
		private void calcZHeights(int x) {
			for (int i = 0; i < rows; i++) {
				zheights[x][i] = (float)((float)1 / (float)facty) *
					((float)heights[x][i] -
					((float)1 - (float)(zy + zh - colw)/
					(float)(lasth - 2*colw - colh)));
			}
		}

		//int numSaved = 0;

		/**
			The line map is a matrix of boolean values.  There is one spot for each potential
			line to be drawn.  If two lines overlap, (ie their begin and end points are the
			same) we set its value in the matrix to false.  Then only the first line at
			that location will be drawn.
		*/
		private void calcLineMap() {
			HashMap lineCache = new HashMap();
			linemap = new boolean[order.length-1][vt.getNumRows()];
			for(int i = 0; i < linemap.length; i++) {
				for(int j = 0; j < linemap[0].length; j++) {
					/*double d1;
					double d2;
					if(types[i] == INT_TYPE || types[i] == DOUBLE_TYPE)
						d1 = vt.getDouble(j, i);
					else
						d1 = (double)colorid[j];
					if(types[i+1] == INT_TYPE || types[i+1] == DOUBLE_TYPE)
						d2 = vt.getDouble(j, i);
					else
						d2 = (double)colorid[j];
					*/

					//PCLine p1 = new PCLine(heights[order[i]][j], heights[order[i+1]][j]);
					Point2D.Float p1 = new Point2D.Float(heights[order[i]][j], heights[order[i+1]][j]);
					//PCLine p1 = new PCLine(d1, d2);
					if(lineCache.containsKey(p1))  {
						if(colorid[j] == -1)
							linemap[i][j] = true;
						else {
							linemap[i][j] = false;
							//numSaved++;
						}
					}
					else {
						linemap[i][j] = true;
						lineCache.put(p1, p1);
					}
				}
				lineCache.clear();
			}
			//System.out.println("numSaved: "+numSaved);
			//numSaved = 0;
		}

		/**
			Get the color to draw.
		*/
		private Color getColor(int x) {
			return ctable[x % ctable.length];
		}

		/**
			Change a color in the color table.
		*/
		private void setColor(int x, Color c) {
			ctable[x % ctable.length] = c;
		}

		int ydist;

		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, y, w, h);

			// recalculate the ylocations for all lines
	  		if(painted) {
      			ydist = h - 2*colw - colh;

				// calculate the ylocations for each line
				for (int i = 0; i < order.length; i++)
					//for (int j = 0; j < rows; j++)
					//	ylocations[i][j] = ydist*heights[order[i]][j];
					calcYLocations(i);
			}

			// create new image
      		buffer = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
			lastw = w;
			lasth = h;

			grid = (int) ((w - 2*pad - colw) / (order.length - 1));

			//if (grid < 2*colw)
			//	grid = 2*colw;
			if(grid < colw)
				grid = colw + 6;
			redrawBuffer = true;
		}

		/**
			Paint the vis onto the screen.  The default view is an image that is blitted
			to the screen.  The image is only redrawn when the columns change or the
			size changes.  The zoomed view is drawn to the screen here however.
		*/
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			if(useAntiAlias.getState())
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			else
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);

				Dimension d = getSize();
				int w = d.width, h = d.height;

				// set the font
				g2.setFont(font_default);
				FontMetrics m = g2.getFontMetrics();

				if (!painted) {
					painted = true;
					ascent = m.getAscent();
					descent = m.getDescent();
					colw = ascent + descent + 10;

					colh = 0;
					for(int i = 0; i < cols; i++) {
						if(m.stringWidth(vt.getColumnLabel(i)) > colh)
							colh = m.stringWidth(vt.getColumnLabel(i));
					}

					colh += 10;
					//colh = 100;
					pad = 15;
					grid = (int) ((w - 2*pad - colw) / (order.length - 1));
					//if (grid < 2*colw)
					//	grid = 2*colw;
					if(grid < colw)
						grid = colw + 6;

					ydist = h - 2*colw - colh;
					// calculate the ylocations for each line
					for (int i = 0; i < order.length; i++)
						//for (int j = 0; j < rows; j++)
						//	ylocations[i][j] = ydist*heights[order[i]][j];
						calcYLocations(i);
				}

      			int xpos, ypos;
      			if (!zoomed) {
         			if (redrawBuffer) {
            			drawBuffer(w, h);
						redrawBuffer = false;
         			}

					// draw the image on the screen
					g2.drawImage(buffer, 0, 0, this);

					// draw a box if the user is zooming
					if (mouseband) {
						g2.setColor(Color.black);

						if (mousex - lastx >= 0 && mousey - lasty >= 0)
							g2.draw(new Rectangle2D.Float(lastx, lasty,
								mousex - lastx, mousey - lasty));
						else if (mousex - lastx >= 0 && mousey - lasty < 0)
							g2.draw(new Rectangle2D.Float(lastx, mousey,
								mousex - lastx, lasty - mousey));
						else if (mousex - lastx < 0 && mousey - lasty >= 0)
							g2.draw(new Rectangle2D.Float(mousex, lasty,
								lastx - mousex, mousey - lasty));
						else
							g2.draw(new Rectangle2D.Float(mousex, mousey,
								lastx - mousex, lasty - mousey));
					}
					// draw a red dashed line if the user is moving columns
					if (mousedrag) {
						g2.setPaint(Color.red);
						g2.draw(new Rectangle2D.Float(pad + grid*selectedcol,
							h - colh, colw - 1, colh - 1));

						float dash[] = {10.0f};
						g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER, 10.0f,
							dash, 0.0f));
						g2.draw(new Line2D.Float(mousex, colw, mousex, h - colw - colh));
						g2.setStroke(new BasicStroke(1.0f));

						xpos = pad;

						for (int i = 0; i < order.length; i++) {
							if (i != selectedcol && mousex > xpos && mousex < xpos + colw) {
								g2.draw(new Rectangle2D.Float(pad + grid*i, h - colh,
									colw - 1, colh - 1));
							}
							xpos += grid;
						}
				}
			}
			// we are zoomed
			else {
				g2.setPaint(Color.white);
				g2.fill(new Rectangle2D.Float(0, 0, w, h));

				if (leftof == -1 || rightof == -1)
					return;

				int gridnum = rightof - leftof;

				zgrid = (int) (w /
					((float)leftamt/(float)grid +
					gridnum + (float)rightamt/(float)grid));

				xpos = (int) (zgrid * leftamt / grid);

				for (int i = leftof; i <= rightof; i++) {
						for (int j = 0; j < rows; j++) {
							if (i == leftof && i > 0) {
								g2.setPaint(getColor(colorid[j]));
								if(selectedlines[j])
									g2.setPaint(Color.red);
								g2.draw(new Line2D.Float(xpos,
									h - h*zheights[order[i]][j],
									xpos - zgrid,
									h - h*zheights[order[i-1]][j]));
							}

							if (i != order.length - 1) {
								g2.setPaint(getColor(colorid[j]));
								if(selectedlines[j])
									g2.setPaint(Color.red);
								g2.draw(new Line2D.Float(xpos,
									h - h*zheights[order[i]][j],
									xpos + zgrid,
									h - h*zheights[order[i+1]][j]));
							}

							g2.setPaint(Color.darkGray);
							g2.draw(new Line2D.Float(xpos, 0, xpos, h));
					}
					xpos += zgrid;
				}

				if (gridnum == -1) {
					xpos = -1 * (int) (zgrid * (rightamt - zw) / grid);

					for (int j = 0; j < rows; j++) {
						g2.setPaint(getColor(colorid[j]));
						g2.draw(new Line2D.Float(xpos,
							h - h*zheights[order[rightof]][j],
							xpos + zgrid,
							h - h*zheights[order[leftof]][j]));
					}
				}
			}
		}

   /**
		Draw the default view to the screen.
   */
   private void drawBuffer(int x, int y) {
      if (x < 1)
         x = 1;
      if (y < 1)
         y = 1;

      Graphics2D bg2 = buffer.createGraphics();

		if(useAntiAlias.getState())
			bg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		else
			bg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

      bg2.setFont(font_default);
      FontMetrics m = bg2.getFontMetrics();

      bg2.setPaint(Color.white);
      bg2.fill(new Rectangle2D.Float(0, 0, x, y));

      /*if (y < (2*colw + colh)) {
         bg2.setPaint(Color.black);
         bg2.drawString("Please enlarge the window.", 5, ascent + 5);
         return;
      }*/

      int xpos = pad + (int)(.5*colw);
      int ypos = y - colw - colh;
      //int ydist = y - 2*colw - colh;

      AffineTransform normalXform = bg2.getTransform();
      AffineTransform rightXform = new AffineTransform();
      rightXform.translate(xpos, y);
      rightXform.rotate(Math.toRadians(-90));

	  // draw the data lines
      for (int i = 0; i < order.length-1; i++) {
         for (int j = 0; j < rows; j++) {

            //if (i != order.length - 1) {
			if(linemap[i][j]) {
				bg2.setPaint(getColor(colorid[j]));
              	bg2.draw(new Line2D.Float(xpos,
					//ypos-ydist*heights[order[i]][j],
					ypos-ylocations[order[i]][j],
					xpos + grid,
					//ypos-ydist*heights[order[i+1]][j]));
					ypos-ylocations[order[i+1]][j]));
			}
			//}
            bg2.setPaint(Color.darkGray);
            bg2.draw(new Line2D.Float(xpos, 0, xpos, y));
         }
         xpos += grid;
      }

		// get the last line
		bg2.draw(new Line2D.Float(xpos, 0, xpos, y));

		xpos = pad + (int)(.5*colw);
		bg2.setPaint(Color.red);

	  // draw the selected lines.  this is done outside the main loop so that
	  // we will always draw the selected line on top
      for (int i = 0; i < order.length-1; i++) {
         for (int j = 0; j < rows; j++) {
			if(selectedlines[j]) {
              	bg2.draw(new Line2D.Float(xpos,
					ypos-ylocations[order[i]][j],
					xpos + grid,
					ypos-ylocations[order[i+1]][j]));
			}
         }
         xpos += grid;
		}

      bg2.setPaint(Color.white);
      bg2.fill(new Rectangle2D.Float(0, 0, x, 1));
      bg2.fill(new Rectangle2D.Double(0, 0, 1, colw));

      bg2.setPaint(Color.lightGray);
      bg2.fill(new Rectangle2D.Float(1, 1, x - 1, colw - 1));

      bg2.setPaint(Color.darkGray);
      bg2.fill(new Rectangle2D.Float(0, colw - 1, x, 1));
      bg2.fill(new Rectangle2D.Float(x - 1, 0, 1, colw));


      bg2.setPaint(Color.white);
      bg2.fill(new Rectangle2D.Float(0, ypos, x, 1));
      bg2.fill(new Rectangle2D.Double(0, ypos, 1, colw));

      bg2.setPaint(Color.lightGray);
      bg2.fill(new Rectangle2D.Float(1, ypos + 1, x - 2, colw - 2));
      bg2.setPaint(Color.darkGray);
      bg2.fill(new Rectangle2D.Float(0, ypos + colw - 1, x, 1));
      bg2.fill(new Rectangle2D.Float(x - 1, ypos, 1, colw));


      bg2.setPaint(Color.white);
      bg2.fill(new Rectangle2D.Double(0, ypos + colw, x, y));

      bg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

      xpos = pad + (int)(.5*colw);
      String foo;
	  FontMetrics fm = bg2.getFontMetrics();
      for (int i = 0; i < order.length; i++) {

         if (types[order[i]] != STRING_TYPE) {

            bg2.setPaint(Color.black);
            //foo = Float.toString(bounds[order[i]][1]);
			foo = prettyFloat(bounds[order[i]][1]);
            bg2.drawString(foo,
                           xpos - (int)(.5*m.stringWidth(foo)),
                           colw - 5 - descent);
            //foo = Float.toString(bounds[order[i]][0]);
			foo = prettyFloat(bounds[order[i]][0]);
            bg2.drawString(foo,
                           xpos - (int)(.5*m.stringWidth(foo)),
                           y - colh - 5 - descent);
         }

         bg2.setPaint(Color.darkGray);
         bg2.draw(new Line2D.Float(xpos - (int)(.5*colw), y - colh,
                                    xpos - (int)(.5*colw), y));
         bg2.draw(new Line2D.Float(xpos + (int)(.5*colw), y - colh,
                                    xpos + (int)(.5*colw), y));
         bg2.setPaint(Color.lightGray);
         bg2.fill(new Rectangle2D.Float(xpos - (int)(.5*colw) + 1,
                                         y - colh,
                                         colw - 1, colh));

         bg2.setPaint(Color.black);
         bg2.transform(rightXform);
         bg2.drawString(vt.getColumnLabel(order[i]), (colh-fm.stringWidth(vt.getColumnLabel(order[i])))/2, 5);
         bg2.setTransform(normalXform);

         rightXform.translate(0, grid);

         xpos += grid;
      }
   }

	String prettyFloat(float f) {
		NumberFormat nf = NumberFormat.getInstance();
		//nf.setMinimumFractionDigits(3);
		nf.setMaximumFractionDigits(5);
		StringBuffer sb = new StringBuffer(nf.format(f));
		return sb.toString();
	}

	public void update(Graphics g) {
	}

	/**
		Cache the ylocations, because they change
		infrequently.
	*/
	private void calcYLocations(int i) {
		for (int j = 0; j < rows; j++)
			ylocations[i][j] = ydist*heights[order[i]][j];
	}

   public void mouseClicked(MouseEvent e) {
      int cx = e.getX(), cy = e.getY();

	  // increase/decrease bounds when it is clicked on
      if (cy <= colw) {
         int xpos = pad;
         for (int i = 0; i < cols; i++) {

            if (cx >= xpos && cx <= xpos + colw && types[order[i]] != STRING_TYPE) {

               if (e.isAltDown())
                  bounds[order[i]][1] += 1;
               else if (e.isMetaDown())
                  bounds[order[i]][1] -= 1;

               calcHeights(order[i]);
			   calcYLocations(order[i]);
               //drawBuffer(lastw, lasth);
			   redrawBuffer = true;
               repaint();
               return;

            }

            xpos += grid;
         }
      }
      else if (cy > colw && cy < lasth - colh - colw) {
            int xpos = pad;
            boolean found = false;

            for (int i = 0; i < cols; i++) {

               if (!found && cx >= xpos && cx <= xpos + colw) {
                  found = true;
                  selectedcol = i;
               }
			//repaint();
               xpos += grid;
            }

		/*int pixel = buffer.getRGB(cx, cy);
 		int red = (pixel >> 16) & 0xff;
 		int green = (pixel >> 8) & 0xff;
 		int blue = (pixel) & 0xff;

		if(red != 255 && green != 255 && blue != 255) {
		*/
			found = false;
			int idx = 0;
      		xpos = pad + (int)(.5*colw);
			while(!found && idx < cols) {
				if(cx >= xpos && cx <= (xpos + grid)) {
		  			calcLocation(idx, cx, cy);
					found = true;
				}
				xpos += grid;
				idx++;
			}
	//	}
	  }
	  // increase/decrease bounds when it is clicked on
      else if (cy >= lasth - colh - colw && cy <= lasth - colh) {
         int xpos = pad;
         for (int i = 0; i < cols; i++) {

            if (cx >= xpos && cx <= xpos + colw && types[order[i]] != STRING_TYPE) {

               if (e.isAltDown())
                  bounds[order[i]][0] += 1;
               else if (e.isMetaDown())
                  bounds[order[i]][0] -= 1;

               calcHeights(order[i]);
			   calcYLocations(order[i]);
               //drawBuffer(lastw, lasth);
			   redrawBuffer = true;
               repaint();
               return;
            }

            xpos += grid;

         }
      }
   }

   public void mouseEntered(MouseEvent e) { }
   public void mouseExited(MouseEvent e) { }

   /**
   		Prepare to zoom or move columns when the mouse is pressed.
		Repaint is called to provide some feedback to the user (ie
		draw a box around the selected area or highlight the selected
		column in red)
	*/
   public void mousePressed(MouseEvent e) {

      if (zoomed) {
         zoomed = false;
         mouseband = false;
         mousedrag = false;
         repaint();
         return;
      }
      else {
         int cx = e.getX(), cy = e.getY();

         if (cy > colw && cy < lasth - colh - colw) {
            mouseband = true;
            lastx = cx; lasty = cy; mousex = cx; mousey = cy;
            repaint();
            return;
         }
         else if (cy > lasth - colh) {
            int xpos = pad;
            boolean found = false;

            for (int i = 0; i < cols; i++) {

               if (!found && cx >= xpos && cx <= xpos + colw) {
                  found = true;
                  selectedcol = i;
               }
				repaint();
               xpos += grid;

            }

            if (found) {
               mousedrag = true;
               mousex = cx; mousey = cy;
               repaint();
               return;

            }
         }
      }
   }

   /**
   		Zoom the view if the user originally pressed in the line area,
		or change the order of the columns if the user originally pressed
		in the column label area.
	*/
   public void mouseReleased(MouseEvent e) {
      int cx = e.getX(), cy = e.getY();

      if (mouseband) {
         if (mousex > lastx) {
            zx = lastx; zw = mousex - lastx;
         }
         else {
            zx = mousex; zw = lastx - mousex;
         }

         if (mousey > lasty) {
            zy = lasty; zh = mousey - lasty;
         }
         else {
            zy = mousey; zh = lasty - mousey;
         }

         int diffx = Math.abs(zw),
             diffy = Math.abs(zh);

         if (diffx < 2 || diffy < 2) {
            mouseband = false;
			//drawBuffer(lastw, lasth);
            repaint();
            return;
         }
         else {
            factx = (float) diffx / (float) grid;
            facty = (float) diffy / (float) (lasth - 2*colw - colh);

            boolean foundleft = false;
            int xpos = pad + (int)(.5*colw);
            leftof = -1;
            rightof = -1;

            for (int i = 0; i < cols; i++) {

               if (!foundleft && zx < xpos) {
                  foundleft = true;
                  leftof = i;
                  leftamt = xpos - zx;
               }

               if ((zx + zw) > xpos) {
                  rightof = i;
                  rightamt = zx + zw - xpos;
               }

               xpos += grid;
            }

            zoomed = true;

            for (int i = leftof - 1; i <= rightof + 1; i++)
               if (i >= 0 && i < cols)
                  calcZHeights(order[i]);

            repaint();
            return;
         }
      }
      else if (mousedrag) {
         int xpos = pad, colfound = -1, temp;
         boolean found = false;
         for (int i = 0; i < cols; i++) {

            if (!found && cx >= xpos && cx <= xpos + colw) {
               found = true;
               colfound = i;
            }

            xpos += grid;

         }

		 // if it was found, switch the columns
         if (found) {
            temp = order[selectedcol];
            order[selectedcol] = order[colfound];
            order[colfound] = temp;

            //drawBuffer(lastw, lasth);
			 redrawBuffer = true;
			 // we must recalculate the line map when we move columns
			 calcLineMap();
         }

         mousedrag = false;

         repaint();
      }
   }

   /**
		Keep track of where the mouse is when we are dragging or zooming, so
		that we can paint the feedback at the appropriate location.
   */
   public void mouseDragged(MouseEvent e) {

      int cx = e.getX(), cy = e.getY();

      if (mouseband)
         if (cx > 0 && cx < lastw && cy > colw && cy < lasth - colh - colw) {
            mousex = cx; mousey = cy;
            repaint();
            return;
         }
      if (mousedrag)
         if (cx > 0 && cx < lastw) {
            mousex = cx; mousey = cy;
            repaint();
            return;
         }
   }

   public void mouseMoved(MouseEvent e) { }

	/**
		Handle the menu items.
	*/
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		// add or remove a column from the display
		if(src instanceof IdCheckBoxMenuItem) {
			IdCheckBoxMenuItem mi = (IdCheckBoxMenuItem)src;
			// get the id
			int id = mi.id;
			// it is true, add id to the end of the order list
			if(mi.getState()) {
				int[] tmpOrder = new int[order.length+1];
				for(int i = 0; i < order.length; i++)
					tmpOrder[i] = order[i];
				tmpOrder[tmpOrder.length-1] = id;
				order = tmpOrder;
			}
			// it is false, remove id from the order list and shrink it
			else {
				int[] tmpOrder = new int[order.length-1];
				int curIdx = 0;
				for(int i = 0; i < order.length; i++)
					if(order[i] != id) {
						tmpOrder[curIdx] = order[i];
						curIdx++;
					}
				order = tmpOrder;
			}

      		//for (int i = 0; i < order.length; i++)
         	//	calcHeights(order[i]);

			// since the number of columns shown has changed, change the grid size
			grid = (int) ((lastw - 2*pad - colw) / (order.length - 1));

			if (grid < 2*colw)
               grid = 2*colw;

			// since we changed the order, we must recalculate the line map,
	   		calcLineMap();
			// we must redraw the buffer
			redrawBuffer = true;
			repaint();
		}
		// change the color of one of the classes
		else if(src instanceof UniqueMenuItem) {
			UniqueMenuItem mi = (UniqueMenuItem)src;
			// get the id
			int id = mi.id;

			Color oldColor = getColor(id);
			StringBuffer sb = new StringBuffer("Choose ");
			sb.append(mi.getText());
			sb.append(" Color");
			Color newColor = JColorChooser.showDialog(this, sb.toString(), oldColor);
			if(newColor != null) {
				setColor(id, newColor);
				// we must redraw the buffer
				redrawBuffer = true;
				// get rid of the legend since we changed colors
				if(legend != null) {
					legend.setVisible(false);
					legend.dispose();
					legend = null;
				}
				repaint();
			}
		}
		// change the key column
		else if(src instanceof KeyCheckBoxMenuItem) {
			KeyCheckBoxMenuItem mi = (KeyCheckBoxMenuItem)src;
			int id = mi.id;

			if(id != keycol) {
				keycol = id;
				for(int i = 0; i < keys.length; i++) {
					keys[i].setState(keycol == i);
				}
				findUniqueColors();
				redrawBuffer = true;
				// get rid of the legend since we changed colors
				if(legend != null) {
					legend.setVisible(false);
					legend.dispose();
					legend = null;
				}
				repaint();
			}
		}
		// toggle anti-aliasing
		else if(src == useAntiAlias) {
			redrawBuffer = true;
			repaint();
		}
		// show the legend
		else if(src == showLegend) {
			if(legend == null)
				createLegend();
			legend.show();
		}
		// clear the selected lines
		else if(src == clearSelected) {
			boolean found = false;
			for(int i = 0; i < rows; i++) {
				if(selectedlines[i]) {
					found = true;
					removeSelection(i);
				}
				selectedlines[i] = false;
			}
			// only redraw buffer if we actually cleared any lines
			if(found) {
				redrawBuffer = true;
				repaint();
			}
		}
		// show the info about the selected lines
		else if(src == showInfo) {
			if(infoFrame == null)
				createInfoFrame();
			infoFrame.show();
		}
	}


	/**
		Create info frame.  This shows information
		about the currently selected lines in a
		JTable.
	*/
	private void createInfoFrame() {
		infoFrame = new JD2KFrame("Selection Info");
		infoModel = new InfoTableModel();
		infoTable = new JTable(infoModel);
		scrollPane = new JScrollPane(infoTable);
		infoFrame.getContentPane().add(scrollPane);
		infoFrame.pack();
	}

	/**
		Add a line to the selected set.
	*/
	private void addSelection(int row) {
		infoModel.addInfoRow(row);
		//scrollPane.validate();
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
			return vt.getNumColumns();
		}

		public int getRowCount() {
			if(rows == null)
				return 0;
			return rows.size();
		}

		public String getColumnName(int i) {
			return vt.getColumnLabel(i);
		}

		public Object getValueAt(int row, int col) {
			Integer i = (Integer)rows.get(row);
			return vt.getString(i.intValue(), col);
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	/**
		Create the legend, which shows which colors correspond
		to which items.
	*/
	private void createLegend() {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());

		Iterator it = uniqueColorMap.keySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			String s = (String)it.next();
			Integer in = (Integer)uniqueColorMap.get(s);

			Color c = getColor(in.intValue());
			Constrain.setConstraints (pnl, new ColorComponent(c), 0, i, 1, 1,
				GridBagConstraints.NONE, GridBagConstraints.NORTH, 0.0, 0.0);
			Constrain.setConstraints (pnl, new JLabel (s), 1, i, 1, 1,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1.0, 0.0);
			i++;
		}

		legend = new JD2KFrame("Legend");
		legend.getContentPane().add(pnl);
		legend.pack();
	}

	public Dimension getMinimumSize() {
      return new Dimension(400, 400);
   	}

   public Dimension getPreferredSize() {
      return getMinimumSize();
   }

	/**
		Calculates the line(s) that contain point (x3, y3).  If
		a line is found, it will be displayed as red.
	*/
	private void calcLocation(int col, float x3, float y3) {
		float x1 = pad + (int)(.5*colw) + col*grid;
		float x2 = x1 + grid;
		float ypos = lasth - colw - colh;//-y3;

		y3 = -1*y3;

		boolean found = false;

		for(int i = 0; i < rows; i++) {
			float y1 = -1*(ypos-ylocations[order[col]][i]);
			float y2 = -1*(ypos-ylocations[order[col+1]][i]);

			if( ( ( (y1-TOLERANCE) > y3) && ((y2-TOLERANCE) > y3) ) ||
				( ( (y1+TOLERANCE) < y3) && ((y2+TOLERANCE) < y3) ) )
				;
			else {
				float m = (y2-y1)/(x2-x1);
				float yint = (-1*m*x1+y1);

				if(Math.abs( (m*x3)+yint-y3) <= TOLERANCE) {
					found = true;
					if(selectedlines[i])
						removeSelection(i);
					else
						addSelection(i);
					selectedlines[i] = !selectedlines[i];
				}
			}
		}
		// only redraw if one was found
		if(found) {
			redrawBuffer = true;
			repaint();
		}
	}

	}

	/**
		A small square with a black outline.  The color of the background
		is given in the constructor.
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
	}

	}
}
