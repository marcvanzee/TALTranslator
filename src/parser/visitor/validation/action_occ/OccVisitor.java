package parser.visitor.validation.action_occ;

import io.IO;
import operations.StringOperations;
import parser.syntaxtree.conjunction;
import parser.syntaxtree.disjunction;
import parser.syntaxtree.fof;
import parser.syntaxtree.implication;
import parser.syntaxtree.negation;
import parser.syntaxtree.predicate;
import parser.syntaxtree.quantification;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.ValVisitor;
import tal.TALConstants;

/**
 * Action Occurrences Visitor
 * ------------------------
 * This visitor will check whether the given formula conforms to the general restrictions
 * that are put on an action occurrence formula
 * 
 * Constraints:
 *- contains only Occurs(T1,T2,A) predicates, where T1 and T2 are grounded and T1 < T2
 *- cannot contain quantifiers and connectives except "and"
 *  
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 * 
 */
public class OccVisitor extends ValVisitor  
{
	Integer timePoint;
	
	public OccVisitor()
	{
		super(TALConstants.ACTION_OCC);
		visitor = VisitorConstants.ACTION_OCC;
	}
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
	/**
     * f0 -> implication()
     * f1 -> ( <EQUIV> implication() )*
     */
	public VisitResult visit(fof n, ValParam p)
	{
		VisitResult ret = super.visit(n, p);
		if (n.f1.present()) vError.setError(OccErrorMsgs.NO_CONNECTIVES, ret.image);
		return ret;
	}

	/**
     * f0 -> disjunction()
     * f1 -> ( <IMPLIC> disjunction() )*
     */
	public VisitResult visit(implication n, ValParam p)
   {
		VisitResult ret = super.visit(n, p);
		if (n.f1.present()) vError.setError(OccErrorMsgs.NO_CONNECTIVES, ret.image);
		return ret;
   }

	/**
	* f0 -> conjunction()
	* f1 -> ( <OR> conjunction() )*
	*/
   public VisitResult visit(disjunction n, ValParam p)
   {
	   VisitResult ret = super.visit(n, p);
		if (n.f1.present()) vError.setError(OccErrorMsgs.NO_CONNECTIVES, ret.image);
		return ret;
   }

   /**
    * f0 -> unary()
    * f1 -> ( <AND> unary() )*
    */
   public VisitResult visit(conjunction n, ValParam p) 
   {
	   return super.visit(n, p);
   }

   /**
    * f0 -> ( <EXISTS> | <FORALL> )
    * f1 -> var()
    * f2 -> [ <COLON> ]
    * f3 -> unary()
    */
   public VisitResult visit(quantification n, ValParam p) 
   {
	   VisitResult ret = super.visit(n, p);
	   vError.setError(OccErrorMsgs.NO_QUANTIFIERS, ret.image);
	   return ret;
   }

   /**
    * f0 -> <NOT>
    * f1 -> unary()
    */
   public VisitResult visit(negation n, ValParam p) 
   {
	   VisitResult ret = super.visit(n, p);
	   vError.setError(OccErrorMsgs.NO_CONNECTIVES, ret.image);
	   return ret; 
   }
   
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
		
		// if we have an error we can ignore the rest
		if (vError.error) return ret;
		
		// obtain the name of the predicate
		String pred = IO.treeToStr(n.f0);
		
		// if we are not already in a predicate, it means that we are currently evaluating a predicate
		if (!inPredicate(p))
		{
			// the predicate "Occurs" is only allowed in action occurrences
			if (!StringOperations.equal(pred, TALConstants.OCCURS, false))
				vError.setError(OccErrorMsgs.ONLY_OCCURS, ret.image);
			else {
				String arg1 = getArgPredicate(ret, 0);
				String arg2 = getArgPredicate(ret, 1);
				
				if (!isNumeric(arg1) || !isNumeric(arg2))
					vError.setError(OccErrorMsgs.GROUNDED_TIMEPOINTS, ret.image);
				else if (Integer.parseInt(arg1) >= Integer.parseInt(arg2))
					vError.setError(OccErrorMsgs.ACTION_DUR_AT_LEAST_1, ret.image);
			}
		}
		
		return ret;
	}
}