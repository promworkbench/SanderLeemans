package svn44systemprecision;

public interface DirectlyFollowsModelSemantics extends Cloneable {

	/**
	 * Executes (fires) a transition. For performance reasons, this method does
	 * not check whether the transition is actually enabled and does not return
	 * information on what changed.
	 * 
	 * @param transitionIndex
	 */
	public void executeTransition(int transitionIndex);

	/**
	 * 
	 * @return The transitions that have a probability > 0 to fire.
	 */
	public int[] getEnabledTransitions();

	/**
	 * @return a copy of the underlying array of tokens in the current state.
	 *         (marking).
	 */
	public int getState();

	/**
	 * Sets the state to the supplied state (marking). The state array is copied
	 * into the internal data structure.
	 * 
	 * @param state
	 */
	public void setState(int state);

	public int getInitialState();

	public boolean isFinalState();

	/**
	 * 
	 * @param transitionIndex
	 * @return
	 */
	public String getLabel(int transitionIndex);

	/**
	 * May create a shallow copy, except the state, which must be deep copied.
	 * 
	 * @return
	 */
	DirectlyFollowsModelSemantics clone();

}
