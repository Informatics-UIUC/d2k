package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;



import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

/**
   ADTDefineBins presents a GUI to allow the use to enter classifications for
   a data set using the summary data from an ADTree.
   @author David Clutter and Anca Suvaiala
*/
public class ADTDefineBins extends DefineBins {


    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Allows the user to bin string data using an ADTree");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "ADTDefineBin";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
	String []in = {	"ncsa.d2k.modules.core.datatype.ADTree",
		       "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
	return in;
	}





    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
	String []out = {"ncsa.d2k.modules.core.datatype.BinTree",
		       "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
	return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
    	switch(i) {
	case 0: return "ADTree containing counts";
	case 1: return "Table with metadata in it .";
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
	case 0: return "ADTree";
	case 1: return "Metadata Table";
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
	case 0: return "Bin tree with binning info";
   	case 1: return " metadata table, unchanged";
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
	case 0: return "bin tree";
	case 1: return "metadata table";
	default: return "no such output!";
	}
    }

    public UserView createUserView() {
	return new SQLBinView();
    }





	/**
		The complex user view.
	*/
	class SQLBinView extends BinView {

	    ADTree adt;
	    //	    ExampleTable table;
	    boolean tableArrived = false;
	    boolean adtArrived = false;


	    public void setInput(Object o, int i) {
		if(i == 0) {
		    adt = (ADTree)o;
		    adtArrived = true;
		}
		if(i == 1) {
		    table = (ExampleTableImpl)o;
		    tableArrived = true;

		}
		if(adtArrived && tableArrived ) {
		  initData();
		  adtArrived = false;
		  tableArrived = false;
		}
	    }



	    void initData() {

		if(binListModel != null)
		    binListModel.clear();

		// get the class and attribute names
		Column classColumn = null;
		HashMap cn = new HashMap();
		LinkedList numericAn = new LinkedList();
		LinkedList textAn = new LinkedList();


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

		// get all unique outputs from the output column
		/*	if(classColumn != null) {
			for(int i = 0; i < classColumn.getNumRows(); i++) {
			Object ob = classColumn.getRow(i);
			String s = ob.toString();
			if(!cn.containsKey(s))
			cn.put(s, s);
			}
			}
		*/


		int index = adt.getIndexForLabel((classColumn).getLabel());

		String [] uniqueValues = adt.getUniqueValues(index);
		for (int i = 0; i < uniqueValues.length; i ++)
		    cn.put(uniqueValues[i],uniqueValues[i]);


		System.out.println("classCol = " +
				   classColumn.getLabel() );



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







	    void addNumericUniformRange() {
		System.err.println("addNumericUniformRange not implemented for an ADTree  ");
	    }
	    /*
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
					    if(table.getColumn(z).getLabel().trim().equals(attName.trim())) {
						nc = (NumericColumn)table.getColumn(z);
						break;
					    }
					}

					double max=0.0;
                                        double min=0.0;

					String numericColumn= nc.getLabel().trim();
                                        try {
                                            Statement stmt = conn.getConnection().createStatement();


                                            ResultSet maxVal =
                                                stmt.executeQuery("SELECT MAX(" + numericColumn
                                                                  + ") FROM " + tableName);


                                            maxVal.next();
                                            max = maxVal.getDouble(1);

                                            ResultSet minVal =
                                                stmt.executeQuery("SELECT MIN(" + numericColumn
                                                                  + ") FROM " + tableName);

                                            minVal.next();
                                            min = minVal.getDouble(1);

                                        } catch (SQLException e) { e.printStackTrace(); }
                                        catch (ClassNotFoundException ne) { System.out.println( ne); }
                                        catch (InstantiationException ee) { System.out.println( ee); }
                                        catch (IllegalAccessException nie) { System.out.println( nie); };

					//double max = nc.getMax();
					//double min = nc.getMin();
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
							nameBuffer.append(formatted);//Double.toString(lower)
						}
						nameBuffer.append(",");
						if(q == (numBins - 1))
							nameBuffer.append("...");
						else {
							NumberFormat nf = NumberFormat.getInstance();
							nf.setMaximumFractionDigits(2);
							String formatted = nf.format(upper);
							nameBuffer.append(formatted);//Double.toString(upper)
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

                  */


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
		    Column attCol = null;
		    for(int i = 0; i < table.getNumColumns(); i++) {
			Column c = table.getColumn(i);
			if(c.getLabel().trim() == attName.trim()) {
			    attCol = (Column)c;
			    break;
			}
		    }

		    String stringColumn = attCol.getLabel().trim();
		    // find all the unique entries for this column
		    HashMap items = new HashMap();
				/*
				  for(int i = 0; i < attCol.getNumRows(); i++) {
				  String s = attCol.getString(i);
				  if(!items.containsKey(s))
				  items.put(s, s);
				  }*/



		    int index = adt.getIndexForLabel(stringColumn);

		    String [] uniqueValues = adt.getUniqueValues(index);
		    for (int i = 0; i < uniqueValues.length; i ++)
			items.put(uniqueValues[i],uniqueValues[i]);


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
	}


}








