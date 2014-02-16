package hu.akoel.hetram.gui;

import hu.akoel.hetram.gui.MainPanel.Mode;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ModePanel extends JPanel{
	
	private static final long serialVersionUID = -6019893902104723290L;

	private MainPanel mainPanel;
	private JLabel modeField;
	private JButton drawingButton;
	
	public ModePanel( MainPanel mainPanel ){
		super();
		
		this.mainPanel = mainPanel;
		
		//this.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS));
		
		//
		//Gombok definialasa
		//
		ButtonGroup modeGroup = new ButtonGroup();
		drawingButton = new JButton( "Váltás rajz módba" );
		drawingButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ModePanel.this.mainPanel.setModeField( Mode.DRAWING );				
			}
		});	
		
		modeGroup.add( drawingButton );
		
		modeField = new JLabel();
		//Default ertek beallitasa
		setModeField( ModePanel.this.mainPanel.getMode() );
				
		this.add(Box.createRigidArea(new Dimension(5,0)));
		this.add( modeField);
		this.add(Box.createHorizontalGlue());
		this.add( drawingButton);
		this.add(Box.createRigidArea(new Dimension(5,0)));
		
	}
	
	public void setModeField( Mode mode ){
		this.modeField.setText( mode.getName() );
		
		if( mode.equals( Mode.DRAWING ) ){
			drawingButton.setSelected( false );
			drawingButton.setEnabled( false );			
			
			//A szal elinditasa elott torli az alkalmazott delta ertekeket
//			ModePanel.this.mainPanel.set appliedXDeltaField.setText("");
//			ControlSettingTab.this.appliedYDeltaField.setText("");
			
			//Torlom a mar letezo Thermikus Pont listat es az ertekelofelulet ujrarajzolasaval el is tuntetem
			ModePanel.this.mainPanel.setThermicPointList(null);
			ModePanel.this.mainPanel.getCanvas().setEnabledDrawn( true );
			ModePanel.this.mainPanel.getCanvas().revalidateAndRepaintCoreCanvas();
			
			
		}else if( mode.equals( Mode.ANALYSIS ) ){
			//drawingButton.setSelected( true );
			drawingButton.setSelected( false );
			drawingButton.setEnabled( true );
			
			ModePanel.this.mainPanel.getCanvas().setEnabledDrawn( false );

		}else if( mode.equals( Mode.CALCULATION ) ){
			drawingButton.setSelected( false );
			drawingButton.setEnabled( false );
			
			ModePanel.this.mainPanel.getCanvas().setEnabledDrawn( false );
		}

		
	}
}
