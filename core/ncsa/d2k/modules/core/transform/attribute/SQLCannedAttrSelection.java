package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.controller.userviews.swing.*;
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
import ncsa.d2k.modules.core.datatype.table.*;
/**
 SQLCannedAttrSelection is a module in which table, attribute and bin selections are hard coded
 to speed up testing
 @author Anca Suvaiala
*/

public class SQLCannedAttrSelection extends DataPrepModule implements HasNames {

	static String NUMERIC = "num";
	static String TEXT = "text";

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Defines the binning tree ");
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

	String []in = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper"};
	return in;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String []out = {"ncsa.d2k.modules.core.datatype.BinTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable",
				"java.lang.String"};
		return out;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
    	switch(i) {
			case 0: return "Connection to the database";
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
			case 0: return "sql connection";
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
			case 1: return "table containing metadata";
			case 2: return "name of the table containing the actual data";
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
			case 2: return "sqltable";
			default: return "no such output!";
		}
	}


    String tableName = "CHURN";

    public String getTableName() {
	return tableName;
    }

    public void setTableName( String name) {
	tableName = name;
    }


    public void doit() {

	ConnectionWrapper conn  = (ConnectionWrapper)pullInput(0);
	System.out.println("received connection wrapper");
	BinTree binTree;


	// creating ExampleTable containing metadata - from SQLMeta2VT
 	ResultSet result;
        ExampleTable vt = null;
	try {
            Statement stmt = conn.getConnection().createStatement();
            result = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE ROWNUM=1");

            ResultSetMetaData columnMetadata = result.getMetaData();

            int numColumns = columnMetadata.getColumnCount();
            vt = TableFactory.createExampleTable(numColumns);
            for(int i = 0; i < numColumns; i++) {
                int type = columnMetadata.getColumnType(i+1);
                if ( type == java.sql.Types.SMALLINT ||
                     type == java.sql.Types.NUMERIC ||
                     type == java.sql.Types.INTEGER ||
                     type == java.sql.Types.FLOAT ||
                     type == java.sql.Types.DOUBLE ||
                     type == java.sql.Types.BIGINT ||
                     type == java.sql.Types.REAL ||
                     type == java.sql.Types.DECIMAL )
                    {
                        DoubleColumn col = new DoubleColumn();
                        String columnName = columnMetadata.getColumnLabel(i+1);
			System.out.println("double  column " + i + ": "
					   + columnMetadata.getColumnLabel(i+1));
                        col.setLabel(columnName);
			vt.setColumn(col, i);
                    }
		else if( type == java.sql.Types.CHAR ||
                         type == java.sql.Types.VARCHAR ||
                         type == java.sql.Types.LONGVARCHAR ||
                         type == java.sql.Types.DATE)
                    {
                        StringColumn col = new StringColumn();
                        col.setLabel(columnMetadata.getColumnLabel(i+1));
			System.out.println(" String column " + i + ": "
					   + columnMetadata.getColumnLabel(i+1));
                        vt.setColumn(col, i);
                    }
                else {
                    ObjectColumn col = new ObjectColumn();
                    col.setLabel(columnMetadata.getColumnLabel(i+1));
			System.out.println("Object column " + i + ": "
					   + columnMetadata.getColumnLabel(i+1));
                    vt.setColumn(col, i);
                }

            }

        } catch (SQLException e) { System.out.println( e); }
        catch (ClassNotFoundException ne) { System.out.println( ne); }
        catch (InstantiationException ee) { System.out.println( ee); }
        catch (IllegalAccessException nie) { System.out.println( nie); };

	System.out.println("metadata extracted");
	//ChooseInputFields functionality

	// Construct an array of the indices of the outputs.
	int sz = 1;
	int [] newOuts = new int [sz];
	newOuts [0] = 20;

	vt.setOutputFeatures (newOuts);

	// Construct an array of the indices of the inputs.
	sz = 15;
	int [] newInts = new int [sz];
	newInts [0] = 1;
	newInts [1] = 6;
	newInts [2] = 7;
	newInts [3] = 8;
	newInts [4] = 9;
	newInts [5] = 10;
	newInts [6] = 11;
	newInts [7] = 12;
	newInts [8] = 13;
	newInts [9] = 14;
	newInts [10] = 15;
	newInts [11] = 16;
	newInts [12] = 17;
	newInts [13] = 18;
	newInts [14] = 19;


	vt.setInputFeatures (newInts);


	System.out.println("Choose input fields done");
	//DefineBins functionality

	//	DefaultListModel binListModel;


	/** The class and attribute names, derived from the table. */
	String []classNames;
	String []attributeNames;

	// get the class and attribute names
	Column classColumn = null;
	HashMap cn = new HashMap();
	LinkedList numericAn = new LinkedList();
	LinkedList textAn = new LinkedList();

	int [] ins = vt.getInputFeatures();
	int [] out = vt.getOutputFeatures();

	// determine whether the inputs are numeric or text
	for(int i = 0; i < ins.length; i++) {
	    String label = vt.getColumnLabel(ins[i]);
	    if(vt.getColumn(ins[i]) instanceof NumericColumn)
		numericAn.add(label);
	    else
		textAn.add(label);
	}

	classColumn = vt.getColumn(out[0]);


	try {
	    Statement stmt = conn.getConnection().createStatement();
	    System.out.println("classCol = " +  classColumn.getLabel() + " table name = " + tableName);
	    ResultSet classes =
		stmt.executeQuery("SELECT DISTINCT " + (classColumn).getLabel() + " FROM " + tableName);

	    while (classes.next()) {
		String s = classes.getString(1);
		cn.put(s, s);
	    }
	} catch (SQLException e) {
	    //	    try {
	    //	stmt.close();
	    //	conn.getConnection().close();
	    //} catch (Exception ex) {}
	    e.printStackTrace();
	}
	catch (ClassNotFoundException ne) { System.out.println( ne); }
	catch (InstantiationException ee) { System.out.println( ee); }
	catch (IllegalAccessException nie) { System.out.println( nie); };


	classNames = new String[cn.size()];
	attributeNames = new String[numericAn.size()+textAn.size()];

	//	DefaultListModel numericModel = new DefaultListModel();
	//DefaultListModel textModel = new DefaultListModel();

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
	    //	    numericModel.addElement(item);
	    idx++;
	}
	i = textAn.listIterator();
	while(i.hasNext() && idx < attributeNames.length) {
	    String item = i.next().toString();
	    attributeNames[idx] = item;
	    // textModel.addElement(item);
	    idx++;
	}

	//numericAttributes.setModel(numericModel);
	//textAttributes.setModel(textModel);


	binTree = new BinTree(classNames, attributeNames);
	HashMap binEntries = new HashMap();

	System.out.println("Define bins");
	//add Numeric Uniform Range
	int numBins = 5;

	for(int id = 0; id < sz; id++) {
	    double max=0.0;
	    double min=0.0;

	    String attName = vt.getColumn(newInts[id]).getLabel().trim();
	    System.out.println( "max/min attName  "  + attName);
	    try {
		Statement stmt = conn.getConnection().createStatement();


		ResultSet maxVal =
		    stmt.executeQuery("SELECT MAX(" + attName + ") FROM " + tableName);


		maxVal.next();
		max = maxVal.getDouble(1);

		ResultSet minVal =
		    stmt.executeQuery("SELECT MIN(" + attName + ") FROM " + tableName);

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
			System.out.println("EQ: "+eq);
			binTree.addBinFromEquation(attName, nameBuffer.toString(),  eq.toString(), true);
		    }
		    StringBuffer entryName = new StringBuffer(attName);
		    entryName.append(" : ");
		    entryName.append(nameBuffer.toString());
		    BinEntry be = new BinEntry(attName,
					       nameBuffer.toString(), NUMERIC);
		    //		    addItemToBinList(entryName.toString(), be);
		}
		catch(Exception dbe) {
		dbe.printStackTrace();
		}
		// increment the minimum
		min = upper;
	    }
	}


	//binTree.printAll();
	pushOutput(binTree, 0);
	pushOutput(vt, 1);
	pushOutput(tableName,2);


    }

    /*


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
					catch(BinNotFoundException e) {
						JOptionPane.showMessageDialog(this,
							"The bin did not exist!", "Error",
							JOptionPane.ERROR_MESSAGE);
					}
					catch(AttributeNotFoundException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
    */
		/**
			Add an item to the bin list
		*/
    /*   	void addItemToBinList(String entryName, BinEntry be) {
			binListModel.addElement(entryName);
			binEntries.put(entryName, be);
		}
    */
    /*	void clearNumericTextFields() {
			uniformRange.setText("");
			userSpecifiedNumeric.setText("");
			numericName.setText("");
		}

		void clearStringTextFields() {
			userSpecifiedText.setText("");
			textName.setText("");
		}
    */
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





