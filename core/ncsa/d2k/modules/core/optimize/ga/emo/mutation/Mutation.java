package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.*;

public interface Mutation {

  public static final int MUTATION = 0;
  public static final int REAL_MUTATION = 1;

  public static final String[] TYPES =
      {"Mutation",
      "Real Mutation"};

  public static final String[] CLASSES =
      {"ncsa.d2k.modules.core.optimize.ga.emo.mutation.EMOMutation",
      "ncsa.d2k.modules.core.optimize.ga.emo.mutation.EMORealMutation"};

  public void mutatePopulation(Population p);
}