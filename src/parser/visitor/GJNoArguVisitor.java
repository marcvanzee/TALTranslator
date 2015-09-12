//
// Generated by JTB 1.3.2
//

package parser.visitor;
import parser.syntaxtree.*;
import java.util.*;

/**
 * All GJ visitors with no argument must implement this interface.
 */

public interface GJNoArguVisitor<R> {

   //
   // GJ Auto class visitors with no argument
   //

   public R visit(NodeList n);
   public R visit(NodeListOptional n);
   public R visit(NodeOptional n);
   public R visit(NodeSequence n);
   public R visit(NodeToken n);

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> fof()
    * f1 -> <EOF>
    */
   public R visit(one_line n);

   /**
    * f0 -> implication()
    * f1 -> ( <EQUIV> implication() )*
    */
   public R visit(fof n);

   /**
    * f0 -> disjunction()
    * f1 -> ( <IMPLIC> disjunction() )*
    */
   public R visit(implication n);

   /**
    * f0 -> conjunction()
    * f1 -> ( <OR> conjunction() )*
    */
   public R visit(disjunction n);

   /**
    * f0 -> unary()
    * f1 -> ( <AND> unary() )*
    */
   public R visit(conjunction n);

   /**
    * f0 -> literal()
    *       | quantification()
    */
   public R visit(unary n);

   /**
    * f0 -> ( <EXISTS> | <FORALL> )
    * f1 -> <VAR>
    * f2 -> <COLON>
    * f3 -> unary()
    */
   public R visit(quantification n);

   /**
    * f0 -> atom()
    *       | negation()
    */
   public R visit(literal n);

   /**
    * f0 -> <NOT>
    * f1 -> unary()
    */
   public R visit(negation n);

   /**
    * f0 -> terminal()
    *       | <LPAR> fof() <RPAR>
    *       | <LBRACK> fof() <RBRACK>
    */
   public R visit(atom n);

   /**
    * f0 -> <VAR>
    *       | predicate()
    */
   public R visit(terminal n);

   /**
    * f0 -> <IDENT>
    * f1 -> [ argblock() ]
    */
   public R visit(predicate n);

   /**
    * f0 -> <LPAR>
    * f1 -> [ args() ]
    * f2 -> <RPAR>
    */
   public R visit(argblock n);

   /**
    * f0 -> arg()
    * f1 -> ( <COMMA> arg() )*
    */
   public R visit(args n);

   /**
    * f0 -> predicate()
    *       | <IDENT>
    *       | <VAR>
    *       | <NUM>
    */
   public R visit(arg n);

}
