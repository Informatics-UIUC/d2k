package ncsa.d2k.modules.core.transform.attribute;

import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;


public class RandomGenerator extends ncsa.d2k.infrastructure.modules.DataPrepModule implements Serializable {

	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "A DoubleColumn containing the randomly generated numbers";

			default: return "No such output";
		}

	}

	public String getInputInfo (int index) {
		switch (index) {

			default: return "No such output";
		}
	}

	public String getModuleInfo () {
		String text = "Produces a set of randomly generated numbers with the distribution indicated in props. <br>PROPS:<ul><li>setSize - the size of the set of numbers to generate<li>LowerBound/UpperBound - define the range of the set (make sure they make sense for the params of the distribution function)<li>DistFamily - the distribution type <ul><li>0-normal<li>1-Uniform<li>2-Cauchy<li>3-Exponential<li>4-Weibull<li>5-lognormal<li>6-Double Exponential<li>7-Gamma</ul><li>Param1- usually the location, but check the api<li>Param2- usually scale, check the api<li>Param3- sometimes shape, sometimes not used, check api</ul> ";
		return text;
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		return null;
	}

	public String[] getOutputTypes () {
		String[] temp = {"ncsa.d2k.modules.core.datatype.table.DoubleColumn"};
		return temp;
	}

	//set like 'the set of numbers', not like 'set it to 1'
	public int setSize=1000;

	public void setSetSize(int i){
		setSize=i;
	}
	public int getSetSize(){
		return setSize;
	}
	public int distFamily=0;

	public int getDistFamily(){
		return distFamily;
	}
	public void setDistFamily(int i){
		distFamily=i;
	}
	///define the range
	public double lowerBound=-1;
	public double upperBound=1;
	public double getLowerBound(){
		return lowerBound;
	}
	public double getUpperBound(){
		return upperBound;
	}
	public void setLowerBound(double d){
		lowerBound=d;
	}
	public void setUpperBound(double d){
		upperBound=d;
	}


	//going to hold all the params here
	public double[] param=new double[3];
	///p1
	public double getParam0(){
		return param[0];
	}
	public void setParam0(double d){
		param[0]=d;
	}
	////p2
	public double getParam1(){
		return param[1];
	}
	public void setParam1(double d){
		param[1]=d;
	}
	///p3
	public double getParam2(){
		return param[2];
	}
	public void setParam2(double d){
		param[2]=d;
	}

	public void doit(){
		double[] numbers=new double[setSize];
		for(int i=0; i<setSize; i++){
			numbers[i]=DistFunctions.randomGen(param, distFamily, lowerBound, upperBound);
		}
		DoubleColumn col=new DoubleColumn(numbers);
		col.setLabel("RandomNumbers");

		pushOutput(col, 0);

	}
}








