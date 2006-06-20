package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

public class RingNNWriter0 extends OutputModule {
  
  public String getModuleName() {
    return "RingNNWriter0";
  }
  
  public String getModuleInfo() {
    return "This is a new automated itinerary writer working off of 3 templates. " +
    "It creates itineraries that are linked end to end in a ring. <p>" +
    "This also makes the assumption that only ONE itinerary will be running on each " +
    "physical machine. This is NOT checked for. The ports are not changed from their " +
    "values in the templates. <p>" +
    "This module is a pure brute force, fully MAGIC'ed attempt "
    + "to have a module that writes a very specific kind of itinerary. "
    + "As it is written, it <i><b>will not</b></i> be re-use-able.<p>"
    + "That being said... the point of this is to take three templates and "
    + "write an itinerary that executes my (Ricky's, that is) neural network "
    + "solver across an arbitrary number of machines. This will require "
    + "pre-positioning the data and all sorts of yummy things. <p>"
    + "Ok, the MachineListFileName is the name of a file containing the "
    + "list of machines we want to run the itinerary on <i>INCLUDING</i> the localhost. " +
    "It should have the "
    + "following format: ipaddress,# of threads,anything else. "
    + "The BaseDataPathName is the base name of where the data will be found "
    + "on the machines. It is assumed that the data will be forward-staged/pre-positioned "
    + "such that it is always in the same directory on each machine, but the filenames "
    + "will be different in hopes of helping debug problems. Thus, the BaseDataPathName will "
    + "be appended with a sequential number representing which machine it is supposed to be "
    + "on. The first computer on the list will be machine #0 and the others will be numbered from 1 on up.";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "MachineListFileName";
    case 1:
      return "BaseDataPathName";
    case 2:
      return "InitializationFileName";
    case 3:
      return "StarterTemplateFileName";
    case 4:
      return "IntermediateTemplateFileName";
    case 5:
      return "EndTemplateFileName";
    case 6:
      return "NewItineraryBaseName";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "MachineListFileName";
    case 1:
      return "BaseDataPathName";
    case 2:
      return "InitializationFileName";
    case 3:
      return "StarterTemplateFileName";
    case 4:
      return "IntermediateTemplateFileName";
    case 5:
      return "EndTemplateFileName";
    case 6:
      return "NewItineraryBaseName: put the path, but not the \".itn\"";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.String", "java.lang.String", "java.lang.String",
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
  
  private Object[] readMachineList(int readAheadLimit, String machineListPath) throws Exception {
    //    new Object[] bundleToReturn = null;
    
    /*
     * read the machines file and put the contents into a string and int
     * array
     */
    File MachineListFileObject = new File(machineListPath);
    FileReader MachineStream = null;
    MachineStream = new FileReader(MachineListFileObject);
    BufferedReader MachineReader = new BufferedReader(MachineStream);
    MachineReader.mark(readAheadLimit);
    
    // run through and find out how many there are...
    int nMachines = 0;
    String MachineLineContents = new String();
    
    while (true) {
      MachineLineContents = MachineReader.readLine();
      if (MachineLineContents != null) {
        nMachines++;
      } else {
        break;
      }
    }
    MachineReader.reset();
    
    System.out.println("nMachines = " + nMachines);
    int commaIndex = 0;
    String[] machineName = new String[nMachines];
    int[] nThreads = new int[nMachines];
    
    // Beware: skipping index 0 for the localhost...
    //		MachineName[0] = "localhost";
    //		nThreads[0] = nThreadsLocalhost;
    for (int machineIndex = 0; machineIndex < nMachines; machineIndex++) {
      MachineLineContents = MachineReader.readLine();
      //			System.out.println("machine line = [" + MachineLineContents + "]");
      commaIndex = MachineLineContents.indexOf(",");
      machineName[machineIndex] = MachineLineContents.substring(0,commaIndex);
      nThreads[machineIndex] = Integer.parseInt(
          MachineLineContents.substring(commaIndex + 1,
              MachineLineContents.indexOf(",",commaIndex + 1))
      );
      //      System.out.println("#" + machineIndex + " " + machineName[machineIndex] +
      //          " " + nThreads[machineIndex]);
    }
    
    MachineReader.close();
    MachineStream.close();
    
    //    System.out.println("machineName = " + machineName);
    return new Object[] {machineName, nThreads};
  }

  private void writeAnItinerary(ModuleProcessor templateModuleHandler, int indexToHandle,
      String ipAddressForNextKillSignal, String ipAddressForNextMachine,
      String baseDataPathName, String initializationFileName, PrintWriter newItineraryWriter) throws Exception{

    boolean allTheModulesHaveBeenRead = false;
    
    while (!allTheModulesHaveBeenRead){
//      System.out.println("    ---reading a module---");
      String moduleClass = null;
      String moduleAlias = null;
      String[] finalModuleText;
      // read a module
      templateModuleHandler.readAModule();
      allTheModulesHaveBeenRead = templateModuleHandler.doneWithModules();

      if (!allTheModulesHaveBeenRead) {
        moduleClass = templateModuleHandler.getClassName();
        
//            System.out.println("Module #" + starterModuleHandler.nModulesRead() + " was [" +
//                starterModuleHandler.getModuleAlias() + "] of class [" +
//                starterModuleHandler.getClassName() + "]");

            // rename it and its destinations...
//            System.out.println("changed? " + starterModuleHandler.contentsHaveBeenChanged());
        templateModuleHandler.prependAliases( ("#" + indexToHandle + " ") );
      }
      
      // process to see if it is something we want to change...
      if (templateModuleHandler.doneWithModules()) {
//        System.out.println("<-- done reading because doneWithModules = [" + templateModuleHandler.doneWithModules() + "]");
        allTheModulesHaveBeenRead = true;
      } else if (moduleClass.equals("ncsa.d2k.modules.projects.dtcheng.matrix.KillSender")) {
        templateModuleHandler.setPropertyValue("computerName", ipAddressForNextKillSignal);
//        System.out.println("in kill sender");
      } else if (moduleClass.equals("ncsa.d2k.modules.projects.dtcheng.matrix.KillThisItinerary")) {
        templateModuleHandler.setPropertyValue("nextComputer", ipAddressForNextKillSignal);
//        System.out.println("in kill this itinerary");
      } else if (moduleClass.equals("ncsa.d2k.modules.projects.dtcheng.matrix.SenderGolf") ||
          moduleClass.equals("ncsa.d2k.modules.projects.dtcheng.matrix.SenderHotel")) {
        templateModuleHandler.setPropertyValue("computerName", ipAddressForNextMachine);
//        System.out.println("in sender golf");
      } else if (moduleClass.equals("ncsa.d2k.modules.projects.dtcheng.generators.GenerateString")) {
        // now we need to check on the name...
        moduleAlias = templateModuleHandler.getModuleAlias();
//        System.out.println("in string logic; Data = " + moduleAlias.indexOf("Data") + "; nit = " + moduleAlias.indexOf("nit"));
        if (moduleAlias.indexOf("Data") > -1) {
          // let's assume that this means the data path...
          templateModuleHandler.setPropertyValue("stringData", (baseDataPathName + indexToHandle + "_"));
//          System.out.println("in data path");
        } else if (moduleAlias.indexOf("nit") > -1) {
          // let's assume this means the initialization file path...
          	templateModuleHandler.setPropertyValue("stringData", initializationFileName);
//          System.out.println("in init path");
        } else {
          // do nothing...
        }
      } else {
        // do nothing...
      }
      
      // now write it out...
//      System.out.println("   ready to write module; allRead = " + allTheModulesHaveBeenRead + "; moduleClass = [" +
//          moduleClass + "]");
      if (!allTheModulesHaveBeenRead) {
        // get the currently modified module and write it to the new file...
        finalModuleText = templateModuleHandler.theModulesLines();
        for (int lineIndex = 0; lineIndex < finalModuleText.length; lineIndex++) {
          newItineraryWriter.println(finalModuleText[lineIndex]);
        }
      } else {
//        System.out.println("...all modules now read...");
        // we are all done with the modules, just clean up the itinerary...
        newItineraryWriter.println("  </itinerary>");
        newItineraryWriter.println("</toolkit>");
        newItineraryWriter.flush();
        
        // clean up the streams...
        newItineraryWriter.close();
//        newItineraryStream.close();
//        
//        starterReader.close();
//        starterStream.close();    
      }
    }
    
  }
  
  
  
  
  public void doit() throws Exception {
    
    String MachineListFileName = (String)this.pullInput(0);
    String BaseDataPathName = (String)this.pullInput(1);
    String InitializationFileName = (String)this.pullInput(2);
    String StarterTemplateFileName = (String)this.pullInput(3);
    String IntermediateTemplateFileName = (String)this.pullInput(4);
    String EndTemplateFileName = (String)this.pullInput(5);
    String NewItineraryName = (String)this.pullInput(6);
    
    int largeInteger = 10000000; // Beware the MAGIC NUMBER!!! the
    // readAheadLimit
    int moduleBufferSize = 100; // Beware the MAGIC NUMBER!!!
    
    Object[] machineAndThreadLists = readMachineList(largeInteger, MachineListFileName);
    
    String[] machineList = (String[]) machineAndThreadLists[0];
    //    int[] nThreads = (int[]) machineAndThreadLists[1];
    int nTotalMachines = machineList.length;
    
    // get just the "name" part of the new itinerary name
    String justItineraryName = new String();
    int slashIndex = NewItineraryName.lastIndexOf("/");
    int backSlashIndex = NewItineraryName.lastIndexOf("\\");
    int nameStartIndex = Math.max(slashIndex,backSlashIndex);
    justItineraryName = NewItineraryName.substring(nameStartIndex + 1); 
    
    ///////////////////////////////////
    int moduleStartIndex = -3;
    String templateLineContents = null;
    
    int currentMachineIndex = 0;
    String ipAddressForNextMachine = null;
    String ipAddressForNextKillSignal = null;
    if (currentMachineIndex == (nTotalMachines - 1)) {
      ipAddressForNextMachine = new String(machineList[0]);
      ipAddressForNextKillSignal = new String("");
    } else {
      ipAddressForNextMachine = new String(machineList[currentMachineIndex + 1]);
      ipAddressForNextKillSignal = new String(ipAddressForNextMachine);
    }
    
    ///////////////////////////
    // do the starter itinerary
    ///////////////////////////
    
    // open up the output file object
    File newItineraryFileObject = new File(NewItineraryName + currentMachineIndex + ".itn");
    FileOutputStream newItineraryStream = new FileOutputStream(newItineraryFileObject);
    PrintWriter newItineraryWriter = new PrintWriter(newItineraryStream);
    
    // open up the template file object
    File starterFileObject = new File(StarterTemplateFileName);
    FileReader starterStream = new FileReader(starterFileObject);
    BufferedReader starterReader = new BufferedReader(starterStream);
    
    
    // we're just gonna write the first couple of lines ourselves...
    
    newItineraryWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    newItineraryWriter.println("<toolkit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-Instance\" xsi:noNamespaceSchemaLocation=\"itinerary.xsd\">");
    newItineraryWriter.println("  <itinerary label=\"" + justItineraryName + currentMachineIndex + ".itn\">");
    newItineraryWriter.println("    <annotation><![CDATA[<HTML> </HTML>]]></annotation>");
    
    
    // place the reader on the first module definition...
    while (true) {
      // look for the module definitions
      starterReader.mark(largeInteger); // mark this place so we can reset to it...
      templateLineContents = starterReader.readLine();
      moduleStartIndex = templateLineContents.indexOf("<module");
      if (moduleStartIndex != -1) {
        // we are done with the headers because we found a module
        break;
        // otherwise, we're still in the headers
      }
    }
    // the reader is reset so that the next line read will be the module invocation
    starterReader.reset();
    
    
    // start processing the itinerary one module at a time...
    ModuleProcessor starterModuleHandler = new ModuleProcessor(starterReader, moduleBufferSize);

    writeAnItinerary(starterModuleHandler, currentMachineIndex, ipAddressForNextKillSignal,
        ipAddressForNextMachine, BaseDataPathName, InitializationFileName, newItineraryWriter);

    // clean up the streams...
    newItineraryWriter.close();
    newItineraryStream.close();
    
    starterReader.close();
    starterStream.close();    
    
    currentMachineIndex++;

    ///////////////////////////
    // do all of the intermediate itineraries
    ///////////////////////////
    
    for (int machineIndex = 1; machineIndex < nTotalMachines - 1; machineIndex++) {

      if (currentMachineIndex == (nTotalMachines - 1)) {
        ipAddressForNextMachine = new String(machineList[0]);
        ipAddressForNextKillSignal = new String("");
      } else {
        ipAddressForNextMachine = new String(machineList[currentMachineIndex + 1]);
        ipAddressForNextKillSignal = new String(ipAddressForNextMachine);
      }
      
      // open up the output file object
      File intermediateItineraryFileObject = new File(NewItineraryName + currentMachineIndex + ".itn");
      FileOutputStream intermediateItineraryStream = new FileOutputStream(intermediateItineraryFileObject);
      PrintWriter intermediateItineraryWriter = new PrintWriter(intermediateItineraryStream);
      
      // open up the template file object
      File intermediateFileObject = new File(IntermediateTemplateFileName);
      FileReader intermediateStream = new FileReader(intermediateFileObject);
      BufferedReader intermediateReader = new BufferedReader(intermediateStream);
      
      // we're just gonna write the first couple of lines ourselves...
      
      intermediateItineraryWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      intermediateItineraryWriter.println("<toolkit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-Instance\" xsi:noNamespaceSchemaLocation=\"itinerary.xsd\">");
      intermediateItineraryWriter.println("  <itinerary label=\"" + justItineraryName + currentMachineIndex + ".itn\">");
      intermediateItineraryWriter.println("    <annotation><![CDATA[<HTML> </HTML>]]></annotation>");
      
      // place the reader on the first module definition...
      while (true) {
        // look for the module definitions
        intermediateReader.mark(largeInteger); // mark this place so we can reset to it...
        templateLineContents = intermediateReader.readLine();
        moduleStartIndex = templateLineContents.indexOf("<module");
        if (moduleStartIndex != -1) {
          // we are done with the headers because we found a module
          break;
          // otherwise, we're still in the headers
        }
      }
      // the reader is reset so that the next line read will be the module invocation
      intermediateReader.reset();
      
      // start processing the itinerary one module at a time...
      ModuleProcessor intermediateModuleHandler = new ModuleProcessor(intermediateReader, moduleBufferSize);

      writeAnItinerary(intermediateModuleHandler, currentMachineIndex, ipAddressForNextKillSignal,
          ipAddressForNextMachine, BaseDataPathName, InitializationFileName, intermediateItineraryWriter);

      // clean up the streams...
      intermediateItineraryWriter.close();
      intermediateItineraryStream.close();
      
      intermediateReader.close();
      intermediateStream.close();    
      
      currentMachineIndex++;
    }
    
    ///////////////////////////
    // do the end itinerary
    ///////////////////////////

    if (currentMachineIndex == (nTotalMachines - 1)) {
      ipAddressForNextMachine = new String(machineList[0]);
      ipAddressForNextKillSignal = new String("");
    } else {
      ipAddressForNextMachine = new String(machineList[currentMachineIndex + 1]);
      ipAddressForNextKillSignal = new String(ipAddressForNextMachine);
    }
    
    // open up the output file object
    File endItineraryFileObject = new File(NewItineraryName + currentMachineIndex + ".itn");
    FileOutputStream endItineraryStream = new FileOutputStream(endItineraryFileObject);
    PrintWriter endItineraryWriter = new PrintWriter(endItineraryStream);
    
    // open up the template file object
    File endFileObject = new File(EndTemplateFileName);
    FileReader endStream = new FileReader(endFileObject);
    BufferedReader endReader = new BufferedReader(endStream);
    
    // we're just gonna write the first couple of lines ourselves...
    
    endItineraryWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    endItineraryWriter.println("<toolkit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-Instance\" xsi:noNamespaceSchemaLocation=\"itinerary.xsd\">");
    endItineraryWriter.println("  <itinerary label=\"" + justItineraryName + currentMachineIndex + ".itn\">");
    endItineraryWriter.println("    <annotation><![CDATA[<HTML> </HTML>]]></annotation>");
    
    // place the reader on the first module definition...
    while (true) {
      // look for the module definitions
      endReader.mark(largeInteger); // mark this place so we can reset to it...
      templateLineContents = endReader.readLine();
      moduleStartIndex = templateLineContents.indexOf("<module");
      if (moduleStartIndex != -1) {
        // we are done with the headers because we found a module
        break;
        // otherwise, we're still in the headers
      }
    }
    // the reader is reset so that the next line read will be the module invocation
    endReader.reset();
    
    // start processing the itinerary one module at a time...
    ModuleProcessor endModuleHandler = new ModuleProcessor(endReader, moduleBufferSize);

    writeAnItinerary(endModuleHandler, currentMachineIndex, ipAddressForNextKillSignal,
        ipAddressForNextMachine, BaseDataPathName, InitializationFileName, endItineraryWriter);

    // clean up the streams...
    endItineraryWriter.close();
    endItineraryStream.close();
    
    endReader.close();
    endStream.close();    

    
    ////////////////////////////////////
    
    
  }
  
}