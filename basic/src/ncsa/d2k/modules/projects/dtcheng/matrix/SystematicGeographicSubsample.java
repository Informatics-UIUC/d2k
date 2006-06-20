package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class SystematicGeographicSubsample extends OutputModule {
  
  public String getModuleName() {
    return "SystematicGeographicSubsample";
  }
  
  public String getModuleInfo() {
    return "This module attempts to read my proprietary 2-d MFM permanent storage things "
    + "and then do a systematic, geographic subsampling... All missing values need to still "
    + "be in here and can be dropped by this module if need be. The idea is to start at some point on the map "
    + "and then choose that point and then skip over a certain number of columns and keep that one. "
    + "When we reach the edge of the map, we skip down the same number of rows as we have been skipping "
    + "columns and resume picking columns. So, one thing we need to know is how many to skip. This will "
    + "be expressed by a skip factor. 1 (unity/one) means keep everything. 2 means \"every other,\" that is, "
    + "every 2nd one is kept. 3 means every third (in out out in out out in ...). You will end up with "
    + "1 over the skip factor squared as much data as you started with.<p>"
    + "The data are assumed to be in the format row=example/point on the map. The order is assumed "
    + "to be: the first point recorded is the upper left-hand corner and we go across the rows like " + "you're reading a page in English. <p>"
    + "The meta-data should be in a column MFM in standard Arc ASCII order: ncols, nrows, xllcorner, " + "yllcorner, cellsize, NODATA_value.";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "FullDataFilename";
    case 1:
      return "MetaData";
    case 2:
      return "SkipFactor";
    case 3:
      return "ChosenFilename";
    case 4:
      return "NotChosenFilename";
    case 5:
      return "xOffset";
    case 6:
      return "yOffset";
    case 7:
      return "BufferSize";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "FullDataFilename";
    case 1:
      return "MetaData";
    case 2:
      return "SkipFactor";
    case 3:
      return "ChosenFilename";
    case 4:
      return "NotChosenFilename";
    case 5:
      return "xOffset";
    case 6:
      return "yOffset";
    case 7:
      return "BufferSize";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.String", "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Integer", "java.lang.String",
        "java.lang.String", "java.lang.Integer", "java.lang.Integer", "java.lang.Integer" };
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
    String[] types = {};
    return types;
  }
  
  private double[] moveRowIntoBuffer(double[] singleRow, double[] theBuffer, int firstOpenIndex) {
    
    for (int withinIndex = 0; withinIndex < singleRow.length; withinIndex++) {
/*      System.out.println("   about to move to [" + (firstOpenIndex + withinIndex) + "] in the output, from [" +
          withinIndex + "] in the single row with a value of [" + singleRow[withinIndex] + "]");
*/
      theBuffer[firstOpenIndex + withinIndex] = singleRow[withinIndex];
    }
    return theBuffer;
    
  }
  
  public void doit() throws Exception {
    
    
    
    String FullDataFilename = (String)this.pullInput(0);
    MultiFormatMatrix MetaData = (MultiFormatMatrix)this.pullInput(1);
    int SkipFactor = ((Integer)this.pullInput(2)).intValue();
    String ChosenFilename = (String)this.pullInput(3);
    String NotChosenFilename = (String)this.pullInput(4);
    int xOffset = ((Integer)this.pullInput(5)).intValue();
    int yOffset = ((Integer)this.pullInput(6)).intValue();
    int OutputBufferSize = ((Integer)this.pullInput(7)).intValue();
    
    
    // pull out the info from the MetaData
    int nColsMap = (int)MetaData.getValue(0,0);
//    double noDataValue = MetaData.getValue(5,0);
    
    // read the input dataset info
    
    int nDimensions = -1;
    int inputBufferSize = -5;
    int nRowsInput = -2;
    int nCols = -3;
//    int LastBlockIndex = -9;
    
    File inputHeaderFile = new File(FullDataFilename + ".bin");
    if (!inputHeaderFile.exists()) {
      System.out.println("um, the header file [" + inputHeaderFile + "] has failed to exist...");
      throw new Exception("um, the header file [" + inputHeaderFile + "] has failed to exist...");
    }
    FileInputStream inputFileStream = new FileInputStream(FullDataFilename + ".bin");
    ObjectInputStream ininin = new ObjectInputStream(inputFileStream);
    nDimensions = ininin.readInt();
    inputBufferSize = ininin.readInt();
    int lastInputBlockIndex = ininin.readInt();
    nRowsInput = ininin.readInt();
    nCols = ininin.readInt();
    System.out.println("nDimensions = " + nDimensions + "; BufferSize = " + inputBufferSize + "; LastBlockIndex = " + lastInputBlockIndex + "; NumRows = "
        + nRowsInput + "; NumCols = " + nCols);
    ininin.close();
    inputFileStream.close();
    
    // set up output files
    int realBufferSize = (OutputBufferSize/nCols + 1) * nCols;
    
    double[] inputBuffer = null;
    double[] singleRow = new double[nCols];
    double[] chosenBuffer = new double[realBufferSize];
    double[] notChosenBuffer = new double[realBufferSize];
    double[] finalBuffer = null;
    
    // allocate the input data file stuff
    File inputDataFile = null;
    FileInputStream inputDataFileStream = null;
    ObjectInputStream inData = null;
    // allocate the output data file stuff
    File chosenFile = null; 
    File notChosenFile = null;
    FileOutputStream chosenStream = null; 
    FileOutputStream notChosenStream = null;
    ObjectOutputStream chosenOut = null;
    ObjectOutputStream notChosenOut = null;
    int nRowsInInputBuffer = 0; // don't matter
    int inputMapRow = 0; // don't matter
    int inputMapCol = 0; // don't matter
    
    // read in the input data and then start kicking out to the output files...
    
    int chosenBlockIndex = 0; // this actually needs to be zero
    int notChosenBlockIndex = 0; // this actually needs to be zero
    int nRowsChosen = 0; // this actually needs to be zero
    int nRowsNotChosen = 0; // this actually needs to be zero
    
    
    int matrixRealRow = 0; // actually needs to be zero
    boolean keepRow = true; // actually needs to be true
    int currentReadIndex = 0; // actually needs to be zero
    int currentChosenIndex = 0; // actually needs to be zero
    int currentNotChosenIndex = 0; // actually needs to be zero
    
    for (int inputBlockIndex = 0; inputBlockIndex < lastInputBlockIndex + 1; inputBlockIndex++) {
      
      if (matrixRealRow >= nRowsInput - 1) {
        // that is, if there are no more rows to read, we just break out...
        break;
      }
      inputDataFile = new File(FullDataFilename + inputBlockIndex + ".bin");
      inputDataFileStream = new FileInputStream(inputDataFile);
      inData = new ObjectInputStream(inputDataFileStream);

      System.out.println("reading file [" + inputDataFile + "]");
      inputBuffer = (double[])inData.readObject();
      
      inData.close();
      inputDataFileStream.close();
      
      nRowsInInputBuffer = inputBuffer.length / nCols;
      if (inputBufferSize % nCols > 0) {
        System.out.println("um, i'm gonna force the buffer to be an even multiple of the number of columns\n which is not the case...");
        throw new Exception();
      }
      
      currentReadIndex = 0; // set the read index after pulling in each buffer...
      
      // let's go: read a row, decide where it goes, and put it there...
      
      for (int inputRowIndex = 0; inputRowIndex < nRowsInInputBuffer; inputRowIndex++) {
//        System.out.println("----- i think i'm starting a row ------");
        
        inputMapCol = matrixRealRow % nColsMap;
        inputMapRow = matrixRealRow / nColsMap;
        
        // read the row
        keepRow = true; // innocent until proven guilty...
        for (int withinRowIndex = 0; withinRowIndex < nCols; withinRowIndex++) {
/*          System.out.println("reading from Block [" + inputBlockIndex + "]; of [" + nRowsInInputBuffer +
              "] rows we are on rowInBuffer [" + inputRowIndex +
              "], mapCol [" + inputMapCol + "], mapRow [" + inputMapRow + "], realRow [" + matrixRealRow +
              "] withinRowIndex [" + withinRowIndex + "], value = [" + singleRow[withinRowIndex] + "]");
*/     
          singleRow[withinRowIndex] = inputBuffer[currentReadIndex + withinRowIndex];
          
          // if there is a missing value in here, might as well dump it...
          if (Double.isNaN(singleRow[withinRowIndex])) {
            keepRow = false;
            break;
          }
        }
        matrixRealRow++; // bump up the counter since we read a row...
        currentReadIndex += nCols;
        
        // decide where it goes and put it there
        if (keepRow) {
          if ((inputMapRow - yOffset) % SkipFactor == 0 && (inputMapCol - xOffset) % SkipFactor == 0) {
            // it IS chosen
//            System.out.println(" the row is CHOSEN");
            chosenBuffer = moveRowIntoBuffer(singleRow,chosenBuffer,currentChosenIndex);
            currentChosenIndex += nCols;
            nRowsChosen++;
          } else {
            // it is NOT chosen
//            System.out.println(" the row is NOT chosen");
            notChosenBuffer = moveRowIntoBuffer(singleRow,notChosenBuffer,currentNotChosenIndex);
            currentNotChosenIndex += nCols;
            nRowsNotChosen++;
          }
        }
        
        // check on the output buffers...
        if (currentChosenIndex >= realBufferSize) {
          // the chosen buffer is full and we should write it to disk...
          chosenFile = new File(ChosenFilename + chosenBlockIndex + ".bin");
          chosenStream = new FileOutputStream(chosenFile);
          chosenOut = new ObjectOutputStream(chosenStream);

          System.out.println("  --> writing file [" + chosenFile + "]");
          chosenOut.writeObject(chosenBuffer);
          
          chosenOut.flush(); chosenOut.close(); chosenStream.close();
          
          chosenBlockIndex++;
          currentChosenIndex = 0; // reset current index
        }
        if (currentNotChosenIndex >= realBufferSize){
          // the chosen buffer is full and we should write it to disk...
          notChosenFile = new File(NotChosenFilename + notChosenBlockIndex + ".bin");
          notChosenStream = new FileOutputStream(notChosenFile);
          notChosenOut = new ObjectOutputStream(notChosenStream);
          
          System.out.println("  --> writing file [" + notChosenFile + "]");
          notChosenOut.writeObject(notChosenBuffer);
          
          notChosenOut.flush(); notChosenOut.close(); notChosenStream.close();
          
          notChosenBlockIndex++;
          currentNotChosenIndex = 0; // reset current index
        }
      } // end of for loop over matrix rows in buffer...
    } // end of for loop over input blocks
    
    // write the remaining block and the header info for each output buffer
    
    if (currentChosenIndex > 0) {
      finalBuffer = new double[currentChosenIndex];
      for (int withinIndex = 0; withinIndex < currentChosenIndex; withinIndex++) {
        finalBuffer[withinIndex] = chosenBuffer[withinIndex];
      }
      chosenFile = new File(ChosenFilename + chosenBlockIndex + ".bin");
      chosenStream = new FileOutputStream(chosenFile);
      chosenOut = new ObjectOutputStream(chosenStream);
      
      System.out.println("  --> writing file [" + chosenFile + "]");
      chosenOut.writeObject(finalBuffer);
      
      chosenOut.flush(); chosenOut.close(); chosenStream.close();
     }
    if (currentNotChosenIndex > 0) {
      finalBuffer = new double[currentNotChosenIndex];
      for (int withinIndex = 0; withinIndex < currentNotChosenIndex; withinIndex++) {
        finalBuffer[withinIndex] = notChosenBuffer[withinIndex];
      }
      notChosenFile = new File(NotChosenFilename + notChosenBlockIndex + ".bin");
      notChosenStream = new FileOutputStream(notChosenFile);
      notChosenOut = new ObjectOutputStream(notChosenStream);
      
      System.out.println("  --> writing file [" + notChosenFile + "]");
      notChosenOut.writeObject(finalBuffer);
      
      notChosenOut.flush(); notChosenOut.close(); notChosenStream.close();
    }

    // the chosen header
    chosenFile = new File(ChosenFilename + ".bin");
    chosenStream = new FileOutputStream(chosenFile);
    chosenOut = new ObjectOutputStream(chosenStream);
    
    chosenOut.writeInt(2);
    chosenOut.writeInt(realBufferSize);
    chosenOut.writeInt(chosenBlockIndex);
    chosenOut.writeInt(nRowsChosen);
    chosenOut.writeInt(nCols);
    
    chosenOut.flush(); chosenOut.close(); chosenStream.close();
    
    System.out.println("We have " + nRowsChosen + " rows (missing values were dropped) in the CHOSEN set.");

    // the non chosen header
      
    notChosenFile = new File(NotChosenFilename + ".bin");
    notChosenStream = new FileOutputStream(notChosenFile);
    notChosenOut = new ObjectOutputStream(notChosenStream);

    notChosenOut.writeInt(2);
    notChosenOut.writeInt(realBufferSize);
    notChosenOut.writeInt(notChosenBlockIndex);
    notChosenOut.writeInt(nRowsNotChosen);
    notChosenOut.writeInt(nCols);

    notChosenOut.flush(); notChosenOut.close(); notChosenStream.close();
    
    System.out.println("We have " + nRowsNotChosen + " rows (missing values were dropped) in the NOT chosen set.");
    
  }
}