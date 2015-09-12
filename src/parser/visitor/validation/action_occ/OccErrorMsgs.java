package parser.visitor.validation.action_occ;

public class OccErrorMsgs 
{
	/**
	 * Action occurrences can only contain the predicate "Occurs"
	 */
	public static final String ONLY_OCCURS = "Action occurrences can only contain the predicate \"Occurs\"";
	
	/**
	 * The time points in the action occurrences should be grounded
	 */
	public static final String GROUNDED_TIMEPOINTS = "The time points in the action occurrences should be grounded temporal terms";
	
	/**
	 * An action should have a duration of at least 1
	 */
	public static final String ACTION_DUR_AT_LEAST_1 = "An action should have a duration of at least 1";
	
	/**
	 * Action occurrences cannot contain connectives except for "and"
	 */
	public static final String NO_CONNECTIVES = "Action occurrences cannot contain connectives except for \"and\"";
	
	/**
	 * Action occurrences cannot contain quantifiers
	 */
	public static final String NO_QUANTIFIERS = "Action occurrences cannot contain quantifiers";
	
}
