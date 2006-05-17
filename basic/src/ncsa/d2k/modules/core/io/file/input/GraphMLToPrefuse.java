package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import prefuse.data.*;
import prefuse.data.io.*;

public class GraphMLToPrefuse extends InputModule {

  public String[] getInputTypes() {
    return new String[] {
      "java.lang.String"
    };
  }

  public String[] getOutputTypes() {
    return new String[] {
      "prefuse.data.Graph"
    };
  }

  public String getInputInfo(int i) {
    if (i == 0) {
      return "File Name";
    }
    return null;
  }

  public String getOutputInfo(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    return null;
  }

  public String getInputName(int i) {
    if (i == 0) {
      return "File Name";
    }
    return null;
  }

  public String getOutputName(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    return null;
  }

  public String getModuleInfo() {
    return "Reads a GraphML file to a Prefuse graph.";
  }

  public void doit() throws Exception {
    String fn = (String)pullInput(0);

    GraphMLReader reader = new GraphMLReader();

    Graph graph = reader.readGraph(fn);
    pushOutput(graph, 0);
  }
}
