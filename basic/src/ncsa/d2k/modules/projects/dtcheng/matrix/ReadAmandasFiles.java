package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class ReadAmandasFiles extends InputModule {
  
  public String getModuleName() {
    return "ReadAmandasFiles";
  }
  
  public String getModuleInfo() {
    return "This module attempts to read Amanda White's text data files.... " +
    "And, it then tries to convert string variables into a series of indicator variables... " +
    "It should kick out an MFM and a string array of translation tables eventually...";
  }
  
  private boolean VerboseStatus = true;
  public void setVerboseStatus(boolean newValue) {
    this.VerboseStatus = newValue;
  }
  public boolean getVerboseStatus() {
    return this.VerboseStatus;
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "FileName";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "FileName";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "java.lang.String", };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Matrix";
    case 1:
      return "TranslationTable";
    case 2:
      return "NumCategoriesTable";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
// case 0:
// return "Matrix";
    case 0:
      return "Matrix";
    case 1:
      return "TranslationTable";
    case 2:
      return "NumCategoriesTable";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.String",
        "[I"};
//    String[] types = { , };
    return types;
  }
  
  public void doit() throws Exception {
    
    String FileName = (String) this.pullInput(0);
    
    // create the "File"
    File dataFileObject = new File(FileName);
    FileReader dataStream = new FileReader(dataFileObject);
    BufferedReader dataReader = new BufferedReader(dataStream);
    
    // actually try to read in the goodies and crank it out and write it
    // down...
    
    int magicLargeInt = 500;
    String magicDelimiterString = " ";
    int magicMaximumNumberOfCategories = 30;
    
    int previousDelimiterIndex = 0;
    int currentDelimiterIndex = 0;
    int currentColumn = 0;
    int currentStringColumn = 0;
    String tempString = null;
    String lineContents = null;
    
    int nStringColumns = 0; // This actually needs to be zero...
    int nLines = 0; // This actually needs to be zero...
    
    
    // first read one line of data to find any string variables
    lineContents = dataReader.readLine(); // the headers...
    dataReader.mark(magicLargeInt); // mark the first line of actual data so we
                                    // can reset...
    lineContents = dataReader.readLine(); // the first actual data...
    
    boolean stillInMiddle = true;
    currentColumn = 0;
    while (stillInMiddle) {
      currentDelimiterIndex = lineContents.indexOf(magicDelimiterString,previousDelimiterIndex);
      if (currentDelimiterIndex < 0) {
        // we have hit the end of the line
        tempString = lineContents.substring(previousDelimiterIndex);
        try {
          Double.parseDouble(tempString);
        } catch (NumberFormatException nfe) {
          nStringColumns++;
        }
        
        previousDelimiterIndex = currentDelimiterIndex + 1;
        currentColumn++;
        stillInMiddle = false;
      } else {
        // we have a column to process...
        tempString = lineContents.substring(previousDelimiterIndex,currentDelimiterIndex);
        try {
          Double.parseDouble(tempString);
        } catch (NumberFormatException nfe) {
          nStringColumns++;
        }
        
        previousDelimiterIndex = currentDelimiterIndex + 1;
        currentColumn++;
      }
    }
    dataReader.reset();
    dataReader.mark(magicLargeInt); // mark the first line of actual data so we
                                    // can reset...
    
    // create an array of counters for the string variables so that we can count
    // up how many unique categories
    // are represented...
    int[] stringCategoryCounters = new int[nStringColumns];
    String[][] stringCategoryValues = new String[nStringColumns][magicMaximumNumberOfCategories];
    boolean weHaveSeenThisCategoryBefore = false;
    // second, read through the entire file to find how many different
    // categories there are
    
    lineContents = dataReader.readLine(); // seed the process...
    while (true) {
      nLines++;
      
      stillInMiddle = true;
      currentStringColumn = 0;
      while (stillInMiddle) {
        currentDelimiterIndex = lineContents.indexOf(magicDelimiterString,previousDelimiterIndex);
        if (currentDelimiterIndex < 0) {
          // we have hit the end of the line
          tempString = lineContents.substring(previousDelimiterIndex);
          try {
            Double.parseDouble(tempString);
          } catch (NumberFormatException nfe) {
            weHaveSeenThisCategoryBefore = false;
            for (int categoryCounter = 0; categoryCounter < stringCategoryCounters[currentStringColumn]; categoryCounter++) {
              if (tempString.equals(stringCategoryValues[currentStringColumn][categoryCounter])) {
                // we already have this one listed: do nothing, just break...
                weHaveSeenThisCategoryBefore = true;
                break;
              }
            }
            if (!weHaveSeenThisCategoryBefore) {
              // record it and bump up the counter for this column
              stringCategoryValues[currentStringColumn][(stringCategoryCounters[currentStringColumn])] = new String(tempString);
              stringCategoryCounters[currentStringColumn]++;
            }
            currentStringColumn++;
          }
          
          previousDelimiterIndex = currentDelimiterIndex + 1;
          stillInMiddle = false;
        } else {
          // we have a column to process...
          tempString = lineContents.substring(previousDelimiterIndex,currentDelimiterIndex);
          try {
            Double.parseDouble(tempString);
          } catch (NumberFormatException nfe) {
            
            weHaveSeenThisCategoryBefore = false;
            for (int categoryCounter = 0; categoryCounter < stringCategoryCounters[currentStringColumn]; categoryCounter++) {
              if (tempString.equals(stringCategoryValues[currentStringColumn][categoryCounter])) {
                // we already have this one listed: do nothing, just break...
                weHaveSeenThisCategoryBefore = true;
                break;
              }
            }
            if (!weHaveSeenThisCategoryBefore) {
              // record it and bump up the counter for this column
              stringCategoryValues[currentStringColumn][(stringCategoryCounters[currentStringColumn])] = new String(tempString);
              stringCategoryCounters[currentStringColumn]++;
            }
            currentStringColumn++;
          }
          previousDelimiterIndex = currentDelimiterIndex + 1;
        }
      }
      if (dataReader.ready()) {
        lineContents = dataReader.readLine();
      } else {
        break;
      }
    }
    
    
    // third, read through the entire file and replace the string variables with
    // indicators
    
    dataReader.close();
    dataStream.close();
    

    long nRows = nLines;
    // the columns are tricky. we need all the original columns except for
    // the categoricals. but if the categoricals have only one value, then we
    // drop them. and, further we need (# of values - one) for each kept
    // categorical.
    
    long nCols = currentColumn - stringCategoryCounters.length; // start out by dropping the string categories themselves
    // then we'll add back in the right number of indicator variables..

    // test out by dumping our amazing arrays...    
    int maxValue = 0;
    for (int catIndex = 0; catIndex < stringCategoryCounters.length; catIndex++) {
      if (stringCategoryCounters[catIndex] > maxValue) {
        maxValue = stringCategoryCounters[catIndex];
      }
    }

    String[][] translationTable = new String[stringCategoryCounters.length][maxValue];
    
    for (int catIndex = 0; catIndex < stringCategoryCounters.length; catIndex++) {
      for (int valueIndex = 0; valueIndex < stringCategoryCounters[catIndex]; valueIndex++) {
        if (VerboseStatus) {
          System.out.println("string column " + catIndex + "'s value #" + valueIndex + " is [" +
              stringCategoryValues[catIndex][valueIndex] + "]");
        }
        translationTable[catIndex][valueIndex] = stringCategoryValues[catIndex][valueIndex];
      }
      if (stringCategoryCounters[catIndex] > 1) {
        // this one is "worthy" of inclusion, i.e., is not all the same value
        nCols += (stringCategoryCounters[catIndex] - 1);
      }
    }

  
    MultiFormatMatrix bigMatrix = new MultiFormatMatrix(3, new long[] {nRows, nCols});

    double valueToStore = Double.NaN;
    int storageColumn = Integer.MIN_VALUE;

    // let's just try starting over...
    dataStream = new FileReader(dataFileObject);
    dataReader = new BufferedReader(dataStream);
    
    lineContents = dataReader.readLine(); // get the headers

    // now we get to read the goodies and try to make sense of them...
    for (int lineIndex = 0; lineIndex < nLines; lineIndex++) {
      lineContents = dataReader.readLine();

//      System.out.println("working on [" + lineContents + "]");
      
      stillInMiddle = true;
      currentStringColumn = 0;
      storageColumn = 0;
      previousDelimiterIndex = 0;
      while (stillInMiddle) {
//        System.out.println("lineIndex = " + lineIndex + "; currentStringColumn = " + currentStringColumn +
//            "; storageColumn = " + storageColumn + "; previousDelimiterIndex = " + previousDelimiterIndex +
//            "; currentDelimiterIndex = " + currentDelimiterIndex);
        currentDelimiterIndex = lineContents.indexOf(magicDelimiterString,previousDelimiterIndex);
        if (currentDelimiterIndex < 0) {
          // we have hit the end of the line
          tempString = lineContents.substring(previousDelimiterIndex);
          try {
            valueToStore = Double.parseDouble(tempString);
            bigMatrix.setValue(lineIndex,storageColumn,valueToStore);
            storageColumn++;
          } catch (NumberFormatException nfe) {
            for (int categoryCounter = 0; categoryCounter < stringCategoryCounters[currentStringColumn] - 1; categoryCounter++) {
              // notice the "minus one" in the conditional. this is because for n categories, we use (n-1) indicator variables
              if (tempString.equals(translationTable[currentStringColumn][categoryCounter])) {
                // this is the category, put in a one...
                bigMatrix.setValue(lineIndex,storageColumn,1.0);
                storageColumn++;
              } else {
                // this is NOT the category, put down a zero
                bigMatrix.setValue(lineIndex,storageColumn,0.0);
                storageColumn++;
              }
            }
            currentStringColumn++;
          }
          previousDelimiterIndex = currentDelimiterIndex + 1;
          stillInMiddle = false;
        } else {
          // we have a column to process...
          tempString = lineContents.substring(previousDelimiterIndex,currentDelimiterIndex);
          try {
            valueToStore = Double.parseDouble(tempString);
            bigMatrix.setValue(lineIndex,storageColumn,valueToStore);
            storageColumn++;
          } catch (NumberFormatException nfe) {
            for (int categoryCounter = 0; categoryCounter < stringCategoryCounters[currentStringColumn] - 1; categoryCounter++) {
              // notice the "minus one" in the conditional. this is because for n categories, we use (n-1) indicator variables
              if (tempString.equals(translationTable[currentStringColumn][categoryCounter])) {
                // this is the category, put in a one...
                bigMatrix.setValue(lineIndex,storageColumn,1.0);
                storageColumn++;
              } else {
                // this is NOT the category, put down a zero
                bigMatrix.setValue(lineIndex,storageColumn,0.0);
                storageColumn++;
              }
            }
            currentStringColumn++;
          }
          previousDelimiterIndex = currentDelimiterIndex + 1;
        }
      }
    }
    
    System.out.println("[" + this.getAlias() + "] final output is " + nLines + " rows by " + storageColumn + " columns.");
    
    
    dataReader.close();
    dataStream.close();
    
    this.pushOutput(bigMatrix, 0);
    this.pushOutput(translationTable,1);
    this.pushOutput(stringCategoryCounters,2);
  }
}






