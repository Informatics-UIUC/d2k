//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import java.io.*;

/**
 * Create an ARFF File Parser for the specified file.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CreateARFFParser extends InputModule {

    public String[] getInputTypes() {
        String[] in = {"java.lang.String"};
        return in;
    }

    public String getInputInfo(int i) {
        return "The absolute path to an ARFF file.";
    }

    public String getInputName(int i) {
        return "File Name";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.ARFFFileParser"};
        return out;
    }

    public String getOutputName(int i) {
        return "File Parser";
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

    public String getModuleName() {
        return "Create ARFF File Parser";
    }

    public void doit() throws Exception {
        String fn = (String)pullInput(0);
        File file = new File(fn);
        if(!file.exists())
            throw new FileNotFoundException(getAlias()+" "+file+" did not exist.");
        ARFFFileParser arff = new ARFFFileParser(file);
        pushOutput(arff, 0);
    }
}