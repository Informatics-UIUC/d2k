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

  /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int i) {
    if (i == 0) {
      return "File Name";
    }
    return null;
  }

  /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    return null;
  }

  /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int i) {
    if (i == 0) {
      return "File Name";
    }
    return null;
  }

  /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    return null;
  }

  /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
    return "<p>Overview: Reads a GraphML file to a Prefuse graph.</p>";
  }

  /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
	    return "GraphML to Prefuse";
	  }
  
  /**
 * Performs the main work of the module.
 */
public void doit() throws Exception {
    String fn = (String)pullInput(0);

    GraphMLReader reader = new GraphMLReader();

    Graph graph = reader.readGraph(fn);
    pushOutput(graph, 0);
  }
}
