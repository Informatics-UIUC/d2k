package ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * This module examines each input column and output column in the original
 * Table, and for each column that contains nominal values, it converts the
 * single column into multiple columns (of booleans or ints) - one for each
 * possible value of the attribute.  Columns that are not marked as input or
 * output will not be converted.
 *
 * For original Tables that are not Example Tables (no inputs/outputs
 * specified), all columns containing nominal values will be converted.
 *
 * The user can select whether the generated scalar columns are of type
 * boolean or int.
 */
public class ScalarizeNominals extends ncsa.d2k.core.modules.DataPrepModule
{

///////
// Property descriptions, variables, get, and set methods
///////

  /**
     Property description for the scalar column type specification
  */
  private static final PropertyDescription scalarTypeIsBooleanDescr =
    new PropertyDescription (
        "scalarTypeIsBoolean",
        "Use boolean type for new scalar columns",
        "Controls whether converted nominal columns will have scalar type " +
        "boolean (true), or type int (false). " );

  private boolean scalarTypeIsBoolean = true;
  public void setScalarTypeIsBoolean( boolean value )
  {
    scalarTypeIsBoolean = value;
  }
  public boolean getScalarTypeIsBoolean()
  {
    return scalarTypeIsBoolean;
  }

  /**
     Property description for the option that enables verbose output.
  */
  private static final PropertyDescription verboseOutputDescr =
    new PropertyDescription (
        "verboseOutput",
        "Generate verbose diagnostic output",
        "Controls whether verbose output will be genearted for debugging " +
        "purposes." );

  private boolean verboseOutput = false;
  public void setVerboseOutput( boolean value )
  {
    verboseOutput = value;
  }
  public boolean getverboseOutput() {
    return verboseOutput;
  }

  /**
     Returns an array of PropertyDescription objects,
     one object for each property in the class.
  */
  public final PropertyDescription[] getPropertiesDescriptions()
  {
    PropertyDescription[] propDescrs = { scalarTypeIsBooleanDescr,
                                         verboseOutputDescr };
    return propDescrs;
  }


///////
// Standard module methods
//////

  /**
     Return a description of the function of this module.
     @return A description of the function of the module.
  */
  public String getModuleInfo ()
  {
    String str = "<html> <head> </head> <body> ";
    str += "This module examines each input column and output column, and ";
    str += "for each column that contains nominal values, it converts the ";
    str += "single column into multiple columns (of booleans or ints) - one ";
    str += "for each possible value of the attribute.  Columns that are not ";
    str += "marked as input or output will not be converted. ";
    str += "<p>";
    str += "For tables that are not Example Tables (no inputs/outputs ";
    str += "specified), all columns containing nominal values will be ";
    str += "converted.";
    str += " </body></html>";
    return str;
  }

  /**
     Return the human readable name of the module.
     @return The human readable name of the module.
  */
  public String getModuleName()
  {
    return "ScalarizeNominals";
  }

  /**
     Returns an array of strings containing the data types of the module inputs.
     @return The data types of the module inputs.
  */
  public String[] getInputTypes()
  {
    String [] intypes =
  	       {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
    return intypes;
  }

  /**
     Returns an array of strings containing the datatypes of the module outputs.
     @return The data types of the module outputs.
  */
  public String[] getOutputTypes()
  {
    String [] outtypes =
               {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
    return outtypes;
  }

  /**
     Returns the description of a specific input.
     @param idx The index of the input.
     @return The description of the indexed input.
  */
  public String getInputInfo(int idx)
  {
    switch (idx) {
      case 0:
        return "The input example table with the inputs and outputs selected.";
      default:
        return "No such input.";
    }
  }

  /**
     Returns the human readable name of the specific input.
     @param idx The index of the input.
     @return The human readable name of the indexed input.
  */
  public String getInputName(int idx) {
    switch(idx) {
      case 0:
        return "Example table";
      default:
        return "No such input.";
    }
  }

  /**
     Returns the description of the specific output.
     @param idx The index of the output.
     @return The description of the indexed output.
  */
  public String getOutputInfo (int idx)
  {
    switch (idx) {
      case 0:
        String str = "The output example table with the inputs and outputs ";
        str += "that originally contained nominal values now represented by ";
        str += "additional columns containing scalars. ";
        return str;
      default:
        return "No such output";
    }
  }

  /**
     Returns the human readable name of the specific output.
     @param idx The index of the output.
     @return The human readable name of the indexed output.
  */
  public String getOutputName(int idx) {
    switch(idx) {
      case 0:
        return "Scalarized Table";
      default:
        return "No such output.";
    }
  }


///////////
//  WORK
///////////
  /**
     The doit() method does the real work of the module.
   */
  public void doit () throws Exception
  {
    // input table which may be an example table
    //
    TableImpl et = (TableImpl) this.pullInput(0);
    boolean isExampleTable = false;
    int [] inputs = null;
    int [] outputs = null;

    // if input table is an example table, set variables appropriately
    //
    if (et instanceof ExampleTable) {
      isExampleTable = true;
      inputs = ((ExampleTable)et).getInputFeatures ();
      outputs = ((ExampleTable)et).getOutputFeatures ();
    }

    // output example table and input/output feature variables
    //
    TableImpl tmpTbl =
	            (TableImpl) DefaultTableFactory.getInstance().createTable();
    ExampleTableImpl newet = (ExampleTableImpl)tmpTbl.toExampleTable();
    int newColIdx = 0;
    ArrayList newInputs = new ArrayList ();
    ArrayList newOutputs = new ArrayList ();
    int iIndex = 0;
    int oIndex = 0;

    // Whether to convert nominals is based on the table type and, for
    // example tables, on whether or not column is an input or output feature.
    // We initialize our vars as if it is not an example table and only
    // perform checks/changes when it is.
    //
    boolean performConversion = false;
    boolean performConversionIfNominal = true;
    boolean colIsInput = false;
    boolean colIsOutput = false;
    int numNewCols;

    // Loop through the columns and convert or copy into new table
    //
    for (int i = 0; i < et.getNumColumns (); i++) {

      // For example tables, decide if we should convert nominals based
      // on whether it's an input or output feature column
      //
      if (isExampleTable) {
        if (iIndex < inputs.length && i == inputs[iIndex]) {
          performConversionIfNominal = true;
          colIsInput = true;
          iIndex++;
        } else if (oIndex < outputs.length && i == outputs[oIndex]) {
          performConversionIfNominal = true;
          colIsOutput = true;
          oIndex++;
        } else {
          performConversionIfNominal = false;
        }
      }

      Column col = et.getColumn(i);

      // If we are to convert nominals, is this column a nominal?
      // We use a more restricted test for nominals than the
      // isColumnNominal() test because we use the column value
      // to construct a name for the new column.   For some
      // column types that are nominals, this isn't possible.
      //
      if ( performConversionIfNominal ) {
        if (col instanceof StringColumn
            || col instanceof ByteArrayColumn
            || col instanceof CharArrayColumn) {
          performConversion = true;
        }
      }

      // If conversion is to be performed, do it and add new cols to new
      // example table.  Otherwise, copy existing column.
      //
      if (performConversion) {
  	Column [] addCols = this.getScalarColumns (col, et.getColumnLabel(i));
        numNewCols = addCols.length;

        for (int j = 0; j < numNewCols; j++) {
          newet.addColumn (addCols[j]);
  	}
        
        performConversion = false;

      } else {
        numNewCols = 1;
        newet.addColumn (col);
      }

      // For input and output feature columns, make entries in new
      // indices that reflect the actions just performed.   In all cases,
      // leave newColIdx pointing at next fee slot in newet.
      //
      if ( colIsInput ) {
        for (int j = 0; j < numNewCols; j++) {
  	    newInputs.add ( new Integer(newColIdx++) );
  	}
        colIsInput = false;
      } else if ( colIsOutput ) {
        for (int j = 0; j < numNewCols; j++) {
  	    newOutputs.add ( new Integer(newColIdx++) );
  	}
        colIsOutput = false;
      } else {
        newColIdx += numNewCols;
      }
    }

    // reconstruct the inputs and outputs.
    //
    int howMany = newInputs.size ();
    if (howMany > 0) {
      int [] newinputs = new int [howMany];
      for (int i = 0 ; i < howMany ; i++) {
        newinputs[i] = ((Integer)newInputs.get (i)).intValue ();
        if (verboseOutput ) {
          System.out.println ("Input column : " +
                              ((Integer)newInputs.get (i)).intValue ());
        }
      }
      newet.setInputFeatures (newinputs);
    }

    howMany = newOutputs.size ();
    if (howMany > 0) {
      int [] newoutputs = new int [howMany];
      for (int i = 0 ; i < howMany ; i++) {
        newoutputs[i] = ((Integer)newOutputs.get (i)).intValue ();
        if (verboseOutput ) {
          System.out.println ("Output column : " +
                              ((Integer)newOutputs.get (i)).intValue ());
        }
      }
      newet.setOutputFeatures (newoutputs);
    }

    this.pushOutput (newet, 0);
  }

  /**
     This method converts col, containing nominal values,
     into an array of Columns containing the scalar values.
     @param col The column containing the nominal data.
     @param label The label associated with the column being converted;
                  This is used to construct the new column labels.
     @return An array of Columns containing the scalar values.
  */
  public Column [] getScalarColumns (Column col, String label)
  {
    // First, find all the unique values of the column.
    int rowCnt = col.getNumRows ();
    HashMap vals = new HashMap ();
    int ii = 0;

    for (int i = 0; i < rowCnt; i++) {
      String tt = col.getString (i);

      // If a  new value has been found, add it to the ones we know about
      //
      if (vals.get (tt) == null) {
  	vals.put (tt, new Integer (ii++));
      }
    }

    // Make an array of the different values for this field. Basically
    // here we just ascertain the order of the keys.
    //
    int numDiffValues = vals.size ();
    String [] possibleValues = new String [numDiffValues];
    Object [] keys = (Object []) vals.keySet().toArray ();

    for (int i = 0; i < numDiffValues; i++) {
      String ck = (String) keys[i];
      possibleValues [((Integer) vals.get (ck)).intValue ()] = ck;
    }

    // Create an array corresponding to the scalar values and rows.
    // Set the entries that "match" the original column values.
    // Decide on boolean or int type based on property
    //
    boolean [][] bsclr;
    int [][] isclr;

    if ( scalarTypeIsBoolean ) {
      bsclr = new boolean [numDiffValues][rowCnt];
      isclr = new int[0][0];		// initialize to keep compiler happy
      for (int i = 0; i < rowCnt; i++) {
        bsclr [((Integer)vals.get (col.getString (i))).intValue ()][i] = true;
      }
    } else {
      bsclr = new boolean[0][0];	// initialize to keep compiler happy
      isclr = new int [numDiffValues][rowCnt];
      for (int i = 0; i < rowCnt; i++) {
        isclr [((Integer)vals.get (col.getString (i))).intValue ()][i] = 1;
      }
    }

    // Now convert the array columns to the appropriate types of columns
    // and use the original column label combined with the column value
    // to create new labels.
    //
    Column [] colVect;

    if ( scalarTypeIsBoolean ) {
      colVect = new BooleanColumn[numDiffValues];
      for (int i = 0; i < numDiffValues; i++) {
        colVect[i] = new BooleanColumn (bsclr [i]);
      }
    } else {
      colVect = new IntColumn[numDiffValues];
      for (int i = 0; i < numDiffValues; i++) {
        colVect[i] = new IntColumn (isclr [i]);
      }
    }

    for (int i = 0; i < numDiffValues; i++) {
      String lbl =label+"="+possibleValues[i];
      colVect[i].setLabel (lbl);
      if (verboseOutput) {
        System.out.println ("LABEL : "+lbl);
      }
    }

    return colVect;
  }

}
