package ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions;

import java.io.Serializable;

/**

	NNactivation

    This is the base class for classes that compute activation functions
    and their derivatives when given the weighted sum (or other combination function).
    The class system
    is used for the benefit of being able to easily interchange them in
    the Genetic Algorithm approach (or any other optimizer) to paramater
    optimization of a neural net

	@author Peter Groves
*/

abstract public class NNactivation implements Serializable{

  /* Constructor - never does anything */
  public NNactivation(){
  }

  /* computes the activation of the number its given
  @param - double
	*/
  abstract public double activationOf(double x);

  /* computes the derivative function value of the number
  @param - double
  */
  abstract public double derivativeOf(double y);

  /* gives the name of the activation function
  */
  abstract public String getName();

}
