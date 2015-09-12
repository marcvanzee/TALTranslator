package parser.visitor.validation.observations;

public class ObsErrorMsgs 
{
	/**
	 * All temporal variables in the observations should be grounded
	 */
	public static final String ALL_TEMP_GROUNDED = "All temporal variables in the observations should be grounded";
	
	/**
	 * All time points between the minimal and maximal time point should have an observation assertion
	 */
	public static final String ONLY_ONE_TIMPOINT = "The formula can have only one grounded temporal term";
	
	/**
	 * Observations cannot contain the predicate "Occurs", an action occurrence should be put in the Action Occurrences
	 */
	public static final String NO_OCCURS = "Observations cannot contain the predicate \"Occurs\"," +
			" an action occurrence should be put in the Action Occurrences";
}
