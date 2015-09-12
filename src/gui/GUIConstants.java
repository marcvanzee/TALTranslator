package gui;

public class GUIConstants 
{
	public static boolean unsaved(String item) { return item.charAt(0)=='*'; }

	public static String excludeSaveSign(String text) {
		return (text.charAt(0)=='*'? text.substring(1): text);
	}
}
