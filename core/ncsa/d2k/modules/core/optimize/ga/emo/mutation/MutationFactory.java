package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

public class MutationFactory {

  public static final int MUTATION = 0;
  public static final int REAL_MUTATION = 1;

  public static final String[] TYPES =
      {"Mutation",
      "Real Mutation"};


  public static final Mutation createMutation(int type) {
    switch(type) {
      case(MUTATION):
        return new StandardMutationWrapper();
      case(REAL_MUTATION):
        return new RealMutationWrapper();
      default:
        return new StandardMutationWrapper();
    }
  }
}