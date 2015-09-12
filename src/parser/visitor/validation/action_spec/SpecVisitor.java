package parser.visitor.validation.action_spec;

import io.IO;

import java.io.StringReader;
import operations.EnumOperations;
import operations.StringOperations;
import parser.TALParser;
import parser.syntaxtree.Node;
import parser.syntaxtree.NodeToken;
import parser.syntaxtree.atom;
import parser.syntaxtree.conjunction;
import parser.syntaxtree.disjunction;
import parser.syntaxtree.fof;
import parser.syntaxtree.implication;
import parser.syntaxtree.negation;
import parser.syntaxtree.one_line;
import parser.syntaxtree.predicate;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.ValVisitor;
import tal.TALConstants;

/**
 * Action Specifications Visitor
 * ------------------------
 * This visitor will check whether the given formula conforms to the general restrictions
 * that are put on an action specification formula
 * 
 * Constraints:
 *- Action specifications have the following form: 
 *  Occurs(T1,T2,A) -> (PRE -> POST)
 *- only POST contains change context annotation
 *- T1 can occur in PRE and POST, T2 only in POST
 *- every time point T' that occurs in PRE other than T1: T' < T1
 *- every time point T' that occurs in POST other than T2: T' < T2
 *  
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University

 */
public class SpecVisitor extends ValVisitor  
{
	// variables T1 and T2 in Occurs(T1,T2,A)
	String t1, t2;

	String lastConnective;
	
	public SpecVisitor()
	{
		super(TALConstants.ACTION_SPEC);
		visitor = VisitorConstants.ACTION_SPEC;
	}
	
	
	///////////////////////////////////////////////////////////////
	/// Auto-class Visitors
	///////////////////////////////////////////////////////////////
		
	/**
	 * TODO: only postconsition has change context annotation
	 */
	public VisitResult visit(NodeToken n, ValParam p) 
	{
		VisitResult ret = super.visit(n, p);
		String image = ret.image;

		if (image.equals(t2) && ((SpecParam)p).inPre) 
			vError.setError(SpecErrorMsgs.T2_IN_POST_CONDITION);
		
		return ret;
	}
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
	/**
	 * f0 -> fof()
	 * f1 -> <EOF>
	 */
	public VisitResult visit(one_line n, ValParam p) {
		VisitResult ret = n.f0.accept(this, p);
		
		// skip these error if we occurred one before
		if (vError.error) return ret;
		
		if (lastConnective != TALConstants.IMPL)
			vError.setError(SpecErrorMsgs.OUTER_CONNECTIVE_IMPL, ret.image);

		return ret;
	}
	   
	/**
     * f0 -> implication()
     * f1 -> ( <EQUIV> implication() )*
     */
	public VisitResult visit(fof n, ValParam p)
	{
		VisitResult ret = super.visit(n, p);
		if (n.f1.present()) lastConnective = TALConstants.EQUIV;
		return ret;
	}
	
	/**
     * f0 -> disjunction()
     * f1 -> ( <IMPLIC> disjunction() )*
     */
	public VisitResult visit(implication n, ValParam p)
	{
		SpecParam sp = ((SpecParam)p);
		int curImplCount = sp.implCount;
		
		if (!n.f1.present() || curImplCount > 1) return super.visit(n, p);
	
		sp.implCount++;
		String lhs="", rhs="";
		
		if (curImplCount == 0) {
			// visit the first argument and tell it that it is the occurs predicate
			// so we can collect the temporal variables
			sp.inOccurs = true;
			lhs = n.f0.accept(this, sp).image;
						
			// this is the first implication we encounter, so the left hand side
			// should be the predicate "Occurs", possibly surrounded by parentheses
			if (!isPredicate(lhs, "occurs"))
				vError.setError(SpecErrorMsgs.ANTECEDENT_IS_OCCURS, lhs);	
			
			// if there follow no more implications, we are already in the postcondition
			sp.inOccurs = false; sp.inPost = true;
			
			// in the case that we have a production with no bracket, i.e.
			// occurs -> pre -> post
			// then this method will not be called the second time, but n.f1 will simply
			// contain both implication. therefore we need to check for this here and if
			// there are two implication we need to treat them correctly.
			int size = EnumOperations.getEnumSize(n.f1.elements());
			
			if (size > 1) {
				// we need to treat the first one as pre and the second one as post
				sp.inPre=true; sp.inPost=false;
				String pre = n.f1.elementAt(0).accept(this, sp).image;
				
				sp.inPre=false; sp.inPost=true;
				String post = n.f1.elementAt(1).accept(this, sp).image;
				
				rhs = pre + post;
			} else 
				rhs = n.f1.accept(this, sp).image;
		} else if (curImplCount == 1) {
			// this is the second implication, meaning that we have pre and post in:
			// Occurs() -> (pre -> post)
			
			// T2 cannot occur in pre
			sp.inPre = true; sp.inPost = false;
			lhs = n.f0.accept(this, sp).image;
			
			// Only post can have context annotation
			sp.inPre = false; sp.inPost = true;
			rhs = n.f1.accept(this, sp).image;
		} 
		
		VisitResult ret = new VisitResult();
		ret.rebuild(lhs + rhs);	
		lastConnective = TALConstants.IMPL;
		
		return ret;
   }
	
	/**
	* f0 -> conjunction()
	* f1 -> ( <OR> conjunction() )*
	*/
   public VisitResult visit(disjunction n, ValParam p)
   {
	   VisitResult ret = super.visit(n, p);
	   if (n.f1.present()) lastConnective = TALConstants.OR;
	   return ret;
   }

   /**
    * f0 -> unary()
    * f1 -> ( <AND> unary() )*
    */
   public VisitResult visit(conjunction n, ValParam p) 
   {
	   VisitResult ret = super.visit(n, p);
	   if (n.f1.present()) lastConnective = TALConstants.AND;
	   return ret;
   }
   
   /**
    * f0 -> <NOT>
    * f1 -> unary()
    */
   public VisitResult visit(negation n, ValParam p) 
   {
	   VisitResult ret = super.visit(n, p);
	   lastConnective = TALConstants.NOT;
	   return ret; 
   }

   /**
    * f0 -> terminal()
    *       | <LPAR> fof() <RPAR>
    *       | <LBRACK> fof() <RBRACK>
    */
   public VisitResult visit(atom n, ValParam p) 
   {
	   // in each action specification Occurs(T1,T2,A) -> pre -> post, post is the only place
	   // where we can allow change context annotation
	   VisitResult ret = n.f0.accept(this, p);
	   if (ret.image.trim().startsWith("[") && !((SpecParam)p).inPost)
		   vError.setError(SpecErrorMsgs.ANNOTATION_IN_POST, ret.image);
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
		VisitResult ret = super.visit(n, p);
		
		if (vError.error) return ret;
		
		// if we are in occurs we collect the temporal parameters
		if (((SpecParam)p).inOccurs)
		{		
			String t1 = getArgPredicate(ret, 0);
			String t2 = getArgPredicate(ret, 1);
			
			if (isTempVariable(t1) && isTempVariable(t2)) {
				this.t1 = t1;
				this.t2 = t2;
			} else {
				vError.setError(SpecErrorMsgs.OCCURS_TEMP_VARS, ret.image);
			}
		}
			
		return ret;
	}
	   
	///////////////////////////////////////////////////////////////
	/// Helper methods
	///////////////////////////////////////////////////////////////	
	
	
	private boolean isPredicate(String formula, String pred)
	{
		formula = StringOperations.stripParentheses(formula);
		String parsedFormula = "";
		
		TALParser.ReInit(new StringReader(formula));
		try {
			Node ret = TALParser.predicate();
			parsedFormula = IO.treeToStr(ret);			
		} catch (Exception e) {
			return false;
		}
		
		// TODO: this is dirty like Christina Aguilera
		return (formula.toLowerCase().startsWith(pred.toLowerCase()) && parsedFormula.equals(formula));
	}
}