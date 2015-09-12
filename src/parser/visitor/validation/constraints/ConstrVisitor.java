package parser.visitor.validation.constraints;

import parser.visitor.validation.ValVisitor;
import tal.Narrative;
import constraints.ConstraintConstants;

public class ConstrVisitor extends ValVisitor {
	protected int constr;
	protected Narrative narrative;
	
	public ConstrVisitor() { super(); }
	public ConstrVisitor(int narrativeType) { super(narrativeType); }
	
	protected void setConstrError(String formula) {
		vError.setError("Constraint \"" + ConstraintConstants.getConstraintDescription(constr) + "\" not met.", formula);
	}
	
	public void setNarrative(Narrative narrative) {
		this.narrative = narrative;
	}
}
