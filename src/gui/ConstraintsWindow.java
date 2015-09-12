package gui;

import io.IO;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import tal.NarrativeManager;
import tal.TALConstants;
import constraints.ConstraintConstants;
import constraints.ConstraintsManager;

public class ConstraintsWindow 
{
	Object result;
	Shell shlConstraints, parent;
	NarrativeManager narrative;
	ConstraintsManager constraints;

	/**
	 * Create the dialog.
	 * @param parent
	 */
	public ConstraintsWindow(Shell parent, NarrativeManager narrative) 
	{
		this.narrative = narrative;
		this.constraints = narrative.getConstraintsManager();
		this.parent = parent;
		
		createContents();
		
		shlConstraints.open();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlConstraints = new Shell(parent);
		shlConstraints.setSize(393, 449);
		
		shlConstraints.setText("Constraints");
		
		shlConstraints.setLocation(Positioning.getCenter(parent.getDisplay(), shlConstraints.getSize()));
		
		Composite composite = new Composite(shlConstraints, SWT.NONE);
		composite.setBounds(10, 10, 367, 401);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 367, 15);
		lblNewLabel.setText("Enable/Disable constraints. Some constraints are always enabled or disabled.");
		
		Button btnClose = new Button(composite, SWT.NONE);
		btnClose.setBounds(282, 366, 75, 25);
		btnClose.setText("Close");
		
		btnClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shlConstraints.dispose();
            }
        });
		
		HashMap<Integer, Boolean> constrMap = constraints.getMap();
		
		int h=55;
		
		for (final int c : constrMap.keySet())
		{
			String constrDescription = ConstraintConstants.getConstraintDescription(c);
			
			final Button button = new Button(composite, SWT.CHECK);
			button.setBounds(10, h, 300, 16);
			button.setText(constrDescription);
			button.setSelection(constrMap.get(c));
			
						
			if (ConstraintConstants.getEnabledConstraints().contains(c) ||
					ConstraintConstants.getDisabledConstraints().contains(c))
				button.setEnabled(false);
			
			
			button.addListener (SWT.Selection, new Listener () {
				public void handleEvent (Event event) {
					// if a constraint is set, we first need to check whether the current narrative
					// is compatible with the constraint. if not, we cannot continue and we inform the user				
					constraints.put(c, button.getSelection());
					
					if (button.getSelection() == true) {
						int narrativeType = ConstraintConstants.constraintNarrativeType(c);			
						if (!narrative.validate(narrativeType)) {
							button.setSelection(false);
							IO.alert(shlConstraints, "Constraint Activation", "Could not activate constraint because narrative \"" +
							TALConstants.getNarrativeDescription(narrativeType) + "\" does not meet constraint. See log for more details.");
							constraints.put(c, false);
						}
					}
					
					
					
				}
			});
			
			h += 25;
		}
	}
}
