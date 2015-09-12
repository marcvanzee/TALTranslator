package tal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * A narrative consists of observations, action occurrences, etc... (called narrative types)
 * For each narrative type we maintain a string representation which is used in the GUI
 * and saved to files. This is implemented in a HashMap where the narrative types
 * are key, because we will have only one string per narrative type.
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class Narrative 
{
	private HashMap<String, Integer> narrative = new HashMap<String, Integer>();
	private HashMap<Integer, String> narrativeStr = new HashMap<Integer, String>();
	
	
	public Narrative() {
		// initialize all strings representations with the empty string
		narrativeStr.put(TALConstants.OBSERVATION, "");
		narrativeStr.put(TALConstants.ACTION_OCC, "");
		narrativeStr.put(TALConstants.ACTION_SPEC, "");
		narrativeStr.put(TALConstants.PERSISTENCE, "");
		narrativeStr.put(TALConstants.DOMAIN_CONSTR, "");
		narrativeStr.put(TALConstants.DEP_CONSTR, "");
	}
	
	/**
	 * Associates the specified value with the specified key in this map. 
	 * If the map previously contained a mapping for the key, the old value is replaced. 
	 * 
	 * @param formula key with which the specified value is to be associated
	 * @param type the narrative type as defined in TALConstants
	 * @return the previous value associated with the formula, or null if the formula did not exist in the narrative yet. 
	 * (A null return can also indicate that the map previously associated null with key)
	 */
	public Integer put(String formula, int narrativeType) {
		return narrative.put(formula, narrativeType);
	}
	
	public void remove(String formula) {
		narrative.remove(formula);
	}
	
	public HashSet<String> get(int narrativeType) {
		HashSet<String> ret = new HashSet<String>();
		
		for (String f : narrative.keySet()) {
			if (narrative.get(f) == narrativeType)
				ret.add(f);
		}
		
		return ret;
	}
	
	public int get(String formula) {
		return narrative.get(formula);
	}
	
	public boolean contains(String formula) {
		return narrative.containsKey(formula);
	}
	
	public HashMap<String, Integer> getMap() {
		return narrative;
	}
	
	/**
	 * Replaces the string representation of a narrative type with a new string
	 * @param narrativeType
	 * @param formula
	 */
	public void setStr(int narrativeType, String formula) {
		narrativeStr.put(narrativeType, formula);
	}
	
	public void concatStr(int narrativeType, String formula)  {
		String str = narrativeStr.get(narrativeType);
		narrativeStr.put(narrativeType, str + formula);
	}
	
	public String getStr(int narrativeType) {
		return narrativeStr.get(narrativeType);
	}
	
	public HashMap<Integer, String> getStrMap() {
		return narrativeStr;
	}
}
