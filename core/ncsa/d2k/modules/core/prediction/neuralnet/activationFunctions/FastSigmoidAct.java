package ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions;
import java.io.Serializable;
/*
	Sigmoid approximation using linear functions

*/


public class FastSigmoidAct extends NNactivation implements Serializable{

	boolean bug=true;
 //computes the activation of the number its given
  //@param - double

  public double activationOf(double y){


	double x=y/4.1;

	if (x>1)
		return 1;
	if(x<-1)
		return 0;
	if(x<0)
		return (.5+ x*(1+x/2));

	return (.5+  x*(1-x/2));






  }

	public String getName(){
	return "Fast Sigmoid";
	}

  //computes the derivative function value of the number
  //@param - double

  public double derivativeOf(double y){

    double x = activationOf(y);


    return x*(1-x);
  }


}
