package parser.visitor;

import io.IO;
import parser.visitor.transformation.implication_reduction.ImplVisitor;
import parser.visitor.transformation.negation_reduction.NegParam;
import parser.visitor.transformation.negation_reduction.NegResult;
import parser.visitor.transformation.negation_reduction.NegVisitor;
import parser.visitor.transformation.simplify.SimplifyVisitor;
import parser.visitor.validation.ValParam;
import parser.visitor.validation.ValVisitor;
import parser.visitor.validation.action_occ.OccVisitor;
import parser.visitor.validation.action_spec.SpecParam;
import parser.visitor.validation.action_spec.SpecVisitor;
import parser.visitor.validation.constraints.c5_deterministic_actions.C5Param;
import parser.visitor.validation.constraints.c5_deterministic_actions.C5Visitor;
import parser.visitor.validation.constraints.c6_action_dur_1.C6Visitor;
import parser.visitor.validation.constraints.c7_no_concurrent_actions.C7Visitor;
import parser.visitor.validation.observations.ObsVisitor;

public class VisitorFactory
{	
	public TALVisitor<? extends VParam> getVisitor(int visitor)
	{
		switch (visitor) {
		case VisitorConstants.BASIC: return new TALVisitor<VParam>();
		case VisitorConstants.SIMPLIFY: return new SimplifyVisitor();
		case VisitorConstants.REMOVE_IMPL: return new ImplVisitor();
		case VisitorConstants.NEG_REDUCTION: return new NegVisitor();
		
		case VisitorConstants.VALIDATION: 
		case VisitorConstants.DOM_CONSTR: 
		case VisitorConstants.PERSISTENCE: 
		case VisitorConstants.DEP_CONSTR: return new ValVisitor();
		
		case VisitorConstants.OBSERVATION: return new ObsVisitor();
		case VisitorConstants.ACTION_OCC: return new OccVisitor();
		case VisitorConstants.ACTION_SPEC: return new SpecVisitor();
		
		case VisitorConstants.CONSTR_ALL_ACTION_DETERMINISTIC: return new C5Visitor();
		case VisitorConstants.CONSTR_ALL_ACTIONS_DUR_1: return new C6Visitor();
		case VisitorConstants.CONSTR_NO_CONCURRENT_ACTIONS: return new C7Visitor();
		}
		
		//IO.print("Warning: could not find visitor " + VisitorConstants.getVisitorDescription(visitor) + ", using default one...");
		return new TALVisitor<VParam>();
	}
	
	public VisitResult getVisitorResult(int visitor)
	{
		switch (visitor) {
		case VisitorConstants.NEG_REDUCTION: return new NegResult();
		}
		//IO.print("Warning: could not find visitor result for visitor " + VisitorConstants.getVisitorDescription(visitor) + ", using default one...");
		return new VisitResult();
	}
	
	public VParam getVisitorParam(int visitor)
	{
		switch (visitor) {
		case VisitorConstants.BASIC: return new VParam();
		case VisitorConstants.NEG_REDUCTION: return new NegParam();
		case VisitorConstants.VALIDATION: return new ValParam();
		case VisitorConstants.ACTION_SPEC: return new SpecParam();
		case VisitorConstants.CONSTR_ALL_ACTION_DETERMINISTIC: return new C5Param();
		}
		
		//IO.print("Warning: could not find visitor parameter for visitor " + VisitorConstants.getVisitorDescription(visitor) + ", using default one...");
		return new ValParam();
	}
}
