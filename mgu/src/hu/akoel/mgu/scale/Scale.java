package hu.akoel.mgu.scale;


import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.PixelPerUnitChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.scale.values.PixelPerCmValue;
import hu.akoel.mgu.scale.values.ScaleValue;
import hu.akoel.mgu.scale.values.UnitValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.ZoomRateValue;
import hu.akoel.mgu.values.Value;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class Scale {

	public static enum UNIT{
		mm(0.001, "mm"),
		cm(0.01, "cm"),
		m(1, "m"),
		km(1000, "km");
		
		private double exchange;
		private String sign;
		
		private UNIT( double exchange, String sign ){
			this.exchange = exchange;
			this.sign = sign;
		}
		
		public double getExchange(){
			return exchange;
		}
		
		public String getSign(){
			return sign;
		}
	}
	
	private MCanvas canvas;
	private PixelPerCmValue pixelPerCm;
	private UnitValue unit;
	private ScaleValue startScale;
	private ZoomRateValue zoomRate;
	private ScaleValue minScale;
	private ScaleValue maxScale;
	
	private ArrayList<ScaleChangeListener> scaleChangeListenerList = new ArrayList<ScaleChangeListener>();
	
	public Scale( MCanvas canvas, double pixelPerCm, UNIT unit, double startScale){
		commonConstructorForFreeScale(canvas, new PixelPerCmValue(pixelPerCm, pixelPerCm), new UnitValue(unit, unit), new ScaleValue(startScale, startScale), null, null, null );
	}
	
	public Scale( MCanvas canvas, double pixelPerCm, UNIT unit, double startScale, double zoomRate ){
		commonConstructorForFreeScale(canvas, new PixelPerCmValue(pixelPerCm, pixelPerCm), new UnitValue(unit, unit), new ScaleValue(startScale, startScale), new ZoomRateValue(zoomRate, zoomRate), null, null );
	}
	
	public Scale( MCanvas canvas, double pixelPerCm, UNIT unit, double startScale, double zoomRate, double minScale, double maxScale ){
		commonConstructorForFreeScale(canvas, new PixelPerCmValue(pixelPerCm, pixelPerCm), new UnitValue(unit, unit), new ScaleValue(startScale, startScale), new ZoomRateValue(zoomRate, zoomRate), new ScaleValue(minScale, minScale), new ScaleValue(maxScale, maxScale) );
	}
	
	
	
	
	public Scale( MCanvas canvas, PixelPerCmValue pixelPerCm, UnitValue unit, ScaleValue startScale){
		commonConstructorForFreeScale(canvas, pixelPerCm, unit, startScale, null, null, null );
	}
	
	public Scale( MCanvas canvas, PixelPerCmValue pixelPerCm, UnitValue unit, ScaleValue startScale, ZoomRateValue zoomRate ){
		commonConstructorForFreeScale(canvas, pixelPerCm, unit, startScale, zoomRate, null, null );		
	}
	
	public Scale( MCanvas canvas, PixelPerCmValue pixelPerCm, UnitValue unit, ScaleValue startScale, ZoomRateValue zoomRate, ScaleValue minScale, ScaleValue maxScale ){
		commonConstructorForFreeScale(canvas, pixelPerCm, unit, startScale, zoomRate, minScale, maxScale );		
	}
	
	private void commonConstructorForFreeScale( MCanvas canvas, PixelPerCmValue pixelPerCm, UnitValue unit, ScaleValue startScale, ZoomRateValue zoomRate, ScaleValue minScale, ScaleValue maxScale ){ 
		this.canvas = canvas;
		this.pixelPerCm = pixelPerCm;
		this.unit = unit;
		this.startScale = startScale;
	
		this.zoomRate = zoomRate;
		this.minScale = minScale;
		this.maxScale = maxScale;
		
		double startPPUX = getPixelPerUnitByScale(pixelPerCm.getX(), unit.getUnitX(), startScale.getX());
		double startPPUY = getPixelPerUnitByScale(pixelPerCm.getY(), unit.getUnitY(), startScale.getY());

		//Ha adtam meg nagyitasi hatarokat
		if( null != minScale && null != maxScale ){
		
			double minPPUX = getPixelPerUnitByScale(pixelPerCm.getX(), unit.getUnitX(), maxScale.getX());
			double minPPUY = getPixelPerUnitByScale(pixelPerCm.getY(), unit.getUnitY(), maxScale.getY());
			
			double maxPPUX = getPixelPerUnitByScale(pixelPerCm.getX(), unit.getUnitX(), minScale.getX());
			double maxPPUY = getPixelPerUnitByScale(pixelPerCm.getY(), unit.getUnitY(), minScale.getY());

			canvas.setPossiblePixelPerUnits( new PossiblePixelPerUnits( 
				new PixelPerUnitValue(startPPUX, startPPUY), 
				zoomRate, 
				new PixelPerUnitValue(minPPUX, minPPUY), 
				new PixelPerUnitValue(maxPPUX, maxPPUY)
			));
		
		//Ha nem voltak nagyitasi hatarok
		}else{
			canvas.setPossiblePixelPerUnits( new PossiblePixelPerUnits( 
					new PixelPerUnitValue(startPPUX, startPPUY), 
					zoomRate					
				));
		}

		canvas.addPixelPerUnitChangeListener( new ScalePixelPerUnitChangeListener( pixelPerCm, unit ));

	}
	
	public Scale( MCanvas canvas, double pixelPerCm, UNIT unit, ArrayList<ScaleValue> possibleScaleList, int pointerForPossibleScaleList){
		commonConstructorForDiscrateScale(canvas, new PixelPerCmValue(pixelPerCm, pixelPerCm), new UnitValue( unit, unit ), possibleScaleList, pointerForPossibleScaleList);
	}
	
	public Scale( MCanvas canvas, PixelPerCmValue pixelPerCm, UnitValue unit, ArrayList<ScaleValue> possibleScaleList, int pointerForPossibleScaleList){
		commonConstructorForDiscrateScale(canvas, pixelPerCm, unit, possibleScaleList, pointerForPossibleScaleList);
	}

	private void commonConstructorForDiscrateScale( MCanvas canvas, PixelPerCmValue pixelPerCm, UnitValue unit, ArrayList<ScaleValue> possibleScaleList, int pointerForPossibleScaleList ){
		this.canvas = canvas;
		this.pixelPerCm = pixelPerCm;
		this.unit = unit;
		
		ArrayList<PixelPerUnitValue> possiblePPUList = new ArrayList<PixelPerUnitValue>();
		
		for( Value scale: possibleScaleList ){
			possiblePPUList.add(
					new PixelPerUnitValue(
							getPixelPerUnitByScale(pixelPerCm.getX(), unit.getUnitX(), scale.getX()), getPixelPerUnitByScale(pixelPerCm.getY(), unit.getUnitY(), scale.getY() )
					)				
			);
		}
		
		canvas.setPossiblePixelPerUnits( new PossiblePixelPerUnits(possiblePPUList, pointerForPossibleScaleList));
	
		canvas.addPixelPerUnitChangeListener( new ScalePixelPerUnitChangeListener( pixelPerCm, unit ));

	}
	
	public void addScaleChangeListener( ScaleChangeListener scaleChangeListener ){
		scaleChangeListenerList.add(scaleChangeListener);
	}	
	
	public double getPixelPerUnitByScale(double pixelPerCm, UNIT unit, double scale){
		return pixelPerCm * unit.getExchange() / UNIT.cm.getExchange() / scale;
	}
	
	public double getScaleByPixelPerUnit( double pixelPerCm, UNIT unit, double ppu){
		return (pixelPerCm * unit.getExchange() / UNIT.cm.getExchange() / ppu	);
	}
		
	public UNIT getUnitX(){
		return unit.getUnitX();
	}
	
	public UNIT getUnitY(){
		return unit.getUnitY();
	}
	
	//TODO meg kell csinalni
	public void setPixelPerCm( PixelPerCmValue pixelPerCm ){
		this.pixelPerCm = pixelPerCm;
		double startPPUX = getPixelPerUnitByScale(pixelPerCm.getX(), unit.getUnitX(), startScale.getX());
		double startPPUY = getPixelPerUnitByScale(pixelPerCm.getY(), unit.getUnitY(), startScale.getY());
		canvas.setPossiblePixelPerUnits( new PossiblePixelPerUnits( new PixelPerUnitValue(startPPUX, startPPUY), zoomRate ));
		this.canvas.firePixelPerUnitChangeListener();
		this.canvas.repaintCoreCanvas();
	}
	
	public PixelPerCmValue getPixelPerCm(){
		return this.pixelPerCm;
	}
	
	public void setZoomRate( ZoomRateValue zoomRate ){
		this.zoomRate = zoomRate;
		double startPPUX = getPixelPerUnitByScale(pixelPerCm.getX(), unit.getUnitX(), startScale.getX());
		double startPPUY = getPixelPerUnitByScale(pixelPerCm.getY(), unit.getUnitY(), startScale.getY());
		canvas.setPossiblePixelPerUnits( new PossiblePixelPerUnits( new PixelPerUnitValue(startPPUX, startPPUY), zoomRate ));
		this.canvas.firePixelPerUnitChangeListener();
	}
	
	public ZoomRateValue getZoomRate(){
		return zoomRate;
	}
	
	class ScalePixelPerUnitChangeListener implements PixelPerUnitChangeListener{
		public PixelPerUnitValue pixelPerCm;
		public UnitValue unit;
		
		public ScalePixelPerUnitChangeListener( PixelPerCmValue pixelPerCm, UnitValue unit){
			this.pixelPerCm = new PixelPerUnitValue( pixelPerCm.getX(), pixelPerCm.getY() );
			this.unit = new UnitValue( unit.getUnitX(), unit.getUnitY() );
		}
		
		@Override
		public void getPixelPerUnit( Value pixelPerUnit ) {
			
			double scaleX = getScaleByPixelPerUnit(pixelPerCm.getX(), unit.getUnitX(), pixelPerUnit.getX());
			double scaleY = getScaleByPixelPerUnit(pixelPerCm.getY(), unit.getUnitY(), pixelPerUnit.getY());
			
			for( ScaleChangeListener listener : scaleChangeListenerList){
				listener.getScale( new ScaleValue( scaleX, scaleY ) );
			}			
		}
	}
	
	public ScaleValue getScale(){
		double scaleX = getScaleByPixelPerUnit(pixelPerCm.getX(), unit.getUnitX(), canvas.getPixelPerUnit().getX());
		double scaleY = getScaleByPixelPerUnit(pixelPerCm.getY(), unit.getUnitY(), canvas.getPixelPerUnit().getY());
		return new ScaleValue( scaleX, scaleY );
	}
	
	public JPanel getControl(){
		JTextField pixelPerCmField;	
		JTextField zoomRateField;	
		
		int row = 0;
		
		//Pixel per cm
		JLabel pixelPerCmLabel = new JLabel("1 cm = ");
		JLabel pixelPerCmUnit = new JLabel( "px" );
		pixelPerCmField = new JTextField();
		pixelPerCmField.setColumns(8);
		pixelPerCmField.setText(String.valueOf( Scale.this.getPixelPerCm().getX() ) );
		pixelPerCmField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf( Scale.this.getPixelPerCm().getX() );

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				Scale.this.setPixelPerCm( new PixelPerCmValue( Double.valueOf(goodValue), Double.valueOf(goodValue) ) );				
				Scale.this.canvas.revalidateAndRepaintCoreCanvas();
				return true;
			}
		});
		

		
		//Rate
		JLabel zoomRateLabel = new JLabel("Zoom rate: ");
		zoomRateField = new JTextField();
		zoomRateField.setColumns(8);
		zoomRateField.setText(String.valueOf( Scale.this.getZoomRate().getX() ) );
		zoomRateField.setInputVerifier(new InputVerifier() {
			String goodValue = String.valueOf( Scale.this.getZoomRate().getX() );

			@Override
			public boolean verify(JComponent input) {
				JTextField text = (JTextField) input;
				String possibleValue = text.getText();
				try {
					Double.valueOf(possibleValue);
					goodValue = possibleValue;
				} catch (NumberFormatException e) {
					text.setText(goodValue);
					return false;
				}
				Scale.this.setZoomRate( new ZoomRateValue( Double.valueOf(goodValue), Double.valueOf(goodValue) ) );				
				return true;
			}
		});
		
		JPanel scalePanel = new JPanel();
		scalePanel.setLayout(new GridBagLayout());
		scalePanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black), "Scale", TitledBorder.LEFT, TitledBorder.TOP));
		GridBagConstraints scaleConstraints = new GridBagConstraints();

		// 1. sor - pixel per cm
		row = 0;
		scaleConstraints.gridx = 0;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(pixelPerCmLabel, scaleConstraints);

		scaleConstraints.gridx = 1;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(pixelPerCmField, scaleConstraints);

		scaleConstraints.gridx = 2;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(pixelPerCmUnit, scaleConstraints);

		// 2. sor - rate
		row++;
		scaleConstraints.gridx = 0;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(zoomRateLabel, scaleConstraints);

		scaleConstraints.gridx = 1;
		scaleConstraints.gridy = row;
		scaleConstraints.gridwidth = 1;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scaleConstraints.fill = GridBagConstraints.HORIZONTAL;
		scaleConstraints.weightx = 0;
		scaleConstraints.anchor = GridBagConstraints.WEST;
		scalePanel.add(zoomRateField, scaleConstraints);
		
		return scalePanel;
	}
}

	
	
