package ncsa.d2k.modules.core.io.sql;
/**
 * <p>Title: ResultSetTableModel
 * <p>Description: This class takes a JDBC ResultSet object and implements
 * the TableModel interface in terms of it so that a Swing JTable component
 * can display the contents of the ResultSet. </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: NCSA ALG</p>
 * @author Dora Cai
 * @version 1.0
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ResultSetTableModel implements TableModel{
    ResultSet results;             // The ResultSet data to interpret
    Vector vector;				   // The Vector data to interpet
    ResultSetMetaData metadata;    // Additional information about the results
    int numcols, numrows;          // How many rows and columns in the table


    /**
     * Constructor ResultSetTableModel()
     * 
     * This constructor creates a TableModel from a ResultSet.
     * 
     * @param <code>ResultSet</code> The data collection to display
     * 
     * @throws SQLException
     **/
    ResultSetTableModel(ResultSet results) throws SQLException {
	this.results = results;                 // Save the results
	metadata = results.getMetaData();       // Get metadata on them
	numcols = metadata.getColumnCount();    // How many columns?
	results.last();                         // Move to last row
	numrows = results.getRow();             // How many rows?
    }

    /**
     * Constructor ResultSetTableModel()
     * 
     * This constructor creates a TableModel from a ResultSet.
     * 
     * @param <code>Vector</code> The data collection to display
     * 
     * @throws SQLException
     **/

    ResultSetTableModel(Vector results) throws SQLException {
        vector = results;
        numcols = 1;    					// How many columns?
        numrows = vector.size();            // How many rows?
    }

    /**
     * Call this when done with the table model.  It closes the ResultSet and
     * the Statement object used to create it.
     **/
    public void close() {
      if (metadata != null) {
		try { 
			results.getStatement().close();
		}
		catch(SQLException e) {
			// ??
		}
      }
    }

    /**
     * protected Finalize()
     *  
     *  Automatically close when we're garbage collected 
     *  
     */
    protected void finalize() { 
    	close(); 
    }

    /**
     * Method getColumnCount()
     *  
     *  @return <code>int</code> number of columns from global field
     */
    public int getColumnCount() { 
    	return numcols; 
    }
    /**
     * Method getRowCount()
     *  
     *  @return <code>int</code> number of rows from global field
     */
    public int getRowCount() { 
    	return numrows; 
    }

    /**
     * Method getColumnName()
     *  
     *  @return <code>String</code> the name of column identified by index or "Name"
     */
    public String getColumnName(int column) {
      if (metadata == null) {
        return ("Name");
      }
      else 
      {
		try {
		    return metadata.getColumnLabel(column+1);
		} catch (SQLException e) { 
			return e.toString(); 
		}
      }
    }

    /**
     * Method getColumnClass()
     * 
     * This TableModel method specifies the data type for each column.
     * We could map SQL types to Java types, but for this example, we'll just
     * convert all the returned data to strings.
     * 
     * @param <code>int</code> column index to fetch Java class for 
     * 
     * @return <code>java.lang.Class</code> for column identified by index
     */
    public Class getColumnClass(int column) { return String.class; }

    /**
     * Method getValueAt().
     * 
     * This is the key method of TableModel: it returns the value at each cell
     * of the table.  We use strings in this case.  If anything goes wrong, we
     * return the exception as a string, so it will be displayed in the table.
     * Note that SQL row and column numbers start at 1, but TableModel column
     * numbers start at 0.
     * 
     * @param row The row index
     * @param column The column index
     * 
     * @return A object in the specified cell
     **/
    public Object getValueAt(int row, int column) {
      if (metadata != null) {
		try {
		    results.absolute(row+1);                // Go to the specified row
		    Object o = results.getObject(column+1); // Get value of the column
		    if (o == null) return null;
		    else return o.toString();               // Convert it to a string
		} catch (SQLException e) { return e.toString(); }
      }
      else 
      {
        Object o = vector.get(row);
        return o.toString();
      }
    }

    /**
     * Method isCellEditable.
     *    
     * Our table isn't editable
     * 
     * @return <code>boolean</code> always returns false
     */
    public boolean isCellEditable(int row, int column) { return false; }

    /**
     * Method setValueAt().
     *    
     * Our table isn't editable   
     * Since its not editable, we don't need to implement these methods
     */
    public void setValueAt(Object value, int row, int column) {}

    /**
     * Method addTableModelListener().
     *    
     * Our table isn't editable   
     * Since its not editable, we don't need to implement these methods
     */    
    public void addTableModelListener(TableModelListener l) {}

    /**
     * Method removeTableModelListener().
     *    
     * Our table isn't editable   
     * Since its not editable, we don't need to implement these methods
     */        
    public void removeTableModelListener(TableModelListener l) {}
}