package parser.visitor.validation;

public class ValErrorMsgs
{
	/**
	 * All predicates should have at least one argument (a temporal one)
	 */
	public static final String NO_ARGS = "All predicates should have at least one argument (a temporal one)";
	
	/**
	 * Predicates can only be fluents, actions or "Occurs"
	 */
	public static final String PRED_CONSTRAINT = "Predicates can only be fluents, actions or \"Occurs\"";
	
	/**
	 * The predicate "Occurs" requires three arguments
	 */
	public static final String ARGS_HOLDS_OCCURS = "The predicate \"Occurs\" requires three arguments";
	
	/**
	 * The last argument of "Occurs" should be an action
	 */
	public static final String OCCURS_FIRST_ARG_ACTION = "The last argument of \"Occurs\" should be an action";

	/**
	 * Only action specifications can contain change context annotation "[" and "]"
	 */
	public static final String NO_ANNOTATION = "Only action specifications can contain change context annotation \"[\" and \"]\"";
}
