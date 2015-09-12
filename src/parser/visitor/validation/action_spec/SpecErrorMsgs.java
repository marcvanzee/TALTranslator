package parser.visitor.validation.action_spec;

public class SpecErrorMsgs
{
	/**
	 * The antecedent of an action specification should be the predicate "Occurs"
	 */
	public static final String ANTECEDENT_IS_OCCURS = "The antecedent of an action specification should be the predicate \"Occurs\"";
	
	/**
	 * The outer connective of an action specification formula should be an implication.
	 */
	public static final String OUTER_CONNECTIVE_IMPL = "The outer connective of an action specification formula should be an implication."; 
			
	/**
	 * The temporal terms in "Occurs" should be temporal variables (start with a T and followed by a number)
	 */
	public static final String OCCURS_TEMP_VARS = "The temporal terms in \"Occurs\" should be temporal variables " +
			"(start with a T and followed by a number)";
	
	/**
	 * Both the temporal variables in \"Occurs\" should occur at least once outside this predicate
	 */
	public static final String TEMP_VARS_OUTSIDE_OCCURS = "Both the temporal variables in \"Occurs\" should occur at least once outside " +
			"this predicate";
	
	/**
	 * The second temporal variable in \"Occurs\" cannot occur in the precondition of the action specification
	 */
	public static final String T2_IN_POST_CONDITION = "The second temporal variable in \"Occurs\" cannot occur in the precondition of the " +
			"action specification";
	
	/**
	 * Change context annotation can only occur in the post-condition of an action specification formula
	 */
	public static final String ANNOTATION_IN_POST = "Change context annotation can only occur in the post-condition of an action specification formula";
}
