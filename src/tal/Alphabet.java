package tal;

import java.util.HashMap;
import java.util.HashSet;

import operations.StringOperations;


/**
 * The alphabet used in a TAL narrative, together with several helper methods related to the alphabet
 * It is implemented in the same was a Narrative, so see there fore more information
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class Alphabet 
{
	private HashMap<String, Integer> alphabet = new HashMap<String, Integer>();
	private String alphabetStr = "";
	
	public boolean isFluent(String s)
	{
		return (alphabet.containsKey(s) && alphabet.get(s) == TALConstants.FLUENT);
	}
	
	public boolean isAction(String s)
	{
		return (alphabet.containsKey(s) && alphabet.get(s) == TALConstants.ACTION);
	}
	
	public boolean isConstant(String s)
	{
		return (alphabet.containsKey(s) && alphabet.get(s) == TALConstants.CONSTANT);
	}
	
	/**
	 * returns true when the argument to this method is a valid predicate, meaning
	 * that it is either a fluent, an action or the reserved predicate "Occurs".
	 * 
	 * @param pred
	 * @return
	 */
	public boolean isValidPredicate(String pred) 
	{
		// the predicate should occur in the alphabet but cannot be a constant
		// i.e. it is a fluent or an action
		boolean ret1 = isReservedPredicate(pred);
		boolean ret2 = alphabet.containsKey(pred) && (alphabet.get(pred) != TALConstants.CONSTANT);
		
		return ret1 || ret2;
	}
	
	/**
	 * returns true when the argument to this method is a valid predicate that requires
	 * a temporal term as its first argument, meaning that it is a fluent or a reserved
	 * predicate ("Holds" or "Occurs").
	 * 
	 * @param pred
	 * @return
	 */
	public boolean isTemporalPredicate(String pred)
	{
		boolean ret1 = isReservedPredicate(pred);
		boolean ret2 = alphabet.containsKey(pred) && (alphabet.get(pred) == TALConstants.FLUENT);
		
		return ret1 || ret2;
	}
	
	/**
	 * The argument is "Occurs" ("Holds and "Occludes" do not occurs in the narrative
	 * specification but only after reification of the narrative).
	 * 
	 * @param pred
	 * @return
	 */
	public boolean isReservedPredicate(String pred)
	{
		return StringOperations.equal(pred, TALConstants.OCCURS, false);
	}
	
	public boolean containsKey(String pred)
	{
		return alphabet.containsKey(pred);
	}
	
	public void put(String pred, int type)
	{
		alphabet.put(pred, type);
	}
	
	public void remove(String pred)
	{
		alphabet.remove(pred);
	}
	
	public HashSet<String> getAll(int type)
	{
		HashSet<String> ret = new HashSet<String>();
		
		for (String formula : alphabet.keySet())
			if (alphabet.get(formula) == type) ret.add(formula);
		
		return ret;
	}
	
	public HashMap<String, Integer> getMap()
	{
		return alphabet;
	}
	
	public void setStr(String str) {
		alphabetStr = str;
	}
	
	public void concatStr(String str) {
		alphabetStr += str;
	}
	
	public void putAll(HashMap<String, Integer> a) {
		alphabet.putAll(a);
	}
	
	public String getStr() {
		return alphabetStr;
	}
}
