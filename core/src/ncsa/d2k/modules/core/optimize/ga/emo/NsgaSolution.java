package ncsa.d2k.modules.core.optimize.ga.emo;
import ncsa.d2k.modules.core.optimize.util.*;

/**
 * Supports the solution characteristics of the NSGA-II, in particual
 * supports multiple objective functions and, crowding and rank.
 */

public interface NsgaSolution extends MOSolution {
	/**
	 * returns the constraint value, representing a constraint violation.
	 * @returns the constraint value, representing degree constraint violation.
	 */
	public double getConstraint ();

	/**
	 * sets the constraint value.
	 * @param the new constraint value.
	 */
	public void setConstraint (double rank);

	/**
	 * returns the non-dominated front index, or rank.
	 * @param returns the non-dominated front index, or rank.
	 */
	public int getRank ();

	/**
	 * sets the non-dominated front index, or rank.
	 * @param the non-dominated front index, or rank.
	 */
	public void setRank (int rank);

	/**
	 * returns the crowding distance.
	 * @returns the crowding distance.
	 */
	public double getCrowdingDistance ();

	/**
	 * sets the crowding distance
	 * @param crowding the new crowding distance.
	 */
	public void setCrowdingDistance (double crowding);

}
