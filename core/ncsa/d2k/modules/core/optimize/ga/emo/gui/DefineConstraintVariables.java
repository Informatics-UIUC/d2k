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

/**
 * Define variables to be used in the calculation of constraint violation
 * functions.
 */
public class DefineConstraintVariables
    extends DefineFitnessVariables {

  public String getModuleName() {
    return "Define Constraint Variables";
  }

  public String getModuleInfo() {
    String s = "<p>Overview:";
    s += "Define variables to be used in the calculation of constraint violation functions.";
    s += "<p>Detailed Description: ";
    s += " Define variables that are calculated by performing transformations ";
    s += " on a Table.";
    s += "This module provides a GUI used to specify expression strings.";
    s += "These expressions are interpreted as operations on existing ";
    s += "attributes in the table and are used to construct new attributes. ";
    s += "When the GUI is dismissed, the information needed to construct ";
    s += "these new attributes is encapsulated in a <i>Transformation</i> ";
    s += "object that is applied when evaluating a population.";
    s += "The available operations on numeric attributes are addition, ";
    s += "subtraction, multiplication, division, and modulus. The ";
    s += "operations available on boolean attributes are AND and OR.";
    return s;
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