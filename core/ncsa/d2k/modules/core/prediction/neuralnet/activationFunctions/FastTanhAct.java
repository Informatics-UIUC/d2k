package ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions;
import java.io.Serializable;
/*Activation class that uses tanh(x) approximation for faster computation*/

public class FastTanhAct extends NNactivation implements Serializable{

boolean bug=true;


  //computes the activation of the number its given
  //@param - double

  public double activationOf(double x){

	if(x>1.92033)
		return .96016;
	if(x<=-1.92033)
		return -.96016;
	if(x>0)
		return (.96016-.26037*(x-1.92033)*(x-1.92033));

	return ( -.96016 + .26037*(x+1.92033)*(x+1.92033));



  }

	public String getName(){
	return "Fast Tanh";
	}

  //computes the derivative function value of the number
  //@param - double

  public double derivativeOf(double y){

  double x = activationOf(y);

  double z= (1-x*x);


	//if((Double.isNaN(z) || Double.isInfinite(z)) && bug){
	//System.out.println("Tanh: err: y="+y+" x="+x+"  der="+z);
	//bug=false;
	//}
  return z;


  }


}
