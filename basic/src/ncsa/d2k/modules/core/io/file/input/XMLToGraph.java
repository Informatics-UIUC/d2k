package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.pgraph.*;

/**
 * Reads a Jung Graph object from a GraphML file.
 *
 * @author gpape
 */
public class XMLToGraph extends InputModule {

   public void doit() throws IOException {
      pushOutput(PGraphML.load(new File((String)pullInput(0))), 0);
   }

   public String getInputInfo(int index) {
      return (index == 0) ? "The absolute path to the GraphML file." : null;
   }

   public String getInputName(int index) {
      return (index == 0) ? "File Name" : null;
   }

   public String[] getInputTypes() {
      return new String[] {"java.lang.String"};
   }

   public String getModuleInfo() {
      return "<p>Overview: This module creates a Jung Graph object from a " +
             "GraphML file.</p><p>Acknowledgement: This module uses " +
             "functionality from the JUNG project. See " +
             "http://jung.sourceforge.net.</p>";
   }

   public String getOutputInfo(int index) {
      return (index == 0) ? "The loaded Graph object." : null;
   }

   public String getOutputName(int index) {
      return (index == 0) ? "Graph" : null;
   }

   public String[] getOutputTypes() {
      return new String[] {"edu.uci.ics.jung.graph.Graph"};
   }

}
