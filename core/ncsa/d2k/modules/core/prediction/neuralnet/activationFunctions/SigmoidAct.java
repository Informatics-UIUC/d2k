package ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions;


/* This is an activation that uses the standard sigmoid
   of f(x)=1/(1+e^-x)
*/


public class SigmoidAct extends NNactivation{

 //computes the activation of the number its given
  //@param - double

  public double activationOf(double x){

    return (1/(1+Math.exp(-x)));


  }

  //computes the derivative function value of the number
  //@param - double

  public double derivativeOf(double y){

    double x = activationOf(y);
    return (x*(1-x));
  }
	public String getName(){
	return "Standard Sigmoid";
	}

}
