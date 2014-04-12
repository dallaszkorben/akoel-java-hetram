package hu.akoel.mgu.drawnblock.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public class BuildingMaterialBlock extends DrawnBlock{
	
	private static final Color NORMAL_COLOR = Color.blue;
	private static final Stroke NORMAL_STROKE = new BasicStroke(1);
	private static final Color NORMAL_BACKGROUND = new Color( 100, 100, 0 );
	
	private static final Color SELECTED_COLOR = Color.magenta;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	private static final Color SELECTED_BACKGROUND = new Color( 0, 0, 100 );
	
	private static final Color INFOCUS_COLOR = Color.red;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(1);
	private static final Color INFOCUS_BACKGROUND = new Color( 0, 0, 100 );
	
	private static final Color INPROCESS_COLOR = Color.blue;
	private static final Stroke INPROCESS_STROKE = new BasicStroke(3);
	private static final Color INPROCESS_BACKGROUND = Color.black;
		
	public BuildingMaterialBlock(Status status, BigDecimal x1,BigDecimal y1) {
		super(status, x1, y1);
		
		int patternSize = 15;
		
		TexturePaint normalTexturePaint;
		TexturePaint selectedTexturePaint;
		TexturePaint infocusTexturePaint;
		TexturePaint inprocessTexturePaint;

		// Create a buffered image texture patch of size 5x5 
		//BufferedImage bi = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
		//Graphics2D big = bi.createGraphics();
		
		Rectangle r = new Rectangle( 0, 0, patternSize, patternSize );
		
		//
		// Normal
		//
		BufferedImage bi1 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big1 = bi1.createGraphics();
		big1.setColor( NORMAL_BACKGROUND );
		big1.fillRect( 0, 0, patternSize, patternSize );
		big1.setColor( NORMAL_COLOR ); 
		big1.drawLine( 0, 0, patternSize, patternSize );
		 
		normalTexturePaint = new TexturePaint( bi1,r ); 

		//
		// Selected
		//
		BufferedImage bi2 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big2 = bi2.createGraphics();
		big2.setColor( SELECTED_BACKGROUND );
		big2.fillRect( 0, 0, patternSize, patternSize );
		big2.setColor( SELECTED_COLOR ); 
		big2.drawLine( 0, 0, patternSize, patternSize );
		 
		selectedTexturePaint = new TexturePaint( bi2,r ); 
		
		//
		// Infocus
		//
		BufferedImage bi3 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big3 = bi3.createGraphics();
		big3.setColor( INFOCUS_BACKGROUND );
		big3.fillRect( 0, 0, patternSize, patternSize );
		big3.setColor( INFOCUS_COLOR ); 
		big3.drawLine( 0, 0, patternSize, patternSize );
		 
		infocusTexturePaint = new TexturePaint( bi3,r ); 
		
		//
		// Inprocess
		//
		BufferedImage bi4 = new BufferedImage( patternSize, patternSize, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big4 = bi4.createGraphics();
		big4.setColor( INPROCESS_BACKGROUND );
		big4.fillRect( 0, 0, patternSize, patternSize );
		big4.setColor( INPROCESS_COLOR ); 
		big4.drawLine( 0, 0, patternSize, patternSize );
		 
		inprocessTexturePaint = new TexturePaint( bi4,r ); 

		
		setNormal( NORMAL_COLOR, NORMAL_STROKE, NORMAL_BACKGROUND );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, INFOCUS_BACKGROUND );
		setInprocess( INPROCESS_COLOR, INPROCESS_STROKE, INPROCESS_BACKGROUND );
		
		setNormalTexturalPaint( normalTexturePaint );
		setSelectedTexturalPaint( selectedTexturePaint );
		setInfocusTexturalPaint( infocusTexturePaint );
		setInprocessTexturalPaint( inprocessTexturePaint );
		
		refreshStatus();
	}


}
