package hu.akoel.hetram.gui;

import hu.akoel.hetram.HetramCanvas;
import hu.akoel.hetram.HetramDrawnElementFactory;
import hu.akoel.hetram.Hetram;
import hu.akoel.hetram.gui.ElementSettingTab.DRAWING_ELEMENT;
import hu.akoel.hetram.gui.ElementSettingTab.HOMOGENEOUS_PATTERN;
import hu.akoel.hetram.gui.ElementSettingTab.PATTERN_TYPE;
import hu.akoel.hetram.gui.ElementSettingTab.ROW_PATTERN;
import hu.akoel.hetram.gui.drawingelements.ColoredPatternBuildingSturcturalElement;
import hu.akoel.hetram.gui.drawingelements.DotFullPatternAdapter;
import hu.akoel.hetram.gui.drawingelements.HatchFullPatternAdapter;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement.TYPE;
import hu.akoel.hetram.gui.drawingelements.HomogeneousPatternBuildingStructuralElement;
import hu.akoel.hetram.gui.drawingelements.HomogeneousPatternFactory;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.gui.drawingelements.RowPatternBuildingStructuralElement;
import hu.akoel.hetram.gui.drawingelements.RowPatternFactory;
import hu.akoel.hetram.gui.drawingelements.SymmetricEdgeElement;
import hu.akoel.hetram.gui.drawingelements.ZigZagRowPatternAdapter;
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
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MainPanel extends JFrame {

	private static final long serialVersionUID = 3911667532503747257L;

	private static final String version = "1.0.1";

	public static enum Mode {
		DRAWING("Rajz mód"), ANALYSIS("Elemzés mód"), CALCULATION("Szamítás");

		private String name;

		private Mode(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 800;
	private static final int DEFAULT_SETTINGTABBEDPANEL = 310;

	private static final Precision precision = Precision.per_1000;

	private ThermicPointList thermicPointList = null;

	private StatusLine statusLine;
	private SettingTabbedPanel settingTabbedPanel;
	private ModePanel modePanel;
	private JPanel containerPanel;

	// Canvas parameterei
	private HetramCanvas myCanvas;
	private Color background = Color.white;
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits( new PixelPerUnitValue( 1, 1 ) );
	private TranslateValue positionToMiddle = new TranslateValue( 0.3, 0.6 );

	private CalculationListener calculationListener = null;

	// ------------------------
	//
	// Kiindulasi parameterek
	//
	// ------------------------

	//
	// Mukodesi mode
	//
	private Mode mode = Mode.DRAWING;

	//
	// Rajzi elemek - ElementSettings
	//
	private DRAWING_ELEMENT drawingElement = DRAWING_ELEMENT.BUILDINGELEMENT;
	private double buildingStructureLambda = 0.02;
	private Color elementLineColor = Color.blue;
	private Color elementBackgroundColor = Color.black;
	private PATTERN_TYPE patternType = PATTERN_TYPE.COLOR;
	private HOMOGENEOUS_PATTERN homogenPattern = HOMOGENEOUS_PATTERN.HATCH;
	private ROW_PATTERN rowPattern = ROW_PATTERN.ZIGZAG;
	private double openEdgeAlphaBegin = 8;
	private double openEdgeAlphaEnd = 8;
	private double openEdgeTemperature = 20;
	private Color openEdgeColor = Color.red;
	private Color symmetricEdgeColor = Color.green;

	//
	// Vezerles - ControlSettings
	//

	private BigDecimal verticalMaximumDifference = null;
	private BigDecimal horizontalMaximumDifference = null;

	private BigDecimal verticalAppliedDifference;
	private BigDecimal horizontalAppliedDifference;

	private int verticalDifferenceDivider = 1;
	private int horizontalDifferenceDivider = 1;
	private double calculationPrecision = 0.001;

	//
	// Megjelenites - VisibilitySettings
	//
	private boolean needDrawTemperatureByColor = true;

	private boolean needDrawThermicPoint = false;
	private Color thermicPointColor = Color.green;
	private double thermicPointRadius = 0.002;

	private boolean needDrawTemperatureByFont = false;
	private boolean needDrawCurrentByArrow = false;
	private CURRENT_TYPE currentType = CURRENT_TYPE.TRAJECTORY;

	//
	// Rajzolofelulet - CanvasSettings
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
	private double pixelPerCm = 20.57;// 42.1;
	private Scale.UNIT unit = Scale.UNIT.m;
	private double startScale = 10;
	private double rate = 1.2;

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		new Hetram();
	}

	public DrawnBlockCanvas getCanvas() {
		return myCanvas;
	}

	public Grid getGrid() {
		return myGrid;
	}

	public CrossLine getCrossLine() {
		return myCrossLine;
	}

	public Scale getScale() {
		return myScale;
	}

	public Axis getAxis() {
		return myAxis;
	}

	JMenuBar menuBar;
	JMenu fileMainMenu;
	JMenuItem fileNewMenuItem;
	JMenuItem fileSaveMenuItem;
	JMenuItem fileLoadMenuItem;
	JMenu helpMainMenu;

	public MainPanel() {

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Hetram " + version);
		this.setUndecorated(false);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.createBufferStrategy(1);

		//
		// Menu keszites
		//

		// Create the menu bar.
		menuBar = new JMenuBar();

		// File
		fileMainMenu = new JMenu("File");
		fileMainMenu.setMnemonic(KeyEvent.VK_F);
		//fileMainMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(fileMainMenu);

		// File-New
		fileNewMenuItem = new JMenuItem("Új", KeyEvent.VK_N); // Mnemonic Akkor ervenyes ha lathato a menu elem
		fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK)); // Mindegy hogy lathato-e a menu vagy
		fileNewMenuItem.addActionListener(new NewActionListener());
		fileMainMenu.add(fileNewMenuItem);
		
		// File-Save
		fileSaveMenuItem = new JMenuItem("Mentés ...", KeyEvent.VK_S); // Mnemonic Akkor ervenyes ha lathato a menu elem
		fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK)); // Mindegy hogy lathato-e a menu vagy sem
		//fileSaveMenuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		fileSaveMenuItem.addActionListener(new SaveActionListener());
		fileMainMenu.add(fileSaveMenuItem);

		// File-Load
		fileLoadMenuItem = new JMenuItem("Betöltés", KeyEvent.VK_L);
		fileLoadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		//fileLoadMenuItem.getAccessibleContext().setAccessibleDescription( "This doesn't really do anything");
		fileLoadMenuItem.addActionListener(new LoadActionListener());
		fileMainMenu.add(fileLoadMenuItem);

		// Elvalasztas
		fileMainMenu.addSeparator();

		// Help
		menuBar.add(Box.createHorizontalGlue());
		helpMainMenu = new JMenu("Help");
		helpMainMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMainMenu);

		this.setJMenuBar(menuBar);

		myCanvas = new HetramCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle, this,	precision);

		//
		// Szamitott eredmenyek grafikus megjelenitese a legfelsobb layer-en
		//
		myCanvas.addPainterListenerToHighest(new PainterListener() {

			@Override
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {

				//
				// Ha szukseges a homerseklet-szin megjelenitetse
				//
				if (needDrawTemperatureByColor && null != thermicPointList) {
					thermicPointList.drawTemperatureByColor(canvas, g2);
				}

				//
				// Ha szukseges a termikus PONT megjelenitese
				//
				if (needDrawThermicPoint && null != thermicPointList) {
					thermicPointList.drawPoint(canvas, g2, thermicPointColor,
							thermicPointRadius);
				}

				//
				// Ha szukseges a homerseklet megjelenitese
				//
				if (needDrawTemperatureByFont && null != thermicPointList) {
					thermicPointList.drawPointTemperatureByFont(
							(DrawnBlockCanvas) canvas, g2);
				}

				// Ha szukseges a hoaram megjelenitese (vektor, vektorpar,
				// trajektoria)
				if (needDrawCurrentByArrow && null != thermicPointList) {
					thermicPointList.setCurrentType(currentType);
					thermicPointList.drawCurrent(canvas, g2);
				}
			}

			@Override
			public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {
			}
		});

		myGrid = new Grid(myCanvas, gridType, gridColor, gridWidth,
				gridPosition, gridDelta);

		myCrossLine = new CrossLine(myCanvas, crossLinePosition,
				crossLineColor, crossLineWidthInPixel, crossLineLength,
				crossLinePainterPosition);

		myScale = new Scale(myCanvas, pixelPerCm, unit, startScale, rate);

		myAxis = new Axis(myCanvas, axisPosition, axisColor, axisWidthInPixel,
				painterPosition);

		// Meretarany valtozas kijelzese
		myScale.addScaleChangeListener(new ScaleChangeListener() {
			@Override
			public void getScale(Value scale) {
				statusLine.setScale(scale.getX());
			}
		});

		// Pozicio kijelzese
		myCanvas.addCursorPositionChangeListener(new CursorPositionChangeListener() {
			@Override
			public void getWorldPosition(double xPosition, double yPosition) {
				statusLine.setXPosition(xPosition);
				statusLine.setYPosition(yPosition);
			}
		});

		// Homerseklet es Hoaram kijelzese
		myCanvas.addCursorPositionChangeListener(new CursorPositionChangeListener() {
			@Override
			public void getWorldPosition(double xPosition, double yPosition) {

				if (null != thermicPointList) {

					ThermicPoint tp = thermicPointList
							.getThermicPointByPosition(xPosition, yPosition);

					if (null == tp) {
						statusLine.setTemperature(null);
						statusLine.setQNorth(null);
						statusLine.setQEast(null);
						statusLine.setQSouth(null);
						statusLine.setQWest(null);

					} else {
						statusLine.setTemperature(tp.getActualTemperature());

						statusLine.setQNorth(tp.getNorthCurrent());
						statusLine.setQEast(tp.getEastCurrent());
						statusLine.setQSouth(tp.getSouthCurrent());
						statusLine.setQWest(tp.getWestCurrent());
					}
				}
			}
		});

		//
		// Status Line letrehozasa
		//
		this.statusLine = new StatusLine(this);

		//
		// Vezerlo egyseg letrehozasa
		//
		this.settingTabbedPanel = new SettingTabbedPanel(this);

		//
		// Mode
		//
		this.modePanel = new ModePanel(this);

		this.containerPanel = new JPanel();
		this.containerPanel.setLayout(new BoxLayout(containerPanel,	BoxLayout.Y_AXIS));
		this.containerPanel.add(settingTabbedPanel);
		this.containerPanel.add(Box.createVerticalGlue());
		this.containerPanel.add(modePanel);

		//
		// Mezooszto
		//
		// baloldalan a rajzfelulet
		// jobboldalan a vezerloegyseg
		//
		// JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
		// myCanvas, controlPanel );
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				myCanvas, containerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane
				.setDividerLocation(DEFAULT_WIDTH - DEFAULT_SETTINGTABBEDPANEL);
		splitPane.setResizeWeight(1.0);

		this.getContentPane().setLayout(new BorderLayout(10, 10));

		//
		// A rajzfelulet a vezerloegyseggel jobboldalan, elvalasztva egymastol
		//
		this.getContentPane().add(splitPane, BorderLayout.CENTER);

		//
		// Status line
		//
		this.getContentPane().add(statusLine, BorderLayout.SOUTH);

		this.setVisible(true);

		// Meretarany kezdoertek kiirasa
		statusLine.setScale(myScale.getScale().getX());

		// for (int j = 0; j < thermicPointList.getSize(); j++) {
		// System.out.println(thermicPointList.get(j));
		// }
	}

	public SettingTabbedPanel getSettingTabbedPanel() {
		return settingTabbedPanel;
	}

	// ------------------------------
	//
	// Mukodesi mod
	//
	// ------------------------------
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Mode getMode() {
		return this.mode;
	}

	public void setModeField(Mode mode) {
		modePanel.setModeField(mode);
	}

	// -----------------------------------
	//
	// Vezerlo felulet - ContolSetting
	//
	// -----------------------------------

	public BigDecimal getHorizontalMaximumDifference() {
		return horizontalMaximumDifference;
	}

	public void setHorizontalMaximumDifference(	BigDecimal horizontalMaximumDifference) {
		this.horizontalMaximumDifference = horizontalMaximumDifference;
		settingTabbedPanel.controlSettingTab.setHorizontalMaximumDifference(horizontalMaximumDifference);
	}

	public BigDecimal getVerticalMaximumDifference() {
		return verticalMaximumDifference;
	}

	public void setVerticalMaximumDifference(BigDecimal verticalMaximumDifference) {
		this.verticalMaximumDifference = verticalMaximumDifference;
		settingTabbedPanel.controlSettingTab.setVerticalMaximumDifference(verticalMaximumDifference);
	}

	public void setHorizontalAppliedDifference(BigDecimal difference) {
		this.horizontalAppliedDifference = difference;
/*		if( null != difference ){
			settingTabbedPanel.controlSettingTab.setAppliedXDeltaField( myCanvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( horizontalAppliedDifference ).toPlainString() );;
		}else{
			settingTabbedPanel.controlSettingTab.setAppliedXDeltaField("");
		}
*/		
	}

	public BigDecimal getHorizontalAppliedDifference() {
		return horizontalAppliedDifference;
	}

	public void setVerticalAppliedDifference(BigDecimal difference) {
		this.verticalAppliedDifference = difference;
/*		if( null != difference ){
			settingTabbedPanel.controlSettingTab.setAppliedYDeltaField( myCanvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( verticalAppliedDifference ).toPlainString() );;
		}else{
			settingTabbedPanel.controlSettingTab.setAppliedYDeltaField("");
		}
*/		
		
		// settingTabbedPanel.controlSettingTab.setVerticalAppliedDifference(difference);
	}

	public BigDecimal getVerticalAppliedDifference() {
		return verticalAppliedDifference;
	}

	public int getHorizontalDifferenceDivider() {
		return horizontalDifferenceDivider;
	}

	public int getVerticalDifferenceDivider() {
		return verticalDifferenceDivider;
	}

	public void setVerticalDifferenceDivider(int verticalDifferenceDivider) {
		this.verticalDifferenceDivider = verticalDifferenceDivider;
	}

	public void setHorizontalDifferenceDivider(int horizontalDifferenceDivider) {
		this.horizontalDifferenceDivider = horizontalDifferenceDivider;
	}

/*	public void doGenerateMaximumDifference() {
		myCanvas.doGenerateMaximumDifference();
		// System.err.println(getHorizontalMaximumDifference() + ", " +
		// getVerticalMaximumDifference());
	}
*/
/*	public void setEnableCalculateButton( boolean enable ){
		settingTabbedPanel.controlSettingTab.setEnableCalculateButton( enable );
	}
*/	
	// -----------------------------------
	//
	// Megjelenites - VisibilitySetting
	//
	// -----------------------------------
	public void setThermicPointList(ThermicPointList thermicPointList) {
		this.thermicPointList = thermicPointList;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public void setNeedDrawTemperatureByColor(boolean need) {
		this.needDrawTemperatureByColor = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public boolean isNeedDrawTemperatureByColor() {
		return this.needDrawTemperatureByColor;
	}

	public void setNeedDrawPoint(boolean need) {
		this.needDrawThermicPoint = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public boolean isNeedDrawPoint() {
		return this.needDrawThermicPoint;
	}

	public void setNeedDrawTemperatureByFont(boolean need) {
		this.needDrawTemperatureByFont = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public boolean isNeedDrawTemperatureByFont() {
		return this.needDrawTemperatureByFont;
	}

	public void setNeedDrawCurrent(boolean need) {
		this.needDrawCurrentByArrow = need;
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	public void setDrawCurrentBy(CURRENT_TYPE currentType) {
		this.currentType = currentType;
		if (null != thermicPointList) {
			myCanvas.revalidateAndRepaintCoreCanvas();
		}
	}

	public CURRENT_TYPE getDrawCurrentBy() {
		return this.currentType;
	}

	// -------------------------------
	//
	// Rajzolofelulet - CanvasSetting
	//
	// -------------------------------
	public boolean isNeedDrawAxis() {
		return needDrawAxis;
	}

	public void setNeedDrawAxis(boolean need) {
		this.needDrawAxis = need;
	}

	public boolean isNeedDrawCurrentByArrow() {
		return this.needDrawCurrentByArrow;
	}

	public void setNeedDrawGrid(boolean need) {
		this.needDrawGrid = need;
	}

	public boolean isNeedDrawGrid() {
		return this.needDrawGrid;
	}

	public void setNeedDrawCrossline(boolean need) {
		this.needDrawCrossline = need;
	}

	public boolean isNeedDrawCrossLine() {
		return this.needDrawCrossline;
	}

	public double getThermicPointRadius() {
		return thermicPointRadius;
	}

	public void setThermicPointRadius(double thermicPointRadius) {
		this.thermicPointRadius = thermicPointRadius;
	}

	public double getPixelPerCm() {
		return this.pixelPerCm;
	}

	public void setPixelPerCm(double pixelPerCm) {
		this.pixelPerCm = pixelPerCm;
	}

	public double getCalculationPrecision() {
		return calculationPrecision;
	}

	public void setCalculationPrecision(double calculationPrecision) {
		this.calculationPrecision = calculationPrecision;
	}

	public void setCalculationListener(CalculationListener calculationListener) {
		this.calculationListener = calculationListener;
	}

	// ------------------------------
	//
	// Rajzi elemenk -ElementSetting
	//
	// ------------------------------
	public void setDrawingElement(DRAWING_ELEMENT drawingElement) {
		this.drawingElement = drawingElement;
	}

	public DRAWING_ELEMENT getDrawingElement() {
		return this.drawingElement;
	}

	public void setBuildingStructureLambda(double buildingStructureLambda) {
		this.buildingStructureLambda = buildingStructureLambda;
	}

	public double getBuildingStructureLambda() {
		return this.buildingStructureLambda;
	}

	public Color getElementLineColor() {
		return elementLineColor;
	}

	public void setElementLineColor(Color color) {
		this.elementLineColor = color;
	}

	public Color getElementBackgroundColor() {
		return elementBackgroundColor;
	}

	public void setElementBackgroundColor(Color color) {
		this.elementBackgroundColor = color;
	}

	public PATTERN_TYPE getPatternType() {
		return patternType;
	}

	public void setPatternType(PATTERN_TYPE patternType) {
		this.patternType = patternType;
	}

	public HOMOGENEOUS_PATTERN getHomogenPattern() {
		return homogenPattern;
	}

	public void setHomogenPattern(HOMOGENEOUS_PATTERN homogenPattern) {
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

	public void setDrawnBlockFactory(HetramDrawnElementFactory dbf) {
		this.myCanvas.setDrawnBlockFactory(dbf);
	}

	/**
	 * Termikus pontok letrehozasa es homersekleteik kiszamitasa
	 * 
	 * @param precision
	 */
	public void doCalculate(double precision) {

		// Termikus pontok legyartasa, kozottuk levo kapcsolatok megteremtese
		// (nics szamolas meg)
		thermicPointList = myCanvas.generateThermicPoints();
		// System.err.println(thermicPointList);
		// thermicPointList = elementSet.generateThermicPoints();

		// Figyelo osztaly a szamitas nyomonkovetesere
		if (null != calculationListener) {
			thermicPointList.setCalculationListener(calculationListener);
		}

		// Sokismeretlenes egyenletrendszer megoldasa, eredmenye: a termikus
		// pontok homerseklete
		thermicPointList.solve(precision);
	}

	public void revalidateAndRepaint() {
		myCanvas.revalidateAndRepaintCoreCanvas();
	}

	/**
	 * Uj szerkesztes menupont betolto objektuma
	 * 
	 * @author akoel
	 * 
	 */
	class NewActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
		
			// Az eredeti rajzolatot torlom
			// ArrayList<HetramDrawnElement> ahetramDrawnElementList =
			// MainPanel.this.myCanvas.getDrawnBlockList();
			// hetramDrawnElementList.clear();
			MainPanel.this.myCanvas.getDrawnBlockList().clear();

			// Beallitom a modot rajzolasra
			MainPanel.this.modePanel.setModeField(Mode.DRAWING);

			// Kirajzoltatom a beolvasott abrat
			MainPanel.this.myCanvas.revalidateAndRepaintCoreCanvas();
			
		}

	};
	
	/**
	 * File mentes menupont vegrehato objektuma
	 * 
	 * @author akoel
	 * 
	 */
	class SaveActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// JMenuItem source = (JMenuItem)(e.getSource());

			@SuppressWarnings("unchecked")
			ArrayList<HetramDrawnElement> list = (ArrayList<HetramDrawnElement>) MainPanel.this
					.getCanvas().getDrawnBlockList();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();

			try {
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();

				// Root element
				Element rootElement = doc.createElement("hetram");
				doc.appendChild(rootElement);

				for (HetramDrawnElement el : list) {
					Element element = el.getXMLElement(doc);
					rootElement.appendChild(element);
				}

				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				//
				// Consolra ir
				//
				// StreamResult result = new StreamResult(System.out);

				//
				// File nevet valaszt es beleir
				//

				// Filechooser inicializalasa a felhasznalo munkakonyvtaraba
				final JFileChooser fc = new JFileChooser(
						System.getProperty("user.dir"));

				// A dialogus ablak cime
				fc.setDialogTitle("Save the plan");

				// Csak az XML kiterjesztesu fajlokat lathatom
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"xml", "xml");
				fc.setFileFilter(filter);

				// Nem engedi meg az "All" filter hasznalatat
				fc.setAcceptAllFileFilterUsed(false);

				// Dialogus ablak inditasa
				int returnVal = fc.showSaveDialog(MainPanel.this);

				// Ha kivalasztottam a nevet
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File file = fc.getSelectedFile();
					String filePath = file.getPath();

					// Mindenkeppen XML lesz a kiterjesztese
					if (!filePath.toLowerCase().endsWith(".xml")) {
						file = new File(filePath + ".xml");
					}

					// Stream letrehozasa
					StreamResult result = new StreamResult(file);

					// Iras
					transformer.transform(source, result);

				}

			} catch (ParserConfigurationException | TransformerException e1) {
				JOptionPane.showMessageDialog(MainPanel.this, "Nem sikerült a file mentése: \n" + e1.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
			}

		}
	};

	/**
	 * File betoltes menupont betolto objektuma
	 * 
	 * @author akoel
	 * 
	 */
	class LoadActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			final JFileChooser fc = new JFileChooser(
					System.getProperty("user.dir"));

			fc.setDialogTitle("Load a plan");

			FileNameExtensionFilter filter = new FileNameExtensionFilter("xml",	"xml");
			fc.setFileFilter(filter);

			// Nem engedi meg az "All" filter hasznalatat
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(MainPanel.this);

			// HetramDrawnElement hetramDrawnElement;

			// Az eredeti rajzolatot torlom
			// ArrayList<HetramDrawnElement> ahetramDrawnElementList =
			// MainPanel.this.myCanvas.getDrawnBlockList();
			// hetramDrawnElementList.clear();
			MainPanel.this.myCanvas.getDrawnBlockList().clear();

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder;
				try {
					dBuilder = dbFactory.newDocumentBuilder();

					// Error kimenetre irja hogy [Fatal Error] es csak utanna
					// megy a catch agba
					Document doc = dBuilder.parse(file);

					// Recommended
					doc.getDocumentElement().normalize();

					// Root element = "hetram"
					// doc.getDocumentElement().getNodeName();

					NodeList nList = doc.getElementsByTagName("drawnblock");
					for (int i = 0; i < nList.getLength(); i++) {

						Node drawnBlockNode = nList.item(i);

						if (drawnBlockNode.getNodeType() == Node.ELEMENT_NODE) {

							Element drawnBlockElement = (Element) drawnBlockNode;

							String drawnBlockType = drawnBlockElement
									.getAttribute("type");
							String drawnBlockForm = drawnBlockElement
									.getAttribute("form");

							// BUILDING STRUCTURE - HOMOGENEOUS
							if (drawnBlockType
									.equals(TYPE.BUILDINGSTRUCTURE_HOMOGENEOUSPATTERN
											.name())) {
								if (drawnBlockForm
										.equals(ElementSettingTab.HOMOGENEOUS_PATTERN.DOT
												.name())) {
									MainPanel.this.myCanvas
											.addDrawnBlock(new HomogeneousPatternBuildingStructuralElement(
													drawnBlockElement,
													new HomogeneousPatternFactory(
															new DotFullPatternAdapter())));
								} else if (drawnBlockForm
										.equals(ElementSettingTab.HOMOGENEOUS_PATTERN.HATCH
												.name())) {
									MainPanel.this.myCanvas
											.addDrawnBlock(new HomogeneousPatternBuildingStructuralElement(
													drawnBlockElement,
													new HomogeneousPatternFactory(
															new HatchFullPatternAdapter())));
								}

								// BUILDING STRUCTURE - ROWPATTERN
							} else if (drawnBlockType
									.equals(TYPE.BUILDINGSTRUCTURE_ROWPATTERN
											.name())) {
								if (drawnBlockForm
										.equals(ElementSettingTab.ROW_PATTERN.ZIGZAG
												.name())) {
									MainPanel.this.myCanvas
											.addDrawnBlock(new RowPatternBuildingStructuralElement(
													drawnBlockElement,
													new RowPatternFactory(
															new ZigZagRowPatternAdapter()),
													MainPanel.this));
								}

								// BUILDING STRUCTURE - COLORED
							} else if (drawnBlockType
									.equals(TYPE.BUILDINGSTRUCTURE_COLORED
											.name())) {
								MainPanel.this.myCanvas
										.addDrawnBlock(new ColoredPatternBuildingSturcturalElement(
												drawnBlockElement));
								// hetramDrawnElementList.add( );

								// EDGE - OPEN
							} else if (drawnBlockType.equals(TYPE.EDGE_OPEN
									.name())) {
								MainPanel.this.myCanvas
										.addDrawnBlock(new OpenEdgeElement(
												drawnBlockElement));
								// hetramDrawnElementList.add( new
								// OpenEdgeElement( eElement ) );

								// EDGE - SYMMETRIC
							} else if (drawnBlockType
									.equals(TYPE.EDGE_SYMMETRIC.name())) {
								MainPanel.this.myCanvas
										.addDrawnBlock(new SymmetricEdgeElement(
												drawnBlockElement));
								// hetramDrawnElementList.add( new
								// SymmetricEdgeElement( eElement ) );

							}

						}

					}

				} catch (ParserConfigurationException | SAXException
						| IOException e1) {

					JOptionPane.showMessageDialog(
							MainPanel.this,
							"Nem sikerült a file beolvasása: \n"
									+ e1.getMessage(), "Hiba",
							JOptionPane.ERROR_MESSAGE);

					/*
					 * Object [] buttonArray = {"Accept", "blabl"}; int a3;
					 * 
					 * do { a3 = JOptionPane.showOptionDialog(null,
					 * "Mean arterial pressure restored.\nReassess all vitals STAT."
					 * , "Title", JOptionPane.YES_NO_OPTION,
					 * JOptionPane.ERROR_MESSAGE, null, buttonArray,
					 * buttonArray[0]); } while(a3 ==
					 * JOptionPane.CLOSED_OPTION); if (a3 ==
					 * JOptionPane.YES_OPTION) { } if (a3 ==
					 * JOptionPane.NO_OPTION) { }
					 */

				}

				// Beallitom a modot rajzolasra
				MainPanel.this.modePanel.setModeField(Mode.DRAWING);

				// Kirajzoltatom a beolvasott abrat
				MainPanel.this.myCanvas.revalidateAndRepaintCoreCanvas();
			}
		}

	};

}
