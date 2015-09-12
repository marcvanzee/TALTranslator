package parser.visitor.transformation.negation_reduction;

import parser.visitor.VParam;

public class NegParam extends VParam 
{
	public boolean pushNegation = false;
	
	public NegParam() {}
	
	public NegParam(boolean n)
	{
		this.pushNegation = n;
	}
}
