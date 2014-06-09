package hu.akoel.mgu.example;


import hu.akoel.mgu.MControlPanel;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.drawnblock.DrawnBlockStatusPanel;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.scale.ScaleChangeListener;
import hu.akoel.mgu.scale.values.ScaleValue;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.LengthValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.Value;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ExampleMCanvas_Scale_SameUnits_DiscretZoom extends JFrame {

	private static final long serialVersionUID = 5810956401235486862L;

	private MCanvas myCanvas;
	private SizeValue boundSize = new SizeValue(0.0, 0.0, 400.0, 400);	
	private Color background = Color.black;
	private TranslateValue positionToMiddle = null;//new Position( 10, 10);
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(new PixelPerUnitValue(1,1));
	//private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(new Position(1,1), new Position(1.2, 1.2), new Position(1,1), new Position(15,15));

	private Grid myGrid;	
	private Color gridColor = Color.green;
	private int gridWidth = 1;
	private DeltaValue gridDelta = new DeltaValue(1.0, 1.0);
	private Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST; 
	private Grid.Type gridType = Grid.Type.DOT;

	private CrossLine myCrossLine;
	private PositionValue crossLinePosition = new PositionValue( 5, 5 );
	private Color crossLineColor = Color.red;
	private int crossLineWidthInPixel = 5;
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
//	private double startScale = 100;
//	private Position rate = new Position(1.2, 1.2);
	private ArrayList<ScaleValue> possibleScaleList = new ArrayList<ScaleValue>();
	
	public static void main(String[] args) {		
		new ExampleMCanvas_Scale_SameUnits_DiscretZoom();
	}

	public ExampleMCanvas_Scale_SameUnits_DiscretZoom() {
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Example :: JCanvas with JGrid, JCrossLine, JAxis, Scale :: Bounds :: Discrate zoom values");
		this.setUndecorated(false);
		this.setSize(700, 700);
		this.createBufferStrategy(1);

//		myCanvas = new JCanvas(BorderFactory.createLoweredBevelBorder(), background, worldSize );
		myCanvas = new MCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle, boundSize);
		
		myGrid = new Grid( myCanvas, gridType, gridColor, gridWidth, gridPosition, gridDelta );		
		
		myCrossLine = new CrossLine( myCanvas, crossLinePosition, crossLineColor, crossLineWidthInPixel, crossLineLength, crossLinePainterPosition);
	
		myAxis = new Axis(myCanvas, axisPosition, axisColor, axisWidthInPixel, painterPosition);
		
		possibleScaleList.add( new ScaleValue( 2, 2 ));
		possibleScaleList.add( new ScaleValue( 5, 5 ));
		possibleScaleList.add( new ScaleValue( 8, 8 ));
		possibleScaleList.add( new ScaleValue( 10, 10 ));
		possibleScaleList.add( new ScaleValue( 20, 20 ));
		possibleScaleList.add( new ScaleValue( 40, 40 ));
		possibleScaleList.add( new ScaleValue( 50, 50 ));
		possibleScaleList.add( new ScaleValue( 100, 100 ));
		possibleScaleList.add( new ScaleValue( 200, 200 ));
		possibleScaleList.add( new ScaleValue( 500, 500 ));
		possibleScaleList.add( new ScaleValue( 1000, 1000 ));
			
		myScale = new Scale( myCanvas, pixelPerCm, unit, possibleScaleList, 7 );
		
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
				
		//------------------------------
		//
		// Parancsgomb panel a gombokkal
		//
		//------------------------------

		//Ujra rajzol minden statikus rajzi elemet
		JButton reprintButton = new JButton("reprint");
		reprintButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myCanvas.repaint();				
			}			
		});	
		
		//Kirajzol eloterbe egy fuggvenyt
		JButton commandButtonDrawFunction = new JButton("draw Function");
		commandButtonDrawFunction.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//myCanvas.removePainterListenersFromHighest();
				myCanvas.addPainterListenerToHighest(new PainterListener(){
					
					@Override
					public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {					
						g2.setColor(new Color(250, 200, 0));
						g2.setStroke(new BasicStroke(3));
						
						PositionValue previous = null;
						double increment = canvas.getWorldXLengthByPixel(2);
						double start = canvas.getWorldXByPixel(0);
						double stop = canvas.getWorldXByPixel(canvas.getViewableSize().width );
						for( double x=start; x<=stop; x+=increment ){
							double y = 0.01*(x*x*x) - 0.07*(x*x) + 0.1*(x) - 0;
							if( null == previous ){
								previous = new PositionValue(x, y);
							}
							g2.drawLine(previous.getX(), previous.getY(), x, y);
							previous = new PositionValue(x, y);
						}
						
						g2.setColor(Color.blue);
						g2.drawLine(0, 0, 0, 0);

					}

					@Override
					public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}	
					
				}, MCanvas.Level.UNDER);		
				myCanvas.repaint();
			}
			
		});

		//Parancsgomb panel
		JPanel commandButtonPanel = new JPanel();
		commandButtonPanel.setLayout( new FlowLayout(FlowLayout.LEFT));
		commandButtonPanel.add(commandButtonDrawFunction);

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
					statusPanel.setScale( "xM=" + df.format(1/myScale.getScale().getX() ) + ":1" );
				}else{
					statusPanel.setScale( "xM=1:" + df.format(myScale.getScale().getX() ) );
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
}

