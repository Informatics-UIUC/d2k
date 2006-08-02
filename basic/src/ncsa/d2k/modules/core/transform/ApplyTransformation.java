/*
 * $Header$
 *
 * ===================================================================
 *
 * D2K-Workflow
 * Copyright (c) 1997,2006 THE BOARD OF TRUSTEES OF THE UNIVERSITY OF
 * ILLINOIS. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License v2.0
 * as published by the Free Software Foundation and with the required
 * interpretation regarding derivative works as described below.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v2.0 for more details.
 *
 * This program and the accompanying materials are made available
 * under the terms of the GNU General Public License v2.0 (GPL v2.0)
 * which accompanies this distribution and is available at
 * http://www.gnu.org/copyleft/gpl.html (or via mail from the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.), with the special and mandatory
 * interpretation that software only using the documented public
 * Application Program Interfaces (APIs) of D2K-Workflow are not
 * considered derivative works under the terms of the GPL v2.0.
 * Specifically, software only calling the D2K-Workflow Itinerary
 * execution and workflow module APIs are not derivative works.
 * Further, the incorporation of published APIs of other
 * independently developed components into D2K Workflow code
 * allowing it to use those separately developed components does not
 * make those components a derivative work of D2K-Workflow.
 * (Examples of such independently developed components include for
 * example, external databases or metadata and provenance stores).
 *
 * Note: A non-GPL commercially licensed version of contributions
 * from the UNIVERSITY OF ILLINOIS may be available from the
 * designated commercial licensee RiverGlass, Inc. located at
 * (www.riverglassinc.com)
 * ===================================================================
 *
 */
package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Transformation;


/**
 *  <p>Overview: This module applies a Transformation to a Table.
 * </p><P>Detailed Description: This module applies a Transformation to a
 * MutableTable and outputs the transformed table as a MutableTable.
 * </p><P>Data Handling: This modules modifies the input Table</P>
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class ApplyTransformation extends DataPrepModule {

   //~ Methods *****************************************************************


    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the module
     */
    public void doit() throws Exception {
        Transformation t = (Transformation) pullInput(0);
        MutableTable mt = (MutableTable) pullInput(1);

        boolean ok = t.transform(mt);

        if (!ok) {
            throw new Exception("Transformation failed.");
        } else {
            mt.addTransformation(t);
        }

        pushOutput(mt, 0);
    }


    /**
     * Returns a description of the input at the specified index.
     *
     * @param i Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int i) {

        switch (i) {

            case 0:
                return "Transformation to apply to the input Table.";

            case 1:
                return "The Table to apply the Transformation to.";

            default:
                return "no such input";
        }
    }


    /**
     * Returns the name of the input at the specified index.
     *
     * @param i Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int i) {

        switch (i) {

            case 0:
                return "Transformation";

            case 1:
                return "Table";

            default:
                return "no such input";
        }
    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {
        String[] in =
                {
                        "ncsa.d2k.modules.core.datatype.table.Transformation",
                        "ncsa.d2k.modules.core.datatype.table.MutableTable"
                };

        return in;
    }


    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        return "<p>Overview: This module applies a Transformation to a Table. " +
                "</p><P>Detailed Description: " +
                "This module applies a Transformation to a MutableTable and outputs " +
                "the transformed table as a MutableTable. " +
                "</p><P>Data Handling: This modules modifies the input Table</P>";
    }


    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Apply Transformation";
    }


    /**
     * Returns a description of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int i) {

        switch (i) {

            case 0:
                return "The transformed input Table";

            default:
                return "no such input";
        }
    }


    /**
     * Returns the name of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int i) {

        switch (i) {

            case 0:
                return "Table";

            default:
                return "no such input";
        }
    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};

        return out;
    }
} // end class ApplyTransformation
