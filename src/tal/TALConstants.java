package tal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * TODO: Add Exceptions
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class TALConstants 
{
	// Alphabet types
	public static final int FLUENT 			= 0;
	public static final int ACTION 			= 1;
	public static final int CONSTANT 		= 2;
	
	// Narrative types
	public static final int OBSERVATION 	= 3;
	public static final int ACTION_OCC 		= 4;
	public static final int ACTION_SPEC 	= 5;
	public static final int DOMAIN_CONSTR 	= 6;
	public static final int DEP_CONSTR 		= 7;
	public static final int PERSISTENCE 	= 8;
	
	// these constants are necessary for a narrative specification and for write/reading of files
	public static final int GENERAL_CONSTR 	= 9;
	public static final int ALPHABET		= 10;
	public static final int PROJECT			= 11;
	
	
	// symbols used in the parser, we need to re-use them sometimes when rewriting
	// for example: p -> q   can be rewritten to  (not) p or q
	public static final String EQUIV 		= "<->";
	public static final String IMPL 		= "->";
	public static final String OR 			= "or";
	public static final String AND 			= "and";
	public static final String FORALL 		= "forall";
	public static final String EXISTS 		= "exists";
	public static final String LPAR 		= "(";
	public static final String RPAR 		= ")";
	public static final String NOT 			= "not";
	public static final String SPACE 		= " ";
	
	public static final String COMMA 		= ",";
	public static final String COLON 		= ":";
	public static final String SEMICOLON 	= ";";
	public static final String PARENTHESES 	= "()";
	public static final String LBRACK 		= "[";
	public static final String RBRACK 		= "]";
	
	// reserved predicates
	public static final String HOLDS 		= "holds";
	public static final String OCCURS 		= "occurs";
	public static final String OCCLUDES 	= "occludes";
	
	
	
	public static HashMap<Integer, String> getNarrativeMap() 
	{
		HashMap<Integer, String> h = new HashMap<Integer, String>();
		
		h.put(TALConstants.FLUENT, "Fluents");
		h.put(TALConstants.ACTION, "Actions");
		h.put(TALConstants.CONSTANT, "Constants");
		h.put(TALConstants.OBSERVATION, "Observation");
		h.put(TALConstants.ACTION_OCC, "Action Occurrences");
		h.put(TALConstants.ACTION_SPEC, "Action Specifications");
		h.put(TALConstants.DOMAIN_CONSTR,"Domain Constraints");
		h.put(TALConstants.DEP_CONSTR, "Dependency Constraints");
		h.put(TALConstants.PERSISTENCE, "Persistence Statements");
		h.put(TALConstants.ALPHABET, "Alphabet");
		
		return h;
	}
	
	public static String getNarrativeDescription(int c) 
	{
		return getNarrativeMap().get(c);
	}
	
	public static int getNarrativeId(String d) {
		HashMap<Integer, String> map = getNarrativeMap();
		
		for (Entry<Integer, String> entry : map.entrySet()) {
			if (d.equals(entry.getValue()))
					return entry.getKey();
		}
		
		return -1;
	}
	
	public static Collection<String> getNarrativeDescriptions() {
		return getNarrativeMap().values();
	}
	
	/**
	 * Retrieve all the connectives that are accepted by the parser as
	 * defined in the parse file FOLParser_pre.jj in the package 
	 * parser.
	 * 
	 * @return a Hashmap of all the connectives with the connective
	 * as key and a boolean as value. This boolean is true when the
	 * connective is an infix operator (binary, e.g. "and"), otherwise it will
	 * be false (unary, e.g., "not").
	 */
	public static HashMap<String, Boolean> getConnectives() 
	{
		HashMap<String, Boolean> h = new HashMap<String, Boolean>();
		
		h.put(TALConstants.AND, true);
		h.put(TALConstants.OR, true);
		h.put(TALConstants.IMPL, true);
		h.put(TALConstants.EQUIV, true);
		h.put(TALConstants.FORALL, false);
		h.put(TALConstants.EXISTS, false);
		h.put(TALConstants.NOT, false);
		
		return h;
	}
	
	public static HashSet<String> getReservedTokens() 
	{
		HashSet<String> h = new HashSet<String>();
		
		h.addAll(getConnectives().keySet());
		h.add(TALConstants.LPAR);
		h.add(TALConstants.RPAR);
		
		return h;
	}
	
	public static boolean isProject(int type) {
		return type == TALConstants.PROJECT;
	}
	
	public static boolean isAlphabet(int type) {
		return type == TALConstants.ACTION || type == TALConstants.FLUENT || type == TALConstants.CONSTANT || type == TALConstants.ALPHABET;
	}
	
	public static boolean isNarrative(int type) {
		return type == TALConstants.OBSERVATION || type == TALConstants.ACTION_OCC || type == TALConstants.ACTION_SPEC ||
				type == TALConstants.DEP_CONSTR || type == TALConstants.DOMAIN_CONSTR || type == TALConstants.PERSISTENCE;
	}
	
}