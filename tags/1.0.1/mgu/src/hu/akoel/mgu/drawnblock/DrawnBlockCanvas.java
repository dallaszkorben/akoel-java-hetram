package hu.akoel.mgu.drawnblock;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.border.Border;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;

public class DrawnBlockCanvas extends MCanvas{

	private static final long serialVersionUID = -8308688255617119442L;
	
	public static enum Precision{
		per_10( "#.#", 1, 10 ),				//1/10		dm
		per_100( "#.##", 2, 100 ),			//1/100		cm
		per_1000( "#.###", 3, 1000 ),		//1/1000	mm
		per_10000( "#.####", 4, 10000 ),	//1/10000	1/10 mm		
		per_100000( "#.#####", 5, 100000 );	//1/100000	1/100 mm
		
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
	
	
	private DrawnBlockFactory drawnBlockFactory;
	
	private ArrayList<DrawnBlock> drawnBlockList = new ArrayList<DrawnBlock>();
	private ArrayList<DrawnBlock> temporaryDrawnBlockList = new ArrayList<DrawnBlock>();
	
	private DrawnBlockMouseListener drawnBlockMouseListener;
	
	private DrawnBlockPainterListener drawnBlockPainterListener = new DrawnBlockPainterListener();
	private ArrayList<CursorPositionChangeListener> secondaryCursorPositionChangeListenerList = new ArrayList<CursorPositionChangeListener>();
	
	private SecondaryCursor secondaryCursor = new DefaultSecondaryCursor( this );
	
	private boolean needFocus = true;
	
	private boolean enabledDrawn = true;
	
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
		
		//A kurzor mozgasat vizsgalolo listener
		this.setDrawnBlockMouseListener( new DrawnBlockMouseListener( this ) );
//		drawnBlockMouseListener = new DrawnBlockMouseListener( this );
		
		//A kurzor mozgasat vizsgalolo listener
		//this.addMouseInputListener( drawnBlockMouseListener );
		
		//Azt figyeli, hogy egy DrawnBlock fokuszba kerult-e
//		this.addMouseMotionListener( drawnBlockInFocusListener );
		
		//A kozepso reteg also felet hasznaljuk a DrawnBlock-ok megjelenitesere
		this.addPainterListenerToMiddle( drawnBlockPainterListener, Level.UNDER );
		

			
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

	public void setDrawnBlockMouseListener( DrawnBlockMouseListener drawnBlockMouseListener ){
		this.drawnBlockMouseListener = drawnBlockMouseListener;
		this.setMouseInputListener( drawnBlockMouseListener );
	}
	
/*	public void clearDrawnBlockMouseListener(){
		this.drawnBlockMouseListener = null;
		this.removeMouseInputListener();
	}
*/	
	public ArrayList<CursorPositionChangeListener> getSecondaryCursorPositionChangeListenerList(){
		return secondaryCursorPositionChangeListenerList;
	}
	
	/**
	 * Beallit egy masik Masodlakos kurzort
	 * 
	 * @param secondaryCursor
	 */
	public void setSecondaryCursor( SecondaryCursor secondaryCursor ){
		this.secondaryCursor = secondaryCursor;
		revalidateAndRepaintCoreCanvas();
	}
	
	/**
	 * Visszaadja a masodlagos kurzort
	 * 
	 * @return
	 */
	public SecondaryCursor getSecondaryCursor(){
		return this.secondaryCursor;
	}
	
	/**
	 * Visszaadja a rajzfelulet pontossagat
	 * 
	 * @return
	 */
	public Precision getPrecision(){
		return precision;
	}
	
	/**
	 * Engedelyezi vagy tiltja a rajzolast
	 * 
	 * @param enabled
	 */
	public void setEnabledDrawn( boolean enabled ){
		this.enabledDrawn = enabled;
	}
	
	/**
	 * Megmondja, hogy engedelyezett-e a rajzolas
	 * 
	 * @return
	 */
	public boolean isEnabledDrawn(){
		return this.enabledDrawn;
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

	public Grid getGrid(){
		return myGrid;
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
		if( null != drawnBlockMouseListener ){
			drawnBlockMouseListener.setDrawnBlockFactory(drawnBlockFactory);
		}
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
	public void removeDrawnBlock( DrawnBlock drawnBlock ){
		this.drawnBlockList.remove( drawnBlock );
	}
		
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

		drawnBlockMouseListener.repaintSecondaryCursorAndDrawnBlockToDraw();
	}
	
	public void zoomOut(double xCenter, double yCenter, int xPoint, int yPoint){
		super.zoomOut(xCenter, yCenter, xPoint, yPoint);
		
		//Azert kell, hogy a zoom utan kovetkezo ujrarajzolas miatt eltuno fokuszban levo DrawnBlock ujra bekeruljon a temporary listaba
//		fireMouseMoved();
		
		drawnBlockMouseListener.repaintSecondaryCursorAndDrawnBlockToDraw();

	}
	

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
	
}
