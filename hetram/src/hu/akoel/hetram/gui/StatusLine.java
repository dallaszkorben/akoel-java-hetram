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

	private JTextField qNorthField = new JTextField();
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

		qNorthField.setColumns( 10 );
		qNorthField.setBorder(BorderFactory.createLoweredBevelBorder());
		qNorthField.setEditable(false);
		this.add( qNorthField);
		
		qEastField.setColumns( 10 );
		qEastField.setBorder(BorderFactory.createLoweredBevelBorder());
		qEastField.setEditable(false);
		this.add( qEastField );
		
		qSouthField.setColumns( 10 );
		qSouthField.setBorder(BorderFactory.createLoweredBevelBorder());
		qSouthField.setEditable(false);
		this.add( qSouthField );

		qWestField.setColumns( 10 );
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
	
	public void setQNorth( Double qNorth ){
		if( null == qNorth ){
			qNorthField.setText( "" );
		}else{
			String out = "qN: " + CommonOperations.get3Decimals( Math.abs( qNorth ) );
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
			String out = "qE: " + + CommonOperations.get3Decimals( Math.abs( qEast ) );
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
			String out = "qS: " + CommonOperations.get3Decimals( Math.abs( qSouth ) ); 
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
			String out = "qW: " + CommonOperations.get3Decimals( Math.abs( qWest ) );
			if( qWest > 0 )
				out += " ← ";
			else if (qWest < 0)
				out += " → ";
			//out += "W/1m";
			out += "W";
			qWestField.setText( out );
		}
	}
	
	public void setVerticalOpenEdgeSumQ( Double qSumEast, Double qSumWest ){
		String out = "";
		
		qNorthField.setText( "" );
		qSouthField.setText( "" );
		qEastField.setText( "" );
		qWestField.setText( "" );
		
		if( qSumEast != 0 ){
			if( qSumEast > 0 ){
				out = "QE: " + CommonOperations.get3Decimals( Math.abs(qSumEast)) + " W → ";
			}else if( qSumEast < 0 ){
				out = "QE: " + CommonOperations.get3Decimals( Math.abs(qSumEast)) + " W ← ";
			}
			qEastField.setText( out );
		}

		if( qSumWest != 0 ){
			if( qSumWest > 0 ){
				out = "QW: " + CommonOperations.get3Decimals( Math.abs(qSumWest)) + " W ← ";
			}else if( qSumWest < 0 ){
				out = "QW: " + CommonOperations.get3Decimals( Math.abs(qSumWest)) + " W → ";
			}
			qWestField.setText( out );
		}
		
	}
	
	public void setHorizontalOpenEdgeSumQ( Double qSumNorth, Double qSumSouth ){
		String out = "";
		
		qNorthField.setText( "" );
		qSouthField.setText( "" );
		qEastField.setText( "" );
		qWestField.setText( "" );

		if( qSumNorth != 0 ){
			if( qSumNorth > 0 ){
				out = "QN: " + CommonOperations.get3Decimals( Math.abs(qSumNorth)) + " W ↑ ";
			}else if( qSumNorth < 0 ){
				out = "QN: " + CommonOperations.get3Decimals( Math.abs(qSumNorth)) + " W ↓ ";
			}
			qNorthField.setText( out );
		}

		if( qSumSouth != 0 ){
			if( qSumSouth > 0 ){
				out = "QS: " + CommonOperations.get3Decimals( Math.abs(qSumSouth)) + " W ↓ ";
			}else if( qSumSouth < 0 ){
				out = "QS: " + CommonOperations.get3Decimals( Math.abs(qSumSouth)) + " W ↑ ";
			}
			qSouthField.setText( out );
		}

	}


}
