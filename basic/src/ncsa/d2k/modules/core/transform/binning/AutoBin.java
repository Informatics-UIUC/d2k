package ncsa.d2k.modules.core.transform.binning;

import java.text.*;
import java.beans.PropertyVetoException;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.core.modules.*;

/**
 * Automatically discretize scalar data for the Naive Bayesian classification
 * model.  This module requires a ParameterPoint to determine the method of binning
 * to be used.
 */
public class AutoBin extends AutoBinOPT {

  private int binMethod;
  public void setBinMethod(int i) throws PropertyVetoException {
    binMethod =i;
    if(binMethod != 0 && binMethod != 1)
          throw new PropertyVetoException("Discretization Method must be 0 or 1",null);
      }
  public int getBinMethod() {
    return binMethod;
  }

  private int binWeight=1;
  public void setBinWeight(int i)  throws PropertyVetoException {
    if(i < 1)
      throw new PropertyVetoException("Number of items per bin must be a positive integer.",null);
    binWeight = i;
  }
  public int getBinWeight() {
    return binWeight;
  }

  private int numberOfBins=2;
  public void setNumberOfBins(int i)  throws PropertyVetoException {
    if(i < 2)
	throw new PropertyVetoException("Number of bins must be higher than 1.", null);
         numberOfBins = i;
  }
  public int getNumberOfBins() {
    return numberOfBins;
  }

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getModuleInfo() {
    String s = "<p>Overview: Automatically discretize scalar data for the " +
        "Naive Bayesian classification model." +
        "<p>Detailed Description: Given a table of Examples, define the bins for each " +
        "scalar input column.  Nominal input columns will have a bin defined " +
        "for each unique value in the column." +
        "<p>Data Handling: When binning scalar columns by the number of bins, " +
        "the maximum and minimum values of each column must be found.  When " +
        "binning scalar columns by weight, the data in each individual column " +
        "is first sorted using a merge sort and then another pass is used to " +
        "find the bounds of the bins. This module does not modify the input data." +
        "<p>Scalability: The module requires enough memory to make copies of " +
        "each of the scalar input columns.";
    return s;
  }


  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[3];
    pds[0] = new PropertyDescription("binMethod", "Discretization Method",
        "The method to use for discretization.  Select 1 to create bins" +
        " by weight.  This will create bins with an equal number of items in "+
        "each slot.  Select 0 to do uniform discretization by specifying the number of bins. "+
        "This will result in equally spaced bins between the minimum and maximum for "+
        "each scalar column.");
    pds[1] = new PropertyDescription("binWeight", "Number of Items per Bin",
        "When binning by weight, this is the number of items" +
        " that will go in each bin.  However, the bins may contain more or fewer values than "+
        "<i>weight</i> values, depending on how many items equal the bin limits. Typically " +
	 "the last bin will contain less or equal to <i>weight</i> values and the rest of the " +
         "bins will contain a number that is  equal or greater to <i>weight</i> values.");
    pds[2] = new PropertyDescription("numberOfBins", "Number of Bins",
                                     "Define the number of bins absolutely. "+
                                     "This will give equally spaced bins between "+
                                     "the minimum and maximum for each scalar "+
                                     "column.");
    return pds;
  }


  public void doit() throws Exception {
    tbl = (ExampleTable) pullInput(0);

    inputs = tbl.getInputFeatures();
    outputs = tbl.getOutputFeatures();
    if ((inputs == null) || (inputs.length == 0))
	  throw new Exception(getAlias() + ": Please select the input features, they are missing.");

    if (outputs == null || outputs.length == 0)
	  throw new Exception(getAlias() + ": Please select an output feature, it is missing");

    if(tbl.isColumnScalar(outputs[0])) 
	  throw new Exception(getAlias() + ": Output feature must be nominal. Please transform it.");

    nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(3);

   int type = getBinMethod();
   

    // if type == 0, specify the number of bins
    // if type == 1, use uniform weight
    //BinTree bt;
    BinDescriptor[] bins;
    if (type == 0) {
     //int number = (int)pp.getValue(ParamSpaceGenerator.NUMBER_OF_BINS);
     int number = getNumberOfBins();
     if(number < 0)
       throw new Exception(getAlias()+": Number of bins not specified!");
      bins = numberOfBins(number);
    }
    else {
//      int weight = (int)pp.getValue(ParamSpaceGenerator.ITEMS_PER_BIN);
      int weight = getBinWeight();
      bins = sameWeight(weight);
    }

    BinTransform bt = new BinTransform(bins, false);

    pushOutput(bt, 0);
  }
}

//QA Comments Anca:
// added propertyVetoExceptions
// added checks for input/output feature existence and nominal class variable
