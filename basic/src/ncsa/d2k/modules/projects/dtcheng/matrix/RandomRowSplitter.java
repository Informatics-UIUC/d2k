package ncsa.d2k.modules.projects.dtcheng.matrix;

//import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.core.modules.*;

//import java.io.EOFException;
import java.util.Random;

public class RandomRowSplitter extends ComputeModule {
  
  public String getModuleName() {
    return "RandomRowSplitter";
  }
  
  public String getModuleInfo() {
    return "This module pulls in an MFM and a double and splits up the rows it into two "
    + "matrices in a pseudo random fashion. This is to give pseudo-random split-sampling "
    + "of datasets. The outputed matrices will be of the same format as the inputted matrix. "
    + "<p> This is <i>not</i> guarunteed to work. If it fails, try a different random seed.";
    
  }
  
  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "MultiFormatMatrix";
    case 1:
      return "FractionInTopMatrix";
    case 2:
      return "RandomSeed";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "MultiFormatMatrix";
    case 1:
      return "FractionInTopMatrix";
    case 2:
      return "RandomSeed";
    default:
      return "Error!  No such input.  ";
    }
  }
  
  public String[] getInputTypes() {
    String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "java.lang.Double", "java.lang.Long" };
    return types;
  }
  
  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "TopMatrix";
    case 1:
      return "BottomMatrix";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "TopMatrix";
    case 1:
      return "BottomMatrix";
    default:
      return "Error!  No such output.  ";
    }
  }
  
  public String[] getOutputTypes() {
    String[] types = { "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix", };
    return types;
  }
  
  public void doit() throws Exception {
    
    MultiFormatMatrix OriginalMatrix = (MultiFormatMatrix) this.pullInput(0);
    double FractionInTopMatrix = ((Double) this.pullInput(1)).doubleValue();
    long RandomSeed = ((Long) this.pullInput(2)).longValue();
    
    int FormatIndex = OriginalMatrix.dataFormat;
    long nExamplesOriginal = OriginalMatrix.getDimensions()[0];
    long nColumns = OriginalMatrix.getDimensions()[1];
    
    // Beware the MAGIC NUMBER!!! that is, how i set aside the overflow set...
    double OverFlowThreshold = 0.9;
    double TopThreshold = FractionInTopMatrix * OverFlowThreshold;
    
    long ExtraCapacity = 5000; // Beware the MAGIC NUMBER!!! this is some extra space in the overflow thing....
    
    long nExamplesTop = (long) (java.lang.Math.floor(nExamplesOriginal * FractionInTopMatrix) - 1.0) + 1;
    long nExamplesBottom = nExamplesOriginal - nExamplesTop;
    long nExamplesOverFlow = (long) (nExamplesOriginal * (1.0 - OverFlowThreshold)) + ExtraCapacity;
    
    MultiFormatMatrix TopMatrix = new MultiFormatMatrix(FormatIndex, new long[] { nExamplesTop, nColumns });
    MultiFormatMatrix BottomMatrix = new MultiFormatMatrix(FormatIndex, new long[] { nExamplesBottom, nColumns });
    MultiFormatMatrix OverFlow = new MultiFormatMatrix(FormatIndex, new long[] { nExamplesOverFlow, nColumns });
    
    System.out.println("   ---[" + getAlias() + "] says top fraction: " + FractionInTopMatrix +
        "; top: " + nExamplesTop + "; bottom: " + nExamplesBottom + "; overflow: " + nExamplesOverFlow);
    //	 prepare the random number generator
    Random RandomNumberGenerator = new Random(RandomSeed);
    
    long OverFlowStorageRow = 0;
    long TopStorageRow = 0;
    long BottomStorageRow = 0;
    double TheRandomNumber = 0;
    
    // initally run through and split it up three ways: top, bottom, and overflow. then we will split up the overflow to fill them out...
    for (long Example = 0; Example < nExamplesOriginal; Example++) {
      TheRandomNumber = RandomNumberGenerator.nextDouble();
      if (OverFlowStorageRow < nExamplesOverFlow) {
        // there is room in the overflow
        if (TheRandomNumber > OverFlowThreshold) {
          for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
            OverFlow.setValue(OverFlowStorageRow, ColIndex, OriginalMatrix.getValue(Example, ColIndex));
          }
          OverFlowStorageRow++;
        } else if ((TheRandomNumber < TopThreshold) && (TopStorageRow < nExamplesTop)) {
          // the above comparison was originally (TheRandomNumber > TopThreshold)
          // that appears to have been incorrect...
          
          // it should go to the top and there is room in the top
          for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
            TopMatrix.setValue(TopStorageRow, ColIndex, OriginalMatrix.getValue(Example, ColIndex));
          }
          TopStorageRow++;
        } else if (BottomStorageRow < nExamplesBottom) {
          // there is room in the bottom
          for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
            BottomMatrix.setValue(BottomStorageRow, ColIndex, OriginalMatrix.getValue(Example, ColIndex));
          }
          BottomStorageRow++;
          
          
        } else {
          // there is no room where we'd like to put it, so we'll dump it in the overflow
          for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
            OverFlow.setValue(OverFlowStorageRow, ColIndex, OriginalMatrix.getValue(Example, ColIndex));
          }
          OverFlowStorageRow++;
        }
      } else {
        // there is NO MORE room in the overflow
        if ((TheRandomNumber > FractionInTopMatrix) && (TopStorageRow < nExamplesTop)) {
          for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
            TopMatrix.setValue(TopStorageRow, ColIndex, OriginalMatrix.getValue(Example, ColIndex));
          }
          TopStorageRow++;
        } else {
          // ok, and if the overflow is full and the top is full, then, um, the bottom better have something open
          for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
            BottomMatrix.setValue(BottomStorageRow, ColIndex, OriginalMatrix.getValue(Example, ColIndex));
          }
          BottomStorageRow++;
        }
      }
    }
    
    // now we need to split up the stuff that ended up in the overflow...
    long nActuallyInOverFlow = Math.min(OverFlowStorageRow, nExamplesOverFlow);
    long nNeededInTop = nExamplesTop - TopStorageRow;
    
    for (long OverFlowExample = 0; OverFlowExample < nNeededInTop; OverFlowExample++) {
      for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
        TopMatrix.setValue(TopStorageRow, ColIndex, OverFlow.getValue(OverFlowExample, ColIndex));
      }
      TopStorageRow++;
    }
    for (long OverFlowExample = nNeededInTop; OverFlowExample < nActuallyInOverFlow; OverFlowExample++) {
      for (long ColIndex = 0; ColIndex < nColumns; ColIndex++) {
        BottomMatrix.setValue(BottomStorageRow, ColIndex, OverFlow.getValue(OverFlowExample, ColIndex));
      }
      BottomStorageRow++;
    }
    
    this.pushOutput(TopMatrix, 0);
    this.pushOutput(BottomMatrix, 1);
  }
  
}