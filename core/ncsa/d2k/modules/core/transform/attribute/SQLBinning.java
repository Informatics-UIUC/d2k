package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import java.sql.*;
import ncsa.d2k.modules.core.io.sql.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
   SQLBinningModule.java
   @author David Clutter
*/
public class SQLBinning extends DataPrepModule {

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Takes a Coonection, SQL table name, BinTree and an ExampleTable and     classifies all input variables.  </body></html>";
	}

	/**
	   Return the name of this module.
	   @return The name of this module.
	*/
	public String getModuleName() {
		return "binningMod";
	}

	/**
	   Return a String array containing the datatypes the inputs to this
	   module.
	   @return The datatypes of the inputs.
	*/
	public String[] getInputTypes() {
		String[] types =
			{
				"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
				"java.lang.String",
				"ncsa.d2k.modules.core.datatype.BinTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return types;
	}

	/**
	   Return a String array containing the datatypes of the outputs of this
	   module.
	   @return The datatypes of the outputs.
	*/
	public String[] getOutputTypes() {
		String[] types =
			{
				"ncsa.d2k.modules.core.datatype.BinTree",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable" };
		return types;
	}

	/**
	   Return a description of a specific input.
	   @param i The index of the input
	   @return The description of the input
	*/
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "A SQL connection to the database";
			case 1 :
				return "table name ";
			case 2 :
				return "An empty BinTree.";
			case 3 :
				return "An ExampleTable.";
			default :
				return "No such input";
		}
	}

	/**
	   Return the name of a specific input.
	   @param i The index of the input.
	   @return The name of the input
	*/
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "binTree.";
			case 1 :
				return "exampleTable.";
			case 2 :
				return "No such input";
			case 3 :
				return "No such input";
			default :
				return "NO SUCH INPUT!";
		}
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "The full BinTree.";
			case 1 :
				return "The ExampleTable, unchanged";
			default :
				return "No such output";
		}
	}

	/**
	   Return the name of a specific output.
	   @param i The index of the output.
	   @return The name of the output
	*/
	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "binTree.";
			case 1 :
				return "exampleTable";
			default :
				return "NO SUCH OUTPUT!";
		}
	}

	/**
	   Perform the calculation.
	*/
	public void doit() {
		ConnectionWrapperImpl conn = (ConnectionWrapperImpl) pullInput(0);
		String tableName = (String) pullInput(1);
		BinTree bt = (BinTree) pullInput(2);
		ExampleTable vt = (ExampleTable) pullInput(3);

		int[] ins = vt.getInputFeatures();
		int[] out = vt.getOutputFeatures();

		// we only support one out variable..
		int classColumn = out[0];
		String classLabel = vt.getColumnLabel(classColumn);
		int totalClassified = 0;
		int classTotal;
		int binListTotal;

		long startTime = System.currentTimeMillis();

		// get sql counts and set the tallys
		String cn[] = bt.getClassNames();
		String an[] = bt.getAttributeNames();

		try {
			Statement stmt = conn.getConnection().createStatement();
			for (int i = 0; i < cn.length; i++) {
				classTotal = 0;

				for (int j = 0; j < an.length; j++) {
					String[] bn = bt.getBinNames(cn[i], an[j]);
					binListTotal = 0;

					for (int k = 0; k < bn.length; k++) {

						BinTree.ClassTree ct =
							(BinTree.ClassTree) bt.get(cn[i]);
						BinTree.ClassTree.BinList bl =
							(BinTree.ClassTree.BinList) ct.get(an[j]);
						BinTree.ClassTree.Bin b =
							(BinTree.ClassTree.Bin) bl.get(bn[k]);
						String condition = b.getCondition(an[j]);


						//String condition = bt.getCondition(cn[i],an[j],bn[k]);
						//System.out.println("cn, an, bn, BIN Condition: "
						//         + cn[i] + " " + an[j] + " "
						//                 + bn[k] + " " + condition);

						String query =
							"SELECT COUNT(*)  FROM "
								+ tableName
								+ " WHERE "
								+ classLabel
								+ " = \'"
								+ cn[i]
								+ "\' AND "
								+ condition;
						// System.out.println("BIN Query: "+ query);
						ResultSet count =
							stmt.executeQuery(
								"SELECT COUNT(*)  FROM "
									+ tableName
									+ " WHERE "
									+ classLabel
									+ " = \'"
									+ cn[i]
									+ "\' AND "
									+ condition);

						while (count.next()) {
							int s = count.getInt(1);
							b.setTally(s);
							totalClassified = totalClassified + s;
							classTotal = classTotal + s;
							binListTotal = binListTotal + s;
						  boolean debug = true;
							if (debug) System.out.println("COUNT(class:" + cn[i] + ",att:" +an[j] + " )=" + s);

						}
						bl.setTotal(binListTotal);
						count.close();
					}
				}
				bt.setClassTotal(cn[i], classTotal);
			}

			bt.setTotalClassified(totalClassified);
		} catch (SQLException e) {
			System.out.println(e);
		} catch (ClassNotFoundException ne) {
			System.out.println(ne);
		} catch (InstantiationException ee) {
			System.out.println(ee);
		} catch (IllegalAccessException nie) {
			System.out.println(nie);
		};

		long endTime = System.currentTimeMillis();
		System.out.println("time in msec " + (endTime - startTime));

		/*
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
			}
		*/

		pushOutput(bt, 0);
		pushOutput(vt, 1);
	}
}
