package ncsa.d2k.modules.core.prediction.neuralnet.activationFunctions;
import java.io.Serializable;
/* This activation is a function proposed by some
   guy named D.L. Elliot, the paper is at
   ftp://ftp.isr.umd.edu/pub/TechReports/1993/TR_93-8.pdf

   @author Peter Groves
*/


public class ElliotAct extends NNactivation implements Serializable{

	boolean bug=true;
 /*computes the activation of the number its given
  @param - double
  */

  public double activationOf(double x){

	double z;
    if(x>0){
      z=(x/(1+x));
    }
    else{
	z=(x/(1-x));
	}
	/*if((Double.isNaN(z)||Double.isInfinite(z)) && bug){
	System.out.println("Elliot: err: x="+x+"act="+z);
	bug = false;
	}*/
  return z;

  }

  /*computes the derivative function value of the number
  @param - double
  */

  public double derivativeOf(double y){

    double x = activationOf(y);

    double m;
    if(x>0)
      m=1-x;
    else
      m=1+x;

    return m*m;
  }
	public String getName(){
	return "Elliot's Proposed Activation";
	}

}
