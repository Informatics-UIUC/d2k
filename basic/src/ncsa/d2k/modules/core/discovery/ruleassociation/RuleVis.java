package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;
import ncsa.gui.Constrain;

/**
	RuleVis.java
*/
public class RuleVis extends ncsa.d2k.core.modules.VisModule
{
	static final int BAR_HEIGHT = 56;
	static final Color RULE_VIS_BACKGROUND = new Color (238, 237,237);
	static final Color RULE_VIS_CONFIDENCE = new Color (196, 195, 26);
	static final Color RULE_VIS_SUPPORT = new Color (87, 87, 100);
	static final Color RULE_VIS_HIGHLIGHT = new Color (247, 247, 247);

	/**	This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Rule Table";
			default:
				return "No such input";
		}
	}
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
		    case 0:
			return "A representation of association rules to be displayed. ";
		    default:
			return "No such input";
		}
	}

	/**	This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		return null;
        }

	/**	This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Rule Visualization";
	}

	/**	This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
	  StringBuffer sb = new StringBuffer( "<p>Overview: ");
          sb.append( "This module provides a visual representation of the association rules encapsulated in the ");
          sb.append("input <i>Rule Table<i>. ");

          sb.append("</p><p>Detailed Description: ");
          sb.append("This module presents a visual represention of association rules identified by ");
          sb.append("a discovery algorithm. ");
          sb.append("D2K includes several modules that implement association rule discovery algorithms, ");
          sb.append("all of which save their results in a <i>Rule Table</i> structure that can be used as ");
          sb.append("input to this module. ");

          sb.append("</p><p> ");
          sb.append("The main region of the display contains is a matrix that visually represents the rules. ");
          sb.append("Each numbered column in the matrix corresponds to an association rule  ");
          sb.append("that met the minimum support and confidence requirements specified by the user in the ");
          sb.append("rule discovery module(s).    ");
          sb.append("Items used in the rules, that is [attribute=value] pairs, are listed along the left  ");
          sb.append("side of the matrix. ");
          sb.append("Note that some items in the original data set may not be included in any rule ");
          sb.append("because there was insufficient support and/or confidence to consider the item ");
          sb.append("significant. ");

          sb.append("</p><p> ");
          sb.append("An icon in the matrix cell corresponding to ( row = <i>item i</i>, column = <i>rule r</i>) ");
          sb.append("indicates that <i>item i</i> is included in <i>rule r</i>. ");
          sb.append("If the matrix cell icon is a box, the item is part of the rule antecedent.  If ");
          sb.append("the icon is a check mark, the item is part of the rule consequent. ");
          sb.append("For example, if the rules being displayed indicate whether or not a mushroom is edible, ");
          sb.append("a rule might be &quot;odor=none&quot; and &quot;ring_number=one&quot; then &quot;edibility=edible&quot;. ");
          sb.append("This rule would be displayed in a column with a box in the row for the item &quot;odor=none&quot; ");
          sb.append("and a box in the row for &quot;ring_number=one&quot;, and there would be a check in the ");
          sb.append("row for &quot;edibility=edible&quot;. ");

          sb.append("</p><p> ");
          sb.append("Above the main matrix are two rows of bars labelled <i>Confidence</i> and <i>Support</i>. ");
          sb.append("These bars align with, and correspond to, the rule columns in the main matrix.  For any given rule, ");
          sb.append("the confidence and support values for that rule are represented by the degree to which the ");
          sb.append("bars above the rule column are filled in.   Brushing the mouse on a confidence or support ");
          sb.append("bar displays the exact value that is graphically represented by the bar height. ");

          sb.append("</p><p>");
          sb.append("The rules can be ordered by confidence or by support. ");
          sb.append("To sort the rules, click either the support or the confidence label -- ");
          sb.append("these labels are clickable buttons. ");
          sb.append("If support is selected, rules will be sorted using support as the primary key and confidence as the secondary key. ");
          sb.append("Conversely, if the confidence button is chosen, confidence is the primary sort key and support is the secondary key.  ");

          sb.append("</p><p> ");
          sb.append("The options menu allows the user to print a screen capture of the module display. ");
	  sb.append("The print output contains only the cells that are visible in the display window, not all the cells ");
	  sb.append("in the rule table.  The user can scroll to different part of the matrix and print multiple times  ");
	  sb.append("to get the full picture of large matrices. ");

          sb.append("</p><p>Scalability: ");
          sb.append("While this module can display a large number of items and rules, there can be a noticable delay " );
	  sb.append("in opening the visualization when a large number of cells are involved. " );
          sb.append("Also, as the number of cells increases beyond ");
          sb.append("a certain point, it is difficult to gain insights from the display.  Advanced features to help ");
          sb.append("in these cases are being discussed for a future release. ");

	  return sb.toString();
	}


        public PropertyDescription[] getPropertiesDescriptions() {
		// hide properties that the user shouldn't udpate
       		return new PropertyDescription[0];
   	}


	/**	This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new RuleVisView();
	}

	/**	This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		String[] fieldMap = {};
		return fieldMap;
	}

	/**	RuleVisView
		This is the UserView class.
	*/
	private class RuleVisView extends ncsa.d2k.userviews.swing.JUserPane implements ActionListener, Printable {
		java.util.List itemLabels = null;
		RuleTable ruleTable;
		int numExamples;
		ValueVisDataModel vvdm;
		RuleVisDataModel rvdm;
		JMenuItem print;
		JMenuItem pmml;
		JMenuBar menuBar;

		/** this identifies the order the rules are displayed in, also what rules are
		 * displayed. */
		int [] order = null;


		/**	This method adds the components to a Panel and then adds the Panel
			to the view.
		*/
		public void initView(ViewModule mod) {
			//module = mod;
			menuBar = new JMenuBar();
			JMenu options = new JMenu("Options");
			print = new JMenuItem("Print...");
			print.addActionListener(this);
			options.add(print);

			// Commented out for Basic as PMML not working right.  - Ruth
			// pmml = new JMenuItem("Save as PMML..");
			// pmml.addActionListener(this);
			// options.add(pmml);

			menuBar.add(options);
		}

		public void endExecution () {
			itemLabels = null;
			 ruleTable = null;
		}

		public Object getMenu() {
			return menuBar;
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


		/** construct a table for the row heads.
		*/
		JTable rhjt = null;
		private JViewport getRowHeadTable (int imageHeight)
		{
			String [][] rowNames = new String [itemLabels.size()][1];
			String [] cn = {"attributes"};
			for (int i = 0; i < itemLabels.size(); i++)
				rowNames  [i][0] = ((String)itemLabels.get(i));
			JViewport jv = new JViewport ();
			DefaultTableModel dtm = new DefaultTableModel (rowNames, cn);
			rhjt = new JTable (dtm);
			rhjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			jv.setBackground (RuleVis.RULE_VIS_BACKGROUND);

			// Set the width of the heads.
			rhjt.createDefaultColumnsFromModel ();
			jv.setView (rhjt);

			// value label cell renderer specifies the height to
			// be that of the value cells
			rhjt.setRowHeight (imageHeight + rhjt.getIntercellSpacing ().height*2);
			rhjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
			return jv;
		}

		/** construct a table for the row heads.
		*/
		JTable vhjt = null;
		private JViewport getConfSupHeadTable ()
		{
			String [][] rowNames = new String[ruleTable.getNumColumns()-2][1];
			for(int i=2;i<ruleTable.getNumColumns();i++)
				rowNames[i-2][0]=ruleTable.getColumnLabel(i);
			JViewport jv = new JViewport();
			String [] cn = {""};
			DefaultTableModel dtm = new DefaultTableModel (rowNames, cn);
			vhjt = new JTable (dtm);
			vhjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			jv.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			vhjt.createDefaultColumnsFromModel ();

			// set the view.
			jv.setView (vhjt);

			// value label cell renderer specifies the height to
			// be that of the value cells
			vhjt.setShowGrid (false);
			vhjt.setRowHeight (RuleVis.BAR_HEIGHT + vhjt.getIntercellSpacing ().height*2);
			vhjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
			return jv;
		}

		/**	Create a panel which will contain the buttons that sort by the various
			means, confidence and support.
		*/
		JButton conf = new JButton("Confidence");
		JButton  sup = new JButton("Support");
		final int BSIZE = 13;
		private JPanel getSortPanel () {
			JPanel buttons = new JPanel ();
			return buttons;
		}

		/**	A sorting button was clicked, resort by confidence or support.
			@param e the action event.
		*/
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == conf){
				//ruleTable.sortByConfidence();
				this.rvdm.sortConfidenceSupport();
				this.repaint();
			}
			else if (e.getSource() == sup){
				//ruleTable.sortBySupport();
				this.rvdm.sortSupportConfidence();
				this.repaint();
			}
			else if (e.getSource() == print) {
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
			else if(e.getSource() == pmml) {
			JFileChooser fileChooser = new JFileChooser();
			int retVal = fileChooser.showSaveDialog(null);
			if(retVal == fileChooser.APPROVE_OPTION) {
				java.io.File file = fileChooser.getSelectedFile();
				//TODO add back pmml
				//WriteRuleAssocPMML.writePMML(ruleTable, file.getAbsolutePath());
			}

			}

			rvdm.fireTableDataChanged();
			vvdm.fireTableDataChanged();
		}

		/**	Set up the labels. We will find the width of the longest label,
			then we will set the width of each of the labels header rows
			to be the width of the widest one. They line up this way.
			@param names the names of the rows.
			@param nameIndices the indexes of the row names in the order displayed.
		*/
		private void setupRowHeaders (java.util.List names) {

			// First compute the max width of the guy.
			int width = 150;

			// Set the widths to the max.
			TableColumnModel tcm = vhjt.getColumnModel();
			TableColumn col = tcm.getColumn (0);
			col.setMinWidth (width);
			col.setMaxWidth (width);
			col.setPreferredWidth (width);

			tcm = rhjt.getColumnModel();
			col = tcm.getColumn (0);
			col.setMinWidth (width);
			col.setMaxWidth (width);
			col.setPreferredWidth (width);

			Dimension dim = new Dimension (width, vhjt.getPreferredSize ().height);
			vhjt.setPreferredScrollableViewportSize (dim);
			dim.height = vhjt.getPreferredSize ().height;
			rhjt.setPreferredScrollableViewportSize (dim);
		}

		/**	This method is called whenever an input arrives, and is responsible
			for modifying the contents of any gui components that should reflect
			the value of the input.

			@param input this is the object that has been input.
			@param index the index of the input that has been received.
		*/
		public void setInput(Object o, int index) {
			if (index == 0)
				ruleTable = (RuleTable)o;
                                // Added call to cleanup() to remove items
                                // from the RuleTable that are not used in any rules
                                ruleTable.cleanup();
				itemLabels = ruleTable.getNamesList();
				// Do we have all the inputs we need?
				if ((ruleTable != null) /*&& (itemLabels != null)*/){
				this.setLayout (new GridBagLayout ());
				this.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				////////////////////////////////////////////////////////////////////////
				// Create the cell renderers images, then compute the width and height of the
				// cells.
				Image a = /*module.*/getImage ("/images/rulevis/checkmark-blue.gif");
				Image b = /*module.*/getImage ("/images/rulevis/box-beige.gif");
				conf.addActionListener (this);
				sup.addActionListener (this);

				int imgHeight = b.getHeight (this) > a.getHeight(this) ?
						b.getHeight (this) : a.getHeight (this);
				int imgWidth = b.getWidth (this) > a.getWidth (this) ?
						b.getWidth (this) : a.getWidth (this);

				////////////////////////////////////////////////////////////////////////
				// Create the table that will go on the top, it will show the confidence
				// support and the row headers.
				vvdm = new ValueVisDataModel (ruleTable, numExamples);
				final JTable vjt = new JTable (vvdm);
				vjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				vjt.createDefaultColumnsFromModel ();
				JViewport valueRowHeaders = this.getConfSupHeadTable ();

				// set up the scroller for the values, provide it with the row headers.
				JPanel tmp = new JPanel();
				tmp.setOpaque(false);
				tmp.setLayout(new GridBagLayout());
				Constrain.setConstraints(tmp, vjt, 0, 0, 1, 1, GridBagConstraints.NONE,
						GridBagConstraints.WEST, 0.0, 0.0, new Insets(0, 0, 0, 0));
				JPanel filler = new JPanel();
				Dimension fillSize = new Dimension(12, 12);
				filler.setMinimumSize(fillSize);
				filler.setPreferredSize(fillSize);
				filler.setOpaque(false);
				Constrain.setConstraints(tmp, filler, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.CENTER, 1.0, 0.0, new Insets(0, 0, 0, 0));

				JScrollPane valueScroller = new JScrollPane (tmp,
						JScrollPane.VERTICAL_SCROLLBAR_NEVER,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				int unitInc = imgWidth - vjt.getIntercellSpacing ().width*2 - 1;
				valueScroller.setRowHeader (valueRowHeaders);
				valueScroller.getHorizontalScrollBar ().setUnitIncrement (unitInc);
				valueScroller.getHorizontalScrollBar ().setBlockIncrement (unitInc*4);

				///////////////////////////////////////////////////////////////////////
				// next construct the table of the rules themselves, this table
				// will also have row headers in a different table, the scroller
				// understands and deals with the concept of table row headers.
				rvdm = new RuleVisDataModel (ruleTable, itemLabels, numExamples);
				JTable rjt = new JTable (rvdm);
				rjt.createDefaultColumnsFromModel ();
				rjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				JViewport rowHeaders = this.getRowHeadTable (imgHeight);

				// Set up the scroller for the rules.
				JScrollPane ruleScroller = new JScrollPane (rjt);
				ruleScroller.setRowHeader (rowHeaders);
				ruleScroller.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				ruleScroller.getViewport().setBackground (RuleVis.RULE_VIS_BACKGROUND);
				ruleScroller.getHorizontalScrollBar ().setUnitIncrement (unitInc);
				ruleScroller.getHorizontalScrollBar ().setBlockIncrement (unitInc*4);
				ruleScroller.setCorner (ScrollPaneConstants.UPPER_LEFT_CORNER, this.getSortPanel ());

				// Set up the viewports and scrollers for the row labels.
				this.setupRowHeaders (ruleTable.getNamesList());

				// Set up a table model listener so when rows move in the rules, rows also
				// move appropriately in the confidences.
				rjt.getColumnModel ().addColumnModelListener (new TableColumnModelListener () {
					public void columnAdded(TableColumnModelEvent e) {}
					public void columnRemoved(TableColumnModelEvent e) {}
					public void columnMarginChanged(ChangeEvent e) {}
					public void columnSelectionChanged(ListSelectionEvent e) {}
					public void columnMoved(TableColumnModelEvent e) {
						if (e.getFromIndex () != e.getToIndex ()) {
							vjt.getColumnModel ().moveColumn (e.getFromIndex (), e.getToIndex ());
						}
								}
				});

				///////////////////////////////////////////////////////////////
				// Assign the appropriate cell renderers for all the tables, the
				// labels all share one renderer, the rules have their own as do
				// the values.

				// Set the cell renderer for the rules
				TableColumnModel tcm = rjt.getColumnModel();
				RuleCellRenderer rcr = new RuleCellRenderer (
					imgHeight + rjt.getIntercellSpacing ().height*2,
					imgWidth + rjt.getIntercellSpacing ().width*2,
					a, b);
				for (int i = 0; i < rvdm.getColumnCount (); i++) {
					TableColumn col = tcm.getColumn (i);
					col.setCellRenderer (rcr);
					col.setMinWidth (30);
					col.setMaxWidth (30);
					col.setPreferredWidth (30);
				}
				rjt.setRowHeight (imgHeight + rjt.getIntercellSpacing ().height*2);
				rjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

				// Set the cell renderer for the values
				tcm = vjt.getColumnModel();
				ValueCellRenderer vcr = new ValueCellRenderer (
					imgWidth + vjt.getIntercellSpacing ().height*2);
				for (int i = 0; i < vvdm.getColumnCount (); i++) {
					TableColumn col = tcm.getColumn (i);
					col.setCellRenderer (vcr);
					col.setMinWidth (30);
					col.setMaxWidth (30);
					col.setPreferredWidth (30);
				}
				vjt.setRowHeight (RuleVis.BAR_HEIGHT + vjt.getIntercellSpacing ().height*2);
				vjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

				// Set the cell renderer for the rule labels
				tcm = rhjt.getColumnModel();
				LabelCellRenderer lcr = new LabelCellRenderer ();
				TableColumn col = tcm.getColumn (0);
				lcr.setBackground (Color.white);
				col.setCellRenderer (lcr);

				// Same renderer for the conf/support labels
				tcm = vhjt.getColumnModel();
				SortButtonCellRenderer sortButtonRenderer = new SortButtonCellRenderer(conf, sup);
				col = tcm.getColumn (0);
				col.setCellRenderer (sortButtonRenderer);
				SortButtonCellEditor sortButtonEditor = new SortButtonCellEditor(conf, sup);
				col.setCellEditor (sortButtonEditor);

				// Put them into a frame, then place the frame in this view with an inset.
				JPanel jp = new JPanel ();
				jp.setLayout (new GridBagLayout ());
				this.setConstraints (jp, valueScroller, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.WEST, 1, 0, new Insets (0, 0, 0, 0));
				this.setConstraints (jp, ruleScroller, 0, 1, 1, 1, GridBagConstraints.BOTH,
						GridBagConstraints.WEST, 1, 1, new Insets (0, 0, 0, 0));
				this.setConstraints (this, jp, 0, 0, 1, 1, GridBagConstraints.BOTH,
						GridBagConstraints.WEST, 1, 1, new Insets (10, 10,10, 10));

				// Now fix up the sizes of the scroll panels considering the
				// usage of scroll bars. The value scroller never has a scroll bar
				// yet it's preferred size includes the hieght of one.
				Dimension dim = valueScroller.getMinimumSize ();
				dim.height = vjt.getPreferredSize ().height+2;
				valueScroller.setMinimumSize (dim);
				valueScroller.setPreferredSize (dim);
				valueScroller.setMaximumSize (dim);

				dim = ruleScroller.getPreferredSize ();
				/*dim.height = rjt.getPreferredSize ().height +
					rjt.getTableHeader ().getPreferredSize ().height +
					ruleScroller.getHorizontalScrollBar ().getPreferredSize ().height+
					vjt.getIntercellSpacing ().height*2 +1;
				ruleScroller.setPreferredSize (dim);
				ruleScroller.setMaximumSize (dim);*/
				ruleScroller.setSize (dim);

				// Now link the conf/supp viewport to the rules viewport
				ruleScroller.getViewport ().addChangeListener (new ScrollLinkageChangeListener(
						ruleScroller.getViewport (), valueScroller.getViewport()));
			}
		}

		/**	Link the operation of one scroller to another, in this case, the rule
			scroller is the master, and the value scroll is the slave, and only in
			the horizontal direction.
		*/
		class ScrollLinkageChangeListener implements ChangeListener {
			JViewport master, slave;

			/**	Take the master and slave viewport.
				@param m the master viewport directs scrolling.
				@param s the slave viewport is driven by the scroller.
			*/
			ScrollLinkageChangeListener (JViewport m, JViewport s) {
				master = m;
				slave = s;
			}

			/**	The master viewport has changed, update the slave.
			*/
			public void stateChanged (ChangeEvent ce) {
				Point pos = master.getViewPosition ();
				pos.y = 0;
				slave.setViewPosition (pos);
			}
		}

		/**	Create and set up a grid bag constraint with the values given.
			@param x the x corrdinate in the layout grid.
			@param y the y coordinate.
			@param width the number of rows the object spans.
			@param height the number of cols the object spans.
			@param fill determines how the objects size is computed.
			@param anchor how the object is aligned within the cell.
			@param weightX fraction of the leftover horizontal space the object will use.
			@param weightY fraction of the leftover vertical space the object will use.

		*/
		private GridBagConstraints getConstraints (int x, int y, int width, int height, int fill,
			 int anchor, double weightX, double weightY){
			GridBagConstraints c = new GridBagConstraints ();
			c.gridx 	= x;
			c.gridy 	= y;
			c.gridwidth = width;
			c.gridheight = height;
			c.fill 		= fill;
			c.anchor 	= anchor;
			c.weightx	= weightX;
			c.weighty 	= weightY;
			return c;
		}

		/**	Set up the grid bag constraints for the object, and includes insets other than the
			default.
			@param it the component being added.
			@param x the x corrdinate in the layout grid.
			@param y the y coordinate.
			@param width the number of rows the object spans.
			@param height the number of cols the object spans.
			@param fill determines how the objects size is computed.
			@param anchor how the object is aligned within the cell.
			@param weightX fraction of the leftover horizontal space the object will use.
			@param weightY fraction of the leftover vertical space the object will use.
		*/
		public void setConstraints (Container cont, Component it, int x, int y, int width, int height, int fill,
			 int anchor, double weightX, double weightY, Insets insets)
		{
			GridBagConstraints c = this.getConstraints (x, y, width, height, fill, anchor, weightX, weightY);
			c.insets 	= insets;
			((GridBagLayout) cont.getLayout()).setConstraints (it, c);
			cont.add (it);
		}
		/**	Set up the grid bag constraints for the object.
			@param it the component being added.
			@param x the x corrdinate in the layout grid.
			@param y the y coordinate.
			@param width the number of rows the object spans.
			@param height the number of cols the object spans.
			@param fill determines how the objects size is computed.
			@param anchor how the object is aligned within the cell.
			@param weightX fraction of the leftover horizontal space the object will use.
			@param weightY fraction of the leftover vertical space the object will use.
		*/
		public void setConstraints (Container cont, Component it, int x, int y,
				int width, int height, int fill,
			 int anchor, double weightX, double weightY)
		{
			GridBagConstraints c = this.getConstraints (x, y, width, height, fill, anchor, weightX, weightY);
			c.insets = new Insets (2, 2, 2, 2);
			((GridBagLayout) cont.getLayout()).setConstraints (it, c);
			cont.add (it);
		}

		/**	The data model for rule vis will have an array of bytes representing the number of
			rules along the first dimension, and the number of possible attributes along the
			second dimension. each entry in this matrix will have a 'I' if the associated attribute
			in that rule is an input, and 'O' if it is an objective, anything else means the
			attribute is not represented in the rule.
		*/
		class RuleVisDataModel extends AbstractTableModel {
			int numsets = 0;

			// Save array lengths for performance reasons
			private int numColumns = 0;
			private int numRows = 0;

			/** contains the original rule data. */
			RuleTable rules;

			/** this is set if we are sorting on confidence as the primary. */
			boolean sortOnConfidence = true;

			/**	Init this guy given the rules. Figure out which items are
				represented in the rule set and with are totally absent. When
				we know what items are represented, we create an secondary item
				index so we may exclude those items are are not represented from
				the display.
			*/
			RuleVisDataModel (RuleTable rls, java.util.List tbl, int numsets) {
				rules = rls;
				numRows = tbl.size();
				numColumns = rules.getNumRows();
						this.numsets = numsets;

				// I added this so we could do a dual key sort.
				this.unSort ();
			}

			public int getColumnCount() {
				return numColumns;
			}

			public int getRowCount() {
				return numRows;
			}

			/**	return text that indicates if the cell at row, column is
				an antecedent, a result or not.
			*/
			public Object getValueAt (int row, int column) {
				String returnval = "";
				int[] head = rules.getRuleAntecedent(order[column]);
				for (int i=0; i<head.length; i++)
					if (head[i] == row)
				returnval = IF;
				int[] body = rules.getRuleConsequent(order[column]);
				for (int i=0; i<body.length; i++)
					if (body[i] == row)
						returnval = THEN;
				return returnval;
			}

			public String getColumnName (int columnIndex) {
				return Integer.toString (columnIndex);
			}

			/**
				Compare two rules on the basis of a primary key first and then a secondary
				key. Primary and secondary keys can be either of support or confidence.
				@param l the first entry to compare
				@param r the second entry.
			*/
			public int compare (int l) {
				double primary1, secondary1;
				if (sortOnConfidence) {
					primary1 = rules.getConfidence(order[l]);
					secondary1 = rules.getSupport(order[l]);
				} else {
					secondary1 = rules.getConfidence(order[l]);
					primary1 = rules.getSupport(order[l]);
				}

				// First check on the primary keys
				if (primary1 > bestPrimary)
					return 1;
				if (primary1 < bestPrimary)
					return -1;

				// Primarys are equal, check the secondary keys.
				if (secondary1 > bestSecondary)
					return 1;
				if (secondary1 < bestSecondary)
					return -1;
				return 0;
			}

			/**
				Swap two entries int he order array.
				@param l the first entry.
				@param r the second entry.
			*/
			public void swap (int l, int r) {
				int swap = order [l];
				order [l] = order [r];
				order [r] = swap;
			}

			/**
				Set the pivot value for the quicksort.
			*/
			double bestPrimary;
			double bestSecondary;
			private void setPivot (int l) {
				if (sortOnConfidence) {
					this.bestPrimary = rules.getConfidence(order[l]);
					this.bestSecondary = rules.getSupport(order[l]);
				} else {
					this.bestSecondary = rules.getConfidence(order[l]);
					this.bestPrimary = rules.getSupport(order[l]);
				}
			}

			/**
				Perform a quicksort on the data using the Tri-median method to select the pivot.
				@param l the first rule.
				@param r the last rule.
			*/
			private void quickSort(int l, int r) {

				int pivot = (r + l) / 2;
				this.setPivot (pivot);

				// from position i=l+1 start moving to the right, from j=r-2 start moving
				// to the left, and swap when the fitness of i is more than the pivot
				// and j's fitness is less than the pivot
				int i = l;
				int j = r;
				while (i <= j) {
					while ((i < r) && (this.compare (i) > 0))
						i++;
					while ((j > l) && (this.compare (j) < 0))
						j--;
					if (i <= j) {
						this.swap (i, j);
						i++;
						j--;
					}
				}

				// sort the two halves
				if (l < j)
					quickSort(l, j);
				if (i < r)
					quickSort(j+1, r);
			}

			/**
				Bubble sort on confidence as primary key.
			*/
			private void unSort () {
				int numRules = rules.getNumRules();

				// Create and init a new handle array.
				order = new int [numRules];
				for (int i = 0 ; i < numRules ; i++)
					order[i] = i;
			}

			/**
				Bubble sort on confidence as primary key.
			*/
			public void sortConfidenceSupport () {
				sortOnConfidence = true;
				int numRules = rules.getNumRules();
				this.unSort();
				this.quickSort (0, numRules-1);
			}

			/**
				Bubble sort on support as primary key.
			*/
			public void sortSupportConfidence () {
				sortOnConfidence = false;
				int numRules = rules.getNumRules();
				this.unSort();
				this.quickSort (0, numRules-1);
			}

			/**
				The order we originally get the rules in is sorted on the basis
				of the size of the rules.
			*/
			public void sortOnSize () {
				this.unSort();
			}

		}

		/**	The data model for rule vis will have an array of bytes representing the number of
			rules along the first dimension, and the number of possible attributes along the
			second dimension. each entry in this matrix will have a 'I' if the associated attribute
			in that rule is an input, and 'O' if it is an objective, anything else means the
			attribute is not represented in the rule.
		*/
		class ValueVisDataModel extends AbstractTableModel {
			/** this is the data, we use just the last two entries in each row. */
			//TableImpl results = null;
			RuleTable results = null;

			/** this is the number of documents in the original dataset, to compute percent */
			int numsets = 0;

			// Save array lengths for performance reasons
			private int numColumns = 0;
			private int numRows = 0;

			/**	Init this guy given the rules. Figure out which items are
				represented in the rule set and with are totally absent. When
				we know what items are represented, we create an secondary item
				index so we may exclude those items are are not represented from
				the display.
			*/
			ValueVisDataModel (RuleTable rls, int numsets) {
				results = rls;
				//First 2 columns of rls represent body and head of a rule
				numRows = rls.getNumColumns() - 2;
				numColumns = rls.getNumRows();
				this.numsets = numsets;
			}

			/**	return the number of columns.
				@returns the number of columns.
			*/
			public int getColumnCount() {
				return numColumns;
			}

			/**	return the number of rows.
				@returns the number of rows.
			*/
			public int getRowCount() {
			return numRows;
			}

			public String getColumnName (int columnIndex) {
				return "";
			}

			/**
				Compute the value at row and column
				@returns the value at row and column.
			*/
			public Object getValueAt (int row, int column) {
				float percent = 0;
				if(row == 0)
					percent = (float)results.getConfidence(order[column]);
				else
					percent = (float)results.getSupport(order[column]);
				return new Float(percent);
			}

		}
	}

	private static final String HEAD = "Head";
	private static final String BODY = "Body";
	private static final String IF = "If";
	private static final String THEN = "Then";
}

// Start QA Comments
//
// 3/*/03  - Ruth did QA and Tom did updates to the module.
// 3/28/03 - Ready for basic.
//         - WISH:  Group items so attributes show up next to each other (perhaps
//                  done earlier in module sequence prior to building items or rule table.
//         - WISH:  Allow sorting by items that have included in most rules.
//         - WISH:  Show rule consequent items at top of item list.
//         - WISH:  Put back option to save as PMML when that is working.
//         - WISH:  Offer way to print entire matrix, not just viewable area.
// 4/5/03  - Updated Info a bit after Loretta changed so items that aren't in any rules
//           are no longer displayed.  Also, removed that from the Wish List. - ruth
// End QA Comments.
