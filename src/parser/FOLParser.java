package parser;

import io.IO;

import java.io.StringReader;
import java.util.LinkedList;

import parser.syntaxtree.Node;
import tal.TALConstants;


/**
 * Convenience class to communicate with the parser
 * This parser will check formulas for syntactical correctness
 * for a first-order formula and store them in a tree.
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class FOLParser extends Parser 
{
	private TALParser parser = null;
	private LinkedList<Node> trees = new LinkedList<Node>();
		
	public boolean parse(String str) 
	{
		IO.gui1("Checking FOL correctness...");
		
		trees = new LinkedList<Node>();
		
		for (String line : str.split(TALConstants.SEMICOLON))
		{
			if (!parseLine(line))
				return false;
		}
		
		IO.gui("succeeded!");
		
		return true;
	}
	
	private boolean parseLine(String str) 
	{
		// parse the tree and see whether we run into any syntactical error
		// related to the construction of a first-order formula
		Node tree = null;
		if (parser == null) parser = new TALParser(new StringReader(""));
		
		TALParser.ReInit(new StringReader(str));
		
		try 
		{
			tree = TALParser.one_line();
		} catch (ParseException e) 
		{
			IO.gui("failed!");
			IO.gui(prettyErrorPrint(e) + "\n");
			return false;
		}  catch (TokenMgrError e1)
		{
			IO.gui("failed!");
			IO.gui(e1.getMessage() + "\n");
			return false;
		} catch (Exception e) 
		{
			IO.gui("\n ! unexpected error occurred:");
			IO.gui(e.getMessage() + "\n");
			return false;
		}
		
		trees.add(tree);
		
		return true;
	}
	
	public LinkedList<Node> getTrees()
	{
		return trees;
	}
}
