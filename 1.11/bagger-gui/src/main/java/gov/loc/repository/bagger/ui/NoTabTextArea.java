
package gov.loc.repository.bagger.ui;

import javax.swing.JTextArea;
import java.awt.event.KeyEvent;

public class NoTabTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;

	public NoTabTextArea(int row, int cols) {
    	super(row, cols);
//    	this.setFocusTraversalKeysEnabled(false);
    }

    protected void processComponentKeyEvent( KeyEvent e ) {
    	if ( e.getID() == KeyEvent.KEY_PRESSED &&
    			e.getKeyCode() == KeyEvent.VK_TAB ) {
    		e.consume();
    		if (e.isShiftDown()) {
    			transferFocusBackward();
    		} else {
    			transferFocus();
    		}
    	} else {
    		super.processComponentKeyEvent( e );
    	}
    }
}
