/*
 This class is derived from source code in the Weka project, available here:
 http://www.cs.waikato.ac.nz/ml/weka/
*/

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    AttributeEvaluator.java
 *    Copyright (C) 1999 Mark Hall
 *
 */


package ncsa.d2k.modules.weka.filters.evaluation;





/**
 * Abstract attribute evaluator. Evaluate attributes individually.
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public abstract class AttributeEvaluator extends ASEvaluation {
    // ===============
    // Public methods.
    // ===============

    /**
     * evaluates an individual attribute
     *
     * @param attribute the index of the attribute to be evaluated
     * @return the "merit" of the attribute
     * @exception Exception if the attribute could not be evaluated
     */
    public abstract double evaluateAttribute(int attribute) throws Exception;
}




