package parser.visitor.validation;

import parser.visitor.VParam;

public class ValParam extends VParam 
{
	public boolean inPredicate = false;
	public boolean inTempPredicate = false;
	
	public ValParam() {}
	
	/**
	 * Construct a validation parameter
	 * 
	 * @param inPredicate Whether the current production is inside a predicate
	 */
	public ValParam(boolean inPredicate)
	{
		this.inPredicate = inPredicate;
	}
	
	/**
	 * Construct a validation parameter
	 * 
	 * @param inPredicate Whether the current production is inside a predicate
	 * @param inTempPredicate Whether the current production is inside a predicate 
	 * that requires a temporal terms as its first argument
	 */
	public ValParam(boolean inPredicate, boolean inTempPredicate)
	{
		this.inPredicate = inPredicate;
		this.inTempPredicate = inTempPredicate;
	}
	
	public void put(ValParam p)
	{
		this.inPredicate = p.inPredicate;
		this.inTempPredicate = p.inTempPredicate;
	}
}
