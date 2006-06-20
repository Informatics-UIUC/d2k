package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
import ncsa.d2k.core.modules.*;
import java.util.Random;


public class MagicIslandWriter extends OutputModule {
  
  public String getModuleName() {
    return "MagicIslandWriter";
  }
  
  public String getModuleInfo() {
    return "This module attempts to write a 2-d matrix to disk ascii style and a whole bunch of magic to make a 2-d island.";
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "BaseFileName";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "BaseFileName: base name of where to store matrix (no suffix)";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = {
        "java.lang.String", };
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
    String[] types = {  };
    return types;
  }
  
  public void doit() throws Exception {
    
    String BaseFileName = (String) this.pullInput(0);
    String DelimiterString = new String(" ");
    
    String FileName = BaseFileName + ".txt";
    String InfoFileName = BaseFileName + ".info.txt";
    
    long randomSeed = 2345;
    double xxx = Double.NaN;
    double yyy = Double.NaN;
    int onIsland = -5;
    
    double xLeft = 0.25;
    double xRight = 0.75;
    double yTop = 0.83;
    double yBot = 0.33;
    
    Random RandomNumberGenerator = new Random(randomSeed);
    
    long NumRows = 1000;
    long NumCols = 3;
    int FormatIndex = 1;
    
    File InfoFileToWrite = new File(InfoFileName);
    FileWriter outInfoStream = new FileWriter(InfoFileToWrite);
    PrintWriter outInfoWriterObject = new PrintWriter(outInfoStream);
    
    outInfoWriterObject.print(NumRows + "\t = Number of Rows\n");
    outInfoWriterObject.print(NumCols + "\t = Number of Columns\n");
    outInfoWriterObject.print((NumRows*NumCols) + "\t = Total Number of Elements\n");
    outInfoWriterObject.print(FormatIndex + "\t = The MultiFormatMatrix format the matrix was stored in\n");
    outInfoWriterObject.print(DelimiterString + "\t = The string used to delimit elements in the Rows");
    
    // finish cleaning up the mess...
    outInfoWriterObject.flush();
    outInfoWriterObject.close();
    
    // the actual goods
    File FileToWrite = new File(FileName);
    FileOutputStream outStream = new FileOutputStream(FileToWrite);
    PrintWriter outWriterObject = new PrintWriter(outStream);
    
    
    // Here we actually write to file
    for (long RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      xxx = RandomNumberGenerator.nextDouble();
      yyy = RandomNumberGenerator.nextDouble();
      if (xxx < xRight && xxx > xLeft && yyy < yTop && yyy > yBot) {
        onIsland = 1;
      } else {
        onIsland = 0;
      }
      outWriterObject.print(xxx + DelimiterString + yyy + DelimiterString + onIsland + "\n");
    }
    
    // clean up the mess...
    outWriterObject.flush();
    outWriterObject.close();
    
  }
}