package ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions;

/*Activation class that uses tanh(x)*/

public class TanhAct extends NNactivation{

boolean bug=true;


  //computes the activation of the number its given
  //@param - double

  public double activationOf(double x){

  double z= ((2/(1+Math.exp(-2*x)))-1);

	//if((Double.isNaN(z) || Double.isInfinite(z)) && bug){
	//System.out.println("Tanh: err: x="+x+"  act="+z);
	//bug=false;
	//}
  return z;
  }
   public String getName(){
	return "Tanh";
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
