package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class RenumberCategories extends ComputeModule {

  public String getModuleName() {
    return "RenumberCategories";
  }


  public String getModuleInfo() {
    return "This module takes a renumbering key generated" +
    		"by DropMissingValuesAndRenumber and uses it to convert from the " +
			"0-last categories back to the original category names.<p>The outgoing matrix " +
			"will be stored in the same format as the incoming matrix.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "NewCategoriesMatrix";
      case 1:
        return "RenumberKey";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "NewCategoriesMatrix: the matrix with the new categories, that is, the ones running from 0-last";
      case 1:
        return "RenumberKey: a matrix for which the value in a particular row is the original category number represented by the row index";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "OriginalCategoriesMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "OriginalCategoriesMatrix: the original categories before they were renumbered";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }


  public void doit() throws Exception {

    MultiFormatMatrix NewCategoriesMatrix = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix RenumberKey = (MultiFormatMatrix)this.pullInput(1);
    
    MultiFormatMatrix OriginalCategoriesMatrix = new MultiFormatMatrix(NewCategoriesMatrix.dataFormat,NewCategoriesMatrix.getDimensions());
    
    long nRows = NewCategoriesMatrix.getDimensions()[0];
    
    for (long rowIndex = 0; rowIndex < nRows; rowIndex++) {
    	OriginalCategoriesMatrix.setValue(rowIndex,0,RenumberKey.getValue((long)NewCategoriesMatrix.getValue(rowIndex,0),0));
    }
    
      this.pushOutput(OriginalCategoriesMatrix, 0);
    }

  }










