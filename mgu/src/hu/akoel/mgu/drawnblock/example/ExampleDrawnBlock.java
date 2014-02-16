package hu.akoel.mgu.drawnblock.example;

import hu.akoel.mgu.MControlPanel;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.drawnblock.DrawnBlock;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.drawnblock.DrawnBlockSnapControl;
import hu.akoel.mgu.drawnblock.DrawnBlockStatusPanel;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ExampleDrawnBlock extends JFrame {

	private static final long serialVersionUID = 5810956401235486863L;

	private DrawnBlockCanvas myCanvas;	
	private Color background = Color.black;
	private TranslateValue positionToMiddle = new TranslateValue( 0, 0);
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(new PixelPerUnitValue(10,10));

	private Grid myGrid;	
	private Color gridColor = Color.green;
	private int gridWidth = 1;
	private DeltaValue gridDelta = new DeltaValue(1.0, 1.0);
	private Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST; 
	private Grid.Type gridType = Grid.Type.DOT;

	private CrossLine myCrossLine;
	private PositionValue crossLinePosition = new PositionValue( 0, 0 );
	private Color crossLineColor = Color.red;
	private int crossLineWidthInPixel = 1;
	private Value crossLineLength = new LengthValue( 1, 1 );
	private CrossLine.PainterPosition crossLinePainterPosition = CrossLine.PainterPosition.DEEPEST;
	
	private Axis myAxis;
	private Color axisColor = Color.yellow;
	private int axisWidthInPixel = 1;
	private Axis.AxisPosition axisPosition = Axis.AxisPosition.AT_LEFT_BOTTOM;
	private Axis.PainterPosition painterPosition = Axis.PainterPosition.HIGHEST;
		
	private Scale myScale;
	private double pixelPerCm = 42.1;
	private Scale.UNIT unit = Scale.UNIT.m;
	private double startScale = 100;//38.426;//100;
	private double rate = 1.2;
	private double minScale = 0.5;
	private double maxScale =  600;
	private DrawnBlockCanvas.Precision precision = DrawnBlockCanvas.Precision.ONE_100;
	
	public static void main(String[] args) {		
		new ExampleDrawnBlock();
	}

	public ExampleDrawnBlock() {
		
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setTitle( "Example :: DrawnBlock" );
		this.setUndecorated( false );
		this.setSize( 700, 700 );
		this.createBufferStrategy( 1 );

		myCanvas = new DrawnBlockCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle, precision );
	
		myGrid = new Grid( myCanvas, gridType, gridColor, gridWidth, gridPosition, gridDelta );		
		
		myCrossLine = new CrossLine( myCanvas, crossLinePosition, crossLineColor, crossLineWidthInPixel, crossLineLength, crossLinePainterPosition);
	
		myAxis = new Axis(myCanvas, axisPosition, axisColor, axisWidthInPixel, painterPosition);
			
		myScale = new Scale(myCanvas, pixelPerCm, unit, startScale, rate, minScale, maxScale);


		
		//-----------------
		//
		// K-i oldali elem
		//
		// Vezerlopanel
		//
		//-----------------
		MControlPanel controlPanel = new MControlPanel();
		controlPanel.addElement( myGrid.getControl( myScale ) );
		controlPanel.addElement( myCrossLine.getControl( myScale ) );
		controlPanel.addElement( myAxis.getControl( ) );
		//controlPanel.addElement( myScale.getControl( ) );		
		controlPanel.addElement( (new DrawnBlockSnapControl(myCanvas, myGrid).getControl()));
				
		//------------------------------
		//
		// Parancsgomb panel a gombokkal
		//
		//------------------------------
		JButton commandButtonAddBuildingMaterial = new JButton("add Building Material");
		commandButtonAddBuildingMaterial.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				DrawnBlockFactory dbf = new BuildingMaterialFactory();
				myCanvas.setDrawnBlockFactory( dbf );
				
			}
		});

		JButton commandButtonAddInsulation = new JButton("add Insulation");
		commandButtonAddInsulation.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				DrawnBlockFactory dbf = new InsulationFactory();
				myCanvas.setDrawnBlockFactory( dbf );
				
			}
		});

		JPanel commandButtonPanel = new JPanel();
		commandButtonPanel.setLayout( new FlowLayout(FlowLayout.LEFT));
		commandButtonPanel.add(commandButtonAddBuildingMaterial );
		commandButtonPanel.add(commandButtonAddInsulation );
		
		//-------------
		//
		// Statuszpanel
		//
		//-------------
		final DrawnBlockStatusPanel statusPanel = new DrawnBlockStatusPanel();

		// Kezdo ertekek kiirasa
		DecimalFormat df = new DecimalFormat("#.00");
		if( myScale.getScale().getX() < 1.0 ){
			statusPanel.setScale( "M=" + df.format(1/myScale.getScale().getX() ) + ":1" );
		}else{
			statusPanel.setScale( "M=1:" + df.format(myScale.getScale().getX() ) );
		}
		
		// Kurzor pozicio figyelo
		myCanvas.addCursorPositionChangeListener( new CursorPositionChangeListener() {
			@Override
			public void getWorldPosition(double xPosition, double yPosition) {
				DecimalFormat df = new DecimalFormat("#.0000");				
				statusPanel.setXPosition( "x: " + df.format(xPosition));
				statusPanel.setYPosition( "y: " + df.format(yPosition));				
			}
		});
		
		// Meretarany figyelo
		myScale.addScaleChangeListener(new ScaleChangeListener() {		
			@Override
			public void getScale(Value scale) {
				DecimalFormat df = new DecimalFormat("#.00");
				if( myScale.getScale().getX() < 1.0 ){
					statusPanel.setScale( "M=" + df.format(1/myScale.getScale().getX() ) + ":1" );
				}else{
					statusPanel.setScale( "M=1:" + df.format(myScale.getScale().getX() ) );
				}
			}
		});
		
		//--------------------------------------
		//
		// D-i oldali elemek
		//
		// Tartalmazzak a parancsgombi panelt es
		// a status panel-t
		//
		//--------------------------------------
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS	));
		
		southPanel.add( commandButtonPanel );
		southPanel.add( statusPanel );
	
		//--------------------------------
		//
		// Panelek elhelyezese az ablakban
		//
		//--------------------------------
		this.getContentPane().setLayout(new BorderLayout(10,10));
		this.getContentPane().add(myCanvas, BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		this.getContentPane().add(controlPanel, BorderLayout.EAST);	
	
		this.setVisible(true);		

	}
	
	
	/**
	 * 
	 * Epitoanyagot legyarto osztaly
	 * 
	 * @author akoel
	 *
	 */
	class BuildingMaterialFactory implements DrawnBlockFactory{

		@Override
		public DrawnBlock getNewDrawnBlock( Status status, BigDecimal x1, BigDecimal y1 ) {
			
			return new BuildingMaterialBlock( status , x1, y1 );
		}
		
	}
	
	/**
	 *
	 * Szigetelest legyarto osztaly
	 * 
	 * @author akoel
	 *
	 */
	class InsulationFactory implements DrawnBlockFactory{

		@Override
		public DrawnBlock getNewDrawnBlock( Status status, BigDecimal x1, BigDecimal y1 ) {
			
			return new InsulationBlock( status , x1, y1, null, new BigDecimal("15.0"), null, new BigDecimal("0.0") );
		}
		
	}
}

