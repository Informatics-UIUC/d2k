package ncsa.d2k.modules.core.prediction.compound;

import ncsa.d2k.infrastructure.modules.*;
/**
	The object to be passed from AssignModelScore
	to CompoundModelGen.  Contains a name for the
	model, an instance of the model to predict each output,
	and a score/cross-validation error value for each of
	those model instances

	@author Peter Groves
	*/
public class ModelScore implements java.io.Serializable{

	public PredictionModelModule[] models;

	public double[] errors;

	public String modelName;

/**	Constructor

	Simply initializes the objects fields to those passed in.

	@param mods The models
	@param errs The error values corresponding to the models
	@param modName The name to identify the type of model this
					instance is keeping track of
*/
	public ModelScore(	PredictionModelModule[] mods,
						double[] errs,
						String modName)
	{
		models=mods;
		errors=errs;
		modelName=modName;
	}
}


