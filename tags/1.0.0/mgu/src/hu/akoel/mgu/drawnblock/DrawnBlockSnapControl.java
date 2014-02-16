package hu.akoel.mgu.drawnblock;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import hu.akoel.mgu.grid.Grid;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class DrawnBlockSnapControl extends JPanel{

	private static final long serialVersionUID = -7405525427175628680L;

	private static final boolean DEFAULT_GRIDSNAP = true;
	private static final boolean DEFAULT_SIDEEXTENTIONSNAP = true;
	private static final boolean DEFAULT_SIDEDIVISIONSNAP = true;
	private static final int DEFAULT_SNAPDELTA = 15;
	private static final double DEFAULT_SNAPSIDEDIVISION = 0.5;
	
	private DrawnBlockCanvas canvas;
	private Grid grid;

	public DrawnBlockSnapControl( DrawnBlockCanvas canvas, Grid grid ){
		this.canvas = canvas;
		this.grid = grid;
		
		//GridSnap engedelyezese
		DrawnBlockSnapControl.this.canvas.setNeededGridSnap( DEFAULT_GRIDSNAP, grid );
		
		//Oldal meghosszabitasra igazitas engedelyezese
		DrawnBlockSnapControl.this.canvas.setNeededSideExtentionSnap( DEFAULT_SIDEEXTENTIONSNAP );

		//Oldal osztaspontra igazitas engedelyezese
		DrawnBlockSnapControl.this.canvas.setNeededSideDivisionSnap( DEFAULT_SIDEDIVISIONSNAP );

		//Alapertelmezett snap tartomany
		DrawnBlockSnapControl.this.canvas.setSnapDelta( DEFAULT_SNAPDELTA );
		
		//Alapertelmezett snap oldalfelosztas
		DrawnBlockSnapControl.this.canvas.setSnapSideDivision( DEFAULT_SNAPSIDEDIVISION );
				
	}
	
	public JPanel getControl(){
		
		JTextField snapDeltaField = new JTextField();
		snapDeltaField.setColumns( 8 );
		snapDeltaField.setText( String.valueOf( DrawnBlockSnapControl.this.canvas.getSnapDelta() ) );
		snapDeltaField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( DrawnBlockSnapControl.this.canvas.getSnapDelta() );
			
			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField)input;
	            String possibleValue = text.getText();
	            try{
	            	Integer.valueOf(possibleValue);
	            	goodValue = possibleValue;
	            }catch(NumberFormatException e){
	            	text.setText(goodValue);
	            	return false;
	            }
	            DrawnBlockSnapControl.this.canvas.setSnapDelta( Integer.valueOf( goodValue ) );
	            DrawnBlockSnapControl.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
		
		JCheckBox turnOnGridSnap = new JCheckBox("Grid");
		turnOnGridSnap.setSelected( DEFAULT_GRIDSNAP );
		turnOnGridSnap.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					DrawnBlockSnapControl.this.canvas.setNeededGridSnap( false, null );
				}else{
					DrawnBlockSnapControl.this.canvas.setNeededGridSnap( true, grid );
				}
				
			}
		});
		
		JCheckBox turnOnSideExtentionSnap = new JCheckBox("Side extention");
		turnOnSideExtentionSnap.setSelected( DEFAULT_SIDEEXTENTIONSNAP );
		turnOnSideExtentionSnap.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					DrawnBlockSnapControl.this.canvas.setNeededSideExtentionSnap( false );
				}else{
					DrawnBlockSnapControl.this.canvas.setNeededSideExtentionSnap( true );
				}				
			}
		});
		
		JCheckBox turnOnSideDivisionSnap = new JCheckBox("Side division");
		turnOnSideDivisionSnap.setSelected( DEFAULT_SIDEDIVISIONSNAP );
		turnOnSideDivisionSnap.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					DrawnBlockSnapControl.this.canvas.setNeededSideDivisionSnap( false );
				}else{
					DrawnBlockSnapControl.this.canvas.setNeededSideDivisionSnap( true );
				}				
			}
		});
		
		JTextField snapSideDivisionField = new JTextField();
		snapSideDivisionField.setColumns( 8 );
		snapSideDivisionField.setText( String.valueOf( DrawnBlockSnapControl.this.canvas.getSnapSideDivision() ) );
		snapSideDivisionField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( DrawnBlockSnapControl.this.canvas.getSnapSideDivision() );
			
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
	            DrawnBlockSnapControl.this.canvas.setSnapSideDivision( Double.valueOf( goodValue ) );
	            DrawnBlockSnapControl.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
		
		JPanel snapPanel = new JPanel();
		snapPanel.setLayout(new GridBagLayout());
		snapPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Snap", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints snapPanelConstraints = new GridBagConstraints();

		//1. sor - Snap Range
		snapPanelConstraints.gridx = 0;
		snapPanelConstraints.gridy = 0;
		snapPanelConstraints.gridwidth = 2;
		snapPanelConstraints.weightx = 0;
		snapPanel.add( new JLabel("Snap range: "), snapPanelConstraints);

		snapPanelConstraints.gridx = 3;
		snapPanelConstraints.gridwidth = 1;
		snapPanelConstraints.weightx = 1;
		snapPanel.add( snapDeltaField, snapPanelConstraints);
				
		snapPanelConstraints.gridx = 4;
		snapPanelConstraints.gridwidth = 1;
		snapPanelConstraints.weightx = 0;
		snapPanel.add( new JLabel(" px" ), snapPanelConstraints );	
				
		//2. sor - GridSnap
		snapPanelConstraints.gridx = 0;
		snapPanelConstraints.gridy++;
		snapPanelConstraints.gridwidth = 4;
		snapPanelConstraints.anchor = GridBagConstraints.WEST;
		snapPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		snapPanelConstraints.weightx = 1;
		snapPanel.add(turnOnGridSnap, snapPanelConstraints);
		
		//3. sor - SideExtentionSnap
		snapPanelConstraints.gridx = 0;
		snapPanelConstraints.gridy++;
		snapPanelConstraints.gridwidth = 4;
		snapPanelConstraints.anchor = GridBagConstraints.WEST;
		snapPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		snapPanelConstraints.weightx = 1;
		snapPanel.add( turnOnSideExtentionSnap, snapPanelConstraints);
		
		//4. sor - SideDivision
		snapPanelConstraints.gridx = 0;
		snapPanelConstraints.gridy++;
		snapPanelConstraints.gridwidth = 4;
		snapPanelConstraints.anchor = GridBagConstraints.WEST;
		snapPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		snapPanelConstraints.weightx = 1;
		snapPanel.add( turnOnSideDivisionSnap, snapPanelConstraints );
		
		//5. sor - SideDivision value
		snapPanelConstraints.gridx = 0;
		snapPanelConstraints.gridy++;
		snapPanelConstraints.gridwidth = 1;
		snapPanel.add( new JLabel("     "), snapPanelConstraints );		
		
		//4. sor - SideDivision
		snapPanelConstraints.gridx = 1;
		snapPanelConstraints.gridwidth = 1;
		snapPanelConstraints.anchor = GridBagConstraints.WEST;
		snapPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		snapPanelConstraints.weightx = 1;
		snapPanel.add( snapSideDivisionField, snapPanelConstraints );
		
		return snapPanel;
	}
}
