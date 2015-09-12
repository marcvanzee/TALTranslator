package parser.visitor.validation.constraints.c6_action_dur_1;

import parser.syntaxtree.predicate;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.constraints.ConstrVisitor;
import tal.TALConstants;
import constraints.ConstraintConstants;

/**
 * Constraint #6: All actions have a duration of 1 in the action occurrences
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class C6Visitor extends ConstrVisitor 
{
	public C6Visitor() { 
		super(); 
		constr = ConstraintConstants.ALL_ACTIONS_DUR_1;
		visitor = VisitorConstants.CONSTR_ALL_ACTIONS_DUR_1;
		narrativeType = TALConstants.ACTION_OCC;
	}
	
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
	
	/**
	 * Predicate
	 * 
	 * f0 -> <IDENT>
	 * f1 -> [ argblock() ]
	 */
	public VisitResult visit(predicate n, ValParam p) 
	{
		// first carry out general constraints
		VisitResult ret = super.visit(n, p);
				
		// if we are not already in a predicate, it means that we are currently evaluating a predicate
		if (!inPredicate(p))
		{
			String arg1 = getArgPredicate(ret, 0);
			String arg2 = getArgPredicate(ret, 1);
				
			if (Integer.parseInt(arg2) - Integer.parseInt(arg1) != 1)
					setConstrError(ret.image);
		}
		
		return ret;
	}
}
