package ncsa.d2k.modules.core.optimize.random;

import ncsa.d2k.core.modules.*;
import java.util.ArrayList;
import ncsa.d2k.modules.core.datatype.table.Example;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;

public class ExampleCollector
      extends ncsa.d2k.core.modules.ComputeModule {
   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[1];
      descriptions[0] = new PropertyDescription(
            "minimize",
            "Minimize Objective Score",
            "Set to true if the objective score should be minimize, false if it should be maximized.");
/*      descriptions[1] = new PropertyDescription(
            "threashhold",
            "Objective Threashhold Value",
            "Stop optimization when this threashhold value is reached.");*/
      return descriptions;
   }

   private boolean minimizing = true;
   public void setMinimize(boolean value) {
      minimizing = value;
   }

   public boolean getMinimize() {
      return this.minimizing;
   }

/*   private double threashhold = 0.0;
   public void setThreashhold(double value) {
      this.threashhold = value;
   }

   public double getThreashhold() {
      return this.threashhold;
   }*/

   /**
    * returns information about the input at the given index.
    * @return information about the input at the given index.
    */
   public String getInputInfo(int index) {
      switch (index) {
         case 0:
            return "<p>" +
                  "      This is the number of examples to collect before producing the best " +
                  "      example as an output." +
                  "    </p>";
         case 1:
            return "<p>" +
                  "      The scored parameter point, this is an example." +
                  "    </p>";
         default:
            return "No such input";
      }
   }

   /**
    * returns information about the output at the given index.
    * @return information about the output at the given index.
    */
   public String getInputName(int index) {
      switch (index) {
         case 0:
            return "Number Examples";
         case 1:
            return "Example";
         default:
            return "NO SUCH INPUT!";
      }
   }

   /**
    * returns string array containing the datatypes of the inputs.
    * @return string array containing the datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] types = {
            "java.lang.Integer", "ncsa.d2k.modules.core.datatype.table.Example"};
      return types;
   }

   /**
    * returns information about the output at the given index.
    * @return information about the output at the given index.
    */
   public String getOutputInfo(int index) {
      switch (index) {
         case 0:
            return "<p>" +
                  "      This is the best scored paramter point." +
                  "    </p>";
         default:
            return "No such output";
      }
   }

   /**
    * returns information about the output at the given index.
    * @return information about the output at the given index.
    */
   public String getOutputName(int index) {
      switch (index) {
         case 0:
            return "Best Example";
         default:
            return "NO SUCH OUTPUT!";
      }
   }

   /**
    * returns string array containing the datatypes of the outputs.
    * @return string array containing the datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] types = {
            "ncsa.d2k.modules.core.datatype.table.Example"};
      return types;
   }

   /**
    * returns the information about the module.
    * @return the information about the module.
    */
   public String getModuleInfo() {
      return "<p>" +
            "      Overview: This module takes two inputs. One is the number of examples to " +
            "      collect, the other will accept the examples. This module will collect " +
            "      the number of examples specified on it's first input, and when all " +
            "      examples are collected will output the best scored example." +
            "    </p>" +
            "    <p>" +
            "      Detailed Description: This module will received an Integer on it's first " +
            "      input port. This integer specifies the number of examples that will be " +
            "      produced, and ultimately received on the second input. The examples will " +
            "      be stored as they are received, and when N examples are received, the " +
            "      one with the highest score will be output on the only output." +
            "    </p>" +
            "    <p>" +
            "      Trigger Criteria: This module will not enable until it has gotten an " +
            "      Integer (N) on the first port. When it receives this input, it will " +
            "      enable each time the next N examples are received on the second port." +
            "    </p>";
   }

   /**
    * Return the human readable name of the module.
    * @return the human readable name of the module.
    */
   public String getModuleName() {
      return "Example Collector";
   }

   /** this is the number of examples to collect. */
   private int n = 0;

   ArrayList examples;
   /**
    * We just need to reset n.
    */
   public void beginExecution () {
      n = 0;
      examples = new ArrayList();
   }
   public void endExecution() {
      examples = null;
   }

   /**
    * This module will trigger when it has received N, the number of examples
    * to collect on the first port. From that point on, it will trigger each time it gets an
    * input the next N times on the second port.
    */
   public boolean isReady() {
      if (n == 0) {
         if (this.getInputPipeSize(0) > 0 && this.getInputPipeSize(1) > 0) {
            return true;
         }
         else {
            return false;
         }
      } else
         if (this.getInputPipeSize(1) > 0)
            return true;
      return false;
   }

   /**
    * This is the design patter exemplar for a gather operation.
    */
   public void doit () throws Exception{

      // If we haven't yet received n, it must be available.
      if (n == 0) {
         n = ((Integer)this.pullInput(0)).intValue();
         if (n <= 0)
            throw new RuntimeException("The input on the first port must be an Integer greater than 0.");
      }

		Object tmp = this.pullInput(1);
      examples.add(tmp);
      System.out.println ("EXAMPLE ("+tmp.getClass().getName()+"): "+n);
      n--;
      if (n == 0) {

         // we are done, find the best score and push it.
         Example best = (Example) examples.get(0);
         if (!minimizing) {
            for (int i = 1; i < examples.size(); i++) {
               Example example = (Example) examples.get(i);
               if (example.getOutputDouble(0) > best.getOutputDouble(0))
                  best = example;
            }
         } else {
            for (int i = 1; i < examples.size(); i++) {
               Example example = (Example) examples.get(i);
               if (example.getOutputDouble(0) < best.getOutputDouble(0))
                  best = example;
            }
         }
         this.pushOutput(best, 0);
      }
   }

   /**
    * Just print an example.
    * @param label
    * @param ex
    */
   private void printExample(String label, Example ex) {
      System.out.println(label);
      System.out.println("  Inputs");
      ExampleTable et = (ExampleTable) ex.getTable();
            int ni = et.getNumInputFeatures();
      int no = et.getNumOutputFeatures();

      for (int i = 0; i < ni; i++) {
         System.out.println("    " + et.getInputName(i) + " = " +
                            ex.getInputDouble(i));
      }
      System.out.println("  Outputs");
      for (int i = 0; i < no; i++) {
         System.out.println("    " + et.getOutputName(i) + " = " +
                            ex.getOutputDouble(i));
      }
   }
}
