package tal;

import io.IO;

import java.util.Iterator;
import java.util.LinkedList;

import parser.ParseException;
import parser.syntaxtree.Node;
import parser.visitor.TALVisitor;
import parser.visitor.VisitResult;
import parser.visitor.VisitorFactory;

public class VisitorManager 
{
	public VisitResult visitTree(String tree, int visitor) 
	{ 
		try {
			return visitTree(IO.strToTree(tree), new int[]{visitor});
		} catch (ParseException e) { e.printStackTrace(); }
		
		return null;
	} 
	
	public VisitResult visitTree(String tree, int[] visitor) 
	{ 
		try {
			return visitTree(IO.strToTree(tree), visitor);
		} catch (ParseException e) { e.printStackTrace(); }
		
		return null;
	} 
	
	public VisitResult visitTree(Node tree, int visitor) { return visitTree(tree, new int[]{visitor}); } 
	
	public VisitResult visitTree(Node tree, int[] visitors)
	{
		VisitorDescription vd = new VisitorDescription();
		vd.add(visitors);
		
		return startVisiting(tree, vd);
	}
	
	private VisitResult startVisiting(Node tree, VisitorDescription visitors)
	{
		VisitResult result = null;
		
		for (Pair vd : visitors)
		{
			result = tree.accept(vd.visitor, null);
			try {
				tree = IO.strToTree(result.image);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		
		return result;
	}
	
	private class VisitorDescription implements Iterable<Pair>
	{
		LinkedList<Pair> visitors = new LinkedList<Pair>();
		VisitorFactory factory = new VisitorFactory();

		@Override
		public Iterator<Pair> iterator() {
			return visitors.iterator();
		}
		
		public void add(int v)
		{
			TALVisitor<?> visitor = factory.getVisitor(v);
			visitors.add(new Pair(v, visitor));
		}
		
		public void add(int[] v)
		{
			for (int v1 : v) add(v1);
		}
			
	}
	
	private class Pair
	{
		int name;
		TALVisitor<?> visitor;
		
		public Pair(int n, TALVisitor<?> v) {
			name = n;
			visitor = v;
		}
	}
}
