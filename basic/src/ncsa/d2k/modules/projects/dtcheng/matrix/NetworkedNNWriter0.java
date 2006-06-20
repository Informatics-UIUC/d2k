package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class NetworkedNNWriter0 extends OutputModule {
	
	public String getModuleName() {
		return "NetworkedNNWriter0";
	}
	
	public String getModuleInfo() {
		return "This module is a pure brute force, fully MAGIC'ed attempt "
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
			return "MachineListFileName";
		case 1:
			return "nThreadsLocalhost";
		case 2:
			return "BaseDataPathName";
		case 3:
			return "InitializationFileName";
		case 4:
			return "NewItineraryName";
		case 5:
			return "QueenTemplateFileName";
		case 6:
			return "WorkerTemplateFileName";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String getInputInfo(int i) {
		switch (i) {
		case 0:
			return "MachineListFileName: the list of machines the itinerary is built for";
		case 1:
			return "nThreadsLocalhost: the number of threads to run on the localhost (not found in machines file)";
		case 2:
			return "BaseDataPathName: the trunk of the name where the data will be found";
		case 3:
			return "InitializationFileName: where to find the initialization file";
		case 4:
			return "NewItineraryName: where to put the new itinerary";
		case 5:
			return "QueenTemplateFileName: where to find the Queen template";
		case 6:
			return "WorkerTemplateFileName: where to find the Worker template";
		default:
			return "Error!  No such input.  ";
		}
	}
	
	public String[] getInputTypes() {
		String[] types = { "java.lang.String", "java.lang.Integer", "java.lang.String",
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
			String originalLineContents, int nMachines) throws Exception {
		
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
	private String doFanOut(String destinationBaseAlias, int inputIndex, PrintWriter outputWriter, BufferedReader queenReader,
			String originalLineContents, int nMachines) throws Exception {
		
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
			outputWriter.println("      <output index=\"" +
					machineIndex +"\" classname=\"[[I\">");
			outputWriter.println("        <destination alias=\"#" + machineIndex +
					" " + destinationBaseAlias + "\" index=\"" + inputIndex + "\"/>");
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
		
		String MachineListFileName = (String)this.pullInput(0);
		int nThreadsLocalhost = ((Integer)this.pullInput(1)).intValue();
		String BaseDataPathName = (String)this.pullInput(2);
		String InitializationFileName = (String)this.pullInput(3);
		String NewItineraryName = (String)this.pullInput(4);
		String QueenTemplateFileName = (String)this.pullInput(5);
		String WorkerTemplateFileName = (String)this.pullInput(6);
		
		int largeInteger = 10000000; // Beware the MAGIC NUMBER!!! the
		// readAheadLimit
		
		/*
		 * read the machines file and put the contents into a string and int
		 * array
		 */
		File MachineListFileObject = new File(MachineListFileName);
		FileReader MachineStream = new FileReader(MachineListFileObject);
		BufferedReader MachineReader = new BufferedReader(MachineStream);
		MachineReader.mark(largeInteger);
		
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
		
		nMachines++; // the localhost is not on the list...
		
		int commaIndex = 0;
		String[] MachineName = new String[nMachines];
		int[] nThreads = new int[nMachines];
		
		// Beware: skipping index 0 for the localhost...
		MachineName[0] = "localhost";
		nThreads[0] = nThreadsLocalhost;
		for (int machineIndex = 1; machineIndex < nMachines; machineIndex++) {
			MachineLineContents = MachineReader.readLine();
			//			System.out.println("machine line = [" + MachineLineContents + "]");
			commaIndex = MachineLineContents.indexOf(",");
			MachineName[machineIndex] = MachineLineContents.substring(0,commaIndex);
			nThreads[machineIndex] = Integer.parseInt(
					MachineLineContents.substring(commaIndex + 1,
							MachineLineContents.indexOf(",",commaIndex + 1))
			);
		}
		
		MachineReader.close();
		MachineStream.close();
		
		// get just the "name" part of the new itinerary name
		String JustItineraryName = new String();
		int slashIndex = NewItineraryName.lastIndexOf("/");
		int backSlashIndex = NewItineraryName.lastIndexOf("\\");
		int nameStartIndex = Math.max(slashIndex,backSlashIndex);
		JustItineraryName = NewItineraryName.substring(nameStartIndex + 1); 
		
		
		/*		for (int machineIndex = 0; machineIndex < nMachines; machineIndex++) {
		 System.out.println("name = " + MachineName[machineIndex] + "; threads = " +
		 nThreads[machineIndex]); }
		 */		 
		/*
		 * now i need to read in the Queen template and write it back down while
		 * looking for the first things to replace...
		 */		
		
		/* Create the new itinerary stuff */
		File NewItineraryFileObject = new File(NewItineraryName);
		FileOutputStream NewItineraryStream = new FileOutputStream(NewItineraryFileObject);
		PrintWriter NewItineraryWriter = new PrintWriter(NewItineraryStream);
		
		/* Prepare to read the templates */
		File QueenFileObject = new File(QueenTemplateFileName);
		FileReader QueenStream = new FileReader(QueenFileObject);
		BufferedReader QueenReader = new BufferedReader(QueenStream);
		
		File WorkerFileObject = new File(WorkerTemplateFileName);
		FileReader WorkerStream = new FileReader(WorkerFileObject);
		BufferedReader WorkerReader = new BufferedReader(WorkerStream);
		// mark the beginning of the stream2;
		QueenReader.mark(largeInteger);
		WorkerReader.mark(largeInteger);
		
		String QueenLineContents = new String();
		String WorkerLineContents = new String();
		
		String moduleAlias = new String();
		
		//		int nFunnyAliases = 0; // this actually needs to be zero
		
		int moduleStartIndex = -3;
//		int proximityEndIndex = -15;
//		int itineraryStartIndex = -234;
		int itineraryEndIndex = -16;
		int proximityStartIndex = -6;
//		int machineDoneStartIndex = -7;
		int fieldFirstQuote = -4;
		int fieldLastQuote = -5;
		
		int numberSignIndex = -100;
		int ylocStart = -101;
		int ylocEnd = -1010;
		int ylocValue = -104;
		int moduleEndIndex = -109;
//		int propertyEndIndex = -1090;
//		int outputStartIndex = -1234;
//		int outputEndIndex = -12345;
//		int outputValue = -123456;
		int destinationIndex = -102;
//		int destinationNumberSignIndex = -103;
		
		int ylocShift = 1450; // Beware the MAGIC NUMBER!!! vertical offset
		
		/*
		 * i will be silly and read through the queen template file to extract
		 * out a list of all the module aliases and store them in a big string
		 * array
		 */
		
		int nQueenModules = 0; // this actually needs to be zero
		while (true) {
			QueenLineContents = QueenReader.readLine();
			if (QueenLineContents == null) { // end of stream reached
				break;
			}
			// look for the module definitions
			moduleStartIndex = QueenLineContents.indexOf("<module");
			if (moduleStartIndex >= 0) {
				nQueenModules++;
			}
		}
		
		String QueenModuleNames[] = new String[nQueenModules];
		// try to go back to the beginning of the worker file
		QueenReader.reset();
		int storageNumber = 0;
		while (true) {
			QueenLineContents = QueenReader.readLine();
			if (QueenLineContents == null) { // end of stream reached
				break;
			}
			// look for the module definitions
			moduleStartIndex = QueenLineContents.indexOf("<module");
			if (moduleStartIndex >= 0) {
				fieldFirstQuote = QueenLineContents.indexOf("alias=\"") + 6;
				fieldLastQuote = QueenLineContents.indexOf("\"",fieldFirstQuote + 1);
				// when pulling out the name, we will drop the number signs
				moduleAlias = QueenLineContents.substring(fieldFirstQuote + 1,fieldLastQuote);
				QueenModuleNames[storageNumber] = moduleAlias;
				storageNumber++;
			}
		}
		// try to go back to the beginning of the worker file in case i
		// forget later
		QueenReader.reset();
		
		/*
		 * i will be silly and read through the worker template file to extract
		 * out a list of all the module aliases and store them in a big string
		 * array
		 */
		
		int nWorkerModules = 0; // this actually needs to be zero
		while (true) {
			WorkerLineContents = WorkerReader.readLine();
			if (WorkerLineContents == null) { // end of stream reached
				break;
			}
			// look for the module definitions
			moduleStartIndex = WorkerLineContents.indexOf("<module");
			if (moduleStartIndex >= 0) {
				nWorkerModules++;
			}
		}
		
		String WorkerModuleBaseNames[] = new String[nWorkerModules];
		// try to go back to the beginning of the worker file
		WorkerReader.reset();
		
		storageNumber = 0;
		while (true) {
			WorkerLineContents = WorkerReader.readLine();
			if (WorkerLineContents == null) { // end of stream reached
				break;
			}
			// look for the module definitions
			moduleStartIndex = WorkerLineContents.indexOf("<module");
			if (moduleStartIndex >= 0) {
				fieldFirstQuote = WorkerLineContents.indexOf("alias=\"") + 6;
				fieldLastQuote = WorkerLineContents.indexOf("\"",fieldFirstQuote + 1);
				// when pulling out the name, we will drop the number signs
				moduleAlias = WorkerLineContents.substring(fieldFirstQuote + 3,fieldLastQuote);
				WorkerModuleBaseNames[storageNumber] = moduleAlias;
				storageNumber++;
			}
		}
		// try to go back to the beginning of the worker file in case i forget
		// later
		//		WorkerStream.reset();
		WorkerReader.reset();
		
		// check and see if i did it right...
		
		/*
		 * for (int moduleIndex = 0; moduleIndex < nWorkerModules;
		 * moduleIndex++) { System.out.println("WorkerModuleNames[" +
		 * moduleIndex + "] = [" + WorkerModuleBaseNames[moduleIndex] + "]"); }
		 */ 		
		
		
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
		
		NewItineraryWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		NewItineraryWriter.println("<toolkit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-Instance\" xsi:noNamespaceSchemaLocation=\"itinerary.xsd\">");
		NewItineraryWriter.println("  <itinerary label=\"" + JustItineraryName + "\">");
		NewItineraryWriter.println("    <annotation><![CDATA[<HTML> </HTML>]]></annotation>");
		
		//		System.out.println("Here?");
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
		System.out.println("nMachines = " + nMachines);
		while (true) {
			// check if it is one of the modules that goes to the Workers
			fieldFirstQuote = QueenLineContents.indexOf("alias=\"") + 6;
			fieldLastQuote = QueenLineContents.indexOf("\"",fieldFirstQuote + 1);
			moduleAlias = QueenLineContents.substring(fieldFirstQuote + 1,fieldLastQuote);
			
			//			System.out.println("moduleAlias = [" + moduleAlias + "]");
			// search through all the modules that need to be connected to
			// the workers
			if (moduleAlias.equals("Long: nChunks")) {
				QueenLineContents = setProperty("constantValue", new Integer(nMachines), QueenLineContents,
						NewItineraryWriter, QueenReader);
			} else if (moduleAlias.equals("String: Initialization FileName")) {
				QueenLineContents = setProperty("stringData", InitializationFileName, QueenLineContents,
						NewItineraryWriter, QueenReader);
			} else if (moduleAlias.equals("FanOut LayerTable to Workers")) {
				QueenLineContents = doFanOut("T: LayerTable", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut N_to_LT to Workers")){
				QueenLineContents = doFanOut("T: N_to_LT", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut L_sf_NT to Workers")){
				QueenLineContents = doFanOut("T: L_sf_NT", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut Wn_ft_NT to Workers")){
				QueenLineContents = doFanOut("T: Wn_ft_NT", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut nElementsThreshold to Workers")){
				QueenLineContents = doFanOut("nElementsThreshold", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut FractionInTraining to Workers")){
				QueenLineContents = doFanOut("RandomRowSplitter", 1, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut False to Workers")){
				QueenLineContents = doFanOut("T: false", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut True to Workers")){
				QueenLineContents = doFanOut("T: true", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut NewWeights to Workers")){
				QueenLineContents = doFanOut("New Weights", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut NewBiases to Workers")){
				QueenLineContents = doFanOut("Biases In NN", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanOut CheckFlag to Workers")){
				QueenLineContents = doFanOut("CheckFlag", 0, NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
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
			else if (moduleAlias.equals("FanIn ValidationFracCorrect Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanIn ValidationError Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanIn BiasGradient Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanIn TrainingError Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanIn WeightGradient Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanIn TrainingFracCorrect Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else if (moduleAlias.equals("FanIn ValidationError Collector")){
				QueenLineContents = doFanIn(NewItineraryWriter, QueenReader, QueenLineContents, nMachines);
			} else {
				/*
				 * it is a module, just not one we want to mess with... we need
				 * to go until we find the next module or a proximity or /itinerary
				 */
				while (true) {
					if (QueenLineContents.endsWith(">")) {
//						NewItineraryWriter.println(QueenLineContents + " <!--not messed with... " + moduleAlias + "-->");
						NewItineraryWriter.println(QueenLineContents);
					} else {
						NewItineraryWriter.println(QueenLineContents);
						
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
		
		/*
		 * so... now we need to put in all the worker templates, assigning the
		 * proper yloc's and connecting them to the correct collectors...
		 */
		
		for (int machineIndex = 0; machineIndex < nMachines; machineIndex++) {
			// read and write the worker template to the new itinerary
			// skip down to the first module
//			NewItineraryWriter.println("<!-- Begin Worker #" + machineIndex + "-->");
			while (true) {
				WorkerLineContents = WorkerReader.readLine();
				moduleStartIndex = WorkerLineContents.indexOf("<module");
				if (moduleStartIndex != -1) {
					// first module, so break, otherwise, just keep reading...
					break;
				}
			}
			
			
			while (true) {
				
				/*
				 * * now, i need to read in the module name, put in a machine number, and
				 * connect it up to the right places
				 */				 
				fieldFirstQuote = WorkerLineContents.indexOf("alias=\"") + 6;
				fieldLastQuote = WorkerLineContents.indexOf("\"",fieldFirstQuote + 1);
				
				moduleAlias = WorkerLineContents.substring(fieldFirstQuote + 1,fieldLastQuote);
				
				// check if it is one of the NN activators which need to output
				// properly
				if (moduleAlias.equals("# String: Full Dataset")) {
					WorkerLineContents = setWorkerProperty("stringData",
							new String(BaseDataPathName + machineIndex + "_"),
							machineIndex, ylocShift, WorkerLineContents, NewItineraryWriter,
							WorkerReader);
				} else if (moduleAlias.equals("# NNCondensedLessRoundingInts Training")) {
					WorkerLineContents = doNNWorkerToCollectors("Training", NewItineraryWriter,
							WorkerReader, WorkerLineContents, ylocShift, machineIndex);
				} else if (moduleAlias.equals("# NNCondensedLessRoundingInts Validation")) {
					WorkerLineContents = doNNWorkerToCollectors("Validation", NewItineraryWriter,
							WorkerReader, WorkerLineContents, ylocShift, machineIndex);
				}
				// if MFMs start working, we could put this stuff in with the
				// doNNWorkerToCollectors subroutine
				else if (moduleAlias.equals("# MFMToDoubleArray: BiasGradient")) {
					// handle the module invocation
					numberSignIndex = WorkerLineContents.indexOf("#");
					ylocStart = WorkerLineContents.indexOf("yloc") + 6;
					ylocEnd = WorkerLineContents.indexOf("\"",ylocStart);
					ylocValue = Integer.parseInt(WorkerLineContents.substring(ylocStart,ylocEnd)) +
					ylocShift * machineIndex;
					NewItineraryWriter.println(
							WorkerLineContents.substring(0,numberSignIndex + 1) + machineIndex +
							WorkerLineContents.substring(numberSignIndex + 1,ylocStart) + ylocValue +
							WorkerLineContents.substring(ylocEnd)
					);
					// do the input line
					WorkerLineContents = WorkerReader.readLine();
					NewItineraryWriter.println(WorkerLineContents);
					// do the output line manually
					NewItineraryWriter.println("      <output index=\"0\" classname=\"[[D\">");
					NewItineraryWriter.println("        <destination alias=\"FanIn BiasGradient Collector\" index=\""
							+ (machineIndex + 1) + "\"/>");
					NewItineraryWriter.println("      </output>");
					// read down to the end of the module
					while (true) {
						WorkerLineContents = WorkerReader.readLine(); // not to
						// be
						// used...
						moduleEndIndex = WorkerLineContents.indexOf("</module");
						if (moduleEndIndex != -1) {
							// we are done with the module, so write it and
							// break
							NewItineraryWriter.println(WorkerLineContents);
							break;
						}
					}
				} else if (moduleAlias.equals("# MFMToDoubleArray: WeightGradient")) {
					// handle the module invocation
					numberSignIndex = WorkerLineContents.indexOf("#");
					ylocStart = WorkerLineContents.indexOf("yloc") + 6;
					ylocEnd = WorkerLineContents.indexOf("\"",ylocStart);
					ylocValue = Integer.parseInt(WorkerLineContents.substring(ylocStart,ylocEnd)) +
					ylocShift * machineIndex;
					NewItineraryWriter.println(
							WorkerLineContents.substring(0,numberSignIndex + 1) + machineIndex +
							WorkerLineContents.substring(numberSignIndex + 1,ylocStart) + ylocValue +
							WorkerLineContents.substring(ylocEnd)
					);
					// do the input line
					WorkerLineContents = WorkerReader.readLine();
					NewItineraryWriter.println(WorkerLineContents);
					// do the output line manually
					NewItineraryWriter.println("      <output index=\"0\" classname=\"[[D\">");
					NewItineraryWriter.println("        <destination alias=\"FanIn WeightGradient Collector\" index=\""
							+ (machineIndex + 1) + "\"/>");
					NewItineraryWriter.println("      </output>");
					// read down to the end of the module
					while (true) {
						WorkerLineContents = WorkerReader.readLine(); // not to
						// be
						// used...
						moduleEndIndex = WorkerLineContents.indexOf("</module");
						if (moduleEndIndex != -1) {
							// we are done with the module, so write it and
							// break
							NewItineraryWriter.println(WorkerLineContents);
							break;
						}
					}
				}
				// otherwise, it is a standard module...
				else {
					// handle the module invocation
					numberSignIndex = WorkerLineContents.indexOf("#");
					ylocStart = WorkerLineContents.indexOf("yloc") + 6;
					ylocEnd = WorkerLineContents.indexOf("\"",ylocStart);
					ylocValue = Integer.parseInt(WorkerLineContents.substring(ylocStart,ylocEnd)) +
					ylocShift * machineIndex;
					NewItineraryWriter.println(
							WorkerLineContents.substring(0,numberSignIndex + 1) + machineIndex +
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
							NewItineraryWriter.println(WorkerLineContents);
							break;
						} else if (destinationIndex == -1) {
							// it is a non-destination type line, so just write
							// it, but continue
							NewItineraryWriter.println(WorkerLineContents);
						} else {
							// it is a destination line, so change it and
							// continue
							numberSignIndex = WorkerLineContents.indexOf("#");
							NewItineraryWriter.println(
									WorkerLineContents.substring(0,numberSignIndex + 1) + machineIndex +
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
			
			WorkerReader.reset(); // reset for the next time through
		}
		
		
		/*
		 * do the proximity machine assignments
		 */
		String previousMachineName = new String();
		boolean thisIsANewMachine = true;
		if (nMachines > 1) {
//			NewItineraryWriter.println("<!-- The machine assignments will go here -->");
			NewItineraryWriter.println("    <proximity port=\"7021\">");
			// the localhost
			NewItineraryWriter.println("      <machine name=\"localhost\" processors=\"" + nThreads[0] +
			"\" dataPort=\"7022\" controlPort=\"7021\">");
			for (int moduleIndex = 0; moduleIndex < nQueenModules; moduleIndex++) {
				NewItineraryWriter.println("        <assigned name=\"" + QueenModuleNames[moduleIndex] + "\"/>");
			}
			for (int moduleIndex = 0; moduleIndex < nWorkerModules; moduleIndex++) {
				NewItineraryWriter.println("        <assigned name=\"#" + 0 + " " +
						WorkerModuleBaseNames[moduleIndex] + "\"/>");
			}
//			NewItineraryWriter.println("      </machine>"); now taken care of below
			// the rest of them...
			for (int machineIndex = 1; machineIndex < nMachines; machineIndex++) {
				if (previousMachineName.equals(MachineName[machineIndex])) {
					thisIsANewMachine = false;
				} else {
					thisIsANewMachine = true;
				}
				if (thisIsANewMachine) {
					NewItineraryWriter.println("      </machine>");
					NewItineraryWriter.println("      <machine name=\"" + 
							MachineName[machineIndex] + "\" processors=\"" + nThreads[machineIndex] +
					"\" dataPort=\"7022\" controlPort=\"7021\">");
				}
				for (int moduleIndex = 0; moduleIndex < nWorkerModules; moduleIndex++) {
					NewItineraryWriter.println("        <assigned name=\"#" + machineIndex + " " +
							WorkerModuleBaseNames[moduleIndex] + "\"/>");
				}
				previousMachineName = new String(MachineName[machineIndex]);
			}
			NewItineraryWriter.println("      </machine>");
			NewItineraryWriter.println("    </proximity>");
		}

/*		if (nMachines > 1) {
			NewItineraryWriter.println("<!-- The machine assignments will go here -->");
			NewItineraryWriter.println("    <proximity port=\"7021\">");
			// the localhost
			NewItineraryWriter.println("      <machine name=\"localhost\" processors=\"" + nThreads[0] +
			"\" dataPort=\"7022\" controlPort=\"7021\">");
			for (int moduleIndex = 0; moduleIndex < nQueenModules; moduleIndex++) {
				NewItineraryWriter.println("        <assigned name=\"" + QueenModuleNames[moduleIndex] + "\"/>");
			}
			for (int moduleIndex = 0; moduleIndex < nWorkerModules; moduleIndex++) {
				NewItineraryWriter.println("        <assigned name=\"#" + 0 + " " +
						WorkerModuleBaseNames[moduleIndex] + "\"/>");
			}
			NewItineraryWriter.println("      </machine>");
			// the rest of them...
			for (int machineIndex = 1; machineIndex < nMachines; machineIndex++) {
				NewItineraryWriter.println("      <machine name=\"" + 
						MachineName[machineIndex] + "\" processors=\"" + nThreads[machineIndex] +
				"\" dataPort=\"7022\" controlPort=\"7021\">");
				
				for (int moduleIndex = 0; moduleIndex < nWorkerModules; moduleIndex++) {
					NewItineraryWriter.println("        <assigned name=\"#" + machineIndex + " " +
							WorkerModuleBaseNames[moduleIndex] + "\"/>");
				}
				NewItineraryWriter.println("      </machine>");
			}
			NewItineraryWriter.println("    </proximity>");
		}
*/
		
		
		
		
		// write the last few bits...
		NewItineraryWriter.print("  </itinerary>\n</toolkit>");
		//		System.out.println("  </itinerary>\n</toolkit>");
		
		// clean up the mess and get ready to exit.
		NewItineraryWriter.flush();
		NewItineraryWriter.close();
		NewItineraryStream.close();
		QueenReader.close();
		QueenStream.close();
		WorkerReader.close();
		WorkerStream.close();
		
		//		System.out.println("don't forget to hardcode the initialization file path...");
	}
	
}