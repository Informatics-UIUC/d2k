package ncsa.d2k.modules.core.transform.attribute;


import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.gui.*;

/**
   DefineBins presents a GUI to allow the user to enter classifications for
   a data set.
   @author David Clutter
*/
public class DefineBins extends UIModule  {

	protected static final String NUMERIC = "num";
	protected static final String TEXT = "text";

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Allows the user to bin data.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "DefineBins";
	}

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.BinTree","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "ExampleTable with data in it.";
			default: return "No such input";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		switch(i) {
			case 0:
				return "Table";
			default: return "NO SUCH INPUT!";
		}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "Contains binning info.";
			case 1: return "Table, unchanged.";
			default: return "No such output";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "BinTree";
			case 1:
				return "Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public UserView createUserView() {
		return new BinView();
	}

	public String []getFieldNameMapping() {
		return null;
	}

	/**
		The complex user view.
	*/
	class BinView extends JUserPane implements ActionListener {
		/** general */
		private DefineBins parent;
		BinTree binTree;
		private JButton abort;
		private JButton done;

		/** numeric stuff */
		JList numericAttributes;
		JTextField uniformRange;
		JTextField userSpecifiedNumeric;
		JTextField userSpecifiedRange;
		JButton addUserSpecifiedRange;

		private JTextField numericName;
		private JButton addNumeric;
		private JButton addNumericUniform;
		private JButton autoNumeric;

		/** text stuff */
		JList textAttributes;
		private JTextField userSpecifiedText;
		private JTextField textName;
		private JButton addText;
		private JButton autoText;

		/** general */
		private JList binList;
		DefaultListModel binListModel;
		HashMap binEntries;
		private JButton remove;

		private HelpWindow helpWindow;
		private JButton help;

		/** inputs */
		ExampleTable table;

		/** The class and attribute names, derived from the table. */
		String []classNames;
		String []attributeNames;

        protected HashMap indexLookup;

		public void setInput(Object o, int i) {
			if(i == 0) {
				table = (ExampleTable)o;
				initData();
			}
		}

		public Dimension getPreferredSize() {
			return new Dimension (700, 500);
		}

		public void initView(ViewModule vm) {
			parent = (DefineBins)vm;
            nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);

			numericAttributes = new JList();
			numericAttributes.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ListSelectionModel numericLSM = new DefaultListSelectionModel();
			numericAttributes.setSelectionModel(numericLSM);

			textAttributes = new JList();
			textAttributes.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ListSelectionModel textLSM = new DefaultListSelectionModel();
			textAttributes.setSelectionModel(textLSM);

			binList = new JList();
			binList.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ListSelectionModel binLSM = new DefaultListSelectionModel();
			binList.setSelectionModel(binLSM);
			binList.setModel((binListModel = new DefaultListModel()));

			JPanel buttonArea = new JPanel();
			abort = new JButton("Abort");
			abort.addActionListener(this);
			done = new JButton("Done");
			done.addActionListener(this);
			help = new JButton("Help");
			help.addActionListener(this);
			buttonArea.add(help);
			buttonArea.add(abort);
			buttonArea.add(done);

			helpWindow =new HelpWindow();

			// layout the numeric panel
			JPanel numeric = new JPanel();
			numeric.setLayout(new GridBagLayout());
			JScrollPane scrollNumeric = new JScrollPane(numericAttributes);
			JLabel inLabel = new JLabel("Scalar Inputs");
			//scrollNumeric.setColumnHeaderView(inLabel);

			// an outline panel for uniform range
			JOutlinePanel uni = new JOutlinePanel("Uniform Range");
			uni.setLayout(new GridBagLayout());
			Constrain.setConstraints(uni, new JLabel("Number of Bins"),
				0, 0, 3, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 3, 1);
			Constrain.setConstraints(uni, (uniformRange = new JTextField(10)),
				3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 1, 1);
			Constrain.setConstraints(uni, (addNumericUniform = new JButton("Add")),
				4, 0, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.EAST, 1, 1);

			// an outline panel for user specified
			JOutlinePanel spec = new JOutlinePanel("User Specified");
			spec.setLayout(new GridBagLayout());
			Constrain.setConstraints(spec, new JLabel("Conditions"),
				0, 0, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.WEST, 1, 1);
			Constrain.setConstraints(spec, (userSpecifiedNumeric = new JTextField(10)),
				1, 0, 3, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 3, 1);
			Constrain.setConstraints(spec, new JLabel("Name"),
				0, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.WEST, 1, 1);
			Constrain.setConstraints(spec, (numericName = new JTextField(10)),
				1, 1, 3, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 3, 1);
			Constrain.setConstraints(spec, (addNumeric = new JButton("Add")),
				1, 2, 2, 1, GridBagConstraints.NONE,
				GridBagConstraints.EAST, 2, 1);

			JOutlinePanel specRange = new JOutlinePanel("User Specified Range");
			specRange.setLayout(new GridBagLayout());
			Constrain.setConstraints(specRange, new JLabel("Range"),
				0, 0, 3, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 3, 1);
			Constrain.setConstraints(specRange, (userSpecifiedRange = new JTextField(10)),
				3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 1, 1);
			Constrain.setConstraints(specRange, (addUserSpecifiedRange = new JButton("Add")),
				4, 0, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.EAST, 1, 1);
			addUserSpecifiedRange.addActionListener(this);

			JPanel p1 = new JPanel();
			p1.setLayout(new GridBagLayout());
			Constrain.setConstraints(p1, uni, 0, 0, 1, 1,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				1, 1);
			Constrain.setConstraints(p1, specRange, 0, 1, 1, 1,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				1, 1);
			Constrain.setConstraints(p1, spec, 0, 2, 1, 1,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				1, 1);

			// now add everything to numeric
			numeric.setLayout(new BorderLayout());
			// the scroller goes in the center
			numeric.add(scrollNumeric, BorderLayout.CENTER);
			// everything else goes in the south
			numeric.add(p1, BorderLayout.SOUTH);

			JPanel str = new JPanel();
			str.setLayout(new GridBagLayout());
			JScrollPane textScroll = new JScrollPane(textAttributes);
			JLabel textLabel = new JLabel("Nominal Inputs");
			//textScroll.setColumnHeaderView(textLabel);

			JOutlinePanel usp = new JOutlinePanel("User Specified");
			usp.setLayout(new GridBagLayout());
			Constrain.setConstraints(usp, new JLabel("Condition"),
				0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
				1, 1);
			Constrain.setConstraints(usp, (userSpecifiedText = new JTextField(10)), 1, 0, 3, 1,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				3, 1);
			Constrain.setConstraints(usp, new JLabel("Name"),
				0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,
				1, 1);
			Constrain.setConstraints(usp, (textName = new JTextField(10)), 1, 1, 3, 1,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				3, 1);
			Constrain.setConstraints(usp, (addText = new JButton("Add")), 1, 3, 2, 1,
				GridBagConstraints.NONE, GridBagConstraints.EAST,
				2, 1);

			JPanel p2 = new JPanel();
			p2.setLayout(new GridBagLayout());
			JOutlinePanel aText = new JOutlinePanel("Automatic");
			aText.add( (autoText = new JButton("Auto") ) );
			// add the outline panel
			Constrain.setConstraints(p2, usp, 0, 0, 1, 1,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				1, 1);
			// add the auto button
			Constrain.setConstraints(p2, aText, 0, 1, 0, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				1, 1);

			// now add everything to str
			str.setLayout(new BorderLayout());
			// the scroller goes in the center
			str.add(textScroll, BorderLayout.CENTER);
			// everything else goes in the south
			str.add(p2, BorderLayout.SOUTH);

			addNumeric.addActionListener(this);
			addNumericUniform.addActionListener(this);
			//autoNumeric.addActionListener(this);
			addText.addActionListener(this);
			autoText.addActionListener(this);

			JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
			jtp.add(numeric, "Scalar Inputs");
			jtp.add(str, "Nominal Inputs");

			JPanel binArea = new JPanel();
			binArea.setLayout(new BorderLayout());
			JScrollPane binScroller = new JScrollPane(binList);
			JLabel binLabel = new JLabel("Current Bins");
			binScroller.setColumnHeaderView(binLabel);
			JPanel p3 = new JPanel();
			remove = new JButton("Remove");
			p3.add(remove);
			binArea.add(binScroller, BorderLayout.CENTER);
			binArea.add(p3, BorderLayout.SOUTH);

			remove.addActionListener(this);

			JPanel mainArea = new JPanel();
			mainArea.setLayout(new BorderLayout());
			mainArea.add(jtp, BorderLayout.CENTER);
			mainArea.add(binArea, BorderLayout.EAST);

			setLayout(new BorderLayout());
			add(mainArea, BorderLayout.CENTER);
			add(buttonArea, BorderLayout.SOUTH);
		}

		private void initData() {
			if(binListModel != null)
				binListModel.clear();

            indexLookup = new HashMap();
            for(int i = 0; i< table.getNumColumns(); i++)
                indexLookup.put(table.getColumnLabel(i), new Integer(i));
			// get the class and attribute names
			//Column classColumn = null;
			HashSet cn = new HashSet();
			LinkedList numericAn = new LinkedList();
			LinkedList textAn = new LinkedList();

			int [] ins = table.getInputFeatures();
			int [] outs = table.getOutputFeatures();

			// determine whether the inputs are numeric or text
			for(int i = 0; i < ins.length; i++) {
				String label = table.getColumnLabel(ins[i]);
				//if(table.getColumn(ins[i]) instanceof NumericColumn)
                if(table.isColumnScalar(ins[i]))
					numericAn.add(label);
				else
					textAn.add(label);
			}

			//classColumn = table.getColumn(outs[0]);
            //int numRows = classColumn.getNumRows();

			// get all unique outputs from the output column
			//if(classColumn != null) {
			for(int i = 0; i < table.getNumRows(); i++) {
				String s = table.getString(i, outs[0]);
				if(s != null && !cn.contains(s))
					cn.add(s);
			}

			classNames = new String[cn.size()];
			attributeNames = new String[numericAn.size()+textAn.size()];

			DefaultListModel numericModel = new DefaultListModel();
			DefaultListModel textModel = new DefaultListModel();

			Iterator i = cn.iterator();
			int idx = 0;
			while(i.hasNext() && idx < classNames.length) {
				String el = i.next().toString();
				classNames[idx] = el;
				idx++;
			}
			i = numericAn.listIterator();
			idx = 0;
			while(i.hasNext() && idx < attributeNames.length) {
				String item = i.next().toString();
				attributeNames[idx] = item;
				numericModel.addElement(item);
				idx++;
			}
			i = textAn.listIterator();
			while(i.hasNext() && idx < attributeNames.length) {
				String item = i.next().toString();
				attributeNames[idx] = item;
				textModel.addElement(item);
				idx++;
			}

			numericAttributes.setModel(numericModel);
			textAttributes.setModel(textModel);
			binTree = new BinTree(classNames, attributeNames);
			binEntries = new HashMap();
		}

		/**
			ActionListener interface
		*/
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src == addNumeric)
				addNumericBin();
			else if(src == autoNumeric)
				autoNumericBin();
			else if(src == addNumericUniform)
				addNumericUniformRange();
			else if(src == addUserSpecifiedRange)
				addSpecifiedRange();
			else if(src == addText)
				addStringBin();
			else if(src == autoText)
				autoStringBin();
			else if(src == remove)
				removeBin();
			else if(src == done) {
		        pushOutput(binTree, 0);
		        pushOutput(table, 1);
				viewDone("Done");
				table = null;
			}
			else if(src == abort)
				parent.viewCancel();
			else if(src == help)
				helpWindow.setVisible(true);
		}

		private void addNumericBin() {
			String eq = userSpecifiedNumeric.getText().trim();
			String name = numericName.getText().trim();
			Object []selected =  numericAttributes.getSelectedValues();

			if(eq.length() == 0) {
				JOptionPane.showMessageDialog(this,
					"No conditions given.", "Error",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(name.length() == 0) {
				JOptionPane.showMessageDialog(this,
					"No name given.", "Error",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(selected.length == 0 || selected.length > 1) {
				JOptionPane.showMessageDialog(this,
					"You must one attribute.", "Error",
					JOptionPane.ERROR_MESSAGE);
				return;
			}

			String attributeName = (String)selected[0];

			try {
				binTree.addBinFromEquation(attributeName, name, eq, true);
				BinEntry be = new BinEntry(attributeName, name, NUMERIC);
				addItemToBinList(name, be);
			}
			catch(Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
					"Unable to proceed.  Check your equation and try again",
					"Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		void addNumericUniformRange() {
			String uniform = uniformRange.getText().trim();
			String userSpec = userSpecifiedNumeric.getText().trim();

			/*if(uniform.indexOf(COMMA) != -1) {
				addSpecifiedRange();
				return;
			}*/

			if(uniform.length() > 0) {
				// get the number of bins
				int numBins = 0;
				try {
					numBins = Integer.parseInt(uniform);
				}
				catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(this,
						"The value input for Uniform Range is not a number",
						"Error adding numeric bin!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				// get the attribute name
				Object []selected = numericAttributes.getSelectedValues();
				if(selected.length == 0) {
					JOptionPane.showMessageDialog(this,
						"You must select at least one attribute",
						"Error adding numeric bin!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				for(int id = 0; id < selected.length; id++) {
					String attName = selected[id].toString();

					//NumericColumn nc = null;
                    /*int colIdx = -1;
					for(int z = 0; z < table.getNumColumns(); z++) {
						if(table.getColumnLabel(z).trim().equals(attName.trim())) {
							//nc = (NumericColumn)table.getColumn(z);
                            colIdx = z;
							break;
						}
					}*/
                    int colIdx = ((Integer)indexLookup.get(attName.trim())).intValue();

                    MaxMin mm = getMaxMin(colIdx);
					double max = mm.max;//nc.getMax();
					double min = mm.min;//nc.getMin();
					double diff = max - min;
					double increment = diff / (double)numBins;

					for(int q = 0; q < numBins; q++) {
						double lower = min;
						double upper = min+increment;

						StringBuffer nameBuffer = new StringBuffer();
						nameBuffer.append(OPEN_PAREN);
						if(q == 0)
							nameBuffer.append(DOTS);
						else {
							//NumberFormat nf = NumberFormat.getInstance();
							//nf.setMaximumFractionDigits(2);
							//String formatted = nf.format(lower);
							nameBuffer.append(nf.format(lower));
						}
						nameBuffer.append(COLON);
						if(q == (numBins - 1))
							nameBuffer.append(DOTS);
						else {
							//NumberFormat nf = NumberFormat.getInstance();
							//nf.setMaximumFractionDigits(2);
							//String formatted = nf.format(upper);
							nameBuffer.append(nf.format(upper));
						}
						nameBuffer.append(CLOSE_BRACKET);

						try {
							if(q == 0)
								binTree.addNumericBin(attName,
									nameBuffer.toString(),
									BinTree.LESS_THAN_EQUAL_TO, upper);
							else if(q == (numBins - 1))
								binTree.addNumericBin(attName,
									nameBuffer.toString(),
									BinTree.GREATER_THAN, lower);
							else {
								// make the proper equation and add the bin that way
								StringBuffer eq = new StringBuffer();
								eq.append(OPEN_PAREN);
								eq.append(attName);
								eq.append(BinTree.GREATER_THAN);
								eq.append(lower);
								eq.append(AND);
								eq.append(attName);
								eq.append(BinTree.LESS_THAN_EQUAL_TO);
								eq.append(upper);
								eq.append(CLOSE_PAREN);
								//System.out.println("EQ: "+eq);
								binTree.addBinFromEquation(attName, nameBuffer.toString(),
									eq.toString(), true);
							}
							StringBuffer entryName = new StringBuffer(attName);
							entryName.append(COLON);
							entryName.append(nameBuffer.toString());
							BinEntry be = new BinEntry(attName,
								nameBuffer.toString(), NUMERIC);
							addItemToBinList(entryName.toString(), be);
						}
						catch(Exception dbe) {
							dbe.printStackTrace();
							JOptionPane.showMessageDialog(this,
								"Error adding bins. Check your conditions and try again.", "There was an error "+
								"adding the bins", JOptionPane.ERROR_MESSAGE);
						}
						// increment the minimum
						min = upper;
					}
				}
				clearNumericTextFields();
			}
			else {
				JOptionPane.showMessageDialog(this,
					"You must specify a value for Uniform Range.", "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}

			private class MaxMin {
				double min;
				double max;
			}

			private MaxMin getMaxMin(int colIdx) {
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;

				for(int i = 0; i < table.getNumRows(); i++) {
					double d = table.getDouble(i, colIdx);
					if(d < min)
						min = d ;
					if(d > max)
						max = d;
				}
				MaxMin mm = new MaxMin();
				mm.min = min;
				mm.max = max;
				return mm;
			}

		void addSpecifiedRange() {
			// use string tokenizer to get the tokens.  the only delimiter should
			// be comma!
			String uniform = userSpecifiedRange.getText().trim();
			StringTokenizer strTok = new StringTokenizer(uniform, COMMA);
			double []bounds = new double[strTok.countTokens()];
			int idx = 0;
			try {
				while(strTok.hasMoreTokens()) {
					double d;
					d = Double.parseDouble(strTok.nextToken());
					bounds[idx] = d;
					idx++;
				}
			}
			catch(NumberFormatException e) {
				// print something mean
				JOptionPane.showMessageDialog(this,
						"Make sure the items are numeric.",
						"There was an error adding the bins",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// get the attribute name
			Object []selected = numericAttributes.getSelectedValues();
			if(selected.length == 0) {
				JOptionPane.showMessageDialog(this,
					"You must select at least one attribute",
					"Error adding numeric bin!",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			for(int id = 0; id < selected.length; id++) {
				String attName = selected[id].toString();

				for(int i = 0; i < bounds.length; i++) {
					StringBuffer nameBuffer = new StringBuffer(EMPTY);
					if(i == 0) {
						nameBuffer.append(OPEN_BRACKET);
						//nameBuffer.append("...,");
                        nameBuffer.append(DOTS);
                        nameBuffer.append(COMMA);
						nameBuffer.append(Double.toString(bounds[i]));
						nameBuffer.append(CLOSE_BRACKET);
						// add X <= bounds[0]
						try {
							binTree.addNumericBin(attName,
								nameBuffer.toString(),
								BinTree.LESS_THAN_EQUAL_TO, bounds[i]);
							StringBuffer entryName = new StringBuffer(attName);
							entryName.append(COLON);
							entryName.append(nameBuffer.toString());
							BinEntry be = new BinEntry(attName,
								nameBuffer.toString(), NUMERIC);
							addItemToBinList(entryName.toString(), be);
						}
						catch(Exception dbe) {
							dbe.printStackTrace();
							JOptionPane.showMessageDialog(this,
								"Error adding bins. Check your conditions and try again.",
								"There was an error adding the bins",
								JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						// make the proper equation and add the bin that way
						StringBuffer eq = new StringBuffer();
						eq.append(OPEN_PAREN);
						eq.append(attName);
						eq.append(BinTree.GREATER_THAN);
						eq.append(bounds[i-1]);
						eq.append(AND);
						eq.append(attName);
						eq.append(BinTree.LESS_THAN_EQUAL_TO);
						eq.append(bounds[i]);
						eq.append(CLOSE_PAREN);
						//System.out.println("EQ: "+eq);
						nameBuffer.append(OPEN_PAREN);
						nameBuffer.append(bounds[i-1]);
						nameBuffer.append(COMMA);
						nameBuffer.append(bounds[i]);
						nameBuffer.append(CLOSE_BRACKET);

						try {
							binTree.addBinFromEquation(attName, nameBuffer.toString(),
								eq.toString(), true);

							StringBuffer entryName = new StringBuffer(attName);
							entryName.append(COLON);
							entryName.append(nameBuffer.toString());
							BinEntry be = new BinEntry(attName,
								nameBuffer.toString(), NUMERIC);
							addItemToBinList(entryName.toString(), be);
						}
						catch(Exception dbe) {
							dbe.printStackTrace();
							JOptionPane.showMessageDialog(this,
								"Error adding bins. Check your conditions and try again.",
								"There was an error adding the bins",
								JOptionPane.ERROR_MESSAGE);
						}
					}
				}

				// finally add bounds[i] <= X
				StringBuffer nameBuffer = new StringBuffer(OPEN_PAREN);
				nameBuffer.append(Double.toString(bounds[bounds.length-1]));
				nameBuffer.append(COMMA);
				//nameBuffer.append("...]");
                nameBuffer.append(DOTS);
                nameBuffer.append(CLOSE_BRACKET);
				try {
					binTree.addNumericBin(attName,
						nameBuffer.toString(),
						BinTree.GREATER_THAN, bounds[bounds.length-1]);
					StringBuffer entryName = new StringBuffer(attName);
					entryName.append(COLON);
					entryName.append(nameBuffer.toString());
					BinEntry be = new BinEntry(attName,
						nameBuffer.toString(), NUMERIC);
					addItemToBinList(entryName.toString(), be);
				}
				catch(Exception dbe) {
					JOptionPane.showMessageDialog(this,
						"Error adding bins. Check your conditions and try again.",
						"There was an error adding the bins",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		private void autoNumericBin() {
			JOptionPane.showMessageDialog(this,
				"Not yet supported.", "Error!",
				JOptionPane.ERROR_MESSAGE);
		}

		void addStringBin() {
			String eq = userSpecifiedText.getText().trim();
			String name = textName.getText().trim();
			Object []selected =  textAttributes.getSelectedValues();

			if(eq.length() == 0) {
				JOptionPane.showMessageDialog(this,
					"No conditions given.", "Error",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(name.length() == 0) {
				JOptionPane.showMessageDialog(this,
					"No name given.", "Error",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(selected.length == 0 || selected.length > 1) {
				JOptionPane.showMessageDialog(this,
					"You must one attribute.", "Error",
					JOptionPane.ERROR_MESSAGE);
				return;
			}

			String attributeName = (String)selected[0];

			try {
				binTree.addBinFromEquation(attributeName, name, eq, false);
				BinEntry be = new BinEntry(attributeName, name, TEXT);
				addItemToBinList(name, be);
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(this,
					"Unable to proceed.  Check your equation and try again",
					"Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		/**
			create a unique bin for each item
		*/
		void autoStringBin() {
			Object []selected = textAttributes.getSelectedValues();

			for(int z = 0; z < selected.length; z++) {
				String attName = (String)selected[z];

				if(attName == null) {
					JOptionPane.showMessageDialog(this,
						"You must choose an attribute", "Auto error",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				//Column attCol = null;
                int attIdx = ((Integer)indexLookup.get(attName.trim())).intValue();

				// find all the unique entries for this column
				HashSet items = new HashSet();
                int numRows = table.getNumRows();
				for(int i = 0; i < numRows; i++) {
					String s = table.getString(i, attIdx);
					if(s != null && !items.contains(s))
						items.add(s);
				}

				Iterator i = items.iterator();
				while(i.hasNext()) {
					String s = (String)i.next();
					try {
						binTree.addStringBin(attName, s, s);
						StringBuffer entryBuffer = new StringBuffer(s);
						entryBuffer.append(COLON);
						entryBuffer.append(attName);
						entryBuffer.append(EQUAL_TO);
						entryBuffer.append(s);

						BinEntry be = new BinEntry(attName, s, TEXT);
						addItemToBinList(entryBuffer.toString(), be);
					}
					catch(Exception e) {
						JOptionPane.showMessageDialog(this,
							"Unable to proceed.  Please add bins manually.", "Error",
							JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		}

		/**
			Remove a bin
		*/
		private void removeBin() {
			Object []entryNames = binList.getSelectedValues();
			for(int i = 0; i < entryNames.length; i++) {
				String n = (String)entryNames[i];
				BinEntry be = (BinEntry)binEntries.get(n);
				if(be != null) {
					try {
						binTree.removeBin(be.attributeName, be.binName);
						binListModel.removeElement(n);
						binEntries.remove(n);
					}
					catch(BinTree.BinNotFoundException e) {
						JOptionPane.showMessageDialog(this,
							"The bin did not exist!", "Error",
							JOptionPane.ERROR_MESSAGE);
					}
					catch(BinTree.AttributeNotFoundException ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		/**
			Add an item to the bin list
		*/
		void addItemToBinList(String entryName, BinEntry be) {
			binListModel.addElement(entryName);
			binEntries.put(entryName, be);
		}

		void clearNumericTextFields() {
			uniformRange.setText(EMPTY);
			userSpecifiedNumeric.setText(EMPTY);
			numericName.setText(EMPTY);
		}

		private void clearStringTextFields() {
			userSpecifiedText.setText(EMPTY);
			textName.setText(EMPTY);
		}

		class BinEntry {
			String binName;
			String attributeName;
			String type;

			BinEntry(String attname, String binname, String t) {
				binName = binname;
				attributeName = attname;
				type = t;
			}
		}

	}

    protected static final String EMPTY = "";
    protected static final String EQUAL_TO = " == ";
    protected static final String COLON = " : ";
    protected static final String COMMA = ",";
    protected static final String OPEN_PAREN = "(";
    protected static final String CLOSE_PAREN = ")";
    protected static final String OPEN_BRACKET = "[";
    protected static final String CLOSE_BRACKET = "]";
    protected static final String DOTS = "...";
	protected static final String AND = ") && (";
    protected NumberFormat nf;

	final private class HelpWindow extends JD2KFrame {
		HelpWindow() {
			super("About DefineBins");
			JEditorPane jep = new JEditorPane("text/html", getHelpString());
			getContentPane().add(new JScrollPane(jep));
			setSize(400, 200);
		}
	}

	private static final String getHelpString() {
		StringBuffer sb = new StringBuffer("<html>");
		sb.append("<h2>DefineBins</h2>");
		sb.append("Allows for the grouping of data.  The data is spilt into two ");
		sb.append("types, numeric and textual.");
		sb.append("<h3>Scalar Inputs</h3>");
		sb.append("<ul><li>Uniform Range: Creates N evenly-spaced bins for each of the");
		sb.append("selected inputs, where N is the number entered in the text field.");
		sb.append("<li>User Specified Range: Specify a comma-separated list of ");
		sb.append("the bins to create.  If 2, 3, 4 is entered, then bins will be created ");
		sb.append("for values less than 2, between 2 and 3, between 3 and 4, and greater ");
		sb.append("four.");
		sb.append("<li>User Specified: An equation can be entered to manually ");
		sb.append("to create bins.  These equations must be properly parenthesized.  ");
		sb.append("The following symbols can be used: ");
		sb.append("<ul><li>=");
		sb.append("<li> &lt");
		sb.append("<li> >");
		sb.append("<li> &lt=");
		sb.append("<li> >=");
		sb.append("<li> (");
		sb.append("<li> )");
		sb.append("<li> &&");
		sb.append("<li> ||");
		sb.append("</ul>");
		sb.append("For example, if the input is named Length, then the following ");
		sb.append("equation would be valid:<br>");
		sb.append("((Length = 5) && (Length = 10))<br>");
		sb.append("</ul>");
		sb.append("<h3>Nominal Inputs</h3>");
		sb.append("<ul><li>User Specified: An equation can be entered manually ");
		sb.append("to create bins.  These equations must be properly parenthesized.  ");
		sb.append("The following symbols can be used: ");
		sb.append("<ul><li>=");
		sb.append("<li> &lt");
		sb.append("<li> >");
		sb.append("<li> &lt=");
		sb.append("<li> >=");
		sb.append("<li> (");
		sb.append("<li> )");
		sb.append("<li> &&");
		sb.append("<li> ||");
		sb.append("</ul>");
		sb.append("For example, if the input is named State, then the following ");
		sb.append("equation would be valid:<br>");
		sb.append("((State = IL) && (State = WI))<br>");
		sb.append("<li>Automatic: Automatically creates a bin for each unique item ");
		sb.append("in the selected column.");
		sb.append("</ul>");
		sb.append("</html>");
		return sb.toString();
	}
}
