package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class FileSystemNNWriter0 extends OutputModule {
  
  public String getModuleName() {
    return "FileSystemNNWriter0";
  }
  
  public String getModuleInfo() {
    return "This has been adapted to use a shared filesystem to communicate rather than servers and sockets...<p>"
    + "This module is a pure brute force, fully MAGIC'ed attempt "
    + "to have a module that writes a very specific kind of itinerary. "
    + "As it is written, it <i><b>will not</b></i> be re-use-able.<p>"
    + "That being said... the point of this is to take two templates and "
    + "write an itinerary that executes my (Ricky's, that is) neural network "
    + "solver across an arbitrary number of machines. This will require "
    + "pre-positioning the data and all sorts of yummy things. <p>"
    + "Ok, the MachineListFileName is the name of a file containing the "
    + "list of machines we want to run the itinerary on. It should have the "
    + "following format: ipaddress,# of threads,anything else. "
    + "the localhost (which will <i><b>NOT</b></i> be listed in the machines list) "
    + "will manage the whole thing and hence be called the "
    + "Queen [as in bee]) while all the others will be called Workers. <p>"
    + "The BaseDataPathName is the base name of where the data will be found "
    + "on the machines. It is assumed that the data will be forward-staged/pre-positioned "
    + "such that it is always in the same directory on each machine, but the filenames "
    + "will be different in hopes of helping debug problems. Thus, the BaseDataPathName will "
    + "be appended with a sequential number representing which machine it is supposed to be "
    + "on. The local host will be machine #0 and the others will be numbered from 1 on up.";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "nWorkers";
    case 1:
      return "BaseDataPathName";
    case 2:
      return "InitializationFileName";
    case 3:
      return "DancePath";
    case 4:
      return "NewQueenName";
    case 5:
      return "BaseWorkerName";
    case 6:
      return "QueenTemplateFileName";
    case 7:
      return "WorkerTemplateFileName";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "nWorkers: the number of Worker Bee itineraries to write";
    case 1:
      return "BaseDataPathName: the trunk of the name where the data will be found";
    case 2:
      return "InitializationFileName: where to find the initialization file";
    case 3:
      return "DancePath: the directory where the file-based communication will take place (should be empty)";
    case 4:
      return "NewQueenName: where to put the new Queen itinerary";
    case 5:
      return "BaseWorkerName: where to put the new Worker itineraries";
    case 6:
      return "QueenTemplateFileName: where to find the Queen template";
    case 7:
      return "WorkerTemplateFileName: where to find the Worker template";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.Integer", "java.lang.String", "java.lang.String", "java.lang.String",
        "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { , };
    return types;
  }
  
  private String doFanIn(PrintWriter outputWriter, BufferedReader queenReader,
      String originalLineContents, int nMachines, int yFixedOffset, String collectorName, String theDancePath) throws Exception {
    
    // Beware the MAGIC NUMBER!!!
    int xFanInOffset = 3400;
    int yFanInOffset = 1160;
    
    int moduleEndIndex = 30;
    String currentLineContents = originalLineContents;
    // write this module header
    outputWriter.println(currentLineContents);
    // read and write the first input line (which is more just a
    // comment [unfed NameInput])
    currentLineContents = queenReader.readLine();
    outputWriter.println(currentLineContents);
    // construct the outputs...
    for (int machineIndex = 0; machineIndex < nMachines; machineIndex++){
      outputWriter.println("      <input index=\"" +
          (machineIndex + 1) +"\" classname=\"java.lang.Object\"/>");
    }
    // read and write the output and property lines
    currentLineContents = queenReader.readLine(); // output start
    outputWriter.println(currentLineContents);
    currentLineContents = queenReader.readLine(); // destination definition
    outputWriter.println(currentLineContents);
    currentLineContents = queenReader.readLine(); // output end
    outputWriter.println(currentLineContents);
    
    // look for the final line
    // read another line and check if it is a </module>
    while (true) {
      currentLineContents = queenReader.readLine();
      moduleEndIndex = currentLineContents.indexOf("</modu");
      if (moduleEndIndex == -1) {
        // we're not to the end yet
        //				System.out.println("real [" + currentLineContents + "]");
      } else {
        outputWriter.println(currentLineContents);
        currentLineContents = queenReader.readLine();
        break;
      }
    }
    
    // now we get to put in the little reader combos...
    for (int machineIndex = 0; machineIndex < nMachines; machineIndex++){
      outputWriter.println("<module alias=\"from " + machineIndex + " ReadCap " + collectorName +
          "\" classname=\"ncsa.d2k.modules.projects.dtcheng.matrix.StupidReadingCap\" xloc=\"" +
          (xFanInOffset + 143) + "\" yloc=\"" + (13 + yFanInOffset*machineIndex + yFixedOffset) +"\">");
      outputWriter.println("  <input index=\"0\" classname=\"java.lang.String\"/>");
      outputWriter.println("  <output index=\"0\" classname=\"java.lang.Object\">");
      outputWriter.println("    <destination alias=\"FanIn " +
          collectorName + " Collector\" index=\"" +
          (machineIndex + 1) + "\"/>");
      outputWriter.println("  </output>");
      outputWriter.println("  <property name=\"outputCounts\" binaryValue=\"rO0ABXVyAAJbSU26YCZ26rKlAgAAeHAAAAABAAAAAA==\"/>");
      outputWriter.println("</module>");
      outputWriter.println("<module alias=\"from " + machineIndex + " String: " + collectorName +
          "\" classname=\"ncsa.d2k.modules.projects.dtcheng.generators.GenerateString\" xloc=\"" +
          (1 + xFanInOffset) + "\" yloc=\"" + (12 + yFanInOffset*machineIndex + yFixedOffset) + "\">");
      outputWriter.println("  <output index=\"0\" classname=\"java.lang.String\">");
      outputWriter.println("    <destination alias=\"from " + machineIndex + " ReadCap " + collectorName  + "\" index=\"0\"/>");
      outputWriter.println("  </output>");
      outputWriter.println("  <property name=\"stringData\" value=\"" + theDancePath + machineIndex + "_waggle_" +
          collectorName + "\"/>");
      outputWriter.println("  <property name=\"outputCounts\" binaryValue=\"rO0ABXVyAAJbSU26YCZ26rKlAgAAeHAAAAABAAAAAA==\"/>");
      outputWriter.println("</module>");
      
    }
    
    
    return new String(currentLineContents);
  }
  
  private String doNNWorkerToCollectors(String trainingOrValidation, PrintWriter outputWriter, BufferedReader workerReader,
      String originalLineContents, int ylocShift, int machineIndex) throws Exception {
    
    int outputStartIndex = -1234;
    int outputEndIndex = 12333;
    int outputValue = -4444;
    int destinationIndex = -1233;
    int moduleEndIndex = -1232;
    String currentLineContents = originalLineContents;
    
    // handle the module invocation
    int numberSignIndex = currentLineContents.indexOf("#");
    int ylocStart = currentLineContents.indexOf("yloc") + 6;
    int ylocEnd = currentLineContents.indexOf("\"",ylocStart);
    int ylocValue = Integer.parseInt(currentLineContents.substring(ylocStart,ylocEnd)) +
    ylocShift * machineIndex;
    outputWriter.println(
        currentLineContents.substring(0,numberSignIndex + 1) + machineIndex +
        currentLineContents.substring(numberSignIndex + 1,ylocStart) + ylocValue +
        currentLineContents.substring(ylocEnd)
    );
    // now step through the rest of the module
    while (true) {
      currentLineContents = workerReader.readLine();
      outputStartIndex = currentLineContents.indexOf("<output");
      destinationIndex = currentLineContents.indexOf("<destination");
      moduleEndIndex = currentLineContents.indexOf("</module");
      
      if (moduleEndIndex != -1) {
        // we are done with the module, so write it and
        // break
        outputWriter.println(currentLineContents);
        break;
      } else if (outputStartIndex != -1) {
        // we are observing an output statement; reset it to
        // something useful
        outputStartIndex = currentLineContents.indexOf("\"") + 1;
        outputEndIndex = currentLineContents.indexOf("\"",outputStartIndex + 1);
        outputValue = Integer.parseInt(
            currentLineContents.substring(outputStartIndex,outputEndIndex)
        );
        if (outputValue == 1) {
          // the error or fraction correct: brute force
          // writing
          outputWriter.println("      <output index=\"1\" classname=\"java.lang.Double\">");
          outputWriter.println("        <destination alias=\"FanIn " +
              trainingOrValidation + "Error Collector\" index=\"" + (machineIndex + 1) + "\"/>");
          outputWriter.println("      </output>");
        } else if (outputValue == 2) {
          // the error or fraction correct: brute force
          // writing
          outputWriter.println("      <output index=\"2\" classname=\"java.lang.Double\">");
          outputWriter.println("        <destination alias=\"FanIn " +
              trainingOrValidation + "FracCorrect Collector\" index=\"" + (machineIndex + 1) + "\"/>");
          outputWriter.println("      </output>");
        } else {
          // it is a non-special output, so treat it
          // normally
          outputWriter.println(currentLineContents);
        }
      } else if (destinationIndex == -1) {
        // it is a non-destination type line, so just write
        // it, but continue
        outputWriter.println(currentLineContents);
      } else {
        // it is a destination line, so change it and
        // continue
        numberSignIndex = currentLineContents.indexOf("#");
        outputWriter.println(
            currentLineContents.substring(0,numberSignIndex + 1) + machineIndex +
            currentLineContents.substring(numberSignIndex + 1)
        );
      }
      
    } // end of stepping through module "while"
    
    return new String(currentLineContents);
  }
  private String doFanOut(String destinationBaseAlias, PrintWriter outputWriter, BufferedReader queenReader,
      String originalLineContents, int nMachines, int xFanOutFixed, String theDancePath) throws Exception {
    
    // Beware the MAGIC NUMBER!!!
    int xFanOutOffset = 2400;
    int yFanOutOffset = 160;
    
    int moduleEndIndex = 0;
    String currentLineContents = originalLineContents;
    // write this module header
    outputWriter.println(currentLineContents);
    //		System.out.println(currentLineContents);
    // read and write the input line
    currentLineContents = queenReader.readLine();
    outputWriter.println(currentLineContents);
    //		System.out.println(currentLineContents);
    // construct the outputs...
    for (int machineIndex = 0; machineIndex < nMachines; machineIndex++){
      // to the WriteCap itself...
      // watch out for the math tricks...
      outputWriter.println("      <output index=\"" +
          (2*machineIndex) + "\" classname=\"java.lang.Object\">");
      outputWriter.println("        <destination alias=\"to " + machineIndex +
          " WriteCap " + destinationBaseAlias + "\" index=\"" + 0 + "\"/>");
      outputWriter.println("      </output>");
      // to the filename trigger...
      outputWriter.println("      <output index=\"" +
          (2*machineIndex + 1) + "\" classname=\"java.lang.Object\">");
      outputWriter.println("        <destination alias=\"T: to " + machineIndex + " " +
          destinationBaseAlias + "\" index=\"" + 1 + "\"/>");
      outputWriter.println("      </output>");
    }
    // read another line and check if it is a </module>
    while (true) {
      currentLineContents = queenReader.readLine();
      moduleEndIndex = currentLineContents.indexOf("</modu");
      if (moduleEndIndex == -1) {
        // we're not to the end yet
        outputWriter.println(currentLineContents);
        //				System.out.println("real [" + currentLineContents + "]");
      } else {
        outputWriter.println(currentLineContents);
        currentLineContents = queenReader.readLine();
        break;
      }
    }
    
    // ok, now we need to put in the FanOutConstellation Modules...
    for (int machineIndex = 0; machineIndex < nMachines; machineIndex++){
      // a little brute force...
      outputWriter.println("<module alias=\"to " + machineIndex +
          " String: " + destinationBaseAlias +
          "\" classname=\"ncsa.d2k.modules.projects.dtcheng.generators.GenerateString\" xloc=\"" +
          (35 + xFanOutOffset + xFanOutFixed) + "\" yloc=\"" + (34*machineIndex + yFanOutOffset) + "\">");
      outputWriter.println("  <output index=\"0\" classname=\"java.lang.String\">");
      outputWriter.println("    <destination alias=\"T: to " + machineIndex + " " + destinationBaseAlias + "\" index=\"0\"/>");
      outputWriter.println("  </output>");
      outputWriter.println("  <property name=\"stringData\" value=\"" + theDancePath + machineIndex +
          "_waggle_" + destinationBaseAlias + "\"/>");
      outputWriter.println("  <property name=\"outputCounts\" binaryValue=\"rO0ABXVyAAJbSU26YCZ26rKlAgAAeHAAAAABAAAAAQ==\"/>");
      outputWriter.println("</module>");
      outputWriter.println("<module alias=\"T: to " + machineIndex + " " + destinationBaseAlias + 
          "\" classname=\"ncsa.d2k.modules.core.control.TriggerPushB\" xloc=\"" +
          (52 + xFanOutOffset + xFanOutFixed) + "\" yloc=\"" + (112*machineIndex + yFanOutOffset) + "\">");
      outputWriter.println("<input index=\"0\" classname=\"java.lang.Object\"/>");
      outputWriter.println("<input index=\"1\" classname=\"java.lang.Object\"/>");
      outputWriter.println("<output index=\"0\" classname=\"java.lang.Object\">");
      outputWriter.println("  <destination alias=\"to " + machineIndex + " WriteCap " + destinationBaseAlias + "\" index=\"1\"/>");
      outputWriter.println("  </output>");
      outputWriter.println("<property name=\"debug\" value=\"false\"/>");
      outputWriter.println("<property name=\"outputCounts\" binaryValue=\"rO0ABXVyAAJbSU26YCZ26rKlAgAAeHAAAAABAAAAAQ==\"/>");
      outputWriter.println("</module>");
      outputWriter.println("<module alias=\"to " + machineIndex +
          " WriteCap " + destinationBaseAlias +
          "\" classname=\"ncsa.d2k.modules.projects.dtcheng.matrix.StupidWritingCap\" xloc=\"" +
          (156 + xFanOutOffset + xFanOutFixed) + "\" yloc=\"" + (2*machineIndex + yFanOutOffset) + "\">");
      outputWriter.println("<input index=\"0\" classname=\"java.lang.Object\"/>");
      outputWriter.println("<input index=\"1\" classname=\"java.lang.String\"/>");
      outputWriter.println("<property name=\"outputCounts\" binaryValue=\"rO0ABXVyAAJbSU26YCZ26rKlAgAAeHAAAAAA\"/>");
      outputWriter.println("</module>");
      
    }
    
    
    return new String(currentLineContents);
  }
  private String setProperty(String propertyName, Object propertyValue, String originalLineContents, PrintWriter outputWriter, BufferedReader queenReader) throws Exception {
    String currentLineContents = originalLineContents;
    
    //		System.out.println("Setting property [" + propertyName + "] to [" + propertyValue + "]");
    int moduleEndIndex = -58;
    int propertyEndIndex = -59;
    
    // write out the first line
    outputWriter.println(currentLineContents); // module invocation
    // look for the property line with the value in it, otherwise
    // just write them back down
    while (true) {
      currentLineContents = queenReader.readLine();
      moduleEndIndex = currentLineContents.indexOf("</module");
      propertyEndIndex = currentLineContents.indexOf(propertyName);
      if (moduleEndIndex != -1) {
        // we are done with the module, so write it,
        // read the next, and break
        outputWriter.println(currentLineContents);
        currentLineContents = queenReader.readLine();
        break;
      } else if (propertyEndIndex != -1) {
        // write it ourselves, but then continue...
        outputWriter.println("      <property name=\"" + propertyName + "\" value=\"" +
            propertyValue + "\"/>");
      } else {
        outputWriter.println(currentLineContents);
      }
    }
    return new String(currentLineContents);
  }
  private String setWorkerProperty(String propertyName, Object propertyValue, int machineIndex, int ylocShift, String originalLineContents, PrintWriter outputWriter, BufferedReader workerReader) throws Exception {
    String currentLineContents = originalLineContents;
    
    //		System.out.println("Setting property [" + propertyName + "] to [" + propertyValue + "] (worker #" + machineIndex + ")");
    
    int moduleEndIndex = -58;
    int propertyEndIndex = -59;
    int numberSignIndex = 53;
    int ylocStart = 444;
    int ylocEnd = 1234;
    int ylocValue = 513;
    int destinationIndex = 555;
    
    // handle the module invocation
    numberSignIndex = currentLineContents.indexOf("#");
    ylocStart = currentLineContents.indexOf("yloc") + 6;
    ylocEnd = currentLineContents.indexOf("\"",ylocStart);
    ylocValue = Integer.parseInt(currentLineContents.substring(ylocStart,ylocEnd)) +
    ylocShift * machineIndex;
    outputWriter.println(
        currentLineContents.substring(0,numberSignIndex + 1) + machineIndex +
        currentLineContents.substring(numberSignIndex + 1,ylocStart) + ylocValue +
        currentLineContents.substring(ylocEnd)
    );
    // now step through the rest of the module
    while (true) {
      currentLineContents = workerReader.readLine();
      destinationIndex = currentLineContents.indexOf("<destination");
      moduleEndIndex = currentLineContents.indexOf("</module");
      propertyEndIndex = currentLineContents.indexOf(propertyName);
      
      // look for the property line with the value in it, otherwise
      // just write them back down with necessary adjustments
      if (moduleEndIndex != -1) {
        // we are done with the module, so write it and
        // break
        outputWriter.println(currentLineContents);
        break;
      }  else if (propertyEndIndex != -1) {
        // write it ourselves, but then continue...
        outputWriter.println("      <property name=\"" + propertyName + "\" value=\"" +
            propertyValue + "\"/>");
      } else if (destinationIndex == -1) {
        // it is a non-destination type line, so just write
        // it, but continue
        outputWriter.println(currentLineContents);
      } else {
        // it is a destination line, so change it and
        // continue
        numberSignIndex = currentLineContents.indexOf("#");
        outputWriter.println(
            currentLineContents.substring(0,numberSignIndex + 1) + machineIndex +
            currentLineContents.substring(numberSignIndex + 1)
        );
      }
    } // end of stepping through module "while"
    return new String(currentLineContents);
  }
  
  
  public void doit() throws Exception {
    
    int nWorkers = ((Integer)this.pullInput(0)).intValue();
    String BaseDataPathName = (String)this.pullInput(1);
    String InitializationFileName = (String)this.pullInput(2);
    String DancePath = (String)this.pullInput(3);
    String NewQueenName = (String)this.pullInput(4);
    String BaseWorkerName = (String)this.pullInput(5);
    String QueenTemplateFileName = (String)this.pullInput(6);
    String WorkerTemplateFileName = (String)this.pullInput(7);
    
    int largeInteger = 10000000; // Beware the MAGIC NUMBER!!! the
    // readAheadLimit
    
    /*
     * read the machines file and put the contents into a string and int
     * array
     */
    
    // get just the "name" part of the new itinerary names
    String justQueenName = new String();
    int slashIndex = NewQueenName.lastIndexOf("/");
    int backSlashIndex = NewQueenName.lastIndexOf("\\");
    int nameStartIndex = Math.max(slashIndex,backSlashIndex);
    justQueenName = NewQueenName.substring(nameStartIndex + 1); 
    
    String justWorkerBase = new String();
    slashIndex = BaseWorkerName.lastIndexOf("/");
    backSlashIndex = BaseWorkerName.lastIndexOf("\\");
    nameStartIndex = Math.max(slashIndex,backSlashIndex);
    justWorkerBase = BaseWorkerName.substring(nameStartIndex + 1); 
    
    
    //----------begin new goodies
    
    // set up some MAGIC NUMBERS!!!
    int ylocShift = 0; // Beware the MAGIC NUMBER!!! vertical offset
    
    // set up some variables to use...
    int moduleStartIndex = -3;
    int itineraryEndIndex = -16;
    int proximityStartIndex = -6;
    int fieldFirstQuote = -4;
    int fieldLastQuote = -5;
    int numberSignIndex = -100;
    int ylocStart = -101;
    int ylocEnd = -1010;
    int ylocValue = -104;
    int moduleEndIndex = -109;
    int destinationIndex = -102;
    
    String moduleAlias = new String();
    String QueenLineContents = new String();
    
    // do the Queen...
    
    /* Create the new Queen itinerary stuff */
    File newQueenFileObject = new File(NewQueenName);
    FileOutputStream newQueenStream = new FileOutputStream(newQueenFileObject);
    PrintWriter NewQueenWriter = new PrintWriter(newQueenStream);
    
    /* Prepare to read the templates */
    File QueenFileObject = new File(QueenTemplateFileName);
    FileReader QueenStream = new FileReader(QueenFileObject);
    BufferedReader QueenReader = new BufferedReader(QueenStream);
    
    // mark the beginning of the stream2;
    QueenReader.mark(largeInteger);
    
    /*
     * the idea here is to step through the Queen Template until we hit the
     * end this will be extremely MAGIC-ized in that it is a line by line
     * decision making process that will depend totally on the order of
     * things in the QueenTemplate
     * 
     * the WHILE LOOPS will be structured so as to read the next line at the
     * end of the loop except for the very first lines. but this means that
     * you begin testing knowing that what came before had already decided
     * it didn't want that line.
     */
    
    // we're just gonna write the first couple of lines ourselves...
    
    NewQueenWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    NewQueenWriter.println("<toolkit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-Instance\" xsi:noNamespaceSchemaLocation=\"itinerary.xsd\">");
    NewQueenWriter.println("  <itinerary label=\"" + justQueenName + "\">");
    NewQueenWriter.println("    <annotation><![CDATA[<HTML> </HTML>]]></annotation>");
    
    /*
     * first the lame header stuff... the idea is read in until we hit some
     * module definitions
     */
    // read the very first line...
    QueenLineContents = QueenReader.readLine();
    while (true) {
      // look for the module definitions
      // still in the headers, so write out what we've got
      moduleStartIndex = QueenLineContents.indexOf("<module");
      if (moduleStartIndex != -1) {
        // we are done with the headers because we found a module
        break;
        // otherwise, we're still in the headers
      }
      QueenLineContents = QueenReader.readLine();
    }
    /*
     * System.out.println("Got to here: done with Queen headers...");
     * NewItineraryWriter.flush();
     */
    // module interpreter
    System.out.println("nMachines = " + nWorkers);
    while (true) {
      // check if it is one of the modules that goes to the Workers
      fieldFirstQuote = QueenLineContents.indexOf("alias=\"") + 6;
      fieldLastQuote = QueenLineContents.indexOf("\"",fieldFirstQuote + 1);
      moduleAlias = QueenLineContents.substring(fieldFirstQuote + 1,fieldLastQuote);
      
      //			System.out.println("moduleAlias = [" + moduleAlias + "]");
      // search through all the modules that need to be connected to the workers
      if (moduleAlias.equals("Long: nChunks")) {
        QueenLineContents = setProperty("constantValue", new Integer(nWorkers), QueenLineContents,
            NewQueenWriter, QueenReader);
      } else if (moduleAlias.equals("String: Initialization FileName")) {
        QueenLineContents = setProperty("stringData", InitializationFileName, QueenLineContents,
            NewQueenWriter, QueenReader);
      } else if (moduleAlias.equals("FanOut LayerTable to Workers")) {
        QueenLineContents = doFanOut("LayerTable", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 0, DancePath);
      } else if (moduleAlias.equals("FanOut N_to_LT to Workers")){
        QueenLineContents = doFanOut("N_to_LT", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 200, DancePath);
      } else if (moduleAlias.equals("FanOut L_sf_NT to Workers")){
        QueenLineContents = doFanOut("L_sf_NT", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 400, DancePath);
      } else if (moduleAlias.equals("FanOut Wn_ft_NT to Workers")){
        QueenLineContents = doFanOut("Wn_ft_NT", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 600, DancePath);
      } else if (moduleAlias.equals("FanOut nElementsThreshold to Workers")){
        QueenLineContents = doFanOut("nElementsThreshold", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 800, DancePath);
      } else if (moduleAlias.equals("FanOut FractionInTraining to Workers")){
        QueenLineContents = doFanOut("FracInTraining", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 1000, DancePath);
      } else if (moduleAlias.equals("FanOut NewWeights to Workers")){
        QueenLineContents = doFanOut("Weights", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 1200, DancePath);
      } else if (moduleAlias.equals("FanOut NewBiases to Workers")){
        QueenLineContents = doFanOut("Biases", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 1400, DancePath);
      } else if (moduleAlias.equals("FanOut CheckFlag to Workers")){
        QueenLineContents = doFanOut("CheckFlag", NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 1600, DancePath);
      }
      /*
       * now, we must check if it is one of the modules that collects from
       * the Workers if so, i need to make sure it collects from all of
       * them
       * 
       * Beware the MAGIC ASSUMPTION!!! the input indices will be offset
       * by one because there is an unfed NameInput module that serves as
       * a visual comment which will occupy the zeroth input index. thus,
       * machine "m" will connect to input "m+1" rather than "m"
       */				
      else if (moduleAlias.equals("FanIn TrainingError Collector")){
        QueenLineContents = doFanIn(NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 150, "TrainingError", DancePath);
      } else if (moduleAlias.equals("FanIn TrainingFracCorrect Collector")){
        QueenLineContents = doFanIn(NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 160, "TrainingFracCorrect", DancePath);
      } else if (moduleAlias.equals("FanIn BiasGradient Collector")){
        QueenLineContents = doFanIn(NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 170, "BiasGradient", DancePath);
      } else if (moduleAlias.equals("FanIn WeightGradient Collector")){
        QueenLineContents = doFanIn(NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 180, "WeightGradient", DancePath);
      } else if (moduleAlias.equals("FanIn ValidationFracCorrect Collector")){
        QueenLineContents = doFanIn(NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 190, "ValidationFracCorrect", DancePath);
      } else if (moduleAlias.equals("FanIn ValidationError Collector")){
        QueenLineContents = doFanIn(NewQueenWriter, QueenReader, QueenLineContents, nWorkers, 200, "ValidationError", DancePath);
      } else {
        /*
         * it is a module, just not one we want to mess with... we need
         * to go until we find the next module or a proximity or /itinerary
         */
        while (true) {
          if (QueenLineContents.endsWith(">")) {
            //						NewItineraryWriter.println(QueenLineContents + " <!--not messed with... " + moduleAlias + "-->");
            NewQueenWriter.println(QueenLineContents);
          } else {
            NewQueenWriter.println(QueenLineContents);
            
          }
          QueenLineContents = QueenReader.readLine();
          
          moduleStartIndex = QueenLineContents.indexOf("<module");
          proximityStartIndex = QueenLineContents.indexOf("<prox");
          itineraryEndIndex = QueenLineContents.indexOf("</itin");
          if (!(moduleStartIndex == -1 && proximityStartIndex == -1 && itineraryEndIndex == -1)) {
            // we are done here, allow it to pass on...
            break;
          }
        }
        if (moduleStartIndex == -1) {
          /*
           * it is not a module. that is, it is the beginning of the
           * proximities or the end of the itinerary so we need to
           * move on to a different set of logic
           */
          break;
        }
      }
    } // end of module interpreter
    
    // write the last few bits...
    NewQueenWriter.print("  </itinerary>\n</toolkit>");
    //		System.out.println("  </itinerary>\n</toolkit>");
    
    // clean up the mess and get ready to exit.
    NewQueenWriter.flush();
    NewQueenWriter.close();
    newQueenStream.close();
    QueenReader.close();
    QueenStream.close();
    
    
    
    ///////////////////////
    // do the Workers... //
    ///////////////////////
    
    // set up the template...
    File WorkerFileObject = new File(WorkerTemplateFileName);
    FileReader WorkerStream = new FileReader(WorkerFileObject);
    BufferedReader WorkerReader = new BufferedReader(WorkerStream);
    
    WorkerReader.mark(largeInteger);
    String WorkerLineContents = new String();
    
    
    
    // loop over the number of workers
    
    String workerFileNameToWrite = new String();
    
    for (int workerIndex=0; workerIndex < nWorkers; workerIndex++) {
      // set up the worker bee output itinerary...
      workerFileNameToWrite = BaseWorkerName + workerIndex + ".itn";
      
      /* Create the new Worker itinerary stuff */
      File newWorkerFileObject = new File(workerFileNameToWrite);
      FileOutputStream newWorkerStream = new FileOutputStream(newWorkerFileObject);
      PrintWriter NewWorkerWriter = new PrintWriter(newWorkerStream);
      
      
      // we're just gonna write the first couple of lines ourselves...
      NewWorkerWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      NewWorkerWriter.println("<toolkit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-Instance\" xsi:noNamespaceSchemaLocation=\"itinerary.xsd\">");
      NewWorkerWriter.println("  <itinerary label=\"" + justWorkerBase + workerIndex + "\">");
      NewWorkerWriter.println("    <annotation><![CDATA[<HTML> </HTML>]]></annotation>");
      
      /*
       * first the lame header stuff... the idea is read in until we hit some
       * module definitions
       */
      // read the very first line...
      WorkerLineContents = WorkerReader.readLine();
      while (true) {
        // look for the module definitions
        // still in the headers, so write out what we've got
        moduleStartIndex = WorkerLineContents.indexOf("<module");
        if (moduleStartIndex != -1) {
          // we are done with the headers because we found a module
          break;
          // otherwise, we're still in the headers
        }
        WorkerLineContents = WorkerReader.readLine();
      }
      
      // read and write the worker template to the new itinerary
      while (true) {
        
        /* now, i need to read in the module name and put in a machine number  */			
        
        fieldFirstQuote = WorkerLineContents.indexOf("alias=\"") + 6;
        fieldLastQuote = WorkerLineContents.indexOf("\"",fieldFirstQuote + 1);
        
        moduleAlias = WorkerLineContents.substring(fieldFirstQuote + 1,fieldLastQuote);
        
        // check if it is one of the NN activators which need to output
        // properly
        if (moduleAlias.equals("# String: Full Dataset")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(BaseDataPathName + workerIndex + "_"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: nElementsThreshold")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "nElementsThreshold"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: FracInTraining")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "FracInTraining"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: Biases")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "Biases"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: Weights")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "Weights"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: LayerTable")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "LayerTable"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: L_sf_NT")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "L_sf_NT"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: N_to_LT")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "N_to_LT"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: Wn_ft_NT")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "Wn_ft_NT"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: CheckFlag")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "CheckFlag"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: TrainingError")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "TrainingError"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: TrainingFracCorrect")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "TrainingFracCorrect"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: BiasGradient")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "BiasGradient"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: WeightGradient")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "WeightGradient"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: ValidationError")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "ValidationError"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        } else if (moduleAlias.equals("# String: ValidationFracCorrect")) {
          WorkerLineContents = setWorkerProperty("stringData",
              new String(DancePath + workerIndex + "_waggle_" + "ValidationFracCorrect"),
              workerIndex, ylocShift, WorkerLineContents, NewWorkerWriter,
              WorkerReader);
        }
        
        // otherwise, it is a standard module...
        else {
          // handle the module invocation
          numberSignIndex = WorkerLineContents.indexOf("#");
          ylocStart = WorkerLineContents.indexOf("yloc") + 6;
          ylocEnd = WorkerLineContents.indexOf("\"",ylocStart);
          ylocValue = Integer.parseInt(WorkerLineContents.substring(ylocStart,ylocEnd)) +
          ylocShift * workerIndex;
          NewWorkerWriter.println(
              WorkerLineContents.substring(0,numberSignIndex + 1) + workerIndex +
              WorkerLineContents.substring(numberSignIndex + 1,ylocStart) + ylocValue +
              WorkerLineContents.substring(ylocEnd)
          );
          // now step through the rest of the module
          while (true) {
            WorkerLineContents = WorkerReader.readLine();
            destinationIndex = WorkerLineContents.indexOf("<destination");
            moduleEndIndex = WorkerLineContents.indexOf("</module");
            
            if (moduleEndIndex != -1) {
              // we are done with the module, so write it and
              // break
              NewWorkerWriter.println(WorkerLineContents);
              break;
            } else if (destinationIndex == -1) {
              // it is a non-destination type line, so just write
              // it, but continue
              NewWorkerWriter.println(WorkerLineContents);
            } else {
              // it is a destination line, so change it and
              // continue
              numberSignIndex = WorkerLineContents.indexOf("#");
              NewWorkerWriter.println(
                  WorkerLineContents.substring(0,numberSignIndex + 1) + workerIndex +
                  WorkerLineContents.substring(numberSignIndex + 1)
              );
            }
          } // end of stepping through module "while"
        } // end of standard module "else"
        
        WorkerLineContents = WorkerReader.readLine();
        itineraryEndIndex = WorkerLineContents.indexOf("</itinerary>");
        if (itineraryEndIndex != -1) {
          break;
        }
      }
      NewWorkerWriter.print("  </itinerary>\n</toolkit>");
      
      // finish up this one and get ready to go...
      
      NewWorkerWriter.flush();
      NewWorkerWriter.close();
      newWorkerStream.close();
      
      WorkerReader.reset(); // reset for the next time through
    }
    
    WorkerReader.close();
    WorkerStream.close();
   
    
    //////////////////////////////
    //----------end new goodies //
    //////////////////////////////
    
    
  }
  
}