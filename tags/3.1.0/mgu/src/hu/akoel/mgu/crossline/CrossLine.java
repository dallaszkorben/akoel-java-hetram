package hu.akoel.mgu.crossline;

import hu.akoel.mgu.ColorSelector;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.MCanvas.Level;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
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
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class CrossLine {
	
	public static enum PainterPosition{
		DEEPEST,
		MIDDLE,
		HIGHEST
	}
	
	private MCanvas canvas;
	private Value position;
	private Color color;
	private int widthInPixel;
	private Value length;
	private PainterPosition painterPosition;
	private PainterListener painterListener;
	
	private boolean defaultOn = true;

	public CrossLine( MCanvas canvas, Value position, Color color, int widthInPixel, Value length, PainterPosition painterPosition ){
		common( canvas, position, color, widthInPixel, length, painterPosition );		
	}

	public CrossLine( MCanvas canvas, Value position, Color color, int widthInPixel, Value length, PainterPosition painterPosition, boolean defaultOn ){
		common( canvas, position, color, widthInPixel, length, painterPosition );
		this.defaultOn = defaultOn;
	}

	private void common( MCanvas canvas, Value position, Color color, int widthInPixel, Value length, PainterPosition painterPosition ){
		this.canvas = canvas;
		this.position = position;
		this.color = color;
		this.length = length;  
		this.widthInPixel = widthInPixel;
		this.painterPosition = painterPosition;
		
		painterListener = new CrossLinePainterListener( );
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
	
	public void setColor( Color color ){
		this.color = color;
	}
	
	public double getPositionX(){
		return position.getX();
	}
	
	public double getPositionY(){
		return position.getY();
	}
	
	public void setPositionX( double positionX ){
		this.position.setX( positionX );
	}
	
	public void setPositionY( double positionY ){
		this.position.setY( positionY );
	}
	
	public double getLengthX(){
		return length.getX();
	}
	
	public double getLengthY(){
		return length.getY();
	}
	
	public void setLengthX( double lengthX ){
		this.length.setX( lengthX );
	}

	public void setLengthY( double lengthY ){
		this.length.setY( lengthY );
	}

	public void setWidthInPixel( int widthInPixel ){
		this.widthInPixel = widthInPixel;
	}
	
	class CrossLinePainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			g2.setColor( color );
			g2.setStroke(new BasicStroke(widthInPixel));
			
			g2.drawLine( (position.getX() - length.getX()/2.0), position.getY(), (position.getX() + length.getX()/2.0), position.getY() );
			g2.drawLine( position.getX(), (position.getY() - length.getY()/2.0), position.getX(), (position.getY() + length.getY()/2.0) );
		}

		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
	}
	
	public JPanel getControl( Scale scale ){
		final JTextField crossLineXPosField;
		final JTextField crossLineYPosField;
		final JTextField crossLineXLengthField;
		final JTextField crossLineYLengthField;
		
		final JComboBox<String> crossLineWidthCombo;
		
		ColorSelector colorSelector = new ColorSelector();
		colorSelector.setSelectedItem( color );
		colorSelector.addActionListener( new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ColorSelector cs = (ColorSelector) e.getSource();
				//ElementSettingTab.this.mainPanel.setElementLineColor( cs.getSelectedColor() );
				color = cs.getSelectedColor();
				CrossLine.this.canvas.revalidateAndRepaintCoreCanvas();
			}
			
		});
		
		crossLineXPosField = new JTextField();
		crossLineXPosField.setColumns( 8 );
		crossLineXPosField.setText( String.valueOf( this.getPositionX() ) );
		crossLineXPosField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( CrossLine.this.getPositionX() );
			
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
	            CrossLine.this.setPositionX( Double.valueOf(goodValue));
	            CrossLine.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
	
		crossLineYPosField = new JTextField();
		crossLineYPosField.setColumns( 8 );
		crossLineYPosField.setText( String.valueOf( CrossLine.this.getPositionY() ) );
		crossLineYPosField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( CrossLine.this.getPositionY() );
			
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
	            CrossLine.this.setPositionY( Double.valueOf(goodValue));
	            CrossLine.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
		
		crossLineXLengthField = new JTextField();
		crossLineXLengthField.setColumns( 8 );
		crossLineXLengthField.setText( String.valueOf( CrossLine.this.getLengthX() ) );
		crossLineXLengthField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( CrossLine.this.getLengthX() );
			
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
	            CrossLine.this.setLengthX( Double.valueOf( goodValue ) );
	            CrossLine.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
	
		crossLineYLengthField = new JTextField();
		crossLineYLengthField.setColumns( 8 );
		crossLineYLengthField.setText( String.valueOf( CrossLine.this.getLengthY() ) );
		crossLineYLengthField.setInputVerifier( new InputVerifier() {
			String goodValue =  String.valueOf( CrossLine.this.getLengthY() );
			
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
	            CrossLine.this.setLengthY( Double.valueOf( goodValue ) );
	            CrossLine.this.canvas.revalidateAndRepaintCoreCanvas();
	            return true;
			}
		});
		

		String[] crossLineWidthElements = { "1", "3", "5" };
		crossLineWidthCombo = new JComboBox<String>(crossLineWidthElements);
		crossLineWidthCombo.setSelectedIndex(0);
		crossLineWidthCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> jcmbType = (JComboBox<String>) e.getSource();
				String cmbType = (String) jcmbType.getSelectedItem();
				
				if( cmbType.equals( "1")){					
					CrossLine.this.setWidthInPixel(1);
				}else if( cmbType.equals( "3")){
					CrossLine.this.setWidthInPixel(3);
				}else if( cmbType.equals( "5")){
					CrossLine.this.setWidthInPixel(5);
				}
				CrossLine.this.canvas.revalidateAndRepaintCoreCanvas();
			}
		});		
		
		JCheckBox turnOnCrossLine = new JCheckBox( "Turn On Crossline" );
		turnOnCrossLine.setSelected( defaultOn );
		if( defaultOn ){
			turnOn();
		}else{
			turnOff();
		}
		
		turnOnCrossLine.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.DESELECTED){
					CrossLine.this.turnOff();
					crossLineWidthCombo.setEnabled(false);
					crossLineXPosField.setEnabled(false);
					crossLineYPosField.setEnabled(false);
					crossLineXLengthField.setEnabled(false);
					crossLineYLengthField.setEnabled(false);
				}else{
					CrossLine.this.turnOn();
					crossLineWidthCombo.setEnabled(true);
					crossLineXPosField.setEnabled(true);
					crossLineYPosField.setEnabled(true);
					crossLineXLengthField.setEnabled(true);
					crossLineYLengthField.setEnabled(true);
				}
				CrossLine.this.canvas.repaint();
			}
		});
		
		JPanel crossLinePanel = new JPanel();
		crossLinePanel.setLayout(new GridBagLayout());
		crossLinePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Cross line", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints crossLinePanelConstraints = new GridBagConstraints();
		
		//1. sor - Turn on CrossLine
		crossLinePanelConstraints.gridx = 0;
		crossLinePanelConstraints.gridy = 0;
		crossLinePanelConstraints.gridwidth = 4;
		crossLinePanelConstraints.anchor = GridBagConstraints.WEST;
		crossLinePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanelConstraints.anchor = GridBagConstraints.WEST;
		crossLinePanel.add(turnOnCrossLine, crossLinePanelConstraints);
		
		//2. sor - Color
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy++;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel("Color: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add( colorSelector, crossLinePanelConstraints);	

		//3. sor - Position X
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy++;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel("Position X: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineXPosField, crossLinePanelConstraints);	
		
		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel(" " + scale.getUnitX().getSign() ), crossLinePanelConstraints );

		//3. sor - Position Y
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy++;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel("Position Y: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineYPosField, crossLinePanelConstraints);	
		
		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel(" " + scale.getUnitY().getSign() ), crossLinePanelConstraints);
		
		//4. sor - Width
		crossLinePanelConstraints.gridx = 0;
		crossLinePanelConstraints.gridy++;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add(new JLabel("     "),crossLinePanelConstraints );
		
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel("Width: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineWidthCombo, crossLinePanelConstraints);
		
		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel(" px"), crossLinePanelConstraints);
		
		//5. sor - Length X
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy++;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel("Length X: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineXLengthField, crossLinePanelConstraints);
		
		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel(" " + scale.getUnitX().getSign() ), crossLinePanelConstraints);
		
		//6. sor - Length Y
		crossLinePanelConstraints.gridx = 1;
		crossLinePanelConstraints.gridy++;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel("Length Y: "), crossLinePanelConstraints);

		crossLinePanelConstraints.gridx = 2;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 1;
		crossLinePanel.add(crossLineYLengthField, crossLinePanelConstraints);
		
		crossLinePanelConstraints.gridx = 3;
		crossLinePanelConstraints.gridwidth = 1;
		crossLinePanelConstraints.weightx = 0;
		crossLinePanel.add( new JLabel(" " + scale.getUnitY().getSign() ), crossLinePanelConstraints);

		return crossLinePanel;
	}
}
