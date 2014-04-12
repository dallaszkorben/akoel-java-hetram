package hu.akoel.mgu.drawnblock;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DrawnBlockStatusPanel extends JPanel{

	private static final long serialVersionUID = -6986444338932860146L;

	private JTextField scaleField;
	private JTextField scaleXField;
	private JTextField scaleYField;
	private JTextField xPositionField;
	private JTextField yPositionField;
	
	private boolean differentUnits;
	
	public DrawnBlockStatusPanel( boolean differentUnits ){
		commonConstructor( differentUnits );
	}

	public DrawnBlockStatusPanel(){
		commonConstructor( false );
	}

	private void commonConstructor( boolean differentUnits ){
		this.differentUnits = differentUnits;
		
		setLayout( new FlowLayout( FlowLayout.LEFT ));
		
		if( differentUnits ){
			scaleXField = new JTextField("Mx=");
			scaleXField.setColumns(10);
			scaleXField.setBorder(BorderFactory.createLoweredBevelBorder());
			scaleXField.setEditable(false);
			
			scaleYField = new JTextField("My=");
			scaleYField.setColumns(10);
			scaleYField.setBorder(BorderFactory.createLoweredBevelBorder());
			scaleYField.setEditable(false);
		}else{
			scaleField = new JTextField("M=");
			scaleField.setColumns(10);
			scaleField.setBorder(BorderFactory.createLoweredBevelBorder());
			scaleField.setEditable(false);
		}
		
		xPositionField = new JTextField("x:");
		xPositionField.setColumns(8);
		xPositionField.setBorder(BorderFactory.createLoweredBevelBorder());
		xPositionField.setEditable(false);
		
		yPositionField = new JTextField("y:");
		yPositionField.setColumns(8);
		yPositionField.setBorder(BorderFactory.createLoweredBevelBorder());
		yPositionField.setEditable(false);
		
		if( differentUnits){
			this.add( scaleXField );
			this.add( scaleYField );
		}else{
			this.add( scaleField );
		}
		
		this.add( xPositionField );
		this.add( yPositionField );
	}

	public void setXScale( String scale ){
		if( differentUnits ){
			scaleXField.setText( scale );
		}else{
			scaleField.setText( scale );
		}
	}
	
	public void setYScale( String scale ){
		if( differentUnits ){
			scaleYField.setText( scale );
		}else{
			scaleField.setText( scale );
		}
	}
	
	public void setScale( String scale ){
		if( differentUnits ){
			scaleXField.setText( scale );
			scaleYField.setText( scale );
		}else{
			scaleField.setText( scale );
		}
	}
	
	public void setXPosition( String xPosition ){
		xPositionField.setText( xPosition );
	}
	
	public void setYPosition( String yPosition ){
		yPositionField.setText( yPosition );
	}
}
