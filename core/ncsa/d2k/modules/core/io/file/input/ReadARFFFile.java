//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import java.io.*;

public class ReadARFFFile extends InputModule {

    public String[] getInputTypes() {
        String[] in = {"java.lang.String"};
        return in;
    }

    public String getInputInfo(int i) {
        return "The absolute path to an ARFF file.";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.ARFFFileParser"};
        return out;
    }

    public String getOutputInfo(int i) {
        return "An ARFF File Parser.";
    }

    public String getModuleInfo() {
        StringBuffer s = new StringBuffer("<p>Overview: ");
        s.append("This module creates an ARFFFileParser for the specified file. ");
        s.append("<p>DetailedDescription: ");
        s.append("ARFFFileParser is used to read data from an ARFF file. ");
        s.append("<p>Data Type Restrictions: ");
        s.append("The input to this module must be an ARFF file.");
        s.append("<p>Data Handling: ");
        s.append("The module does not destroy or modify the input data.");
        return s.toString();
    }

    public void doit() throws Exception {
        String fn = (String)pullInput(0);
        File file = new File(fn);
        if(!file.exists())
            throw new FileNotFoundException(file+" did not exist.");
        ARFFFileParser arff = new ARFFFileParser(file);
        pushOutput(arff, 0);
    }
}