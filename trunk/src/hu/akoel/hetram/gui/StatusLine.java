package hu.akoel.hetram.gui;

import hu.akoel.hetram.accessories.CommonOperations;

import java.awt.Color;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StatusLine extends JPanel{

	private static final long serialVersionUID = 530941861804065426L;
	
	private MainPanel mainPanel;
	
	private JTextField scaleField = new JTextField();
	private JTextField xPositionField = new JTextField();
	private JTextField yPositionField = new JTextField();
	private JTextField temperatureField = new JTextField();

/*	private JTextField qVerticalField = new JTextField();
	private JTextField qHorizontalField = new JTextField();
*/	private JTextField qNorthField = new JTextField();
	private JTextField qEastField = new JTextField();
	private JTextField qSouthField = new JTextField();
	private JTextField qWestField = new JTextField();
	

	public StatusLine( MainPanel mainPanel ) {
		super();
		
		this.mainPanel = mainPanel;
		
		this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
		
		scaleField.setColumns( 7 );
		scaleField.setBorder(BorderFactory.createLoweredBevelBorder());
		scaleField.setEditable(false);
		this.add( scaleField );
		
		xPositionField.setColumns( 6 );
		xPositionField.setBorder(BorderFactory.createLoweredBevelBorder());
		xPositionField.setEditable(false);
		this.add( xPositionField );
		
		yPositionField.setColumns( 6 );
		yPositionField.setBorder(BorderFactory.createLoweredBevelBorder());
		yPositionField.setEditable(false);
		this.add( yPositionField );
		
		temperatureField.setColumns( 6 );
		temperatureField.setBorder(BorderFactory.createLoweredBevelBorder());
		temperatureField.setEditable(false);
		temperatureField.setBackground( Color.yellow );
		this.add( temperatureField );
/*		
		qVerticalField.setColumns( 8 );
		qVerticalField.setBorder(BorderFactory.createLoweredBevelBorder());
		qVerticalField.setEditable(false);
		this.add( qVerticalField);

		qHorizontalField.setColumns( 8 );
		qHorizontalField.setBorder(BorderFactory.createLoweredBevelBorder());
		qHorizontalField.setEditable(false);
		this.add( qHorizontalField);
*/
		qNorthField.setColumns( 9 );
		qNorthField.setBorder(BorderFactory.createLoweredBevelBorder());
		qNorthField.setEditable(false);
		this.add( qNorthField);
		
		qEastField.setColumns( 9 );
		qEastField.setBorder(BorderFactory.createLoweredBevelBorder());
		qEastField.setEditable(false);
		this.add( qEastField );
		
		qSouthField.setColumns( 9 );
		qSouthField.setBorder(BorderFactory.createLoweredBevelBorder());
		qSouthField.setEditable(false);
		this.add( qSouthField );

		qWestField.setColumns( 9 );
		qWestField.setBorder(BorderFactory.createLoweredBevelBorder());
		qWestField.setEditable(false);
		this.add( qWestField );
		
	}
	
	public void setScale( Double scale ){
		
		if (scale < 1.0) {			
			scaleField.setText( "M=" + mainPanel.getCanvas().getRoundedDoubleWitPrecision(scale) + ":1" );
		} else {
			scaleField.setText( "M=1:" + mainPanel.getCanvas().getRoundedDoubleWitPrecision(scale) );
		}
	}
	
	public void setXPosition( double xPosition ){
		xPositionField.setText( "x:" + mainPanel.getCanvas().getRoundedDoubleWitPrecision(xPosition) + " m");
		//xPositionField.setText( "y:" + xPosition.setScale( mainPanel.getCanvas().getPrecision().getScale(), RoundingMode.HALF_UP ).toPlainString() + " m");
	}
	
	public void setYPosition( double yPosition ){
		yPositionField.setText( "y:" + mainPanel.getCanvas().getRoundedDoubleWitPrecision(yPosition) + " m");
		//yPositionField.setText( "y:" + yPosition.setScale( mainPanel.getCanvas().getPrecision().getScale(), RoundingMode.HALF_UP ).toPlainString() + " m");
	}
	
	public void setTemperature( Double temperature ){
		//DecimalFormat df = new DecimalFormat("#.0000");
		if( null == temperature ){
			temperatureField.setText("");
		}else{
			temperatureField.setText( CommonOperations.get3Decimals(temperature) + "°C");
		}
	}
/*
	public void setQVertical( Double q ){
		if( null == q ){
			qVerticalField.setText( "" );
		}else{
			String out = "q";
			if( q > 0 )
				out += "↑: "  + CommonOperations.get3Decimals( q );
			else if( q < 0 )
				out += "↓: "  + CommonOperations.get3Decimals( q );
			out += "W";
			qVerticalField.setText( out );
		}		
	}
	
	public void setQHorizontal( Double q ){
		if( null == q ){
			qVerticalField.setText( "" );
		}else{
			String out = "q";
			if( q > 0 )
				out += "→: "  + CommonOperations.get3Decimals( q );
			else if( q < 0 )
				out += "←: "  + CommonOperations.get3Decimals( q );
			out += "W";
			qHorizontalField.setText( out );
		}
		
	}
*/
	
	public void setQNorth( Double qNorth ){
		if( null == qNorth ){
			qNorthField.setText( "" );
		}else{
			String out = "qN: " + CommonOperations.get3Decimals( qNorth );
			if( qNorth > 0 )
				out += " ↑ ";
			else if( qNorth < 0 )
				out += " ↓ ";
			//out += "W/1m";
			out += "W";
			qNorthField.setText( out );
		}
		
	}
	
	public void setQEast( Double qEast ){
		if( null == qEast ){
			qEastField.setText( "" );
		}else{
			String out = "qE: " + + CommonOperations.get3Decimals( qEast );
			if( qEast > 0 )
				out += " → ";
			else if( qEast < 0 )
				out += " ← ";
			//out += "W/1m";
			out += "W";
			qEastField.setText( out );
		}
	}
	
	public void setQSouth( Double qSouth ){
		if( null == qSouth ){
			qSouthField.setText( "" );
		}else{
			String out = "qS: " + CommonOperations.get3Decimals( qSouth ); 
			if( qSouth > 0 )
				out += " ↓ ";
			else if( qSouth < 0)
				out += " ↑ ";
			//out += "W/1m";
			out += "W";
			qSouthField.setText( out );
		}
	}

	public void setQWest( Double qWest ){
		if( null == qWest ){
			qWestField.setText( "" );
		}else{
			String out = "qW: " + + CommonOperations.get3Decimals( qWest );
			if( qWest > 0 )
				out += " ← ";
			else if (qWest < 0)
				out += " → ";
			//out += "W/1m";
			out += "W";
			qWestField.setText( out );
		}
	}

}
