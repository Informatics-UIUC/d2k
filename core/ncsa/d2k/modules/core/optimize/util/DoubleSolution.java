package ncsa.d2k.modules.core.optimize.util;

import java.io.Serializable;

/**
        Represents a solution in the a space where the parameters are doubles.
*/
abstract public class DoubleSolution implements Solution, java.io.Serializable {

        /** the parameters. */
        protected double [] parameters;

        /** the ranges with describe the parameters. */
        protected DoubleRange [] ranges;

        protected double [] constraints;

        public DoubleSolution (DoubleRange[] ranges) {
          this(ranges, 0);
        }

        /**
         * the constructor takes a list of Ranges for the parameters,
         * and a list of objective constraints for the objective.
         */
        public DoubleSolution (DoubleRange [] ranges, int numConstraints) {
                constraints = new double[numConstraints];
                this.ranges = ranges;
                int number = ranges.length;
                this.parameters = new double [number];
                for (int j = 0; j < number; j++) {
                        parameters [j] = Math.random ();
                        parameters [j] = ranges [j].getMin () + (parameters [j] *
                                                (ranges [j].getMax () - ranges [j].getMin ()));
                }
        }

        public int getNumConstraints() {
          return constraints.length;
        }

        public void setConstraint(double val, int i) {
          constraints[i] = val;
        }

        public double getConstraint(int i) {
          return constraints[i];
        }

        /**
         * returns the array of doubles containing the parameters to the
         * solution.
         * @param parameters the list of parameters yielding the solution.
         */
        public Object getParameters () {
                return parameters;
        }

        /**
        * sets the parameters to a new double array
        *@param parameters the list of parameters to the solution
        */
        public void setParameters(Object params){
                parameters=(double[])params;
        }
        /**
                just returns the parameter as it is
        **/

        public double getDoubleParameter(int paramIndex){
                return parameters[paramIndex];
        }

        /**
                assigns the new double as is to the parameter set
        */

        public void setDoubleParameter(double newParam, int paramIndex){
                parameters[paramIndex]=newParam;
        }

        abstract public Object clone();

        public double[] toDoubleValues() {
          int numParameters = ranges.length;
          double[] retVal = new double[numParameters];
          for(int i = 0; i < numParameters; i++) {
            retVal[i] = getDoubleParameter(i);
          }
          return retVal;
        }

}
