package parser.visitor.validation.constraints.c3_complete_initial_state;

import io.IO;

import java.util.HashSet;

import parser.syntaxtree.predicate;
import parser.visitor.VisitResult;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.constraints.ConstrVisitor;
import tal.TALConstants;

/**
 * Constraint #1: Complete initial state
 * 
 * Every fluent should have a defined value at time point 0
 * TODO: Cannot be used yet! Only when the entire narrative is parsed
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class C3Visitor extends ConstrVisitor
{
	public HashSet<String> definedFluents = new HashSet<String>();

	public C3Visitor(int narrativeType) { super(narrativeType); }
	
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
		VisitResult ret = super.visit(n, p);
		
		if (narrativeType == TALConstants.OBSERVATION)
		{
			// obtain the name of the predicate
			String pred = IO.treeToStr(n.f0);
			
			// if we are not already in a predicate, it means that we are currently evaluating a predicate
			if (!inPredicate(p))
			{
				// if the first argument is 0, then we should save the fluent as a defined one
				if (getArgPredicate(ret, 0).equals("0"))
					definedFluents.add(pred);
			}
		}
		
		return ret;
	}
	
	public void postProcess(int narrativeType) {
		if (narrativeType == TALConstants.OBSERVATION)
		{
			for (String fluent : alphabet.getAll(TALConstants.FLUENT)) {
				if (!definedFluents.contains(fluent))
					vError.error = true; // no error msg for constraints
			}
		}
	}
}
