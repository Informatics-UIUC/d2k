package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.vis.widgets.ConfusionMatrix;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: this module creates a table that compares the performances
 * of all the models that were produced.
 * the module accepts the Prediction Table of each model and accumulates the
 * statistics.
 * the output will be a 3 column Table. first column - index number of the model.
 * second column - precision of the model. thrid column - recall of the model.
 * the recall is regarding prediction true positive in the output column.
 * Data Restriction: this module can handle only binary one class classificatin.
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 */

public class CreateModelComparisonTable extends ComputeModule
{

  private String fileName; //output file name
  private MutableTable tbl; //the performance table
  private int counter ; //how many tables were accepted already
  private int aproxNumModels; //helps to initialized the table to a reasonable size.


  public void setAproxNumModels(int num){aproxNumModels = num;}
  public int getAproxNumModels(){return aproxNumModels;}
  public String getFileName (){return fileName;}
  public void setFileName(String str){fileName = str;}


  public void setTbl(MutableTable _tbl){tbl = _tbl;}

  public PropertyDescription[] getPropertiesDescriptions(){
    PropertyDescription[] pds = new PropertyDescription[2];
    pds[0] = new PropertyDescription("fileName", "Output File Name",
                                     "The Comparison Table will be written to this file");
    pds[1] = new PropertyDescription("aproxNumModels", "Aproximate Number of Models",
                                     "The aproximate number of expected models in this " +
                                     "itinerary. The Comparison Table will be initiated to " +
                                     "hold such number fo records");
    return pds;
  }

  /**
   * @todo this module now handles only binary classification.
   * it can deal only with one output feature.
   * @throws java.lang.Exception
   */

  protected void doit() throws Exception{

    PredictionTable pt = (PredictionTable) pullInput(0);
//System.out.println();
    int []outputs = pt.getOutputFeatures();
    int []preds = pt.getPredictionSet();
    int numPos = 0;

      if(outputs == null)
        throw new Exception("The output attributes were undefined.");
      if(preds == null)
        throw new Exception("The prediction features were undefined.");

      int[][] performance = new int[2][];
      performance[CORRECT] = new int[outputs.length]; //precision
      performance[TRUE_TRUE] = new int [outputs.length]; //recall

      for(int i = 0; i < outputs.length; i++) {


        int outCol = outputs[i];
        int predCol = preds[i];
        for (int j=0; j<pt.getNumRows(); j++){

          if(pt.getInt(j, outputs[i]) == 1){
            numPos++; //counting the actual positive classes
   //         System.out.println("found true in row # " + j);
          }

//if prediction is correct...
          if(pt.getInt(j, outputs[i]) == pt.getInt(j, preds[i])){
            performance[CORRECT][i] ++; //increment precision
            //check if need to increment recall...
            if(pt.getInt(j, outputs[i]) == 1) performance[TRUE_TRUE][i] ++;
          }

   /*       System.out.print( j + ")\t");
          System.out.print("actual = " + pt.getInt(j, outputs[i]));
          System.out.print("\tpredicted = " + pt.getInt(j, preds[i]));
          System.out.print("\tcorrect so far: " + performance[CORRECT][i] );
          System.out.print("\ttrue positives so far: " + performance[TRUE_TRUE][i] );
          System.out.println();
*/

          }//for j
      }//for i

//compute precision and recall
      double precision = ((double) performance[CORRECT][0] * 100) / ((double)pt.getNumRows());
      double recall = ((double)performance[TRUE_TRUE][0] * 100) / ((double)numPos);

//find out if table needs to be extended
      if(counter >= tbl.getNumRows()) tbl.addRows(1);

//setting values for current model
      tbl.setInt(counter, counter, INDEX);
      tbl.setDouble(precision, counter, PRECISION);
      tbl.setDouble(recall, counter, RECALL);
      //incrementing counter.
      counter ++;

  }


//constants
  public static final int CORRECT = 0; //indices into the performance 2D array
  public static final int TRUE_TRUE = 1;

  public static final int INDEX = 0;    //indices into the output table.
  public static final int PRECISION = 1;
  public static final int RECALL = 2;


//initializes the output table.
  public void beginExecution(){
   tbl = new MutableTableImpl();
   Column[] cols = new Column[3];
   cols[INDEX] = new IntColumn(aproxNumModels); //model index
   cols[INDEX].setLabel("Model Index");
   cols[PRECISION] = new DoubleColumn(aproxNumModels); //precision
   cols[PRECISION].setLabel("Precision");

   cols[RECALL] = new DoubleColumn(aproxNumModels); //recall
      cols[RECALL].setLabel("Recall");
   tbl.addColumns(cols);
   counter = 0;

 }

//writes the table to a binary file
  public void endExecution() {

    //trim the table
    if(counter < tbl.getNumRows()){
      tbl.removeRows(counter, tbl.getNumRows() - counter);
    }

    if(fileName == null){
      System.out.println(getAlias() + ": Output File Name property was not set!" +
                         " Cannot save the comparison table to output file.");
      return;

    }


       FileOutputStream file = null;
       ObjectOutputStream out = null;

       try {
          file = new FileOutputStream(fileName);
       }
       catch (FileNotFoundException e) {
          System.out.println( getAlias() + ": Could not open file: " + fileName +
                                 "\nTable could not be saved\n" );
            e.printStackTrace();
            return;
       }
       catch (SecurityException e) {
        System.out.println(  getAlias() + ": Could not open file: " + fileName +
                                 "\nTable could not be saved\n" );
         e.printStackTrace();
         return;
       }
       catch(Exception e){
         System.out.println(  getAlias() + ": Could not open file: " + fileName +
                                 "\nTable could not be saved\n" );
         e.printStackTrace();
         return;

       }

       try {
           out = new ObjectOutputStream(file);
           out.writeObject(tbl);
           out.flush();
           out.close();
       }
       catch (IOException e) {
           System.out.println( getAlias() + ": Unable to serialize object " +
                                  "\nTable could not be saved" );
          e.printStackTrace();
       }


tbl = null;

  }



  public String getModuleName(){
    return "Create Model Comparison Table";
  }

  public String getModuleInfo(){
    return "<P>This module creates a Table in which each row holds"+
        " data about the performance of a prediction model, computed acording " +
        "to the input <i>Prediction Table</I>. At the end of execution it writes the table to a file." +
        " This Table can then be loaded into a plotting vis module, to visualize percision/recall VS model number.</P>" +
        "<P><U>Data Handling</U>: This module can only handle one output feature and this " +
        "feature should be binary.</P>";
  }

  public String getInputName(int idx){
    switch(idx){
      case 0: return "Prediction Table";
      default: return "No such input";
    }
  }


  public String getInputInfo(int idx){
    switch(idx){
      case 0: return "A Prediction Table created by a Prediction Model Module.";
      default: return "No such input";
    }
  }

  public String[] getInputTypes(){
    String[] retVal = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
    return retVal;
  }


  public String[] getOutputTypes(){
    String[] retVal = {};
    return retVal;
  }

  public String getOutputName(int idx){
   switch(idx){
    // case 0: return "Comparison Table";
     default: return "No such input";
   }
 }


 public String getOutputInfo(int idx){
   switch(idx){
  //   case 0: return "A Table in which each row represents the performance of a prediction model.";
     default: return "No such input";
   }
 }





}