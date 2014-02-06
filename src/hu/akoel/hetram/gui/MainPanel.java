package hu.akoel.hetram.gui;

import hu.akoel.hetram.Test;
import hu.akoel.hetram.accessories.Length;
import hu.akoel.hetram.accessories.Orientation;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.gui.ElementSettingTab.DRAWING_ELEMENT;
import hu.akoel.hetram.gui.ElementSettingTab.HOMOGEN_PATTERN;
import hu.akoel.hetram.gui.ElementSettingTab.PATTERN_TYPE;
import hu.akoel.hetram.gui.ElementSettingTab.ROW_PATTERN;
import hu.akoel.hetram.listeners.CalculationListener;
import hu.akoel.hetram.structures.StructureSet;
import hu.akoel.hetram.structures.Structure;
import hu.akoel.hetram.structures.SurfaceSealing;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.hetram.thermicpoint.ThermicPointList.CURRENT_TYPE;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.drawnblock.DrawnBlockFactory;
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

	private static final int DEFAULT_WIDTH = 700;
	private static final int DEFAULT_HEIGHT = 700;
	private static final int DEFAULT_SETTINGTABBEDPANEL = 310;
	
		
	private ThermicPointList thermicPointList = null;
	private StructureSet elementSet = null;
		
	private StatusLine statusLine;
	private SettingTabbedPanel controlPanel;

	// Canvas parameterei
	private DrawnBlockCanvas myCanvas;
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
	//Rajzi elemek - ElementSettings
	//	
	private DRAWING_ELEMENT drawingElement = DRAWING_ELEMENT.BUILDINGELEMENT;
	private double buildingStructureLambda = 0.02;
	private Color elementLineColor = Color.blue;
	private Color elementBackgroundColor = Color.black;
	private PATTERN_TYPE patternType = PATTERN_TYPE.COLOR;
	private HOMOGEN_PATTERN homogenPattern = HOMOGEN_PATTERN.HATCH;
	private ROW_PATTERN rowPattern = ROW_PATTERN.ZIGZAG;

	//
	//Vezerles - ControlSettings
	//
	double calculationPrecision = 0.001;
	
	//
	//Megjelenites - VisibilitySettings
	//
	private boolean needDrawTemperatureByColor = true;

	private boolean needDrawThermicPoint = false;
	private Color thermicPointColor = Color.green;
	private double thermicPointRadius = 0.002;

	private boolean needDrawTemperatureByFont = false;
	private boolean needDrawCurrentByArrow = false;
	private CURRENT_TYPE currentType = CURRENT_TYPE.TRAJECTORY;
	
	//
	//Rajzolofelulet - CanvasSettings
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
	private double temperatureE = -15;
	private double temperatureI = 20;
		
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		new Test();
	}

	public DrawnBlockCanvas getCanvas(){
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
		this.setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		this.createBufferStrategy(1);

		myCanvas = new DrawnBlockCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle);
		
		//
		// Szamitott eredmenyek grafikus megjelenitese a legfelsobb layer-en
		//
		myCanvas.addPainterListenerToHighest(new PainterListener() {

			@Override
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				
				//
				// Ha szukseges a homerseklet-szin megjelenitetse
				//
				if( needDrawTemperatureByColor && null != thermicPointList ){
					thermicPointList.drawTemperatureByColor(canvas, g2);
				}
				
				//
				// Ha szukseges a termikus PONT megjelenitese
				//
				if( needDrawThermicPoint && null != thermicPointList ){
					thermicPointList.drawPoint(canvas, g2, thermicPointColor, thermicPointRadius );
				}
				
				//
				// Ha szukseges a homerseklet megjelenitese
				//
				if( needDrawTemperatureByFont && null != thermicPointList ){
					thermicPointList.drawPointTemperatureByFont(canvas, g2);
				}
				
				// Ha szukseges a hoaram megjelenitese (vektor, vektorpar, trajektoria)
				if( needDrawCurrentByArrow && null != thermicPointList ){
					thermicPointList.setCurrentType(currentType);
					thermicPointList.drawCurrent(canvas, g2);
				}
			}

			@Override
			public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {
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
		myCanvas.addCursorPositionChangeListener(new CursorPositionChangeListener() {			
			@Override
			public void getWorldPosition(double xPosition, double yPosition) {
				statusLine.setXPosition( xPosition );
				statusLine.setYPosition( yPosition );
			}
		});

		//Homerseklet es Hoaram kijelzese
		myCanvas.addCursorPositionChangeListener(new CursorPositionChangeListener() {			
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

		//
		// Status Line letrehozasa
		//
		this.statusLine = new StatusLine();
		
		//
		// Vezerlo egyseg letrehozasa
		//
		this.controlPanel = new SettingTabbedPanel( this );
			
		//
		//Mezooszto
		//
		// baloldalan a rajzfelulet
		// jobboldalan a vezerloegyseg
		//
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,  myCanvas, controlPanel );
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation( DEFAULT_WIDTH - DEFAULT_SETTINGTABBEDPANEL );
		splitPane.setResizeWeight(1.0);

			
		this.getContentPane().setLayout(new BorderLayout(10, 10));
		
		//
		// A rajzfelulet a vezerloegyseggel jobboldalan, elvalasztva egymastol
		//
		this.getContentPane().add( splitPane, BorderLayout.CENTER );
		
		//
		// Status line
		//
		this.getContentPane().add( statusLine, BorderLayout.SOUTH );
		
		this.setVisible(true);

		//Meretarany kezdoertek kiirasa
		statusLine.setScale( myScale.getScale().getX() );
			
//			for (int j = 0; j < thermicPointList.getSize(); j++) {
//				System.out.println(thermicPointList.get(j));
//			}
	}
	
	
	//-----------------------------------
	//
	// Megjelenites - Visibility setting
	//
	//-----------------------------------
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
	
	
	
	//-------------------------------
	//
	// Rajzolofelulet - CanvasSetting
	//
	//-------------------------------
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
	
	
	//------------------------------
	//
	// Rajzi elemenk -ElementSetting
	//
	//------------------------------
	public void setDrawingElement( DRAWING_ELEMENT drawingElement ){
		this.drawingElement = drawingElement;
	}
	
	public DRAWING_ELEMENT getDrawingElement(){
		return this.drawingElement;
	}
	
	public void setBuildingStructureLambda( double buildingStructureLambda ){
		this.buildingStructureLambda = buildingStructureLambda;
	}
	
	public double getBuildingStructureLambda(){
		return this.buildingStructureLambda;
	}
	
	public Color getElementLineColor(){
		return elementLineColor;
	}
	
	public void setElementLineColor( Color color ){
		this.elementLineColor = color;
	}
	
	public Color getElementBackgroundColor(){
		return elementBackgroundColor;
	}
	
	public void setElementBackgroundColor( Color color ){
		this.elementBackgroundColor = color;
	}
	
	public PATTERN_TYPE getPatternType() {
		return patternType;
	}

	public void setPatternType(PATTERN_TYPE patternType) {
		this.patternType = patternType;
	}
	
	public HOMOGEN_PATTERN getHomogenPattern() {
		return homogenPattern;
	}

	public void setHomogenPattern(HOMOGEN_PATTERN homogenPattern) {
		this.homogenPattern = homogenPattern;
	}

	public ROW_PATTERN getRowPattern() {
		return rowPattern;
	}

	public void setRowPattern(ROW_PATTERN rowPattern) {
		this.rowPattern = rowPattern;
	}
	
	public void setDrawnBlockFactory( DrawnBlockFactory dbf ){
		this.myCanvas.setDrawnBlockFactory( dbf );
	}
	
	
	/**
	 * Termikus pontok letrehozasa es homersekleteik kiszamitasa
	 * 
	 * @param precision
	 */
	public void doCalculate( double precision ){
		
		//Termikus pontok legyartasa, kozottuk levo kapcsolatok megteremtese (nics szamolas meg) 
		thermicPointList = elementSet.generateThermicPoints();
		
		//Figyelo osztaly a szamitas nyomonkovetesere
		if( null != calculationListener ){
			thermicPointList.setCalculationListener(calculationListener);
		}
		
		//Sokismeretlenes egyenletrendszer megoldasa, eredmenye: a termikus pontok homerseklete
		thermicPointList.solve( precision );		
	}
	
	public void revalidateAndRepaint(){
		myCanvas.revalidateAndRepaintCoreCanvas();
	}
	
	private StructureSet temporarelyGenerateElementSet() {
		
		//
		// Falsarok
		//

		Structure hWall = new Structure( lambda1, new Position(0, 0.7), new Position(1.0, 1.0));
		hWall.setCloseElement(new SurfaceSealing( Orientation.SOUTH, new Length( 0.3, 1.0), alfaI, temperatureI ) );
		
		Structure vWall = new Structure( lambda1, new Position(0,0), new Position(0.3, 0.7 ) );
		vWall.setCloseElement(new SurfaceSealing( Orientation.EAST, new Length( 0.0, 0.7 ), alfaI, temperatureI ) );
		
		Structure hInsul = new Structure( lambda2, new Position( 0, 1.0), new Position(1.0, 1.1 ) );
		hInsul.setCloseElement(new SurfaceSealing( Orientation.NORTH, new Length( 0.0, 1.0 ), alfaE, temperatureE ) );
				
		Structure vInsul = new Structure( lambda2, new Position(-0.1, 0), new Position(0, 1.1) );
		vInsul.setCloseElement(new SurfaceSealing( Orientation.WEST, new Length( 0, 1.1), alfaE, temperatureE ) );
		vInsul.setCloseElement(new SurfaceSealing( Orientation.NORTH, new Length( -0.1, 0.0 ), alfaE, temperatureE ) );

		
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
		
		StructureSet es;
		
		es = new StructureSet();
		es.add( vWall );
		es.add( hWall );
		es.add( hInsul );
		es.add( vInsul );

		return es;
	}
}
