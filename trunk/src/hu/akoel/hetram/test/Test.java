package hu.akoel.hetram.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Locale;

import javax.lang.model.util.Elements;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

import hu.akoel.hetram.Element;
import hu.akoel.hetram.Element.SideOrientation;
import hu.akoel.hetram.accessories.Length;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.DThermicConnector;
import hu.akoel.hetram.connectors.ThermicConnector;
import hu.akoel.hetram.CommonOperations;
import hu.akoel.hetram.ElementSet;
import hu.akoel.hetram.SurfaceClose;
import hu.akoel.hetram.SymmetricClose;
import hu.akoel.hetram.ThermicPoint;
import hu.akoel.hetram.ThermicPointList;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.scale.ScaleChangeListener;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.LengthValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.Value;

public class Test extends JFrame {

	private static final long serialVersionUID = 7824210627357949349L;

	ThermicPointList thermicPointList;
	

	// GCanvas parameterei
	private MCanvas myCanvas;
	private Color background = Color.black;
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(
			new PixelPerUnitValue(1, 1));
	private TranslateValue positionToMiddle = new TranslateValue(0, 0);

	// Grid parameterei
	private Grid myGrid;
	private Color gridColor = Color.green;
	private int gridWidth = 1;
	private DeltaValue gridDelta = new DeltaValue(0.1, 0.1);
	private Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST;
	private Grid.Type gridType = Grid.Type.DOT;

	// CrossLine parameterei
	private CrossLine myCrossLine;
	private PositionValue crossLinePosition = new PositionValue(0, 0);
	private Color crossLineColor = Color.red;
	private int crossLineWidthInPixel = 1;
	private Value crossLineLength = new LengthValue(0.05, 0.05);
	private CrossLine.PainterPosition crossLinePainterPosition = CrossLine.PainterPosition.DEEPEST;

	// Scale parameterei
	private Scale myScale;
	private double pixelPerCm = 42.1;
	private Scale.UNIT unit = Scale.UNIT.m;
	private double startScale = 9;
	private double rate = 1.2;

	private double lambda1 = 0.45;
	private double lambda2 = 0.45;
	private double lambda3 = 1.0;
	private double alfaE = 24;
	private double alfaI = 8;
	private double temperatureE = -13;
	private double temperatureI = 20;

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		new Test();
	}

	public Test() {

		thermicPointList = getResult();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Proba");
		this.setUndecorated(false);
		this.setSize(700, 600);
		this.createBufferStrategy(1);

		myCanvas = new MCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle);
		myCanvas.addPainterListenerToHighest(new PainterListener() {

			@Override
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				thermicPointList.drawCurrentByArrow(canvas, g2);
			}

			@Override
			public void paintByViewer(MCanvas canvas, Graphics2D g2) {
			}
		});

		myGrid = new Grid(myCanvas, gridType, gridColor, gridWidth,	gridPosition, gridDelta);

		myCrossLine = new CrossLine(myCanvas, crossLinePosition,
				crossLineColor, crossLineWidthInPixel, crossLineLength,
				crossLinePainterPosition);

		myScale = new Scale(myCanvas, pixelPerCm, unit, startScale, rate);
		myScale.addScaleChangeListener(new ScaleChangeListener() {

			@Override
			public void getScale(Value scale) {
				DecimalFormat df = new DecimalFormat("#.00");

				if (scale.getX() < 1.0) {
					// canvasControl.setStatusPanelXScale( "xM=" +
					// df.format(1/scale.getX() ) + ":1" );
				} else {
					// canvasControl.setStatusPanelXScale( "xM=1:" +
					// df.format(scale.getX() ) );
				}

				if (scale.getY() < 1.0) {
					// canvasControl.setStatusPanelYScale( "yM=" +
					// df.format(1/scale.getY() ) + ":1" );
				} else {
					// canvasControl.setStatusPanelYScale( "yM=1:" +
					// df.format(scale.getY() ) );
				}
			}
		});

		this.getContentPane().setLayout(new BorderLayout(10, 10));
		this.getContentPane().add(myCanvas, BorderLayout.CENTER);
		this.setVisible(true);

		

		
//		myCanvas.revalidateAndRepaintCoreCanvas();

/*		for (int i = 0; i < thermicPointList.getSize(); i++) {

			System.out.println(thermicPointList.get(i));
		}
*/		
	}

	

	private ThermicPointList getResult() {

		
		Element hWall = new Element( lambda1, new Position(0, 0.7), new Position(1.0, 1.0));
		//hWall.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( 0.0, 1.0), alfaE, temperatureE ) );
		//hWall.setCloseElement(new SurfaceClose( SideOrientation.WEST, new Length( 0.7, 1.0), alfaE, temperatureE ) );
		hWall.setCloseElement(new SurfaceClose( SideOrientation.SOUTH, new Length( 0.3, 1.0), alfaI, temperatureI ) );
		
		Element vWall = new Element( lambda1, new Position(0,0), new Position(0.3, 0.7 ) );
		//vWall.setCloseElement(new SurfaceClose( SideOrientation.WEST, new Length( 0.0, 0.7), alfaE, temperatureE ) );
		vWall.setCloseElement(new SurfaceClose( SideOrientation.EAST, new Length( 0.0, 0.7 ), alfaI, temperatureI ) );
		
		Element hInsul = new Element( lambda2, new Position( 0, 1.0), new Position(1.0, 1.1 ) );
		hInsul.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( 0.0, 1.0 ), alfaE, temperatureE ) );
				
		Element vInsul = new Element( lambda2, new Position(-0.1, 0), new Position(0, 1.1) );
		vInsul.setCloseElement(new SurfaceClose( SideOrientation.WEST, new Length( 0, 1.1), alfaE, temperatureE ) );
		vInsul.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( -0.1, 0.0 ), alfaE, temperatureE ) );
		
		ElementSet elementSet = new ElementSet();
		elementSet.add( vWall );
		elementSet.add( hWall );
		elementSet.add( hInsul );
		elementSet.add( vInsul );
		
/*		Element pillar1 = new Element( lambda1, new Position(0,0), new Position( 0.3,0.3 ));
		
		pillar1.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( 0.0, 3.0), alfaE, temperatureE ) );
		pillar1.setCloseElement(new SurfaceClose( SideOrientation.SOUTH, new Length( 0.0, 3.0), alfaI, temperatureI ) );
		
		pillar1.setCloseElement(new SymmetricClose(	SideOrientation.WEST, new Length( 0.0, 3.0 ) ) );
		pillar1.setCloseElement(new SymmetricClose( SideOrientation.EAST, new Length( 0.0, 3.0) ) );
		//pillar1.setCloseElement(new SurfaceClose( SideOrientation.EAST, new Length( 0.0, 3.0), alfaI, temperatureI ) );
		
//		pillar1.setCloseElement(new SurfaceClose( SideOrientation.WEST, new Length( 0.0, 3.0), alfaI, temperatureI ) );		
//		pillar1.setCloseElement(new SurfaceClose( SideOrientation.EAST, new Length( 0.0, 3.0), alfaI, temperatureI ) );
		
		ElementSet elementSet = new ElementSet();
		elementSet.add( pillar1);
*/
		
		
/*		
		Element e1 = new Element( lambda2, new Position(0,0), new Position(0.3, 0.3));
//e1.setCloseElement(new SurfaceClose(SideOrientation.NORTH, new Length(0,0.3), alfaE, temperatureE ) );
		e1.setCloseElement(new SurfaceClose(SideOrientation.SOUTH, new Length(0,0.3), alfaI, temperatureI ) );		
		e1.setCloseElement(new SymmetricClose( SideOrientation.WEST, new Length( 0.0, 3.0 ) ) );
		
		Element e2 = new Element( lambda3, new Position(0.3,0), new Position(0.5, 0.3));
//e2.setCloseElement(new SurfaceClose(SideOrientation.NORTH, new Length(0.3,0.5), alfaE, temperatureE ) );
		e2.setCloseElement(new SurfaceClose(SideOrientation.SOUTH, new Length(0.3,0.5), alfaI, temperatureI ) );

		
		
		Element e3 = new Element( lambda2, new Position(0.5,0), new Position(0.8, 0.3));
//e3.setCloseElement(new SurfaceClose(SideOrientation.NORTH, new Length(0.5,0.8), alfaE, temperatureE ) );
		e3.setCloseElement(new SurfaceClose(SideOrientation.SOUTH, new Length(0.5,0.8), alfaI, temperatureI ) );
		e3.setCloseElement(new SymmetricClose( SideOrientation.EAST, new Length( 0.0, 3.0 ) ) );
		
		
		Element e4 = new Element( lambda1, new Position(0,0.3), new Position(0.8, 0.4));
		e4.setCloseElement(new SurfaceClose(SideOrientation.NORTH, new Length(0,0.8), alfaE, temperatureE ) );
		e4.setCloseElement(new SymmetricClose( SideOrientation.EAST, new Length( 0.3, 4.0 ) ) );
		e4.setCloseElement(new SymmetricClose( SideOrientation.WEST, new Length( 0.3, 4.0 ) ) );
		
		ElementSet elementSet = new ElementSet();
		elementSet.add(e1);
System.err.println(elementSet.getHorizontalMaximumDifference() + " - " + elementSet.getVerticalMaximumDifference());		
		elementSet.add(e2);
System.err.println(elementSet.getHorizontalMaximumDifference() + " - " + elementSet.getVerticalMaximumDifference());		
		elementSet.add(e3);
System.err.println(elementSet.getHorizontalMaximumDifference() + " - " + elementSet.getVerticalMaximumDifference());
		elementSet.add(e4);
System.err.println(elementSet.getHorizontalMaximumDifference() + " - " + elementSet.getVerticalMaximumDifference());		

//System.err.println(elementSet.getHorizontalMaximumDifference() + ", " + elementSet.getVerticalMaximumDifference() );
//ThermicPointList list = elementSet.divideElements( 0.01, 0.01 );	
*/

thermicPointList = elementSet.divideElements( 0.01, 0.01 );

thermicPointList.solve(0.001);		
		
		return thermicPointList;
	}

}


