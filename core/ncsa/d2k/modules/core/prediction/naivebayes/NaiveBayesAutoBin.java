package ncsa.d2k.modules.core.prediction.naivebayes;

import java.text.*;

import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class NaiveBayesAutoBin
    extends NaiveBayesAutoBinOPT {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Automatically discretize scalar data for the " +
        "Naive Bayesian classification model.  This module requires a " +
        "ParameterPoint to determine the method of binning to be used." +
        "<p>Detailed Description: Given a Parameter Point from a Naive Bayes " +
        "Parameter Space and a table of Examples, define the bins for each " +
        "scalar input column.  Nominal input columns will have a bin defined " +
        "for each unique value in the column." +
        "<p>Properties: none" +
        "<p>Data Type Restrictions: This module does not modify the input data." +
        "<p>Data Handling: When binning scalar columns by the number of bins, " +
        "the maximum and minimum values of each column must be found.  When " +
        "binning scalar columns by weight, the data in each individual column " +
        "is first sorted using a merge sort and then another pass is used to " +
        "find the bounds of the bins." +
        "<p>Scalability: The module requires enough memory to make copies of " +
        "each of the scalar input columns.";
    return s;
  }

  private int binMethod;
  public void setBinMethod(int i) throws Exception {
    if(binMethod < 0 || binMethod > 1)
      throw new Exception("Bin Method must be 0 or 1");
    binMethod = i;
  }

  public int getBinMethod() {
    return binMethod;
  }


  private int binWeight;
  public void setBinWeight(int i) throws Exception {
    if(binWeight < 0)
      throw new Exception("Bin Weight must be a positive integer.");
    binWeight = i;
  }
  public int getBinWeight() {
    return binWeight;
  }

  private int numberOfBins;
  public void setNumberOfBins(int i) throws Exception {
    if(numberOfBins < 0)
      throw new Exception("Number of bins must be a positive integer.");
    numberOfBins = i;
  }
  public int getNumberOfBins() {
    return numberOfBins;
  }

  public void doit() throws Exception {
    ExampleTable et = (ExampleTable) pullInput(0);
    //ParameterPoint pp = (ParameterPoint) pullInput(1);
//    HashMap nameToIndexMap = new HashMap();

    tbl = et;
    nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(3);

    /*for (int i = 0; i < pp.getNumParameters(); i++) {
      String name = pp.getName(i);
      nameToIndexMap.put(name, new Integer(i));
    }

    Integer method = (Integer) nameToIndexMap.get(NaiveBayesParamSpaceGenerator.
                                                  BIN_METHOD);

    */
   //int type = (int)pp.getValue(NaiveBayesParamSpaceGenerator.BIN_METHOD);
   int type = getBinMethod();

    /*if (method == null) {
      throw new Exception(getAlias() + ":  Could not find Bin Method!");
    }
    int type = method.intValue();
*/

    // if type == 0, specify the number of bins
    // if type == 1, use uniform weight
    //BinTree bt;
    BinDescriptor[] bins;
    if (type == 0) {
      /*Integer number = (Integer) nameToIndexMap.get(
          NaiveBayesParamSpaceGenerator.NUMBER_OF_BINS);
      if (number == null) {
        throw new Exception(getAlias() + ": Number of bins not specified!");
      }
      */
     //int number = (int)pp.getValue(NaiveBayesParamSpaceGenerator.NUMBER_OF_BINS);
     int number = getNumberOfBins();
     if(number < 0)
       throw new Exception(getAlias()+": Number of bins not specified!");
      bins = numberOfBins(number);
    }
    else {
      /*Integer weight = (Integer) nameToIndexMap.get(
          NaiveBayesParamSpaceGenerator.ITEMS_PER_BIN);
      if (weight == null) {
        throw new Exception(getAlias() + ": Items per bin not specified!");
      }*/
//      int weight = (int)pp.getValue(NaiveBayesParamSpaceGenerator.ITEMS_PER_BIN);
      int weight = getBinWeight();
      bins = sameWeight(weight);
    }

    BinTransform bt = new BinTransform(bins, false);

    pushOutput(bt, 0);
    pushOutput(et, 1);

    tbl = null;
  }
}