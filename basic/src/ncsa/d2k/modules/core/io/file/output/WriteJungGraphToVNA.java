//package ncsa.d2k.modules.t2k.io.file;
package ncsa.d2k.modules.core.io.file.output;

import edu.uci.ics.jung.graph.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.beans.PropertyVetoException;
//import ncsa.sonic.ciknow.io.JungGraphToVNA;

/**
 * This module writes the contents of a JUNG Graph to a file in VNA format.
 *
 * @author Sean Mason
 */
public class WriteJungGraphToVNA extends OutputModule {

    public WriteJungGraphToVNA() {
    }

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
       StringBuffer sb = new StringBuffer("<p>Overview: ");
       sb.append("<p>This modules writes a JUNG Graph object ot a file in the VNA graph description format.");
       sb.append("Vertices in the network should have a UserDatum entry called \"id\" that contains the vertex ID.");
       sb.append("If this attribute is not found, a numeric ID will be assigned to the vertex.</p>");
       sb.append("<p>Edges within the graph may contain edge weights in a UserDatum named \"edgeWeight\", and an optional ");
       sb.append("edge type in a UserDatum named \"edgeType\".");
       sb.append("</p><p>Data Handling: ");
       sb.append("This module does not destroy its input data, but may add generated IDs to nodes that do not have them.");
       sb.append("</p>");
       return sb.toString();
   }

   public PropertyDescription[] getPropertiesDescriptions() {
      return new PropertyDescription[0];
   }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
      return "Write JUNG Graph to VNA";
   }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
      String[] types = {"java.lang.String","edu.uci.ics.jung.graph.Graph"};
      return types;
   }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
      String[] types = {      };
      return types;
   }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
      switch (i) {
         case 0: return "The name of the file to be written.";
         case 1: return "The Graph to write.";
         default: return "No such input";
      }
   }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
      switch(i) {
         case 0:
            return "File Name";
         case 1:
            return "JUNG Graph";
         default: return "No such input";
      }
   }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
      switch (i) {
         default: return "No such output";
      }
   }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
      switch(i) {
         default: return "No such output";
      }
   }

    /**
      Write the table to the file.
   */
    public void doit() throws Exception {
      String fileName = (String)pullInput(0);
      Graph graph = (Graph)pullInput(1);
      FileWriter fw;
      String newLine = "\n";

      JungGraphToVNA converter = new JungGraphToVNA();
      converter.write(graph, fileName);
   }
}

