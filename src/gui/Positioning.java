package gui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class Positioning {

	public static Point getCenter(Display display, Point size) {

		Rectangle screen = display.getPrimaryMonitor().getBounds();
		int width = screen.width;
		int height = screen.height;
		
		int x = width/2-size.x/2;
		int y = height/2-size.y/2;
		
		return new Point(x,y);
	}
	
	public static Point getBounds(Display display) {
		Rectangle r = display.getPrimaryMonitor().getBounds();
		return new Point(r.width, r.height);
	}

}
