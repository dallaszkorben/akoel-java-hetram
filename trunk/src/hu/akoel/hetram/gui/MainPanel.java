package hu.akoel.hetram.gui;

import hu.akoel.hetram.Element;
import hu.akoel.hetram.ElementSet;
import hu.akoel.hetram.SurfaceClose;
import hu.akoel.hetram.SymmetricClose;
import hu.akoel.hetram.ThermicPoint;
import hu.akoel.hetram.ThermicPointList;
import hu.akoel.hetram.Element.SideOrientation;
import hu.akoel.hetram.ThermicPointList.CURRENT_TYPE;
import hu.akoel.hetram.accessories.Length;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.listeners.CalculationListener;
import hu.akoel.hetram.test.Test;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class MainPanel extends JFrame{

	private static final long serialVersionUID = 3911667532503747257L;

	private ThermicPointList thermicPointList = null;
	private ElementSet elementSet = null;
		
	private StatusLine statusLine;
	private SettingTabbedPanel controlPanel;

	// GCanvas parameterei
	private MCanvas myCanvas;
	private Color background = Color.black;
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits( new PixelPerUnitValue(1, 1));
	private TranslateValue positionToMiddle = new TranslateValue(0.3, 0.6);

	private CalculationListener calculationListener = null;
	
	//------------------------
	//
	//Kiindulasi parameterek
	//
	//------------------------

	//
	//ControlSettings
	//
	double calculationPrecision = 0.001;
	
	//
	//VisibilitySettings
	//
	private boolean needDrawTemperatureByColor = true;

	private boolean needDrawThermicPoint = false;
	private Color thermicPointColor = Color.green;
	private double thermicPointRadius = 0.002;

	private boolean needDrawTemperatureByFont = false;
	private boolean needDrawCurrentByArrow = true;
	private CURRENT_TYPE currentType = CURRENT_TYPE.TRAJECTORY;
	
	//
	//CanvasSettings
	//	
	// Axis parameterei	
	private boolean needDrawAxis = false;
	private Axis myAxis;
	private Color axisColor = Color.yellow;
	private int axisWidthInPixel = 1;
	private Axis.AxisPosition axisPosition = Axis.AxisPosition.AT_LEFT_BOTTOM;
	private Axis.PainterPosition painterPosition = Axis.PainterPosition.HIGHEST;	

	// Grid parameterei
	private boolean needDrawGrid = true;
	private Grid myGrid;
	private Color gridColor = Color.green;
	private int gridWidth = 1;
	private DeltaValue gridDelta = new DeltaValue(0.1, 0.1);
	private Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST;
	private Grid.Type gridType = Grid.Type.DOT;

	// CrossLine parameterei
	private boolean needDrawCrossline = true;
	private CrossLine myCrossLine;
	private PositionValue crossLinePosition = new PositionValue(0, 0);
	private Color crossLineColor = Color.red;
	private int crossLineWidthInPixel = 1;
	private Value crossLineLength = new LengthValue(0.05, 0.05);
	private CrossLine.PainterPosition crossLinePainterPosition = CrossLine.PainterPosition.DEEPEST;

	// Scale parameterei
	private Scale myScale;
	private double pixelPerCm = 20.57;//42.1;
	private Scale.UNIT unit = Scale.UNIT.m;
	private double startScale = 10;
	private double rate = 1.2;	
	
	//
	//Temporary data
	//
	private double lambda1 = 0.67;
	private double lambda2 = 0.20;
	private double lambda3 = 1.0;
	private double alfaE = 8;
	private double alfaI = 24;
	private double temperatureE = 20;
	private double temperatureI = -15;
		
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		new Test();
	}

	public MCanvas getMCanvas(){
		return myCanvas;
	}
	
	public Grid getGrid(){
		return myGrid;
	}
	
	public CrossLine getCrossLine(){
		return myCrossLine;
	}
	
	public Scale getScale(){
		return myScale;
	}
	
	public Axis getAxis(){
		return myAxis;
	}
	
	public MainPanel( ){

//Feltoltom csak. nincs szamitas		
elementSet = temporarelyGenerateElementSet();
			
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Proba");
		this.setUndecorated(false);
		this.setSize(700, 600);
		this.createBufferStrategy(1);

		myCanvas = new MCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle);
		myCanvas.addPainterListenerToHighest(new PainterListener() {

			@Override
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				if( needDrawTemperatureByColor && null != thermicPointList ){
					thermicPointList.drawTemperatureByColor(canvas, g2);
				}
				if( needDrawThermicPoint && null != thermicPointList ){
					thermicPointList.drawPoint(canvas, g2, thermicPointColor, thermicPointRadius );
				}
				if( needDrawTemperatureByFont && null != thermicPointList ){
					thermicPointList.drawPointTemperatureByFont(canvas, g2);
				}
				if( needDrawCurrentByArrow && null != thermicPointList ){
					thermicPointList.setCurrentType(currentType);
					thermicPointList.drawCurrent(canvas, g2);
				}
			}

			@Override
			public void paintByViewer(MCanvas canvas, Graphics2D g2) {
			}
		});

		myGrid = new Grid(myCanvas, gridType, gridColor, gridWidth,	gridPosition, gridDelta);

		myCrossLine = new CrossLine(myCanvas, crossLinePosition, crossLineColor, crossLineWidthInPixel, crossLineLength,	crossLinePainterPosition);

		myScale = new Scale(myCanvas, pixelPerCm, unit, startScale, rate);

		myAxis = new Axis(myCanvas, axisPosition, axisColor, axisWidthInPixel, painterPosition);
			
		//Meretarany valtozas kijelzese
		myScale.addScaleChangeListener(new ScaleChangeListener() {
			@Override
			public void getScale(Value scale) {
				statusLine.setScale( scale.getX() );
			}
		});
			
		//Pozicio kijelzese
		myCanvas.addPositionChangeListener(new PositionChangeListener() {			
			@Override
			public void getWorldPosition(double xPosition, double yPosition) {
				statusLine.setXPosition( xPosition );
				statusLine.setYPosition( yPosition );
			}
		});

		//Homerseklet es Hoaram kijelzese
		myCanvas.addPositionChangeListener(new PositionChangeListener() {			
			@Override
			public void getWorldPosition(double xPosition, double yPosition) {				
					
				if( null != thermicPointList ){
					
					ThermicPoint tp = thermicPointList.getThermicPointByPosition(xPosition, yPosition);
					
					if( null == tp ){
						statusLine.setTemperature( null );
						statusLine.setQNorth( null );
						statusLine.setQEast( null );
						statusLine.setQSouth( null );
						statusLine.setQWest( null );
							
					}else{					
						statusLine.setTemperature( tp.getActualTemperature() );
					
						statusLine.setQNorth( tp.getNorthCurrent() );
						statusLine.setQEast( tp.getEastCurrent() );
						statusLine.setQSouth( tp.getSouthCurrent() );
						statusLine.setQWest( tp.getWestCurrent() );
					}
				}													
			}
		});

		this.statusLine = new StatusLine();
		this.controlPanel = new SettingTabbedPanel( this );
			
		//Mezooszto
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,  myCanvas, controlPanel );
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(400);
		splitPane.setResizeWeight(1.0);

			
		this.getContentPane().setLayout(new BorderLayout(10, 10));
		this.getContentPane().add( splitPane, BorderLayout.CENTER );
		this.getContentPane().add( statusLine, BorderLayout.SOUTH );
		this.setVisible(true);

		//Meretarany kezdoertek kiirasa
		statusLine.setScale( myScale.getScale().getX() );
			
//			for (int j = 0; j < thermicPointList.getSize(); j++) {
//				System.out.println(thermicPointList.get(j));
//			}
	}
	
	public void setThermicPointList( ThermicPointList thermicPointList ){
		this.thermicPointList = thermicPointList;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	public void setNeedDrawTemperatureByColor( boolean need ){
		this.needDrawTemperatureByColor = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	public boolean isNeedDrawTemperatureByColor( ){
		return this.needDrawTemperatureByColor;
	}	
	
	public void setNeedDrawPoint( boolean need ){
		this.needDrawThermicPoint = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	public boolean isNeedDrawPoint(){
		return this.needDrawThermicPoint;		
	}
	
	public void setNeedDrawTemperatureByFont( boolean need ){
		this.needDrawTemperatureByFont = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public boolean isNeedDrawTemperatureByFont(){
		return this.needDrawTemperatureByFont;
	}

	public void setNeedDrawCurrent( boolean need ){
		this.needDrawCurrentByArrow = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public void setDrawCurrentBy( CURRENT_TYPE currentType ){
		this.currentType = currentType;
		if( null != thermicPointList ){
			myCanvas.revalidateAndRepaintCoreCanvas();
		}
	}
	
	public CURRENT_TYPE getDrawCurrentBy(){
		return this.currentType;
	}
	
	
	
	
	public boolean isNeedDrawAxis(){
		return needDrawAxis;
	}
	
	public void setNeedDrawAxis( boolean need ){
		this.needDrawAxis = need;
	}
	
	public boolean isNeedDrawCurrentByArrow(){
		return this.needDrawCurrentByArrow;
	}

	public void setNeedDrawGrid( boolean need ){
		this.needDrawGrid = need;
	}
	
	public boolean isNeedDrawGrid(){
		return this.needDrawGrid;
	}
	
	public void setNeedDrawCrossline( boolean need ){
		this.needDrawCrossline = need;
	}
	
	public boolean isNeedDrawCrossLine(){
		return this.needDrawCrossline;
	}
	
	public Double getHorizontalMaximumDifference(){
		return elementSet.getHorizontalMaximumDifference();
	}

	public Double getVerticalMaximumDifference(){
		return elementSet.getVerticalMaximumDifference();
	}

	public Double getHorizontalAppliedDifference(){
		return elementSet.getHorizontalAppliedDifference();
	}

	public Double getVerticalAppliedDifference(){
		return elementSet.getVerticalAppliedDifference();
	}

	public int getHorizontalDifferenceDivider(){
		return elementSet.getHorizontalDifferenceDivider();
	}
	
	public int getVerticalDifferenceDivider(){
		return elementSet.getVerticalDifferenceDivider();
	}
	
	public void setVerticalDifferenceDivider( int verticalDifferenceDivider ){
		elementSet.setVerticalDifferenceDivider(verticalDifferenceDivider);
	}

	public void setHorizontalDifferenceDivider( int horizontalDifferenceDivider ){
		elementSet.setHorizontalDifferenceDivider(horizontalDifferenceDivider);
	}

	public double getThermicPointRadius(){
		return thermicPointRadius;
	}
	
	public void setThermicPointRadius( double thermicPointRadius ){
		this.thermicPointRadius = thermicPointRadius;
	}
	
	public double getPixelPerCm(){
		return this.pixelPerCm;
	}
	
	public void setPixelPerCm( double pixelPerCm ){
		this.pixelPerCm = pixelPerCm;
	}
	
	public double getCalculationPrecision(){
		return calculationPrecision;
	}
	
	public void setCalculationPrecision( double calculationPrecision ){
		this.calculationPrecision = calculationPrecision;
	}
	
	public void setCalculationListener( CalculationListener calculationListener ){
		this.calculationListener = calculationListener;
	}
	
	public void doCalculate( double precision ){
		thermicPointList = elementSet.generateThermicPoints();
		
		//Figyelo osztaly a szamitas nyomonkovetesere
		if( null != calculationListener ){
			thermicPointList.setCalculationListener(calculationListener);
		}
		
		thermicPointList.solve( precision );		
	}
	
	public void revalidateAndRepaint(){
		myCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	private ElementSet temporarelyGenerateElementSet() {
		
		//
		// Falsarok
		//

		Element hWall = new Element( lambda1, new Position(0, 0.7), new Position(1.0, 1.0));
		hWall.setCloseElement(new SurfaceClose( SideOrientation.SOUTH, new Length( 0.3, 1.0), alfaI, temperatureI ) );
		
		Element vWall = new Element( lambda1, new Position(0,0), new Position(0.3, 0.7 ) );
		vWall.setCloseElement(new SurfaceClose( SideOrientation.EAST, new Length( 0.0, 0.7 ), alfaI, temperatureI ) );
		
		Element hInsul = new Element( lambda2, new Position( 0, 1.0), new Position(1.0, 1.1 ) );
		hInsul.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( 0.0, 1.0 ), alfaE, temperatureE ) );
				
		Element vInsul = new Element( lambda2, new Position(-0.1, 0), new Position(0, 1.1) );
		vInsul.setCloseElement(new SurfaceClose( SideOrientation.WEST, new Length( 0, 1.1), alfaE, temperatureE ) );
		vInsul.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( -0.1, 0.0 ), alfaE, temperatureE ) );

		
/*		//
		// Egyenes fal
		//
		Element hWall = new Element( lambda1, new Position(0.0, 0.0), new Position(0.38, 0.38));
		//hWall.setCloseElement(new SurfaceClose( SideOrientation.SOUTH, new Length( 0.0, 0.38), alfaI, temperatureI ) );
		//hWall.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( 0.0, 0.38), alfaE, temperatureE ) );
		hWall.setCloseElement(new SurfaceClose( SideOrientation.NORTH, new Length( 0.0, 0.38), alfaI, temperatureI ) );
		hWall.setCloseElement(new SurfaceClose( SideOrientation.SOUTH, new Length( 0.0, 0.38), alfaE, temperatureE ) );
		//hWall.setCloseElement(new SymmetricClose( SideOrientation.WEST, new Length( 0.0, 0.38) ) );
		//hWall.setCloseElement(new SymmetricClose( SideOrientation.EAST, new Length( 0.0, 0.38) ) );
*/
		
		ElementSet es;
		
		es = new ElementSet();
		es.add( vWall );
		es.add( hWall );
		es.add( hInsul );
		es.add( vInsul );

		return es;
	}
}
