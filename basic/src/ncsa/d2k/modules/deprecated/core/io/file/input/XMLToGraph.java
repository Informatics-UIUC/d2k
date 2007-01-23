package ncsa.d2k.modules.deprecated.core.io.file.input;

import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.pgraph.*;

/**
 * Reads a Jung Graph object from a GraphML file.
 *
 * @author gpape
 */
public class XMLToGraph extends InputModule {

	public void beginExecution() {
	      System.out.println(getClass().getName() + " is deprecated.");
	      super.beginExecution();
	   }
   /**
* Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleName() {
	   return "XML to Graph";
   }
   
   /**
 * Performs the main work of the module.
 */
public void doit() throws IOException {
      pushOutput(PGraphML.load(new File((String)pullInput(0))), 0);
   }

   /**
 * Returns a description of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a description should be returned.
 *
 * @return <code>String</code> describing the input at the specified index.
 */
public String getInputInfo(int index) {
      return (index == 0) ? "The absolute path to the GraphML file." : null;
   }

   /**
 * Returns the name of the input at the specified index.
 *
 * @param inputIndex Index of the input for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the input at the specified index.
 */
public String getInputName(int index) {
      return (index == 0) ? "File Name" : null;
   }

   public String[] getInputTypes() {
      return new String[] {"java.lang.String"};
   }

   /**
 * Describes the purpose of the module.
 *
 * @return <code>String</code> describing the purpose of the module.
 */
public String getModuleInfo() {
      return "<p>Overview: This module creates a Jung Graph object from a " +
             "GraphML file.</p><p>Acknowledgement: This module uses " +
             "functionality from the JUNG project. See " +
             "http://jung.sourceforge.net.</p>";
   }

   /**
 * Returns a description of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> describing the output at the specified index.
 */
public String getOutputInfo(int index) {
      return (index == 0) ? "The loaded Graph object." : null;
   }

   /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a name should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
public String getOutputName(int index) {
      return (index == 0) ? "Graph" : null;
   }

   public String[] getOutputTypes() {
      return new String[] {"edu.uci.ics.jung.graph.Graph"};
   }

}
