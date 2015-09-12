package parser;

public abstract class Parser {
	public abstract boolean parse(String str);
	
	public static String prettyErrorPrint(ParseException e) 
	{
		String eol = System.getProperty("line.separator", "\n");
		
		// Default error message
		String[] emsg = e.getMessage().split(eol, 2);
		
		// do some fancy parsing with the rest of the error messages: take out all the enters and spaces and replace them with a comma
		String first = emsg[0] + eol;
		
		String last = emsg[1].replaceAll(eol, ""); 					// remove all carriage returns because JavaCC has way too many
		last = last.replaceAll("[ ][.][.][.][ ][ ][ ][ ]", ", ");	// replace all "..." with a comma
		last = last.replaceAll("[ ][ ][ ]", "");					// remove all additional spaces
		
		return first + last + eol + eol;
	}
}
