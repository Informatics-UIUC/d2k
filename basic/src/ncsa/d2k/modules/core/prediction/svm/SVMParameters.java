package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SVMParameters
{

  public static final int NUM_PROPS = 11;

  //parameters (properties) code

       public static final int SVM_TYPE = 0;
       public static final int KERNEL_TYPE = 1;
       public static final int DEGREE = 2;
       public static final int GAMMA = 3;
       public static final int COEF0 = 4;
       public static final int CACHE_SIZE = 6;
       public static final int EPS =8;
       public static final int C = 7;
       public static final int NU = 5;
       public static final int P = 9;
       public static final int SHRINKING = 10;

//properties names
        public static final String[] PROPS_NAMES = {
            "SVM Type",
            "Kernel Type",
            "Degree",
            "Gamma",
            "Coefficent 0 ",
             "Nu",
             "Cache Size",
             "C",
             "Epsilon",
            "P",
            "Shrinking Heuristics"
        };

//properties descriptions
        public static final String[] PROPS_DESCS = {
            "Type of the SVM. C-SVC, nu-SVC, One-Class SVM, epsilon-SVR or nu-SVR)",
            "Type of the kernel. Linear, Polynomial, Radial Basis or Sigmoid",
            "Degree of kernel function, applicable to polynomial kernel only.",
            "Gamma of kernel function. Applicable to polynomial, radial or sigmoid kernels.",
            "Coefficent 0 of kernel function. Applicable to polynomial or sigmoid kernels.",
             "Parameter nu of nu-SVC, One-class SVM, and nu-SVR.",
            "Cache memory size in MB.",
            "Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.",
             "Stopping criterion.",
            "Epsilon of loss function in epsilon-SVR.",
            "Binary value to turn on/off shrinking heuristics."

       };



    //svm type values
    public static final int C_SVC = 0;
    public static final int NU_SVC = 1;
    public static final int ONE_CLASS_SVM = 2;
    public static final int EPSILON_SVR = 3;
    public static final int NU_SVR = 4;
    public static final String[] SVM_TYPE_NAMES = {"C-SVC", "nu-SVC", "One Class SVM",
        "Epsilon SVR", "nu SVR"};

    //kernel type values
    public static final int LINEAR = 0;
    public static final int POLYNOMIAL = 1;
    public static final int RADIAL = 2;
    public static final int SIGMOID = 3;
    public static final String[] KERNEL_TYPE_NAMES = {"Liner", "Polynomial",
        "Radial", "Sigmoid"};



	/**
	  Returns a list of the property descriptions.
	  @return a list of the property descriptions.
	  */
	public static PropertyDescription[] getPropertiesDescriptions()
	{
		PropertyDescription[] pds = new PropertyDescription[NUM_PROPS];
                for (int i=0; i<NUM_PROPS; i++)
                  pds[i] = new PropertyDescription(PROPS_NAMES[i], PROPS_NAMES[i], PROPS_DESCS[i]);
		return pds;
	}

}
