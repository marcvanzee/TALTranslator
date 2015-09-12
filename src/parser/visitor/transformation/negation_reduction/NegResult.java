package parser.visitor.transformation.negation_reduction;

import java.util.LinkedList;

import parser.visitor.VisitResult;

public class NegResult extends VisitResult 
{
	public boolean appliedNeg = false;
	
	public NegResult() 
	{
	}
	
	public NegResult(String image) 
	{
		rebuild(image);
	}

	public NegResult(String image, boolean appliedNeg)
	{
		super(image);
		this.appliedNeg = appliedNeg;
	}

	public NegResult(String image, LinkedList<VisitResult> nodeList,
			boolean isLiteral, boolean isNegated, boolean appliedNeg) {
		super(image, nodeList, isLiteral, isNegated);
		this.appliedNeg = appliedNeg;
	}
}
