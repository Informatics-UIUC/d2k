/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */

/**
	RuleVis.java
*/
public class RuleVis extends ncsa.d2k.infrastructure.modules.VisModule
/*#end^2 Continue editing. ^#&*/
{
	static final int BAR_HEIGHT = 56;
	static final Color RULE_VIS_BACKGROUND = new Color (238, 237,237);
	static final Color RULE_VIS_CONFIDENCE = new Color (196, 195, 26);
	static final Color RULE_VIS_SUPPORT = new Color (87, 87, 100);
	static final Color RULE_VIS_HIGHLIGHT = new Color (247, 247, 247);
	static final String IMAGE_LOC =
		"/ncsa/d2k/modules/core/discovery/ruleassociation/";
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
/*&%^3 Do not modify this section. */
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Rules\">    <Text>These rules consist of an array of rules where each rule is some number of antecedants, followed by one target or prediction, the support value, then the confidence value. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"item sets\">    <Text>This structure contains the examples used to generate the itemsets.</Text>  </Info></D2K>";
			default: return "No such input";
		}
/*#end^3 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
/*&%^4 Do not modify this section. */
		String [] types =  {
			"[[I",
			"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets"};
		return types;
/*#end^4 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
/*&%^5 Do not modify this section. */
		switch (index) {
			default: return "No such output";
		}
/*#end^5 Continue editing. ^#&*/
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
/*&%^6 Do not modify this section. */
		String [] types =  {
};
		return types;
/*#end^6 Continue editing. ^#&*/
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
/*&%^7 Do not modify this section. */
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Rule Visualizer\">    <Text>Display rules in a tabular form. </Text>  </Info></D2K>";
/*#end^7 Continue editing. ^#&*/
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
	}
/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/

	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
/*&%^9 Do not modify this section. */
		return new RuleVisView();
/*#end^9 Continue editing. ^#&*/
	}

	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
/*&%^10 Do not modify this section. */
		String[] fieldMap = {};
/*#end^10 Continue editing. ^#&*/
		return fieldMap;
	}
/*&%^11 Do not modify this section. */

/*#end^11 Continue editing. ^#&*/

}

/*&%^12 Do not modify this section. */
/**
	RuleVisView
	This is the UserView class.
*/
class RuleVisView extends ncsa.d2k.controller.userviews.swing.JUserPane implements ActionListener {
/*#end^12 Continue editing. ^#&*/
	ViewModule module;
	/**
		This method adds the components to a Panel and then adds the Panel
		to the view.
	*/
	public void initView(ViewModule mod) {
/*&%^13 Do not modify this section. */
/*#end^13 Continue editing. ^#&*/
		module = mod;
	}

	int [][] rules = null;
	String[] names = null;
	int numExamples;
	int [] nameIndices = null;

	public void endExecution () {
		rules = null;
		names = null;
		nameIndices = null;
	}

	/** construct a table for the row heads.
	*/
	JTable rhjt = null;
	private JViewport getRowHeadTable (int nameIndices [], int imageHeight)
	{
		String [][] rowNames = new String [nameIndices.length][1];
		String [] cn = {"attributes"};
		for (int i = 0; i < nameIndices.length ; i++)
			rowNames  [i][0] = names [nameIndices [i]].replace ('^', '=');
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
		String [][] rowNames = {{"Confidence"}, {"Support"}};
		JViewport jv = new JViewport ();
		String [] cn = {""};
		DefaultTableModel dtm = new DefaultTableModel (rowNames, cn);
		vhjt = new JTable (dtm);
		vhjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
		jv.setBackground (RuleVis.RULE_VIS_BACKGROUND);

		// Set the width of the heads.
		vhjt.createDefaultColumnsFromModel ();
		jv.setView (vhjt);

		// value label cell renderer specifies the height to
		// be that of the value cells
		vhjt.setShowGrid (false);
		vhjt.setRowHeight (RuleVis.BAR_HEIGHT + vhjt.getIntercellSpacing ().height*2);
		vhjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
		return jv;
	}

	/**
		Create a panel which will contain the buttons that sort by the various
		means, confidence and support.
	*/
	JButton conf, sup, unsort;
	private JPanel getSortPanel () {
		JPanel buttons = new JPanel ();
		buttons.setLayout (new GridBagLayout ());
		conf = new JButton ("C");
		sup = new JButton ("S");
		unsort = new JButton ("U");

		this.setConstraints (buttons, new JLabel ("sort by:"), 0, 0, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.WEST, 0, 0);
		this.setConstraints (buttons, conf, 1, 0, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.WEST, 0, 0);
		this.setConstraints (buttons, sup, 2, 0, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.WEST, 0, 0);
		this.setConstraints (buttons, unsort, 3, 0, 1, 1, GridBagConstraints.NONE,
					GridBagConstraints.WEST, 0, 0);

		conf.addActionListener (this);
		sup.addActionListener (this);

		return buttons;
	}

	/**
		A sorting button was clicked, resort by confidence or support.
		@param e the action event.
	*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == conf)
			rdm.sortConfidenceSupport();
		else if (e.getSource() == sup)
			rdm.sortSupportConfidence();
		else
			rdm.unSort();

		rvdm.fireTableRowsUpdated (0, rvdm.getColumnCount () - 1);
		vvdm.fireTableRowsUpdated (0, vvdm.getColumnCount () - 1);
	}

	/**
		Set up the labels. We will find the width of the longest label,
		then we will set the width of each of the labels header rows
		to be the width of the widest one. They line up this way.
		@param names the names of the rows.
		@param nameIndices the indexes of the row names in the order displayed.
	*/
	private void setupRowHeaders (String [] names, int [] nameIndices) {

		// First compute the max width of the guy.
		int width = 0;
		width = 150;

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

	/**
		This method is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.

		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	RuleDataModel rdm;
	ValueVisDataModel vvdm;
	RuleVisDataModel rvdm ;
	public void setInput(Object o, int index) {
		if (index == 0)
			rules = (int [][]) o;
		else
			if (index == 1) {
				ItemSets is = (ItemSets)o;
				names = is.names;
				numExamples = is.numExamples;
			}
			/*
				names = (String[]) o;
			} else
				sets = (String [][]) o;
			*/

		// Do we have all the inputs we need?
		if (rules != null && names != null) {

			this.setLayout (new GridBagLayout ());
			this.setBackground (RuleVis.RULE_VIS_BACKGROUND);

			////////////////////////////////////////////////////////////////////////
			// Create the cell renderers images, then compute the width and height of the
			// cells.
			Image a = module.getImage (RuleVis.IMAGE_LOC+"checkmark-blue.gif");
			Image b = module.getImage (RuleVis.IMAGE_LOC+"box-beige.gif");

			int imgHeight = b.getHeight (this) > a.getHeight(this) ?
					b.getHeight (this) : a.getHeight (this);
			int imgWidth = b.getWidth (this) > a.getWidth (this) ?
					b.getWidth (this) : a.getWidth (this);

			// This is our own data model, it will manage sorting and organizing the data
			rdm = new RuleDataModel (rules);

			////////////////////////////////////////////////////////////////////////
			// Create the table that will go on the top, it will show the confidence
			// support and the row headers.
			vvdm = new ValueVisDataModel (rdm, numExamples);
			final JTable vjt = new JTable (vvdm);
			vjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			vjt.createDefaultColumnsFromModel ();
			JViewport valueRowHeaders = this.getConfSupHeadTable ();

			// set up the scroller for the values, provide it with the row headers.
			JScrollPane valueScroller = new JScrollPane (vjt,
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
			rvdm = new RuleVisDataModel (rdm, names, numExamples);
			nameIndices = rvdm.getNameIndices ();
			JTable rjt = new JTable (rvdm);
			rjt.createDefaultColumnsFromModel ();
			rjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			JViewport rowHeaders = this.getRowHeadTable (nameIndices, imgHeight);

			// Set up the scroller for the rules.
			JScrollPane ruleScroller = new JScrollPane (rjt);
			ruleScroller.setRowHeader (rowHeaders);
			ruleScroller.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			ruleScroller.getViewport().setBackground (RuleVis.RULE_VIS_BACKGROUND);
			ruleScroller.getHorizontalScrollBar ().setUnitIncrement (unitInc);
			ruleScroller.getHorizontalScrollBar ().setBlockIncrement (unitInc*4);
			ruleScroller.setCorner (ScrollPaneConstants.UPPER_LEFT_CORNER, this.getSortPanel ());

			// Set up the viewports and scrollers for the row labels.
			this.setupRowHeaders (names, nameIndices);

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
			lcr = new LabelCellRenderer ();
			col = tcm.getColumn (0);
			col.setCellRenderer (lcr);

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
			dim.height = rjt.getPreferredSize ().height +
				rjt.getTableHeader ().getPreferredSize ().height +
				ruleScroller.getHorizontalScrollBar ().getPreferredSize ().height+
				vjt.getIntercellSpacing ().height*2 +1;
			//ruleScroller.setMinimumSize (dim);
			ruleScroller.setPreferredSize (dim);
			ruleScroller.setMaximumSize (dim);
			ruleScroller.setSize (dim);

			// Now link the conf/supp viewport to the rules viewport
			ruleScroller.getViewport ().addChangeListener (new ScrollLinkageChangeListener(
					ruleScroller.getViewport (), valueScroller.getViewport()));
		}
	}

	/**
		Link the operation of one scroller to another, in this case, the rule
		scroller is the master, and the value scroll is the slave, and only in
		the horizontal direction.
	*/
	class ScrollLinkageChangeListener implements ChangeListener {
		JViewport master, slave;

		/**
			Take the master and slave viewport.
			@param m the master viewport directs scrolling.
			@param s the slave viewport is driven by the scroller.
		*/
		ScrollLinkageChangeListener (JViewport m, JViewport s) {
			master = m;
			slave = s;
		}

		/**
			The master viewport has changed, update the slave.
		*/
		public void stateChanged (ChangeEvent ce) {
			Point pos = master.getViewPosition ();
			pos.y = 0;
			slave.setViewPosition (pos);
		}
	}

	/**
		Create and set up a grid bag constraint with the values given.
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
		 int anchor, double weightX, double weightY)
	{
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

	/**
		Set up the grid bag constraints for the object, and includes insets other than the
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
	/**
		Set up the grid bag constraints for the object.
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

/*&%^14 Do not modify this section. */
/*#end^14 Continue editing. ^#&*/

}

/**
	The data model for rule vis will have an array of bytes representing the number of
	rules along the first dimension, and the number of possible attributes along the
	second dimension. each entry in this matrix will have a 'I' if the associated attribute
	in that rule is an input, and 'O' if it is an objective, anything else means the
	attribute is not represented in the rule.
*/
class RuleVisDataModel extends AbstractTableModel {
	int [] attrShown = null;
	int numsets = 0;

	// Save array lengths for performance reasons
	private int numColumns = 0;
	private int numRows = 0;

	/** contains the original rule data. */
	RuleDataModel rules;

	/**
		Init this guy given the rules. Figure out which items are
		represented in the rule set and with are totally absent. When
		we know what items are represented, we create an secondary item
		index so we may exclude those items are are not represented from
		the display.
	*/
	RuleVisDataModel (RuleDataModel rls, String [] tbl, int numsets) {
		numRows = tbl.length;
		numColumns = rls.getNumRules ();
		rules = rls;
		int counter = 0;
		boolean [] itemRepresented = new boolean [numRows];
		for (int i = 0 ; i < numRows ; i++)
			itemRepresented [i] = false;
		for (int i = 0 ; i < numColumns ; i++) {

			// the last item is the output, skip in first loop
			int size = rls.getSize (i);
			for (int j = 0 ; j < size - 1; j++) {
				int ind = rls.get (i, j);
				if (itemRepresented [ind] == false) {
					itemRepresented [ind] = true;
					counter++;
				}
			}

			// Do the output.
			int outputIndex = rls.get (i, size - 1);
			if (itemRepresented [outputIndex] == false) {
				itemRepresented [outputIndex] = true;
				counter++;
			}
		}

		// Init the list of attributes we will show. We won't display any attributes
		// that were not represented in the rules.
		attrShown = new int [counter];
		for (int i = 0, ac = 0 ; i < itemRepresented.length ; i++)
			if (itemRepresented[i] == true) {
				attrShown [ac++] = i;
			}
		this.numsets = numsets;
		numRows = counter;
	}

	int [] getNameIndices () {
		return attrShown;
	}

	public int getColumnCount() {
		return numColumns;
	}

	public int getRowCount() {
		return numRows;
	}

	/**
		return text that indicates if the cell at row, column is
		an antecedent, a result or not.
	*/
	public Object getValueAt (int row, int column) {
		byte attrType = rules.getAttributeType (column, attrShown [row]);
		if (attrType == (byte)'I')
			return "If";
		else if (attrType == (byte)'O')
			return "Then";
		else
			return "";
	}

	public String getColumnName (int columnIndex) {
		return Integer.toString (columnIndex);
		// return Integer.toString (rules.getRuleIndex (columnIndex));
	}

}


/**
	The data model for rule vis will have an array of bytes representing the number of
	rules along the first dimension, and the number of possible attributes along the
	second dimension. each entry in this matrix will have a 'I' if the associated attribute
	in that rule is an input, and 'O' if it is an objective, anything else means the
	attribute is not represented in the rule.
*/
class ValueVisDataModel extends AbstractTableModel {
	/** this is the data, we use just the last two entries in each row. */
	RuleDataModel results = null;

	/** this is the number of documents in the original dataset, to compute percent */
	int numsets = 0;

	// Save array lengths for performance reasons
	private int numColumns = 0;
	private int numRows = 0;


	/**
		Init this guy given the rules. Figure out which items are
		represented in the rule set and with are totally absent. When
		we know what items are represented, we create an secondary item
		index so we may exclude those items are are not represented from
		the display.
	*/
	ValueVisDataModel (RuleDataModel rls, int numsets) {
		results = rls;
		numRows = 2;
		numColumns = rls.getNumRules ();
		this.numsets = numsets;
	}

	/**
		return the number of columns.
		@returns the number of columns.
	*/
	public int getColumnCount() {
		return numColumns;
	}

	/**
		return the number of rows.
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

		// Is it a header? We have to share the same data model for the
		// scroller to work right.
		double percent = 0;
		if (row == 0) {
			percent = results.getConfidence (column);
			percent /= 10000.0;
		} else {
			percent = results.getSupport (column);
			percent = (percent * 100) / this.numsets;
		}
		return new Float (percent);
	}
}
