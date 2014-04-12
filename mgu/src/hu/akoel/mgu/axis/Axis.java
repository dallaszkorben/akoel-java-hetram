package hu.akoel.mgu.axis;

import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.MCanvas.Level;
import hu.akoel.mgu.values.SizeValue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;


public class Axis {
	
	public static enum AxisPosition{
		AT_ZERO_ZERO,
		AT_LEFT_BOTTOM,
		AT_LEFT_TOP,
		AT_RIGHT_BOTTOM,
		AT_RIGHT_TOP,		
	}
	
	public static enum PainterPosition{
		DEEPEST,
		MIDDLE,
		HIGHEST
	}
	
	int mainStickSizeInPixel = 8;
	int mainStickNumberSize = 14;
	int secondaryStickSizeInPixel = 6;
    double[] mainStickBaseNumbers = new double[]{0.25, 0.5, 1};//{0.25, 0.5, 1};
    int minimalDistanceInPixel = 40;
    
	MCanvas canvas;
	AxisPosition axisPosition;
	Color axisColor;
	Color stickColor;
	Color numberColor;
	int axisWidthInPixel;
	
	PainterListener painterListener;
	PainterPosition painterPosition;
	 
	public Axis( MCanvas canvas, AxisPosition axisPosition, Color axisColor, int axisWidthInPixel, PainterPosition painterPosition ){
		this.canvas = canvas;
		this.axisPosition = axisPosition;
		this.axisColor = axisColor;
		this.stickColor = axisColor;
		this.numberColor = axisColor;
		this.axisWidthInPixel = axisWidthInPixel;
		this.painterPosition = painterPosition;
		
		painterListener = new AxisPainterListener( );
			  
		if( painterPosition.equals(PainterPosition.DEEPEST ) ){
			canvas.addPainterListenerToDeepest(painterListener, Level.ABOVE);
		}else if( painterPosition.equals( PainterPosition.MIDDLE)){
			canvas.addPainterListenerToMiddle(painterListener, Level.ABOVE);
		}else if( painterPosition.equals(PainterPosition.HIGHEST)){
			canvas.addPainterListenerToHighest(painterListener, Level.ABOVE);
		}	
			  
	};
	
	public void refresh(){
		canvas.revalidateAndRepaintCoreCanvas();
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
    
	public void setMainStickNumberSize( int mainStickNumberSize ){
		this.mainStickNumberSize = mainStickNumberSize;
	}
	
	public void setAxisPosition( AxisPosition axisPosition ){
		this.axisPosition = axisPosition;
	}
	
	public void setAxisColor( Color axisColor ){
		this.axisColor = axisColor;
	}
	
	public void setNumberColor( Color numberColor ){
		this.numberColor = numberColor;
	}
	
	public void setMainStickSizeInPixel( int mainStickSizeInPixel ){
		this.mainStickSizeInPixel = mainStickSizeInPixel;
	}
	
	public void setSecondaryStickSizeInPixel( int secondaryStickSizeInPixel ){
		this.secondaryStickSizeInPixel = secondaryStickSizeInPixel;
	}

	/**
	 * Minimum ekkora tavolsag kell ket egymas koveto fobeosztas kozott
	 * 
	 * @param minimalDistanceInPixel
	 */
	public void setMinimalDistanceInPixel( int minimalDistanceInPixel ){
		this.minimalDistanceInPixel = minimalDistanceInPixel;
	}
	
	/**
	 * A parameterkent megadott 0 es 1 koze eso szamok 10 hatvanyai lesznek a
	 * megjelenitheto fobeosztasok.
	 * A default ertek: {0.25, 0.5, 1}
	 * 
	 * @param units
	 */
	public void setMainStickBaseNumbers( double[] mainStickBaseNumbers ){
		this.mainStickBaseNumbers = mainStickBaseNumbers;
	} 
	
    class AxisPainterListener implements PainterListener{

    	public AxisPainterListener(){
    		
    	}
    	
		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			double positionXVerticalAxis = 0;
			double positionYHorizontalAxis = 0;
			
			double positionYHorizontalNumber = 0;
			double positionXVerticalNumber = 0;
			
			BigDecimal xSteps = getXSteps(), mainXStick;
			BigDecimal ySteps = getYSteps(), mainYStick;
			
			FontRenderContext frc;
			TextLayout textLayout;
			Font fontNumber = new Font("Default", Font.PLAIN, mainStickNumberSize);
			
			if( axisPosition.equals( AxisPosition.AT_ZERO_ZERO) ){
				positionXVerticalAxis = 0;
				positionYHorizontalAxis = 0;
				
				positionXVerticalNumber = positionXVerticalAxis + canvas.getWorldXLengthByPixel( mainStickSizeInPixel );
				positionYHorizontalNumber = positionYHorizontalAxis + canvas.getWorldYLengthByPixel( mainStickSizeInPixel );					

			}else if( axisPosition.equals( AxisPosition.AT_LEFT_BOTTOM )){
				positionXVerticalAxis = canvas.getWorldXByPixel( 0 );
				positionYHorizontalAxis = canvas.getWorldSize().getYMin();
								
				positionXVerticalNumber = positionXVerticalAxis + canvas.getWorldXLengthByPixel( mainStickSizeInPixel );
				positionYHorizontalNumber = positionYHorizontalAxis + canvas.getWorldYLengthByPixel( mainStickSizeInPixel );
				
			}else if( axisPosition.equals( AxisPosition.AT_LEFT_TOP)){
				positionXVerticalAxis = canvas.getWorldXByPixel( 0 );
				positionYHorizontalAxis = canvas.getWorldSize().getYMax();	
				
				positionXVerticalNumber = positionXVerticalAxis + canvas.getWorldXLengthByPixel( mainStickSizeInPixel );
				positionYHorizontalNumber = positionYHorizontalAxis - canvas.getWorldYLengthByPixel( mainStickSizeInPixel + mainStickNumberSize );								

			}else if( axisPosition.equals( AxisPosition.AT_RIGHT_BOTTOM ) ){
				positionXVerticalAxis = canvas.getWorldSize().getXMax();
				positionYHorizontalAxis = canvas.getWorldSize().getYMin();
				
				//TODO Ki kell talalni valamit ra hogy a szam valodi szelessegetol fuggjon
				positionXVerticalNumber = positionXVerticalAxis - canvas.getWorldXLengthByPixel(mainStickSizeInPixel + 20 );
				positionYHorizontalNumber = positionYHorizontalAxis + canvas.getWorldYLengthByPixel( mainStickSizeInPixel );					
				
			}else if( axisPosition.equals( AxisPosition.AT_RIGHT_TOP ) ){
				positionXVerticalAxis = canvas.getWorldSize().getXMax();
				positionYHorizontalAxis = canvas.getWorldSize().getYMax();
				
				positionXVerticalNumber = positionXVerticalAxis - canvas.getWorldXLengthByPixel(mainStickSizeInPixel + 20 );
				positionYHorizontalNumber = positionYHorizontalAxis - canvas.getWorldYLengthByPixel( mainStickSizeInPixel + mainStickNumberSize );				
				
			}
			
			g2.setColor( axisColor );
			g2.setStroke(new BasicStroke(axisWidthInPixel));

			SizeValue worldSize = canvas.getWorldSize();
			
			//Vizszintes axis rajzolasa
			g2.drawLine(worldSize.getXMin(), positionYHorizontalAxis, worldSize.getXMax(), positionYHorizontalAxis);
			
			//Fuggoleges axis rajzolasa
			g2.drawLine(positionXVerticalAxis, worldSize.getYMin(), positionXVerticalAxis, worldSize.getYMax());
			
			//Vizszintes fobeosztasok es feliratok
			mainXStick = ((new BigDecimal(canvas.getWorldSize().getXMin())).divide(xSteps, 0, BigDecimal.ROUND_CEILING)).multiply(xSteps);
			mainXStick = mainXStick.subtract(xSteps);
			
			g2.setStroke(new BasicStroke(1));
			while(mainXStick.doubleValue() <= worldSize.getXMax()){
			
				//Alathato reszen kivuli elemeket ne rajzolja ki
				if( mainXStick.doubleValue() >= worldSize.getXMin() ){
				
					//Vizszintes fobeosztas
					g2.setColor(stickColor);
					g2.drawLine(
						mainXStick.doubleValue(), 
						positionYHorizontalAxis - canvas.getWorldYLengthByPixel(mainStickSizeInPixel)/2, 
						mainXStick.doubleValue(), 
						positionYHorizontalAxis + canvas.getWorldYLengthByPixel(mainStickSizeInPixel)/2 
					);
				
					//Vizszintes ertekek
					frc = g2.getFontRenderContext();
					textLayout = new TextLayout(String.valueOf(mainXStick), fontNumber, frc);					
					g2.setColor(numberColor);
					g2.drawFont(
							textLayout, 
							mainXStick.doubleValue() - canvas.getWorldXLengthByPixel((int)(textLayout.getBounds().getWidth()/2)), 
							positionYHorizontalNumber
					);
				} 
				
				//Kovetkezo fobeosztas
				mainXStick = mainXStick.add(xSteps);
			}

			//Fuggoleges fobeosztasok es feliratok
			mainYStick = ((new BigDecimal(worldSize.getYMin())).divide(ySteps, 0, BigDecimal.ROUND_CEILING)).multiply(ySteps);
			mainYStick = mainYStick.subtract(ySteps);

			g2.setStroke(new BasicStroke(1));
			while(mainYStick.doubleValue() <= worldSize.getYMax()){
			
				//Alathato reszen kivuli elemeket ne rajzolja ki
				if( mainYStick.doubleValue() >= worldSize.getYMin() ){
				
					//Fuggoleges fobeosztas
					g2.setColor(stickColor);
					g2.drawLine(
						positionXVerticalAxis - canvas.getWorldXLengthByPixel(mainStickSizeInPixel)/2,
						mainYStick.doubleValue(),
						positionXVerticalAxis + canvas.getWorldXLengthByPixel(mainStickSizeInPixel)/2,
						mainYStick.doubleValue()
					);
				
					//Fuggoleges ertekek
					frc = g2.getFontRenderContext();
					textLayout = new TextLayout(String.valueOf(mainYStick), fontNumber, frc);						
					g2.setColor(numberColor);
					g2.drawFont(
							textLayout, 
							positionXVerticalNumber,							 
							mainYStick.doubleValue() - canvas.getWorldYLengthByPixel( (int)(textLayout.getAscent()/2) )
					);
					
					
				}					        
				
				//Kovetkezo fobeosztas
				mainYStick = mainYStick.add(ySteps);
			}
			
		}

		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
		
		/**
		 * Visszaadja a parameterkent megadott szamhoz legkozelebbi jobb oldali
		 * 10 valamelyik hatvanyat
		 */
		private BigDecimal getNextOrder(BigDecimal base){
			int counter;
			  
		    //base > 0
		    if(base.compareTo(BigDecimal.valueOf(0)) > 0){
		      counter = 0;

		      //base >= 1
		      if (base.compareTo(BigDecimal.valueOf(1)) >= 0) {
		        while (base.compareTo(BigDecimal.valueOf(1)) > 0) {
		          base = base.movePointLeft(1);
		          counter ++;
		        }
		      }
		      //base < 1
		      else {
		        counter = 1;
		        while (base.compareTo(BigDecimal.valueOf(1)) < 0) {
		          base = base.movePointRight(1);
		          counter--;
		        }
		      }
		      BigDecimal ten = new BigDecimal(1);
		      ten = ten.movePointRight(counter);

		      return ten;
		    }else
		    	return base;
		}
		
		protected BigDecimal getXSteps(){

		    //A vilagban minimum ekkora lepeseket kell tennem, hogy a minimalis tavolsagot
		    //tudjam tartani a kepernyon
		    BigDecimal minMarkerDistance = BigDecimal.valueOf( canvas.getWorldXLengthByPixel(minimalDistanceInPixel) );
		    BigDecimal actualMarkerDistance = BigDecimal.valueOf( canvas.getWorldXLengthByPixel(minimalDistanceInPixel) );

		    int i = 0;
		    while(mainStickBaseNumbers.length > i){
		      actualMarkerDistance = getNextOrder(minMarkerDistance).multiply(new BigDecimal(mainStickBaseNumbers[i]));
		      if(actualMarkerDistance.doubleValue() >= minMarkerDistance.doubleValue()){
		        break;
		      }
		      i++;
		    }//while


		    return actualMarkerDistance;
		}// getXSteps()			
		  
		protected BigDecimal getYSteps(){

		    //A vilagban minimum ekkora lepeseket kell tennem, hogy a minimalis tavolsagot
		    //tudjam tartani a kepernyon
		    BigDecimal minMarkerDistance = BigDecimal.valueOf( canvas.getWorldYLengthByPixel(minimalDistanceInPixel) );
		    BigDecimal actualMarkerDistance = BigDecimal.valueOf( canvas.getWorldYLengthByPixel(minimalDistanceInPixel) );

		    int i = 0;
		    while(mainStickBaseNumbers.length > i){
		      actualMarkerDistance = getNextOrder(minMarkerDistance).multiply(new BigDecimal(mainStickBaseNumbers[i]));
		      if(actualMarkerDistance.doubleValue() >= minMarkerDistance.doubleValue()){
		        break;
		      }
		      i++;
		    }//while

		    return actualMarkerDistance;
		}// getYSteps()	
    	
    }
 	
    public JPanel getControl( ){
    	final JRadioButton lbAxisSelector = new JRadioButton( "LEFT BOTTOM", true);
    	final JRadioButton rbAxisSelector = new JRadioButton( "RIGHT BOTTOM");
    	final JRadioButton ltAxisSelector = new JRadioButton( "LEFT TOP");
    	final JRadioButton rtAxisSelector = new JRadioButton( "RIGHT TOP");
    	final JRadioButton zzAxisSelector = new JRadioButton( "ZERO ZERO");
    	
    	ActionListener axisSelectorActionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if( e.getSource() == lbAxisSelector){
					Axis.this.setAxisPosition( AxisPosition.AT_LEFT_BOTTOM );				
				}else if( e.getSource() == rbAxisSelector){
					Axis.this.setAxisPosition( AxisPosition.AT_RIGHT_BOTTOM );
				}else if( e.getSource() == ltAxisSelector){
					Axis.this.setAxisPosition( AxisPosition.AT_LEFT_TOP );
				}else if( e.getSource() == rtAxisSelector){
					Axis.this.setAxisPosition( AxisPosition.AT_RIGHT_TOP );
				}else if( e.getSource() == zzAxisSelector){
					Axis.this.setAxisPosition( AxisPosition.AT_ZERO_ZERO );
				}
				Axis.this.refresh();
			}
		};
		
		ButtonGroup bg = new ButtonGroup();
		
		bg.add(lbAxisSelector);
		lbAxisSelector.addActionListener(axisSelectorActionListener);
		
		bg.add(rbAxisSelector);
		rbAxisSelector.addActionListener(axisSelectorActionListener);
		
		bg.add(ltAxisSelector);
		ltAxisSelector.addActionListener(axisSelectorActionListener);
		
		bg.add(rtAxisSelector);
		rtAxisSelector.addActionListener(axisSelectorActionListener);
		
		bg.add(zzAxisSelector);
		zzAxisSelector.addActionListener(axisSelectorActionListener);
		
		//
		//Axis ki/be kapcsolo
		//
		JCheckBox turnOnAxis = new JCheckBox("Turn On Axis");
		turnOnAxis.setSelected(true);
		turnOnAxis.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					Axis.this.turnOff();
					lbAxisSelector.setEnabled(false);
					rbAxisSelector.setEnabled(false);
					ltAxisSelector.setEnabled(false);
					rtAxisSelector.setEnabled(false);
					zzAxisSelector.setEnabled(false);					
				}else{
					Axis.this.turnOn();
					lbAxisSelector.setEnabled(true);
					rbAxisSelector.setEnabled(true);
					ltAxisSelector.setEnabled(true);
					rtAxisSelector.setEnabled(true);
					zzAxisSelector.setEnabled(true);
				}
				Axis.this.canvas.repaint();
			}
		});
		
		JPanel axisPanel = new JPanel();
		axisPanel.setLayout(new GridBagLayout());
		axisPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Axis", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints axisPanelConstraints = new GridBagConstraints();
		
		//1. sor - Turn On Axis
		axisPanelConstraints.gridx = 0;
		axisPanelConstraints.gridy = 0;
		axisPanelConstraints.gridwidth = 2;
		axisPanelConstraints.anchor = GridBagConstraints.WEST;
		axisPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		axisPanelConstraints.weightx = 1;
		axisPanelConstraints.anchor = GridBagConstraints.WEST;				
		axisPanel.add(turnOnAxis, axisPanelConstraints);
		
		//2. sor - Left button
		axisPanelConstraints.gridx = 0;
		axisPanelConstraints.gridy = 1;
		axisPanelConstraints.gridwidth = 1;
		axisPanelConstraints.weightx = 0;
		axisPanel.add(new JLabel("     "), axisPanelConstraints );
		
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = 1;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(lbAxisSelector, axisPanelConstraints );
		
		//3. sor - Right button
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = 2;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(rbAxisSelector, axisPanelConstraints );
		
		//4. sor - Left Top button
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = 3;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(ltAxisSelector, axisPanelConstraints );
		
		//5. sor - Right Top button
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = 4;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(rtAxisSelector, axisPanelConstraints );
		
		//6. sor - Zero Zero button
		axisPanelConstraints.gridx = 1;
		axisPanelConstraints.gridy = 5;
		axisPanelConstraints.gridwidth = 1;
		axisPanel.add(zzAxisSelector, axisPanelConstraints );
		
		return axisPanel;
    }
}
	

	

