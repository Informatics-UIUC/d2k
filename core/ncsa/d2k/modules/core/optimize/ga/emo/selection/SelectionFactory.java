package ncsa.d2k.modules.core.optimize.ga.emo.selection;

public class SelectionFactory {

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

  public static Selection createSelection(int type) {
    switch(type) {
      case(RANK_SELECTION):
        return new RankSelectionWrapper();
      case(STOCHASTIC_UNIVERSAL_SAMPLING):
        return new StochasticUniversalSamplingWrapper();
      case(TOURNAMENT_WITHOUT_REPLACEMENT):
        return new TournamentWithoutReplacementWrapper();
      case(TOURNAMENT_WITH_REPLACEMENT):
        return new TournamentWithReplacementWrapper();
      case(TRUNCATION):
        return new TruncationWrapper();
      default:
        return new TruncationWrapper();
    }
  }
}