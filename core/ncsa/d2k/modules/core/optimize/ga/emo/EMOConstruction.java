package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 Construction.java
 */

/**
 This class records a variable and the mathematical expression to compute the variable
 */

public class EMOConstruction
    extends Construction
    implements java.io.Serializable {
  // label: is the name of the variable
  // expression is the string representation
  // of the mathematical formulation
  //String label, expression;
  // If myflag is 0 then it is not a fitness function
  // If myflag is 1 then it is a fitness function and can be
  //maximized or minimized
  int myflag;
  // if myflag is 0 it does not matter what is the value of flagvalue
  // If myflag is 1  then
  //            if flagvalue is 0 the function has to be minimized
  //            if flagvalue is 1 the function has to be maximized
  int flagvalue;
  public EMOConstruction(String label, String expression) {
    super(label, expression);
    //this.label = label;
    //this.expression = expression;
    myflag = 0;
    flagvalue = 0;
  }

  public int getmyflag() {
    return myflag;
  }

  public void setmyflag(int newval) {
    myflag = newval;
  }

  public int getflagvalue() {
    return flagvalue;
  }

  public void setflagvalue(int newval) {
    flagvalue = newval;
  }

  public String getLabel() {
    return label;
  }

  public String getExpression() {
    return expression;
  }

  /*public String toString() {
    return label + ": " + expression;
       }*/
}
