package hu.akoel.hetram.gui.tabs;

import hu.akoel.hetram.HetramDrawnElementFactory;
import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.hetram.gui.PatternSelector;
import hu.akoel.hetram.gui.RawPatternSelectorItem;
import hu.akoel.hetram.gui.drawingelements.ColoredPatternBuildingSturcturalElement;
import hu.akoel.hetram.gui.drawingelements.DotFullPatternAdapter;
import hu.akoel.hetram.gui.drawingelements.HomogeneousPatternBuildingStructuralElement;
import hu.akoel.hetram.gui.drawingelements.HomogeneousPatternFactory;
import hu.akoel.hetram.gui.drawingelements.HatchFullPatternAdapter;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.gui.drawingelements.RowPatternFactory;
import hu.akoel.hetram.gui.drawingelements.RowPatternBuildingStructuralElement;
import hu.akoel.hetram.gui.drawingelements.SymmetricEdgeElement;
import hu.akoel.hetram.gui.drawingelements.ZigZagRowPatternAdapter;
import hu.akoel.mgu.ColorSelector;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ElementSettingTab extends JPanel{

	private static final long serialVersionUID = 5151984439858433362L;

	public static enum DRAWING_ELEMENT{
		BUILDINGELEMENT,
		SYMMETRICEDGE,
		OPENEDGE
	}
	
	public static enum PATTERN_TYPE{
		COLOR,
		HOMOGEN,
		ROW
	}
	
	public static enum HOMOGENEOUS_PATTERN{
		HATCH( new HatchPatternSelectorItem() ),
		DOT( new DotPatternSelectorItem() ),
		;	
		
		RawPatternSelectorItem patternSelectorItem;
		
		HOMOGENEOUS_PATTERN( RawPatternSelectorItem patternSelectorItem ){
			this.patternSelectorItem = patternSelectorItem;
		}
		
		public RawPatternSelectorItem getPatternSelectorItem(){
			return patternSelectorItem;
		}
	}
	
	public static enum ROW_PATTERN{
		ZIGZAG( new ZigZagPatternSelectorItem() );
		
		RawPatternSelectorItem patternSelectorItem;
		
		ROW_PATTERN( RawPatternSelectorItem patternSelectorItem ){
			this.patternSelectorItem = patternSelectorItem;
		}
		
		public RawPatternSelectorItem getPatternSelectorItem(){
			return patternSelectorItem;
		}
	}
	
	JTextField lambdaField;
	ColorSelector lineColorSelector;
	ColorSelector backgroundColorSelector;
	JTextField openEdgeAlphaBeginField;
	JTextField openEdgeAlphaEndField;
	JTextField openEdgeTemperatureField;
	ColorSelector openEdgeColorSelector;
	ColorSelector symmetricEdgeColorSelector;
	
	JToggleButton buildingElementSelector;
	JToggleButton symmetricEdgeSelector;
	JToggleButton openEdgeSelector;
	
	JRadioButton patternTypeColorSelector;
	JRadioButton patternTypeHomogenSelector;
	JRadioButton patternTypeRowSelector;
	
	PatternSelector homogenPatternSelector;
	PatternSelector rowPatternSelector;
	
	private MainPanel mainPanel ;
	
	public ElementSettingTab( MainPanel mainPanel ){
		super();
		
		this.mainPanel = mainPanel;
		
		int row = 0;
		
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout(new GridBagLayout());
		GridBagConstraints drawingElementSettingPanelConstraints = new GridBagConstraints();		
		

				
		
		//----------------------------------
		//
		// Epulet szerkezet - BLOKK
		//
		//----------------------------------
		JPanel buildingStructureElementPanel = new JPanel();
		buildingStructureElementPanel.setLayout( new GridBagLayout() );
		buildingStructureElementPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Épulet szerkezet", TitledBorder.LEFT, TitledBorder.TOP ) );		
		GridBagConstraints buildingStructureElementSelectorConstraints = new GridBagConstraints();
		
		//
		// Lambda erteke
		//
		lambdaField = new JTextField();
		lambdaField.setColumns( 8 );
		lambdaField.setText( String.valueOf( ElementSettingTab.this.mainPanel.getBuildingStructureLambda() ) );
		
		//
		// Rajzolat szin
		//
		lineColorSelector = new ColorSelector();
		lineColorSelector.setSelectedItem( ElementSettingTab.this.mainPanel.getElementLineColor() );
		

		//
		// Kitolto szin
		//
		backgroundColorSelector = new ColorSelector();
		backgroundColorSelector.setSelectedItem( ElementSettingTab.this.mainPanel.getElementBackgroundColor() );
		
		ButtonGroup fillingTypeGroup = new ButtonGroup();
		patternTypeColorSelector = new JRadioButton("Szín", false );
		fillingTypeGroup.add( patternTypeColorSelector );
		patternTypeHomogenSelector = new JRadioButton("Homogén kitöltés", false );
		fillingTypeGroup.add( patternTypeHomogenSelector );
		patternTypeRowSelector = new JRadioButton("Sor kitöltés", false );
		fillingTypeGroup.add( patternTypeRowSelector );
		
		//
		//Letrehozza a Homogen mintazatot valaszto combobox-ot
		//
		homogenPatternSelector = new PatternSelector();
		//Feltolti a combobox-ot az elemekkel
		for( int i = 0; i < HOMOGENEOUS_PATTERN.values().length; i++ ){
			homogenPatternSelector.addItem( HOMOGENEOUS_PATTERN.values()[i].getPatternSelectorItem() );
		}
		//Kivalasztja a default erteket
		homogenPatternSelector.setSelectedItem( ElementSettingTab.this.mainPanel.getHomogenPattern().ordinal() );
		


		//
		//Letrehozza a Row mintazatot valaszto combobox-ot
		//
		rowPatternSelector = new PatternSelector();
		//Feltolti a combobox-ot az elemekkel
		for( int i = 0; i < ROW_PATTERN.values().length; i++ ){
			rowPatternSelector.addItem( ROW_PATTERN.values()[i].getPatternSelectorItem() );
		}
		//Kivalasztja a default erteket
		rowPatternSelector.setSelectedItem( ElementSettingTab.this.mainPanel.getRowPattern().ordinal() );
		
		PATTERN_TYPE patternSelector = ElementSettingTab.this.mainPanel.getPatternType();
		if( patternSelector.equals( PATTERN_TYPE.COLOR ) ){
			patternTypeColorSelector.setSelected(true);			
		}else if( patternSelector.equals( PATTERN_TYPE.HOMOGEN ) ){			
			patternTypeHomogenSelector.setSelected(true);
		}else if( patternSelector.equals( PATTERN_TYPE.ROW ) ){
			patternTypeRowSelector.setSelected(true);		
		}
		
		//1. sor lambda
		buildingStructureElementSelectorConstraints.gridx = 0;
		buildingStructureElementSelectorConstraints.gridy = 0;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( new JLabel("λ: "), buildingStructureElementSelectorConstraints);

		buildingStructureElementSelectorConstraints.gridx = 1;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 1;
		//buildingStructureElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		buildingStructureElementPanel.add( lambdaField, buildingStructureElementSelectorConstraints);
				
		buildingStructureElementSelectorConstraints.gridx = 2;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementPanel.add( new JLabel(" W/mK" ), buildingStructureElementSelectorConstraints );	
		
		//2. rajzolat szin
		buildingStructureElementSelectorConstraints.gridx = 0;
		buildingStructureElementSelectorConstraints.gridy = 1;
		buildingStructureElementSelectorConstraints.gridwidth = 2;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( new JLabel("Rajzolat szín: "), buildingStructureElementSelectorConstraints);
		
		buildingStructureElementSelectorConstraints.gridx = 2;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( lineColorSelector, buildingStructureElementSelectorConstraints);
		
		//3. kitolto szin - Background
		buildingStructureElementSelectorConstraints.gridx = 0;
		buildingStructureElementSelectorConstraints.gridy = 2;
		buildingStructureElementSelectorConstraints.gridwidth = 2;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( new JLabel("Kitöltő szín: "), buildingStructureElementSelectorConstraints);
		
		buildingStructureElementSelectorConstraints.gridx = 2;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( backgroundColorSelector, buildingStructureElementSelectorConstraints);
	
		//4. Minta: szin
		buildingStructureElementSelectorConstraints.gridx = 0;
		buildingStructureElementSelectorConstraints.gridy = 3;
		buildingStructureElementSelectorConstraints.gridwidth = 2;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( patternTypeColorSelector, buildingStructureElementSelectorConstraints);

		//5. Minta: homogen
		buildingStructureElementSelectorConstraints.gridx = 0;
		buildingStructureElementSelectorConstraints.gridy = 4;
		buildingStructureElementSelectorConstraints.gridwidth = 2;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( patternTypeHomogenSelector, buildingStructureElementSelectorConstraints);

		//5. Homogen pattern selector 
		buildingStructureElementSelectorConstraints.gridx = 2;
		buildingStructureElementSelectorConstraints.gridy = 4;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( homogenPatternSelector, buildingStructureElementSelectorConstraints);
		
		//6. Minta: sor
		buildingStructureElementSelectorConstraints.gridx = 0;
		buildingStructureElementSelectorConstraints.gridy = 5;
		buildingStructureElementSelectorConstraints.gridwidth = 2;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( patternTypeRowSelector, buildingStructureElementSelectorConstraints);

		//6. Row pattern selector 
		buildingStructureElementSelectorConstraints.gridx = 2;
		buildingStructureElementSelectorConstraints.gridy = 5;
		buildingStructureElementSelectorConstraints.gridwidth = 1;
		buildingStructureElementSelectorConstraints.weightx = 0;
		buildingStructureElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		buildingStructureElementPanel.add( rowPatternSelector, buildingStructureElementSelectorConstraints);
		
	
		//----------------------------------
		//
		// Szabad felulet - BLOKK
		//
		//----------------------------------
		JPanel openEdgeElementPanel = new JPanel();
		openEdgeElementPanel.setLayout( new GridBagLayout() );
		openEdgeElementPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Szabadfelület", TitledBorder.LEFT, TitledBorder.TOP ) );		
		GridBagConstraints openEdgeElementSelectorConstraints = new GridBagConstraints();

		//
		//AlphaBegin
		//
		openEdgeAlphaBeginField = new JTextField();
		openEdgeAlphaBeginField.setColumns( 8 );
		openEdgeAlphaBeginField.setText( String.valueOf( ElementSettingTab.this.mainPanel.getOpenEdgeAlphaBegin() ) );
		

		
		//
		//AlphaEnd
		//
		openEdgeAlphaEndField = new JTextField();
		openEdgeAlphaEndField.setColumns( 8 );
		openEdgeAlphaEndField.setText( String.valueOf( ElementSettingTab.this.mainPanel.getOpenEdgeAlphaEnd() ) );
		

		//Temperature
		openEdgeTemperatureField = new JTextField();
		openEdgeTemperatureField.setColumns( 8 );
		openEdgeTemperatureField.setText( String.valueOf( ElementSettingTab.this.mainPanel.getOpenEdgeTemperature() ) );
		

		//Color
		openEdgeColorSelector = new ColorSelector();
		openEdgeColorSelector.setSelectedItem( ElementSettingTab.this.mainPanel.getOpenEdgeColor() );

		
		row = 0;
		//1. sor alpha begin
		openEdgeElementSelectorConstraints.gridx = 0;
		openEdgeElementSelectorConstraints.gridy = row;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		openEdgeElementPanel.add( new JLabel("α kezdő: "), openEdgeElementSelectorConstraints);

		openEdgeElementSelectorConstraints.gridx = 1;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 1;
		//buildingStructureElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		openEdgeElementPanel.add( openEdgeAlphaBeginField, openEdgeElementSelectorConstraints);
				
		openEdgeElementSelectorConstraints.gridx = 2;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementPanel.add( new JLabel(" W/m²K" ), openEdgeElementSelectorConstraints );	
		
		//2. sor alpha end
		row++;
		openEdgeElementSelectorConstraints.gridx = 0;
		openEdgeElementSelectorConstraints.gridy = row;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		openEdgeElementPanel.add( new JLabel("α vég: "), openEdgeElementSelectorConstraints);

		openEdgeElementSelectorConstraints.gridx = 1;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 1;
		//buildingStructureElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		openEdgeElementPanel.add( openEdgeAlphaEndField, openEdgeElementSelectorConstraints);
				
		openEdgeElementSelectorConstraints.gridx = 2;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementPanel.add( new JLabel(" W/m²K" ), openEdgeElementSelectorConstraints );	
		
		//3. sor T
		row++;
		openEdgeElementSelectorConstraints.gridx = 0;
		openEdgeElementSelectorConstraints.gridy = row;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		openEdgeElementPanel.add( new JLabel("T: "), openEdgeElementSelectorConstraints);

		openEdgeElementSelectorConstraints.gridx = 1;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 1;
		//buildingStructureElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		openEdgeElementPanel.add( openEdgeTemperatureField, openEdgeElementSelectorConstraints);
				
		openEdgeElementSelectorConstraints.gridx = 2;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementPanel.add( new JLabel(" °C" ), openEdgeElementSelectorConstraints );	
		
		//4. sor Color
		row++;
		openEdgeElementSelectorConstraints.gridx = 0;
		openEdgeElementSelectorConstraints.gridy = row;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 0;
		openEdgeElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		openEdgeElementPanel.add( new JLabel("Szín: "), openEdgeElementSelectorConstraints);

		openEdgeElementSelectorConstraints.gridx = 1;
		openEdgeElementSelectorConstraints.gridwidth = 1;
		openEdgeElementSelectorConstraints.weightx = 1;
		//openEdgeElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		openEdgeElementPanel.add( openEdgeColorSelector, openEdgeElementSelectorConstraints);
				
		//----------------------------------
		//
		// Szimmetria el - BLOKK
		//
		//----------------------------------
		JPanel symmetricEdgeElementPanel = new JPanel();
		symmetricEdgeElementPanel.setLayout( new GridBagLayout() );
		symmetricEdgeElementPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Szimmetria él", TitledBorder.LEFT, TitledBorder.TOP ) );		
		GridBagConstraints symmetricEdgeElementSelectorConstraints = new GridBagConstraints();

		//Color
		symmetricEdgeColorSelector = new ColorSelector();
		symmetricEdgeColorSelector.setSelectedItem( ElementSettingTab.this.mainPanel.getSymmetricEdgeColor() );
				
		row = 0;
		//1. sor alpha begin
		symmetricEdgeElementSelectorConstraints.gridx = 0;
		symmetricEdgeElementSelectorConstraints.gridy = row;
		symmetricEdgeElementSelectorConstraints.gridwidth = 1;
		symmetricEdgeElementSelectorConstraints.weightx = 0;
		symmetricEdgeElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		symmetricEdgeElementPanel.add( new JLabel("Szín:       "), symmetricEdgeElementSelectorConstraints );

		symmetricEdgeElementSelectorConstraints.gridx = 1;
		symmetricEdgeElementSelectorConstraints.gridwidth = 1;
		symmetricEdgeElementSelectorConstraints.weightx = 1;
		//symmetricEdgeElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		symmetricEdgeElementPanel.add( symmetricEdgeColorSelector, symmetricEdgeElementSelectorConstraints);
		
		//----------------------------------
		//
		// Rajzolando elem - BLOKK
		//
		//----------------------------------
		JPanel drawingElementPanel = new JPanel();
		drawingElementPanel.setLayout( new GridBagLayout() );
		GridBagConstraints drawingElementSelectorConstraints = new GridBagConstraints();
		
		ChangeListener drawingElementSelectorChangeListener = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				AbstractButton aButton = (AbstractButton)e.getSource();
				ButtonModel aModel = aButton.getModel();
				boolean selected = aModel.isSelected();
				HetramDrawnElementFactory dbf;
				
		        if( selected ){
				
		        	if ( aButton.equals( buildingElementSelector ) ) {		        
		        		ElementSettingTab.this.mainPanel.setDrawingElement( DRAWING_ELEMENT.BUILDINGELEMENT );
						
						dbf = new BuildingStructureFactory();
						ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );
		        	
		        	} else if ( e.getSource().equals( symmetricEdgeSelector ) ) {
						ElementSettingTab.this.mainPanel.setDrawingElement( DRAWING_ELEMENT.SYMMETRICEDGE );
						
						dbf = new SymmetricEdgeFactory();
						ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );
						
					} else if ( e.getSource().equals( openEdgeSelector ) ){
						ElementSettingTab.this.mainPanel.setDrawingElement( DRAWING_ELEMENT.OPENEDGE );
						
						dbf = new OpenEdgeFactory();
						ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );
					}
		        }
			}			
		};
	
		//Rajzolando elem kivalasztasa
		ButtonGroup bg = new ButtonGroup();
		buildingElementSelector = new JToggleButton("Épületszerkezeti elem", false );
		buildingElementSelector.addChangeListener(drawingElementSelectorChangeListener);
		bg.add( buildingElementSelector );		
		openEdgeSelector = new JToggleButton("Szabad felület", false );
		openEdgeSelector.addChangeListener(drawingElementSelectorChangeListener);
		bg.add( openEdgeSelector );
		symmetricEdgeSelector = new JToggleButton("Szimmetria él", false );
		symmetricEdgeSelector.addChangeListener(drawingElementSelectorChangeListener);
		bg.add( symmetricEdgeSelector );

		//Default ertek beallitasa
		if( ElementSettingTab.this.mainPanel.getDrawingElement().equals( DRAWING_ELEMENT.BUILDINGELEMENT ) ){
			buildingElementSelector.setSelected( true );
		}else if( ElementSettingTab.this.mainPanel.getDrawingElement().equals( DRAWING_ELEMENT.SYMMETRICEDGE ) ){
			symmetricEdgeSelector.setSelected( true );
		}else if( ElementSettingTab.this.mainPanel.getDrawingElement().equals( DRAWING_ELEMENT.OPENEDGE ) ){
			openEdgeSelector.setSelected( true );
		}
		
		
		//--------------------------------------------------------
		//
		// Most jonnek az elemek figyelo interface-enek hozzaadasa
		// mert kulonben minden default ertek kivalasztasanal
		// megnyomna a gombokat, amik meg nem is leteznek
		//
		//--------------------------------------------------------
		
		//
		// Rajzi elemek
		//
		
		//Lambda ertek
		lambdaField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( ElementSettingTab.this.mainPanel.getBuildingStructureLambda() );
			
			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField)input;
	            String possibleValue = text.getText();
	            try{
	            	Double.valueOf(possibleValue);
	            	goodValue = possibleValue;
	            }catch(NumberFormatException e){
	            	text.setText(goodValue);
	            	return false;
	            }
	            ElementSettingTab.this.mainPanel.setBuildingStructureLambda( Double.valueOf( goodValue ) );
	            
	            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            buildingElementSelector.doClick();
	            
	            return true;
			}
		});
		
		//Rajzolat szin
		lineColorSelector.addActionListener( new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ColorSelector cs = (ColorSelector) e.getSource();
				ElementSettingTab.this.mainPanel.setElementLineColor( cs.getSelectedColor() );
				
	            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            buildingElementSelector.doClick();

			}
			
		});
		
		//Kitoltoszin szin
		backgroundColorSelector.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ColorSelector cs = (ColorSelector) e.getSource();
				ElementSettingTab.this.mainPanel.setElementBackgroundColor( cs.getSelectedColor() );
				
	            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            buildingElementSelector.doClick();
				
			}
		});
		
		//Homogen kitolto szelektor
		homogenPatternSelector.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				PatternSelector ps = (PatternSelector)e.getSource();
				ElementSettingTab.this.mainPanel.setHomogenPattern( HOMOGENEOUS_PATTERN.values()[ ps.getSelectedIndex() ] );
				
	            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            buildingElementSelector.doClick();
				
			}
		});
		
		//Sorkitoltes szelektor
		rowPatternSelector.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				PatternSelector ps = (PatternSelector)e.getSource();
				ElementSettingTab.this.mainPanel.setRowPattern( ROW_PATTERN.values()[ ps.getSelectedIndex() ] );
			
	            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            buildingElementSelector.doClick();

			}
		});
		
		// Szin radio button
		patternTypeColorSelector.addChangeListener( new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				
				AbstractButton aButton = (AbstractButton)e.getSource();
		        ButtonModel aModel = aButton.getModel();
		        //boolean armed = aModel.isArmed();
		        //boolean pressed = aModel.isPressed();
		        boolean selected = aModel.isSelected();
		        
		        if( selected ){
		        	ElementSettingTab.this.mainPanel.setPatternType( PATTERN_TYPE.COLOR );
		        	homogenPatternSelector.setEnabled( false );
		        	rowPatternSelector.setEnabled( false );
		        	
		            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
		            buildingElementSelector.doClick();

		        }
			}			
		});
		
		// Homogen kitoltes radio button
		patternTypeHomogenSelector.addChangeListener( new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				
		        ButtonModel aModel = ((AbstractButton)e.getSource()).getModel();
		        if( aModel.isSelected() ){		        
		        	ElementSettingTab.this.mainPanel.setPatternType( PATTERN_TYPE.HOMOGEN );
		        	homogenPatternSelector.setEnabled( true );
		        	rowPatternSelector.setEnabled( false );
		        	
		            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
		            buildingElementSelector.doClick();

		        }
			}			
		});
		
		// Sor kitoltes radiobutton
		patternTypeRowSelector.addChangeListener( new ChangeListener(){
			
			@Override
			public void stateChanged(ChangeEvent e) {
		        ButtonModel aModel = ((AbstractButton)e.getSource()).getModel();
		        if( aModel.isSelected() ){		        
		        	ElementSettingTab.this.mainPanel.setPatternType( PATTERN_TYPE.ROW );
		        	homogenPatternSelector.setEnabled( false );
		        	rowPatternSelector.setEnabled( true );
		        	
		            //Kivalasztja az Epuletszerkezet rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
		            buildingElementSelector.doClick();

		        }
			}			
		});
		
		
		//
		// Szabad felulet
		//
		
		//Alpha begin
		openEdgeAlphaBeginField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( ElementSettingTab.this.mainPanel.getOpenEdgeAlphaBegin() );
			
			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField)input;
	            String possibleValue = text.getText();
	            try{
	            	Double.valueOf(possibleValue);
	            	goodValue = possibleValue;
	            }catch(NumberFormatException e){
	            	text.setText(goodValue);
	            	return false;
	            }
	            ElementSettingTab.this.mainPanel.setAlphaBegin( Double.valueOf( goodValue ) );
	            //ElementSettingTab.this.mainPanel.revalidateAndRepaint();
	            
	            //Kivalasztja a Szabad felulet-et rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            openEdgeSelector.doClick();

	            return true;
			}
		});
		
		//Alpha end
		openEdgeAlphaEndField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( ElementSettingTab.this.mainPanel.getOpenEdgeAlphaEnd() );
			
			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField)input;
	            String possibleValue = text.getText();
	            try{
	            	Double.valueOf(possibleValue);
	            	goodValue = possibleValue;
	            }catch(NumberFormatException e){
	            	text.setText(goodValue);
	            	return false;
	            }
	            ElementSettingTab.this.mainPanel.setAlphaEnd( Double.valueOf( goodValue ) );
	            //ElementSettingTab.this.mainPanel.revalidateAndRepaint();

	            //Kivalasztja a Szabad felulet-et rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            openEdgeSelector.doClick();
	            
	            return true;
			}
		});
		
		//Temperature
		openEdgeTemperatureField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( ElementSettingTab.this.mainPanel.getOpenEdgeTemperature() );
			
			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField)input;
	            String possibleValue = text.getText();
	            try{
	            	Double.valueOf(possibleValue);
	            	goodValue = possibleValue;
	            }catch(NumberFormatException e){
	            	text.setText(goodValue);
	            	return false;
	            }
	            ElementSettingTab.this.mainPanel.setOpenEdgeTemperature( Double.valueOf( goodValue ) );
	            //ElementSettingTab.this.mainPanel.revalidateAndRepaint();

	            //Kivalasztja a Szabad felulet-et rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            openEdgeSelector.doClick();
	            
	            return true;
			}
		});
		
		//Color
		openEdgeColorSelector.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ColorSelector cs = (ColorSelector) e.getSource();
				ElementSettingTab.this.mainPanel.setOpenEdgeColor( cs.getSelectedColor() );
				
				 //Kivalasztja a Szabad felulet-et rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            openEdgeSelector.doClick();
	            
			}
		});
		
		//
		// Szimmetria el
		//
		
		//Color
		symmetricEdgeColorSelector.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ColorSelector cs = (ColorSelector) e.getSource();
				ElementSettingTab.this.mainPanel.setSymmetricEdgeColor( cs.getSelectedColor() );
				
				 //Kivalasztja a Szabad felulet-et rajzolo gombot, hiszen annak  a tulajdonsagait valtoztattuak
	            symmetricEdgeSelector.doClick();
	            
			}
		});
		
		
		//
		//-----------------------------------------------------
		//
		
		
		
		
		//1. sor - Rajzolando elem - Epuletszerkezeti elem
		row = 0;
		drawingElementSelectorConstraints.gridx = 0;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 1;
		drawingElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		drawingElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		drawingElementPanel.add( new JLabel( ), drawingElementSelectorConstraints);
		
		drawingElementSelectorConstraints.gridx = 1;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 0;
		drawingElementSelectorConstraints.anchor = GridBagConstraints.CENTER;
		drawingElementSelectorConstraints.fill = GridBagConstraints.NONE;
		drawingElementPanel.add( buildingElementSelector, drawingElementSelectorConstraints);		
		
		drawingElementSelectorConstraints.gridx = 2;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 1;
		drawingElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		drawingElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		drawingElementPanel.add( new JLabel( ), drawingElementSelectorConstraints);		
		
		//2. sor - Rajzolando elem - Szabad felszin
		row++;
		drawingElementSelectorConstraints.gridx = 1;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 0;
		drawingElementSelectorConstraints.anchor = GridBagConstraints.CENTER;
		drawingElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		drawingElementPanel.add( openEdgeSelector, drawingElementSelectorConstraints);		

		//3. sor - Rajzolando elem - Szimmetria felszin
		row++;
		drawingElementSelectorConstraints.gridx = 1;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 0;
		drawingElementSelectorConstraints.anchor = GridBagConstraints.CENTER;
		drawingElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		drawingElementPanel.add( symmetricEdgeSelector, drawingElementSelectorConstraints);				
		//-----------------------------------
		//
		// Rajzolo elemek TAB feltoltese
		//
		//-----------------------------------

		row = 0;

		//
		// Epuletszerkezet
		//
		row++;
		drawingElementSettingPanelConstraints.gridx = 0;
		drawingElementSettingPanelConstraints.gridy = row;
		drawingElementSettingPanelConstraints.anchor = GridBagConstraints.NORTH;
		drawingElementSettingPanelConstraints.weighty = 0;
		drawingElementSettingPanelConstraints.weightx = 1;
		drawingElementSettingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( buildingStructureElementPanel, drawingElementSettingPanelConstraints );
		
		//
		// Szabad felulet
		//
		row++;
		drawingElementSettingPanelConstraints.gridx = 0;
		drawingElementSettingPanelConstraints.gridy = row;
		drawingElementSettingPanelConstraints.anchor = GridBagConstraints.NORTH;
		drawingElementSettingPanelConstraints.weighty = 0;
		drawingElementSettingPanelConstraints.weightx = 1;
		drawingElementSettingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( openEdgeElementPanel, drawingElementSettingPanelConstraints );

		//
		// Szimmetria el
		//
		row++;
		drawingElementSettingPanelConstraints.gridx = 0;
		drawingElementSettingPanelConstraints.gridy = row;
		drawingElementSettingPanelConstraints.anchor = GridBagConstraints.NORTH;
		drawingElementSettingPanelConstraints.weighty = 0;
		drawingElementSettingPanelConstraints.weightx = 1;
		drawingElementSettingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add( symmetricEdgeElementPanel, drawingElementSettingPanelConstraints );

		//
		// Rajzolando elem
		//
		row++;
		drawingElementSettingPanelConstraints.gridx = 0;
		drawingElementSettingPanelConstraints.gridy = row;
		drawingElementSettingPanelConstraints.anchor = GridBagConstraints.NORTH;
		drawingElementSettingPanelConstraints.weighty = 0;
		drawingElementSettingPanelConstraints.weightx = 1;
		drawingElementSettingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(drawingElementPanel, drawingElementSettingPanelConstraints);
		
		//
		// Felfele igazitas
		//
		row++;
		drawingElementSettingPanelConstraints.gridx = 0;
		drawingElementSettingPanelConstraints.gridy = row;
		drawingElementSettingPanelConstraints.anchor = GridBagConstraints.NORTH;
		drawingElementSettingPanelConstraints.weighty = 1;
		drawingElementSettingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel(), drawingElementSettingPanelConstraints);
		
	}
	

	
	/**
	 * Szimmetria oldal legyartasat vegzo osztaly
	 * 
	 * @author akoel
	 *
	 */
	class SymmetricEdgeFactory implements HetramDrawnElementFactory{

		private HetramDrawnElement bs;
		
		@Override
		public HetramDrawnElement getNewDrawnBlock(Status status, BigDecimal x1, BigDecimal y1) {
			
			//Ezeket szerzi parameterkent
			Color color = ElementSettingTab.this.mainPanel.getSymmetricEdgeColor();
			
			bs = new SymmetricEdgeElement( status , x1, y1, null, null, null, new BigDecimal(0.0), color );
			return bs;
		}		
	}
	
	/**
	 * Szabad felszin legyartasat vegzo osztaly
	 * 
	 * @author akoel
	 *
	 */
	class OpenEdgeFactory implements HetramDrawnElementFactory{

		private HetramDrawnElement bs;
		
		@Override
		public HetramDrawnElement getNewDrawnBlock(Status status, BigDecimal x1, BigDecimal y1) {
			
			//Ezeket szerzi parameterkent
			double alphaBegin = ElementSettingTab.this.mainPanel.getOpenEdgeAlphaBegin();
			double alphaEnd = ElementSettingTab.this.mainPanel.getOpenEdgeAlphaEnd();
			double temperature = ElementSettingTab.this.mainPanel.getOpenEdgeTemperature();
			Color color = ElementSettingTab.this.mainPanel.getOpenEdgeColor();
			
			bs = new OpenEdgeElement( status , x1, y1, null, null, null, new BigDecimal(0.0), alphaBegin, alphaEnd, temperature, color );
		
			return bs;
		}
	}
	
	/**
	 * Epuletszerkezet legyartasat vegzo osztaly
	 * 
	 * @author akoel
	 *
	 */
	class BuildingStructureFactory implements HetramDrawnElementFactory{

		private HetramDrawnElement bs;
		
		@Override
		public HetramDrawnElement getNewDrawnBlock( Status status, BigDecimal x1, BigDecimal y1 ) {

			//TODO lehet hogy at kellene pakolni a BuildingSturctureElement osztalyba			

			//A szerkezeti elem minimalis merete, mellyel le lehet helyezni
			BigDecimal minimalSize = new BigDecimal("1E-" + String.valueOf( ElementSettingTab.this.mainPanel.getCanvas().getPrecision().getScale() ) );
			
			//Parameterek megszerzese
			double lambda = ElementSettingTab.this.mainPanel.getBuildingStructureLambda();
			Color color = ElementSettingTab.this.mainPanel.getElementLineColor();
			Color background = ElementSettingTab.this.mainPanel.getElementBackgroundColor();

			PATTERN_TYPE patternSelector = ElementSettingTab.this.mainPanel.getPatternType();
			if( patternSelector.equals( PATTERN_TYPE.COLOR ) ){
				bs = new ColoredPatternBuildingSturcturalElement( status, x1, y1, minimalSize, null, minimalSize, null, lambda, color, background );
			}else if( patternSelector.equals( PATTERN_TYPE.HOMOGEN ) ){	
				HOMOGENEOUS_PATTERN homogenPattern = ElementSettingTab.this.mainPanel.getHomogenPattern();
				HomogeneousPatternFactory homogeneousPatternFactory = null;
				if( homogenPattern.equals( HOMOGENEOUS_PATTERN.HATCH ) ){
					homogeneousPatternFactory = new HomogeneousPatternFactory( new HatchFullPatternAdapter() );
				}else if( homogenPattern.equals( HOMOGENEOUS_PATTERN.DOT ) ){
					homogeneousPatternFactory = new HomogeneousPatternFactory( new DotFullPatternAdapter() );
				}
				
				bs = new HomogeneousPatternBuildingStructuralElement( homogeneousPatternFactory, status, x1, y1, minimalSize, null, minimalSize, null, lambda, color, background );
			}else if( patternSelector.equals( PATTERN_TYPE.ROW ) ){
				ROW_PATTERN rowPattern = ElementSettingTab.this.mainPanel.getRowPattern();
				RowPatternFactory rowPatternFactory = null;
				if( rowPattern.equals( ROW_PATTERN.ZIGZAG ) ){
					rowPatternFactory = new RowPatternFactory( new ZigZagRowPatternAdapter() );
				}
				
				bs = new RowPatternBuildingStructuralElement( rowPatternFactory, mainPanel, status, x1, y1, minimalSize, null, minimalSize, null, lambda, color, background);		
			}

			return bs;
		}		
	}
	
	public void setStructureElementLambda( double lambda ){
		//Csak ezzel a jatekkal lehet elerni, hogy a setText() metodussal a JTextField-be irt adatra lefusson a verifikacio es igy az ertek valtozoba irasa
		//lambdaField.setFocusable(true);
		lambdaField.requestFocusInWindow();
		lambdaField.setText( String.valueOf( lambda ) );
		mainPanel.getCanvas().requestFocusInWindow();
	}
	
	public void setStructureElementLineColor( Color color ){
		lineColorSelector.setSelectedItem(color);
	}

	public void setStructureElementBackgroundColor( Color color ){
		backgroundColorSelector.setSelectedItem(color);
	}

	public void selectPatternTypeColorSelector(){
		patternTypeColorSelector.setSelected(true);
	}

	public void selectPatternTypeHomogenSelector(){
		patternTypeHomogenSelector.setSelected(true);
	}
	
	public void selectPatternTypeRowSelector(){
		patternTypeRowSelector.setSelected(true);
	}
		
	public void selectHomogeneusPattern(HOMOGENEOUS_PATTERN pattern){
		homogenPatternSelector.setSelectedItem( pattern.ordinal() );
	}
	
	public void selectRowPattern( ROW_PATTERN pattern ){
		rowPatternSelector.setSelectedItem( pattern.ordinal() );
	}
	
	public void setOpenEdgeAlphaBegin( double alpha ){
		//Csak ezzel a jatekkal lehet elerni, hogy a setText() metodussal a JTextField-be irt adatra lefusson a verifikacio es igy az ertek valtozoba irasa
		openEdgeAlphaBeginField.requestFocusInWindow();
		openEdgeAlphaBeginField.setText( String.valueOf( alpha ));
		mainPanel.getCanvas().requestFocusInWindow();
	}

	public void setOpenEdgeAlphaEnd( double alpha ){
		//Csak ezzel a jatekkal lehet elerni, hogy a setText() metodussal a JTextField-be irt adatra lefusson a verifikacio es igy az ertek valtozoba irasa
		openEdgeAlphaEndField.requestFocusInWindow();
		openEdgeAlphaEndField.setText( String.valueOf( alpha ) );
		mainPanel.getCanvas().requestFocusInWindow();

	}
	
	public void setOpenEdgeTemperature( double temperature ){
		//Csak ezzel a jatekkal lehet elerni, hogy a setText() metodussal a JTextField-be irt adatra lefusson a verifikacio es igy az ertek valtozoba irasa
		openEdgeTemperatureField.requestFocusInWindow();
		openEdgeTemperatureField.setText( String.valueOf( temperature ));
		mainPanel.getCanvas().requestFocusInWindow();
	}

	public void setOpenEdgeColor( Color color ){
		openEdgeColorSelector.setSelectedItem( color );
	}
	
	public void setSymmetricEdgeColor( Color color ){
		symmetricEdgeColorSelector.setSelectedItem( color );
	}	
	
}


class HatchPatternSelectorItem implements RawPatternSelectorItem{

	@Override
	public void drawImageIcon( Graphics2D g2, int width, int height ) {
		int divider = 3;				
		double growth = width / divider;
		
		for( int i = 0; i <= divider; i++ ){
		
			int xPos = (int)( i * growth );
		
			g2.drawLine( xPos, 0, (int)(xPos + growth), height);
		
		}
	}			
}

class DotPatternSelectorItem implements RawPatternSelectorItem{

	@Override
	public void drawImageIcon( Graphics2D g2, int width, int height ) {
		int divider = 7;				
		double growth = width / divider;
		
		for( double i = 0; i < width; i += growth ){
			
			for( double j = 0; j < height; j += growth ){

				g2.drawLine( (int)i, (int)j, (int)i, (int)j);

			}
		}
	}			
}

class ZigZagPatternSelectorItem implements RawPatternSelectorItem{

	@Override
	public void drawImageIcon( Graphics2D g2, int width, int height ) {
		int divider = 3;				
		double growth = width / divider;
		
		for( int i = 0; i <= divider; i++ ){
			
			int xPos = (int)( i * growth );
		
			g2.drawLine( xPos, 0, (int)(xPos + growth/2), height);
			g2.drawLine( (int)(xPos + growth), 0, (int)(xPos + growth/2), height);
		
		}
	}			
}

