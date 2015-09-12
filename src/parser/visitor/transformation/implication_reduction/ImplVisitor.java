package parser.visitor.transformation.implication_reduction;

import parser.syntaxtree.Node;
import parser.syntaxtree.fof;
import parser.syntaxtree.implication;
import parser.visitor.TALVisitor;
import parser.visitor.VParam;
import parser.visitor.VisitResult;

/**
 * The ImplVisitor is an extension of the GJTALVisitor which removes
 * all (bi-)implications using the following production rules:
 * 
 * p->q 	<=>		-p|q
 * p<->q	<=>		(-p|q) & (-q|p)
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class ImplVisitor extends TALVisitor<VParam>
{
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
   /**
    * Turn p <-> q into -p|q & -q|p
    * 
    * f0 -> implication()
    * f1 -> ( <EQUIV> implication() )*
    */
   public VisitResult visit(fof n, VParam p) 
   {
	   return implReduce(n.f0, n.f1, n.f1.present(), p, EQUIV);
   }
   
   /**
    * Turn p -> q into -p|q
    * 
    * f0 -> disjunction()
    * f1 -> ( <IMPLIC> disjunction() )*
    */
   public VisitResult visit(implication n, VParam p) 
   {
	   return implReduce(n.f0, n.f1, n.f1.present(), p, IMPL);
   }
   
   private VisitResult implReduce(Node argf0, Node argf1, boolean f1present, VParam p, String connective)
   {
	   VisitResult f0 = (VisitResult) argf0.accept(this, p);
	   
	   if (f1present) 
	   {
		   VisitResult f1 = (VisitResult) argf1.accept(this, p);
	
		   String lhs = f0.image;
		   String rhs = skipToken(f1.image, connective); // skip the first token
		   
		
		   // revisit the right hand side because it can contain more implications
		   // for example if we have the formula: p(0) -> q(1) -> r(2), the result is:
		   // lhs: p(0)
		   // rhs: q(1) -> r(2)
		   //
		   // not revisiting the rhs results in the formula "not p(0) or (q(1) -> r(2))"
		   VisitResult rhsResult = revisit(rhs, p);
		 		
			rhs = (rhsResult != null) ? rhsResult.image : "";
			
			if (!f0.isLiteral) lhs = LPAR + lhs + RPAR;
			if (!rhsResult.isLiteral) rhs = LPAR + rhs + RPAR;
			 
			// rewrite the formula
			String formula = "";
			
			if (connective.equals(EQUIV))
				formula =
					   LPAR + NOT + _ + lhs + _ + OR + _ + rhs + RPAR +
					   _ + AND + _ + LPAR + NOT + _ + rhs + _ + OR + _ + lhs + RPAR;
			else
				formula = NOT + _ + lhs + _ + OR + _ + rhs;
			 
			return new VisitResult(formula);

	   } else {
		   return f0;
	   }
   }
}
