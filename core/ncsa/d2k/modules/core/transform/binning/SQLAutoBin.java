package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import java.text.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.io.sql.*;
import java.sql.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class SQLAutoBin extends AutoBin {

	public String[] getInputTypes() {
		String[] in =
			{
				"ncsa.d2k.modules.core.io.sql.DBConnection",
				"java.lang.String",
				"ncsa.d2k.modules.core.datatype.table.ExampleTable",
				"java.lang.String" };
		return in;
	}

	/**
	* Get the name of the input parameter
	* @param i is the index of the input parameter
	* @return Name of the input parameter
	*/
	public String getInputName(int i) {
		switch (i) {
			case 0 :
				return "DatabaseConnection";
			case 1 :
				return "DatabaseTableName";
			case 2 :
				return "MetaDataExampleTable";
			case 3 :
				return "QueryCondition";
			default :
				return "No such input";
		}
	}

	/**
	* Get the data types for the output parameters
	* @return A object of class BinTransform
	*/
	public String[] getOutputTypes() {
		String[] types =
			{ "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform" };
		return types;
	}

	/**
	 * Get input information
	 * @param i is the index of the input parameter
	 * @return A description of the input parameter
	 */
	public String getInputInfo(int i) {
		switch (i) {
			case 0 :
				return "The database connection.";
			case 1 :
				return "The selected table from a database.";
			case 2 :
				return "ExampleTable containing the names of the input/output features";
			case 3 :
				return "The query conditions.";
			default :
				return "No such input";
		}
	}

	/**
	 * Get the name of the output parameters
	 * @param i is the index of the output parameter
	 * @return Name of the output parameter
	 */
	public String getOutputName(int i) {
		switch (i) {
			case 0 :
				return "Bin Transform";
			default :
				return "no such output!";
		}
	}

	/**
	 * Get output information
	 * @param i is the index of the output parameter
	 * @return A description of the output parameter
	 */
	public String getOutputInfo(int i) {
		switch (i) {
			case 0 :
				return "A BinTransform object that contains column_numbers, names and lables";
			default :
				return "No such output";
		}
	}

	public String getModuleName() {
		return "SQL Auto Discretization";
	}

	public String getModuleInfo() {
		String s =
			"<p>Overview: Automatically discretize scalar data for the "
				+ "Naive Bayesian classification model. "
				+ "<p>Detailed Description: Given a database connection, a database table name "
				+ "a query condition, and a metadata ExampleTable containing the names of columns "
				+ "define the bins for each scalar input column.  Nominal input columns will have a bin "
				+ "defined for each unique value in the column."
				+ "<p>Data Type Restrictions: This module does not modify the input data."
				+ "<p>Data Handling: When binning scalar columns by the number of bins, "
				+ "the maximum and minimum values of each column must be found.  When "
				+ "binning scalar columns by weight, group by statements are used.";
		return s;
	}

	DBConnection conn;
	String tableName, whereClause;
	ExampleTable tbl;
    NumberFormat nf;
    
	public void doit() throws Exception {

		conn = (DBConnection) pullInput(0);
		tableName = (String) pullInput(1);
		tbl = (ExampleTable) pullInput(2);
		whereClause = (String) pullInput(3);
		nf = NumberFormat.getInstance();
		  nf.setMaximumFractionDigits(3);
		int type = getBinMethod();

		BinDescriptor[] bins;
		if (type == 0) {
			int number = getNumberOfBins();
			if (number < 0)
				throw new Exception(
					getAlias() + ": Number of bins not specified!");
			bins = numberOfBins(number);
		} else {
			int weight = getBinWeight();
			bins = sameWeight(weight);
		}

		BinTransform bt = new BinTransform(bins, false);

		pushOutput(bt, 0);
		//pushOutput(et, 1);
	}

	protected BinDescriptor[] numberOfBins(int num) throws Exception {

		List bins = new ArrayList();
		int[] inputs = tbl.getInputFeatures();
		int[] outputs = tbl.getOutputFeatures();
		boolean colTypes[] = getColTypes(inputs.length);

		for (int i = 0; i < inputs.length; i++) {
			boolean isScalar = colTypes[i];
			// if it is scalar, find the max and min and create equally-spaced bins
			//System.out.println("scalar ? " + i + " " +  isScalar );
			if (isScalar) {
				// find the maxes and mins

				double minMaxTotal[] = getMMTValues(i);

				double max = minMaxTotal[0];
				double min = minMaxTotal[1];
				double[] binMaxes = new double[num - 1];
				double interval = (max - min) / (double) num;
				binMaxes[0] = min + interval;
                //System.out.println("binmaxes[0] " + binMaxes[0]);
                
                // add the first bin manually
				BinDescriptor bd =
					BinDescriptorFactory.createMinNumericBinDescriptor(inputs[i], binMaxes[0],nf,tbl);
				bins.add(bd);

				// now add the rest
				for (int j = 1; j < binMaxes.length; j++) {
					binMaxes[j] = binMaxes[j - 1] + interval;
					bd =
						BinDescriptorFactory.createNumericBinDescriptor(
							inputs[i],
							binMaxes[j - 1],
							binMaxes[j],nf,tbl);
					bins.add(bd);
				}
				//System.out.println("binmaxes[length-1] " + binMaxes[binMaxes.length-1]);
				// now add the last bin
				bd =
					BinDescriptorFactory.createMaxNumericBinDescriptor(
						inputs[i],
						binMaxes[binMaxes.length - 1],nf,tbl);
				bins.add(bd);
			}

			// if it is nominal, create a bin for each unique value.
			else {
				HashSet vals = uniqueValues(i);
				Iterator iter = vals.iterator();
				while (iter.hasNext()) {
					String item = (String) iter.next();
					String[] st = new String[1];
					st[0] = item;
					BinDescriptor bd =
						new TextualBinDescriptor(
							i,
							item,
							st,
							tbl.getColumnLabel(i));
					bins.add(bd);
				}
			}
		}

		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);

		}
		return bn;
	}

	protected BinDescriptor[] sameWeight(int weight) throws Exception {
		List bins = new ArrayList();
		int[] inputs = tbl.getInputFeatures();
		int[] outputs = tbl.getOutputFeatures();
		boolean colTypes[] = getColTypes(inputs.length);

		for (int i = 0; i < inputs.length; i++) {
			// if it is scalar, get the data and sort it.  put (num) into each bin,
			// and create a new bin when the last one fills up
			boolean isScalar = colTypes[i];
			System.out.println("scalar ? " + i + " " + isScalar);
			if (isScalar) {
				try {
					Double db1 = null;
					ArrayList list = new ArrayList();
					String colName = tbl.getColumnLabel(i);
					Connection con = conn.getConnection();
					String queryStr =
						"select "
							+ colName
							+ ", count("
							+ colName
							+ ") from "
							+ tableName
							+ " group by "
							+ colName;
					Statement stmt = con.createStatement();
					ResultSet groupSet = stmt.executeQuery(queryStr);
					int itemCnt = 0;
					while (groupSet.next()) {
						itemCnt += groupSet.getInt(2);
						db1 = new Double(groupSet.getDouble(1));
						if (itemCnt >= (weight - 1)) {
							// itemCnt >= specified weight, add the value to the list
							list.add(db1);
							// reset itemCnt
							itemCnt = 0;
						}
					}
					// put anything left in a bin
					if (itemCnt > 0)
						list.add(db1);

					double[] binMaxes = new double[list.size()];
					for (int j = 0; j < binMaxes.length; j++)
						binMaxes[j] = ((Double) list.get(j)).doubleValue();
					// add the first bin manually
					BinDescriptor nbd =
						BinDescriptorFactory.createMinNumericBinDescriptor(
							i,
							binMaxes[0],
							nf,
							tbl);
					bins.add(nbd);

					for (int j = 1; j < binMaxes.length; j++) {
						// now create the BinDescriptor and add it to the bin list
						nbd =
							BinDescriptorFactory.createNumericBinDescriptor(
								i,
								binMaxes[j - 1],
								binMaxes[j],
								nf,
								tbl);
						bins.add(nbd);
					}
					// now add the last bin
					nbd =
						BinDescriptorFactory.createMaxNumericBinDescriptor(
							i,
							binMaxes[binMaxes.length - 1],
							nf,
							tbl);
					bins.add(nbd);
					stmt.close();
				} catch (Exception e) {
					/*    JOptionPane.showMessageDialog(msgBoard,
					          e.getMessage(), "Error",
					          JOptionPane.ERROR_MESSAGE); */
					System.out.println(
						"Error occured in addFromWeight." + e.getMessage());
				}
			} else {
				HashSet vals = uniqueValues(i);
				Iterator iter = vals.iterator();
				while (iter.hasNext()) {
					String item = (String) iter.next();
					String[] st = new String[1];
					st[0] = item;
					BinDescriptor bd =
						new TextualBinDescriptor(
							i,
							item,
							st,
							tbl.getColumnLabel(i));
					bins.add(bd);
				}
			}
		}

		BinDescriptor[] bn = new BinDescriptor[bins.size()];
		for (int i = 0; i < bins.size(); i++) {
			bn[i] = (BinDescriptor) bins.get(i);

		}
		return bn;

		//return bt;
	}

	/** verify whether the column is a numeric column
	         *  @return a boolean array, numeric columns are flaged as true, and
	         *          categorical columns are flaged as false.
	         */
	public boolean[] getColTypes(int len) {
		boolean colTypes[] = new boolean[len];
		try {
			Connection con = conn.getConnection();
			DatabaseMetaData metadata = con.getMetaData();
			String[] names = { "TABLE" };
			ResultSet tableNames =
				metadata.getTables(null, "%", tableName, names);
			while (tableNames.next()) {
				ResultSet columns =
					metadata.getColumns(
						null,
						"%",
						tableNames.getString("TABLE_NAME"),
						"%");
				while (columns.next()) {
					String columnName = columns.getString("COLUMN_NAME");
					String dataType = columns.getString("TYPE_NAME");
					for (int col = 0; col < len; col++) {
						if (tbl.getColumnLabel(col).equals(columnName)) {
							if (dataType.equals("NUMBER")
								|| dataType.equals("INTEGER")
								|| dataType.equals("FLOAT")
								|| dataType.equals("NUMERIC")) {
								colTypes[col] = true;
							} else {
								colTypes[col] = false;
							}
							break;
						}
					}
				}
			}
			return colTypes;
		} catch (Exception e) {
			/*  JOptionPane.showMessageDialog(msgBoard,
			        e.getMessage(), "Error",
			        JOptionPane.ERROR_MESSAGE); */
			System.out.println("Error occured in getColTypes.");
			return null;
		}
	}

	public double[] getMMTValues(int col) {
		double minMaxTotal[] = new double[3];
		try {
			String colName = tbl.getColumnLabel(col);
			Connection con = conn.getConnection();
			String queryStr =
				"select min("
					+ colName
					+ "), max("
					+ colName
					+ "), sum("
					+ colName
					+ ") from "
					+ tableName;
			Statement stmt = con.createStatement();
			ResultSet totalSet = stmt.executeQuery(queryStr);
			totalSet.next();
			minMaxTotal[0] = totalSet.getDouble(1);
			minMaxTotal[1] = totalSet.getDouble(2);
			minMaxTotal[2] = totalSet.getDouble(3);
			stmt.close();
		} catch (Exception e) {
			/* JOptionPane.showMessageDialog(msgBoard,
			       e.getMessage(), "Error",
			       JOptionPane.ERROR_MESSAGE); */
			System.out.println("Error occured in getMMTValues.");
		}
		return minMaxTotal;

	}

	/** find unique values in a column
	 *  @param col the column to check for
	 *  @return a HashSet object that stores all unique values
	 */
	private HashSet uniqueValues(int col) {
		// count the number of unique items in this column
		HashSet set = new HashSet();
		try {
			String colName = tbl.getColumnLabel(col);
			Connection con = conn.getConnection();
			String queryStr =
				"select distinct " + colName + " from " + tableName;
			Statement stmt = con.createStatement();
			ResultSet distinctSet = stmt.executeQuery(queryStr);
			while (distinctSet.next()) {
				set.add(distinctSet.getString(1));
			}
			stmt.close();
			return set;
		} catch (Exception e) {
			/* JOptionPane.showMessageDialog(msgBoard,
			         e.getMessage(), "Error",
			         JOptionPane.ERROR_MESSAGE); */
			System.out.println("Error occoured in uniqueValues.");
			return null;
		}
	}




}
