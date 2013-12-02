package hu.akoel.hetram.gui;

import hu.akoel.hetram.CommonOperations;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ControlSettingTab extends JPanel {

	private static final long serialVersionUID = 6137407949120046302L;

	private MainPanel mainPanel;
	
	private JTextField appliedXDeltaField;
	private JTextField appliedYDeltaField;
	private JButton calculateButton;

	public ControlSettingTab(MainPanel mainPanel) {
		super();

		this.mainPanel = mainPanel;

		int row = 0;

		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout(new GridBagLayout());
		GridBagConstraints controlConstraints = new GridBagConstraints();

		// ----------------------------------------------
		//
		// Control TAB elemek letrehozasa
		//
		// ----------------------------------------------
		JPanel resolutionPanel = new JPanel();
		resolutionPanel.setLayout(new GridBagLayout());
		resolutionPanel.setBorder(BorderFactory.createTitledBorder(	BorderFactory.createLineBorder(Color.black), "Felbontas",TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints resolutionPanelConstraints = new GridBagConstraints();

		//
		// Maximum horizontal deltaX
		//
		JLabel maximumXDeltaLabel = new JLabel("Maximum Δx: ");
		JTextField maximumXDeltaField = new JTextField();
		maximumXDeltaField.setEditable(false);
		maximumXDeltaField.setColumns(8);
		maximumXDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(mainPanel.getHorizontalMaximumDifference())));
		JLabel maximumXDeltaUnit = new JLabel("m");

		//
		// Maximum vertical deltaY
		//
		JLabel maximumYDeltaLabel = new JLabel("Maximum Δy: ");
		JTextField maximumYDeltaField = new JTextField();
		maximumYDeltaField.setEditable(false);
		maximumYDeltaField.setColumns(8);
		maximumYDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(mainPanel.getVerticalMaximumDifference())));
		JLabel maximumYDeltaUnit = new JLabel("m");

		//
		// Kert horizontal deltaX oszto
		//
		JLabel askedXDeltaDividerLabel = new JLabel("Δx osztó: ");
		JTextField askedXDeltaDividerField = new JTextField();
		askedXDeltaDividerField.setEditable(true);
		askedXDeltaDividerField.setColumns(4);
		askedXDeltaDividerField.setText("1");
		askedXDeltaDividerField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(String.valueOf( ControlSettingTab.this.mainPanel.getHorizontalDifferenceDivider()));

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Integer.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				ControlSettingTab.this.mainPanel.setHorizontalDifferenceDivider(Integer.valueOf(goodValue));
				return true;
			}
		});

		//
		// Kert vertical deltaY oszto
		//
		JLabel askedYDeltaDividerLabel = new JLabel("Δy osztó: ");
		JTextField askedYDeltaDividerField = new JTextField();
		askedYDeltaDividerField.setEditable(true);
		askedYDeltaDividerField.setColumns(4);
		askedYDeltaDividerField.setText("1");
		askedYDeltaDividerField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(String.valueOf(ControlSettingTab.this.mainPanel.getVerticalDifferenceDivider()));

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Integer.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				ControlSettingTab.this.mainPanel.setVerticalDifferenceDivider(Integer.valueOf(goodValue));
				return true;
			}
		});

		calculateButton = new JButton("Szamit");
		calculateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//A szal elinditasa elott torli az alkalmazott delta ertekeket
				ControlSettingTab.this.appliedXDeltaField.setText("");
				ControlSettingTab.this.appliedYDeltaField.setText("");
				
				//Letiltja a Kalkulacios gombot
				ControlSettingTab.this.calculateButton.setEnabled(false);
				
				//Egy szal definialasa a kalkulacio szamara
				Thread t = new Thread(){
					
					public void run(){						
						
						//Elinditja a kalkulaciot a megadott ertekekkel
						ControlSettingTab.this.mainPanel.doCalculate();
						
						//Ha befejezodott a kalkulacio, akkor az alkalmazott delta ertekeket megjeleniti
						ControlSettingTab.this.appliedXDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(ControlSettingTab.this.mainPanel.getHorizontalAppliedDifference())));
						ControlSettingTab.this.appliedYDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(ControlSettingTab.this.mainPanel.getVerticalAppliedDifference())));

						//Grafika ujra rajzolasa
						ControlSettingTab.this.mainPanel.revalidateAndRepaint();
						
						//Ujra engedelyezi a Kalkulacios homb hasznalatat
						//Letiltja a Kalkulacios gombot
						ControlSettingTab.this.calculateButton.setEnabled(true);

					}					
					
				};
				
				//A szal elinditasa
				t.start();
					
				
				
	

				
			}
		});
		
		// Alkalmazott horizontal deltaX
		//
		JLabel appliedXDeltaLabel = new JLabel("Alkalmazott Δx: ");
		appliedXDeltaField = new JTextField();
		appliedXDeltaField.setEditable(false);
		appliedXDeltaField.setColumns(8);
		appliedXDeltaField.setText("");
		JLabel appliedXDeltaUnit = new JLabel("m");

		//
		// Alkalmazott vertical deltaY
		//
		JLabel appliedYDeltaLabel = new JLabel("Alkalmazott Δy: ");
		appliedYDeltaField = new JTextField();
		appliedYDeltaField.setEditable(false);
		appliedYDeltaField.setColumns(8);
		appliedYDeltaField.setText("");
		JLabel appliedYDeltaUnit = new JLabel("m");

		// ************************************
		// ************************************
		// TABOK FELTOLTESE
		// ************************************
		// ************************************

		// ------------------------------------
		//
		// Control TAB feltoltese
		//
		// ------------------------------------

		//
		// Maximum X delta
		//
		row = 0;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(maximumXDeltaLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(maximumXDeltaField, controlConstraints);

		controlConstraints.gridx = 2;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(maximumXDeltaUnit, controlConstraints);

		//
		// Maximum Y delta
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(maximumYDeltaLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(maximumYDeltaField, controlConstraints);

		controlConstraints.gridx = 2;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(maximumYDeltaUnit, controlConstraints);

		//
		// deltaX oszto
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(askedXDeltaDividerLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(askedXDeltaDividerField, controlConstraints);

		//
		// deltaY oszto
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(askedYDeltaDividerLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(askedYDeltaDividerField, controlConstraints);

		//
		// Calculate gomb
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 3;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(calculateButton, controlConstraints);

		//
		// Alkalmazott X delta
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(appliedXDeltaLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(appliedXDeltaField, controlConstraints);

		controlConstraints.gridx = 2;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(appliedXDeltaUnit, controlConstraints);

		//
		// Alkalmazott Y delta
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(appliedYDeltaLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(appliedYDeltaField, controlConstraints);

		controlConstraints.gridx = 2;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(appliedYDeltaUnit, controlConstraints);

		//
		// Felfele igazitas
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 1;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 1;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel(), controlConstraints);

	}
}
