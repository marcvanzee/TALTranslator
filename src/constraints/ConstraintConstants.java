package constraints;

import io.IO;

import java.util.HashSet;

import tal.TALConstants;

public class ConstraintConstants {
	public static final int NO_EQUALITY 				= 0;
	public static final int NO_DEPENDENCY_CONSTRAINTS 	= 1;
	public static final int ONLY_RELATIONAL_FLUENTS 	= 2;
	public static final int COMPLETE_INITIAL_STATE 		= 3;
	public static final int ALL_FLUENTS_PERSISTENT 		= 4;
	public static final int ALL_ACTION_DETERMINISTIC 	= 5;
	public static final int ALL_ACTIONS_DUR_1 			= 6;
	public static final int NO_CONCURRENT_ACTIONS 		= 7;
	public static final int NO_DOMAIN_CONSTRAINTS 		= 8;
	
	public static String getConstraintDescription(int i)
	{
		switch (i){
		case 0: return "No equality";
		case 1: return "No dependency constraints";
		case 2: return "Only relational fluents";
		case 3: return "All fluents are defined at time 0";
		case 4: return "All fluents are pesistent";
		case 5: return "All actions are deterministic";
		case 6: return "All actions have a duration of 1 time point";
		case 7: return "No concurrent actions";
		case 8: return "No domain constraints";
		}
		
		IO.error("Error: unknown constraint description");
		
		return null;
	}
	
	/**
	 * These constraints are always enabled
	 */
	public static HashSet<Integer> getEnabledConstraints() {
		HashSet<Integer> ret = new HashSet<Integer>();
		
		ret.add(NO_EQUALITY);
		ret.add(NO_DEPENDENCY_CONSTRAINTS);
		ret.add(ONLY_RELATIONAL_FLUENTS);
		ret.add(ALL_FLUENTS_PERSISTENT);
		ret.add(NO_DOMAIN_CONSTRAINTS);
		
		return ret;
	}
	
	/**
	 * These constraints are always disabled
	 */
	public static HashSet<Integer> getDisabledConstraints() {
		HashSet<Integer> ret = new HashSet<Integer>();
		
		ret.add(COMPLETE_INITIAL_STATE);
		
		return ret;
	}
	
	/**
	 * These are the constraints that actually require a visitor to do something
	 * 
	 * @param visitorId the id of the visitor as defined in VisitorConstants
	 * @return
	 */
	public static boolean requiresVisit(int c) {
		return !(constraintNarrativeType(c) == -1);
	}
	
	/**
	 * This will return the narrative type for which this constraint is active.
	 * This means that the constraint only has to be validated when a given
	 * formula is of the returned narrative type. If this value is -1, it means
	 * that the constraint does not requird any narrative to be checked. The
	 * second return value can be caught using requiresVisit(c) as well.
	 * 
	 * @param c
	 * @return
	 */
	public static int constraintNarrativeType(int c) {
		switch (c) {
		case COMPLETE_INITIAL_STATE: return TALConstants.OBSERVATION;
		case ALL_ACTION_DETERMINISTIC: return TALConstants.ACTION_SPEC;
		case ALL_ACTIONS_DUR_1: 
		case NO_CONCURRENT_ACTIONS: return TALConstants.ACTION_OCC;
		}
		
		return -1;
	}
}
