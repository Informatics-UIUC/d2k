package ncsa.d2k.modules.core.optimize.ga.emo.selection;

import ncsa.d2k.modules.core.optimize.ga.*;

public interface Selection {

  public static final int RANK_SELECTION = 0;
  public static final int STOCHASTIC_UNIVERSAL_SAMPLING = 1;
  public static final int TOURNAMENT_WITHOUT_REPLACEMENT = 2;
  public static final int TOURNAMENT_WITH_REPLACEMENT = 3;
  public static final int TRUNCATION = 4;

  public static final String[] TYPES =
    {"Rank Selection",
    "Stochastic Universal Sampling",
    "Tournament Without Selection",
    "Tournament With Selection",
    "Truncation"};

  public static final String[] CLASSES =
      {"ncsa.d2k.modules.core.optimize.ga.emo.selection.EMORankSelection",
      "ncsa.d2k.modules.core.optimize.ga.emo.selection.EMOStochasticUniversalSampling",
      "ncsa.d2k.modules.core.optimize.ga.emo.selection.EMOTournamentWithoutReplacement",
      "ncsa.d2k.modules.core.optimize.ga.emo.selection.EMOTournamentWithReplacement",
      "ncsa.d2k.modules.core.optimize.ga.emo.selection.EMOTruncation"};

  public void performSelection(Population p);
}