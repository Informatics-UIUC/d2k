package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.Construction;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;

public class DefineConstraintVariables
    extends DefineFitnessVariables {

  public String getModuleName() {
    return "Define Constraint Variables";
  }

  protected UserView createUserView() {
    return new DefineConstraintVarView();
  }

  //inner class, extends attributeConstruction's inner gui class
  private class DefineConstraintVarView
      extends DefineVarView {

      protected void done() {
        constructions = (Object[]) getLastCons();

        Construction[] tmp = new Construction[constructions.length];
        for (int i = 0; i < constructions.length; i++) {
          tmp[i] = (Construction) constructions[i];

          float[] tmpfloat = new float[0];
          // add an empty column of floats to the table
          table.addColumn(tmpfloat);
          // set the label of the new column added to the table
          table.setColumnLabel(tmp[i].label, (table.getNumColumns() - 1));
        }

        Constraints constraints = parameters.constraints;
        if(constraints == null) {
          constraints = new Constraints();
          parameters.constraints = constraints;
        }

        for(int i = 0; i < tmp.length; i++) {
          constraints.addConstraintVariable(tmp[i]);
        }

        pushOutput(parameters, 0);
        parameters = null;
        viewDone("Done");
      }
  }
}