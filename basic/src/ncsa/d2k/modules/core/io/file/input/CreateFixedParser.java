package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;

/**
 * Create a new fixed file reader.
 */
public class CreateFixedParser extends InputModule {

    public String[] getInputTypes() {
        String[] in = { "java.lang.String",
												"ncsa.d2k.modules.core.datatype.table.Table"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.FixedFileParser"};
        return out;
    }

    public String getInputInfo(int i) {
        switch(i) {
            case(0):
                return "The absolute path to the fixed file.";
            case(1):
                return "A table of metadata describing the fixed file.";
            default:
                return "";
        }
    }

    public String getInputName(int i) {
        switch(i) {
            case(0):
                return "File Name";
            case(1):
                return "Header Table";
            default:
                return "";
        }
    }

    public String getOutputInfo(int i) {
        return "A FixedFileParser for the specified fixed format file.";
    }

    public String getOutputName(int i) {
        return "Fixed File Parser";
    }

    public String getModuleInfo() {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("This module creates a parser for a fixed-format file.");
        sb.append("</p><p>Detailed Description: ");
        sb.append("Given a file name and a \"header\" Table of metadata ");
        sb.append("describing the file's format, this module creates a ");
        sb.append("parser for that file. This parser can then be passed to ");
        sb.append("ParseFileToTable, for example, to read the file into a Table.");
        sb.append("The table of metadata has five columns with the following labels:");
        sb.append("LABEL, TYPE, START, STOP, LENGTH not necessarily in this order.");
        sb.append("The START column needs to be always present. Only one of the ");
	sb.append("STOP or LENGTH columns need to be present.");
	sb.append("The STOP column will be ignored if LENGTH column is present.");
	sb.append("Columns start from 1 not from zero. Start and stop columns are inclusive,");
	sb.append("for a column with one character the start and stop will be equal. ");
        sb.append("</p><p>Data Type Restrictions: ");
        sb.append("The specified file must be in fixed format.");
        sb.append("</p><p>Data Handling: ");
        sb.append("This module does not destroy or modify its input data.");
        sb.append("</p>");
        return sb.toString();
    }

    public String getModuleName() {
       return "Create Fixed-format Parser";
    }

    public void doit() throws Exception {
        String fileName = (String)pullInput(0);
        Table meta = (Table)pullInput(1);

        File file = new File(fileName);
        if(!file.exists())
            throw new FileNotFoundException(file+" did not exist.");

        FixedFileParser ff = new FixedFileParser(file, meta);
        pushOutput(ff, 0);
    }


}
// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/16/03 - Anca started QA process. Added fixed format description.
//


// 2/18/03 - checked into basic.
// END QA Comments


/* Fixed Format Description
 Column start -inclusive, Column End inclusive too. Means column start can equal column length
 for fields of length 1.
 Column's start cannot be zero! Column begin only from 1.
*/