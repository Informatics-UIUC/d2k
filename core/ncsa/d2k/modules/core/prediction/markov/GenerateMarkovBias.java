//package working.click;
//package opie;
package ncsa.d2k.modules.core.prediction.markov;

//import ncsa.d2k.dtcheng.classes.*;
//import ncsa.d2k.dtcheng.general.*;
//import ncsa.d2k.dtcheng.io.*;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.io.*;
import java.util.*;
import java.text.*;


public class GenerateMarkovBias extends ncsa.d2k.core.modules.ComputeModule 
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
		switch (index) {
			case 0: return "TriggerObject";
			default: return "No such input";
		}
	}

  public String getOutputInfo (int index)
    {
		switch (index) {
			case 0: return "MarkovBiasTable";
			default: return "No such output";
		}
	}

  public String getModuleInfo()
    {
		return "<html>  <head>      </head>  <body>    ncsa.d2k.modules.core.prediction.markov.GenerateMarkovBias  </body></html>";
	}


  ////////////////////////
  //  TYPE DEFINITIONS  //
  ////////////////////////

  public String[] getInputTypes()
    {
		String[] types = {"java.lang.Object"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
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

    TableImpl transform_bias_table = null;

    transform_bias_table = (TableImpl)DefaultTableFactory.getInstance().createTable(NumBiasFeatures);

    DoubleColumn Order_column = new DoubleColumn (1);
    Order_column.setDouble(Order, 0);

    transform_bias_table.setColumn(Order_column, 0);

    this.pushOutput(transform_bias_table, 0);
    }


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "GenerateMarkovBias";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
  }


