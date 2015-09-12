package tal;

import io.IO;

import java.util.HashMap;
import java.util.LinkedList;

import parser.syntaxtree.Node;
import parser.visitor.VisitorConstants;
import parser.visitor.VisitorFactory;
import parser.visitor.validation.ValError;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.ValVisitor;
import parser.visitor.validation.constraints.ConstrVisitor;
import constraints.ConstraintConstants;

/**
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class Validator {
	LinkedList<Node> trees;
	Alphabet alphabet;
	Narrative narrative;
	int narrativeType;
	HashMap<Integer, Boolean> constraints;
	
	public Validator(LinkedList<Node> trees, Alphabet alphabet, Narrative narrative,
			int narrativeType, HashMap<Integer, Boolean> constraints) 
	{
		this.trees = trees;
		this.alphabet = alphabet;
		this.narrative = narrative;
		this.narrativeType = narrativeType;
		this.constraints = constraints;
	}
	
	public boolean parseNarrative()
	{
		int visitorId = VisitorConstants.getNarrativeVisitor(narrativeType);
		VisitorFactory factory = new VisitorFactory();
		
		// run the narrative specific visitor, which are all validation visitors
		ValVisitor valVisitor = (ValVisitor) factory.getVisitor(visitorId);
		ValParam valParam = (ValParam) factory.getVisitorParam(visitorId);
		valVisitor.setAlphabet(alphabet);
		
		IO.gui1("Checking " + TALConstants.getNarrativeDescription(narrativeType) + " constraints...");
		// validate all formulas
		if (!validateFormulas(valVisitor, valParam)) return false;
			
		IO.gui("succeeded!");		
		return true;
	}
	
	public boolean parseConstraints()
	{
		VisitorFactory factory = new VisitorFactory();
		IO.gui1("Checking additional constraints...");
		
		// go over all the constraints
		for (int c : constraints.keySet()) {
			if (constraints.get(c) && ConstraintConstants.requiresVisit(c)
					&& (ConstraintConstants.constraintNarrativeType(c) == narrativeType)) {
				int visitorId = VisitorConstants.getConstraintVisitor(c);
				ConstrVisitor constrVisitor = (ConstrVisitor) factory.getVisitor(visitorId);
				constrVisitor.setAlphabet(alphabet);
				constrVisitor.setNarrative(narrative);
				ValParam constrParam = (ValParam) factory.getVisitorParam(visitorId);
				
				if (!validateFormulas(constrVisitor, constrParam)) return false;
			}
		}
		
		IO.gui("succeeded!\n");		
		
		return true;
	}
	
	private boolean validateFormulas(ValVisitor valVisitor, ValParam valParam)
	{
		ValError vError;
		
		for (Node tree : trees) {
			IO.print("checking formula " + IO.treeToStr(tree));
			// some visitor (such as ObsVisitor) require to be reset between different formulas
			valVisitor.reset();
			
			tree.accept(valVisitor, valParam);
			vError = valVisitor.getValError();
			
			if (vError.error){
				printError(vError, IO.treeToStr(tree));
				return false;
			}
		}
		
		return true;
	}
	
	private void printError(ValError result, String tree)
	{
		IO.gui("failed!\n" + "Error message> " + result.getMessage());
		if (tree != null) IO.gui("in formula: " + tree);
		if (result.getFormula() != "") IO.gui("in subformula: " + result.getFormula() + "\n");
	}
}
