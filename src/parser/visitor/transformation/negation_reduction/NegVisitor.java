package parser.visitor.transformation.negation_reduction;

import java.util.LinkedList;

import parser.syntaxtree.Node;
import parser.syntaxtree.NodeListOptional;
import parser.syntaxtree.NodeSequence;
import parser.syntaxtree.conjunction;
import parser.syntaxtree.disjunction;
import parser.syntaxtree.negation;
import parser.syntaxtree.quantification;
import parser.visitor.TALVisitor;
import parser.visitor.VParam;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;

/**
 * The NegReductionVisitor is an extension of the GJTALVisitor which assumes:
 * - all (bi-)implications have been rewritten to conjunctions/disjunctions
 * - all double negations have been removed
 * 
 * It outputs a formula in which all occurrences of negation signs immediately
 * precede the atomic formulas, using the following production rules:

 * not (X and ... and Z) 	<=> not X or ... or not Z		(de Morgan)
 * not (X or ... or Z)		<=> not X and ... and not Z		(de Morgan)
 * 
 * not forall X : psi 		<=> exists X : not psi
 * not exists X : psi 		<=> forall X : not psi
 * 
 * Given that the assumptions are met, this will result in a formula that is
 * in Negation Normal Form.
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class NegVisitor extends TALVisitor<NegParam>
{ 
	public NegVisitor()
	{
		this.visitor = VisitorConstants.NEG_REDUCTION;
	} 

	///////////////////////////////////////////////////////////////
	/// Auto-class Visitors
	///////////////////////////////////////////////////////////////

	/**
	* occurs in: fof, implication, disjunction, conjunction, args
	*/
	public NegResult visit(NodeListOptional n, NegParam p) 
	{
		if (!n.present()) return null;
		
		NegResult ret = (NegResult) super.visit(n,p);		
		ret.appliedNeg = negationApplied(ret.nodes);

		return ret;
	}

	/**
	* occurs in: fof, implication, disjunction, conjunction, args, atom (parentheses). 
	*/
	public NegResult visit(NodeSequence n, NegParam p) 
	{
		NegResult ret = (NegResult) super.visit(n,p);
		ret.appliedNeg = negationApplied(ret.nodes);
		
		return ret;
	}
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
	
   /**
	* f0 -> conjunction()
	* f1 -> ( <OR> conjunction() )*
	*/
  public NegResult visit(disjunction n, NegParam p)
  {
	  return negVisit(n.f0, n.f1, n.f1.present(), p);
  }

  /**
   * f0 -> unary()
   * f1 -> ( <AND> unary() )*
   */
  public NegResult visit(conjunction n, NegParam p) 
  {
	  return negVisit(n.f0, n.f1, n.f1.present(), p);
  }
  
  /**
   * f0 -> ( <EXISTS> | <FORALL> )
   * f1 -> var()
   * f2 -> <COLON>
   * f3 -> unary()
   */
  public NegResult visit(quantification n, NegParam p) 
  {
	  NegResult ret = (NegResult) super.visit(n, p);
	  
	  if (p != null && p.pushNegation)
	  {
		  LinkedList<VisitResult> nodes = ret.nodes;
		  String quantifier = nodes.get(0).image.trim();
		  String quantifierNew = ( quantifier.equals(FORALL) ? EXISTS : FORALL);
		  
		  String var = nodes.get(1).image;
		  NegResult unaryRet = (NegResult) nodes.get(nodes.size()-1);
		  
		  // if the negation could not be applied to <unary> we should add a negation
		  String unary = (unaryRet.appliedNeg ? "" : "not ") + unaryRet.image;
		  
		  String image = quantifierNew + _ + var + _ + COLON + _ + unary;
		  
		  ret = new NegResult(image, true);
	  }
	  
	  return ret;
  }
	
  /**
   * f0 -> <NOT>
   * f1 -> unary()
   */
  public NegResult visit(negation n, NegParam p) 
  {
	  // we try to push the not into f1. there are the following possibilities:
	  // not (...)				-> push through
	  // not atom				-> do nothing
	  // not forall/exists		-> push through
	  NegResult ret;
	  
	  // if we are asked to negate, cancel this negation and inform the parent
	  // that the negation is applied successfully
	  if (p != null && p.pushNegation)
	  {
		  NegParam param = new NegParam(false);
		  ret = (NegResult) n.f1.accept(this, param);
		  ret.appliedNeg = true;
		  
		  return ret;		  
	  }
	  
	  // else we are going to try to push this negation through
	  NegParam param = new NegParam(true);
	  	  
	  ret = (NegResult) n.f1.accept(this, param);
	  
	  // if the negation succeeded, we do not have to print this not operator, else we do
	  if (ret.appliedNeg)
	  {
		  return new NegResult(ret.image);
	  }
	  else 
	  {
		  ret = (NegResult) concatResults(true, p, n.f0, n.f1);
		  ret.isNegated = true;
		  
		  return ret;
	  }
  }
  
  
   ///////////////////////////////////////////////////////////////
   /// Helper methods
   ///////////////////////////////////////////////////////////////

  
	/**
	 * General method that works for both AND and OR connectives. 
	 * If we are receiving a negation change the connective and try to push the
	 * negation through. If that fails, add a negation to the clause.
	 * 
	 * @param argf0
	 * @param argf1
	 * @param f1present
	 * @param p
	 * @param connective
	 * @return
	 */
	private NegResult negVisit(Node argf0, Node argf1, boolean f1present, NegParam p)
	{
		NegResult f0 = (NegResult) argf0.accept(this, p);
		String formula = f0.image;
		boolean pushNegation = (p != null && p.pushNegation); // prevent null pointer exeception if p == null

		// if we receive a negation and we have not pushed it through we should add a NOT and we 
		// should surround it with parentheses if its not a literal because NOT has the highest precedence
		if (pushNegation && !f0.appliedNeg)
			formula = NOT + _ + (f0.isLiteral ? formula : LPAR + formula + RPAR);
		
		// we have to use f1present instead of f1.present() because the method present() does not exist
		// for the parent class Node that is extended by both conjunction and disjunction
		if (f1present)
		{
			VisitResult f1 = (VisitResult) argf1.accept(new TALVisitor<VParam>(), p);
			
			// obtain the connective, the new connective and skip it in the rhs
			String connective = getToken(f1.image);
			String newConnective = connective.equals(AND) ? OR : AND;
			String rhs = skipToken(f1.image);
						
			// revisit the right hand side, see method implReduce in 
			// parser.visitor.removeimpl.ImplVisitor for an explanation
			NegResult rhsResult = (NegResult) revisit(rhs, p);	
			
			rhs = (rhsResult != null) ? rhsResult.image : "";
						
			// similar for the right hand side as left hand side
			if (pushNegation && !rhsResult.appliedNeg)
				rhs = NOT + _ + (f1.isLiteral ? rhs : LPAR + rhs + RPAR);
			 
			// add rhs to the formula
			formula += _ + (pushNegation ? newConnective : connective) + _ + rhs;
		}
		
		return new NegResult(formula, pushNegation);
	}
	
	private boolean negationApplied(LinkedList<VisitResult> nodes)
	{
		for (VisitResult node : nodes)
			if (((NegResult) node).appliedNeg) return true;
		
		return false;
	}
}