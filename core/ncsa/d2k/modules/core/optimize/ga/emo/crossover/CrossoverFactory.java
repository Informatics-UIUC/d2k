package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

import ncsa.d2k.modules.core.optimize.ga.crossover.*;

public class CrossoverFactory {
  private static final int TWO_POINT_CROSSOVER = 0;
  private static final int UNIFORM_CROSSOVER = 1;
  private static final int SIMULATED_BINARY_CROSSOVER = 2;

  /*public static final String[] TYPES =
      {"Two Point Crossover",
      "Uniform Crossover",
      "Simulated Binary Crossover"};
      */

  private static Crossover createCrossover(int type) {
    switch(type) {
      case(TWO_POINT_CROSSOVER):
        return new TwoPointCrossoverWrapper();
      case(UNIFORM_CROSSOVER):
        return new UniformCrossoverWrapper();
      case(SIMULATED_BINARY_CROSSOVER):
        return new SimulatedBinaryCrossoverWrapper();
      default:
        return new UniformCrossoverWrapper();
    }
  }

  public static final int getBinaryDefault() {
    return UNIFORM_CROSSOVER;
  }

  public static final int getRealDefault() {
    return SIMULATED_BINARY_CROSSOVER;
  }

  public static final int NUM_CROSSOVER = 3;

  public static Crossover[] createCrossoverOptions() {
    Crossover[] retVal = new Crossover[NUM_CROSSOVER];
    for(int i = 0; i < NUM_CROSSOVER; i++) {
      retVal[i] = createCrossover(i);
    }
    return retVal;
  }
}