package ncsa.d2k.modules.projects.dtcheng.matrix;


//import ncsa.d2k.modules.projects.dtcheng.*;

import ncsa.d2k.core.modules.*;


public class MakeLayerTable extends ComputeModule {

  public String getModuleName() {
    return "MakeLayerTable";
  }


  public String getModuleInfo() {
    return "This module creates the LayerTable for the Neural Net " +
        "implementation based on the number of inputs, a HiddenLayerTable, " +
        "and the number of options (minus one). If no hidden layers are " +
        "desired, the HiddenLayerTable should have a zero as its first element.";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "nExplanatoryVariables";
      case 1:
        return "HiddenLayerTable";
      case 2:
        return "nOptionsMinusOne";
      case 3:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "nExplanatoryVariables";
      case 1:
        return "HiddenLayerTable";
      case 2:
        return "nOptionsMinusOne";
      case 3:
        return "NumberOfElementsThreshold";
      default:
        return "Error!  No such input.  ";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Long",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
        return "nNeurons";
      case 2:
        return "nInputs";
      case 3:
        return "nLayers";
      case 4:
        return "nWeights";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "LayerTable";
      case 1:
        return "nNeurons";
      case 2:
        return "nInputs";
      case 3:
        return "nLayers";
      case 4:
        return "nWeights";
      default:
        return "Error!  No such output.  ";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Long",
        "java.lang.Long",
    };
    return types;
  }


  public void doit() throws Exception {

    long nExplanatoryVariables = ((Long)this.pullInput(0)).longValue();
    MultiFormatMatrix HiddenLayerTable = (MultiFormatMatrix)this.pullInput(1);
    long nOptionsMinusOne = ((Long)this.pullInput(2)).longValue();
    long NumberOfElementsThreshold = ((Long)this.pullInput(3)).longValue();

    long NumElements = -1;
    int FormatIndex = -2;

    if (HiddenLayerTable.getValue(0,0) == 0.0) {
      NumElements = 2;
      FormatIndex = 1;
    }
    else {
        // determine the proper format
        // Beware the MAGIC NUMBER!!! 2 = 1 row for inputs + 1 row for outputs
        NumElements = HiddenLayerTable.getDimensions()[0] + 2;
        if (NumElements < NumberOfElementsThreshold) {
          // small means keep it in core; single dimensional in memory is best
          FormatIndex = 1; // Beware the MAGIC NUMBER!!!
        }
        else { // not small means big, so write it out of core; serialized blocks
          // on disk are best
          FormatIndex = 3; // Beware the MAGIC NUMBER!!!
        }
      }


    MultiFormatMatrix LayerTable = new MultiFormatMatrix(FormatIndex, new long[] {NumElements, 1});

    LayerTable.setValue(0, 0, nExplanatoryVariables);
    LayerTable.setValue(NumElements - 1, 0, nOptionsMinusOne);

    for (long RowIndex = 1; RowIndex < NumElements - 1; RowIndex++) {
      LayerTable.setValue(RowIndex, 0, HiddenLayerTable.getValue(RowIndex - 1, 0));
    }

    Long nLayers = new Long(LayerTable.getDimensions()[0]);

    long nNeuronsTemp = 0;
    for (long RowIndex = 0; RowIndex < nLayers.intValue(); RowIndex++) {
      nNeuronsTemp += (long)LayerTable.getValue(RowIndex,0);
    }
    Long nNeurons = new Long(nNeuronsTemp);

    Long nInputs = new Long((long)LayerTable.getValue(0,0));

    long nWeightsTemp = 0;
    // Beware the MAGIC NUMBER!!! the "nLayers.intValue() - 1" gets us to
    // the next to the last element...
    for (long RowIndex = 0; RowIndex < nLayers.intValue() - 1; RowIndex++) {
      nWeightsTemp += ((long)LayerTable.getValue(RowIndex,0) *
                       (long)LayerTable.getValue(RowIndex + 1, 0));
    }
    Long nWeights = new Long(nWeightsTemp);

    this.pushOutput(LayerTable, 0);
    this.pushOutput(nNeurons, 1);
    this.pushOutput(nInputs, 2);
    this.pushOutput(nLayers, 3);
    this.pushOutput(nWeights, 4);
  }

}
