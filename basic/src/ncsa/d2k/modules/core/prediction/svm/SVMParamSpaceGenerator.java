package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SVMParamSpaceGenerator extends AbstractParamSpaceGenerator
{
	/* VERED - 7-23-04: tese are now part of SVMParameter

         public static final String SVM_TYPE = "SVM Type";
	public static final String KERNEL_TYPE = "Kernel Type";
	public static final String DEGREE = "Degree of Kernel";
	public static final String GAMMA = "Gamma of Kernel";
	public static final String COEF0 = "Coefficent 0 of Kernel";
	public static final String NU = "Nu of Kernel";
	public static final String CACHE_SIZE = "Cache Size";
	public static final String C = "C of Kernel";
	public static final String EPS = "Stopping Criterion";
	public static final String P = "P of Kernel";
	public static final String SHRINKING = "Shrinking Heuristics";*/

	/**
	* Returns a reference to the developer supplied defaults. These are
	* like factory settings, absolute ranges and definitions that are not
	* mutable.
	* @return the default settings space.
	*/
	protected ParameterSpace getDefaultSpace() {

		ParameterSpace psi = new ParameterSpaceImpl();
		String[] names = SVMParameters.PROPS_NAMES;
		double[] min = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		double[] max = {4, 3, 9, 5, 0, 0, 0, 0, 0, 0, 0};
		double[] def = {0, 2, 3, 0, 0, 0.5, 40, 1, 0.001, 0.1, 1};
		int[] res = {5, 4, 10, 6, 1, 1, 1, 1, 1, 1, 1};
		int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER,
			ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
		ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
		ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.INTEGER};
		psi.createFromData(names, min, max, def, res, types);
		return psi;
	}

	public String getModuleName() {
		return "new Support Vector Machine Parameter Space Generator";
	}

	/**
	  Returns a list of the property descriptions.
	  @return a list of the property descriptions.
	  */
	public PropertyDescription[] getPropertiesDescriptions()
	{
          PropertyDescription[] pds = SVMParameters.getPropertiesDescriptions();
          //because this module's editor is a special one and all of the values
          //are numeric - replace the description for properties that are
          //represented by drop down lists in the regular SVM Builder module.

          pds[SVMParameters.SVM_TYPE] = new PropertyDescription(
        SVMParameters.PROPS_NAMES[SVMParameters.SVM_TYPE],
        SVMParameters.PROPS_NAMES[SVMParameters.SVM_TYPE],
        "Type of the SVM. (0 = C-SVC, 1 = nu-SVC, 2 = One-Class SVM, 3 = epsilon-SVR, 4 = nu-SVR)");

          pds[SVMParameters.KERNEL_TYPE] = new PropertyDescription(
        SVMParameters.PROPS_NAMES[SVMParameters.KERNEL_TYPE],
        SVMParameters.PROPS_NAMES[SVMParameters.KERNEL_TYPE],
        "Type of the SVM.  (0 = Linear, 1 = Polynomial, 2 = Radial Basis, 3 = Sigmoid)");


          pds[SVMParameters.SHRINKING] = new PropertyDescription(
        SVMParameters.PROPS_NAMES[SVMParameters.SHRINKING],
        SVMParameters.PROPS_NAMES[SVMParameters.SHRINKING],
        "Toggles shrinking heuristics on (1) and off (0)");



	   return pds;
       }

}
