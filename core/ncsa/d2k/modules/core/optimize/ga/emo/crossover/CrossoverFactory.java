package ncsa.d2k.modules.core.optimize.ga.emo.crossover;

public class CrossoverFactory {
  public static final int TWO_POINT_CROSSOVER = 0;
  public static final int UNIFORM_CROSSOVER = 1;
  public static final int SIMULATED_BINARY_CROSSOVER = 2;

  public static final String[] TYPES =
      {"Two Point Crossover",
      "Uniform Crossover",
      "Simulated Binary Crossover"};

  public static Crossover createCrossover(int type) {
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
}