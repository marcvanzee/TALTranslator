package parser.visitor.validation;

import io.IO;
import operations.StringOperations;
import parser.syntaxtree.NodeToken;
import parser.syntaxtree.arg;
import parser.syntaxtree.argblock;
import parser.syntaxtree.atom;
import parser.syntaxtree.predicate;
import parser.visitor.TALVisitor;
import parser.visitor.VisitResult;
import parser.visitor.VisitorConstants;
import parser.visitor.VisitorFactory;
import tal.Alphabet;
import tal.TALConstants;

/**
 * Validation Visitor
 * ------------------------
 * This visitor will check whether the given formula conforms to the basic TAL
 * restrictions. They are always enabled and do not depend on the narrative type.
 * 
 * General Restrictions
 *X- predicates have at least one argument (namely, the temporal one)
 *X- predicates are either actions, fluents or "Occurs"
 *X- "Occurs(T1,T2,A)" requires three arguments, where A should be an action
 * - arguments to predicates are either actions, variables, integers or constants
 *X- only the action specifications can contain change context annotation
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 */
public class ValVisitor extends TALVisitor<ValParam>  
{
	protected Alphabet alphabet = new Alphabet();
	protected ValError vError;
	protected int narrativeType;
	
	public ValVisitor()
	{
		vError = new ValError();
		visitor = VisitorConstants.VALIDATION;
	}
	
	public ValVisitor(int narrativeType)
	{
		this();
		this.narrativeType = narrativeType;
	}
	
	public void setAlphabet(Alphabet alphabet) 
	{		
		this.alphabet = alphabet;
	}
	
	///////////////////////////////////////////////////////////////
	/// Auto-class Visitors
	///////////////////////////////////////////////////////////////
	
	
	public VisitResult visit(NodeToken n, ValParam p) 
	{
		return super.visit(n, p);
	}
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
	
	
	/**
	 * f0 -> terminal()
	 *       | <LPAR> fof() <RPAR>
	 *       | <LBRACK> fof() <RBRACK>
	 */
	public VisitResult visit(atom n, ValParam p) {
		VisitResult ret = super.visit(n, p);
		
		if (ret.nodes.get(0).image.equals(TALConstants.LBRACK) && (this.narrativeType != TALConstants.ACTION_SPEC))
			vError.setError(ValErrorMsgs.NO_ANNOTATION, ret.image);
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
		// first obtain the name of the predicate
		String pred = IO.treeToStr(n.f0);
		
		// make a copy so that the original value doesn't change
		ValParam param = getParam();
		
		param.put(p);
		
		// if we are not inside a predicate (meaning that we are one), then we should
		// make sure that the temporal term is grounded if this is a fluent or a reserved 
		// predicate ("Holds" or "Occurs")
		if (!inPredicate(param) && alphabet.isTemporalPredicate(pred))
		{
			// don't enable inPredicate yet so we can identify this when evaluating
			// the first set of arguments: suppose we have p(1,q(2)). Now inPredicate
			// is false when evaluating 1, so we can verify the temporal term, but not for 2,
			// which also never need to be a temporal term
			param.inTempPredicate = true;
		}
		
		VisitResult ret = super.visit(n, param);
		
		// if we have an error we can ignore the rest
		if (vError.error) return ret;
		
		// if we are not already in a predicate, it means that we are currently evaluating a predicate
		if (!inPredicate(param))
		{
			if (!n.f1.present())
				vError.setError(ValErrorMsgs.NO_ARGS, ret.image);

			else 
			{
				if (!alphabet.isValidPredicate(pred))
					vError.setError(ValErrorMsgs.PRED_CONSTRAINT, ret.image);
			
				// if we have a predicate "Occurs" then there need to be 3 arguments
				else if (StringOperations.equal(pred, TALConstants.OCCURS, false))
				{
					if (getNumArgs(ret) != 3)
						vError.setError(ValErrorMsgs.ARGS_HOLDS_OCCURS, ret.image);
					else {
						// the third argument should be an action
						String action = getArgPredicate(ret, 2);

						if (!alphabet.isAction(action))
							vError.setError(ValErrorMsgs.OCCURS_FIRST_ARG_ACTION, ret.image);
					}
				}
			}
		}
		
		return ret;
	}

	/**
	 * f0 -> <LPAR>
	 * f1 -> [ args() ]
	 * f2 -> <RPAR>
	 */
	public VisitResult visit(argblock n, ValParam p) 
	{
		VisitResult ret = super.visit(n, p);
		
		if (!n.f1.present())
			vError.setError(ValErrorMsgs.NO_ARGS);
		
		return ret;
	}
	
	   /**
	    * f0 -> predicate()
	    *       | <IDENT>
	    *       | <VAR>
	    *       | <NUM>
	    */
	   public VisitResult visit(arg n, ValParam p) 
	   {	   
		   // from now on, all predicates that follow should know that they are
		   // not predicates but functions
		   ValParam param = getParam();
		   param.put(p);
		   param.inPredicate = true;
		   
		   return super.visit(n, param);
	   }
	
	   
	///////////////////////////////////////////////////////////////
	/// Helper methods
	///////////////////////////////////////////////////////////////	
	
	   
	protected int getNumArgs(VisitResult ret)
	{
		// we know that we have at least one argument (else vError.error would be true
		// and we could not be here) so we can call the arguments list	
		VisitResult argblock = ret.nodes.get(1);
		
		// the second argument of the argument block is argblock = "(" [ args() ] ")"
		VisitResult args = argblock.nodes.get(1);
		
		// if we have less than two productions, we have one argument
		if (args.nodes.size() < 2) return 1;
		
		// otherwise we have to count the number of production in the second argument:
		// args = arg() (<COMMA> arg())* 
		return args.nodes.get(1).nodes.size() + 1;
	}
	
	protected String getArgPredicate(VisitResult result, int i)
	{
		// we know that we have at least one argument (else vError.error would be true
		// and we could not be here) so we can call the arguments list
		VisitResult argblock = result.nodes.get(1);
		
		// the second argument of the argument block is args: argblock = "(" [ args() ] ")"
		VisitResult args = argblock.nodes.get(1);
		
		String ret;
		
		if (i == 0)
		{
			// retrieve the first argument
			// args = arg() (<COMMA> arg())* 
			ret = args.nodes.get(0).image;
		} else
		{
			// we need to go one step deeper into the second argument
			VisitResult argList = args.nodes.get(1);
			
			// now we need to get the argument that we want -1, because we already skipped the first argument
			VisitResult arg = argList.nodes.get(i-1);
			
			// this is a production of: <COMMA> arg(), so we need the second argument
			ret = arg.nodes.get(1).image;
		}
		
		return getPredicate(ret);
	}
	
	private String getPredicate(String str)
	{
		int index = str.indexOf("(");
		return str.substring(0, (index > 0 ? index : str.length()));
	}
	
	protected boolean inPredicate(ValParam p)
	{
		return p != null && p.inPredicate;
	}
	
	protected boolean inTempPredicate(ValParam p)
	{
		return p != null && p.inTempPredicate;
	}
	
	protected boolean isNumeric(String s) {
		return s.matches("\\d+");
	}
	
	protected boolean isTempVariable(String s) {
		return s.matches("T\\d*");
	}
	
	public ValError getValError()
	{
		return vError;
	}
	
	private ValParam getParam()
	{
		VisitorFactory factory = new VisitorFactory();
		return (ValParam)factory.getVisitorParam(visitor);
	}
	
	public void reset() {}
}