package ncsa.d2k.modules.core.prediction.svm;


import ncsa.d2k.modules.core.prediction.UpdateableModelModule;
import ncsa.d2k.modules.*;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.datatype.table.*;

import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered
 * @version 1.0
 *
 * @todo when this module is done move to package
 * ncsa.d2k.modules.core.prediction.svm
 *
 */

public class IsvmModel extends PredictionModelModule
    implements UpdateableModelModule, Serializable{

  public IsvmModel(int num) {
    numAttributes = num;
  }

  public IsvmModel() {

 }


protected  Isvm model ;

 public Isvm getModel(){return model;}
 public  void setModel(Isvm _model){ model = _model;}

  protected int numAttributes;
  public void setNumAttributes(int num){numAttributes = num;}
  public int getNumAttributes(){return numAttributes;}

  protected double nu;
  public double getNu(){return nu;}
  public void setNu(double dbl){nu = dbl;}


  public PropertyDescription[] getPropertiesDescriptions(){
    PropertyDescription[] pds = new PropertyDescription[1];
    pds[0] = new PropertyDescription("nu", "Nu", "SVM Parameter nu");
    return pds;
  }



  protected void makePredictions(PredictionTable pt) throws java.lang.Exception {

//this.printModel();


    if(numAttributes != pt.getNumInputFeatures())
         throw new Exception("Incompatible number of input features! This model " +
                             "was initialized with " + numAttributes + " input features " +
                             "but the prediction table has " + pt.getNumInputFeatures() +
                             "input features.");


    int[] predIdx = pt.getPredictionSet();

    for(int i=0; i<pt.getNumRows(); i++){
      for (int j=0; j<predIdx.length; j++){
        SparseDoubleMatrix1D example = SVMUtils.getExampleMatrix(pt, i);
        double pred = model.classify(example);
        //converting the prediction to be 1 or -1.
        if(pred > 0) pred = 1; else pred = -1;
        pt.setDoublePrediction(pred, i, j);
      }
    }
  }

  /**
   *
   */
  public void update(ExampleTable tbl)throws Exception{
    if(numAttributes != tbl.getNumInputFeatures())
      throw new Exception("Incompatible number of input features! This model " +
                          "was initialized with " + numAttributes + " input features " +
                          "but the input table has " + tbl.getNumInputFeatures() +
                          "input features.");
   setTrainingTable(tbl);

    SparseDoubleMatrix2D data =  SVMUtils.get2DMatrix(tbl);
    SparseDoubleMatrix1D classes = SVMUtils.getClassMatrix(tbl);

    model.train(data, classes, nu);
   // this.printModel();


  }

  public void init(){
    model = new Isvm(numAttributes);
    model.init();
  }



/*  public void doit() throws Exception {
         ExampleTable et = (ExampleTable)pullInput(0);
         this.setTrainingTable(et);
         this.predict(et);
         pushOutput(et,0);
     }*/



     public String[] getInputTypes() {
           String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
           return in;
       }



  /**
    *	Describe the function of this module.
    *	@return the description of this module.
    */
   public String getModuleInfo() {
       return "Makes predictions on a set of examples, utilizing the svm algorithm." +
"This model wraps the Isvm class and serves as an interface between Isvm and D2K Toolkit." +
           "<P>Note: There is a great importancy to the value of 'Number of Input Features' in " +
           "the generating module of this model 'CreateIsvmModel'. If the value of this " +
           "property does not match the number of input features of the input table of this" +
           "model, (or the Table according to which the update is being done), then " +
           "an exception is thrown and the itinerary is aborted.</P>" +
           "<P><U>Data Restriction</U>: This model can process only scalar data. It " +
           "can handle only one output feature that should be binary. In particular " +
           "the values of the output feature should be 1 and -1.</P>";
   }

//debugging purposes.
public void printModel(){
     model.printModel();
   }


public UpdateableModelModule copy(){
     IsvmModel ret = new IsvmModel();
     ret.model = model.copy();
     ret.nu = nu;
     ret.numAttributes = numAttributes;
     return ret;
   }

}