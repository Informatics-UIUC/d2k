//package working.click;
//package opie;
package ncsa.d2k.modules.core.prediction.markov;

//import ncsa.d2k.dtcheng.classes.*;
//import ncsa.d2k.dtcheng.general.*;
//import ncsa.d2k.dtcheng.io.*;

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;


import java.io.*;
import java.util.*;
import java.text.*;


public class GenerateMarkovBias extends ncsa.d2k.infrastructure.modules.ComputeModule implements Serializable
  {

  //////////////////////
  //  INITIALIZATION  //
  //////////////////////

  public void beginExecution()
    {
    }

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int     NumBiasFeatures            = 1;

  private double  Order;//     = 1.0;
  public  void    setOrder  (double value) {       this.Order = value;}
  public  double  getOrder  ()             {return this.Order;}


  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getInputInfo (int index)
    {
    String[] inputDescriptions = {"TriggerObject"};
    return inputDescriptions[index];
    }

  public String getOutputInfo (int index)
    {
    String[] outputDescriptions = {"MarkovBiasVerticalTable"};
    return outputDescriptions[index];
    }

  public String getModuleInfo()
    {
    String text = this.getClass().getName();
    return text;
    }


  ////////////////////////
  //  TYPE DEFINITIONS  //
  ////////////////////////

  public String[] getInputTypes()
    {
    String[] temp = {"java.lang.Object"};
    return temp;
    }

  public String[] getOutputTypes()
    {
    String[] temp = {"ncsa.d2k.util.datatype.VerticalTable"};
    return temp;
    }



/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/
/**********************************************************************************************************************************/

  ////////////
  //  MAIN  //
  ////////////



  //VerticalTable cached_output_examples_vertical_table;

  public void doit()
    {

    VerticalTable transform_bias_vertical_table = null;

    transform_bias_vertical_table = new VerticalTable(NumBiasFeatures);

    DoubleColumn Order_column = new DoubleColumn (1);
    Order_column.setDouble(Order, 0);

    transform_bias_vertical_table.setColumn(Order_column, 0);

    this.pushOutput(transform_bias_vertical_table, 0);
    }

  }


