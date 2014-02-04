package hu.akoel.hetram.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.drawnblock.DrawnBlock;

public class BuildingStructureFullFilledElement extends DrawnBlock{

	private static final long serialVersionUID = -8868671968355924643L;

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
	
	private MainPanel mainPanel;
	private double lambda;
	private Color color;
	private Color background; 

	private BuildingStructureFullFilledElement(Status status, double x1, double y1,
			java.lang.Double minLength, java.lang.Double maxLength,
			java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
	}

	private BuildingStructureFullFilledElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );
	}
	
	public BuildingStructureFullFilledElement(MainPanel mainPanel, Status status, double x1, double y1, double lambda, Color color, Color background ) {
		super(status, x1, y1);
				
		this.mainPanel = mainPanel;
		this.lambda = lambda;		
		this.color = color;
		this.background = background;
		
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	public void draw( MGraphics g2 ){

		int patternWidth;
		int patternHeight;

		TexturePaint normalTexturePaint;
		TexturePaint selectedTexturePaint;
		TexturePaint infocusTexturePaint;
		TexturePaint inprocessTexturePaint;

		//Szelesebb mint magas
		if( getWidth() > getHeight() ){
			
			patternHeight = mainPanel.getCanvas().getPixelYLengthByWorld(getHeight() ) ;
			patternWidth = patternHeight / 2;
			
		//Magasabb mint szeles
		}else{
			
			patternWidth = mainPanel.getCanvas().getPixelXLengthByWorld(getWidth() );
			patternHeight = patternWidth / 2;
			
		}
	
System.err.println(patternWidth + " - " + patternHeight );		
if( patternWidth > 0 && patternHeight > 0 ){		
		//
		//Normal
		//
		BufferedImage bi1 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big1 = bi1.createGraphics();
		big1.setColor( background );
		big1.fillRect( 0, 0, patternWidth, patternHeight );
		big1.setColor( color ); 
		
		//
		// Selected
		//
		BufferedImage bi2 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big2 = bi2.createGraphics();
		big2.setColor( SELECTED_BACKGROUND );
		big2.fillRect( 0, 0, patternWidth, patternHeight );
		big2.setColor( SELECTED_COLOR ); 
		
		//
		// Infocus
		//
		BufferedImage bi3 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big3 = bi3.createGraphics();
		big3.setColor( background );
		big3.fillRect( 0, 0, patternWidth, patternHeight );
		big3.setColor( INFOCUS_COLOR ); 
		
		//
		// Inprocess
		//
		BufferedImage bi4 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big4 = bi4.createGraphics();
		big4.setColor( background );
		big4.fillRect( 0, 0, patternWidth, patternHeight );
		big4.setColor( color ); 
		
		
		
		
		//Szelesebb mint magas
		if( getWidth() > getHeight() ){
			
			double shift = mainPanel.getCanvas().getPixelXPositionByWorldBeforeTranslate( getY1() ) - (int)( mainPanel.getCanvas().getPixelXPositionByWorldBeforeTranslate( getY1() ) / patternHeight );
			big1.translate( 0,  shift );
			big4.translate( 0,  shift );
			
//			big1.translate( 0,  mainPanel.getCanvas().getPixelYLengthByWorld(getY1() - (int)(getY1() / getHeight() ) ) );
//			big4.translate( 0,  mainPanel.getCanvas().getPixelYLengthByWorld(getY1() - (int)(getY1() / getHeight() ) ) );
			
//			patternHeight = mainPanel.getCanvas().getPixelYLengthByWorld(getHeight());
//			patternWidth = patternHeight / 2;
			
			//Normal
			big1.drawLine( 0, 0, patternWidth / 2, patternHeight );
			big1.drawLine( patternWidth, 0, patternWidth / 2, patternHeight );	
			
			//Selected
			big2.drawLine( 0, 0, patternWidth / 2, patternHeight );
			big2.drawLine( patternWidth, 0, patternWidth / 2, patternHeight );	
		
			//Infocus
			big3.drawLine( 0, 0, patternWidth / 2, patternHeight );
			big3.drawLine( patternWidth, 0, patternWidth / 2, patternHeight );	
			
			//Inprocess
			big4.drawLine( 0, 0, patternWidth / 2, patternHeight );
			big4.drawLine( patternWidth, 0, patternWidth / 2, patternHeight );	

			
		//Magasabb mint szeles
		}else{
			
//			patternWidth = mainPanel.getCanvas().getPixelXLengthByWorld(getWidth());
//			patternHeight = patternWidth / 2;
			
			//Normal
			big1.drawLine( 0, 0, patternWidth, patternHeight / 2 );
			big1.drawLine( 0, patternHeight, patternWidth, patternHeight / 2 );			
			
			//Selected
			big2.drawLine( 0, 0, patternWidth, patternHeight / 2 );
			big2.drawLine( 0, patternHeight, patternWidth, patternHeight / 2 );			

			//Infocus
			big3.drawLine( 0, 0, patternWidth, patternHeight / 2 );
			big3.drawLine( 0, patternHeight, patternWidth, patternHeight / 2 );					 
			 
			//Inprocess
			big4.drawLine( 0, 0, patternWidth, patternHeight / 2 );
			big4.drawLine( 0, patternHeight, patternWidth, patternHeight / 2 );

			
		}
		
		
		Rectangle r = new Rectangle( 0, 0, patternWidth, patternHeight );
		normalTexturePaint = new TexturePaint( bi1,r );
		selectedTexturePaint = new TexturePaint( bi2,r ); 
		infocusTexturePaint = new TexturePaint( bi3,r );
		inprocessTexturePaint = new TexturePaint( bi4,r );

		setNormal( color, NORMAL_STROKE, normalTexturePaint );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, selectedTexturePaint );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, infocusTexturePaint );
		setInprocess( color, INPROCESS_STROKE, inprocessTexturePaint );
		
		super.draw(g2);
//		g2.setPaint( inprocessTexturePaint );
//		g2.fillRectangle( getX1(), getY1(), getX2(), getY2());
//		g2.setStroke( INPROCESS_STROKE );
		
		
}	
		
		g2.setColor( color );
		g2.drawRectangle(getX1(), getY1(), getX2(), getY2());
		
	}	
		
	
}












