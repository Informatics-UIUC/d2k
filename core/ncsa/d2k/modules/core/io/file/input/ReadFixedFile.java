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
public class ReadFixedFile extends InputModule {

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
                return "filename";
            case(1):
                return "header";
            default:
                return "";
        }
    }

    public String getOutputInfo(int i) {
        return "A FixedFileParser for the specified fixed format file.";
    }

    public String getOutputName(int i) {
        return "FixedFileParser";
    }

    public String getModuleInfo() {
        return "Create a FixedFileParser for the specified file.";
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