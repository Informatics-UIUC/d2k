package ncsa.d2k.modules.core.io.file.output;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

import java.io.*;

/**
   WriteVTToFile.java
   Write the contents of a Table to a flat file.
   @author David Clutter
*/
public class WriteVTToFile extends OutputModule
	implements HasNames, Serializable {

	transient String delimiter;

	boolean comma;
	boolean tab;
	boolean space;
	boolean useDataTypes;
	boolean useColumnLabels;

    public boolean getComma() {
    	return comma;
    }

    public void setComma(boolean b) {
    	comma = b;
    }

    public boolean getTab() {
    	return tab;
    }

    public void setTab(boolean b) {
    	tab = b;
    }

    public boolean getSpace() {
    	return space;
    }

    public void setSpace(boolean b) {
    	space = b;
    }

	public void setUseDataTypes(boolean b) {
		useDataTypes = b;
	}

	public boolean getUseDataTypes() {
		return useDataTypes;
	}

	public void setUseColumnLabels(boolean b) {
		useColumnLabels = b;
	}

	public boolean getUseColumnLabels() {
		return useColumnLabels;
	}

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
    	StringBuffer sb = new StringBuffer("Write the contents of a ");
		sb.append("Table to a flat file.  Can use space, comma, ");
		sb.append("or tab as a delimiter.  If useColumnLabels is set, ");
		sb.append("the first row of the file will be the column labels.");
		sb.append(" If useDataTypes is set, the data type of each row will ");
		sb.append(" be written.");
		return sb.toString();
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "writeVT";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
    	String []in = {"java.lang.String",
			"ncsa.d2k.modules.datatype.table.Table"};
		return in;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		return null;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
    	switch(i) {
			case 0: return "The name of the file to write.";
			case 1: return "The Table to write.";
			default: return "No such input.";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
       	switch(i) {
			case 0: return "fileName";
			case 1: return "table";
			default: return "No such input.";
		}
	}

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		return "No such output!";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "No such output!";
    }

    /**
    	Write the table to the file.
	*/
    public void doit() {
    	String fileName = (String)pullInput(0);
		Table vt = (Table)pullInput(1);
		FileWriter fw;
		String newLine = "\n";

		if(comma)
			delimiter = ",";
		if(tab)
			delimiter = "\t";
		if(space)
			delimiter = " ";

		try {
			fw = new FileWriter(fileName);

			// write the column labels
			if(useColumnLabels) {
				for(int i = 0; i < vt.getNumColumns(); i++) {
					String s = vt.getColumnLabel(i);
					fw.write(s, 0, s.length());
					if(i != (vt.getNumColumns() - 1))
						fw.write(delimiter.toCharArray(), 0, delimiter.length());
				}
				fw.write(newLine.toCharArray(), 0, newLine.length());
			}

			// write the datatypes.
			if(useDataTypes) {
				for(int i = 0; i < vt.getNumColumns(); i++) {
					String s = getDataType(vt.getColumn(i));
					fw.write(s, 0, s.length());
					if(i != (vt.getNumColumns() - 1))
						fw.write(delimiter.toCharArray(), 0, delimiter.length());
				}
				fw.write(newLine.toCharArray(), 0, newLine.length());
			}

			// write the actual data
			for(int i = 0; i < vt.getNumRows(); i++) {
				for(int j = 0; j < vt.getNumColumns(); j++) {
					String s = vt.getString(i, j);
					//System.out.println("s: "+s);
					fw.write(s, 0, s.length());
					if(j != (vt.getNumColumns() - 1) )
						fw.write(delimiter.toCharArray(), 0, delimiter.length());
				}
				fw.write(newLine.toCharArray(), 0, newLine.length());
			}
			fw.flush();
			fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
		Get the datatype of a column.
	*/
	public static final String getDataType(Column c) {
		if(c instanceof StringColumn)
			return "String";
		else if(c instanceof IntColumn)
			return "int";
		else if(c instanceof FloatColumn)
			return "float";
		else if(c instanceof DoubleColumn)
			return "double";
		else if(c instanceof LongColumn)
			return "long";
		else if(c instanceof ShortColumn)
			return "short";
		else if(c instanceof BooleanColumn)
			return "boolean";
		else if(c instanceof ObjectColumn)
			return "Object";
		else if(c instanceof ByteArrayColumn)
			return "byte[]";
		else if(c instanceof CharArrayColumn)
			return "char[]";
		else
			return "unknown";
	}
}
