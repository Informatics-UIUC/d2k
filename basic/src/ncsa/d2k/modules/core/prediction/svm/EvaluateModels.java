package ncsa.d2k.modules.core.prediction.svm;


import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
/**


=======
	EvaluateModels

		takes in a model and an example Table
		and runs the model's predict function
		on the input table
               capable of receiving multiple models. they are all tested on the
               same test data, and make it easy to compare performances.
*/
public class  EvaluateModels extends ncsa.d2k.modules.core.prediction.ModelPredict
{


public String getModuleName() {
    return "Evaluate Models";
  }

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		StringBuffer sb = new StringBuffer( "Overview: This module applies a prediction model to a table of examples and ");
		sb.append("makes predictions for each output attribute based on the values of the input attributes. ");

		sb.append("</p><p>Detailed Description:  This module applies a previously built model to a new set of examples that have the ");
		sb.append("same attributes as those used to train/build the model.  The module creates a new table that contains ");
		sb.append("columns for each of the values the model predicts, in addition to the columns found in the original table. ");
		sb.append("The new columns are filled in with values predicted by the model based on the values of the input attributes. ");
                sb.append("The module is capable of testing severals models on the same test data. ");

		return sb.toString();
	}


        private boolean first = true;
        private ExampleTable tbl = null;

        public boolean isReady(){
          if(first)
            return (getInputPipeSize(0) > 0 && getInputPipeSize(1) > 0);
          else return (getInputPipeSize(1) > 0);
        }

	/**
	*/
	public void doit() throws Exception {
          if(first){
           tbl = (ExampleTable) pullInput(0);
            first = false;
          }
		PredictionModelModule pmm=(PredictionModelModule)pullInput(1);

		PredictionTable pt=pmm.predict(tbl);
		pushOutput(pt, 0);

	}

}


