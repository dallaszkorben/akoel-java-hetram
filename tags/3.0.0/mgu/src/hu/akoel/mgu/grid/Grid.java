package hu.akoel.mgu.grid;

import hu.akoel.mgu.ColorSelector;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.MCanvas.Level;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.Value;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class Grid {
	
	public static enum Type{
		SOLID,
		DASHED,
		CROSS,
		DOT,
	}
	
	public static enum PainterPosition{
		DEEPEST,
		MIDDLE,
		HIGHEST
	}
	
	private MCanvas canvas;
	private Type type;
	private Color color;
	private int widthInPixel;
	private PainterPosition painterPosition;
	private Value deltaGrid;
	private int crossLengthInPixel = 3;
	private PainterListener painterListener;
	
	private boolean defaultOn = true;

	public Grid( MCanvas canvas, Type type, Color color, int widthInPixel, PainterPosition painterPosition, Value deltaGrid ){
		common( canvas, type, color, widthInPixel, painterPosition, deltaGrid );
		
	}
	
	public Grid( MCanvas canvas, Type type, Color color, int widthInPixel, PainterPosition painterPosition, Value deltaGrid, boolean defaultOn ){
		common( canvas, type, color, widthInPixel, painterPosition, deltaGrid );
		this.defaultOn = defaultOn; 
	}
	
	private void common( MCanvas canvas, Type type, Color color, int widthInPixel, PainterPosition painterPosition, Value deltaGrid ){
		this.canvas = canvas;
		this.type = type;
		this.color = color;
		this.widthInPixel = widthInPixel;
		this.painterPosition = painterPosition;
		this.deltaGrid = deltaGrid;
		
		painterListener = new GridPainterListener( );
		
	}
	
	public void turnOff(){
		if( painterPosition.equals(PainterPosition.DEEPEST ) ){
			canvas.removePainterListenerFromDeepest(painterListener);
		}else if( painterPosition.equals( PainterPosition.MIDDLE)){
			canvas.removePainterListenerFromMiddle(painterListener);
		}else if( painterPosition.equals(PainterPosition.HIGHEST)){
			canvas.removePainterListenerFromHighest(painterListener);
		}
	}
	
	public void turnOn(){
		if( painterPosition.equals(PainterPosition.DEEPEST ) ){
			canvas.addPainterListenerToDeepest(painterListener, Level.ABOVE);
		}else if( painterPosition.equals( PainterPosition.MIDDLE)){
			canvas.addPainterListenerToMiddle(painterListener, Level.ABOVE);
		}else if( painterPosition.equals(PainterPosition.HIGHEST)){
			canvas.addPainterListenerToHighest(painterListener, Level.ABOVE);
		}	
	}
	
	public void setCrossLengthInPixel( int crossLengthInPixel ){
		this.crossLengthInPixel = crossLengthInPixel;
	}
	
/*	public void setDeltaGrid( Position deltaGrid ){
		this.deltaGrid = deltaGrid;
	}
*/
	
	public double getDeltaGridX(){
		return this.deltaGrid.getX();
	}

	public double getDeltaGridY(){
		return this.deltaGrid.getY();
	}

	public void setDeltaGridX( double deltaGridX ){
		this.deltaGrid.setX( deltaGridX );
	}
	
	public void setDeltaGridY( double deltaGridY ){
		this.deltaGrid.setY( deltaGridY );
	}
	
	public void setWidthInPixel( int widthInPixel ){
		this.widthInPixel = widthInPixel;
	}
	
	public void setColor( Color color ){
		this.color = color;
	}
	
	public void setType( Type type ){
		this.type = type;
	}
	
	class GridPainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			int n, m;
			double xStart, yStart;
			double yPosition, xPosition;
			double crossXLength, crossYLength;
			
			SizeValue size = canvas.getWorldSize();
			
			xStart = (int)(size.getXMin() / deltaGrid.getX()) * deltaGrid.getX();
			yStart = (int)(size.getYMin() / deltaGrid.getY()) * deltaGrid.getY();				
			
			g2.setColor( color );
			g2.setStroke(new BasicStroke(widthInPixel));
			
			if(type.equals( Type.SOLID ) || type.equals( Type.DASHED ) ){
				
				if( type.equals( Type.DASHED )){
					g2.setStroke( new BasicStroke( widthInPixel, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1,4}, 0) );
				}
				
				m = 1;
				xPosition = xStart;
				while ( xPosition <= size.getXMax() ){
					g2.drawLine(xPosition, size.getYMin(), xPosition, size.getYMax());
					xPosition = xStart + m * deltaGrid.getX(); //!!!!!!!!
		           m++;
		         }

		         n = 1;
		         yPosition = yStart;
		         while ( yPosition <= size.getYMax() ){
		           g2.drawLine( size.getXMin(), yPosition, size.getXMax(), yPosition );
		           yPosition = yStart + n * deltaGrid.getY(); //!!!!!!!!
		           n++;
		         }
		         
			}else{
				if(type.equals( Type.CROSS ) ){
					crossXLength = crossLengthInPixel / canvas.getPixelPerUnit().getX();
					crossYLength = crossLengthInPixel / canvas.getPixelPerUnit().getY();
				}else{
					crossXLength = 0;
					crossYLength = 0;
				}
		        
		    	 n = 1;
		    	 xPosition = xStart;
		    	 while (xPosition <= size.getXMax() ){
		    		 m = 1;
		    		 yPosition = yStart;
		    		 while (yPosition <= size.getYMax() ){
		    			 g2.drawLine( xPosition - crossXLength, yPosition, xPosition + crossXLength, yPosition );
		    			 g2.drawLine( xPosition, yPosition - crossYLength, xPosition, yPosition + crossYLength );

		    			 yPosition = yStart + m * deltaGrid.getY() ; //!!!!!!!!
		    			 m++;
		    		 }//while
		    		 
		    		 xPosition = xStart + n * deltaGrid.getX(); //!!!!!!!!
		    		 n++;
		    	 }//while
			}
			
		}

		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {	}
		
	}
	
	public JPanel getControl( Scale scale ){
		JTextField gridXDeltaField;
		JTextField gridYDeltaField;
		
		final JComboBox<String> gridTypeCombo;
		final JComboBox<String> gridWidthCombo;
//		JComboBox<String> crossLineWidthCombo;
		
		ColorSelector colorSelector = new ColorSelector();
		colorSelector.setSelectedItem( color );
		colorSelector.addActionListener( new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ColorSelector cs = (ColorSelector) e.getSource();
				//ElementSettingTab.this.mainPanel.setElementLineColor( cs.getSelectedColor() );
				color = cs.getSelectedColor();
				Grid.this.canvas.revalidateAndRepaintCoreCanvas();
			}
			
		});

		
		gridXDeltaField = new JTextField();
		gridXDeltaField.setColumns( 8 );
		gridXDeltaField.setText( String.valueOf( this.getDeltaGridX() ) );
		gridXDeltaField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( Grid.this.getDeltaGridX() );
			
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
	            Grid.this.setDeltaGridX( Double.valueOf( goodValue ) );
	            Grid.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
		
		gridYDeltaField = new JTextField();
		gridYDeltaField.setColumns( 8 );
		gridYDeltaField.setText( String.valueOf( Grid.this.getDeltaGridY() ) );
		gridYDeltaField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( Grid.this.getDeltaGridY() );
			
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
	            Grid.this.setDeltaGridY( Double.valueOf(goodValue));
	            Grid.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
		
		String[] gridWidthElements = { "1", "3" };
		gridWidthCombo = new JComboBox<String>(gridWidthElements);
		gridWidthCombo.setSelectedIndex(0);
		gridWidthCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();
				
				if( cmbType.equals( "1")){					
					Grid.this.setWidthInPixel(1);
				}else if( cmbType.equals( "3")){
					Grid.this.setWidthInPixel(3);
				}
				Grid.this.canvas.revalidateAndRepaintCoreCanvas();
			}
		});		
		
		String[] gridTypeElements = { "Solid", "Dashed", "Cross", "Dot" };
		gridTypeCombo = new JComboBox<String>(gridTypeElements);
		gridTypeCombo.setSelectedIndex(3);
		gridTypeCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();
				
				if( cmbType.equals( "Solid")){
					Grid.this.setType( Grid.Type.SOLID );
				}else if( cmbType.equals( "Dashed")){
					Grid.this.setType( Grid.Type.DASHED );
				}else if( cmbType.equals( "Cross")){
					Grid.this.setType( Grid.Type.CROSS );
				}else if( cmbType.equals( "Dot")){
					Grid.this.setType( Grid.Type.DOT );
				}
				Grid.this.canvas.revalidateAndRepaintCoreCanvas();
			}
		});
		
		JCheckBox turnOnGrid = new JCheckBox("Turn On Grid");
		turnOnGrid.setSelected( defaultOn );
		if( defaultOn ){
			turnOn();
		}else{
			turnOff();
		}
		
		turnOnGrid.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					Grid.this.turnOff();
					gridTypeCombo.setEnabled(false);
					gridWidthCombo.setEnabled(false);
				}else{
					Grid.this.turnOn();
					gridTypeCombo.setEnabled(true);
					gridWidthCombo.setEnabled(true);
				}
				Grid.this.canvas.repaint();
			}
		});
		
		
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridBagLayout());
		gridPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Grid", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints gridPanelConstraints = new GridBagConstraints();
		
		//1. sor - Turn on grid
		gridPanelConstraints.gridx = 0;
		gridPanelConstraints.gridy = 0;
		gridPanelConstraints.gridwidth = 4;
		gridPanelConstraints.anchor = GridBagConstraints.WEST;
		gridPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(turnOnGrid, gridPanelConstraints);
		
		//2. sor - Color
		gridPanelConstraints.gridx = 0;
		gridPanelConstraints.gridy++;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("     "),gridPanelConstraints );

		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel("Color: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add( colorSelector, gridPanelConstraints);
		
		//3. sor - Type
		gridPanelConstraints.gridx = 0;
		gridPanelConstraints.gridy++;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel("     "),gridPanelConstraints );

		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel("Type: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridTypeCombo, gridPanelConstraints);
		 
		//4. sor - Width
		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridy++;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel("Width: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridWidthCombo, gridPanelConstraints);
		
		gridPanelConstraints.gridx = 3;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add(new JLabel(" px"),gridPanelConstraints );
		
		//5. sor - Grid delta x
		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridy++;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel("Delta X: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridXDeltaField, gridPanelConstraints);
				
		gridPanelConstraints.gridx = 3;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel(" " + scale.getUnitX().getSign() ), gridPanelConstraints );	
		
		//6. sor - Grid delta y
		gridPanelConstraints.gridx = 1;
		gridPanelConstraints.gridy++;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel("Delta Y: "), gridPanelConstraints);

		gridPanelConstraints.gridx = 2;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 1;
		gridPanel.add(gridYDeltaField, gridPanelConstraints);
				
		gridPanelConstraints.gridx = 3;
		gridPanelConstraints.gridwidth = 1;
		gridPanelConstraints.weightx = 0;
		gridPanel.add( new JLabel(" " + scale.getUnitY().getSign() ), gridPanelConstraints );	
		
		return gridPanel;
	}
	
}
