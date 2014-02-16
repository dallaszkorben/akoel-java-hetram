package hu.akoel.mgu.drawnblock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;

public class DrawnBlockCanvas extends MCanvas{

	private static final long serialVersionUID = -8308688255617119442L;
	
	public static enum Precision{
		ONE_10( "#.#", 1, 10 ),
		ONE_100( "#.##", 2, 100 ),
		ONE_1000( "#.###", 3, 1000 ),
		ONE_10000( "#.####", 4, 10000 ),
		ONE_100000( "#.#####", 5, 100000 );
		
		private String decimalFormat;
		private int scale;
		private int powered;
		
		private Precision( String decimalFormat, int scale, int powered ){
			this.decimalFormat = decimalFormat;
			this.scale = scale;
			this.powered = powered;
		}
		
		public String getDecimalFormat(){
			return this.decimalFormat;
		}
		
		public int getScale(){
			return scale;
		}
		
		public int getPowered(){
			return powered;
		}
	}
	
	private DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols();			
	
	private ArrayList<CursorPositionChangeListener> secondaryCursorPositionChangeListenerList = new ArrayList<CursorPositionChangeListener>();
		
	private Stroke basicStroke = new BasicStroke();
	
	private DrawnBlockFactory drawnBlockFactory;
	
	private ArrayList<DrawnBlock> drawnBlockList = new ArrayList<DrawnBlock>();
	private ArrayList<DrawnBlock> temporaryDrawnBlockList = new ArrayList<DrawnBlock>();
	
	private DrawnBlockDrawListener drawnBlockDrawListener = new DrawnBlockDrawListener();
	private DrawnBlockPainterListener drawnBlockPainterListener = new DrawnBlockPainterListener();
	
	private SecondaryCursor secondaryCursor = new SecondaryCursor();
	
	private boolean needFocus = true;
	
	private int snapDelta = 0;
	private double snapSideDivision = 0.5;
	private boolean neededSideExtentionSnap = false;
	private boolean neededGridSnap = false;
	private boolean neededSideDivisionSnap = false;
	private Grid myGrid;
	
	private Precision precision; 
	
	public DrawnBlockCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle, Precision precision ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle );		
		commonConstructor( precision );
	}
	
	public DrawnBlockCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle, SizeValue boundSize, Precision precision ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle, boundSize );
		commonConstructor( precision );
	}

	private void commonConstructor( Precision precision ){
		
		this.decimalSymbol.setDecimalSeparator('.');		
		this.precision = precision;
		
		//Azt figyeli, hogy egy DrawnBlock fokuszba kerult-e
//		this.addMouseMotionListener( drawnBlockInFocusListener );
		
		//A kozepso reteg also felet hasznaljuk a DrawnBlock-ok megjelenitesere
		this.addPainterListenerToMiddle( drawnBlockPainterListener, Level.UNDER );
		
		//A kurzor mozgasat vizsgalolo listener
		this.addMouseInputListener( drawnBlockDrawListener );
			
		//Ctrl-Z figyelese
		this.addKeyListener( new KeyAdapter(){
			public void keyPressed(KeyEvent ke){

				//ctrl-z
				if( ke.getKeyCode() == KeyEvent.VK_Z && ( ke.getModifiers() & KeyEvent.CTRL_MASK) != 0){
					int lastElement = drawnBlockList.size() - 1;
					if( lastElement >= 0 ){
						
						//Legutoljara elhelyezett elem kitorlese
						removeDrawnBlock( lastElement );
						//drawnBlockList.remove( lastElement );

						//Ujrarajzoltatom a Canvas-t az utolsonak elhelyezett DrawnBlock nelkul
						revalidateAndRepaintCoreCanvas();
					}
					
				}
			 }		
		});
	}

	public Precision getPrecision(){
		return precision;
	}
	
	/**
	 * Engedelyezi a masodlagos kurzor legkozelebbi DrawnBlock oldalhoz, vagy annak
	 * meghosszabbitasahoz valo igazitasat
	 * 
	 * @param needed
	 */
	public void setNeededSideExtentionSnap( boolean needed ){
		this.neededSideExtentionSnap = needed;
	}
	
	public boolean getNeededSideExtentionSnap(){
		return neededSideExtentionSnap;
	}

	/**
	 * Engedelyezi a masodlagos kurzor legkozelebbi Grid ponthoz valo igazitasat
	 * 
	 * @param needed
	 */
	public void setNeededGridSnap( boolean needed, Grid myGrid ){
		this.neededGridSnap = needed;
		this.myGrid = myGrid;
	}
	
	public boolean getNeededGridSnap(){
		return neededGridSnap;
	}
	
	public void setNeededSideDivisionSnap( boolean needed ){
		neededSideDivisionSnap = needed;
	}
	
	public boolean getNeededSideDivisionSnap(){
		return neededSideDivisionSnap;
	}
	/**
	 * Beallitja hogy a Snap muvelet mekkora pixel tartomanyban mukodjon
	 * 
	 * @param delta
	 */
	public void setSnapDelta( int delta ){
		this.snapDelta = delta;
	}
	
	/**
	 * Visszaadja a Snap muvelet tartomanyat
	 * 
	 * @return
	 */
	public int getSnapDelta(){
		return this.snapDelta;
	}
	
	public void setSnapSideDivision( double sideDivision ){
		this.snapSideDivision = sideDivision;
	}
	
	public double getSnapSideDivision(){
		return this.snapSideDivision;
	}
	

	public void setNeedFocus( boolean needFocus){
		this.needFocus = needFocus;
	}
	
	public boolean needFocus(){
		return needFocus;
	}

	/**
	 * A masodlagos kurzor poziciojanak valtozasat figyelo osztalyok listaja
	 * 
	 */
	public void addCursorPositionChangeListener( CursorPositionChangeListener positionChangeListener ){
		secondaryCursorPositionChangeListenerList.add( positionChangeListener );
	}
	
	/**
	 * Egy DrawnBlock rajzolasat elvegzo factory megadasa
	 * @param drawnBlockFactory
	 */
	public void setDrawnBlockFactory( DrawnBlockFactory drawnBlockFactory ){
		this.drawnBlockFactory = drawnBlockFactory;
	}
	
	/**
	 * Visszaadja a kirajzolando elemek listajat
	 * @return
	 */
	public ArrayList<? extends DrawnBlock> getDrawnBlockList(){
		return drawnBlockList;
	}
	
	public Iterator<? extends DrawnBlock> iterator(){
		return drawnBlockList.iterator();
	}
	
	/**
	 * Hozzaad a megjelenitendo listahoz egy DrawnBlock-ot
	 * 
	 * @param drawnBlock
	 */
	public void addDrawnBlock( DrawnBlock drawnBlock ){

		if( !this.drawnBlockList.contains( drawnBlock ) ){
			this.drawnBlockList.add( drawnBlock );
		}		
	}
	
	/**
	 * Eltavolit egy DrawnBlock elemet a megjelenitendo DrawnBlock listabol
	 * 
	 * @param drawnBlock
	 */
//	public void removeDrawnBlock( DrawnBlock drawnBlock ){
//		this.drawnBlockList.remove( drawnBlock );
//	}
		
	public void removeDrawnBlock( int drawnBlock ){
		this.drawnBlockList.remove( drawnBlock );
	}
	
	/**
	 * Hozzaad egy DrawnBlock elemet a Temporary listahoz atmeneti megjelenitesre
	 * 
	 * @param drawnBlock
	 */
	public void addTemporaryDrawnBlock( DrawnBlock drawnBlock ){
		
		//Temporary reteget hasznaljuk a fokus megjelenitesre
		//Minden megjelenites utan torlodik a listener, ezert kell mindig hozzaadni
		this.addPainterListenerToTemporary( new TemporaryDrawnBlockPainterListener(), Level.UNDER );	
		
		if( !temporaryDrawnBlockList.contains(drawnBlock)){
			temporaryDrawnBlockList.add(drawnBlock);
		}	
	}
	
	public void addTemporarySecondaryCursor( SecondaryCursor secondaryCursor ){
		
		//Minden megjelenites utan torlodik a listener, ezert kell mindig hozzaadni
		this.addPainterListenerToTemporary( new TemporarySecondaryCursorPainterListener(), Level.ABOVE );
		
		//Itt nincs szukseg a lista megadasara, mert csak egy elem szerepel
	}

	public void zoomIn(double xCenter, double yCenter, int xPoint, int yPoint){
		super.zoomIn(xCenter, yCenter, xPoint, yPoint);

		//Azert kell, hogy a zoom utan kovetkezo ujrarajzolas miatt eltuno fokuszban levo DrawnBlock ujra bekeruljon a temporary listaba
		//Mozgast szimulal, mintha megmozdult volna a kurzor, ami azt eredmenyezi, hogy kirajzolodik a kurzor
		//fireMouseMoved();

		drawnBlockDrawListener.repaintSecondaryCursorAndDrawnBlockToDraw();
	}
	
	public void zoomOut(double xCenter, double yCenter, int xPoint, int yPoint){
		super.zoomOut(xCenter, yCenter, xPoint, yPoint);
		
		//Azert kell, hogy a zoom utan kovetkezo ujrarajzolas miatt eltuno fokuszban levo DrawnBlock ujra bekeruljon a temporary listaba
//		fireMouseMoved();
		
		drawnBlockDrawListener.repaintSecondaryCursorAndDrawnBlockToDraw();

	}
	

	
	/**
	 * 
	 * DrawnBlock rajzolasaert felelos osztaly
	 * 
	 * move, exited
	 * exited, dragged
	 * pressed, released clicked
	 * 
	 * @author akoel
	 *
	 */
	class DrawnBlockDrawListener implements MouseInputListener{
		
		//private PositionValue secondaryStartCursorPosition = new PositionValue(0, 0);
//		BigDecimal secondaryCursorPosition
		
		private boolean drawnStarted = false;
		private DrawnBlock drawnBlockToDraw = null;


		@Override
		public void mouseClicked(MouseEvent e) {
			
//System.err.println("clicked");			
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
			//Ha a baloldali egergombot nyomtam es meg nem kezdtem el rajzolni
			if( e.getButton() == MouseEvent.BUTTON1 && !drawnStarted && null != drawnBlockFactory ){
				
				//A kurzor pozicioja
//				secondaryStartCursorPosition.setX( secondaryCursor.getX() );
//				secondaryStartCursorPosition.setY( secondaryCursor.getY() );

				//Jelzem, hogy elkezdodott a rajzolas
				drawnStarted = true;
				
				//A szerkesztendo DrawnBlock legyartasa
				drawnBlockToDraw = drawnBlockFactory.getNewDrawnBlock( Status.INPROCESS, secondaryCursor.getX(), secondaryCursor.getY() ); 
						
				//Atmeneti listaba helyezi a most rajzolas alatt levo DrawnBlock-ot
				addTemporaryDrawnBlock( drawnBlockToDraw );

			//Ha jobboldali egergombot nyomok miutan mar elkezdtem a rajzot
			}else if( e.getButton() == MouseEvent.BUTTON3 && drawnStarted ){
				
				//Abbahagyja a rajzolast
				drawnStarted = false;
				
				//Ujrarajzoltatom a Canvas-t az elkezdett DrawnBlock nelkul
				revalidateAndRepaintCoreCanvas();
			}

//System.err.println("pressed");			
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			//Ha elindult mar egy rajzolasi folyamat
			if( drawnStarted ){
				
				//Jelzi, hogy meg nem indult el a kovetkezo DrawnBlock rajzolasa
				drawnStarted = false;
				
				if( drawnBlockToDraw.getX1() != drawnBlockToDraw.getX2() || drawnBlockToDraw.getY1() != drawnBlockToDraw.getY2()){
//System.err.println(drawnBlockToDraw.getX1() + ", " + drawnBlockToDraw.getY1() + " | " + drawnBlockToDraw.getX2() + ", " + drawnBlockToDraw.getY2()  );												
					//A lehelyezendo DrawnBlokk statusza NORMAL lesz
					drawnBlockToDraw.setStatus( Status.NORMAL );
				
					//Hozzaadom a statikusan kirajzolando DrawnBlock-ok listajahoz
					addDrawnBlock( drawnBlockToDraw );

				}
				
				//Az ujabb DrawnBlock meg nem letezik
				drawnBlockToDraw = null;
				
				//Azert kell, hogy az elengedes pillanataban ne tunjon el a masodlagos kurzor
				addTemporarySecondaryCursor(secondaryCursor);
				
				//Ujrarajzoltatom a Canvas-t az uj statikus DrawnBlock-kal egyutt
				revalidateAndRepaintCoreCanvas();
								
			}
			
//System.err.println("release");
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
//System.err.println("entered");	
			
//Visszakeri a fokuszt amivel a			
DrawnBlockCanvas.this.setFocusable(true);			
DrawnBlockCanvas.this.requestFocusInWindow();
			
			//Meghatarozza a masodlagos kurzor aktualis erteket
			findOutCursorPosition( e );

			//Kirajzolja a masodlagos kurzort
			addTemporarySecondaryCursor( secondaryCursor );
			
			repaintCoreCanvas();

		}

		@Override
		public void mouseExited(MouseEvent e) {
//System.err.println("exited");			
			revalidateAndRepaintCoreCanvas();
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {

//System.err.println("dragged");			
			
//System.err.println("elotte: " + secondaryCursor);			
			
			//Meghatarozza a masodlagos kurzor aktualis erteket
			findOutCursorPosition( e );
//System.out.println("utanna: " + secondaryCursor);
//System.err.println("");

			//Ha mar elkezdtem rajzolni
			if( drawnStarted ){
	
				// DrawnBlock meretet megvaltoztatja - automatikusan sorba rendezi a koordinatakat x1 <= x2, y1 <= y2
				drawnBlockToDraw.changeSize( secondaryCursor.getX(), secondaryCursor.getY() );
			
				//Elhelyezni a temporary listaban a most szerkesztendo DrawnBlock-ot
				addTemporaryDrawnBlock( drawnBlockToDraw );
			}

			//Elhelyezi a temporary listaban a masodlagos kurzort
			addTemporarySecondaryCursor( secondaryCursor );
			
			//Kirajzolja az elhelyezett szerkesztedno DrawnBlock-ot es a masodlagos kurzort
			repaintCoreCanvas();
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
//System.err.println("moved");
			
			//Meghatarozza a masodlagos kurzor aktualis erteket
			findOutCursorPosition( e );
			
			//Kirajzolja a masodlagos kurzort
			addTemporarySecondaryCursor( secondaryCursor );
			
			//Kirajzolja a masodlagos kurzort
			repaintCoreCanvas();

		}
		
		
		class Arrange{
			DrawnBlock drawnBlockToArrangeX = null;
			DrawnBlock drawnBlockToArrangeY = null;
			
			BigDecimal positionX = null;
			BigDecimal positionY = null;
			
			public void addDrawnBlockToArrangeX( DrawnBlock drawnBlockToArrange, BigDecimal position ){
				this.drawnBlockToArrangeX = drawnBlockToArrange;
				this.positionX = position;
			}

			public void addDrawnBlockToArrangeY( DrawnBlock drawnBlockToArrange, BigDecimal position ){
				this.drawnBlockToArrangeY = drawnBlockToArrange;
				this.positionY = position;
			}
			
			public DrawnBlock getDrawnBlockX(){
				return drawnBlockToArrangeX;
			}
			
			public DrawnBlock getDrawnBlockY(){
				return drawnBlockToArrangeY;
			}
			
			public BigDecimal getPositionX(){
				return positionX;
			}

			public BigDecimal getPositionY(){
				return positionY;
			}

		}

		/**
		 * Meghatarozza a masodlagos kurzor aktualis erteket
		 * 
		 * @param e
		 */
		private void findOutCursorPosition( MouseEvent e ){			
			
			BigDecimal tmpX1, tmpX2, tmpY1, tmpY2;
			
			//Kurzor poziciojanak kerekitese a megadott pontossagra
			BigDecimal x = getRoundedBigDecimalWithPrecision( getWorldXByPixel( e.getX() ) );
			BigDecimal y = getRoundedBigDecimalWithPrecision( getWorldYByPixel( e.getY() ) );
			
			//-------------------------------------------------------------------------------
			//
			// DrawnBlock-ok oldalvonalahoz probalja igazitani a masodlagos kurzor poziciojat
			//
			// -Vegig megy minden statikusan lehelyezett DrawnBlock-on
			// -Megnezi, hogy valamelyik oldalahoz, vagy az oldalanak meghosszabbitasahoz eleg
			//  kozel van-e a kurzor
			// -X es Y iranyba a Kurzorhoz a legkozelebb levo oldalhoz (ha az adott kozelsegen belul van)
			//  igazitja a Masodlagos Kurzort
			//
			//-------------------------------------------------------------------------------
			int delta = getSnapDelta();
			
			BigDecimal dx = new BigDecimal( getWorldXLengthByPixel( delta ) );
			BigDecimal dy = new BigDecimal( getWorldXLengthByPixel( delta ) );
			
			BigDecimal minDX = new BigDecimal( Double.MAX_VALUE );
			BigDecimal minDY = new BigDecimal( Double.MAX_VALUE );
			Arrange arrange = new Arrange();
			
			//--------------------------------------------------------
			//
			// Ha engedelyezett az oldal kiterjeszteshez valo igazitas
			//
			//--------------------------------------------------------
			if( getNeededSideExtentionSnap() ){

				for( DrawnBlock db : drawnBlockList ){
				
				//Ha megfelelo kozelsegben vagyok az egyik lehelyezett DrawnBlock-hoz. 
				//if( db.intersects( new Block( x-dx, y-dy, x+dx, y+dy) ) ){
					
					//Bal oldalrol kozeliti a DrawnBlock baloldalat
					if( db.getX1().subtract( x ).compareTo( new BigDecimal("0" ) ) > 0 && db.getX1().subtract( x ).compareTo( dx ) < 0 ){
					//if( ( db.getX1() - x ) > 0 && ( db.getX1() - x ) < dx ){
						
						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
						if(
								( db.getX1().subtract( x ).compareTo( minDX ) < 0 ) ||
								( db.getX1().subtract( x ).compareTo( minDX ) == 0 && y.compareTo(db.getY1()) >= 0 && y.compareTo( db.getY2()) <= 0 )
//								( ( db.getX1() - x ) < minDX ) || 
//								( ( db.getX1() - x ) == minDX &&  y >= db.getY1() && y <= db.getY2() ) 
						){
							minDX = db.getX1().subtract( x );
							arrange.addDrawnBlockToArrangeX( db, db.getX1() );
//							minDX = db.getX1() - x;
//							arrange.addDrawnBlockToArrangeX( db, db.getX1() );							
						}
						
					//!!! Bal oldalrol kozeliti a DrawnBlock jobboldalat !!!
					}else if( db.getX2().subtract( x ).compareTo(new BigDecimal("0" )) > 0 && db.getX2().subtract( x ).compareTo( dx ) < 0 ){
					//}else if( ( db.getX2() - x ) > 0 && ( db.getX2() - x ) < dx ){
							
						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
						if( 
								( db.getX2().subtract( x ).compareTo( minDX ) < 0 ) ||
								( db.getX2().subtract( x ).compareTo( minDX ) == 0 &&  y.compareTo( db.getY1()) >= 0 && y.compareTo( db.getY2() ) <= 0 )
//								( ( db.getX2() - x ) < minDX ) ||
//								( ( db.getX2() - x ) == minDX &&  y >= db.getY1() && y <= db.getY2() )
						){
							minDX = db.getX2().subtract( x );
							arrange.addDrawnBlockToArrangeX( db, db.getX2() );							
//							minDX = db.getX2() - x;
//							arrange.addDrawnBlockToArrangeX( db, db.getX2() );							
						}
						
					//Jobb oldalrol kozeliti a DrawnBlock jobb oldalat
					}else if( x.subtract( db.getX2() ).compareTo( new BigDecimal("0" )) > 0 && x.subtract( db.getX2() ).compareTo( dx ) < 0 ){
//					}else if( ( x - db.getX2() ) > 0 && ( x - db.getX2() ) < dx ){

						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
						if(
								( x.subtract( db.getX2() ).compareTo( minDX ) < 0 ) ||
								( x.subtract( db.getX2() ).compareTo( minDX ) == 0 &&  y.compareTo( db.getY1() ) >= 0 && y.compareTo( db.getY2() ) <= 0 )
//								( ( x - db.getX2() ) < minDX ) ||
//								( ( x - db.getX2() ) == minDX &&  y >= db.getY1() && y <= db.getY2() )
						){
							minDX = x.subtract( db.getX2() );
							arrange.addDrawnBlockToArrangeX( db, db.getX2() );			
//							minDX = x - db.getX2();
//							arrange.addDrawnBlockToArrangeX( db, db.getX2() );							
						}
					
					//!!! Jobb oldalrol kozeliti a DrawnBlock bal oldalat !!!
					}else if( x.subtract( db.getX1() ).compareTo( new BigDecimal("0")) > 0 && x.subtract( db.getX1() ).compareTo( dx ) < 0 ){
//					}else if( ( x - db.getX1() ) > 0 && ( x - db.getX1() ) < dx ){

						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
						if( 
								( x.subtract( db.getX1() ).compareTo( minDX ) < 0 ) ||
								( x.subtract( db.getX1() ).compareTo( minDX ) == 0 &&  y.compareTo( db.getY1() ) >= 0 && y.compareTo( db.getY2() ) <= 0 )
//								( ( x - db.getX1() ) < minDX ) ||
//								( ( x - db.getX1() ) == minDX &&  y >= db.getY1() && y <= db.getY2() )
						){
							minDX = x.subtract( db.getX1() );
							arrange.addDrawnBlockToArrangeX( db, db.getX1() );
//							minDX = x - db.getX1();
//							arrange.addDrawnBlockToArrangeX( db, db.getX1() );							
						}						
					}
					
					//Fentrol kozeliti a DrawnBlock tetejet
					if( y.subtract( db.getY2() ).compareTo( new BigDecimal("0")) > 0 && y.subtract( db.getY2() ).compareTo( dy ) < 0 ){
//					if( ( y - db.getY2() ) > 0 && ( y - db.getY2() ) < dy ){						
						
						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
						if(
								( y.subtract( db.getY2() ).compareTo( minDY ) < 0 ) ||
								( y.subtract( db.getY2() ).compareTo( minDY ) == 0 &&  x.compareTo( db.getX1() ) >= 0 && x.compareTo( db.getX2() ) <= 0 )
//								( ( y - db.getY2() ) < minDY ) ||
//								( ( y - db.getY2() ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
						){
							minDY = y.subtract( db.getY2() );
							arrange.addDrawnBlockToArrangeY( db, db.getY2() );
//							minDY = y - db.getY2();
//							arrange.addDrawnBlockToArrangeY( db, db.getY2() );							
						}
					
					//!!! Fentrol kozeliti a DrawBlock aljat !!!
					}else if( y.subtract( db.getY1() ).compareTo( new BigDecimal("0")) > 0 && y.subtract( db.getY1() ).compareTo( dy ) < 0 ){
//					}else if( ( y - db.getY1() ) > 0 && ( y - db.getY1() ) < dy ){						
						
						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
						if( 
								( y.subtract( db.getY1() ).compareTo( minDY ) < 0 )||
								( y.subtract( db.getY1() ).compareTo( minDY ) == 0 &&  x.compareTo( db.getX1() ) >= 0 && x.compareTo( db.getX2() ) <= 0 )
//								( ( y - db.getY1() ) < minDY ) ||
//								( ( y - db.getY1() ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
						){
							minDY = y.subtract( db.getY1() );
							arrange.addDrawnBlockToArrangeY( db, db.getY1() );
//							minDY = y - db.getY1();
//							arrange.addDrawnBlockToArrangeY( db, db.getY1() );							
						}						
					
					//Alulrol kozeliti a DrawnBlock aljat
					}else if( db.getY1().subtract( y ).compareTo(new BigDecimal("0")) > 0 && db.getY1().subtract( y ).compareTo(dy) < 0 ){
//					}else if( ( db.getY1() - y ) > 0 && ( db.getY1() - y ) < dy ){						

						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
						if( 
								( db.getY1().subtract( y ).compareTo( minDY ) < 0 ) ||
								( db.getY1().subtract( y ).compareTo( minDY ) == 0 &&  x.compareTo( db.getX1() ) >= 0 && x.compareTo( db.getX2() ) <= 0 )
//								( ( db.getY1() - y ) < minDY ) ||
//								( ( db.getY1() - y ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
						){
							minDY = db.getY1().subtract( y );
							arrange.addDrawnBlockToArrangeY( db, db.getY1() );			
//							minDY = db.getY1() - y;
//							arrange.addDrawnBlockToArrangeY( db, db.getY1() );							
						}						
					
					//!!! Alulrol kozeliti a DrawnBlock tetejet !!!
					}else if( db.getY2().subtract( y ).compareTo(new BigDecimal("0")) > 0 && db.getY2().subtract(y).compareTo(dy) < 0 ){
//					}else if( ( db.getY2() - y ) > 0 && ( db.getY2() - y ) < dy ){						

						//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
						//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
						if( 
								( db.getY2().subtract( y ).compareTo( minDY ) < 0 ) ||
								( db.getY2().subtract( y ).compareTo( minDY ) == 0 &&  x.compareTo(db.getX1()) >= 0 && x.compareTo( db.getX2() ) <= 0 )
								
//								( ( db.getY2() - y ) < minDY ) ||
//								( ( db.getY2() - y ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
						){
							minDY = db.getY2().subtract( y );
							arrange.addDrawnBlockToArrangeY( db, db.getY2() );
//							minDY = db.getY2() - y;
//							arrange.addDrawnBlockToArrangeY( db, db.getY2() );							

						}
					}	
				//}				
				}			
			}
			
			//------------------------------------------
			//
			// Ha engedelyezett a Grid-hez valo igazitas
			//
			//------------------------------------------
			
			if( getNeededGridSnap() ){
				
				BigDecimal xStart = new BigDecimal( String.valueOf(Math.round( ( x.doubleValue() ) / myGrid.getDeltaGridX() ) * myGrid.getDeltaGridX() ) );
				BigDecimal yStart = new BigDecimal( String.valueOf(Math.round( ( y.doubleValue() ) / myGrid.getDeltaGridY() ) * myGrid.getDeltaGridY() ) );
//				double xStart = Math.round( ( x ) / myGrid.getDeltaGridX() ) * myGrid.getDeltaGridX();
//				double yStart = Math.round( ( y ) / myGrid.getDeltaGridY() ) * myGrid.getDeltaGridY();

				
				//Ha ez kozelebb van, mint az eddigi legkozelebbi
				
				if( xStart.subtract( x ).abs().compareTo( dx ) < 0 && xStart.subtract( x ).compareTo( minDX ) < 0 ){
					minDX = xStart.subtract( x ).abs(); 
					arrange.addDrawnBlockToArrangeX( null, getRoundedBigDecimalWithPrecisionFormBigDecimal( xStart ) );				
					//arrange.addDrawnBlockToArrangeX( null, xStart );
				}
				
				if( yStart.subtract( y ).abs().compareTo( dy ) < 0 && yStart.subtract( y ).compareTo( minDY ) < 0 ){
					minDY = yStart.subtract( y ).abs(); 
					arrange.addDrawnBlockToArrangeY( null, getRoundedBigDecimalWithPrecisionFormBigDecimal( yStart ) );
					//arrange.addDrawnBlockToArrangeY( null, yStart );
				}	
				
//				if( Math.abs( xStart - x ) < dx && Math.abs( xStart - x ) < minDX ){
//					minDX = Math.abs( xStart - x );
//					arrange.addDrawnBlockToArrangeX( null, xStart );				
//				}
//				if( Math.abs( yStart - y ) < dy && Math.abs( yStart - y ) < minDY ){
//					minDY = Math.abs( yStart - y );
//					arrange.addDrawnBlockToArrangeY( null, yStart );
//				}	
			
			}
					
			//
			// Most vegzi el a masodlagos kurzor leendo uj koordinatainak modositasat. 
			// A valodi kurzorhoz legkozelebb illesztheto pontot veszi
			// Meg nem tolti be a Masodlagos Kurzorba
			//
			if( null != arrange.getPositionX() ){
				x = arrange.getPositionX();
			}
				
			if( null != arrange.getPositionY() ){
				y = arrange.getPositionY();
			}
			
			//-------------------------------------------------
			//
			// Ha engedelyezett az oldal osztasra valo igazitas
			//
			//-------------------------------------------------
			if( getNeededSideDivisionSnap() ){
			
				BigDecimal sideDivision = new BigDecimal( String.valueOf( getSnapSideDivision() ) );
				
				int cycle = ( new BigDecimal("1").divide( sideDivision, 10, RoundingMode.HALF_UP ) ).intValue();
		
				//Fuggoleges oldalak meghosszabitasara tortent illesztes DE
				//nem tortent a vizszintes oldalak meghosszabitasara illesztes
				if( null != arrange.getDrawnBlockX() && null == arrange.getDrawnBlockY() ){
			
					//Az Y erteket valtoztathatjuk
					DrawnBlock db = arrange.getDrawnBlockX();
		
					for( int i = 1; i < cycle; i++ ){						
						
						BigDecimal possibleNewPosition = db.getY1().add( sideDivision.multiply(db.getHeight() ).multiply( new BigDecimal(i) ) );
						if( possibleNewPosition.subtract(y).abs().compareTo(dy) < 0){
							y = possibleNewPosition;
							break;
						}
					
						//double possibleNewPosition = db.getY1() + i * sideDivision * db.getHeight();
						//if( Math.abs( possibleNewPosition - y ) < dy ){
//							y = possibleNewPosition;
//							break;
//						}
					}
			
				//Vizszintes oldalak meghosszabitasara tortent illeszted DE
				//Nem tortent a fuggoleges oldalak meghosszabbitasara illesztes
				}else if( null != arrange.getDrawnBlockY() && null == arrange.getDrawnBlockX() ){
				
					//Az X erteket valtoztathatjuk
					DrawnBlock db = arrange.getDrawnBlockY();
				
					for( int i = 1; i < cycle; i++ ){
						
						BigDecimal possibleNewPosition = db.getX1().add( sideDivision.multiply(db.getWidth() ).multiply( new BigDecimal(i) ) );
						if( possibleNewPosition.subtract(x).abs().compareTo(dx) < 0){
							x = possibleNewPosition;
							break;
						}
						
//						double possibleNewPosition = db.getX1() + i * sideDivision * db.getWidth();
//						if( Math.abs( possibleNewPosition - x ) < dx ){
//							x = possibleNewPosition;
//							break;
//						}
					}
				}
			}
			
			
			//-----------------------------------
			//
			// Atfedesek elkerulesenek vezerlese
			//
			//-----------------------------------
			
			//
			// Ha meg nem kezdodott el a rajzolas, szabadon mozgo kurzor
			//
//TODO itt meg kellene oldani, hogy ha ket egymast erinto blokk koze kerulne, az nem OK				
			if( !drawnStarted ){

				//Megnezi, hogy az aktualis kurzor egy lehelyezett DrawnBlock-ra esik-e
				for( DrawnBlock db: drawnBlockList ){
				
					//Beleesik a kurzor egy lehelyezett DrawnBlock belsejeben
					if( x.compareTo( db.getX1() ) > 0 && x.compareTo( db.getX2() ) < 0 && y.compareTo( db.getY1() ) > 0 && y.compareTo( db.getY2() ) < 0 ){
//					if( ( x > db.getX1() && x < db.getX2() ) && ( y > db.getY1() && y < db.getY2() ) ){

						//Ha az elobbi X poziciot hasznalom, akkor kivul kerulok
						if( ( secondaryCursor.getX().compareTo( db.getX1() ) <= 0 || secondaryCursor.getX().compareTo( db.getX2() ) >= 0 ) && ( y.compareTo( db.getY1() ) > 0 && y.compareTo( db.getY2() ) < 0 ) ){
//						if( ( secondaryCursor.getX() <= db.getX1() || secondaryCursor.getX() >= db.getX2() ) && ( y > db.getY1() && y < db.getY2() ) ){
							x = secondaryCursor.getX();
						
						//Ha az elobbi Y poziciot hasznalom, akkor kivul kerulok
						}else if( ( x.compareTo( db.getX1() ) > 0 && x.compareTo( db.getX2() ) < 0 ) && ( secondaryCursor.getY().compareTo( db.getY1() ) <= 0 || secondaryCursor.getY().compareTo( db.getY2() ) >= 0 ) ){
//						}else if( ( x > db.getX1() && x < db.getX2() ) && ( secondaryCursor.getY() <= db.getY1() || secondaryCursor.getY() >= db.getY2() ) ){
							
							y = secondaryCursor.getY();
						
						//Kulonben
						}else{
						
							//Akkor a masodlagos kurzor marad a regi pozicioban
							return;
						}
					}
				}
			
			//
			// Ha mar elkezdte a rajzolast
			//
			}else{
				
				//
				// A feltetelezett uj DrawnBlock koordinatainak nagysag szerinti rendezese
				//
				if( x.compareTo( drawnBlockToDraw.getStartX() ) <= 0 ){
					tmpX1 = x;
					tmpX2 = drawnBlockToDraw.getStartX();
				}else{					
					tmpX1 = drawnBlockToDraw.getStartX();
					tmpX2 = x;
				}
				
				if( y.compareTo( drawnBlockToDraw.getStartY() ) <= 0 ){
					tmpY1 = y;
					tmpY2 =  drawnBlockToDraw.getStartY();
				}else{					
					tmpY1 =  drawnBlockToDraw.getStartY();
					tmpY2 = y;
				}
				
				
				
//				if( x.compareTo( secondaryStartCursorPosition.getX() ) <= 0 ){
//					tmpX1 = x;
//					tmpX2 = secondaryStartCursorPosition.getX();
//				}else{					
//					tmpX1 = secondaryStartCursorPosition.getX();
//					tmpX2 = x;
//				}
//				
//				if( y <= secondaryStartCursorPosition.getY() ){
//					tmpY1 = y;
//					tmpY2 = secondaryStartCursorPosition.getY();
//				}else{					
//					tmpY1 = secondaryStartCursorPosition.getY();
//					tmpY2 = y;
//				}

				
				// Vegig a lehelyezett DrawnBlock-okon
				for( DrawnBlock db: drawnBlockList ){
					
					//Ha a most szerkesztett DrawnBlock fedesbe kerulne egy mar lehelyezett DrawnBlock-kal
					Block block = new Block(tmpX1, tmpY1);
					block.changeSize( tmpX2, tmpY2 );
					
					if( db.intersectsOrContains( block )){
//					if( db.intersectsOrContains( new Rectangle.Double( tmpX1, tmpY1, tmpX2-tmpX1, tmpY2-tmpY1 ) )){
					
						//Akkor marad a regi kurzorpozicio
						return;
					}			
				}				
			}
			

			
			//------------------------------------------------
			//
			// A szerkesztendo elem megengedi-e az uj poziciot
			//
			//-------------------------------------------------
//System.err.println(drawnBlockToDraw);
//System.err.println(secondaryCursor);	
			if( drawnStarted && null != drawnBlockToDraw && !drawnBlockToDraw.enabledToChange( x, y ) ){
				
				return;				
			}

			//------------------------------
			//
			//
			// Kurzor figyelo kiszolgalasa
			//
			//-------------------------------
//TODO figyelem double atadas			
			for( CursorPositionChangeListener listener : secondaryCursorPositionChangeListenerList) {
				listener.getWorldPosition( x.doubleValue(), y.doubleValue() );
			}	
			
			
			//A Masodlagos kurzor poziciojanak beallitasa
			secondaryCursor.setPosition( x, y );
		}
		
			
		/**
		 * Megjeleniti a kurzort es a szerkesztes alatt allo DrawnBlock-ot
		 * az atmeneti retegben
		 * 
		 */
		public void repaintSecondaryCursorAndDrawnBlockToDraw(){
			
			if( null != drawnBlockToDraw ){
				addTemporaryDrawnBlock( drawnBlockToDraw );
			}
			
			if( null != secondaryCursor ){
				addTemporarySecondaryCursor( secondaryCursor );
			}
			
			repaintCoreCanvas();
			
		}
		
	}
	

	
	
	/**
	 * Azt figyeli, hogy egy DrawnBlock fokuszba kerult-e
	 * 
	 * @author akoel
	 *
	 */
/*	class DrawnBlockInFocusListener implements MouseMotionListener{
		
		@Override
		public void mouseMoved(MouseEvent e) {
	
			if( needFocus() ){
			
				double xValue = getWorldXByPixel(e.getX() );			
				double yValue = getWorldYByPixel(e.getY());
				boolean needToPrint = false;

				for( DrawnBlock sprite: drawnBlockList){
				
					SizeValue boundBox = sprite.getBoundBoxAbsolute();						
			
					if( 
						xValue >= boundBox.getXMin() &&
						xValue <= boundBox.getXMax() &&
						yValue >= boundBox.getYMin() &&
						yValue <= boundBox.getYMax()
					){
											
						addTemporarySprite(sprite);						
						needToPrint = true;
						sprite.setInFocus(true);

					}else{
						if( sprite.isInFocus() ){
							needToPrint = true;						
							sprite.setInFocus(false);
						}
					}					
				}
				if( needToPrint ){
					repaintCoreCanvas();
				}
			}				
		}
			
		@Override
		public void mouseDragged(MouseEvent e) {}					
	}
*/	

	/**
	 * A megadott pontossagra kerekiti a parametert
	 * 
	 * @param val
	 * @return
	 */
	public BigDecimal getRoundedBigDecimalWithPrecision( double val ){
		return new BigDecimal(  new DecimalFormat( getPrecision().getDecimalFormat(), decimalSymbol ).format(val)  );
	}

	public BigDecimal getRoundedBigDecimalWithPrecisionFormBigDecimal( BigDecimal val ){
		return val.setScale( getPrecision().getScale(), RoundingMode.HALF_UP );
	}
	
	public Double getRoundedDoubleWitPrecision( double val ){
		return Double.valueOf(  new DecimalFormat( getPrecision().getDecimalFormat(), decimalSymbol ).format(val)  );
	}
	
	
	/**
	 * DrawnBlock-ok kirajzolasaert felelos osztaly
	 * 			
	 * @author akoel
	 *
	 */
	class DrawnBlockPainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {

			for( DrawnBlock drawnBlock: drawnBlockList){
				drawnBlock.draw(g2);
			}
			
		}
		
		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
		
	}
	
	/**
	 * Az atmeneti retegben elhelyezett DrawnBlock-ok kirajzolasaert felelos osztaly
	 * 
	 * @author akoel
	 *
	 */
	class TemporaryDrawnBlockPainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			
			// Kirajzolja a Temporary listaban levo elemeket
			for( DrawnBlock drawnBlock: temporaryDrawnBlockList){
				drawnBlock.draw(g2);
			}
			
			//Majd a vegen torli a listat
			temporaryDrawnBlockList.clear();
		}		

		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
		
	}
	
	/**
	 * Az Masodlagos kurzor kirajzolasaert felelos osztaly
	 * 
	 * @author afoldvarszky
	 *
	 */
	class TemporarySecondaryCursorPainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void paintByCanvasAfterTransfer( MCanvas canvas, Graphics2D g2 ) {
		
			secondaryCursor.draw(g2);
			
		}
		
	}
	
	/**
	 * Masodlagos kurzort megvalosito osztaly
	 * 
	 * @author afoldvarszky
	 *
	 */
	class SecondaryCursor{
		
//		PositionValue cursorPosition = null;
		BigDecimal positionX;
		BigDecimal positionY;
		
		public SecondaryCursor(){
			positionX = new BigDecimal("0");
			positionY = new BigDecimal("0");
		}
		
		public void setPosition( BigDecimal x, BigDecimal y ){
			positionX = x;
			positionY = y;
		}
		
		public BigDecimal getX(){
			return positionX;
		}
		
		public BigDecimal getY(){
			return positionY;
		}
		
		public void draw( Graphics2D g2 ){
			int x, y;
			
			if( null != positionX && null != positionY ){

				x = getPixelXPositionByWorldBeforeTranslate( positionX.doubleValue() );
				y = getPixelYPositionByWorldBeforeTranslate( positionY.doubleValue() );
			
				g2.setColor( Color.white );
				g2.setStroke( basicStroke );
				g2.drawLine( x, y - 8, x, y + 8 );
				g2.drawLine( x - 8, y, x + 8, y );
			}
			
		}
		
		public String toString(){
			return new String( positionX.toPlainString() + ", " + positionY.toPlainString());
		}
	}
	
	

}
