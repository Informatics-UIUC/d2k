package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
import ncsa.d2k.util.datatype.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import java.util.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.*;

/**
   SQLDefineBins presents a GUI to allow the use to enter classifications for
   a data set.
   @author David Clutter and Anca Suvaiala
*/
public class SQLDefineBins extends DefineBins {


    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Allows the user to bin data comming from a database");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "SQLDefineBin";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
	String []in = {	"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
			"java.lang.String",
			"ncsa.d2k.util.datatype.ExampleTable"};
	return in;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.modules.core.datatype.BinTree",
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
	case 0: return "SQL Connection ";
	case 1: return "Table with data in it .";
	case 2: return "Table with metadata in it .";
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
	case 0: return "connection";
	case 1: return "table";
	case 2: return "MetadataTable";
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
	    ConnectionWrapper conn;
	    String tableName;
	    //ExampleTable table;
	    boolean tableArrived = false;
	    boolean connArrived = false;
	    boolean tnArrived = false;

	    public void setInput(Object o, int i) {
		if(i == 0) {
		    conn = (ConnectionWrapper)o;
		    connArrived = true;
		}
		if(i == 1) {
		    tableName = (String)o;
		    tnArrived = true;
		}
		if(i == 2) {
		    table = (ExampleTable)o;
		    tableArrived = true;
		}
		if(connArrived && tableArrived && tnArrived) {
		  initData();
		  connArrived = false;
		  tableArrived = false;
		  tnArrived = false;
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

			Statement stmt = null;
			try {
			    stmt = conn.getConnection().createStatement();
			    System.out.println("classCol = " +
					       classColumn.getLabel() +
					       " table name = " + tableName);
			    ResultSet classes =
				stmt.executeQuery("SELECT DISTINCT " + (classColumn).getLabel() + " FROM " + tableName);

			    while (classes.next()) {
				String s = classes.getString(1);
				cn.put(s, s);
			    }
                        } catch (SQLException e) {
			    try {
				stmt.close();
				conn.getConnection().close();
			    } catch (Exception ex) {}
			    e.printStackTrace();
			}
			catch (ClassNotFoundException ne) { System.out.println( ne); }
			catch (InstantiationException ee) { System.out.println( ee); }
			catch (IllegalAccessException nie) { System.out.println( nie); };


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
			String uniform = uniformRange.getText().trim();
			String userSpec = userSpecifiedNumeric.getText().trim();

			/*if(uniform.indexOf(",") != -1) {
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
						nameBuffer.append(OPEN_PAREN);
						if(q == 0)
							nameBuffer.append(DOTS);
						else {
							//NumberFormat nf = NumberFormat.getInstance();
							//nf.setMaximumFractionDigits(2);
							String formatted = nf.format(lower);
							nameBuffer.append(formatted/*Double.toString(lower)*/);
						}
						nameBuffer.append(COMMA);
						if(q == (numBins - 1))
							nameBuffer.append(DOTS);
						else {
							//NumberFormat nf = NumberFormat.getInstance();
							//nf.setMaximumFractionDigits(2);
							String formatted = nf.format(upper);
							nameBuffer.append(formatted/*Double.toString(upper)*/);
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

				String stringColumn = attCol.getLabel().trim();
				// find all the unique entries for this column
				HashMap items = new HashMap();

				try {
				    Statement stmt = conn.getConnection().createStatement();
                                ResultSet uniqueItems =
				    stmt.executeQuery("SELECT DISTINCT" + stringColumn
						      + " FROM " + tableName);

                                while(uniqueItems.next()) {
                                    String s = uniqueItems.getString(1);
                                    items.put(s,s);
                                }
                                } catch (SQLException e) { e.printStackTrace(); }
				catch (ClassNotFoundException ne) { System.out.println( ne); }
				catch (InstantiationException ee) { System.out.println( ee); }
				catch (IllegalAccessException nie) { System.out.println( nie); };



				Iterator i = items.values().iterator();
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
	}


}




