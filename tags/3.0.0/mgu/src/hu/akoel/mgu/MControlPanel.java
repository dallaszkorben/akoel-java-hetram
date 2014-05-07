package hu.akoel.mgu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MControlPanel extends JPanel{

	private static final long serialVersionUID = 8730650364038830165L;

	private GridBagConstraints panelConstraints;
	private JLabel filler;
	
	public MControlPanel(){
		
		super();
		
		this.setLayout(new GridBagLayout());
				
		panelConstraints = new GridBagConstraints();
		
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 0;
		panelConstraints.anchor = GridBagConstraints.NORTH;
		panelConstraints.weighty = 0;
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		//Azert hogy felfele legyen igazitva
		panelConstraints.weighty = 1;
		panelConstraints.gridy++;
		filler = new JLabel();
		this.add( filler, panelConstraints );
		
	}
	
	public void addElement( JPanel element ){

		this.remove( filler );

		panelConstraints.gridy++;		
		panelConstraints.weighty = 0;
		this.add( element, panelConstraints);
		
		//Azert hogy felfele legyen igazitva
		panelConstraints.weighty = 1;
		panelConstraints.gridy++;
		this.add( filler, panelConstraints );
		
	}
}
