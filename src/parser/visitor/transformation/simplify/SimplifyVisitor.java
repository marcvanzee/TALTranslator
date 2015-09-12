package parser.visitor.transformation.simplify;

import java.io.StringReader;

import io.IO;
import operations.StringOperations;
import parser.TALParser;
import parser.syntaxtree.Node;
import parser.syntaxtree.NodeSequence;
import parser.syntaxtree.negation;
import parser.syntaxtree.one_line;
import parser.visitor.TALVisitor;
import parser.visitor.VParam;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import tal.TALConstants;

/**
 * The SimplifyVisitor is an extension of the GJTALVisitor which removes 
 * all double negations and gets rid of unnecessary parentheses in a very 
 * basic way (double parentheses will be replaced by single ones and
 * outer parentheses are removed)
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */

public class SimplifyVisitor extends TALVisitor<VParam>
{
	public SimplifyVisitor()
	{
		visitor = VisitorConstants.SIMPLIFY;
	}
	
	///////////////////////////////////////////////////////////////
	/// Auto-class Visitors
	///////////////////////////////////////////////////////////////
		
	public VisitResult visit(NodeSequence n, VParam p) 
	{
		VisitResult ret = super.visit(n, p);
				
		// if the formula between parentheses is a literal we can ignore the parentheses
		if (isParentheses(ret) && ret.nodes.get(1).isLiteral)
			ret.rebuild(ret.nodes.get(1).image);
		
		return ret;
	}
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
   /**
    * f0 -> fof()
    * f1 -> <EOF>
    */
   public VisitResult visit(one_line n, VParam p) {
      VisitResult ret = super.visit(n, p);
      String formulaNoPar = StringOperations.stripParentheses(ret.image);
      
      return revisit(formulaNoPar, p);
   }
   
   /**
    * f0 -> <NOT>
    * f1 -> unary()
    */
   public VisitResult visit(negation n, VParam p) 
   {
	   // if f1 is preceded by an uneven number of negations, we can
	   // cancel all negations, since not not p <=> p
	   
	   VisitResult ret = (VisitResult) n.f1.accept(this, p);
	    
	   // flip the negation sign
	   ret.isNegated = !ret.isNegated;
	   
	   if (ret.isNegated)
	   {
		   // because <not> has the highest precedence, it will always range over a literal
		   // (or a formula surrounded by parentheses, but we treat this as a literal as well)
		   ret = concatResults(true, p, n.f0, n.f1);
		   ret.isNegated = true;
	   } else 
	   {
		   // the fact that it is no longer negated means that it actually was, so we need to
		   // remove the first not operator and we do not have to add anything
		   ret = new VisitResult(skipToken(ret.image, TALConstants.NOT));
	   }
	   
	   return ret;  
   }
}
