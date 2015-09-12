package parser.visitor;

import io.IO;

import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;

import parser.ParseException;
import parser.TALParser;
import parser.syntaxtree.Node;
import parser.syntaxtree.NodeList;
import parser.syntaxtree.NodeListInterface;
import parser.syntaxtree.NodeListOptional;
import parser.syntaxtree.NodeOptional;
import parser.syntaxtree.NodeSequence;
import parser.syntaxtree.NodeToken;
import parser.syntaxtree.arg;
import parser.syntaxtree.argblock;
import parser.syntaxtree.args;
import parser.syntaxtree.atom;
import parser.syntaxtree.conjunction;
import parser.syntaxtree.disjunction;
import parser.syntaxtree.fof;
import parser.syntaxtree.implication;
import parser.syntaxtree.literal;
import parser.syntaxtree.negation;
import parser.syntaxtree.one_line;
import parser.syntaxtree.predicate;
import parser.syntaxtree.quantification;
import parser.syntaxtree.terminal;
import parser.syntaxtree.unary;
import tal.TALConstants;

/**
 * The superclass GJTalVisitor builds a new tree in an object VisitorResult.
 * This tree can be accessed through visitorresult.tree and its image (String)
 * is stored in visitorresult.image. This tree can then be manipulated when
 * going through the original tree.
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */

public class TALVisitor<A> extends GJDepthFirst<VisitResult,A>
{
	// terminal tokens that we use when rewriting the formulas
	// they are all defined in tal.TALConstants and they should
	// be identical to the tokens defined in the parser file (TALParser_pre.jj)
	protected final String AND = TALConstants.AND;
	protected final String OR = TALConstants.OR;
	protected final String IMPL = TALConstants.IMPL;
	protected final String EQUIV = TALConstants.EQUIV;
	protected final String FORALL = TALConstants.FORALL;
	protected final String EXISTS = TALConstants.EXISTS;
	protected final String NOT = TALConstants.NOT;
	
	protected final String LPAR = TALConstants.LPAR;
	protected final String RPAR = TALConstants.RPAR;
	protected final String _ = TALConstants.SPACE;
	protected final String COLON = TALConstants.COLON;
	
	protected int visitor = VisitorConstants.BASIC;
		
	///////////////////////////////////////////////////////////////
	/// Auto-class Visitors
	///////////////////////////////////////////////////////////////
	
	/**
	 * does not occur in the current grammar
	 */
	public VisitResult visit(NodeList n, A p)  { return null; }
	
	/**
	 * occurs in: fof, implication, disjunction, conjunction, args
	 */
	public VisitResult visit(NodeListOptional n, A p) 
	{
		return (n.present() ? getListResult(n, p) : null);
	}
	
	/**
	 * occurs in: quantification
	 */
	 public VisitResult visit(NodeOptional n, A p) 
	 {
		 return n.present() ? 
					n.node.accept(this, p):
					null;
	 }
	
	 /**
	  * occurs in: fof, implication, disjunction, conjunction, args, atom (parentheses). 
	  */
	public VisitResult visit(NodeSequence n, A p) 
	{
		VisitResult v = getListResult(n, p);
		
		// if the formula is surrounded by parentheses and it was negated before it should remain negated
		// and we will treat it as a literal as well
		if (isParentheses(v))
		{
			v.isNegated = v.nodes.get(1).isNegated;
			v.isLiteral = true;
		}
		
		return v;
	}
	
	/**
	 * occurs in: fof, implication, disjunction, conjunction, quantification, 
	 *            negation, atom, predicate, args, arg, var
	 */
	public VisitResult visit(NodeToken n, A p) 
	{
		VisitResult ret = getResulter();
		
		String image = n.tokenImage;
		
		// add spaces: one for binary operators and two for unary operators, e.g.
		// "and" --> " and "
		// "not" --> "not "
		HashMap<String, Boolean> connectives = TALConstants.getConnectives();
		
		if (connectives.keySet().contains(n.tokenImage))
			ret.image = (connectives.get(n.tokenImage) ? " " : "") + image + " ";
		else
			ret.image = image;
		ret.isLiteral = !TALConstants.getReservedTokens().contains(n.tokenImage);
		
		return ret;
	}
	
	///////////////////////////////////////////////////////////////
	/// Language-specific Visitors
	///////////////////////////////////////////////////////////////
 
	/**
	 * f0 -> fof()
	 * f1 -> <EOF>
	 */
	   public VisitResult visit(one_line n, A p) {
	      return n.f0.accept(this, p);
	   }

	   
	/**
     * f0 -> implication()
     * f1 -> ( <EQUIV> implication() )*
     */
	public VisitResult visit(fof n, A p)
	{
		return (n.f1.present() ?
				concatResults(false, p, n.f0, n.f1) :
				n.f0.accept(this, p));
	}

	/**
     * f0 -> disjunction()
     * f1 -> ( <IMPLIC> disjunction() )*
     */
	public VisitResult visit(implication n, A p)
   {
		return (n.f1.present() ?
				concatResults(false, p, n.f0, n.f1) :
				n.f0.accept(this, p));
   }

	/**
	* f0 -> conjunction()
	* f1 -> ( <OR> conjunction() )*
	*/
   public VisitResult visit(disjunction n, A p)
   {
	   return (n.f1.present() ?
				concatResults(false, p, n.f0, n.f1) :
				n.f0.accept(this, p));
   }

   /**
    * f0 -> unary()
    * f1 -> ( <AND> unary() )*
    */
   public VisitResult visit(conjunction n, A p) 
   {
	   return (n.f1.present() ?
				concatResults(false, p, n.f0, n.f1) :
				n.f0.accept(this, p));
   }

   /**
    * f0 -> literal()
    *       | quantification()
    */
   public VisitResult visit(unary n, A p) 
   {
	   return n.f0.accept(this, p);
   }

   /**
    * f0 -> ( <EXISTS> | <FORALL> )
    * f1 -> var()
    * f2 -> [ <COLON> ]
    * f3 -> unary()
    */
   public VisitResult visit(quantification n, A p) 
   {
	   return concatResults(false, p, n.f0, n.f1, n.f2, n.f3);
   }

   /**
    * f0 -> atom()
    *       | negation()
    */
   public VisitResult visit(literal n, A p) 
   {
	   return n.f0.accept(this, p);
   }

   /**
    * f0 -> <NOT>
    * f1 -> unary()
    */
   public VisitResult visit(negation n, A p) 
   {
	   VisitResult nodeRet = n.f1.accept(this, p);
	 
	   // concat but treat as a literal
	   VisitResult ret = concatResults(true, p, n.f0, n.f1);
	   
	   // flip the negation sign
	   ret.isNegated = !nodeRet.isNegated;
	  
	   return ret;  
   }

   /**
    * f0 -> terminal()
    *       | <LPAR> fof() <RPAR>
    *       | <LBRACK> fof() <RBRACK>
    */
   public VisitResult visit(atom n, A p) 
   {
	   return n.f0.accept(this, p);
   }

   /**
    * f0 -> var()
    *       | predicate()
    */
   public VisitResult visit(terminal n, A p) 
   {
	   VisitResult ret = n.f0.accept(this, p);
	   ret.isLiteral = true;
	   
	   return ret;
   }

   /**
    * f0 -> <IDENT>
    * f1 -> [ argblock() ]
    */
   public VisitResult visit(predicate n, A p) {
	   return concatResults(true, p, n.f0, n.f1);
   }

   /**
    * f0 -> <LPAR>
    * f1 -> [ args() ]
    * f2 -> <RPAR>
    */
   public VisitResult visit(argblock n, A p) {
	   return concatResults(false, p, n.f0, n.f1, n.f2);
   }

   /**
    * f0 -> arg()
    * f1 -> ( <COMMA> arg() )*
    */
   public VisitResult visit(args n, A p) 
   {
	   return concatResults(false, p, n.f0, n.f1);
   }

   /**
    * f0 -> predicate()
    *       | <IDENT>
    *       | <VAR>
    *       | <NUM>
    */
   public VisitResult visit(arg n, A p) 
   {	   
	   VisitResult ret = n.f0.accept(this, p);
	   ret.isLiteral = true;
	   return ret;
   }
   
   
   ///////////////////////////////////////////////////////////////
   /// Helper methods
   ///////////////////////////////////////////////////////////////
   	
   // the idea is simple: concatenate the images (Strings) of the consecutive
   // nodes in the list and then transform it to a tree
   private VisitResult getListResult(NodeListInterface n, A p) 
   {
	   boolean isLiteral = true;
	   boolean isNegated = false;

	   String image = "";
		
	   LinkedList<VisitResult> nodeList = new LinkedList<VisitResult>();
		
	   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
	   {
		   VisitResult node = e.nextElement().accept(this, p);
		    
		   nodeList.add( node );
		   image += node.image;
		   isLiteral = node.isLiteral;
	   }
	
	   // When a list contains part of a formula with a connective such as "a or b", it will always be
	   // only the last part (i.e. "or b"). This is because of the way that the language is built up
	   // in the grammar file. Therefore
	   // we have a literal if the list contains less than three elements and the last one is a literal
	   // e.g. "or p(0)", "or (p(0) and q(1))"
	   isLiteral = (nodeList.size() < 3 && isLiteral);
	   
	   VisitResult ret = getResulter();
	   
	   ret.rebuild(image, nodeList, isLiteral, isNegated);
	   
	   return ret;
	}
	
	/**
	 * 
	 * @param isLiteral
	 * @param nodes
	 * @return
	 */
	public VisitResult concatResults(boolean isLiteral, A p, Node... nodes)
	{
		String image = "";
		VisitResult nodeRet;
		VisitResult ret = getResulter();
		
		for (Node node : nodes)
		{
			nodeRet = node.accept(this, p);
			if (nodeRet != null)
			{
				image += nodeRet.image;
				ret.nodes.add(nodeRet);
			}
		}
		
		ret.rebuild(image, isLiteral, false);
		
		return ret;
	}

	protected String skipToken(String image, String token)
	{
		return image.replaceFirst(token, "");
	}
	
	/**
	 * Skip the first token (separated by one or more spaces from the second token)
	 * 
	 * @param image
	 * @return
	 */
	protected String skipToken(String image)
	{
		String str = image.trim();
		return str.substring(str.indexOf(" ")+1);
	}
	
	/**
	 * Obtain the first token of the image, separated with one or more spaces from the
	 * second token.
	 * 
	 * @param image
	 * @return
	 */
	protected String getToken(String image)
	{
		String str = image.trim();
		int index = str.indexOf(" ");
		return str.substring(0, (index != -1 ? index : str.length()));
	}
	
	private VisitResult getResulter()
	{
		VisitorFactory factory = new VisitorFactory();
		return factory.getVisitorResult(visitor);
	}
	
	public boolean isParentheses(VisitResult v) {
		if (v.nodes.size() == 3)
		{
			String lpar = v.nodes.get(0).image;
			String rpar = v.nodes.get(2).image;
			
			return (lpar.equals(TALConstants.LPAR) && rpar.equals(TALConstants.RPAR));
		}
		
		return false;
	}
	
	public VisitResult revisit(String img, VParam p)
	{
		VisitResult ret = null;
		try {
			   StringReader inputStream = new StringReader(img);
			   TALParser.ReInit(inputStream);
			
			   // TODO: perhaps this should be one_line
			   ret = TALParser.fof().accept(this.getClass().newInstance(), p);
			} catch (ParseException e) 
			{ 
				IO.gui("Error occurred while converting the string to parse tree");
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return ret;
	}
}
