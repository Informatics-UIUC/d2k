package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.*;

/**
 */
public abstract class Crossover {

  /**
   * Perform a crossover on the population.  The Population will always be
   * an EMOPopulation, and the properties for the crossover techinque
   * should be read from the EMOPopulationParams for the EMOPopulation.
   * @param p the population, will always be an EMOPopulation
   */
  public abstract void performCrossover(Population p);

  private double crossoverRate;
  public void setCrossoverRate(double cr) {
    crossoverRate = cr;
  }
  public double getCrossoverRate() {
    return crossoverRate;
  }

  private double generationGap;
  public void setGenerationGap(double gg) {
    generationGap = gg;
  }
  public double getGenerationGap() {
    return generationGap;
  }
}