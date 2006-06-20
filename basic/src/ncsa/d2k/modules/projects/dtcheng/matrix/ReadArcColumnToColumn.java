package ncsa.d2k.modules.projects.dtcheng.matrix;


import ncsa.d2k.modules.projects.dtcheng.io.FlatFile;
import ncsa.d2k.core.modules.*;
//import ncsa.d2k.modules.core.datatype.table.*;
//import java.io.*;
//import java.util.*;
//import java.text.*;


public class ReadArcColumnToColumn extends InputModule {

  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DataPath = "C:\\cygwin\\home\\Administrator\\data\\table.csv";
  public void setDataPath(String value) {
    this.DataPath = value;
  }


  public String getDataPath() {
    return this.DataPath;
  }


  private byte EOLByte1 = 10;
  public void setEOLByte1(byte value) {
    this.EOLByte1 = value;
  }


  public byte getEOLByte1() {
    return this.EOLByte1;
  }


  private int ReadBufferSize = 1000000;
  public void setReadBufferSize(int value) {
    this.ReadBufferSize = value;
  }


  public int getReadBufferSize() {
    return this.ReadBufferSize;
  }


  public String getModuleName() {
    return "ReadArcColumnToColumn";
  }


  public String getModuleInfo() {
    return "ReadArcColumnToColumn. This module reads in the rastor to ASCII export " +
        "from ARC stuff and dumps it into a MultiFormatMatrix. Specifically, this " +
		"reads in stuff from the GRASS export which can write it out as a single column. The Arc thing " +
        "generally kicks out a big table of numbers arranged as if they were " +
        "in the rastor. For statistical type analyses, it is desirable to " +
        "have everything strung out as a column. This module reads it " +
        "directly into a column. I think that this will read across the " +
        "rows. That is, the first element in the resulting matrix will be " +
        "the upper-left-hand element; the second element/row will be from " +
        "the original first row, second column." +
		"<p> It has been lately changed in order to be clever enough to read " +
		"data written by GRASS version 5.3 which puts only a single space between " +
		"the metadata descriptions and their values. Arc lines up the values at column #15." +
		"<p> <i>This assumes the -1 option has been used in GRASS to create the data.</i>";
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      case 1:
        return "MetaDataMatrix";
    }
    return "";
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MultiFormatMatrix";
      case 1:
        return "MetaDataMatrix. This will contain the ncols, " +
            "nrows, xllcorner, yllcorner, cellsize, NoDataValue " +
            "information as a single column 2-D MFM, in that order.";
    }
    return "";
  }


  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return out;
  }


  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "NumberOfElementsThreshold";
      default:
        return "No such input!";
    }
  }


  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "NumberOfElementsThreshold";
      default:
        return "No such input!";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
    };
    return types;
  }


  ////////////
  //  MAIN  //
  ////////////

  public void doit() throws Exception {

    long NumberOfElementsThreshold = ((Long)this.pullInput(0)).longValue();

    byte buffer[] = null;

    FlatFile rio = new FlatFile(DataPath, "r", ReadBufferSize, true);
    buffer = rio.Buffer;
    rio.EOLByte1 = EOLByte1;
    rio.DelimiterByte = 32; // space

    long ncols;
    long nrows;
    double xllcorner;
    double yllcorner;
    double cellsize;
    double NoDataValue;

    {
      String string;
      String checkstring;
      double value;
//      int offset = 14;
      int ArcOffset = 14;
      int GrassOffset = 0;

      System.out.println("ReadArcMatrixToColumn filename = " + DataPath);

      // now making this compatible with the output from GRASS which does not offset everything by 14...
      // Beware the MAGIC NUMBERS!!!
      
      rio.readLine();
      GrassOffset = 7 - 1;
      checkstring = new String(buffer, rio.LineStart + GrassOffset, rio.LineStart + GrassOffset);
      if (checkstring == " ") { // we use the Arc Style reading...
        string = new String(buffer, rio.LineStart + ArcOffset, rio.LineEnd - rio.LineStart - ArcOffset);
        ncols = (int) Double.parseDouble(string);
        System.out.println("ncols = " + ncols);
      }
      else { // we use the GRASS Style reading...
        string = new String(buffer, rio.LineStart + GrassOffset, rio.LineEnd - rio.LineStart - GrassOffset);
        ncols = (int) Double.parseDouble(string);
        System.out.println("ncols = " + ncols);
      }

      rio.readLine();
      GrassOffset = 7 - 1;
      checkstring = new String(buffer, rio.LineStart + GrassOffset, rio.LineStart + GrassOffset);
      if (checkstring == " ") { // we use the Arc Style reading...
        string = new String(buffer, rio.LineStart + ArcOffset, rio.LineEnd - rio.LineStart - ArcOffset);
        nrows = (int) Double.parseDouble(string);
        System.out.println("nrows = " + nrows);
      }
      else { // we use the GRASS Style reading...
        string = new String(buffer, rio.LineStart + GrassOffset, rio.LineEnd - rio.LineStart - GrassOffset);
        nrows = (int) Double.parseDouble(string);
        System.out.println("nrows = " + nrows);
      }

      rio.readLine();
      GrassOffset = 11 - 1;
      checkstring = new String(buffer, rio.LineStart + GrassOffset, rio.LineStart + GrassOffset);
      if (checkstring == " ") { // we use the Arc Style reading...
        string = new String(buffer, rio.LineStart + ArcOffset, rio.LineEnd - rio.LineStart - ArcOffset);
        xllcorner = (double) Double.parseDouble(string);
        System.out.println("xllcorner = " + xllcorner);
      }
      else { // we use the GRASS Style reading...
        string = new String(buffer, rio.LineStart + GrassOffset, rio.LineEnd - rio.LineStart - GrassOffset);
        xllcorner = (double) Double.parseDouble(string);
        System.out.println("xllcorner = " + xllcorner);
      }

      rio.readLine();
      GrassOffset = 11 - 1;
      checkstring = new String(buffer, rio.LineStart + GrassOffset, rio.LineStart + GrassOffset);
      if (checkstring == " ") { // we use the Arc Style reading...
        string = new String(buffer, rio.LineStart + ArcOffset, rio.LineEnd - rio.LineStart - ArcOffset);
        yllcorner = (double) Double.parseDouble(string);
        System.out.println("yllcorner = " + yllcorner);
      }
      else { // we use the GRASS Style reading...
        string = new String(buffer, rio.LineStart + GrassOffset, rio.LineEnd - rio.LineStart - GrassOffset);
        yllcorner = (double) Double.parseDouble(string);
        System.out.println("yllcorner = " + yllcorner);
      }

      rio.readLine();
      GrassOffset = 10 - 1;
      checkstring = new String(buffer, rio.LineStart + GrassOffset, rio.LineStart + GrassOffset);
      if (checkstring == " ") { // we use the Arc Style reading...
        string = new String(buffer, rio.LineStart + ArcOffset, rio.LineEnd - rio.LineStart - ArcOffset);
        cellsize = (double) Double.parseDouble(string);
        System.out.println("cellsize = " + cellsize);
      }
      else { // we use the GRASS Style reading...
        string = new String(buffer, rio.LineStart + GrassOffset, rio.LineEnd - rio.LineStart - GrassOffset);
        cellsize = (double) Double.parseDouble(string);
        System.out.println("cellsize = " + cellsize);
      }

      rio.readLine();
      GrassOffset = 14 - 1;
      checkstring = new String(buffer, rio.LineStart + GrassOffset, rio.LineStart + GrassOffset);
      if (checkstring == " ") { // we use the Arc Style reading...
        string = new String(buffer, rio.LineStart + ArcOffset, rio.LineEnd - rio.LineStart - ArcOffset);
        NoDataValue = (double) Double.parseDouble(string);
        System.out.println("NoDataValue = " + NoDataValue);
      }
      else { // we use the GRASS Style reading...
        string = new String(buffer, rio.LineStart + GrassOffset, rio.LineEnd - rio.LineStart - GrassOffset);
        NoDataValue = (double) Double.parseDouble(string);
        System.out.println("NoDataValue = " + NoDataValue);
      }

    }
    // record the metadata in an MFM
    // Beware the MAGIC NUMBER!!! the format will be SDIM because it is itty-bitty...
    MultiFormatMatrix MetaDataMatrix = new MultiFormatMatrix(1, new long[] {6,1});
    MetaDataMatrix.setValue(0,0,(double)ncols);
    MetaDataMatrix.setValue(0,1,(double)nrows);
    MetaDataMatrix.setValue(0,2,xllcorner);
    MetaDataMatrix.setValue(0,3,yllcorner);
    MetaDataMatrix.setValue(0,4,cellsize);
    MetaDataMatrix.setValue(0,5,NoDataValue);

    long numExamples = nrows;
    long numColumns = ncols;

    long numElements = nrows * ncols;
    int FormatIndex = -1;
    if (numElements < NumberOfElementsThreshold) { // small means keep it in core; single dimensional in memory is best
      FormatIndex = 1; // Beware the MAGIC NUMBER!!!
    }
    else { // not small means big, so go out of core; serialized blocks on disk are best
      FormatIndex = 3; // Beware the MAGIC NUMBER!!!
    }

    long[] dimensions = {numElements, 1};
    MultiFormatMatrix exampleSet = new MultiFormatMatrix(FormatIndex, dimensions);

    double value;

//    long StorageNumber = 0;
    for (int lineIndex = 0; lineIndex < numElements; lineIndex++) {

      rio.readLine();
      String string = new String(buffer, rio.ColumnStarts[0], rio.ColumnEnds[0] - rio.ColumnStarts[0]);
      value = Double.parseDouble(string);
      if (value == NoDataValue) {
        value = java.lang.Double.NaN;
      }
      exampleSet.setValue(lineIndex, 0, value);

/*      for (int f = 0; f < numColumns; f++) {
      }
*/
      }
    rio.close();


    this.pushOutput((MultiFormatMatrix) exampleSet, 0);
    this.pushOutput((MultiFormatMatrix) MetaDataMatrix, 1);

  }

}
