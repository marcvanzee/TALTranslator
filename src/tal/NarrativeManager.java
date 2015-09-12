package tal;

import io.IO;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import parser.FOLParser;
import parser.alphabet.AlphabetParser;
import parser.alphabet.ParseException;
import parser.syntaxtree.Node;
import constraints.ConstraintsManager;

/**
 * The narrative contains the alphabet, a narrative and the parser

 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class NarrativeManager 
{
	private Narrative narrative = new Narrative();
	private Alphabet alphabet = new Alphabet();
	private ConstraintsManager constraints = new ConstraintsManager();
	private FOLParser parser = new FOLParser();
	
	/**
	 * General validation of the entire narrative. This will first
	 * parse the string representation of the alphabet and all narrative types,
	 * and subsequently validate it.
	 * 
	 */
	public boolean validate()
	{
		// first initialize Alphabet and Narrative with string representations
		if (!buildNarrative()) return false;
		
		// then validate the narratives
		for (int n : TALConstants.getNarrativeMap().keySet()) {
			if (TALConstants.isNarrative(n)) {
				IO.print("validating " + TALConstants.getNarrativeDescription(n) + ": " + 
			narrative.get(n));
				if (!validate(n)) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * General validation of a narrative that will do three things:
	 * - Check for syntactically correctness according to First-Order Logic
	 * - Check whether the general constraints (always active) of TAL are met
	 * - Check whether the narrative type specific (such as: observations) constraints
	 *   are met as well. 
	 *   
	 * If this all is met, true is returned. Otherwise, false is returned and the error 
	 * message is printed to the GUI. No narrative is built using this method.
	 */
	public boolean validate(int narrativeType)
	{		
		LinkedList<Node> trees = parser.getTrees();
		Validator validator = new Validator(trees, alphabet, narrative, narrativeType, constraints.getMap());
		
		return validator.parseNarrative() && validator.parseConstraints();
	}
	
	/**
	 * Builds the entire narrative from the string representations using the parser
	 */
	private boolean buildNarrative() {
		if (!buildAlphabet()) return false;
		
		// go over all narrative types
		for (Entry<Integer, String> n : narrative.getStrMap().entrySet())
		{
			IO.guiErr("parsing narrative " + TALConstants.getNarrativeDescription(n.getKey()) + ": " + n.getValue());
			// separate formulas in a narrative type
			String[] formulas = n.getValue().split(";");
			
			// try parsing the formula and add if it succeeds
			for (String formula : formulas) {
				if (empty(formula)) continue;
				if (!parser.parse(formula)) return false;
				if (narrative.contains(formula)) {
					
					String narrativeExisting = TALConstants.getNarrativeDescription(narrative.get(formula));
					String narrativeNew = TALConstants.getNarrativeDescription(n.getKey());
					IO.guiErr("Warning: formula " + formula + "occurs both in " + narrativeExisting + 
							" and in " + narrativeNew + ". First occurrence is discarded");
				}
				IO.print(formula + " in " + TALConstants.getNarrativeDescription(n.getKey()));
				narrative.put(formula, n.getKey());
			}
		}
		
		return true;
	}
	
	private boolean buildAlphabet() {
		AlphabetParser alphParser = new AlphabetParser(new StringReader(""));
		
		alphParser.ReInit(new StringReader(alphabet.getStr()));
		
		HashMap<String, Integer> a = null;
				
		try {
			a = alphParser.alphabet();
		} catch (ParseException e) {
			IO.guiErr("Wrong input, todo: make this nice! " + e.getMessage());
			return false;
		}
		
		if (a == null) return false;
		alphabet.putAll(a);
		return true;
	}
	
	public ConstraintsManager getConstraintsManager() {
		return constraints;
	}
	
	public Alphabet getAlphabet() { return alphabet; }
	
	public Narrative getNarrative() { return narrative; }
		
	public String getData(String narrativeType) 
	{
		int id = TALConstants.getNarrativeId(narrativeType);
		
		if (TALConstants.isAlphabet(id))
			return alphabet.getStr();
		else if (TALConstants.isNarrative(id))
			return narrative.getStr(id);

		return null;
	}
	
	public void setData(String narrativeType, String txt) {
		int id = TALConstants.getNarrativeId(narrativeType);
		
		if (TALConstants.isAlphabet(id))
			alphabet.setStr(txt);
		else if (TALConstants.isNarrative(id))
			narrative.setStr(id, txt);
	}
	
	
	public void concatData(String narrativeType, String txt) {
		int id = TALConstants.getNarrativeId(narrativeType);
		
		concatData(id, txt);
	}
	
	public void concatData(int id, String txt) 
	{		
		if (TALConstants.isAlphabet(id))
			alphabet.concatStr(txt);
		else if (TALConstants.isNarrative(id))
			narrative.concatStr(id, txt);
	}
	
	private boolean empty(String str) {
		return str.matches("\\s*");
	}
}
