package gui.listeners;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class InputListener implements ModifyListener
{
	StyledText text;
	CTabItem tabItem;
	
	public InputListener(StyledText text, CTabItem tabItem) {
		this.text = text;
		this.tabItem = tabItem;
	}

	@Override
	public void modifyText(ModifyEvent arg0) {
		if (tabItem.getText().charAt(0)!='*') 
    		tabItem.setText("*" + tabItem.getText());		
	}
}
