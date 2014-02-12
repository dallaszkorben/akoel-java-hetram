package hu.akoel.hetram.gui;

import hu.akoel.hetram.HetramCanvas;
import hu.akoel.hetram.HetramDrawnElementFactory;
import hu.akoel.hetram.Hetram;
import hu.akoel.hetram.gui.ElementSettingTab.DRAWING_ELEMENT;
import hu.akoel.hetram.gui.ElementSettingTab.HOMOGEN_PATTERN;
import hu.akoel.hetram.gui.ElementSettingTab.PATTERN_TYPE;
import hu.akoel.hetram.gui.ElementSettingTab.ROW_PATTERN;
import hu.akoel.hetram.listeners.CalculationListener;
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
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas.Precision;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

public class MainPanel extends JFrame{

	private static final long serialVersionUID = 3911667532503747257L;

	private static final String version = "v 0.0.2";
	
	private static final int DEFAULT_WIDTH = 700;
	private static final int DEFAULT_HEIGHT = 700;
	private static final int DEFAULT_SETTINGTABBEDPANEL = 310;
	
	private static final Precision precision = Precision.ONE_100000;
		
	private ThermicPointList thermicPointList = null;
		
	private StatusLine statusLine;
	private SettingTabbedPanel controlPanel;

	// Canvas parameterei
	private HetramCanvas myCanvas;
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
	private double openEdgeAlphaBegin = 8;
	private double openEdgeAlphaEnd = 8;
	private double openEdgeTemperature = 20;
	private Color openEdgeColor = Color.red;
	private Color symmetricEdgeColor = Color.green;

	//
	//Vezerles - ControlSettings
	//
	
	private BigDecimal verticalMaximumDifference = null;
	private BigDecimal horizontalMaximumDifference = null;
	
	private BigDecimal verticalAppliedDifference;
	private BigDecimal horizontalAppliedDifference;
	
	private int verticalDifferenceDivider = 1;
	private int horizontalDifferenceDivider = 1;
	
	private double calculationPrecision = 0.001;
	
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
	
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		new Hetram();
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
	
	JMenuBar menuBar;
	JMenu fileMainMenu;
	JMenuItem fileSaveMenuItem;
	JMenuItem fileLoadMenuItem;
	JMenu helpMainMenu;
	
	public MainPanel( ){
			
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Hetram " + version );
		this.setUndecorated(false);
		this.setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		this.createBufferStrategy(1);

		//
		//Menu keszites
		//
		
		//Create the menu bar.
		menuBar = new JMenuBar();

		//File
		fileMainMenu = new JMenu("File");
		fileMainMenu.setMnemonic(KeyEvent.VK_F);
		fileMainMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(fileMainMenu);
		
		//File-Save
		fileSaveMenuItem = new JMenuItem( "Save", KeyEvent.VK_S ); //Mnemonic Akkor ervenyes ha lathato a menu elem
		fileSaveMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_1, ActionEvent.ALT_MASK ) ); //Mindegy hogy lathato-e a menu vagy sem
		fileSaveMenuItem.getAccessibleContext().setAccessibleDescription( "This doesn't really do anything");
		fileSaveMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem source = (JMenuItem)(e.getSource());
		        String s = source.getText();
		        System.err.println(s);
				
			}
		});
		fileMainMenu.add(fileSaveMenuItem);
		
		//File-Load
		fileLoadMenuItem = new JMenuItem( "Load", KeyEvent.VK_L );
		fileLoadMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_2, ActionEvent.ALT_MASK ) );
		fileLoadMenuItem.getAccessibleContext().setAccessibleDescription( "This doesn't really do anything");
		fileMainMenu.add(fileLoadMenuItem);
		
		//Elvalasztas
		fileMainMenu.addSeparator();
		
		//Help
		menuBar.add(Box.createHorizontalGlue());
		helpMainMenu = new JMenu("Help");
		helpMainMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMainMenu);
		
		
		this.setJMenuBar(menuBar);
		
		myCanvas = new HetramCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle, this, precision );
		
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
			public void getWorldPosition( double xPosition, double yPosition) {
				statusLine.setXPosition( xPosition );
				statusLine.setYPosition( yPosition );
			}
		});

		//Homerseklet es Hoaram kijelzese
		myCanvas.addCursorPositionChangeListener(new CursorPositionChangeListener() {			
			@Override
			public void getWorldPosition( double xPosition, double yPosition) {				
					
				if( null != thermicPointList ){
					
					ThermicPoint tp = thermicPointList.getThermicPointByPosition( xPosition, yPosition );
					
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
		this.statusLine = new StatusLine( this );
		
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
	// Vezerlo felulet - ContolSetting
	//
	//-----------------------------------
	
	public BigDecimal getHorizontalMaximumDifference(){
		return horizontalMaximumDifference;
	}
	
	public void setHorizontalMaximumDifference( BigDecimal horizontalMaximumDifference ){
		this.horizontalMaximumDifference = horizontalMaximumDifference;
		controlPanel.controlSettingTab.setHorizontalMaximumDifference( horizontalMaximumDifference );
	}

	public BigDecimal getVerticalMaximumDifference(){
		return verticalMaximumDifference;
	}

	public void setVerticalMaximumDifference( BigDecimal verticalMaximumDifference ){
		this.verticalMaximumDifference = verticalMaximumDifference;
		controlPanel.controlSettingTab.setVerticalMaximumDifference(verticalMaximumDifference);
	}
	
	public void setHorizontalAppliedDifference( BigDecimal difference ){
		this.horizontalAppliedDifference = difference;
		controlPanel.controlSettingTab.setHorizontalAppliedDifference(difference);
	}
	
	public BigDecimal getHorizontalAppliedDifference(){
		return horizontalAppliedDifference;
	}

	public void setVerticalAppliedDifference( BigDecimal difference ){
		this.verticalAppliedDifference = difference;
		controlPanel.controlSettingTab.setVerticalAppliedDifference(difference);
	}

	public BigDecimal getVerticalAppliedDifference(){
		return verticalAppliedDifference;
	}

	public int getHorizontalDifferenceDivider(){
		return horizontalDifferenceDivider;
	}
	
	public int getVerticalDifferenceDivider(){
		return verticalDifferenceDivider;
	}
	
	public void setVerticalDifferenceDivider( int verticalDifferenceDivider ){
		this.verticalDifferenceDivider = verticalDifferenceDivider;
	}

	public void setHorizontalDifferenceDivider( int horizontalDifferenceDivider ){
		this.horizontalDifferenceDivider = horizontalDifferenceDivider;
	}
	
	public void doGenerateMaximumDifference(){
		myCanvas.doGenerateMaximumDifference();
//System.err.println(getHorizontalMaximumDifference() + ", " + getVerticalMaximumDifference());		
	}
	
	//-----------------------------------
	//
	// Megjelenites - VisibilitySetting
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
	
	public double getOpenEdgeAlphaBegin() {
		return openEdgeAlphaBegin;
	}

	public void setAlphaBegin(double alphaBegin) {
		this.openEdgeAlphaBegin = alphaBegin;
	}

	public double getOpenEdgeAlphaEnd() {
		return openEdgeAlphaEnd;
	}

	public void setAlphaEnd(double alphaEnd) {
		this.openEdgeAlphaEnd = alphaEnd;
	}

	public double getOpenEdgeTemperature() {
		return openEdgeTemperature;
	}

	public void setOpenEdgeTemperature(double temperature) {
		this.openEdgeTemperature = temperature;
	}

	public Color getOpenEdgeColor() {
		return openEdgeColor;
	}

	public void setOpenEdgeColor(Color openEdgeColor) {
		this.openEdgeColor = openEdgeColor;
	}
	
	public Color getSymmetricEdgeColor() {
		return symmetricEdgeColor;
	}

	public void setSymmetricEdgeColor(Color symmetricEdgeColor) {
		this.symmetricEdgeColor = symmetricEdgeColor;
	}

	public void setDrawnBlockFactory( HetramDrawnElementFactory dbf ){
		this.myCanvas.setDrawnBlockFactory( dbf );
	}
	
	
	/**
	 * Termikus pontok letrehozasa es homersekleteik kiszamitasa
	 * 
	 * @param precision
	 */
	public void doCalculate( double precision ){
		
		//Termikus pontok legyartasa, kozottuk levo kapcsolatok megteremtese (nics szamolas meg) 
		thermicPointList = myCanvas.generateThermicPoints();
//System.err.println(thermicPointList);		
		//thermicPointList = elementSet.generateThermicPoints();
		
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


}
