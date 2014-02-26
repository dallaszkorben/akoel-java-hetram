package hu.akoel.mgu.test;


import static org.junit.Assert.*;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.LengthValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.ZoomRateValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.Value;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestMCanvas{

	private static final long serialVersionUID = 5810956401235486862L;

	private static MCanvas myCanvas;
//	private SizeValue boundSize = new SizeValue(0.0, 0.0, 400.0, 400);	
	private static Color background = Color.black;
	private static TranslateValue positionToMiddle = null;//new Position( 10, 10);
	private static PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(new PixelPerUnitValue(109.56123458075261, 109.56123458075261), new ZoomRateValue(1.2, 1.2));

	private static Grid myGrid;	
	private static Color gridColor = Color.green;
	private static int gridWidth = 1;
	private static DeltaValue gridDelta = new DeltaValue(1.0, 1.0);
	private static Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST; 
	private static Grid.Type gridType = Grid.Type.DOT;

	private static CrossLine myCrossLine;
	private static PositionValue crossLinePosition = new PositionValue( 5, 5 );
	private static Color crossLineColor = Color.red;
	private static int crossLineWidthInPixel = 5;
	private static Value crossLineLength = new LengthValue( 1, 1 );
	private static CrossLine.PainterPosition crossLinePainterPosition = CrossLine.PainterPosition.DEEPEST;
	
	private static Axis myAxis;
	private static Color axisColor = Color.yellow;
	private static int axisWidthInPixel = 1;
	private static Axis.AxisPosition axisPosition = Axis.AxisPosition.AT_LEFT_BOTTOM;
	private static Axis.PainterPosition painterPosition = Axis.PainterPosition.HIGHEST;
		
//	private Scale myScale;
//	private double pixelPerCm = 42.1;
//	private Scale.UNIT unit = Scale.UNIT.m;
//	private double startScale = 100;
//	private Position rate = new Position(1.2, 1.2);
	
//	private CanvasControl canvasControl;

	@BeforeClass
	public static void prepare(){
	
		JFrame mainFrame = new JFrame();
	
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("Test");
		mainFrame.setUndecorated(false);
		mainFrame.setSize(700, 400);
		mainFrame.createBufferStrategy(1);

		myCanvas = new MCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle);
		
		myGrid = new Grid( myCanvas, gridType, gridColor, gridWidth, gridPosition, gridDelta );		
		
		myCrossLine = new CrossLine( myCanvas, crossLinePosition, crossLineColor, crossLineWidthInPixel, crossLineLength, crossLinePainterPosition);
	
		myAxis = new Axis(myCanvas, axisPosition, axisColor, axisWidthInPixel, painterPosition);
			
//		myScale = new Scale( myCanvas, pixelPerCm, unit, 4.812345 );
		
		myCanvas.addPainterListenerToHighest(new PainterListener(){
			

			@Override
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				g2.setColor(new Color(250, 200, 0));
				g2.setStroke(new BasicStroke(1));
				
				double x;
				double y;
				double width;
				double height;
				
				x = 1.123456789;
				y = 2.345678901;
				width = 3.987654321;
				height = 2.34567654;				
				g2.drawRectangle(x, y, width, height);

				x = x+width;
				y = 2.345678901;
				width = 3.987654321;
				height = 2.34567654;
				
				g2.drawRectangle(x, y, width, height);
				
			}

			@Override
			public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {
				// TODO Auto-generated method stub
				
			}	
			
		}, MCanvas.Level.UNDER);		
		myCanvas.repaint();
		

		mainFrame.getContentPane().add(myCanvas, BorderLayout.CENTER);
	
		mainFrame.setVisible(true);		
		
	}
	
	@Test
	public void test1(){
		
		double x;
		double y;
		double width;
		double height;
		double pixelPerUnitValue = 2.9;
		double rate = 2.63;
		
/*		x = 1.123456789;
		y = 2.345678901;
		width = 3.987654321;
		height = 2.34567654;		
*/	
		
		pixelPerUnitValue = 10;//109.56123458075261;
		myCanvas.setPossiblePixelPerUnits( new PossiblePixelPerUnits( new PixelPerUnitValue(pixelPerUnitValue, pixelPerUnitValue)));
		myCanvas.revalidateAndRepaintCoreCanvas();

		
		for( int i = 1; i<=20; i ++){
		
			x = 1.0;
			y = -1.97612;
			width = 2;
			height = 1.78912;
			
			for( int j = 1; j < 100; j++ ){

				double startX = x - width/2;
				double startY = y - height/2;
		
				double pixelXBy2Methods = myCanvas.getPixelXPositionByWorldBeforeTranslate(startX) + myCanvas.getPixelXLengthByWorld(width);
				double pixelYBy2Methods = myCanvas.getPixelYPositionByWorldBeforeTranslate(startY) + myCanvas.getPixelYLengthByWorld(height);
		
				double pixelXBy1Method = myCanvas.getPixelXPositionByWorldBeforeTranslate( startX + width );
				double pixelYBy1Method = myCanvas.getPixelYPositionByWorldBeforeTranslate( startY + height );
		

				System.err.println( pixelXBy2Methods + " = " + pixelXBy1Method + ", " + pixelYBy2Methods + " = " + pixelYBy1Method );
				
				assertEquals( pixelXBy2Methods, pixelXBy1Method, 0.4 );
				assertEquals( pixelYBy2Methods, pixelYBy1Method, 0.4 );
			
				x = x + width;
				y = y + height;
			}
			
			pixelPerUnitValue *= rate;
		}
	}
}

