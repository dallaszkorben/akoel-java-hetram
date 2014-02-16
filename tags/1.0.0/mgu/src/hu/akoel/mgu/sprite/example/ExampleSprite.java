package hu.akoel.mgu.sprite.example;


import hu.akoel.mgu.MControlPanel;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.axis.Axis;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.drawnblock.DrawnBlockStatusPanel;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.scale.ScaleChangeListener;
import hu.akoel.mgu.sprite.ChangeSizeListener;
import hu.akoel.mgu.sprite.Magnet;
import hu.akoel.mgu.sprite.MagnetType;
import hu.akoel.mgu.sprite.RectangleElement;
import hu.akoel.mgu.sprite.Sprite;
import hu.akoel.mgu.sprite.SpriteCanvas;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.LengthValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.RangeValueInPixel;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.Value;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ExampleSprite extends JFrame {

	private static final long serialVersionUID = 5810956401235486862L;

	private SpriteCanvas myCanvas;	
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
	
	private Sprite baseSprite;
	private Sprite pipeDirectHorizontalSprite;
	private Sprite pipeHorizontalSprite;
	private Sprite pipeVerticalSprite;
	private Sprite pipeDirectVerticalSprite;
	
	//Magnet tipus
	private MagnetType pipeMagnet = new MagnetType("Pipe");
	private MagnetType outMagnet = new MagnetType("Out");
	
	public static void main(String[] args) {		
		new ExampleSprite();
	}

	public ExampleSprite() {
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Example :: Sprite");
		this.setUndecorated(false);
		this.setSize(700, 700);
		this.createBufferStrategy(1);

		myCanvas = new SpriteCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle);
		
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
		
		//------------------------------
		//
		// Parancsgomb panel a gombokkal
		//
		//------------------------------

		//Elhelyezi az eloterbe a Sprite-okat
		JButton commandButtonAddBase = new JButton("add Base");
		commandButtonAddBase.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				baseSprite = new Sprite(new SizeValue(-1, -1.5, 1, 1.5));
			
				//A kozponti Sprite leirasa
				RectangleElement gElementBaseSprite = new RectangleElement(-1,-1.5,2,3,Color.blue, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.cyan, new BasicStroke(3));
				RectangleElement gMagnetEastOfBaseSprite = new RectangleElement(-0.05,-0.05,0.05,0.1,Color.blue, new BasicStroke(1), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
RectangleElement gMagnetEastOfBaseSprite2 = new RectangleElement(-0.05,-0.05,0.05,0.1,Color.blue, new BasicStroke(1), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));				
				RectangleElement gMagnetNorthOfBaseSprite = new RectangleElement(-0.05,-0.05,0.1,0.05,Color.blue, new BasicStroke(1), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				RectangleElement gMagnetSouthOfBaseSprite = new RectangleElement(-0.05,0,0.1,0.05,Color.blue, new BasicStroke(1), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				RectangleElement gMagnetWestOfBaseSprite = new RectangleElement(0,-0.05,0.05,0.1,Color.blue, new BasicStroke(1), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				
				Magnet baseSpriteMagnetEast = new Magnet(baseSprite, outMagnet, 90.0, new RangeValueInPixel(20, 10 ), new PositionValue(1, 0) );
				
				baseSpriteMagnetEast.addPossibleMagnetTypeToConnect( pipeMagnet  );
				baseSpriteMagnetEast.addElement( gMagnetEastOfBaseSprite );

Magnet baseSpriteMagnetEast2 = new Magnet(baseSprite, outMagnet, 90.0, new RangeValueInPixel(20, 10 ), new PositionValue(1, 1) );
baseSpriteMagnetEast2.addPossibleMagnetTypeToConnect( pipeMagnet  );
baseSpriteMagnetEast2.addElement( gMagnetEastOfBaseSprite2 );
				
				
				Magnet baseSpriteMagnetNorth = new Magnet(baseSprite, outMagnet, 0.0, new RangeValueInPixel(10, 20 ), new PositionValue(0, 1.5) );
				baseSpriteMagnetNorth.addPossibleMagnetTypeToConnect(pipeMagnet);
				baseSpriteMagnetNorth.addElement(gMagnetNorthOfBaseSprite);
				
				Magnet baseSpriteMagnetSouth = new Magnet(baseSprite, outMagnet, 180.0, new RangeValueInPixel(10, 20 ), new PositionValue(0, -1.5) );
				baseSpriteMagnetSouth.addPossibleMagnetTypeToConnect(pipeMagnet);
				baseSpriteMagnetSouth.addElement(gMagnetSouthOfBaseSprite);
				
				Magnet baseSpriteMagnetWest = new Magnet(baseSprite, outMagnet, 270.0, new RangeValueInPixel(20, 10 ), new PositionValue(-1, 0) );
				baseSpriteMagnetWest.addPossibleMagnetTypeToConnect( pipeMagnet  );
				baseSpriteMagnetWest.addElement( gMagnetWestOfBaseSprite );

				
				baseSprite.addElement(gElementBaseSprite);
				baseSprite.addMagnet( baseSpriteMagnetEast );
baseSprite.addMagnet( baseSpriteMagnetEast2 );				
				baseSprite.addMagnet( baseSpriteMagnetNorth );
				baseSprite.addMagnet( baseSpriteMagnetSouth );
				baseSprite.addMagnet( baseSpriteMagnetWest );
				baseSprite.setPosition( 0, 0 );
				
				//A Sprite-ok elhelyezese a Canvas-on	
				myCanvas.addSprite(baseSprite);	
						
				//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
				myCanvas.revalidateAndRepaintCoreCanvas();
				
			}
		});
		
		JButton commandButtonAddVertical = new JButton("add Vertical");
		commandButtonAddVertical.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//Normal Vertikalis cso
				//Nem helyezheto le onmagaban
				pipeVerticalSprite = new Sprite(new SizeValue(-0.25, -2, 0.25, 2 ), false);
				
				//A Sprite leirasa
				RectangleElement gElementPipeVerticalSprite = new RectangleElement(-0.125,-2,0.25,4,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.orange, new BasicStroke(3));
				RectangleElement gMagnetNorthOfPipeVerticalSprite = new RectangleElement(-0.05,-0.05,0.1,0.05,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				RectangleElement gMagnetSouthOfPipeVerticalSprite = new RectangleElement(-0.05,0,0.1,0.05,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));

				Magnet pipeSpriteMagnetNorth = new Magnet(pipeVerticalSprite, pipeMagnet, 0.0, new RangeValueInPixel(10, 20 ), new PositionValue(0, 2) );
				pipeSpriteMagnetNorth.addPossibleMagnetTypeToConnect( pipeMagnet  );
				pipeSpriteMagnetNorth.addElement(gMagnetNorthOfPipeVerticalSprite);
				
				Magnet pipeSpriteMagnetSouth = new Magnet(pipeVerticalSprite, pipeMagnet, 180.0, new RangeValueInPixel(10, 20 ), new PositionValue(0, -2) );
				pipeSpriteMagnetSouth.addPossibleMagnetTypeToConnect( pipeMagnet );
				pipeSpriteMagnetSouth.addElement(gMagnetSouthOfPipeVerticalSprite);

				//A Sprite magneseinek hozzarendelese
				pipeVerticalSprite.addMagnet( pipeSpriteMagnetNorth );
				pipeVerticalSprite.addMagnet( pipeSpriteMagnetSouth );
				
				pipeVerticalSprite.addElement(gElementPipeVerticalSprite);
				pipeVerticalSprite.setPosition( 0, 0 );
				
				//A Sprite-ok elhelyezese a Canvas-on
				myCanvas.addSprite(pipeVerticalSprite);
						
				//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
				myCanvas.revalidateAndRepaintCoreCanvas();

			}
		});
		
		JButton commandButtonAddDirectVertical = new JButton("add D Vertical");
		commandButtonAddDirectVertical.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				//Direct kapcsolatu Vertikalis cso
				//Nem helyezheto le onmagaban
				pipeDirectVerticalSprite = new Sprite(new SizeValue(-0.25, -1, 0.25, 1 ), false);
				
				//A Sprite leirasa
				RectangleElement gElementPipeDirectVerticalSprite = new RectangleElement(-0.125,-1,0.25,2,Color.blue, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.magenta, new BasicStroke(3));
				RectangleElement gMagnetNorthOfPipeDirectVerticalSprite = new RectangleElement(-0.05,-0.05,0.1,0.05,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				RectangleElement gMagnetSouthOfPipeDirectVerticalSprite = new RectangleElement(-0.05,0,0.1,0.05,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));

				Magnet pipeDirectVerticalSpriteMagnetNorth = new Magnet(pipeDirectVerticalSprite, pipeMagnet, 0.0, new RangeValueInPixel(10, 20 ), new PositionValue(0, 1) );
				pipeDirectVerticalSpriteMagnetNorth.addPossibleMagnetTypeToConnect( pipeMagnet  );
				pipeDirectVerticalSpriteMagnetNorth.addPossibleMagnetTypeToConnect( outMagnet  );
				pipeDirectVerticalSpriteMagnetNorth.addElement(gMagnetNorthOfPipeDirectVerticalSprite);
				
				Magnet pipeDirectVerticalSpriteMagnetSouth = new Magnet(pipeDirectVerticalSprite, pipeMagnet, 180.0, new RangeValueInPixel(10, 20 ), new PositionValue(0, -1) );
				pipeDirectVerticalSpriteMagnetSouth.addPossibleMagnetTypeToConnect( pipeMagnet );
				pipeDirectVerticalSpriteMagnetSouth.addPossibleMagnetTypeToConnect( outMagnet );
				pipeDirectVerticalSpriteMagnetSouth.addElement(gMagnetSouthOfPipeDirectVerticalSprite);

				//A Sprite magneseinek hozzarendelese
				pipeDirectVerticalSprite.addMagnet( pipeDirectVerticalSpriteMagnetNorth );
				pipeDirectVerticalSprite.addMagnet( pipeDirectVerticalSpriteMagnetSouth );
				
				pipeDirectVerticalSprite.addElement(gElementPipeDirectVerticalSprite);
				pipeDirectVerticalSprite.setPosition( 0, 0 );
				
				//A Sprite-ok elhelyezese a Canvas-on
				myCanvas.addSprite(pipeDirectVerticalSprite);	
						
				//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
				myCanvas.revalidateAndRepaintCoreCanvas();
			}
		});
				
		JButton commandButtonAddHorizontal = new JButton("add Horizontal");
		commandButtonAddHorizontal.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				//Normal horizontalis cso
				//Nem helyezheto le onmagaban
				pipeHorizontalSprite = new Sprite(new SizeValue(-2, -0.25, 2, 0.25), false);
				
				//A Sprite leirasa
				RectangleElement gElementPipeHorizontalSprite = new RectangleElement(-2,-0.125,4,0.25,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.orange, new BasicStroke(3));
				RectangleElement gMagnetWestOfPipeHorizontalSprite = new RectangleElement(0,-0.05,0.05,0.1,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				RectangleElement gMagnetEastOfPipeHorizontalSprite = new RectangleElement(-0.05,-0.05,0.05,0.1,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));

				Magnet pipeSpriteMagnetEast = new Magnet(pipeHorizontalSprite, pipeMagnet, 90.0, new RangeValueInPixel(20, 10 ), new PositionValue(2, 0) );
				pipeSpriteMagnetEast.addPossibleMagnetTypeToConnect( pipeMagnet  );
				pipeSpriteMagnetEast.addElement(gMagnetEastOfPipeHorizontalSprite);
				
				Magnet pipeSpriteMagnetWest = new Magnet(pipeHorizontalSprite, pipeMagnet, 270.0, new RangeValueInPixel(20, 10 ), new PositionValue(-2, 0) );
				pipeSpriteMagnetWest.addPossibleMagnetTypeToConnect( pipeMagnet );
				pipeSpriteMagnetWest.addElement(gMagnetWestOfPipeHorizontalSprite);

				//A Sprite magneseinek hozzarendelese
				pipeHorizontalSprite.addMagnet( pipeSpriteMagnetEast );
				pipeHorizontalSprite.addMagnet( pipeSpriteMagnetWest );
				
				pipeHorizontalSprite.addElement(gElementPipeHorizontalSprite);
				pipeHorizontalSprite.setPosition( 0, 0 );
				
				//A Sprite-ok elhelyezese a Canvas-on
				myCanvas.addSprite(pipeHorizontalSprite);	
						
				//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
				myCanvas.revalidateAndRepaintCoreCanvas();
				
				
			}
		});
		
		JButton commandButtonAddDirectHorizontal = new JButton("add D Horizontal");
		commandButtonAddDirectHorizontal.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				

				//Kozvetlen kapcsolatu horizontalis cso
				//Nem helyezheto le onmagaban
				pipeDirectHorizontalSprite = new Sprite(new SizeValue(-1, -0.25, 1, 0.25), false);
				
				//A Sprite leirasa
				RectangleElement gElementPipedirectHorizontalSprite = new RectangleElement(-1,-0.125,2,0.25,Color.blue, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.magenta, new BasicStroke(3));
				RectangleElement gMagnetWestOfPipeDirectHorizontalSprite = new RectangleElement(0,-0.05,0.05,0.1,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));
				RectangleElement gMagnetEastOfPipeDirectHorizontalSprite = new RectangleElement(-0.05,-0.05,0.05,0.1,Color.green, new BasicStroke(1f), Color.red, new BasicStroke(3), Color.yellow, new BasicStroke(3));

				Magnet pipeToBaseSpriteMagnetEast = new Magnet(pipeDirectHorizontalSprite, pipeMagnet, 90.0, new RangeValueInPixel(20, 10 ), new PositionValue(1, 0) );
				pipeToBaseSpriteMagnetEast.addPossibleMagnetTypeToConnect( pipeMagnet  );
				pipeToBaseSpriteMagnetEast.addPossibleMagnetTypeToConnect( outMagnet  );
				pipeToBaseSpriteMagnetEast.addElement( gMagnetEastOfPipeDirectHorizontalSprite );
	
				Magnet pipeToBaseSpriteMagnetWest = new Magnet(pipeDirectHorizontalSprite, pipeMagnet, 270.0, new RangeValueInPixel(20, 10 ), new PositionValue(-1, 0) );
				pipeToBaseSpriteMagnetWest.addPossibleMagnetTypeToConnect( pipeMagnet );
				pipeToBaseSpriteMagnetWest.addPossibleMagnetTypeToConnect( outMagnet );
				pipeToBaseSpriteMagnetWest.addElement( gMagnetWestOfPipeDirectHorizontalSprite );
				
				//A Sprite magneseinek hozzarendelese
				pipeDirectHorizontalSprite.addMagnet( pipeToBaseSpriteMagnetEast );
				pipeDirectHorizontalSprite.addMagnet( pipeToBaseSpriteMagnetWest );				
				pipeDirectHorizontalSprite.addElement(gElementPipedirectHorizontalSprite);
				pipeDirectHorizontalSprite.setPosition( 0, 0 );
				pipeDirectHorizontalSprite.addChangeWidthListener(new ChangeSizeListener() {
					
					@Override
					public void changed(double xMin, double xMax) {
				
					}
				});
				
				//A Sprite-ok elhelyezese a Canvas-on
				myCanvas.addSprite(pipeDirectHorizontalSprite);	
						
				//A Canvas ujrarajzolasa, az uj Sprite megjelenites miatt
				myCanvas.revalidateAndRepaintCoreCanvas();
				
			}
			
		});

		//Report button
		JButton commandButtonReport = new JButton("Report");
		commandButtonReport.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
			
				
				for( Magnet magnet: baseSprite.getMagnetList() ){
					System.err.println("Sprite: " + magnet.getDirection() + ", " + magnet.getConnectedTo() );
				}
				
				System.err.println();
				

				for( Magnet magnet: pipeDirectHorizontalSprite.getMagnetList() ){
					System.err.println("Pipe to Base sprite: " + magnet.getDirection() + ", " + magnet.getConnectedTo() );
				}
				

				System.err.println();
				

				for( Magnet magnet: pipeHorizontalSprite.getMagnetList() ){
					System.err.println("Pipe: " + magnet.getDirection() + ", " + magnet.getConnectedTo() );
				}
			}
			
		});

		//Parancsgomb panel
		JPanel commandButtonPanel = new JPanel();
		commandButtonPanel.setLayout( new FlowLayout(FlowLayout.LEFT));
		commandButtonPanel.add(commandButtonAddBase );
		commandButtonPanel.add(commandButtonAddHorizontal);
		commandButtonPanel.add(commandButtonAddDirectHorizontal);
		commandButtonPanel.add(commandButtonAddVertical);
		commandButtonPanel.add(commandButtonAddDirectVertical);
		commandButtonPanel.add(commandButtonReport);
		
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

