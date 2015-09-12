package parser.visitor;

import io.IO;

import java.util.LinkedList;

import parser.ParseException;
import parser.syntaxtree.Node;

public class VisitResult
{
	public Node tree;
	public String image;
	public boolean isLiteral = false;
	public boolean isNegated = false;
	public LinkedList<VisitResult> nodes = new LinkedList<VisitResult>(); // we store a list of nodes in case we have a NodeList/NodeSequence
	
	public VisitResult() {

	}
	
	public VisitResult(String img) {
		this();
		rebuild(img);
	}
	
	public VisitResult(String img, boolean isLiteral, boolean isNegated)
	{
		this(img);
		this.isLiteral = isLiteral;
		this.isNegated = isNegated;
	}

	public VisitResult(String img, LinkedList<VisitResult> nodes, boolean isLiteral, boolean isNegated)
	{
		this(img, isLiteral, isNegated);
		this.nodes = nodes;
	}
	
	public void rebuild(String img, boolean isLiteral, boolean isNegated) {
		rebuild(img);
		this.isLiteral = isLiteral;
		this.isNegated = isNegated;
	}
	
	public void rebuild(String img, LinkedList<VisitResult> nodeList,
			boolean isLiteral, boolean isNegated) {
		rebuild(img);
		this.isLiteral = isLiteral;
		this.isNegated = isNegated;
		this.nodes = nodeList;
	}
	
	/**
	 * Rebuild the result using a string, this will set the image to the argument and
	 * build the tree of this image
	 * 
	 * @param image
	 */
	public void rebuild(String img)
	{
		image = removeSpaces(img);
		
		// try to build a tree but if it doesnt work don't worry
		try {
			tree = IO.strToTree(img);
		} catch (ParseException e) {}
	}
	
	/**
	 * Simply rebuild the tree using the current image
	 */
	public void rebuild()
	{
		rebuild(image);
	}
	
	private String removeSpaces(String img)
	{
		return img.replaceAll("  ", " ");
	}
}
