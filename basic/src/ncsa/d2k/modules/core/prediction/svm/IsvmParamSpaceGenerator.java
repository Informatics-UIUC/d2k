package  ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.modules.core.datatype.parameter.ParameterSpace;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.core.modules.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author vered goren
 * @version 1.0
 */

public class IsvmParamSpaceGenerator extends AbstractParamSpaceGenerator {


  public String getModuleName() {
                  return "Isvm Parameter Space Generator";
          }

  protected ParameterSpace getDefaultSpace() {
    ParameterSpace psi = new ParameterSpaceImpl();
                String[] names = {NU, NUM_ATTS};
                double[] min = {0,  0};
                double[] max = {1, 100};
                double[] def = { 0.1, 9};
                int[] res = {1, 1};
                int[] types = { ColumnTypes.DOUBLE, ColumnTypes.INTEGER};
                psi.createFromData(names, min, max, def, res, types);
                return psi;

  }
public static final String NU = "Nu of Kernel";
  public static final String NUM_ATTS = "Number of Input Feetures";


  public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] = new PropertyDescription("nu", "Nu", "SVM Parameter nu");
       pds[1] = new PropertyDescription("numAttributes", "Number Input Features",
                                        "Number of input features in the SVM problem");
      return pds;
    }

}