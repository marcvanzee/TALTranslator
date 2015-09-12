package operations;

import java.util.Enumeration;

public class EnumOperations {
	public static int getEnumSize(Enumeration<?> e) {
		int length = 0;		
		while (e.hasMoreElements()) {
			e.nextElement();
			length++;
		}
		
		return length;
	}
}
