package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import libsvm.*;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

/**
Produces an initialized IsvmModel.
This modules generates a new IsvmModel and initializes it
according to the properties, as they are set by the input parameter point.
 see CreateIsvmModel or IsvmParamSpaceGenerator for information about the
 properties.

  @author vered goren
  */

public class CreateIsvmModelOPT extends ModelProducerModule
{


	public String getInputInfo(int index)
	{
		switch (index) {
                  case 0:
                          return "Controll point in the parameter space";
			default:
				return "No such input";
		}
	}

	public String getInputName(int index)
	{
		switch (index) {
			case 0:
				return "Parameter Point";
			default:
				return "No such input";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return in;
	}

	public String getModuleInfo()
	{
          return  "<P>Produces an initialized IsvmModel.</P>" +
              "<P>This modules generates a new IsvmModel and initializes it " +
              "according to the properties, as they are set by the input parameter point."  +
              "See CreateIsvmModel or IsvmParamSpaceGenerator for information about the " +
              "properties.";



	}

        public String getModuleName()
        {
                return "Create Isvm Model Optimized";
        }



	public String getOutputInfo(int index)
	{
		switch (index) {
			case 0:
				return "Isvm prediction model.";
			default:
				return "no such output";
		}
	}

	public String getOutputName(int index)
	{
		switch (index) {
			case 0:
				return "SVM Predictor";
			default:
				return "no such output";
		}
	}

	public String[] getOutputTypes()
	{
		String[] out = {"ncsa.d2k.modules.projects.vered.svm.IsvmModel"};
		return out;
	}

	public void beginExecution()
	{
	}

	public void endExecution()
	{
		super.endExecution();
	}

	protected void doit() throws Exception{
          ParameterPoint pp = (ParameterPoint) pullInput(0);

           IsvmModel d2k_model = new IsvmModel( (int) pp.getValue(IsvmParamSpaceGenerator.NUM_ATTS));
          d2k_model.setNu(pp.getValue(IsvmParamSpaceGenerator.NU));

          d2k_model.init();
          pushOutput(d2k_model, 0);

	}
}
