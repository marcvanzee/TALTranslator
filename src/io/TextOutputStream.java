package io;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.widgets.Text;

/**
* An output stream that writes its output to a javax.swing.JTextArea
* control.
*
*/

public class TextOutputStream extends OutputStream
{
    private Text text;
    
    /**
     * Creates a new instance of TextAreaOutputStream which writes
     * to the specified instance of javax.swing.JTextArea control.
     *
     * @param control   A reference to the javax.swing.JTextArea
     *                  control to which the output must be redirected
     *                  to.
     */
    public TextOutputStream( Text control ) {
        text = control;
    }
    
    /**
     * Writes the specified byte as a character to the
     * javax.swing.JTextArea.
     *
     * @param   b   The byte to be written as character to the
     *              JTextArea.
     */
    public void write( int b ) throws IOException {
        // append the data as characters to the JTextArea control
        text.append( String.valueOf( ( char )b ) );
    }  
}