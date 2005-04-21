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
 *    ASSearch.java
 *    Copyright (C) 1999 Mark Hall
 *
 */



package ncsa.d2k.modules.weka.filters.search;

import java.io.Serializable;

import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.weka.filters.Utils;
import ncsa.d2k.modules.weka.filters.evaluation.ASEvaluation;


/**
 * Abstract attribute selection search class.
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public abstract class ASSearch implements Serializable {

  // ===============
  // Public methods.
  // ===============

  /**
   * Searches the attribute subset/ranking space.
   *
   * @param ASEvaluator the attribute evaluator to guide the search
   * @param data the training instances.
   * @param inputFeatures array of attributes considered for selection
   * @return an array (not necessarily ordered) of selected attribute indexes
   * @exception Exception if the search can't be completed
   */
  public abstract int [] search(ASEvaluation ASEvaluator,
				Table data) throws Exception;

  /**
   * Creates a new instance of a search class given it's class name and
   * (optional) arguments to pass to it's setOptions method. If the
   * search method implements OptionHandler and the options parameter is
   * non-null, the search method will have it's options set.
   *
   * @param searchName the fully qualified class name of the search class
   * @param options an array of options suitable for passing to setOptions. May
   * be null.
   * @return the newly created search object, ready for use.
   * @exception Exception if the search class name is invalid, or the options
   * supplied are not acceptable to the search class.
   */
  public static ASSearch forName(String searchName,
				 String [] options) throws Exception
  {
    return (ASSearch)Utils.forName(ASSearch.class,
				   searchName,
				   options);
  }
}
