package ncsa.d2k.modules.core.optimize.random;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import java.util.ArrayList;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class RandomSampling extends ComputeModule {

   /** these are the paramter points to test. */
   int pointsPushed = 0;

   public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] descriptions = new PropertyDescription[5];
      descriptions[0] = new PropertyDescription(
            "maxIterations",
            "Maximum Number of Iterations",
            "Optimization halts when this limit on the number of iterations is exceeded.  ");
      descriptions[1] = new PropertyDescription(
            "seed",
            "Random Number Seed",
            "This integer is use to seed the random number generator which is used to select points in parameter space.");
      descriptions[2] = new PropertyDescription(
            "trace",
            "Trace",
            "Report each scored point in parameter space as it becomes available.");
      descriptions[3] = new PropertyDescription(
            "verbose",
            "Verbose Output",
            "Report each scored point in parameter space as it becomes available, and each parameter point that has been pushed.");
      descriptions[4] = new PropertyDescription(
            "useResolution",
            "Constrain Resolution",
            "If this parameter is set, we will use the resolution defined in the paramter space value for each paramter to define the number of distinct values in the ranges.");

      return descriptions;
   }

   private int maxIterations = 100;
   public void setMaxIterations(int value) throws PropertyVetoException {
      if (value < 1) {
         throw new PropertyVetoException(" < 1", null);
      }
      this.maxIterations = value;
   }

   public int getMaxIterations() {
      return this.maxIterations;
   }

   private int seed = 1;
   public void setSeed(int value) {
      this.seed = value;
   }

   public int getSeed() {
      return this.seed;
   }

   private boolean trace = false;
   public void setTrace(boolean value) {
      this.trace = value;
   }

   public boolean getTrace() {
      return this.trace;
   }

   private boolean verbose = false;
   public void setVerbose(boolean value) {
      this.verbose = value;
   }

   public boolean getVerbose() {
      return this.verbose;
   }

   private boolean useresolution = false;

   public void setUseResolution(boolean value) {
      this.useresolution = value;
   }

   public boolean getUseResolution() {
      return this.useresolution;
   }

   public String getModuleName() {
      return "Random Sample";
   }

   public String getModuleInfo() {
      return "<p>      Overview: Generate random points in a space defined by a parameter space       input" +
            " until we push a user defined maximum number of points, we we       converge to a user defined" +
            " optima.    </p>    <p>      Detailed Description: This module will produce <i>Maximum Number" +
            " of       Iterations</i> points in parameter space, unless it converges before       generating" +
            " that many points. It will produce only one point per       invocation, unless it has already" +
            " produced all the points it is going to       and it is just waiting for scored points to come" +
            " back. This module will       not wait for a scored point to come back before producing the" +
            " next one,       it will produce as many poiints as it can, and it will remain enabled     " +
            "  until all those points are produced, or it has converged. The module       converges if a" +
            " score exceeds the property named <i>Objective Threashhold</i>. The Random Seed can be set to" +
            " a positive value to cause this module to       produce the same points, given the same inputs," +
            " on multiple runs. <i>      Trace</i> and <i>Verbose Output</i> properties can be set to produce" +
            " a       little or a lot of console output respectively. If <i>Constrain       Resolution</i>" +
            " is not set, the resolution value from the parameter space       object will be ignored. We" +
            " can minimize the objective score by setting       the <i>Minimize Objective Score</i> property" +
            " to true.    </p>";
   }

   public String getInputName(int i) {
      switch (i) {
         case 0:
            return "Control Parameter Space";
         default:
            return "NO SUCH INPUT!";
      }
   }

   public String getInputInfo(int i) {
      switch (i) {
         case 0:
            return "The Control Parameter Space to search";
         default:
            return "No such input";
      }
   }

   public String[] getInputTypes() {
      String[] types = {
            "ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
      return types;
   }

   public String getOutputName(int i) {
      switch (i) {
         case 0:
            return "Parameter Point";
         case 1:
            return "Number Points";
         default:
            return "NO SUCH OUTPUT!";
      }
   }

   public String getOutputInfo(int i) {
      switch (i) {
         case 0:
            return "The next Parameter Point selected for evaluation";
         case 1:
            return
                  "This is the number of parameter points to produce";
         default:
            return "No such output";
      }
   }

   public String[] getOutputTypes() {
      String[] types = {
            "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint",
            "java.lang.Integer"
      };
      return types;
   }

   private Random randomNumberGenerator = null;

   /**
    * Init the standard fields
    */
   public void beginExecution() {
      pointsPushed = -1;
      randomNumberGenerator = new Random(seed);
   }

   /**
    * There are two states, searching a space, and waiting for a space. When we are
    * waiting, we have no space to search, all previous spaces have been searched. When
    * we receive another paramter space, we will search it. While searching, we have
    * a space and we are pushing points in that space. We will push <code>maxIteration</code>
    * points, one periteration. When we have pushed all of them, we are done.
    * @return true if we are ready to execute.
    */
   public boolean isReady() {
      if (this.pointsPushed == -1 && this.getInputPipeSize(0) == 0) {
         return false;
      } else {
         return true;
      }
   }

   ParameterSpace space;

   /**
    * We do one of two things, we either store the newly aquired space input, and reset the
    * pointsPushed value, or we push another point
    */
   public void doit() {
      if (this.pointsPushed == -1) {
         this.pointsPushed = 0;
         space = (ParameterSpace) this.pullInput(0);
         this.pushOutput(new Integer(this.maxIterations), 1);
      } else {
         this.pushParameterPoint();
         this.pointsPushed++;
         if (this.pointsPushed == maxIterations) {

            // we have traversed this space, start on the next one.
            this.pointsPushed = -1;
            space = null;
         }
      }
   }

   /**
    * Push another paramter point, and update the accounting.
    */
   private void pushParameterPoint() {
      int numParams = space.getNumParameters();
      double[] point = new double[numParams];

      // Create one point in parameter space.
      for (int i = 0; i < numParams; i++) {
         double range = space.getMaxValue(i) - space.getMinValue(i);
         if (useresolution) {
            int resolution = space.getResolution(i) - 1;

            // This would be an error on the users part, resolution should never be zero.
            double increment;
            if (resolution <= 0) {
               increment = 0;
               resolution = 1;
            } else {
               increment = range / (double) resolution;
            }
            point[i] = space.getMinValue(i) +
                  increment * randomNumberGenerator.nextInt(resolution + 1);
         } else {
            switch (space.getType(i)) {
               case ColumnTypes.DOUBLE:
                  point[i] = space.getMinValue(i) +
                        range * randomNumberGenerator.nextDouble();
                  break;
               case ColumnTypes.FLOAT:
                  point[i] = space.getMinValue(i) +
                        range * randomNumberGenerator.nextFloat();
                  break;
               case ColumnTypes.INTEGER:
                  if ( (int) range == 0) {
                     point[i] = space.getMinValue(i);
                  } else {
                     point[i] = space.getMinValue(i) +
                           randomNumberGenerator.nextInt( (int) (range + 1));
                  }
                  break;
               case ColumnTypes.BOOLEAN:
                  if ((int) range == 0) {
                     point[i] = space.getMinValue(i);
                  } else {
                     point[i] = space.getMinValue(i) +
                           randomNumberGenerator.nextInt( (int) (range + 1));
                  }
                  break;
            }
         }
      }

      // we have data, construct a paramter point.
      String[] names = new String[space.getNumParameters()];
      for (int i = 0; i < space.getNumParameters(); i++) {
         names[i] = space.getName(i);
      }
      ParameterPointImpl parameterPoint = (ParameterPointImpl)
            ParameterPointImpl.getParameterPoint(names, point);
      if (trace) {
         System.out.println("RandomSampling: Pushed point " + pointsPushed +
                            " " + parameterPoint);

      }
      if (verbose) {
         this.printExample(pointsPushed + " - Pushed a point : ",
                           parameterPoint);
      }
      this.pushOutput(parameterPoint, 0);
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
