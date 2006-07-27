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
package ncsa.d2k.modules.core.datatype;


/**
 * The <code>Expression</code> interface encapsulates any parsed-string
 * expression that can be evaluated.
 *
 * <p>Classes that implement this interface should not accept an expression
 * <code>String</code> as an argument to any constructor. Rather, they should
 * rely upon the <code>setExpression</code> method, which should attempt to
 * parse the expression and throw an <code>ExpressionException</code> if there
 * is an error. In this way, a <code>String</code>'s validity as an expression
 * can be determined by simply calling <code>setExpression</code> and catching
 * the exception.</p>
 *
 * <p><code>evaluate</code> should return an <code>Object</code> corresponding
 * to an evaluation of the last <code>String</code> specified by <code>
 * setExpression</code>.</p>
 *
 * @author  gpape
 * @version $Revision$, $Date$
 */
public interface Expression {

   //~ Methods *****************************************************************

   /**
    * Attempts to evaluate the last <code>String</code> specified by <code>
    * setExpression</code>.
    *
    * @return an appropriate <code>Object</code>
    *
    * @throws ExpressionException If the given expression string is invalid
    */
   public Object evaluate() throws ExpressionException;

   /**
    * Sets this <code>Expression</code>'s internal state to represent the given
    * expression <code>String</code>.
    *
    * @param  expression some expression
    *
    * @throws ExpressionException If the given expression string is invalid
    */
   public void setExpression(String expression) throws ExpressionException;

}
