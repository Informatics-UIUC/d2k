package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;
import java.util.*;

import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

/**
 * Evaluate the fitness functions and constraints for an EMOPopulation.
 */
public class EvaluatePopulation
    extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
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

  public void beginExecution() {
    fitnessFunctions = null;
    population = null;
    populationTable = null;
  }

  public void endExecution() {
    fitnessFunctions = null;
    population = null;
    populationTable = null;
  }

  /** the population */
  private EMOPopulation population;
  /** the table to copy the population into, if needed for calculation of FF */
  private MutableTable populationTable;

  /** the fitness functions for the problem */
  private FitnessFunctions fitnessFunctions;
  /** the number of fitness variables defined by Constructions */
  private int numFitnessVars;
  /** the number of fitness functions defined by Constructions */
  private int numFitnessFunctions;
  /** the number of fitness functions calculated by an executle */
  private int numExternalFF;

  private Constraints constraints;
  private int numConstraintVars;
  private int numConstraintFunctions;
  private int numExternalConstraints;

  /** true if FF through constructions are present */
  private boolean hasFF;
  private boolean hasFVars;
  /** true if FF through executables are present */
  private boolean hasExternalFF;

  private boolean hasCon;
  private boolean hasCVars;
  private boolean hasExternalCon;

  private List executeList;
  private List inputFileList;

  /** true if the individuals in the population are binary solutions */
  private boolean binaryType;

  private class Pair {
    String exec;
    String input;

    public boolean equals(Object o) {
      Pair p = (Pair) o;

      return p.exec.equals(this.exec) && p.input.equals(this.input);
    }
  }

  public void doit() throws Exception {
    // pull in the population
    EMOPopulation pop = (EMOPopulation) pullInput(0);
    // if the population has changed, get the parameters of this new population
    if (pop != population) {
      population = pop;
      // get the fitness functions
      fitnessFunctions = pop.getParameters().fitnessFunctions;
      // create a table to copy the population into
      populationTable = pop.getParameters().decisionVariables.
          createVariableNameTable();

      // check to see if there are any fitness functions done by Constructions
      numFitnessVars = fitnessFunctions.getNumFitnessVariables();
      hasFVars = (numFitnessVars > 0);
      numFitnessFunctions = fitnessFunctions.getNumFitnessFunctions();
      hasFF = (numFitnessFunctions > 0);

      // check to see if there are any FF done by an external executable
      numExternalFF = fitnessFunctions.getNumExternFitnessFunctions();
      hasExternalFF = (numExternalFF > 0);

      // if no fitness functions were given, quit
      if ( (numFitnessFunctions + numExternalFF) == 0) {
        throw new Exception("No fitness functions could be found.");
      }

      constraints = pop.getParameters().constraints;
      numConstraintVars = constraints.getNumConstraintVariables();
      hasCVars = (numConstraintVars > 0);
      numConstraintFunctions = constraints.getNumConstraintFunctions();
      hasCon = (numConstraintFunctions > 0);
      numExternalConstraints = constraints.getNumExternConstraints();
      hasExternalCon = (numExternalConstraints > 0);

      // if we have external FF, determine which executables need to
      // be executed each time (i.e. weed out duplicates) and which
      // files need to be written each time
      if (hasExternalFF || hasExternalCon) {
        // determine which executables need to be run.
        // if the same executable and input file occur, that only needs
        // to be executed once

        executeList = new ArrayList();
        Pair firstOne = new Pair();
        if (hasExternalFF) {
          firstOne.exec = fitnessFunctions.getExternFitnessFunctionExec(0);
          firstOne.input = fitnessFunctions.getExternFitnessFunctionInput(0);
        }
        else {
          firstOne.exec = constraints.getExternConstraintExec(0);
          firstOne.input = constraints.getExternConstraintInput(0);
        }

        executeList.add(firstOne);

        // for each extern ff
        for (int i = 0; i < this.numExternalFF; i++) {
          Pair p = new Pair();
          p.exec = fitnessFunctions.getExternFitnessFunctionExec(i);
          p.input = fitnessFunctions.getExternFitnessFunctionInput(i);
          if (!executeList.contains(p)) {
            executeList.add(p);
          }
        }

        // for each extern constraint
        for (int i = 0; i < this.numExternalConstraints; i++) {
          Pair p = new Pair();
          p.exec = constraints.getExternConstraintExec(i);
          p.input = constraints.getExternConstraintInput(i);
          if (!executeList.contains(p)) {
            executeList.add(p);
          }
        }

        // now we have a collection of the unique execs that need to be
        // executed each time

        // now find the unique input files
        HashSet inputs = new HashSet();

        for (int i = 0; i < this.numExternalFF; i++) {
          String file = fitnessFunctions.getExternFitnessFunctionInput(i);
          if (!inputs.contains(file)) {
            inputs.add(file);
          }
        }

        for (int i = 0; i < this.numExternalConstraints; i++) {
          String file = constraints.getExternConstraintInput(i);
          if (!inputs.contains(file)) {
            inputs.add(file);
          }
        }

        inputFileList = new ArrayList(inputs);
      }

      // determine if the population contains binary or real-coded genes
      Individual[] individuals = ( (Population) pop).getMembers();
      if (individuals.length > 0) {
        if (individuals[0] instanceof BinarySolution)
          binaryType = true;
        else
          binaryType = false;
      }
    }

    // copy the population into the population table if any
    // Constructions are used to calculate FF or Constraints
    if (hasFVars || hasCVars || hasFF || hasCon) {
      copyPopulationToTable();
    }

    // this is the number of columns in the table by default
    // keep it, because we are going to remove the columns
    // created by Constructions when we are done
    int numDecisionVariables = populationTable.getNumColumns();

    // first evaluate the FF variables and FF created by Constructions
    if (hasFF || hasFVars) {
      // evaluate the fitness variables.  this is done by performing a
      // transformation on the table.  the last column is the new variable
      for (int i = 0; i < numFitnessVars; i++) {
        Construction c = fitnessFunctions.getFitnessVariable(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate fitness variable: " +
                              fitnessFunctions.getFitnessVariableName(i));
        }
      }

      // keep the indices of the FF columns, so that we can set the
      // values on the individuals later
      int[] ffColumns = new int[numFitnessFunctions];

      // evaluate the fitness functions.  this is done by performing a
      // transformation on the table.  the last column is the value of the FF
      for (int i = 0; i < numFitnessFunctions; i++) {
        Construction c = fitnessFunctions.getFitnessFunction(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate fitness function: " +
                              fitnessFunctions.getFitnessFunctionName(i));
        }
        ffColumns[i] = populationTable.getNumColumns() - 1;
      }

      boolean multiObjective = (this.numFitnessFunctions > 1);
      // after transforming the table, we update the
      // population by filling in the corresponding fitness
      // function values into it.
      int sz = ( (Population) pop).size();
      if (sz > 0) {
        Individual member = ( (Population) pop).getMember(0);
        for (int i = 0; i < sz; i++) {
          Individual mymember = ( (Population) pop).getMember(i);
          // set all the objective values for an MO Solution
          if (multiObjective) {
            MOSolution myni = (MOSolution) mymember;
            for (int j = 0; j < numFitnessFunctions; j++) {
              myni.setObjective(j, populationTable.getDouble(i, ffColumns[j]));
            }
          }
          // set the single objective value for an SO Solution
          else {
            SOSolution mysi = (SOSolution) mymember;
            mysi.setObjective(populationTable.getDouble(i, ffColumns[0]));
          }
        }
      }
    }

    // evaluate the constraint variables and constraints created by Constructions
    if (hasCon) {
      // calculate the constraints
      // evaluate the constraint variables.  this is done by performing a
      // transformation on the table.  the last column is the new variable
      for (int i = 0; i < this.numConstraintVars; i++) {
        Construction c = constraints.getConstraintVariable(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate constraint variable: " +
                              constraints.getConstraintVariableName(i));
        }
      }

      // keep the indices of the FF columns, so that we can set the
      // values on the individuals later
      int[] conColumns = new int[this.numConstraintFunctions];

      // evaluate the fitness functions.  this is done by performing a
      // transformation on the table.  the last column is the value of the constraint
      for (int i = 0; i < this.numConstraintFunctions; i++) {
        Construction c = constraints.getConstraintFunction(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate constraint function: " +
                              constraints.getConstraintFunctionName(i));
        }
        conColumns[i] = populationTable.getNumColumns() - 1;
      }

      // after transforming the table, we update the
      // population by filling in the corresponding fitness
      // function values into it.
      int size = ( (Population) pop).size();
      for (int i = 0; i < size; i++) {
        Individual mymember = ( (Population) pop).getMember(i);
        for (int j = 0; j < this.numConstraintFunctions; j++) {
          double weight = constraints.getConstraintFunctionWeight(j);
          mymember.setConstraint(populationTable.getDouble(i, conColumns[j])*weight, j);
        }
      }
    }

    // remove all columns created by the transformations
    int numAddedColumns = this.numConstraintVars + this.numConstraintFunctions +
        this.numFitnessVars + this.numFitnessFunctions;
    for (int i = 0; i < numAddedColumns; i++) {
      populationTable.removeColumns(numDecisionVariables, numAddedColumns);
    }

    // write out the population if external executables are used
    // we only need to write out the first file, and make copies for
    // the rest
    if (hasExternalFF || hasExternalCon) {
      // we need to write out the file.  Only write out the first file,
      // make copies for all other executables
      String firstFile = (String)this.inputFileList.get(0);
      //if (binaryType)
        this.writeBinaryGenesToFile(firstFile);
      //else
      //  this.writeNumericGenesToFile(firstFile);

      // now make copies of the file for the other inputs
      for (int i = 1; i < inputFileList.size(); i++) {
        String fileName = (String) inputFileList.get(i);
        copyFile(new File(firstFile), new File(fileName));
      }

      // now execute the executables
      for (int i = 0; i < executeList.size(); i++) {
        Pair pair = (Pair) executeList.get(i);
        String exec = pair.exec;
        ExecRunner er = new ExecRunner();
        er.exec(exec);
      }

      // read the output for FF
      for (int i = 0; i < this.numExternalFF; i++) {
        this.readExternalFFOutput(i);
      }

      // read the output for constraints
      for (int i = 0; i < this.numExternalConstraints; i++) {
        this.readExternalConstraintOutput(i);
      }
    }

    pushOutput(pop, 0);
  }

  /**
   * Copy the population into the populationTable.
   */
  private void copyPopulationToTable() {
    Population pop = (Population) population;
    // ensure that the table has the same number of rows as the number
    // of individuals in the population
    if (pop.size() != populationTable.getNumRows()) {
      populationTable.setNumRows(pop.size());
    }

    int numOfvar = pop.getNumGenes();
    // copy real-valued variables directly into the table
    if (!binaryType) {
      for (int i = 0; i < pop.size(); i++) {
        double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
        for (int j = 0; j < numOfvar; j++) {
          populationTable.setDouble(tabrows[j], i, j);
        }
      }
    }

    // otherwise, decode binary variables so that the entries copied into the
    // table are real-valued
    else {
      int numTraits = 0;
      BinaryRange[] traits = (BinaryRange[]) pop.getTraits();
      numTraits = traits.length;

      // for each individual
      for (int j = 0; j < pop.size(); j++) {
        double[] values = ((BinaryIndividual)pop.getMember(j)).toDoubleValues();
/*        double[] genes = ( (MOBinaryIndividual) pop.getMember(j)).toDouble();

        // the max and precision are contained in the boundsAndPrecision table
        DecisionVariables dv = population.getParameters().decisionVariables;

        int curPos = 0;
*/
        for (int k = 0; k < numTraits; k++) {
/*          int numBits = traits[k].getNumBits();
          double num = 0.0d;
          double max = dv.getVariableMax(k);
          double min = dv.getVariableMin(k);
          double precision = dv.getVariablePrecision(k);

          double interval = (max - min) * precision;

          // this is one trait
          for (int l = 0; l < numBits; l++) {
            if (genes[curPos] != 0) {
              num = num + Math.pow(2.0, l);
            }
            curPos++;
          }

          // if it is above the max, scale it down
          num = num * precision + min;
          if (num > max) {
            num = max;
          }
          if (num < min) {
            num = min;
          }*/
          populationTable.setDouble(values[k], j, k);
        }
      }
    }
  }

  private static final String SPACE = " ";

  /*
   * This writes the genes of individuals to a file.  The format is as follows:
   *  the first line contains the size of the population, the second line
   *  contains the length of each gene, and all following lines contain
   *  the actual genes, converted to a real value.
   * @param fileName the name of the file to copy to
   */
  private void writeBinaryGenesToFile(String fileName) throws IOException {
    Population popul = (Population) population;

    FileWriter stringFileWriter = new FileWriter(fileName);
    BufferedWriter bw = new BufferedWriter(stringFileWriter);
    PrintWriter pw = new PrintWriter(bw);

    Range[] traits = popul.getTraits();
    int numTraits = traits.length;
    int size = popul.size();

    // write population size in file
    pw.println(size);

    // write length of each gene/chromosome
    pw.println(numTraits);

    // write genes
    for (int j = 0; j < size; j++) {
      double[] values = popul.getMember(j).toDoubleValues();
      /*double[] genes = ( (BinaryIndividual) popul.getMember(j)).toDouble();

      // the max and precision are contained in the DecisionVariables
      DecisionVariables dv = population.getParameters().decisionVariables;

      int curPos = 0;
      for (int k = 0; k < traits.length; k++) {
        int numBits = traits[k].getNumBits();
        double num = 0.0d;
        double max = dv.getVariableMax(k);
        double min = dv.getVariableMin(k);
        double precision = dv.getVariablePrecision(k);

        double interval = (max - min) * precision;

        // this is one trait
        for (int l = 0; l < numBits; l++) {
          if (genes[curPos] != 0) {
            num = num + Math.pow(2.0, l);
          }
          curPos++;
        }

        // if it is above the max, scale it down
        num = num * precision + min;
        if (num > max) {
          num = max;
        }
        if (num < min) {
          num = min;
        }
        //pw.print(num + SPACE);
        pw.print(num);
        pw.print(SPACE);
      }*/
      for(int k = 0; k < numTraits; k++) {
        pw.print(values[k]);
        pw.print(SPACE);
      }

      pw.println();
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

  /*
   * This writes the genes of individuals to input files for different
       * fitness function executables, that might be present in different directories
   *
   * Right now this only works for MO populations!!!
   */
/*  private void writeNumericGenesToFile(String fileName) throws
      IOException {
    Population popul = (Population) population;

    FileWriter stringFileWriter = new FileWriter(fileName);
    BufferedWriter bw = new BufferedWriter(stringFileWriter);
    PrintWriter pw = new PrintWriter(bw);

    //Individual ni = (Individual) popul.getMember(0);
    //double[] genes = (double[]) ni.getGenes();
    int numTraits = popul.getTraits().length;

    // write population size in file
    pw.println(popul.size());
    // write length of each gene/chromosome
    pw.println(numTraits);
    // write genes
    for (int j = 0; j < popul.size(); j++) {
      //   genes = popul.getMember(j).getGenes().toString();

      ////////////////////////////////////////////////////////////
      // !!! LAM
      // if an IntInvidual is used, an int[] will be returned...
      // right now IntIndividuals are not used so thia is currently
      // not a problem
      double[] genes = (double[]) ( (Individual) popul.getMember(j)).getGenes();
      numTraits = genes.length;

      // write to file
      for (int k = 0; k < numTraits; k++) {
        //pw.print(genes[k] + SPACE);
        pw.print(genes[k]);
        pw.print(SPACE);
      }
      pw.println();
    }
    pw.println();
    // close file and streams
    pw.flush();
    bw.flush();
    stringFileWriter.flush();
    pw.close();
    bw.close();
    stringFileWriter.close();
  }*/

  private double[] readOutput(String fileName) throws IOException {
    double[] fit = new double[ ( (Population) population).size()];

    FileReader stringFileReader = new FileReader(fileName);
    BufferedReader br = new BufferedReader(stringFileReader);

    int i = 0;
    String s = br.readLine();
    while (s != null) {
      fit[i] = Double.parseDouble(s);
      i++;
      s = br.readLine();
    }

    // close file and streams
    stringFileReader.close();
    br.close();

    return fit;
  }

  /**
   * Read the output from an external executable and set the objective
   * value on the individual
   * @param ffIndex the index of the ff
   * @throws Exception
   */
  private void readExternalFFOutput(int ffIndex) throws IOException {
    Population popul = (Population) population;
    double[] fit = readOutput(fitnessFunctions.getExternFitnessFunctionOutput(
        ffIndex));

    int size = popul.size();

    if (popul instanceof SOPopulation) {
      // now that we have the fitness values, set the objective on the individual
      for (int i = 0; i < size; i++) {
        ( (SOSolution) popul.getMember(i)).setObjective(fit[i]);
      }
    }
    else {
      // now that we have the fitness values, set the objective on the individual
      for (int i = 0; i < size; i++) {
        ( (MOSolution) popul.getMember(i)).setObjective(
            numFitnessFunctions + ffIndex, fit[i]);
      }
    }
  }

  /**
   * Read the output from an external executable and set the objective
   * value on the individual
   * @param ffIndex the index of the ff
   * @throws Exception
   */
  private void readExternalConstraintOutput(int conIndex) throws IOException {
    Population popul = (Population) population;
    double[] fit = readOutput(fitnessFunctions.getExternFitnessFunctionOutput(
        conIndex));

    int size = popul.size();
    double weight = constraints.getExternConstraintWeight(conIndex);
    for(int i = 0; i < size; i++) {
      Solution s = (Solution)popul.getMember(i);
      s.setConstraint(fit[i]*weight, numConstraintFunctions+conIndex);
    }
  }

  /**
   * Make a copy of a file.
   * @param src
   * @param dest
   * @throws IOException
   */
  private static void copyFile(File src, File dest) throws
      IOException {
    if (src.equals(dest)) {
      return;
    }

    BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
        dest));

    try {
      byte[] b = new byte[2048];
      int read = 0;

      while ( (read = in.read(b)) != -1) {
        out.write(b, 0, read);
      }

      in.close();
      out.close();
    }
    catch (IOException ioe) {
      in.close();
      out.close();
      throw ioe;
    }
  }
}