package parser.visitor;

import tal.TALConstants;
import constraints.ConstraintConstants;

public class VisitorConstants {
	public static final int BASIC 			= 0;
	public static final int SIMPLIFY 		= 1;
	public static final int REMOVE_IMPL 	= 2;
	public static final int NEG_REDUCTION 	= 3;
	
	public static final int VALIDATION 		= 4;
	public static final int OBSERVATION 	= 5;
	public static final int ACTION_OCC 		= 6;
	public static final int ACTION_SPEC 	= 7;
	public static final int DOM_CONSTR		= 8;
	public static final int PERSISTENCE		= 9;
	public static final int DEP_CONSTR		= 10;
	
	public static final int CONSTR_COMPLETE_INITIAL_STATE 		= 11;
	public static final int CONSTR_ALL_ACTION_DETERMINISTIC 	= 12;
	public static final int CONSTR_ALL_ACTIONS_DUR_1 			= 13;
	public static final int CONSTR_NO_CONCURRENT_ACTIONS 		= 14;
	
	public static String getVisitorDescription(int v) {
		switch (v) {
		case 0: return "Basic visitor";
		case 1: return "Simplify visitor";
		case 2: return "Remove (bi-)implications visitor";
		case 3: return "Negation reduction visitor";
		case 4: return "General validation visitor";
		case 5: return "Observation validation visitor";
		case 6: return "Action occurrence validation visitor";
		case 7: return "Action Specification visitor";
		case 8: return "Domain Constraints visitor";
		case 9: return "Persistence Statements visitor";
		case 10:return "Dependency Constraints visitor";
		case 11:return "Constraint 3: complete initial state visitor";
		case 12:return "Constraint 5: deterministic actions visitor";
		case 13:return "Constraint 6: all actions have duration 1 visitor";
		case 14:return "Constraint 7: no concurrent actions visitor";
		}
		//IO.print("Warning: could not find visitor description for number " + v);
		return "";
	}
	
	public static int getNarrativeVisitor(int narrativeType) {
		switch (narrativeType)
		{
		case TALConstants.OBSERVATION:  return  OBSERVATION;
		case TALConstants.ACTION_OCC:   return  ACTION_OCC;
		case TALConstants.ACTION_SPEC:  return  ACTION_SPEC;
		case TALConstants.DOMAIN_CONSTR:return  DOM_CONSTR;
		case TALConstants.PERSISTENCE:  return  PERSISTENCE;
		case TALConstants.DEP_CONSTR:   return  DEP_CONSTR;
		}
		//IO.print("Warning: could not find visitor for narrative " + TALConstants.getNarrativeDescription(narrativeType));
		return -1;
	}
	
	public static int getConstraintVisitor(int constraint) {
		switch (constraint)
		{
		case ConstraintConstants.ALL_ACTION_DETERMINISTIC: return VisitorConstants.CONSTR_ALL_ACTION_DETERMINISTIC;
		case ConstraintConstants.ALL_ACTIONS_DUR_1: return VisitorConstants.CONSTR_ALL_ACTIONS_DUR_1;
		case ConstraintConstants.NO_CONCURRENT_ACTIONS: return VisitorConstants.CONSTR_NO_CONCURRENT_ACTIONS;
		}
		//IO.print("Warning: could not find visitor for constraint " + ConstraintConstants.getConstraintDescription(constraint));
		return -1;
	}
}
