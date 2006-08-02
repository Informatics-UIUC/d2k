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
package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.modules.core.prediction.PMMLTags;


/**
 * Constants for use in PMML markup for NaiveBayes models
 *
 * @author  $author$
 * @version $Revision$, $Date$
 */
public interface NaiveBayesPMMLTags extends PMMLTags {

   //~ Static fields/initializers **********************************************

   /** NaiveBayesModel */
   static public final String NBM = "NaiveBayesModel";

   /** threshold */
   static public final String THRESHOLD = "threshold";

   /** BayesInputs */
   static public final String BAYES_INPUTS = "BayesInputs";

   /** BayesInput */
   static public final String BAYES_INPUT = "BayesInput";

   /** fieldName */
   static public final String FIELD_NAME = "fieldName";

   /** PairCounts */
   static public final String PAIR_COUNTS = "PairCounts";

   /** TargetValueCounts */
   static public final String TARGET_VALUE_COUNTS = "TargetValueCounts";

   /** TargetValueCount */
   static public final String TARGET_VALUE_COUNT = "TargetValueCount";

   /** count */
   static public final String COUNT = "count";

   /** DerivedField */
   static public final String DERIVED_FIELD = "DerivedField";

   /** Discretize */
   static public final String DISCRETIZE = "Discretize";

   /** DiscretizeBin */
   static public final String DISCRETIZE_BIN = "DiscretizeBin";

   /** binValue */
   static public final String BIN_VALUE = "binValue";

   /** Interval */
   static public final String INTERVAL = "Interval";

   /** closure */
   static public final String CLOSURE = "closure";

   /** leftMargin */
   static public final String LEFT_MARGIN = "leftMargin";

   /** rightMargin */
   static public final String RIGHT_MARGIN = "rightMargin";

   /** closedOpen */
   static public final String CLOSED_OPEN = "closedOpen";

   /** closedClosed */
   static public final String CLOSED_CLOSED = "closedClosed";

   /** openClosed */
   static public final String OPEN_CLOSED = "openClosed";

   /** openOpen */
   static public final String OPEN_OPEN = "openOpen";

   /** BayesOutput */
   static public final String BAYES_OUTPUT = "BayesOutput";

   /** numberOfFields */
   static public final String NO_FIELDS = "numberOfFields";
} // end interface NaiveBayesPMMLTags
