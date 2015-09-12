package parser.visitor.validation.constraints.c5_deterministic_actions;

import io.IO;

import java.util.Enumeration;

import operations.EnumOperations;
import parser.syntaxtree.Node;
import parser.syntaxtree.NodeListOptional;
import parser.syntaxtree.implication;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.constraints.ConstrVisitor;
import tal.TALConstants;
import tal.VisitorManager;
import constraints.ConstraintConstants;

/**
 * Constraint #5: Deterministic actions
 * 
 * The only connective that can occur in the postcondition 
 * of action specification formulas is "and"
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class C5Visitor extends ConstrVisitor
{	
	public C5Visitor() { 
		super(); 
		constr = ConstraintConstants.ALL_ACTION_DETERMINISTIC;
		visitor = VisitorConstants.CONSTR_ALL_ACTION_DETERMINISTIC;
		narrativeType = TALConstants.ACTION_SPEC;
	}

	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
	/**
     * f0 -> disjunction()
     * f1 -> ( <IMPLIC> disjunction() )*
     */
	public VisitResult visit(implication n, ValParam p)
	{
		C5Param sp = ((C5Param)p);
		int curImplCount = sp.implCount;
		
		if (!n.f1.present() || curImplCount > 1) return super.visit(n, p);
		// curImplCount is 1 or 2
		
		sp.implCount++;
		String lhs="", pre="",post="";
		
		// visit the first argument 
		lhs = n.f0.accept(this, sp).image;
		if (curImplCount == 0 && EnumOperations.getEnumSize(n.f1.elements()) > 1) 
		{		
			pre = n.f1.elementAt(0).accept(this, sp).image;
			post = getElements(n.f1, sp, 1);
		} else 
			post = n.f1.accept(this, sp).image;

		if (!deterministic(skipToken(post))) setConstrError(post);
		
		VisitResult ret = new VisitResult();
		ret.rebuild(lhs + pre + post);	
		
		return ret;
   }
	
	private boolean deterministic( String formula) {
		VisitorManager v = new VisitorManager();
		int visitors[] = { VisitorConstants.SIMPLIFY, 
				VisitorConstants.REMOVE_IMPL, 
				VisitorConstants.NEG_REDUCTION };
		
		VisitResult vResult = v.visitTree(formula, visitors);
		return !vResult.image.contains(" or ");
	}
	
	private String getElements(NodeListOptional list, ValParam p, int startIndex) {
		String ret = "";
		
		if (list.elementAt(startIndex) == null) { 
			IO.print("Error: element " + startIndex + " out of bounds"); 
			return ""; 
		}
		
		Enumeration<Node> e = list.elements();
		while (e.hasMoreElements())
			ret += e.nextElement().accept(this, p).image;
		
		return ret;
	}
}
