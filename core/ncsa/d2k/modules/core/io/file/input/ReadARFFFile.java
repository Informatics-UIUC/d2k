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
        return "";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.ARFFFileParser"};
        return out;
    }

    public String getOutputInfo(int i) {
        return "";
    }

    public String getModuleInfo() {
        return "";
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