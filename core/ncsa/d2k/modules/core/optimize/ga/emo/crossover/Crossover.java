package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * This interface defines an enumeration of the crossover techniques available
 * in EMO and a method that must be implemented by crossover modules in EMO.
 *
 * To add new crossover techniques, a module must implement this interface.  Then
 * add new fields that correspond to the new class.
 *
 * The need to be able to swap crossover modules in an itinerary arose while
 * working with EMO.  The user can select which crossover type to use in the
 * EMO gui.  D2K had no provision for swapping a module out at run-time in
 * reaction to a user input, so I created this method to change the crossover
 * method at will.  The EMOCrossover class creates an appropriate object that
 * implementsthe Crossover interface.  When EMOCrossover is executed, it simply
 * callsthe performCrossover() method of the Crossover object.  Since the Crossover
 * objects are still modules, they can still be put into an itinerary.
 */
public interface Crossover {

  public static final int TWO_POINT_CROSSOVER = 0;
  public static final int UNIFORM_CROSSOVER = 1;
  public static final int SIMULATED_BINARY_CROSSOVER = 2;

  public static final String[] TYPES =
      {"Two Point Crossover",
      "Uniform Crossover",
      "Simulated Binary Crossover"};

  public static final String[] CLASSES =
      {"ncsa.d2k.modules.core.optimize.ga.emo.crossover.EMOTwoPointCrossover",
      "ncsa.d2k.modules.core.optimize.ga.emo.crossover.EMOUniformCrossover",
      "ncsa.d2k.modules.core.optimize.ga.emo.crossover.EMOSimulatedBinaryCrossover"};

  /**
   * Perform a crossover on the population.  The Population will always be
   * an EMOPopulation, and the properties for the crossover techinque
   * should be read from the EMOPopulationParams for the EMOPopulation.
   * @param p the population, will always be an EMOPopulation
   */
  public void performCrossover(Population p);
}