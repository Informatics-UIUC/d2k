package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.util.*;

/**
 * <p>Title: EvaluateExecutableFitness </p>
 * <p>Description: Evaluate the new population using an executable file for fitness evaluation.
 *      The population object does all the work, this module will simply invoke the
 *      <code>evaluateAll</code> method of the population. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company:NCSA </p>
 * @author Meghna Babbar
 * @version 1.0
 */

//public class EvaluateExecutableFitness extends EvaluateModule {
public class EvaluateExecutableFitness
    extends ncsa.d2k.core.modules.ComputeModule {

//  private int numExecFitness;
  //MutableTableImpl fitnessExecs;// new TableImpl();
//  private int[] execFitnessIds;
//  private int[] numOutputFiles;

  private Table fitnessExecs; // new TableImpl();
  private String[] execPathNames;
  private String[] execInputFileNames;
  private String[] execOutputFileNames;

  //////////////////////////////////
  // Info methods
  //////////////////////////////////

  /**
   This method returns the description of the module.
   @return the description of the module.
   */

  public String getModuleInfo() {
    return "Evalute this population using a fitness executable.";
  }

  /**
     This method returns the description of the various inputs.
     @return the description of the indexed input.
   */
  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return
            "The population contains all individuals for the multiobjective problem";
        //case 1: return "A TableImpl data structure that contains fitness ids for fitnesses evaluated using the executables and the entire paths names for executables with runtime arguments.";
      default:
        return "No such input";
    }
  }

  /**
   This method returns an array of strings that contains the data types for the inputs.
   @return the data types of all inputs.
   */
  public String[] getInputTypes() {
    //String[] types = {"ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation",
    //                "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.NsgaPopulation"};
    //"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
    return types;
  }

  /**
   This method returns the description of the outputs.
   @return the description of the indexed output.
   */
  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "<p>      The population contains all individuals for the multiobjective problem with the updated fitness.    </p>";
//      case 1:
//        return "A TableImpl data structure that contains fitness ids for fitnesses evaluated using the executables and the entire paths names for executables with runtime arguments.";
      default:
        return "No such output";
    }
  }

  /**
   This method returns an array of strings that contains the data types for the outputs.
   @return the data types of all outputs.
   */
  public String[] getOutputTypes() {
    //String[] types = {"ncsa.d2k.modules.core.optimize.ga.emo.NsgaPopulation",
    //            "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
//                                  "ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};

    return types;
  }

  /**
   * Return the human readable name of the indexed input.
   * @param index the index of the input.
   * @return the human readable name of the indexed input.
   */
  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "NsgaPopulation";
//      case 1:
//        return "Fitness executable table";
      default:
        return "NO SUCH INPUT!";
    }
  }

  /**
   * Return the human readable name of the indexed output.
   * @param index the index of the output.
   * @return the human readable name of the indexed output.
   */
  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "MsgaPopulation";
//      case 1:
//        return "Fitness executable table";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void beginExecution() {
    fitnessExecs = null;
  }

  public void endExecution() {
    fitnessExecs = null;
  }

  /**
   Evaluate the individuals in this class.
   */
  public void doit() throws Exception {
    /*if(fitnessExecs == null) {
      fitnessExecs = (MutableTableImpl) this.pullInput(1);
      return;
              }*/

    //  System.out.println("***************************************************");
    //  System.out.println("entering doit function of EvaluateExecutableFitness");
    NsgaPopulation pop = (NsgaPopulation)this.pullInput(0);
    fitnessExecs = pop.getPopulationInfo().externalFitnessInfo;

    //Population pop = (Population) this.pullInput(0);
    // ***************

    //fitnessExecs = new MutableTableImpl();
    //fitnessExecs = (MutableTableImpl) this.pullInput(1);

    // **************
    //execFitnessIds = this.getFitnessIds();

//            execPathNames = new String[getNumFitnessExecs()];
    execPathNames = this.getFitnessExecPaths();

//            execInputFileNames = new String[getNumFitnessExecs()];
    execInputFileNames = this.getInputFileNames();

//            execOutputFileNames = new String[getNumFitnessExecs()];
    execOutputFileNames = this.getOutputFileNames();

//            numOutputFiles= new int[getNumFitnessExecs()];
    //numOutputFiles = this.getNumOutputFiles();
    // writing individual genes to all input files
    // and prepare the input files before execution

    Individual [] members = pop.getMembers();
    if(members.length > 0) {
      if(members[0] instanceof MOBinaryIndividual)
        this.writeBinaryGenesToFile(pop, execInputFileNames);
      else
        this.writeNumericGenesToFile(pop, execInputFileNames);
    }

    // evaluating fitness of individuals
    //Individual ind = pop.getMember(i);
    //if (ind.isDirty()){
    //System.out.println("***************************************************");
    //System.out.println("CurrentGeneration in evaluateexecutablefitness: " + pop.getCurrentGeneration());
    //System.out.println("execFitnessIds1 is " + execFitnessIds[0]);
    //System.out.println("execFitnessIds1 is " + execFitnessIds[0]);
    this.evaluatePopulation(pop);

    this.pushOutput(pop, 0);
    //this.pushOutput (fitnessExecs, 1);
  }

 /*
  * This writes the genes of individuals to input files for different
  * fitness function executables, that might be present in different directories
  */
  public void writeBinaryGenesToFile (Population popul, String[] execInputFileNames) throws IOException {
  //(NsgaPopulation popul, String[] execInputFileNames) throws IOException {

    //  String genes = new String() ;

      for (int i = 0 ; i < getNumFitnessExecs(); i++) {
          try {
             if (execInputFileNames[i] != null){
                 FileWriter stringFileWriter = new FileWriter(execInputFileNames[i]);
                 BufferedWriter bw = new BufferedWriter(stringFileWriter);
                 PrintWriter pw = new PrintWriter(bw);

                // NsgaSolution ni = (NsgaSolution)  popul.getMember(0);
                  //double [] genes = (double []) ((Individual) ni).getGenes ();
                 MOBinaryIndividual ni = (MOBinaryIndividual) popul.getMember(0);
               // double [] genes = (double []) ni.getGenes ();
                 boolean [] genes = (boolean []) ni.getGenes ();
               //  int [] genes = (int [])popul.getMember(0).getGenes ();
                 int numTraits = genes.length;
//                 System.out.println("genes length" + numTraits);
//                 System.out.println("*****************************");

                 //System.out.println("popul size in "+ execInputFileNames[i]+"is "+ popul.size ());

                 // write population size in file
                 pw.println(popul.size ());
                 // write length of each gene/chromosome
                 pw.println(numTraits);
                 // write genes
                 for ( int j = 0; j < popul.size (); j++ ){
                 //   genes = popul.getMember(j).getGenes().toString();
                 //   genes = (double [])((MONumericIndividual) popul.getMember(j)).getGenes ();
                  genes = (boolean [])((MOBinaryIndividual) popul.getMember(j)).getGenes ();
                    numTraits = genes.length;

                    //if (popul.getMember(j).isDirty()){
                     // write to file
                    // System.out.println("numtraits :" + numTraits);
                       for (int k =0; k < numTraits; k++){
                         if (genes[k] == false){
                           // pw.print(genes[k] + "  ");
                           pw.print(0 + "  ");
                         } else {
                           pw.print(1 + "  ");
                         }
                       }
                       pw.println();
                       //System.out.println("indiv id "+ j);
                    // }

                  }
                 pw.println();
                 // close file and streams
                 pw.flush();
                 bw.flush();
                 stringFileWriter.flush();
                 pw.close();
                 bw.close();
                 stringFileWriter.close();

                 }
              }
          catch (Exception e) {

                 e.printStackTrace();
                 System.err.println("IOException");
//                 System.exit(1);

                 }
          }

  }




  private void evaluatePopulation(Population popul) { //(NsgaPopulation popul) { //

    //MONumericIndividual[] ni = new MONumericIndividual[popul.size()];
    NsgaSolution[] ni = new NsgaSolution[popul.size()];
    String outputFileName = new String();

    for (int i = 0; i < popul.size(); i++) {
      //ni[i] = (MONumericIndividual) popul.getMember(i);
      ni[i] = (NsgaSolution) popul.getMember(i);
    }

    /* Evaluate the Fitnesses using the fitness executable path names and alloting
       the fitness values to respective ids in the population
    */

    HashSet rows = new HashSet();
    rows.add(new Integer(0));
    // determine which need to be run
    for (int j = 1; j < getNumFitnessExecs(); j++) {

      String exec = execPathNames[j];
      String in = execInputFileNames[j];

      for (int k = j; k < getNumFitnessExecs(); k++) {
        String exec2 = execPathNames[j];
        String in2 = execInputFileNames[j];

        if (!exec.equals(exec2) && !in.equals(in2)) {
          rows.add(new Integer(k));
        }
      }
    }

    int fitnessID = 0;
    for (int i = 0; i < getNumFitnessExecs(); i++) {

      // ***************** call execrunner
      /**
       * @param args an array of command-line arguments
       * @throws IOException thrown if a problem occurs
       **/

      double[] fitness = new double[popul.size()];

      try {
//System.out.println(" calling executable : "+ execPathNames[i]+" "+i);
        if (rows.contains(new Integer(i))) {
          ExecRunner er = new ExecRunner();
          //er.setMaxRunTimeSecs(0);
          er.exec(execPathNames[i].toString());
//        System.out.println("<STDOUT>\n" + er.getOutString() + "</STDOUT>");
//        System.out.println("<STDERR>\n" + er.getErrString() + "</STDERR>");
        }


        // Exit nicely
        // System.exit(0);

        // Obtain fitnesses after evaluation is over
        // reading fitness of all individuals from all output files
        //for (int k = 0; k < numOutputFiles[i]; k++) {
        //for (int k = 0; k < execOutputFileNames.length; k++) {
          outputFileName = execOutputFileNames[i];
          //System.out.println("name of fitness file : " + outputFileName);
          //System.out.println(" **************** ");
          fitness = this.readFitnessFromFile(popul, outputFileName);
          for (int j = 0; j < popul.size(); j++) {
            ni[j].setObjective(fitnessID, fitness[j]);
            //  System.out.println("Fitness" + fitnessID + " in individual : " + j + "is " + fitness[j] );
          }

          //  System.out.println("Fitness in module : " + fitnessID );
          //  System.out.println("Fitness 1 in individual : " + fitness[1] );
          //  System.out.println("Fitness 2 in individual : " + fitness[2] );
          fitnessID = fitnessID + 1;
        //}

      }
      catch (Exception e) {

        e.printStackTrace();
        //System.exit(1);

      }

    } // i loop for number of fitnesses

  } // evaluateIndividual

  /*
   * This writes the genes of individuals to input files for different
       * fitness function executables, that might be present in different directories
   */
  private void writeNumericGenesToFile(Population popul, String[] execInputFileNames) throws
      IOException {
    //(NsgaPopulation popul, String[] execInputFileNames) throws IOException {

    //  String genes = new String() ;

    for (int i = 0; i < getNumFitnessExecs(); i++) {
      try {
        if (execInputFileNames[i] != null) {
          FileWriter stringFileWriter = new FileWriter(execInputFileNames[i]);
          BufferedWriter bw = new BufferedWriter(stringFileWriter);
          PrintWriter pw = new PrintWriter(bw);

          // NsgaSolution ni = (NsgaSolution)  popul.getMember(0);
          //double [] genes = (double []) ((Individual) ni).getGenes ();
          MONumericIndividual ni = (MONumericIndividual) popul.getMember(0);
          double[] genes = (double[]) ni.getGenes();
          //  boolean [] genes = (boolean []) ni.getGenes ();
          //  int [] genes = (int [])popul.getMember(0).getGenes ();
          int numTraits = genes.length;
          //System.out.println("genes length" + numTraits);
          //System.out.println("*****************************");

          //System.out.println("popul size in "+ execInputFileNames[i]+"is "+ popul.size ());

          // write population size in file
          pw.println(popul.size());
          // write length of each gene/chromosome
          pw.println(numTraits);
          // write genes
          for (int j = 0; j < popul.size(); j++) {
            //   genes = popul.getMember(j).getGenes().toString();
            genes = (double[]) ( (MONumericIndividual) popul.getMember(j)).
                getGenes();
            // genes = (boolean [])((MOBinaryIndividual) popul.getMember(j)).getGenes ();
            numTraits = genes.length;

            //if (popul.getMember(j).isDirty()){
            // write to file
            // System.out.println("numtraits :" + numTraits);
            for (int k = 0; k < numTraits; k++) {
              //if (genes[k] == false){
              pw.print(genes[k] + "  ");
              // pw.print(0 + "  ");
              //} else {
              // pw.print(1 + "  ");
              // }
            }
            pw.println();
            //System.out.println("indiv id "+ j);
            // }

          }
          pw.println();
          // close file and streams
          pw.flush();
          bw.flush();
          stringFileWriter.flush();
          pw.close();
          bw.close();
          stringFileWriter.close();

        }
      }
      catch (Exception e) {

        e.printStackTrace();
        System.err.println("IOException");
        //System.exit(1);

      }
    }

  }

  /*
   * This read the fitness of individuals from output files for different
       * fitness function executables, that might be present in different directories
   */
  private double[] readFitnessFromFile(Population popul, String outFileName) throws
      IOException {
//(NsgaPopulation popul, String outFileName ) throws IOException {
    double[] fit = new double[popul.size()];
    String[] sline = new String[2 * popul.size()];
    try {
      if (outFileName != null) {
        FileReader stringFileReader = new FileReader(outFileName);
        BufferedReader br = new BufferedReader(stringFileReader);

        int i;
//System.out.println("Name of fitness file in read fitness : " + outFileName);
        i = 0;
        sline[i] = br.readLine();
        while (sline[i] != null) {
//System.out.println("sline " + i + " : " + sline[i]);
          //Double db = new Double(sline[i]);
          //fit[i] = db.doubleValue();
          fit[i] = Double.parseDouble(sline[i]);//db.parseDouble(sline[i]);
          i = i + 1;
          sline[i] = br.readLine();
        }
        //System.out.println("num of fitness in read fitness" + i);
        //System.out.println("fitness 1 in read fitness" + fit[1]);
        //   System.out.println("fitness 145 in read fitness" + fit[145]);

        // close file and streams
        br.close();
        stringFileReader.close();

      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("IOException");
      //System.exit(1);
    }
    return fit;
  }

  /*
   * This returns the ID of all the fitness functions that are evaluated using
   * an executable.
   */
/*  public int[] getFitnessIds() {
    int[] execFitIds = new int[getNumFitnessExecs()];
    for (int i = 0; i < getNumFitnessExecs(); i++) {
      execFitIds[i] = fitnessExecs.getInt(i, 0);
    }

    return execFitIds;
  }*/

  /*
   * This returns an array with entire path names of fitness executables
   * and the directory structure where the executable is.
   */
  private String[] getFitnessExecPaths() {
    String[] execFitNames = new String[getNumFitnessExecs()];
    for (int i = 0; i < getNumFitnessExecs(); i++) {
      //execFitNames[i] = fitnessExecs.getString(i,1);
      execFitNames[i] = fitnessExecs.getString(i, 1);
    }
    return execFitNames;
  }

  /*
   * This returns the input file names of all the fitness functions that are evaluated using
   * an executable. These files would contain the population for evaluation.
   */
  private String[] getInputFileNames() {
    String[] execInputFileName = new String[getNumFitnessExecs()];
    for (int i = 0; i < getNumFitnessExecs(); i++) {
      //execInputFileName[i] = fitnessExecs.getString(i,2);
      execInputFileName[i] = fitnessExecs.getString(i, 2);
    }
    return execInputFileName;
  }

  /*
   * This returns the number of output files of all the fitness functions that are evaluated using
       * an executable. These files would contain the fitness values after evaluation.
   */
/*  private int[] getNumOutputFiles() {
    int[] OutputFileNum = new int[getNumFitnessExecs()];
    for (int i = 0; i < getNumFitnessExecs(); i++) {
      //OutputFileNum[i] = fitnessExecs.getInt(i,3);
      OutputFileNum[i] = 1;
    }
    return OutputFileNum;
  }*/

  /*
   * This returns the output file names of all the fitness functions that are evaluated using
       * an executable. These files would contain the fitness values after evaluation.
   */
  /*        public String[][] getOutputFileNames() {
             int[] numFiles = new int[10];
             numFiles = this.getNumOutputFiles();
       String[][] execOutputFileName = new String[getNumFitnessExecs()][1];
             for ( int i = 0; i < getNumFitnessExecs(); i++ ){
                for ( int j = 0; j < numFiles[i]; j++ ){
//                  execOutputFileName[i][j] = fitnessExecs.getString(i,j+4);
                }
             }
             return execOutputFileName;
          }*/

  /*
   * This returns the input file names of all the fitness functions that are evaluated using
   * an executable. These files would contain the population for evaluation.
   */
  private String[] getOutputFileNames() {
    String[] execOutputFileName = new String[getNumFitnessExecs()];
    for (int i = 0; i < getNumFitnessExecs(); i++) {
      //execInputFileName[i] = fitnessExecs.getString(i,2);
      execOutputFileName[i] = fitnessExecs.getString(i, 3);
    }
    return execOutputFileName;
  }

  /*
   * This returns the number of fitness functions that are evaluated using an executable.
   */
  private int getNumFitnessExecs() {
    int size;
    size = fitnessExecs.getNumRows();
    return size;
  }

}