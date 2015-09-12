package constraints;

import io.IO;

import java.util.HashMap;

public class ConstraintsManager {
	private HashMap<Integer, Boolean> constraints = new HashMap<Integer, Boolean>();
	
	private int constraintArr[] = { 
			ConstraintConstants.NO_EQUALITY, 
			ConstraintConstants.NO_DEPENDENCY_CONSTRAINTS, 
			ConstraintConstants.ONLY_RELATIONAL_FLUENTS, 
			ConstraintConstants.COMPLETE_INITIAL_STATE, 
			ConstraintConstants.ALL_FLUENTS_PERSISTENT, 
			ConstraintConstants.ALL_ACTION_DETERMINISTIC, 
			ConstraintConstants.ALL_ACTIONS_DUR_1, 
			ConstraintConstants.NO_CONCURRENT_ACTIONS 
			};
		
	/**
	 * Initialize the constraint manager and disable all constraints by default
	 * Except the ones that are always false (see ConstraintConstants)
	 */
	public ConstraintsManager() {
		for (int c : constraintArr)
			constraints.put(c, false);
		for (int c : ConstraintConstants.getEnabledConstraints())
			constraints.put(c, true);
	}
	
	/**
	 * Change the value of a constraint
	 * 
	 * @param c the integer representation of the constraint as defined in ConstraintParam
	 * @param v whether the constraint should be enabled or disabled
	 */
	public void put(int c, boolean v) {
		if (constraints.containsKey(c))
			constraints.put(c, v);
		else
			IO.error("Warning: trying to add a constraint that has a undefined description. Ignored");
	}
	
	public HashMap<Integer, Boolean> getMap() {
		return constraints;
	}
	
	public boolean isEnabled(int c) {
		return constraints.get(c);
	}

}
