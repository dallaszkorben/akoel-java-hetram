package hu.akoel.hetram.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class VisibilitySettingTab extends JPanel{

	private static final long serialVersionUID = 5151984439858433362L;
	
	private MainPanel mainPanel ;
	
	public VisibilitySettingTab( MainPanel mainPanel ){
		super();
		
		this.mainPanel = mainPanel;
		
		int row = 0;
		
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout(new GridBagLayout());
		GridBagConstraints visibilitySettingConstraints = new GridBagConstraints();
		
	
		//------------------------------------------------
		//
		// Homerseklet szin szerinti megjelnitese - BLOKK
		//
		//------------------------------------------------
		JPanel temperatureByColorPanel = new JPanel();
		temperatureByColorPanel.setLayout( new GridBagLayout() );
		temperatureByColorPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Hőmérséklet-Szin", TitledBorder.LEFT, TitledBorder.TOP ) );
		GridBagConstraints temperatureByColorPanelConstraints = new GridBagConstraints();
		
		//Homerseklet-szin megjelenites
		JCheckBox turnOnTemperatureByColor = new JCheckBox("Látszik");
		turnOnTemperatureByColor.setSelected( mainPanel.isNeedDrawTemperatureByColor() );
		turnOnTemperatureByColor.addItemListener( new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					VisibilitySettingTab.this.mainPanel.setNeedDrawTemperatureByColor( false );
				}else{
					VisibilitySettingTab.this.mainPanel.setNeedDrawTemperatureByColor( true );
				}
			}
		});
		
		//1. sor - Turn on Temperature by Color
		row = 0;
		temperatureByColorPanelConstraints.gridx = 0;
		temperatureByColorPanelConstraints.gridy = row;
		temperatureByColorPanelConstraints.gridwidth = 4;
		temperatureByColorPanelConstraints.anchor = GridBagConstraints.WEST;
		temperatureByColorPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		temperatureByColorPanelConstraints.weightx = 1;
		temperatureByColorPanel.add(turnOnTemperatureByColor, temperatureByColorPanelConstraints);

		//-------------------------------------
		//
		// Termikus pont  megjelnitese - BLOKK
		//
		//-------------------------------------
		JPanel thermicPointPanel = new JPanel();
		thermicPointPanel.setLayout( new GridBagLayout() );
		thermicPointPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Termikus pont", TitledBorder.LEFT, TitledBorder.TOP ) );
		GridBagConstraints thermicPointPanelConstraints = new GridBagConstraints();
		
		//Termikus pont megjelenites
		JCheckBox turnOnThermicPoint = new JCheckBox("Látszik");
		turnOnThermicPoint.setSelected( mainPanel.isNeedDrawPoint() );
		turnOnThermicPoint.addItemListener( new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					VisibilitySettingTab.this.mainPanel.setNeedDrawPoint(false);
				}else{
					VisibilitySettingTab.this.mainPanel.setNeedDrawPoint(true);
				}
			}
		});		
		
		//Termikus Pont radius
		JLabel thermicPointRadiusLabel = new JLabel("Pont sugara: ");
		JTextField thermicPointRadiusField = new JTextField();
		thermicPointRadiusField.setEditable(true);
		thermicPointRadiusField.setColumns(4);
		thermicPointRadiusField.setText( String.valueOf( VisibilitySettingTab.this.mainPanel.getThermicPointRadius() ) );
		thermicPointRadiusField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf( VisibilitySettingTab.this.mainPanel.getThermicPointRadius() );

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
				VisibilitySettingTab.this.mainPanel.setThermicPointRadius( Double.valueOf( goodValue ) );
				VisibilitySettingTab.this.mainPanel.revalidateAndRepaint();
				return true;
			}
		});
		JLabel thermicPointRadiusUnit = new JLabel( "m" );
		
		//1. sor - Turn on Thermic Point on/Off
		row = 0;
		thermicPointPanelConstraints.gridx = 0;
		thermicPointPanelConstraints.gridy = row;
		thermicPointPanelConstraints.gridwidth = 4;
		thermicPointPanelConstraints.anchor = GridBagConstraints.WEST;
		thermicPointPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		thermicPointPanelConstraints.weightx = 1;
		thermicPointPanel.add(turnOnThermicPoint, thermicPointPanelConstraints);	
		
		//2. sor - Thermic point radius
		row++;
		thermicPointPanelConstraints.gridx = 0;
		thermicPointPanelConstraints.gridy = row;
		thermicPointPanelConstraints.gridwidth = 1;
		thermicPointPanelConstraints.weightx = 0;
		thermicPointPanel.add(new JLabel("     "), thermicPointPanelConstraints);
		
		thermicPointPanelConstraints.gridx = 1;
		thermicPointPanelConstraints.gridy = row;
		thermicPointPanelConstraints.gridwidth = 1;
		thermicPointPanelConstraints.weightx = 0;
		thermicPointPanel.add(thermicPointRadiusLabel, thermicPointPanelConstraints );

		thermicPointPanelConstraints.gridx = 2;
		thermicPointPanelConstraints.gridy = row;
		thermicPointPanelConstraints.gridwidth = 1;
		thermicPointPanelConstraints.weightx = 0;
		thermicPointPanel.add(thermicPointRadiusField, thermicPointPanelConstraints );
		
		thermicPointPanelConstraints.gridx = 3;
		thermicPointPanelConstraints.gridy = row;
		thermicPointPanelConstraints.gridwidth = 1;
		thermicPointPanelConstraints.weightx = 0;
		thermicPointPanel.add(thermicPointRadiusUnit, thermicPointPanelConstraints );

		//-----------------------------------------
		//
		// Hőmérséklet szám   megjelnitese - BLOKK
		//
		//-----------------------------------------
		JPanel temperatureByFontPanel = new JPanel();
		temperatureByFontPanel.setLayout( new GridBagLayout() );
		temperatureByFontPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Hőmérséklet-szám", TitledBorder.LEFT, TitledBorder.TOP ) );
		GridBagConstraints temperatureByFontPanelConstraints = new GridBagConstraints();
		
		//Homerseklet szam megjelenites
		JCheckBox turnOnTemperatureByFont = new JCheckBox("Látszik");
		turnOnTemperatureByFont.setSelected( mainPanel.isNeedDrawTemperatureByFont() );
		turnOnTemperatureByFont.addItemListener( new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					VisibilitySettingTab.this.mainPanel.setNeedDrawTemperatureByFont(false);
				}else{
					VisibilitySettingTab.this.mainPanel.setNeedDrawTemperatureByFont(true);
				}
			}
		});
		
		//Leghidegebb szin
		JColorChooser coldestColorChooser = new JColorChooser();

		
		
		//1. sor - Turn on Homerseklet szam
		row = 0;
		temperatureByFontPanelConstraints.gridx = 0;
		temperatureByFontPanelConstraints.gridy = row;
		temperatureByFontPanelConstraints.gridwidth = 4;
		temperatureByFontPanelConstraints.anchor = GridBagConstraints.WEST;
		temperatureByFontPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		temperatureByFontPanelConstraints.weightx = 1;
		temperatureByFontPanel.add(turnOnTemperatureByFont, temperatureByFontPanelConstraints);	
		
		//2. sor - Leghidegebb szin
		
		
		//3. sor - legmelegebb szin
		
		
		
		//
		// Hőáram  megjelnitese - BLOKK
		//
		JPanel currentPanel = new JPanel();
		currentPanel.setLayout( new GridBagLayout() );
		currentPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Termikus áram", TitledBorder.LEFT, TitledBorder.TOP ) );
		GridBagConstraints currentPanelConstraints = new GridBagConstraints();
		
		//Termikus pont megjelenites
		JCheckBox turnOnCurrent = new JCheckBox("Látszik");
		turnOnCurrent.setSelected( mainPanel.isNeedDrawCurrentByArrow() );
		turnOnCurrent.addItemListener( new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					VisibilitySettingTab.this.mainPanel.setNeedDrawCurrentByArrow(false);
				}else{
					VisibilitySettingTab.this.mainPanel.setNeedDrawCurrentByArrow(true);
				}
			}
		});
		
		//1. sor - Turn on Thermic Point
		row = 0;
		currentPanelConstraints.gridx = 0;
		currentPanelConstraints.gridy = row;
		currentPanelConstraints.gridwidth = 4;
		currentPanelConstraints.anchor = GridBagConstraints.WEST;
		currentPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		currentPanelConstraints.weightx = 1;
		currentPanel.add(turnOnCurrent, currentPanelConstraints);			
		
		
		//-----------------------------------
		//
		// Visibility TAB feltoltese
		//
		//-----------------------------------
		
		//
		// Homerseklet-szin szekcio
		//
		row = 0;
		visibilitySettingConstraints.gridx = 0;
		visibilitySettingConstraints.gridy = row;
		visibilitySettingConstraints.anchor = GridBagConstraints.NORTH;
		visibilitySettingConstraints.weighty = 0;
		visibilitySettingConstraints.weightx = 1;
		visibilitySettingConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(temperatureByColorPanel, visibilitySettingConstraints);
		
		//
		// Termikus pont szekcio
		//
		row++;
		visibilitySettingConstraints.gridx = 0;
		visibilitySettingConstraints.gridy = row;
		visibilitySettingConstraints.anchor = GridBagConstraints.NORTH;
		visibilitySettingConstraints.weighty = 0;
		visibilitySettingConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(thermicPointPanel, visibilitySettingConstraints);
		
		//
		// Homerseklet szammal valo kijelzese szekcio
		//
		row++;
		visibilitySettingConstraints.gridx = 0;
		visibilitySettingConstraints.gridy = row;
		visibilitySettingConstraints.anchor = GridBagConstraints.NORTH;
		visibilitySettingConstraints.weighty = 0;
		visibilitySettingConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(temperatureByFontPanel, visibilitySettingConstraints);
		
		//
		// Hoaram megjelenitese szekcio
		//
		row++;
		visibilitySettingConstraints.gridx = 0;
		visibilitySettingConstraints.gridy = row;
		visibilitySettingConstraints.anchor = GridBagConstraints.NORTH;
		visibilitySettingConstraints.weighty = 0;
		visibilitySettingConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(currentPanel, visibilitySettingConstraints);
		
		//
		// Felfele igazitas
		//
		row++;
		visibilitySettingConstraints.gridx = 0;
		visibilitySettingConstraints.gridy = row;
		visibilitySettingConstraints.anchor = GridBagConstraints.NORTH;
		visibilitySettingConstraints.weighty = 1;
		visibilitySettingConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel(), visibilitySettingConstraints);
		
	}
}
