package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import ncsa.d2k.modules.core.datatype.*;

/**
   DefineBins presents a GUI to allow the user to enter classifications for
   a data set.
   @author David Clutter
*/
public class DefineBins extends UIModule implements HasNames {

	static String NUMERIC = "num";
	static String TEXT = "text";

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Allows the user to bin data.");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "defineBin";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
    	/*String []in = {"ncsa.d2k.util.datatype.VerticalTable",
			"java.util.HashMap"};
		*/
		String []in = {"ncsa.d2k.util.datatype.ExampleTable"};
		return in;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
    	/*String []out = {"ncsa.d2k.modules.dataprep.field.BinTree",
			"ncsa.d2k.util.datatype.VerticalTable",
			"java.util.HashMap"};
		*/
		String []out = {"ncsa.d2k.modules.dataprep.field.BinTree",
			"ncsa.d2k.util.datatype.ExampleTable"};
		return out;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
    	switch(i) {
			case 0: return "Table with data in it.";
			case 1: return "Map of attributes and classes.";
			default: return "no such input!";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
    	switch(i) {
			case 0: return "table";
			case 1: return "map";
			default: return "no such input!";
		}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
    	switch(i) {
			case 0: return "Contains binning info";
			case 1: return "table, unchanged";
			case 2: return "map";
			default: return "no such output!";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	switch(i) {
			case 0: return "binInfo";
			case 1: return "table";
			case 2: return "map";
			default: return "no such output!";
		}
	}

	public UserView createUserView() {
		return new BinView();
	}

	public String []getFieldNameMapping() {
		return null;
	}

	void finish(BinTree bt, /*Vertical*/ExampleTable vt/*, HashMap types*/) {
		// read in all the data and classify it
		/*String classLabel = null;
		int classColumn = 0;
		Iterator it = types.keySet().iterator();
		while(it.hasNext()) {
			String name = (String)it.next();
			String type = (String)types.get(name);
			if(type.equals(ChooseAttributes.CLASS)) {
				classLabel = name;
				break;
			}
		}

		for(int i = 0; i < vt.getNumColumns(); i++) {
			if(vt.getColumn(i).getLabel().trim().equals(classLabel.trim()))
				classColumn = i;
		}
		for(int i = 0; i < vt.getNumColumns(); i++) {
			if(i != classColumn && !omit(vt.getColumnLabel(i), types)) {
				Column c = vt.getColumn(i);
				SimpleColumn sc = (SimpleColumn)c;
				for(int j = 0; j < sc.getNumRows(); j++) {
					if(sc instanceof NumericColumn)
						bt.classify(vt.getString(j, classColumn),
							sc.getLabel(), vt.getDouble(j, i));
					else
						bt.classify(vt.getString(j, classColumn),
							sc.getLabel(), vt.getString(j, i));
				}
			}
		}
		*/

		/*int [] ins = vt.getInputFeatures();
		int [] out = vt.getOutputFeatures();

		// we only support one out variable..
		int classColumn = out[0];

		for(int i = 0; i < ins.length; i++) {
			SimpleColumn sc = (SimpleColumn)vt.getColumn(ins[i]);

			// numeric columns
			if(sc instanceof NumericColumn) {
				for(int j = 0; j < sc.getNumRows(); j++)
					bt.classify(vt.getString(j, classColumn),
						sc.getLabel(), vt.getDouble(j, i));
			}

			// everything else is treated as textual columns
			else {
				for(int j = 0; j < sc.getNumRows(); j++)
					bt.classify(vt.getString(j, classColumn),
						sc.getLabel(), vt.getString(j, i));
			}
		}*/

		//bt.printAll();
		pushOutput(bt, 0);
		pushOutput(vt, 1);
		//pushOutput(types, 2);
		executionManager.moduleDone(this);
	}

	/**
		The complex user view.
	*/
	class BinView extends JUserPane implements ActionListener {
		/** general */
		DefineBins parent;
		BinTree binTree;
		JButton abort;
		JButton done;

		/** numeric stuff */
		JList numericAttributes;
		JTextField uniformRange;
		JTextField userSpecifiedNumeric;
		JTextField numericName;
		JButton addNumeric;
		JButton addNumericUniform;
		JButton autoNumeric;

		/** text stuff */
		JList textAttributes;
		JTextField userSpecifiedText;
		JTextField textName;
		JButton addText;
		JButton autoText;

		/** general */
		JList binList;
		DefaultListModel binListModel;
		HashMap binEntries;
		JButton remove;

		/** inputs */
		//VerticalTable table;
		//HashMap types;
		ExampleTable table;
		/** flags for inputs */
		//boolean tableArrived = false;
		//boolean mapArrived = false;

		/** The class and attribute names, derived from the table. */
		String []classNames;
		String []attributeNames;

		public void setInput(Object o, int i) {
			if(i == 0) {
				//table = (VerticalTable)o;
				table = (ExampleTable)o;
				//tableArrived = true;
				initData();
			}
			/*if(i == 1) {
				types = (HashMap)o;
				mapArrived = true;
			}

			if(tableArrived && mapArrived) {
				initData();
				tableArrived = false;
				mapArrived = false;
			}
			*/
		}

		public Dimension getPreferredSize() {
			return new Dimension (700, 500);
		}

		public void initView(ViewModule vm) {
			parent = (DefineBins)vm;

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
			buttonArea.add(abort);
			buttonArea.add(done);

			// layout the numeric panel
			JPanel numeric = new JPanel();
			numeric.setLayout(new GridBagLayout());
			Constrain.setConstraints(numeric, new JLabel("Attribute Names"),
				0, 0, 2, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 2, 1);
			Constrain.setConstraints(numeric, new JScrollPane(numericAttributes),
				0, 1, 4, 2, GridBagConstraints.BOTH,
				GridBagConstraints.WEST, 8, 2);
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

			Constrain.setConstraints(numeric, uni, 0, 3, 4, 2,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				4, 2);
			Constrain.setConstraints(numeric, spec, 0, 7, 4, 2,
				GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
				4, 2);
			Constrain.setConstraints(numeric, (autoNumeric = new JButton("Auto")),
				1, 9, 2, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER,
				2, 1);


			JPanel str = new JPanel();
			str.setLayout(new GridBagLayout());
			Constrain.setConstraints(str, new JLabel("Attribute Names"),
				0, 0, 2, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 2, 1);
			Constrain.setConstraints(str, new JScrollPane(textAttributes),
				0, 1, 4, 1, GridBagConstraints.BOTH,
				GridBagConstraints.WEST, 4, 1);

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

			JOutlinePanel aText = new JOutlinePanel("Automatic");
			aText.add( (autoText = new JButton("Auto") ) );
			// add the outline panel
			Constrain.setConstraints(str, usp, 0, 2, 4, 4,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				4, 4);
			// add the auto button
			Constrain.setConstraints(str, aText, 1, 6, 2, 1,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				2, 1);

			addNumeric.addActionListener(this);
			addNumericUniform.addActionListener(this);
			autoNumeric.addActionListener(this);
			addText.addActionListener(this);
			autoText.addActionListener(this);

			JTabbedPane jtp = new JTabbedPane(JTabbedPane.TOP);
			jtp.add(numeric, "Numeric");
			jtp.add(str, "Text");

			JPanel binArea = new JPanel();
			binArea.setLayout(new GridBagLayout());
			Constrain.setConstraints(binArea, new JLabel("Current Bins"),
				0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.WEST, 1, 1);
			Constrain.setConstraints(binArea, new JScrollPane(binList),
				0, 1, 5, 1, GridBagConstraints.BOTH,
				GridBagConstraints.WEST, 5, 1);
			Constrain.setConstraints(binArea, (remove = new JButton("Remove")),
				0, 6, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.CENTER, 1, 1);

			remove.addActionListener(this);

			JPanel mainArea = new JPanel();
			mainArea.setLayout(new GridBagLayout());
			Constrain.setConstraints(mainArea, jtp,
				0, 0, 3, 1, GridBagConstraints.BOTH,
				GridBagConstraints.WEST, 3, 1);
			Constrain.setConstraints(mainArea, binArea,
				3, 0, 2, 1, GridBagConstraints.BOTH,
				GridBagConstraints.WEST, 2, 1);

			setLayout(new BorderLayout());
			add(mainArea, BorderLayout.CENTER);
			add(buttonArea, BorderLayout.SOUTH);
		}

		void initData() {
			if(binListModel != null)
				binListModel.clear();

			// get the class and attribute names
			Column classColumn = null;
			HashMap cn = new HashMap();
			LinkedList numericAn = new LinkedList();
			LinkedList textAn = new LinkedList();

			/*for(int i = 0; i < table.getNumColumns(); i++) {
				String label = table.getColumnLabel(i);
				String type = (String)types.get(label);
				if(type.equals(ChooseAttributes.CLASS))
					classColumn = table.getColumn(i);
				else if(type.equals(ChooseAttributes.ATTRIBUTE)) {
					if(table.getColumn(i) instanceof NumericColumn)
						numericAn.add(label);
					else
						textAn.add(label);
				}
			}*/

			int [] ins = table.getInputFeatures();
			int [] outs = table.getOutputFeatures();

			// determine whether the inputs are numeric or text
			for(int i = 0; i < ins.length; i++) {
				String label = table.getColumnLabel(ins[i]);
				if(table.getColumn(ins[i]) instanceof NumericColumn)
					numericAn.add(label);
				else
					textAn.add(label);
			}

			classColumn = table.getColumn(outs[0]);
                        int numRows = classColumn.getNumRows();

			// get all unique outputs from the output column
			if(classColumn != null) {
				for(int i = 0; i < numRows; i++) {
					Object ob = classColumn.getRow(i);
					String s = ob.toString();
					if(!cn.containsKey(s))
						cn.put(s, s);
				}
			}
			classNames = new String[cn.size()];
			attributeNames = new String[numericAn.size()+textAn.size()];

			DefaultListModel numericModel = new DefaultListModel();
			DefaultListModel textModel = new DefaultListModel();

			Iterator i = cn.values().iterator();
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
			else if(src == addText)
				addStringBin();
			else if(src == autoText)
				autoStringBin();
			else if(src == remove)
				removeBin();
			else if(src == done)
				parent.finish(binTree, table/*, types*/);
			else if(src == abort)
				parent.viewCancel();
		}

		void addNumericBin() {
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

			if(uniform.indexOf(",") != -1) {
				addSpecifiedRange();
				return;
			}

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

					NumericColumn nc = null;
					for(int z = 0; z < table.getNumColumns(); z++) {
						if(table.getColumn(z).getLabel().trim().equals(
								attName.trim())) {
							nc = (NumericColumn)table.getColumn(z);
							break;
						}
					}

					double max = nc.getMax();
					double min = nc.getMin();
					double diff = max - min;
					double increment = diff / (double)numBins;

					for(int q = 0; q < numBins; q++) {
						double lower = min;
						double upper = min+increment;

						StringBuffer nameBuffer = new StringBuffer();
						nameBuffer.append("(");
						if(q == 0)
							nameBuffer.append("...");
						else {
							NumberFormat nf = NumberFormat.getInstance();
							nf.setMaximumFractionDigits(2);
							String formatted = nf.format(lower);
							nameBuffer.append(formatted/*Double.toString(lower)*/);
						}
						nameBuffer.append(",");
						if(q == (numBins - 1))
							nameBuffer.append("...");
						else {
							NumberFormat nf = NumberFormat.getInstance();
							nf.setMaximumFractionDigits(2);
							String formatted = nf.format(upper);
							nameBuffer.append(formatted/*Double.toString(upper)*/);
						}
						nameBuffer.append("]");

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
								eq.append("(");
								eq.append(attName);
								eq.append(BinTree.GREATER_THAN);
								eq.append(lower);
								eq.append(") && (");
								eq.append(attName);
								eq.append(BinTree.LESS_THAN_EQUAL_TO);
								eq.append(upper);
								eq.append(")");
								//System.out.println("EQ: "+eq);
								binTree.addBinFromEquation(attName, nameBuffer.toString(),
									eq.toString(), true);
							}
							StringBuffer entryName = new StringBuffer(attName);
							entryName.append(" : ");
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

		void addSpecifiedRange() {
			// use string tokenizer to get the tokens.  the only delimiter should
			// be comma!
			String uniform = uniformRange.getText().trim();
			StringTokenizer strTok = new StringTokenizer(uniform, ",");
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
					StringBuffer nameBuffer = new StringBuffer("");
					if(i == 0) {
						nameBuffer.append("[");
						nameBuffer.append("...,");
						nameBuffer.append(Double.toString(bounds[i]));
						nameBuffer.append("]");
						// add X <= bounds[0]
						try {
							binTree.addNumericBin(attName,
								nameBuffer.toString(),
								BinTree.LESS_THAN_EQUAL_TO, bounds[i]);
							StringBuffer entryName = new StringBuffer(attName);
							entryName.append(" : ");
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
						eq.append("(");
						eq.append(attName);
						eq.append(BinTree.GREATER_THAN);
						eq.append(bounds[i-1]);
						eq.append(") && (");
						eq.append(attName);
						eq.append(BinTree.LESS_THAN_EQUAL_TO);
						eq.append(bounds[i]);
						eq.append(")");
						//System.out.println("EQ: "+eq);
						nameBuffer.append("(");
						nameBuffer.append(bounds[i-1]);
						nameBuffer.append(",");
						nameBuffer.append(bounds[i]);
						nameBuffer.append("]");

						try {
							binTree.addBinFromEquation(attName, nameBuffer.toString(),
								eq.toString(), true);

							StringBuffer entryName = new StringBuffer(attName);
							entryName.append(" : ");
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
				StringBuffer nameBuffer = new StringBuffer("(");
				nameBuffer.append(Double.toString(bounds[bounds.length-1]));
				nameBuffer.append(",");
				nameBuffer.append("...]");
				try {
					binTree.addNumericBin(attName,
						nameBuffer.toString(),
						BinTree.GREATER_THAN, bounds[bounds.length-1]);
					StringBuffer entryName = new StringBuffer(attName);
					entryName.append(" : ");
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

		void autoNumericBin() {
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
				SimpleColumn attCol = null;
				for(int i = 0; i < table.getNumColumns(); i++) {
					Column c = table.getColumn(i);
					if(c.getLabel().trim() == attName.trim()) {
						attCol = (SimpleColumn)c;
						break;
					}
				}

				// find all the unique entries for this column
				HashMap items = new HashMap();
                                int numRows = attCol.getNumRows();
				for(int i = 0; i < numRows; i++) {
					String s = attCol.getString(i);
					if(!items.containsKey(s))
						items.put(s, s);
				}

				Iterator i = items.values().iterator();
				while(i.hasNext()) {
					String s = (String)i.next();
					try {
						binTree.addStringBin(attName, s, s);
						StringBuffer entryBuffer = new StringBuffer(s);
						entryBuffer.append(" : ");
						entryBuffer.append(attName);
						entryBuffer.append(" == ");
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
		void removeBin() {
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
			uniformRange.setText("");
			userSpecifiedNumeric.setText("");
			numericName.setText("");
		}

		void clearStringTextFields() {
			userSpecifiedText.setText("");
			textName.setText("");
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
}
