package hu.akoel.hetram.gui;

import hu.akoel.hetram.HetramCanvas;
import hu.akoel.hetram.HetramDrawnElementFactory;
import hu.akoel.hetram.Hetram;
import hu.akoel.hetram.SelectedOpenEdgeForSumQList;
import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.connectors.IThermicConnector;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
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
import hu.akoel.hetram.gui.tabs.ElementSettingTab;
import hu.akoel.hetram.gui.tabs.ElementSettingTab.DRAWING_ELEMENT;
import hu.akoel.hetram.gui.tabs.ElementSettingTab.HOMOGENEOUS_PATTERN;
import hu.akoel.hetram.gui.tabs.ElementSettingTab.PATTERN_TYPE;
import hu.akoel.hetram.gui.tabs.ElementSettingTab.ROW_PATTERN;
import hu.akoel.hetram.listeners.CalculationListener;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.hetram.thermicpoint.ThermicPointList.CURRENT_TYPE;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MCanvas.Level;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.drawnblock.SecondaryCursor;
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
import java.util.Collections;
import java.util.Comparator;
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
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
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

	public static enum QShow {
		THERMICPOINT, OPENEDGE
	}

	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 800;
	private static final int DEFAULT_SETTINGTABBEDPANEL = 310;

	private static final Precision precision = Precision.per_1000;

	private String version;
	private File usedDirectory = null;

	private SelectedOpenEdgeForSumQList selectedOpenEdgeForSumQList = new SelectedOpenEdgeForSumQList();

	private ThermicPointList thermicPointList = null;

	private StatusLine statusLine;
	private SettingTabbedPanel settingTabbedPanel;
	private ModePanel modePanel;
	private JPanel containerPanel;

	// Canvas parameterei
	private HetramCanvas myCanvas;
	private Color background = Color.black;
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(new PixelPerUnitValue(1, 1));
	private TranslateValue positionToMiddle = new TranslateValue(0.3, 0.6);

	private CalculationListener calculationListener = null;

	private ThermicPointList termicPointList;
	
	// ------------------------
	//
	// Kiindulasi parameterek
	//
	// ------------------------

	//
	// Mukodesi mode
	//
	private Mode mode = Mode.DRAWING;
	private QShow qShow = QShow.THERMICPOINT;
	private boolean needToStopCalculation = false;

	//
	// Rajzi elemek - ElementSettings
	//
	private DRAWING_ELEMENT drawingElement = DRAWING_ELEMENT.BUILDINGELEMENT;
	private double buildingStructureLambda = 0.3;
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
	private double calculationPrecision = 0.00000001;

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

	public boolean needToStopCalculation() {
		return needToStopCalculation;
	}

	JMenuBar menuBar;
	JMenu fileMainMenu;
	JMenuItem fileNewMenuItem;
	JMenuItem fileSaveMenuItem;
	JMenuItem fileSaveAsMenuItem;
	JMenuItem fileLoadMenuItem;
	JMenu helpMainMenu;

	public MainPanel(String version) {

		this.version = version;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("");
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
		// fileMainMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		menuBar.add(fileMainMenu);

		// File-New
		fileNewMenuItem = new JMenuItem("Új", KeyEvent.VK_N); // Mnemonic Akkor ervenyes ha lathato a menu elem
		fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK)); // Mindegy hogy lathato-e a menu vagy
		fileNewMenuItem.addActionListener(new NewActionListener());
		fileMainMenu.add(fileNewMenuItem);

		// File-Save
		fileSaveMenuItem = new JMenuItem("Mentés", KeyEvent.VK_S); // Mnemonic Akkor ervenyes ha lathato a menu elem
		fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); // Mindegy hogy lathato-e a menu vagy sem
		fileSaveMenuItem.addActionListener(new SaveActionListener());
		fileSaveMenuItem.setEnabled(false);
		fileMainMenu.add(fileSaveMenuItem);

		// File-Save As
		fileSaveAsMenuItem = new JMenuItem("Mentés mint ...", KeyEvent.VK_S); // Mnemonic Akkor ervenyes ha lathato a menu elem
		// fileSaveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		// ActionEvent.CTRL_MASK)); // Mindegy hogy lathato-e a menu vagy sem
		fileSaveAsMenuItem.addActionListener(new SaveAsActionListener());
		fileMainMenu.add(fileSaveAsMenuItem);

		// File-Load
		fileLoadMenuItem = new JMenuItem("Betöltés", KeyEvent.VK_L);
		fileLoadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		// fileLoadMenuItem.getAccessibleContext().setAccessibleDescription(
		// "This doesn't really do anything");
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

		myCanvas = new HetramCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle, this, precision);

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
					thermicPointList.drawPoint(canvas, g2, thermicPointColor, thermicPointRadius);
				}

				//
				// Ha szukseges a homerseklet megjelenitese
				//
				if (needDrawTemperatureByFont && null != thermicPointList) {
					thermicPointList.drawPointTemperatureByFont((DrawnBlockCanvas) canvas, g2);
				}

				// Ha szukseges a hoaram megjelenitese (vektor, vektorpar,
				// trajektoria)
				if (needDrawCurrentByArrow && null != thermicPointList) {
					thermicPointList.setCurrentType(currentType);
					thermicPointList.drawCurrent(canvas, g2);
				}

				// TODO semmi ertelme hogy itt legyen. At kene rakni a
				// HetramCanvas-ha. Ott viszon nem parameterezhetem a
				// thermicPointList-tel !!!!!
				// A kiemelt OpenEdge az osszegzett Q kijelzese vegett
				if (null != thermicPointList) {
					selectedOpenEdgeForSumQList.drawOpenEdge((DrawnBlockCanvas) canvas, g2);
				}
			}

			@Override
			public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {
			}

		}, Level.UNDER);

		myGrid = new Grid(myCanvas, gridType, gridColor, gridWidth, gridPosition, gridDelta, needDrawGrid );

		myCrossLine = new CrossLine(myCanvas, crossLinePosition, crossLineColor, crossLineWidthInPixel, crossLineLength, crossLinePainterPosition, needDrawCrossline );

		myScale = new Scale(myCanvas, pixelPerCm, unit, startScale, rate);
		
		myAxis = new Axis(myCanvas, axisPosition, axisColor, axisWidthInPixel, painterPosition, needDrawAxis );	
			
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

				if (null != thermicPointList && getMode().equals(Mode.ANALYSIS)) {

					ThermicPoint tp = thermicPointList.getThermicPointByPosition(xPosition, yPosition);

					if (null == tp) {
						statusLine.setTemperature(null);

						if (getQShow().equals(QShow.THERMICPOINT)) {
							statusLine.setQNorth(null);
							statusLine.setQEast(null);
							statusLine.setQSouth(null);
							statusLine.setQWest(null);
						}

					} else {
						statusLine.setTemperature(tp.getActualTemperature());

						if (getQShow().equals(QShow.THERMICPOINT)) {
							statusLine.setQNorth(tp.getNorthCurrent());
							statusLine.setQEast(tp.getEastCurrent());
							statusLine.setQSouth(tp.getSouthCurrent());
							statusLine.setQWest(tp.getWestCurrent());
						}
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
		this.containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
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
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myCanvas, containerPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(DEFAULT_WIDTH - DEFAULT_SETTINGTABBEDPANEL);
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

	public void clearAllSelected() {
		this.myCanvas.clearAllSelected();
	}

	public SelectedOpenEdgeForSumQList getSelectedOpenEdgeForSumQList() {
		return this.selectedOpenEdgeForSumQList;
	}

	// ------------------------------
	//
	// Mukodesi mod
	//
	// ------------------------------
	public void setMode(Mode mode) {
		if (mode.equals(Mode.CALCULATION)) {
			fileNewMenuItem.setEnabled(false);
			fileLoadMenuItem.setEnabled(false);
		} else if (mode.equals(Mode.DRAWING)) {
			fileNewMenuItem.setEnabled(true);
			fileLoadMenuItem.setEnabled(true);
		} else if (mode.equals(Mode.ANALYSIS)) {
			fileNewMenuItem.setEnabled(true);
			fileLoadMenuItem.setEnabled(true);
		}
		this.mode = mode;
		modePanel.setModeField(mode);
	}

	public Mode getMode() {
		return this.mode;
	}

	class TemperatureForGraph {
		BigDecimal position;
		double temperature;

		public TemperatureForGraph(BigDecimal position, double temperature) {
			this.position = position;
			this.temperature = temperature;
		}

		public BigDecimal getPosition() {
			return position;
		}

		public void setPosition(BigDecimal position) {
			this.position = position;
		}

		public double getTemperature() {
			return temperature;
		}

		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}
	}

	public class TemperatureForGraphComparator implements Comparator<TemperatureForGraph> {

		@Override
		public int compare(TemperatureForGraph o1, TemperatureForGraph o2) {
			return o1.position.compareTo(o2.position);
		}
	}

	/**
	 * A kivalasztott OPENEDGE homersekleti menetet jeleniti meg
	 * 
	 * @param openEdgeElement
	 */
	public void showThermicGraph(OpenEdgeElement openEdgeElement) {
		ThermicPoint tp;
		BigDecimal x1, x2, y1, y2;
		BigDecimalPosition position;

		x1 = openEdgeElement.getX1();
		x2 = openEdgeElement.getX2();
		y1 = openEdgeElement.getY1();
		y2 = openEdgeElement.getY2();

		ArrayList<TemperatureForGraph> thermicListForGraph = new ArrayList<TemperatureForGraph>();

		// -----------
		//
		// Vertikalis
		//
		// -----------
		if (x1.equals(x2)) {

			for (int i = 0; i < thermicPointList.getSize(); i++) {
				tp = thermicPointList.get(i);

				position = tp.getPosition();
				IThermicConnector tc;
				
				//
				// EAST
				//
				tc = tp.getEastThermicConnector();
				if( tc instanceof OpenEdgeThermicConnector ){
					OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
					
					//Ez a termikus pont kapcsolodik EAST fele a kivalasztott OPENEDGEELEMENT-hez
					if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
						thermicListForGraph.add(new TemperatureForGraph(tp.getPosition().getY(), tp.getActualTemperature()));
					}					
				}
				
				//
				// WEST
				//
				tc = tp.getWestThermicConnector();
				if( tc instanceof OpenEdgeThermicConnector ){
					OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
					
					//Ez a termikus pont kapcsolodik WEST fele a kivalasztott OPENEDGEELEMENT-hez
					if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
						thermicListForGraph.add(new TemperatureForGraph(tp.getPosition().getY(), tp.getActualTemperature()));
					}					
				}

			}

			// -------------
			//
			// Horizontalis
			//
			// -------------
		} else if (y1.equals(y2)) {

			for (int i = 0; i < thermicPointList.getSize(); i++) {
				tp = thermicPointList.get(i);

				position = tp.getPosition();

				IThermicConnector tc;
				
				//
				// NORTH
				//
				tc = tp.getNorthThermicConnector();
				if( tc instanceof OpenEdgeThermicConnector ){
					OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
					
					//Ez a termikus pont kapcsolodik NORTH fele a kivalasztott OPENEDGEELEMENT-hez
					if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
						thermicListForGraph.add(new TemperatureForGraph(tp.getPosition().getX(), tp.getActualTemperature()));
					}
					
				}
				
				//
				// SOUTH
				//
				tc = tp.getSouthThermicConnector();
				if( tc instanceof OpenEdgeThermicConnector ){
					OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
					
					//Ez a termikus pont kapcsolodik SOUTH fele a kivalasztott OPENEDGEELEMENT-hez
					if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
						thermicListForGraph.add(new TemperatureForGraph(tp.getPosition().getX(), tp.getActualTemperature()));
					}
					
				}
			}

		}

		Collections.sort(thermicListForGraph, new TemperatureForGraphComparator());
		for (TemperatureForGraph tfg : thermicListForGraph) {
			System.err.println(tfg.getPosition() + ", " + tfg.getTemperature());
		}
	}

	/**
	 * Beallitja, hogy Analisis modban a kurzor altal kijelolt termikus pont
	 * hoaramat, vagy a kivalaszott OPENEDGE osszesitett aramat kell
	 * megjeleniteni
	 * 
	 * @param qShow
	 */
	public void setQShow(QShow qShow) {
		this.qShow = qShow;

		// Ha a termikus pontok hoaramanak megjelenitesere van szukseg
		if (qShow.equals(QShow.THERMICPOINT)) {

			// Akkor ertesiti a masodlagos kurzorfigyelot, hogy kiirhassa a
			// kurzor poziciojaban elhelyezkedo pong aramait
			SecondaryCursor secondaryCursor = myCanvas.getSecondaryCursor();
			for (CursorPositionChangeListener listener : myCanvas.getSecondaryCursorPositionChangeListenerList()) {
				listener.getWorldPosition(secondaryCursor.getX().doubleValue(), secondaryCursor.getY().doubleValue());
			}

		// Ha a kivalasztott OPENEDGE eredo aramanak megjelenitesere van
		// szukseg
		} else if (qShow.equals(QShow.OPENEDGE)) {

			// Akkor kiirja az osszesitett aramot
			selectedOpenEdgeForSumQList.writeOpenEdge(thermicPointList, statusLine);
		}
	}

	public QShow getQShow() {
		return this.qShow;
	}

	/*
	 * public void setModeField(Mode mode) { modePanel.setModeField(mode); }
	 */
	// -----------------------------------
	//
	// Vezerlo felulet - ContolSetting
	//
	// -----------------------------------

	public BigDecimal getHorizontalMaximumDifference() {
		return horizontalMaximumDifference;
	}

	public void setHorizontalMaximumDifference(BigDecimal horizontalMaximumDifference) {
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
		/*
		 * if( null != difference ){
		 * settingTabbedPanel.controlSettingTab.setAppliedXDeltaField(
		 * myCanvas.getRoundedBigDecimalWithPrecisionFormBigDecimal(
		 * horizontalAppliedDifference ).toPlainString() );; }else{
		 * settingTabbedPanel.controlSettingTab.setAppliedXDeltaField(""); }
		 */
	}

	public BigDecimal getHorizontalAppliedDifference() {
		return horizontalAppliedDifference;
	}

	public void setVerticalAppliedDifference(BigDecimal difference) {
		this.verticalAppliedDifference = difference;
		/*
		 * if( null != difference ){
		 * settingTabbedPanel.controlSettingTab.setAppliedYDeltaField(
		 * myCanvas.getRoundedBigDecimalWithPrecisionFormBigDecimal(
		 * verticalAppliedDifference ).toPlainString() );; }else{
		 * settingTabbedPanel.controlSettingTab.setAppliedYDeltaField(""); }
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

	public JProgressBar getProgressBar() {
		return settingTabbedPanel.controlSettingTab.getProgressBar();
	}

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

	public ThermicPointList getTermicPointList() {
		return termicPointList;
	}

	public void setTermicPointList(ThermicPointList termicPointList) {
		this.termicPointList = termicPointList;
	}

	/**
	 * Termikus pontok letrehozasa es homersekleteik kiszamitasa
	 * 
	 * @param precision
	 */
	public void doCalculate(double precision) {

		// Ha rajz vagy analizis modban vagyok, akkor a szamitas ujra elvegezendo
		if (getMode().equals(Mode.DRAWING) || getMode().equals(Mode.ANALYSIS)) {

			// Mukodesi mod valtas - Calculation
			setMode(Mode.CALCULATION);

			// Minden selectet torlok
			clearAllSelected();

			// Torlom a mar letezo Thermikus Pont listat es az ertekelofelulet
			// ujrarajzolasaval el is tuntetem
			// mind a szinkodokat, mind a termikus vektorokat, a termikus pont
			// jeloleseket es a homerseklet kijelzest
			setThermicPointList(null);

			// Jelzok alapallapotba allitasa
			SwingUtilities.invokeLater(new Thread() {

				@Override
				public void run() {

					// Nyomogomb allitas
					getSettingTabbedPanel().getControlSettingTab().getCalculateButton().setBackground(Color.red);
					getSettingTabbedPanel().getControlSettingTab().getCalculateButton().setText("Stop");

					// Progressbar: Indeterminate tipusu, 0 hosszu, nincs kijelzes
					getProgressBar().setIndeterminate(true);
					getProgressBar().setStringPainted(false);
					getProgressBar().setValue(0);

				}
			});

			needToStopCalculation = false;

			// Termikus pontok legyartasa, kozottuk levo kapcsolatok megteremtese (nics szamolas meg)	
			termicPointList = myCanvas.generateThermicPointList();

			// Sokismeretlenes egyenletrendszer megoldasa, eredmenye: a termikus pontok homerseklete
			termicPointList.solve(this, precision);

			// Ha viszont eppen szamitas tortenik, akkor le kell allitani azt
		} else if (getMode().equals(Mode.CALCULATION)) {
			needToStopCalculation = true;
		}

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
			MainPanel.this.setMode(Mode.DRAWING);

			// Kirajzoltatom a beolvasott abrat
			MainPanel.this.myCanvas.revalidateAndRepaintCoreCanvas();

			setTitle("");
			fileSaveMenuItem.setEnabled(false);

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

			@SuppressWarnings("unchecked")
			ArrayList<HetramDrawnElement> list = (ArrayList<HetramDrawnElement>) MainPanel.this.getCanvas().getDrawnBlockList();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

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

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				if (null != usedDirectory) {

					// Stream letrehozasa
					StreamResult result = new StreamResult(usedDirectory);

					// Iras
					transformer.transform(source, result);
				}

			} catch (ParserConfigurationException | TransformerException e1) {
				JOptionPane.showMessageDialog(MainPanel.this, "Nem sikerült a file mentése: \n" + e1.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
			}

		}
	};

	/**
	 * File mentes maskent menupont vegrehato objektuma
	 * 
	 * @author akoel
	 * 
	 */
	class SaveAsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// JMenuItem source = (JMenuItem)(e.getSource());

			@SuppressWarnings("unchecked")
			ArrayList<HetramDrawnElement> list = (ArrayList<HetramDrawnElement>) MainPanel.this.getCanvas().getDrawnBlockList();

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

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

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				//
				// Consolra ir
				//
				// StreamResult result = new StreamResult(System.out);

				//
				// File nevet valaszt es beleir
				//

				JFileChooser fc;
				if (null == usedDirectory) {
					fc = new JFileChooser(System.getProperty("user.dir"));
				} else {
					fc = new JFileChooser(usedDirectory);
				}

				// Filechooser inicializalasa a felhasznalo munkakonyvtaraba

				// A dialogus ablak cime
				fc.setDialogTitle("Save the plan");

				// Csak az XML kiterjesztesu fajlokat lathatom
				FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
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

					setTitle(" :: " + file.getName());

					usedDirectory = file;
					fileSaveMenuItem.setEnabled(true);

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

			JFileChooser fc;
			if (null == usedDirectory) {
				fc = new JFileChooser(System.getProperty("user.dir"));
			} else {
				fc = new JFileChooser(usedDirectory);
			}

			fc.setDialogTitle("Load a plan");

			FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
			fc.setFileFilter(filter);

			// Nem engedi meg az "All" filter hasznalatat
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(MainPanel.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				// HetramDrawnElement hetramDrawnElement;
				MainPanel.this.myCanvas.getDrawnBlockList().clear();
				
				File file = fc.getSelectedFile();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
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

							String drawnBlockType = drawnBlockElement.getAttribute("type");
							String drawnBlockForm = drawnBlockElement.getAttribute("form");

							// BUILDING STRUCTURE - HOMOGENEOUS
							if (drawnBlockType.equals(TYPE.BUILDINGSTRUCTURE_HOMOGENEOUSPATTERN.name())) {
								if (drawnBlockForm.equals(ElementSettingTab.HOMOGENEOUS_PATTERN.DOT.name())) {
									MainPanel.this.myCanvas.addDrawnBlock( new HomogeneousPatternBuildingStructuralElement(myCanvas.getPrecision(),drawnBlockElement, new HomogeneousPatternFactory(new DotFullPatternAdapter())));
								} else if (drawnBlockForm.equals(ElementSettingTab.HOMOGENEOUS_PATTERN.HATCH.name())) {
									MainPanel.this.myCanvas.addDrawnBlock(new HomogeneousPatternBuildingStructuralElement(myCanvas.getPrecision(),drawnBlockElement, new HomogeneousPatternFactory(new HatchFullPatternAdapter())));
								}

								// BUILDING STRUCTURE - ROWPATTERN
							} else if (drawnBlockType.equals(TYPE.BUILDINGSTRUCTURE_ROWPATTERN.name())) {
								if (drawnBlockForm.equals(ElementSettingTab.ROW_PATTERN.ZIGZAG.name())) {
									MainPanel.this.myCanvas.addDrawnBlock(new RowPatternBuildingStructuralElement(myCanvas.getPrecision(),drawnBlockElement, new RowPatternFactory(new ZigZagRowPatternAdapter()), MainPanel.this));
								}

								// BUILDING STRUCTURE - COLORED
							} else if (drawnBlockType.equals(TYPE.BUILDINGSTRUCTURE_COLORED.name())) {
								MainPanel.this.myCanvas.addDrawnBlock(new ColoredPatternBuildingSturcturalElement(myCanvas.getPrecision(),drawnBlockElement));
								// hetramDrawnElementList.add( );

								// EDGE - OPEN
							} else if (drawnBlockType.equals(TYPE.EDGE_OPEN.name())) {
								MainPanel.this.myCanvas.addDrawnBlock(new OpenEdgeElement(myCanvas.getPrecision(),drawnBlockElement));
								// hetramDrawnElementList.add( new
								// OpenEdgeElement( eElement ) );

								// EDGE - SYMMETRIC
							} else if (drawnBlockType.equals(TYPE.EDGE_SYMMETRIC.name())) {
								MainPanel.this.myCanvas.addDrawnBlock(new SymmetricEdgeElement(myCanvas.getPrecision(),drawnBlockElement));
								// hetramDrawnElementList.add( new
								// SymmetricEdgeElement( eElement ) );

							}

						}

					}

					setTitle(" :: " + file.getName());

					usedDirectory = file;
					fileSaveMenuItem.setEnabled(true);

				} catch (ParserConfigurationException | SAXException | IOException e1) {

					JOptionPane.showMessageDialog(MainPanel.this, "Nem sikerült a file beolvasása: \n" + e1.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);

				}

				// Beallitom a modot rajzolasra
				MainPanel.this.setMode(Mode.DRAWING);

				// Kirajzoltatom a beolvasott abrat
				MainPanel.this.myCanvas.revalidateAndRepaintCoreCanvas();
			}
		}

	};


	
	public void setTitle(String title) {
		super.setTitle("Hetram " + version + title);
	}
}
