package operations;

public class StringOperations {
	/**
	 * Compare two strings and return true when they are equal. The last parameter
	 * determines whether the string comparing is case-sensitive or not
	 * 
	 * @param str1
	 * @param str2
	 * @param caseSensitive
	 * @return
	 */
	public static boolean equal(String str1, String str2, boolean caseSensitive)
	{
		return (caseSensitive) ? str1.equals(str2) : str1.toLowerCase().equals(str2.toLowerCase());
	}
	
	public static String stripParentheses(String str) {
		String ret = str.trim();
		
		int p = 0;
		while (ret.startsWith("(") && ret.endsWith(")")) { p++; ret = ret.substring(1, ret.length()-1).trim(); }
		return ret;
	}
}
