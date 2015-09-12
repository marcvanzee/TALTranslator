package exceptions;

public class FileParseException extends Exception {
	private static final long serialVersionUID = -5746910035546155851L;

	public FileParseException(String string) {
		super(string);
	}

	public FileParseException(String string, Exception cause) {
		super(string,cause);
	}
	
	
}
