package hu.akoel.hetram.gui;

import hu.akoel.hetram.drawingelements.BuildingStructureElement;
import hu.akoel.hetram.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.drawingelements.SymmetricEdgeElement;
import hu.akoel.mgu.drawnblock.DrawnBlock;
import hu.akoel.mgu.drawnblock.DrawnBlockFactory;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
import hu.akoel.mgu.drawnblock.example.BuildingMaterialBlock;
import hu.akoel.mgu.drawnblock.example.InsulationBlock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class ElementSettingTab extends JPanel{

	private static final long serialVersionUID = 5151984439858433362L;

	public static enum DRAWING_ELEMENT{
		BUILDINGELEMENT,
		SYMMETRICEDGE,
		OPENEDGE
	}
	
	JRadioButton buildingElementSelector;
	JRadioButton symmetricEdgeSelector;
	JRadioButton openEdgeSelector;
	
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
		// Rajzolando elem - BLOKK
		//
		//----------------------------------
		JPanel drawingElementPanel = new JPanel();
		drawingElementPanel.setLayout( new GridBagLayout() );
		drawingElementPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.black ), "Rajzolandó elem", TitledBorder.LEFT, TitledBorder.TOP ) );		
		GridBagConstraints drawingElementSelectorConstraints = new GridBagConstraints();

		ActionListener drawingElementSelectorActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ( e.getSource() == buildingElementSelector ) {
					ElementSettingTab.this.mainPanel.setDrawingElement( DRAWING_ELEMENT.BUILDINGELEMENT );
				
					DrawnBlockFactory dbf = new BuildingStructureFactory();
					ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );
					
				} else if ( e.getSource() == symmetricEdgeSelector ) {
					ElementSettingTab.this.mainPanel.setDrawingElement( DRAWING_ELEMENT.SYMMETRICEDGE );
					
					DrawnBlockFactory dbf = new SymmetricEdgeFactory();
					ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );
					
				} else if ( e.getSource() == openEdgeSelector ) {
					ElementSettingTab.this.mainPanel.setDrawingElement( DRAWING_ELEMENT.OPENEDGE );
					
					DrawnBlockFactory dbf = new OpenEdgeFactory();
					ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );

				}
			}
		};
		
		//Rajzolando elem kivalasztasa
		ButtonGroup bg = new ButtonGroup();
		buildingElementSelector = new JRadioButton("Épületszerkezeti elem", false );
		buildingElementSelector.addActionListener(drawingElementSelectorActionListener);
		bg.add( buildingElementSelector );
		symmetricEdgeSelector = new JRadioButton("Szimmetria él", false );
		symmetricEdgeSelector.addActionListener(drawingElementSelectorActionListener);
		bg.add( symmetricEdgeSelector );
		openEdgeSelector = new JRadioButton("Szabad felszín", false );
		openEdgeSelector.addActionListener(drawingElementSelectorActionListener);
		bg.add( openEdgeSelector );

		//Default ertek beallitasa
		if( ElementSettingTab.this.mainPanel.getDrawingElement().equals( DRAWING_ELEMENT.BUILDINGELEMENT ) ){
			buildingElementSelector.setSelected( true );
			
			//Code ismetles, de nem tehetek rola, a setselected(true) nem inditja el az actionPerformed() metodust
			DrawnBlockFactory dbf = new BuildingStructureFactory();
			ElementSettingTab.this.mainPanel.setDrawnBlockFactory( dbf );
		}else if( ElementSettingTab.this.mainPanel.getDrawingElement().equals( DRAWING_ELEMENT.SYMMETRICEDGE ) ){
			symmetricEdgeSelector.setSelected( true );
		}else if( ElementSettingTab.this.mainPanel.getDrawingElement().equals( DRAWING_ELEMENT.OPENEDGE ) ){
			openEdgeSelector.setSelected( true );
		}
		
		row = 0;
		
		//1. sor - Rajzolando elem - Epuletszerkezeti elem
		row++;
		drawingElementSelectorConstraints.gridx = 0;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 1;
		drawingElementSelectorConstraints.anchor = GridBagConstraints.WEST;
		drawingElementSelectorConstraints.fill = GridBagConstraints.HORIZONTAL;
		drawingElementPanel.add( buildingElementSelector, drawingElementSelectorConstraints);		
		
		//2. sor - Rajzolando elem - Szimmetria felszin
		row++;
		drawingElementSelectorConstraints.gridx = 0;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 1;
		drawingElementPanel.add( symmetricEdgeSelector, drawingElementSelectorConstraints);		
		
		//3. sor - Rajzolando elem - Szabad felszin
		row++;
		drawingElementSelectorConstraints.gridx = 0;
		drawingElementSelectorConstraints.gridy = row;
		drawingElementSelectorConstraints.gridwidth = 1;
		drawingElementSelectorConstraints.weightx = 1;
		drawingElementPanel.add( openEdgeSelector, drawingElementSelectorConstraints);		

				
		//-----------------------------------
		//
		// Visibility TAB feltoltese
		//
		//-----------------------------------
		
		//
		// Rajzolando elem
		//
		row = 0;
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
	class SymmetricEdgeFactory implements DrawnBlockFactory{

		private DrawnBlock bs;
		
		@Override
		public DrawnBlock getNewDrawnBlock(Status status, double x1, double y1) {
			
			//Ezeket szerzi parameterkent
			Color color = Color.green;
			
			bs = new SymmetricEdgeElement( status , x1, y1, null, null, null, 0.0, color );
			return bs;
		}		
	}
	
	/**
	 * Szabad felszin legyartasat vegzo osztaly
	 * 
	 * @author akoel
	 *
	 */
	class OpenEdgeFactory implements DrawnBlockFactory{

		private DrawnBlock bs;
		
		@Override
		public DrawnBlock getNewDrawnBlock(Status status, double x1, double y1) {
			
			//Ezeket szerzi parameterkent
			double alphaBegin = 8;
			double alphaEnd = 8;
			double temperature = 20;
			Color color = Color.red;
			
			bs = new OpenEdgeElement( status , x1, y1, null, null, null, 0.0, alphaBegin, alphaEnd, temperature, color );
		
			return bs;
		}
	}
	
	/**
	 * Epuletszerkezet legyartasat vegzo osztaly
	 * 
	 * @author akoel
	 *
	 */
	class BuildingStructureFactory implements DrawnBlockFactory{
		
		private final Stroke NORMAL_STROKE = new BasicStroke(1);
		
		private final Color SELECTED_COLOR = Color.red;
		private final Color SELECTED_BACKGROUND = Color.yellow;
		private final Stroke SELECTED_STROKE = new BasicStroke(1);

		//private static final Color INPROCESS_COLOR = Color.yellow;
		//private static final Color INPROCESS_BACKGROUND = Color.gray;
		private final Stroke INPROCESS_STROKE = new BasicStroke(3);
		
		private final Color INFOCUS_COLOR = Color.yellow;
		//private static final Color INFOCUS_BACKGROUND = Color.gray;
		private final Stroke INFOCUS_STROKE = new BasicStroke(1);

		private DrawnBlock bs;
		
		@Override
		public DrawnBlock getNewDrawnBlock( Status status, double x1, double y1 ) {

			//TODO lehet hogy at kellene pakolni a BuildingSturctureElement osztalyba			
			
//A beallitastol fuggoen a megfelelo DrawnBlock-ot hozza letre
//es szinten a beallitasoktol fuggo beallitast vegez

//Ezeket szerzi parameterkent
			double lambda = 0.01;
			Color color = Color.blue;
			Color background = Color.black;
//
			
			//Ha Hatched /// mintazat kell
			
				int patternSize = 15;
			
				TexturePaint normalTexturePaint;
				TexturePaint selectedTexturePaint;
				TexturePaint infocusTexturePaint;
				TexturePaint inprocessTexturePaint;

				Rectangle r = new Rectangle( 0, 0, patternSize, patternSize );
			
				//
				//Normal
				//
				BufferedImage bi1 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
				Graphics2D big1 = bi1.createGraphics();
				big1.setColor( background );
				big1.fillRect( 0, 0, patternSize, patternSize );
				big1.setColor( color ); 
				big1.drawLine( 0, 0, patternSize, patternSize );			 
				normalTexturePaint = new TexturePaint( bi1,r ); 
			
				//
				// Selected
				//
				BufferedImage bi2 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
				Graphics2D big2 = bi2.createGraphics();
				big2.setColor( SELECTED_BACKGROUND );
				big2.fillRect( 0, 0, patternSize, patternSize );
				big2.setColor( SELECTED_COLOR ); 
				big2.drawLine( 0, 0, patternSize, patternSize );			 
				selectedTexturePaint = new TexturePaint( bi2,r ); 
			
				//
				// Infocus
				//
				BufferedImage bi3 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
				Graphics2D big3 = bi3.createGraphics();
				big3.setColor( background );
				big3.fillRect( 0, 0, patternSize, patternSize );
				big3.setColor( INFOCUS_COLOR ); 
				big3.drawLine( 0, 0, patternSize, patternSize );			 
				infocusTexturePaint = new TexturePaint( bi3,r ); 
			
				//
				// Inprocess
				//
				BufferedImage bi4 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
				Graphics2D big4 = bi4.createGraphics();
				big4.setColor( background );
				big4.fillRect( 0, 0, patternSize, patternSize );
				big4.setColor( color ); 
				big4.drawLine( 0, 0, patternSize, patternSize );			 
				inprocessTexturePaint = new TexturePaint( bi4,r ); 
			
				bs = new BuildingStructureElement( status , x1, y1, lambda, color, background );
				bs.setNormal( color, NORMAL_STROKE, normalTexturePaint );
				bs.setSelected( SELECTED_COLOR, SELECTED_STROKE, selectedTexturePaint );
				bs.setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, infocusTexturePaint );
				bs.setInprocess( color, INPROCESS_STROKE, inprocessTexturePaint );
			
			return bs;
		}		
	}
}


