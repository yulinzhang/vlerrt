package gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import search.RRTsearchPausing;

@SuppressWarnings("serial")
public class PausingGUI extends GUI {

	protected JButton doneButton, stepButton;
	protected final RRTsearchPausing search;

	public PausingGUI(RRTsearchPausing s){	
		super(s.getWorld(), s.getSearchTree(), true);
		this.search = s;
		doneButton = new JButton(new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				search.setDone(true);
			}
		});
		doneButton.setMnemonic(KeyEvent.VK_ESCAPE); // + ALT
		
		stepButton = new JButton(new AbstractAction("Step") {
			public void actionPerformed(ActionEvent e) {
				search.setNextStep(true);
			}
		});
		stepButton.setMnemonic(KeyEvent.VK_D); // + ALT

		add(doneButton);
		add(stepButton);
	}
}
