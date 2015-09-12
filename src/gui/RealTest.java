package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class RealTest
{
    public static void main(String[] args) 
    {

        Display display = new Display ();

        final Shell shell = new Shell (display);
        final Color green = display.getSystemColor (SWT.COLOR_GREEN);
        final Color orig = shell.getBackground();

        display.addFilter(SWT.KeyDown, new Listener() {

            public void handleEvent(Event e) {
                if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'f'))
                {
                    System.out.println("From Display I am the Key down !!" + e.keyCode);
                }
            }
        });

        shell.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
                if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'f'))
                {
                    shell.setBackground(orig);
                    System.out.println("Key up !!");
                }
            }
            public void keyPressed(KeyEvent e) {
                if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'f'))
                {
                    shell.setBackground(green);
                    System.out.println("Key down !!");
                }
            }
        });

        shell.setSize (200, 200);
        shell.open ();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();

    }
}