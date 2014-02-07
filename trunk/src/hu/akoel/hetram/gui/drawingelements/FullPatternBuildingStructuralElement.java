package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public class FullPatternBuildingStructuralElement extends DrawnBlock{

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
	
	private double lambda;
	
	private FullPatternBuildingStructuralElement(Status status, double x1, double y1,
			java.lang.Double minLength, java.lang.Double maxLength,
			java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
	}

	private FullPatternBuildingStructuralElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );
	}
	
	public FullPatternBuildingStructuralElement(FullPatternInterface fullPatternInterface, Status status, double x1, double y1, double lambda, Color color, Color background ) {
		super(status, x1, y1);
		
		this.lambda = lambda;	
		
		int patternWidth = fullPatternInterface.getPatternWidth();
		int patternHeight = fullPatternInterface.getPatternHeight();
		
		TexturePaint normalTexturePaint;
		TexturePaint selectedTexturePaint;
		TexturePaint infocusTexturePaint;
		TexturePaint inprocessTexturePaint;

		Rectangle r = new Rectangle( 0, 0, patternWidth, patternHeight );
	
		//
		//Normal
		//
		BufferedImage bi1 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big1 = bi1.createGraphics();
		big1.setColor( background );
		big1.fillRect( 0, 0, patternWidth, patternHeight );
		big1.setColor( color ); 
		fullPatternInterface.drawPattern(big1, patternWidth, patternHeight);
		normalTexturePaint = new TexturePaint( bi1,r ); 
	
		//
		// Selected
		//
		BufferedImage bi2 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big2 = bi2.createGraphics();
		big2.setColor( SELECTED_BACKGROUND );
		big2.fillRect( 0, 0, patternWidth, patternHeight );
		big2.setColor( SELECTED_COLOR ); 
		fullPatternInterface.drawPattern(big2, patternWidth, patternHeight);
		selectedTexturePaint = new TexturePaint( bi2,r ); 
	
		//
		// Infocus
		//
		BufferedImage bi3 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big3 = bi3.createGraphics();
		big3.setColor( background );
		big3.fillRect( 0, 0, patternWidth, patternHeight );
		big3.setColor( INFOCUS_COLOR ); 
		fullPatternInterface.drawPattern(big3, patternWidth, patternHeight);
		infocusTexturePaint = new TexturePaint( bi3,r ); 
	
		//
		// Inprocess
		//
		BufferedImage bi4 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big4 = bi4.createGraphics();
		big4.setColor( background );
		big4.fillRect( 0, 0, patternWidth, patternHeight );
		big4.setColor( color ); 
		fullPatternInterface.drawPattern(big4, patternWidth, patternHeight);		 
		inprocessTexturePaint = new TexturePaint( bi4,r ); 
	
		setNormal( color, NORMAL_STROKE, normalTexturePaint );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, selectedTexturePaint );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, infocusTexturePaint );
		setInprocess( color, INPROCESS_STROKE, inprocessTexturePaint );
		
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
}
