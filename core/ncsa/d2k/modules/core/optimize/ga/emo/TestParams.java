package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.emo.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.mutation.*;

import ncsa.d2k.modules.core.optimize.ga.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.*;

import ncsa.d2k.core.modules.agenda.*;
import java.util.*;

public class TestParams extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }
  
  public static final String SELECTION = "Selection";
  public static final String CROSSOVER = "Crossover";
  public static final String MUTATION = "Mutation";

  public void doit() {
    Parameters params = (Parameters)pullInput(0);
    
/*    AgendaManager am = this.getExecutionManager().getAgendaManager(); 
    HashMap map = am.getModules();
    
    // get the selection module
    SystemModule sel = (SystemModule)map.get(SELECTION);
    
    // the index of the parent's output that connects to this
    int parentIndex = sel.getParentIndex(0);
    SystemModule parent = sel.getParents()[0];
    
    // the index of the child's input that connects to this output
    int childIndex = sel.getChildIndex(0);
    SystemModule child = sel.getChildren()[0];
    
    SystemModule newSelection = new Truncation();
    newSelection.initModule();
    newSelection.setAlias(SELECTION);
    
    am.disconnectPipe(parent, parentIndex);
    am.disconnectPipe(sel, 0);
    
    am.removeModule(sel);
    am.addModuleToManager(newSelection);
    am.connectPipe(parent, parentIndex, newSelection, 0);
    am.connectPipe(newSelection, 0, child, childIndex); 
    
    
    //  now get the Crossover module
    SystemModule cross = (SystemModule)map.get(CROSSOVER);
    
    // the index of the parent's output that connects to this
    parentIndex = cross.getParentIndex(0);
    parent = cross.getParents()[0];

    // the index of the child's input that connects to this output
    childIndex = cross.getChildIndex(0);
    child = cross.getChildren()[0];

    SystemModule newCrossover = new SimulatedBinaryCrossover();
    newCrossover.initModule();
    newCrossover.setAlias(CROSSOVER);

    am.disconnectPipe(parent, parentIndex);
    am.disconnectPipe(cross, 0);

    am.removeModule(cross);
    am.addModuleToManager(newCrossover);
    am.connectPipe(parent, parentIndex, newCrossover, 0);
    am.connectPipe(newCrossover, 0, child, childIndex); 
    
    
    // now get the Mutation module 
    SystemModule mut = (SystemModule)map.get(MUTATION);
    // the index of the parent's output that connects to this
    parentIndex = mut.getParentIndex(0);
    parent = mut.getParents()[0];

    // the index of the child's input that connects to this output
    childIndex = mut.getChildIndex(0);
    child = mut.getChildren()[0];

    SystemModule newMutation = new RealMutation();
    newMutation.initModule();
    newMutation.setAlias(MUTATION);

    am.disconnectPipe(parent, parentIndex);
    am.disconnectPipe(mut, 0);

    am.removeModule(mut);
    am.addModuleToManager(newMutation);
    am.connectPipe(parent, parentIndex, newMutation, 0);
    am.connectPipe(newMutation, 0, child, childIndex); 
  */   
 params.crossover = new SimulatedBinaryCrossoverObj();
 params.mutation = new RealMutationObj();
 params.selection = new TruncationObj();
    
    params.populationSize = 250;
    params.maxGenerations = 100;

    pushOutput(params, 0);
  }
}