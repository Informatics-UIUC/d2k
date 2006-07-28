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

  /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int i) {
    if (i == 0) {
      return "Prefuse Graph";
    }
    else if (i == 1) {
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
      return "Prefuse Graph";
    }
    else if (i == 1) {
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
    return null;
  }

  /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
    return "<p>Overview: Saves a Prefuse graph to a GraphML file.</p>";
  }

  /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
	  return "Prefuse to GraphML";
  }
  /**
 * Performs the main work of the module.
 */
public void doit() throws Exception {

    Graph graph = (Graph)pullInput(0);
    String file = (String)pullInput(1);

    GraphMLWriter writer = new GraphMLWriter();
    writer.writeGraph(graph, file);

  }
}
