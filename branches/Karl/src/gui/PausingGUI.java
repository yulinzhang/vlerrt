package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import search.RRTsearchPausing;

@SuppressWarnings("serial")
public class PausingGUI extends GUI implements ActionListener {

	protected JButton doneButton, stepButton;
	protected PausingSearch search;



	public PausingGUI(PausingSearch search){	
		super(search.getWorld(), search.getSearchTree(), true);
		this.search = search; 
		doneButton = new JButton("Exit");
		doneButton.setVerticalTextPosition(AbstractButton.CENTER);
		doneButton.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		doneButton.setMnemonic(KeyEvent.VK_D);
		doneButton.setActionCommand("end");
		doneButton.addActionListener(this);
		
		stepButton = new JButton("Step");
		stepButton.setVerticalTextPosition(AbstractButton.CENTER);
		stepButton.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		stepButton.setMnemonic(KeyEvent.VK_D);
		stepButton.setActionCommand("step");
		stepButton.addActionListener(this);

		add(doneButton);
		add(stepButton);
		setVisible(true);
		
	}

	public void actionPerformed(ActionEvent e) {
		if ("end".equals(e.getActionCommand())) {
			search.setExit(true);
		} else {
			search.setNextStep(true);
		}	
	}
}
