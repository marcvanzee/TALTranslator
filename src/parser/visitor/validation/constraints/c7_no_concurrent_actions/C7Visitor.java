package parser.visitor.validation.constraints.c7_no_concurrent_actions;

import io.IO;

import java.util.HashSet;

import parser.syntaxtree.predicate;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.constraints.ConstrVisitor;
import tal.TALConstants;
import constraints.ConstraintConstants;

/**
 * Constraint #7: No concurrent actions
 * 
 * There are no action occurrence statements that share a time point
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class C7Visitor extends ConstrVisitor 
{
	public C7Visitor() { 
		super(); 
		constr = ConstraintConstants.NO_CONCURRENT_ACTIONS;
		visitor = VisitorConstants.CONSTR_NO_CONCURRENT_ACTIONS;
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
			String action = getArgPredicate(ret, 2);
			
			
			int t1 = Integer.parseInt(arg1);
			int t2 = Integer.parseInt(arg2);
			
			for (OccArgs args : getOccArgs(action)) {
				if ((t1 <= args.t1 && t2 >= args.t1) ||
					(t1 <= args.t2 && t2 >= args.t2) ||
					(t1 >= args.t1 && t2 <= args.t2))	{
					IO.print("t1 and t2: " + t1 + ", " + t2 );
					IO.print("p.t1 and p.t2: " + args.t1 + ", " + args.t2 );
					setConstrError(ret.image);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * TODO: this is rather dirty, but it works
	 * 
	 * @param exludeAction
	 * @return
	 */
	private HashSet<OccArgs> getOccArgs(String exludeAction) {
		HashSet<OccArgs> ret = new HashSet<OccArgs>();
		
		for (String str : narrative.get(TALConstants.ACTION_OCC)) {
			for (String s : str.split(" and ")) {
				int posComma  = s.indexOf(',');
				int posComma2 = s.indexOf(',', posComma+1);
				String t1 = s.substring("occurs(".length(), posComma).trim();
				String t2 = s.substring(posComma+1, posComma2).trim();
				String action = s.substring(posComma2+1, s.lastIndexOf(")")).trim();
				
				if (!action.equals(exludeAction))
					ret.add(new OccArgs(action, Integer.parseInt(t1), Integer.parseInt(t2)));
			}
		}
		
		return ret;
	}
	
	private class OccArgs {
		String action;
		int t1, t2;
		
		public OccArgs(String action, int t1, int t2) { this.action=action; this.t1=t1; this.t2=t2; }
	}
}
