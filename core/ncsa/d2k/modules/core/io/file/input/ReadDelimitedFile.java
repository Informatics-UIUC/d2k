//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.io.*;
import java.util.*;

/**
 * Create a DelimitedFileReader for a file.
 */
public class ReadDelimitedFile extends InputModule {

    private int labelsRow = 0;
    public int getLabelsRow() {
        return labelsRow;
    }
    public void setLabelsRow(int i) {
        labelsRow = i;
    }

    private int typesRow = 1;
    public int getTypesRow() {
        return typesRow;
    }
    public void setTypesRow(int i) {
        typesRow = i;
    }

    public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[2];
        retVal[0] = new PropertyDescription("labelsRow", "Labels Row Index",
            "This is the index of the labels row in the file, or -1 if there is no labels row.");
        retVal[1] = new PropertyDescription("typesRow", "Types Row Index",
            "This is the index of the types row in the file, or -1 if there is no types row.");
        return retVal;
    }

    /*private int inOutRow = -1;
    public int getInOutRow() {
        return inOutRow;
    }
    public void setInOutRow(int i) {
        inOutRow = i;
    }*/

    /*private int nomScalarRow = -1;
    public int getNomScalarRow() {
        return nomScalarRow;
    }
    public void setNomScalarRow(int i) {
        nomScalarRow = i;
    }*/

    public String[] getInputTypes() {
        String[] in = {"java.lang.String"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.DelimitedFileParser"};
        return out;
    }

    public String getInputInfo(int i) {
        return "The absolute path to the delimited file.";
    }

    public String getOutputInfo(int i) {
        return "A DelimitedFileParser for the specified file.";
    }

    public String getInputName(int i) {
        return "file";
    }

    public String getOutputName(int i) {
        return "fileparser";
    }

    public String getModuleInfo() {
        return "Create a DelimitedFileReader for the specified file.  Properties: "+
                " labelsRow: the row of the file that contains labels, indexed from "+
                " zero.  If the file does not contain column labels, this should be "+
                " a negative number.  typesRow: the row of the file that contains types, "+
                " indexed from zero.  If the file does not contain column types, this "+
                " should be a negative number.";
    }

    public String getModuleName() {
        return "ReadDelimitedFile";
    }

    public void doit() throws Exception {
        String fn = (String)pullInput(0);
        File file = new File(fn);
        if(!file.exists())
            throw new FileNotFoundException(getAlias()+": "+file+" did not exist.");
        DelimitedFileParser df = new DelimitedFileParser(file, labelsRow, typesRow);
        pushOutput(df, 0);
    }
}