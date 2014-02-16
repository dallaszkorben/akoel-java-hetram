package hu.akoel.hetram.gui;

import hu.akoel.hetram.gui.MainPanel.Mode;
import hu.akoel.hetram.listeners.CalculationListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ControlSettingTab extends JPanel {

	private static final long serialVersionUID = 6137407949120046302L;

	private MainPanel mainPanel;
	
	private JTextField appliedXDeltaField;
	private JTextField appliedYDeltaField;
	private JButton calculateButton;
	private JProgressBar progressBar;	
	
	private JTextField maximumXDeltaField;
	private JTextField maximumYDeltaField;
	
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
/*		JPanel resolutionPanel = new JPanel();
		resolutionPanel.setLayout(new GridBagLayout());
		resolutionPanel.setBorder(BorderFactory.createTitledBorder(	BorderFactory.createLineBorder(Color.black), "Felbontas",TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints resolutionPanelConstraints = new GridBagConstraints();
*/
		
		
		//
		// Maximum horizontal deltaX
		//
		JLabel maximumXDeltaLabel = new JLabel("Maximum Δx: ");
		maximumXDeltaField = new JTextField();
		maximumXDeltaField.setEditable(false);
		maximumXDeltaField.setColumns(8);
//		maximumXDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(mainPanel.getHorizontalMaximumDifference())));
		JLabel maximumXDeltaUnit = new JLabel("m");

		//
		// Maximum vertical deltaY
		//
		JLabel maximumYDeltaLabel = new JLabel("Maximum Δy: ");
		maximumYDeltaField = new JTextField();
		maximumYDeltaField.setEditable(false);
		maximumYDeltaField.setColumns(8);
//		maximumYDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(mainPanel.getVerticalMaximumDifference())));
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
			String goodValue = String.valueOf( ControlSettingTab.this.mainPanel.getHorizontalDifferenceDivider());

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
			String goodValue = String.valueOf(ControlSettingTab.this.mainPanel.getVerticalDifferenceDivider());

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

		//
		// Szamitas pontossaga
		//
		JLabel calculationPrecisionLabel = new JLabel("Pontosság: ");
		JTextField calculationPrecisionField = new JTextField();
		calculationPrecisionField.setEditable(true);
		calculationPrecisionField.setColumns(4);
		calculationPrecisionField.setText( String.valueOf(ControlSettingTab.this.mainPanel.getCalculationPrecision() ) );
		calculationPrecisionField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf(ControlSettingTab.this.mainPanel.getCalculationPrecision());

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				ControlSettingTab.this.mainPanel.setCalculationPrecision( Double.valueOf(goodValue) );
				return true;
			}
		});
		
		//
		// Szamitas gomb
		//
		calculateButton = new JButton("Szamit");
		calculateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				//Mukodesi mod valtas - Calculation
				ControlSettingTab.this.mainPanel.setModeField( Mode.CALCULATION );
		
				//A szal elinditasa elott elinditja a progressBart -indeterminate modban
				progressBar.setIndeterminate(true);
				
				//A szal elinditasa elott torli az alkalmazott delta ertekeket
				ControlSettingTab.this.appliedXDeltaField.setText("");
				ControlSettingTab.this.appliedYDeltaField.setText("");
				
				//Torlom a mar letezo Thermikus Pont listat es az ertekelofelulet ujrarajzolasaval el is tuntetem
				ControlSettingTab.this.mainPanel.setThermicPointList(null);				
				
				//Letiltja a Kalkulacios gombot
				ControlSettingTab.this.calculateButton.setEnabled(false);
				
				//Majd letrehozom azt a szalat, ami majd a kalkulaciot vegzi
				Thread thread = new Thread(){
				   
					//Egyszer csak elindul a kalkulacio
					public void run() {
				    	
						ControlSettingTab.this.mainPanel.setCalculationListener(new CalculationListener(){

							boolean isIndeterminateMode = true;
							
							@Override
							public void getDifference(double difference) {
								
								//Atvaltok rendes modba
								if( difference <= 1.0 ){ 
										
										if( isIndeterminateMode ){
											progressBar.setIndeterminate( false );
										}							
										
										progressBar.setStringPainted(true);
										progressBar.setValue( (int)(ControlSettingTab.this.mainPanel.getCalculationPrecision()/difference*100) );

								}
							}							
						});
						
						//Elinditja a kalkulaciot a megadott ertekekkel
						ControlSettingTab.this.mainPanel.doCalculate( ControlSettingTab.this.mainPanel.getCalculationPrecision() );
						
						//Ha befejezodott a kalkulacio, akkor az alkalmazott delta ertekeket megjeleniti
//						ControlSettingTab.this.appliedXDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(ControlSettingTab.this.mainPanel.getHorizontalAppliedDifference())));
//						ControlSettingTab.this.appliedYDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(ControlSettingTab.this.mainPanel.getVerticalAppliedDifference())));
//						ControlSettingTab.this.appliedXDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(ControlSettingTab.this.mainPanel.getHorizontalAppliedDifference())));
//						ControlSettingTab.this.appliedYDeltaField.setText(String.valueOf(CommonOperations.get3Decimals(ControlSettingTab.this.mainPanel.getVerticalAppliedDifference())));

						//Ha nem igy rajzoltatom ujra az eredmenyt, akkor nem jelenik meg
						SwingUtilities.invokeLater( new Runnable(){

							@Override
							public void run() {

								//Grafika ujra rajzolasa
								ControlSettingTab.this.mainPanel.revalidateAndRepaint();
								
							}			
						});		
						
						//Ujra engedelyezi a Kalkulacios gomb hasznalatat
						//Letiltja a Kalkulacios gombot
						ControlSettingTab.this.calculateButton.setEnabled(true);
						
						//Nullazza a progressBar-t
						progressBar.setValue(0);
						ControlSettingTab.this.mainPanel.setCalculationListener(null);
						
						//Mukodesi mod valtas - Elemzes
						ControlSettingTab.this.mainPanel.setModeField( Mode.ANALYSIS );
				     
				    }
				};	
				
				//Elinditom a szalat
				thread.start();

			}
			
		});
		
		//
		//Progress bar
		//
		progressBar = new JProgressBar();
		
		
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
		// Kalkulacio pontossaga
		//
		row++;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(calculationPrecisionLabel, controlConstraints);

		controlConstraints.gridx = 1;
		controlConstraints.gridy = row;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(calculationPrecisionField, controlConstraints);
		
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
		// Progress bar
		//

		row++;
		Insets insets = controlConstraints.insets;
		int ipady = controlConstraints.ipady;
		controlConstraints.insets = new Insets(5, 0, 5, 0);
		controlConstraints.ipady = 8;
		controlConstraints.gridx = 0;
		controlConstraints.gridy = row;
		controlConstraints.gridwidth = 3;
		controlConstraints.anchor = GridBagConstraints.NORTH;
		controlConstraints.weighty = 0;
		controlConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(progressBar, controlConstraints);

		
		//
		// Alkalmazott X delta
		//
		row++;
		controlConstraints.insets = insets;
		controlConstraints.ipady = ipady;
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
	
//	public JTextField getMaximumXDeltaField() {
//		return maximumXDeltaField;
//	}
	
	public void setHorizontalMaximumDifference( BigDecimal horizontalMaximumDifference) {
/*		if( horizontalMaximumDifference > 0 ){
			this.maximumXDeltaField.setText( String.valueOf( horizontalMaximumDifference ) );
		}else{
			this.maximumXDeltaField.setText( "" );
		}
*/		
		if( null != horizontalMaximumDifference ){
			this.maximumXDeltaField.setText( horizontalMaximumDifference.toPlainString() );
		}else{
			this.maximumXDeltaField.setText( "" );
		}
	}
	
//	public JTextField getMaximumYDeltaField() {
//		return maximumYDeltaField;
//	}

	public void setVerticalMaximumDifference( BigDecimal verticalMaximumDifference) {
		if( null != verticalMaximumDifference ){
			this.maximumYDeltaField.setText( verticalMaximumDifference.toPlainString() );
		}else{
			this.maximumYDeltaField.setText("");
		}
		
//		if( verticalMaximumDifference > 0 ){
//			this.maximumYDeltaField.setText( String.valueOf( verticalMaximumDifference ) );
//		}else{
//			this.maximumYDeltaField.setText("");
//		}

	}
	
	public void setHorizontalAppliedDifference( BigDecimal difference ){
		if( null != difference ){
			this.appliedXDeltaField.setText( difference.toPlainString() );
		}else{
			this.appliedXDeltaField.setText("");
		}
	}
	
	public void setVerticalAppliedDifference( BigDecimal difference ){
		if( null != difference ){
			this.appliedYDeltaField.setText( difference.toPlainString() );
		}else{
			this.appliedYDeltaField.setText("");
		}
	}

}
