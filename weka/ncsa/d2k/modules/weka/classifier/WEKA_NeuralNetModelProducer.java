
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package ncsa.d2k.modules.weka.classifier;

//==============
// Java Imports
//==============

import java.util.StringTokenizer;

//===============
// Other Imports
//===============

import ncsa.d2k.infrastructure.modules.*;
import ncsa.d2k.util.datatype.*;

import weka.classifiers.neural.*;
import weka.core.Instances;


public class WEKA_NeuralNetModelProducer extends ModelProducerModule {


  //
  /** The number of epochs to train through. */
  private int m_numEpochs;

  //
  /** A flag to tell the build classifier to automatically build a neural net.
   */
  private boolean m_autoBuild;

  //
  /** A flag to state that the gui for the network should be brought up.
      To allow interaction while training. */
  private boolean m_gui;

  //
  /** An int to say how big the validation set should be. */
  private int m_valSize;

  //
  /** The number to to use to quit on validation testing. */
  private int m_driftThreshold;

  //
  /** The number used to seed the random number generator. */
  private long m_randomSeed;

  //
  /** A flag to state that a nominal to binary filter should be used. */
  private boolean m_useNomToBin;

  //
  /** The string that defines the hidden layers */
  private String m_hiddenLayers;

  //
  /** This flag states that the user wants the input values normalized. */
  private boolean m_normalizeAttributes;

  //
  /** This flag states that the user wants the learning rate to decay. */
  private boolean m_decay;

  //
  /** This is the learning rate for the network. */
  private double m_learningRate;

  //
  /** This is the momentum for the network. */
  private double m_momentum;

  //
  /** This flag states that the user wants the network to restart if it
   * is found to be generating infinity or NaN for the error value. This
   * would restart the network with the current options except that the
   * learning rate would be smaller than before, (perhaps half of its current
   * value). This option will not be available if the gui is chosen (if the
   * gui is open the user can fix the network themselves, it is an
   * architectural minefield for the network to be reset with the gui open). */
  private boolean m_reset;

  //
  /** This flag states that the user wants the class to be normalized while
   * processing in the network is done. (the final answer will be in the
   * original range regardless). This option will only be used when the class
   * is numeric. */
  private boolean m_normalizeClass;

  //================
  // Constructor(s)
  //================

  public WEKA_NeuralNetModelProducer() {
    m_normalizeClass = true;
    m_normalizeAttributes = true;
    m_autoBuild = true;
    m_gui = true;
    m_useNomToBin = true;
    m_driftThreshold = 20;
    m_numEpochs = 500;
    m_valSize = 0;
    m_randomSeed = 0;
    m_hiddenLayers = "a";
    m_learningRate = .3;
    m_momentum = .2;
    m_reset = true;
    m_decay = true;
  }

  //==================
  // Public Functions
  //==================

  /**
   * Gets the current settings of NeuralNet.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String [] getOptions() {

    String [] options = new String [21];
    int current = 0;
    options[current++] = "-L"; options[current++] = "" + getLearningRate();
    options[current++] = "-M"; options[current++] = "" + getMomentum();
    options[current++] = "-N"; options[current++] = "" + getTrainingTime();
    options[current++] = "-V"; options[current++] = "" +getValidationSetSize();
    options[current++] = "-S"; options[current++] = "" + getRandomSeed();
    options[current++] = "-E"; options[current++] =""+getValidationThreshold();
    options[current++] = "-H"; options[current++] = getHiddenLayers();
    if (getGUI()) {
      options[current++] = "-G";
    }
    if (!getAutoBuild()) {
      options[current++] = "-A";
    }
    if (!getNominalToBinaryFilter()) {
      options[current++] = "-B";
    }
    if (!getNormalizeNumericClass()) {
      options[current++] = "-C";
    }
    if (!getNormalizeAttributes()) {
      options[current++] = "-I";
    }
    if (!getReset()) {
      options[current++] = "-R";
    }
    if (getDecay()) {
      options[current++] = "-D";
    }


    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }


  protected void doit() throws java.lang.Exception {
    try {
      WEKA_NeuralNetModel NNMod = new WEKA_NeuralNetModel();
      NNMod.setOptions(getOptions());
      pushOutput(NNMod, 0);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      System.out.println("ERROR: WEKA_NeuralNetModelProducer.doit()");
      throw ex;
    }
  }


  public String getInputInfo(int parm1) {
    return "";
  }

  public String getModuleInfo() {
    return "This neural network uses backpropagation to train.";
  }

  public String getOutputInfo(int parm1) {
    return "ncsa.d2k.infrastructure.modules.PredictionModelModule";
  }

  public String[] getInputTypes() {
    return null;
   }

  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.infrastructure.modules.PredictionModelModule"};
    return out;
  }

  /**
   * @param d True if the learning rate should decay.
   */
  public void setDecay(boolean d) {
    m_decay = d;
  }

  /**
   * @return the flag for having the learning rate decay.
   */
  public boolean getDecay() {
    return m_decay;
  }

  /**
   * This sets the network up to be able to reset itself with the current
   * settings and the learning rate at half of what it is currently. This
   * will only happen if the network creates NaN or infinite errors. Also this
   * will continue to happen until the network is trained properly. The
   * learning rate will also get set back to it's original value at the end of
   * this. This can only be set to true if the GUI is not brought up.
   * @param r True if the network should restart with it's current options
   * and set the learning rate to half what it currently is.
   */
  public void setReset(boolean r) {
    if (m_gui) {
      r = false;
    }
    m_reset = r;

  }

  /**
   * @return The flag for reseting the network.
   */
  public boolean getReset() {
    return m_reset;
  }

  /**
   * @param c True if the class should be normalized (the class will only ever
   * be normalized if it is numeric). (Normalization puts the range between
   * -1 - 1).
   */
  public void setNormalizeNumericClass(boolean c) {
    m_normalizeClass = c;
  }

  /**
   * @return The flag for normalizing a numeric class.
   */
  public boolean getNormalizeNumericClass() {
    return m_normalizeClass;
  }

  /**
   * @param a True if the attributes should be normalized (even nominal
   * attributes will get normalized here) (range goes between -1 - 1).
   */
  public void setNormalizeAttributes(boolean a) {
    m_normalizeAttributes = a;
  }

  /**
   * @return The flag for normalizing attributes.
   */
  public boolean getNormalizeAttributes() {
    return m_normalizeAttributes;
  }

  /**
   * @param f True if a nominalToBinary filter should be used on the
   * data.
   */
  public void setNominalToBinaryFilter(boolean f) {
    m_useNomToBin = f;
  }

  /**
   * @return The flag for nominal to binary filter use.
   */
  public boolean getNominalToBinaryFilter() {
    return m_useNomToBin;
  }

  /**
   * This seeds the random number generator, that is used when a random
   * number is needed for the network.
   * @param l The seed.
   */
  public void setRandomSeed(long l) {
    if (l >= 0) {
      m_randomSeed = l;
    }
  }

  /**
   * @return The seed for the random number generator.
   */
  public long getRandomSeed() {
    return m_randomSeed;
  }

  /**
   * This sets the threshold to use for when validation testing is being done.
   * It works by ending testing once the error on the validation set has
   * consecutively increased a certain number of times.
   * @param t The threshold to use for this.
   */
  public void setValidationThreshold(int t) {
    if (t > 0) {
      m_driftThreshold = t;
    }
  }

  /**
   * @return The threshold used for validation testing.
   */
  public int getValidationThreshold() {
    return m_driftThreshold;
  }

  /**
   * The learning rate can be set using this command.
   * NOTE That this is a static variable so it affect all networks that are
   * running.
   * Must be greater than 0 and no more than 1.
   * @param l The New learning rate.
   */
  public void setLearningRate(double l) {
    if (l > 0 && l <= 1) {
      m_learningRate = l;
    }
  }

  /**
   * @return The learning rate for the nodes.
   */
  public double getLearningRate() {
    return m_learningRate;
  }

  /**
   * The momentum can be set using this command.
   * THE same conditions apply to this as to the learning rate.
   * @param m The new Momentum.
   */
  public void setMomentum(double m) {
    if (m >= 0 && m <= 1) {
      m_momentum = m;

    }
  }

  /**
   * @return The momentum for the nodes.
   */
  public double getMomentum() {
    return m_momentum;
  }

  /**
   * This will set whether the network is automatically built
   * or if it is left up to the user. (there is nothing to stop a user
   * from altering an autobuilt network however).
   * @param a True if the network should be auto built.
   */
  public void setAutoBuild(boolean a) {
    if (!m_gui) {
      a = true;
    }
    m_autoBuild = a;
  }

  /**
   * @return The auto build state.
   */
  public boolean getAutoBuild() {
    return m_autoBuild;
  }


  /**
   * This will set what the hidden layers are made up of when auto build is
   * enabled. Note to have no hidden units, just put a single 0, Any more
   * 0's will indicate that the string is badly formed and make it unaccepted.
   * Negative numbers, and floats will do the same. There are also some
   * wildcards. These are 'a' = (number of attributes + number of classes) / 2,
   * 'i' = number of attributes, 'o' = number of classes, and 't' = number of
   * attributes + number of classes.
   * @param h A string with a comma seperated list of numbers. Each number is
   * the number of nodes to be on a hidden layer.
   */
  public void setHiddenLayers(String h) {
    String tmp = "";
    StringTokenizer tok = new StringTokenizer(h, ",");
    if (tok.countTokens() == 0) {
      return;
    }
    double dval;
    int val;
    String c;
    boolean first = true;
    while (tok.hasMoreTokens()) {
      c = tok.nextToken().trim();

      if (c.equals("a") || c.equals("i") || c.equals("o") ||
	       c.equals("t")) {
	tmp += c;
      }
      else {
	dval = Double.valueOf(c).doubleValue();
	val = (int)dval;

	if ((val == dval && (val != 0 || (tok.countTokens() == 0 && first)) &&
	     val >= 0)) {
	  tmp += val;
	}
	else {
	  return;
	}
      }

      first = false;
      if (tok.hasMoreTokens()) {
	tmp += ", ";
      }
    }
    m_hiddenLayers = tmp;
  }

  /**
   * @return A string representing the hidden layers, each number is the number
   * of nodes on a hidden layer.
   */
  public String getHiddenLayers() {
    return m_hiddenLayers;
  }

  /**
   * This will set whether A GUI is brought up to allow interaction by the user
   * with the neural network during training.
   * @param a True if gui should be created.
   */
  public void setGUI(boolean a) {
    m_gui = a;
    if (!a) {
      setAutoBuild(true);

    }
    else {
      setReset(false);
    }
  }

  /**
   * @return The true if should show gui.
   */
  public boolean getGUI() {
    return m_gui;
  }

  /**
   * This will set the size of the validation set.
   * @param a The size of the validation set, as a percentage of the whole.
   */
  public void setValidationSetSize(int a) {
    if (a < 0 || a > 99) {
      return;
    }
    m_valSize = a;
  }

  /**
   * @return The percentage size of the validation set.
   */
  public int getValidationSetSize() {
    return m_valSize;
  }




  /**
   * Set the number of training epochs to perform.
   * Must be greater than 0.
   * @param n The number of epochs to train through.
   */
  public void setTrainingTime(int n) {
    if (n > 0) {
      m_numEpochs = n;
    }
  }

  /**
   * @return The number of epochs to train through.
   */
  public int getTrainingTime() {
    return m_numEpochs;
  }

}