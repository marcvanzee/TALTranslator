package parser.visitor.validation.observations;

import operations.StringOperations;
import io.IO;
import parser.syntaxtree.args;
import parser.syntaxtree.predicate;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.validation.ValErrorMsgs;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.ValVisitor;
import tal.TALConstants;

/**
 * Observations Visitor
 * ------------------------
 * This visitor will check whether the given formula conforms to the general restrictions
 * that are put on an observation formula
 * 
 * Constraints:
 *- is an atemporal first-order formula uniformly grounded in one temporal term T
 *- "Occurs" cannot occur in the observations
 *  
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 * 
 */
public class ObsVisitor extends ValVisitor  
{
	Integer timePoint;
	
	public ObsVisitor()
	{
		super(TALConstants.OBSERVATION);
		visitor = VisitorConstants.OBSERVATION;
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
		
		// if we have an error we do not ignore the rest because if we have Occurs
		// we want to print that error message to avoid confusion
		//if (vError.error) return ret;
		
		// obtain the name of the predicate
		String pred = IO.treeToStr(n.f0);
		
		// if we are not already in a predicate, it means that we are currently evaluating a predicate
		if (!inPredicate(p))
		{
			// the predicate "Occurs" is not allowed in observations
			if (StringOperations.equal(pred, TALConstants.OCCURS, false))
				vError.setError(ObsErrorMsgs.NO_OCCURS, ret.image);
		}
		
		return ret;
	}
	
	/**
	 * f0 -> arg()
	 * f1 -> ( <COMMA> arg() )*
	 */
	   public VisitResult visit(args n, ValParam p) 
	   {
		   // first obtain the name of the predicate
		   String arg1 = IO.treeToStr(n.f0);
			
		   VisitResult ret = super.visit(n, p);
		   
		   // in the observations, all assertions should have a grounded temporal term
		   // but this counts only for predicates that are Holds, Occurs or fluents!
		   // The arguments of action predicates and functions have no such restrictions
		   if (!inPredicate(p) && inTempPredicate(p))
		   {
			   // check whether the first argument is grounded (an integer)
			   if (isNumeric(arg1))
			   {
				   // if so, it should either be the first one, or it should match the existing time point
				   int time = Integer.parseInt(arg1);
				   if (timePoint == null) timePoint = time;
				   else if (timePoint != time) vError.setError(ObsErrorMsgs.ONLY_ONE_TIMPOINT, ret.image);
			   } else
			   {
				   // otherwise, we have an error
				   vError.setError(ObsErrorMsgs.ALL_TEMP_GROUNDED, ret.image);
			   }
			   
		   }
		   
		   return ret;
	   }
	   
	   
	///////////////////////////////////////////////////////////////
	/// Helper methods
	///////////////////////////////////////////////////////////////	
	
	   /**
	    * Reset the time point that can occur in an observation assertion, because different assertions
	    * (basically different "and" formulas) are allowed to have different time points
	    */
	   public void reset() { timePoint = null; }
}