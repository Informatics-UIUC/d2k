//package ncsa.d2k.modules.projects.clutter.rdr;
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
        // return "Create a FixedFileParser for the specified file.";
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("This module creates a parser for a fixed-format file.");
        sb.append("</p><p>Detailed Description: ");
        sb.append("Given a file name and a \"header\" Table of metadata ");
        sb.append("describing the file's format, this module creates a ");
        sb.append("parser for that file. This parser can then be passed to ");
        sb.append("ReadFileToTable, for example to read the file into a Table.");
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