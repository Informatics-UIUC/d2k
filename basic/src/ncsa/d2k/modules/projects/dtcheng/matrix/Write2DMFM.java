package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
//import Jama.Matrix;

public class Write2DMFM
    extends OutputModule {

  public String getModuleName() {
    return "Write2DMFM";
  }

  public String getModuleInfo() {
    return "This module attempts to write a 2-d MFM to disk";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
      	return "BufferSize";
      case 2:
        return "FileName";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
      	return "BufferSize: number of elements to put in each block";
      case 2:
        return "FileName: base name of where to store matrix";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
		"java.lang.Integer",
        "java.lang.String",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
     };
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix X = (MultiFormatMatrix)this.pullInput(0);
    int RawBufferSize = ((Integer)this.pullInput(1)).intValue();
    String FileName = (String)this.pullInput(2);

    int nRows = (int)X.getDimensions()[0];
    int nCols = (int)X.getDimensions()[1];

    int BufferSize = (RawBufferSize/nCols + 1) * nCols;

    
    double [] DoubleBuffer = new double[BufferSize];

    int i = 0;
    int BlockIndex = 0;
    for (long RowIndex = 0; RowIndex < nRows; RowIndex++) {
      for (long ColIndex = 0; ColIndex < nCols; ColIndex++) {

        DoubleBuffer[i++] = X.getValue(RowIndex,ColIndex);

        if (i == BufferSize) {
          try {
            System.out.println("Writing File: " + FileName + BlockIndex + ".bin ...");
            FileOutputStream file = new FileOutputStream(FileName + BlockIndex + ".bin");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(DoubleBuffer);
            out.flush();
            out.close();
            i = 0;
            BlockIndex++;
          }
          catch (java.io.IOException IOE) {
            System.out.println("IOExceptionMiddle");
          }
        }

      }
    }

    // write remaining block
    if (i != 0) {
      double[] DoubleBuffer2 = new double[i];
      System.arraycopy(DoubleBuffer, 0, DoubleBuffer2, 0, i);
      try {
        System.out.println("Writing File: " + FileName + BlockIndex + ".bin ...");
        FileOutputStream file = new FileOutputStream(FileName + BlockIndex + ".bin");
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(DoubleBuffer2);
        out.flush();
        out.close();
      }
      catch (java.io.IOException IOE) {
        System.out.println("IOExceptionBottom");
      }
    }

    // write out the metadata...
    try {
        FileOutputStream file = new FileOutputStream(FileName + ".bin");
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeInt(2);
        out.writeInt(BufferSize);
        out.writeInt(BlockIndex);
        out.writeInt(nRows);
        out.writeInt(nCols);
        out.flush();
        out.close();
      }
      catch (java.io.IOException IOE) {
        System.out.println("IOExceptionTop");
      }

    
    this.pushOutput(X, 0);

  }

}
