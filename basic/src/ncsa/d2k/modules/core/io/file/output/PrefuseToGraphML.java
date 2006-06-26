package ncsa.d2k.modules.core.io.file.output;

import ncsa.d2k.core.modules.*;
import prefuse.data.*;
import prefuse.data.io.*;

public class PrefuseToGraphML extends OutputModule {

  public String[] getInputTypes() {
    return new String[] {
      "prefuse.data.Graph",
      "java.lang.String"
    };
  }

  public String[] getOutputTypes() {
    return null;
  }

  public String getInputInfo(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    else if (i == 1) {
      return "File Name";
    }
    return null;
  }

  public String getOutputInfo(int i) {
    return null;
  }

  public String getInputName(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    else if (i == 1) {
      return "File Name";
    }
    return null;
  }

  public String getOutputName(int i) {
    return null;
  }

  public String getModuleInfo() {
    return "<p>Overview: Saves a Prefuse graph to a GraphML file.</p>";
  }

  public String getModuleName() {
	  return "Prefuse to GraphML";
  }
  public void doit() throws Exception {

    Graph graph = (Graph)pullInput(0);
    String file = (String)pullInput(1);

    GraphMLWriter writer = new GraphMLWriter();
    writer.writeGraph(graph, file);

  }
}
