package ncsa.d2k.modules.core.io.sql;

import ncsa.d2k.infrastructure.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.sql.*;
import java.util.Vector;


public class SQLMeta2VT extends DataPrepModule
{
	public String getOutputInfo (int index) {
	    String[] outputDescriptions = {
		"Pass this on to the next module that needs a connection to this data source.",
		"Table containing only metadata related to the SQL table "
	    };
	    return outputDescriptions[index];
	}

    public String getInputInfo (int index) {
	String[] inputDescriptions = {
	    "Connection to the database ",
	    " Name of the SQL table used"
	};
	return inputDescriptions[index];
    }

    public String getModuleInfo () {
	String text = "Retrieves  SQL table metadata ";
	return text;
    }

    public String[] getInputTypes () {
	String[] temp = {"ncsa.d2k.modules.core.io.sql.ConnectionWrapper",
			 "java.lang.String"};
	return temp;
    }

    public String[] getOutputTypes () {
	String[] temp = {
	    //	    "ncsa.d2k.modules.io.input.sql.ConnectionWrapper",
	    "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
	return temp;
    }

    protected void doit ()  throws Exception
    {
	ConnectionWrapper cw = (ConnectionWrapper) this.pullInput (0);
	String tableName = (String) this.pullInput(1);
	ResultSet result;
	TableImpl vt = null;
	try {
	    Statement stmt
		= ((Connection)cw.getConnection()).createStatement();
	    result = stmt.executeQuery("SELECT * FROM " + tableName
					+ " WHERE ROWNUM=1");


	    ResultSetMetaData columnMetadata
		= result.getMetaData();

	    int numColumns = columnMetadata.getColumnCount();
	    vt = (TableImpl)DefaultTableFactory.getInstance().createTable(numColumns);
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
			col.setLabel(columnName);
			/*
			result = stmt.executeQuery("SELECT MAX(" + columnName
						   + ") FROM " + tableName);
			result.next();
			Double max = result.getDouble(1);
			result = stmt.executeQuery("SELECT MIN(" + columnName
						   + ") FROM " + tableName);
			result.next();
			Double min = result.getDouble(1);

			col.setMax(max);
			col.setMin (min);
			*/
			vt.setColumn(col, i);
		    }
		else if( type == java.sql.Types.CHAR ||
			 type == java.sql.Types.VARCHAR ||
			 type == java.sql.Types.LONGVARCHAR ||
			 type == java.sql.Types.DATE)
		    {
			StringColumn col = new StringColumn();
			col.setLabel(columnMetadata.getColumnLabel(i+1));
			vt.setColumn(col, i);
		    }
		else {
		    ObjectColumn col = new ObjectColumn();
		    col.setLabel(columnMetadata.getColumnLabel(i+1));
		    vt.setColumn(col, i);
		}

	    }

	} catch (SQLException e) { System.out.println( e); }
	catch (ClassNotFoundException ne) { System.out.println( ne); }
	catch (InstantiationException ee) { System.out.println( ee); }
	catch (IllegalAccessException nie) { System.out.println( nie); };

	//this.pushOutput (cw, 0);
	this.pushOutput (vt, 0);
    }

}
