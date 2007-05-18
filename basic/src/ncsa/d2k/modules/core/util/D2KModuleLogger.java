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
package ncsa.d2k.modules.core.util;

import ncsa.d2k.core.modules.RootModule;
import ncsa.d2k.preferences.UserPreferences;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


/**
 * Description of class D2KModuleLogger.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
public class D2KModuleLogger {

   //~ Instance fields *********************************************************

   /** Description of field loggingLevel. */
   private int loggingLevel =
      UserPreferences.thisPreference.getModulesLoggingLevel();
 

   /** Description of field logToConsole. */
   private boolean logToConsole =
      UserPreferences.thisPreference.getLogToConsole();

   /** Description of field logToFile. */
   private boolean logToFile = UserPreferences.thisPreference.getLogToFile();

   /** Description of field myLogger. */
   private Logger myLogger = null;

   /** Description of field pl. */
   public PatternLayout pl = new PatternLayout(" %-5p %c - %m%n");
   
   private FileAppender fapp = null;
   private ConsoleAppender capp = null;

   //~ Constructors ************************************************************


   /**
    * Creates a new D2KModuleLogger object.
    *
    * @param forClass Description of parameter forClass.
    */
   public D2KModuleLogger(Class forClass) {
      new D2KModuleLogger(forClass, null);
   }

   /**
    * Creates a new D2KModuleLogger object.
    *
    * @param forClass Description of parameter forClass.
    * @param format   Description of parameter format.
    */
   public D2KModuleLogger(Class forClass, String format) {

      if (format != null) {
         setPatternlayout(format);
      }

      initLogger(forClass);
   }

   //~ Methods *****************************************************************

   /**
    * Description of method initLogger.
    *
    * @param  classname Description of parameter classname.
    *
    * @return Description of return value.
    */
   private Logger initLogger(Class classname) {
      myLogger = Logger.getLogger(classname);
     
      //be careful about this guy? Will it remove all setting that is previous done
      //by reading from configuration file? Is the reading occurs automatically
      //when getLogger() method is called? We need to find out!
      myLogger.removeAllAppenders();
            
      try {
          fapp = new FileAppender(pl,
                  UserPreferences.thisPreference
                  .getLogFileName()); 
          capp = new ConsoleAppender(pl);
         if (logToFile) {
            myLogger.addAppender(fapp);
         }
         if (logToConsole) {
            myLogger.addAppender(capp);
         }
      } catch (Exception e) { }

      setLevelCase(loggingLevel);
                 
      return myLogger;
   } // end method initLogger

   private void setLevelCase(int lc)
   {
	      switch (lc) {

	         case (RootModule.DEBUG_LEVEL):
	            myLogger.setLevel(Level.DEBUG);
	            break;
	         case (RootModule.INFO_LEVEL):
	            myLogger.setLevel(Level.INFO);
	            break;
	         case (RootModule.WARN_LEVEL):
	            myLogger.setLevel(Level.WARN);
	            break;
	         case (RootModule.ERROR_LEVEL):
	            myLogger.setLevel(Level.ERROR);
	            break;
	         case (RootModule.FATAL_LEVEL):
	            myLogger.setLevel(Level.FATAL);
	            break;
	         case (5)://the case for turning off logger
	        	 myLogger.setLevel(Level.OFF);
	            break;
	         default:
	            myLogger.setLevel(Level.WARN);
	      }
   }
    
   public void setLoggingLevel(int l){
	   loggingLevel=l;//set the level for this D2KModuleLogger class
//	   if (l != 5){//not the case of Level.OFF
		   setLevelCase(l);//set the level for myLogger in D2kmoduleLogger class
//	   }
   }
	   
   public int getLoggingLevel(){
	   return loggingLevel;
   }

   public void setDebugLoggingLevel()
   {
	   myLogger.setLevel(Level.DEBUG);
   }
   public void setInfoLoggingLevel()
   {
	   myLogger.setLevel(Level.INFO);
   }
   public void setWarnLoggingLevel()
   {
	   myLogger.setLevel(Level.WARN);
   }
   public void setErrorLoggingLevel()
   {
	   myLogger.setLevel(Level.ERROR);
   }
   public void setFatalLoggingLevel()
   {
	   myLogger.setLevel(Level.FATAL);
   }
   
   public void resetLoggingLevel()
   {
	      setLevelCase(loggingLevel);
   }
   
   /**
    * Description of method debug.
    *
    * @param message Description of parameter message.
    */
   public void debug(Object message) { myLogger.debug(message); }

   /**
    * Description of method info.
    *
    * @param message Description of parameter message.
    */
   public void info(Object message) { myLogger.info(message); }

   /**
    * Description of method warn.
    *
    * @param message Description of parameter message.
    */
   public void warn(Object message) { myLogger.warn(message); }


   /**
    * Description of method error.
    *
    * @param message Description of parameter message.
    */
   public void error(Object message) { myLogger.error(message); }

   /**
    * Description of method fatal.
    *
    * @param message Description of parameter message.
    */
   public void fatal(Object message) { myLogger.fatal(message); }

   /**
    * user can set layout?
    *
    * @param pattern Description of parameter pattern.
    */
   public void setPatternlayout(String pattern) {
 	   if (myLogger == null)//myLogger has not been initialized
	   {
		   pl = new PatternLayout(pattern);		   		   
	   }
 	   /*else myLogger has already been initialized, 
 	    * user need to temporarily set the pattern
 	    * The question is, how does user retrieve the previous appenders?
 	    * Following part may not work fine at this moment,
 	    *   or even not logical
 	    * Need further revision
 	    * */
 	   else 
	   {
		   myLogger.removeAllAppenders();
		   PatternLayout newpl = new PatternLayout(pattern);
		   try {
			   if (logToFile) {
				   myLogger.addAppender(new FileAppender(newpl,
						   UserPreferences.thisPreference
						   .getLogFileName()));
				   }
			   if (logToConsole) {
				   myLogger.addAppender(new ConsoleAppender(newpl));
				   }
			   } catch (Exception e) { }
	   }
    		  
      // reset pattern for the logger? look up
  //    this.setPatternlayout(pl.toString());
   }


} // end class D2KModuleLogger
