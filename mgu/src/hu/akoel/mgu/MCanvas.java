package hu.akoel.mgu;


import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.Value;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;


public class MCanvas extends JPanel {

	public static enum Level {
		UNDER, ABOVE
	}
	
	private static final long serialVersionUID = 44576557802932936L;

	private SizeValue boundSize;
	//private double pixelPerUnitX, pixelPerUnitY;
//	private Position pixelPerUnit = new Position(0,0);
	private TranslateValue worldTranslate = new TranslateValue( 0.0, 0.0 );
	private TranslateValue positionToMiddle = null;
	private boolean wasTransferedToMiddle = false;
	private PossiblePixelPerUnits possiblePixelPerUnits = null;
	
	private ArrayList<PixelPerUnitChangeListener> pixelPerUnitChangeListenerList = new ArrayList<PixelPerUnitChangeListener>();
	private ArrayList<CursorPositionChangeListener> positionChangeListenerList = new ArrayList<CursorPositionChangeListener>();
	
	//PERMANENT listak
	ArrayList<PainterListener> highestList = new ArrayList<PainterListener>();
	ArrayList<PainterListener> middleList = new ArrayList<PainterListener>();
	ArrayList<PainterListener> deepestList = new ArrayList<PainterListener>();
	
	//TEMPORARY lista
	ArrayList<PainterListener> temporaryList = new ArrayList<PainterListener>();

	public CoreCanvas coreCanvas;
	
	/**
	 * Ez egy olyan canvas, melyben megadjuk hogy mind ket tengely beosztasa a parameterben megadott meretu.
	 * Ebbol az is k0vetkezik, hogy FREE_PORTION tipusu. vagyis a befoglalo canvas meretenek valtozasaval
	 * az oldalak meretaranya nem valtozik, csupan nagyobb vagy kisebb reszet lathatjuk a vilagnak.
	 * Ezen kivul megadhatjuk azt is hogy a Canvas kozepere melyik vilagkoordinata keruljon
	 * 
	 * @param borderType a Canvas keretenek tipusa
	 * @param background a Canvas hatterszine
	 * @param pixelPerUnit a felbontas merteke [pixel/unit]
	 * @param positionToMiddle Az itt megadott ertek legyen a canvas kozepen az indulaskor.
	 * null eseten a 0,0 pozicio lesz a bal also sarokban
	 */
	public MCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle ) {
		
		this.commonConstructor(borderType, background, possiblePixelPerUnits, null);
		
		this.positionToMiddle = positionToMiddle;
		
		//Ha nem adtam meg eltolast a koordinatarendszernek
		if(null == positionToMiddle ){
			setWasTransferedToMiddle(true);	

		}else{
			setWasTransferedToMiddle(false);
		}		
	}
	
	public MCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle, SizeValue boundSize ) {
		
		this.commonConstructor(borderType, background, possiblePixelPerUnits, boundSize);
		
		this.positionToMiddle = positionToMiddle;
		
		//Ha nem adtam meg eltolast a koordinatarendszernek
		if(null == positionToMiddle ){
			setWasTransferedToMiddle(true);	

		}else{
			setWasTransferedToMiddle(false);
		}		
	}
	
	private void commonConstructor(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, SizeValue boundSize ){
		this.setBorder(borderType);
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.possiblePixelPerUnits = possiblePixelPerUnits;
		this.setBoundSize( boundSize );
		this.add( getCoreCanvas( this, background ) );
	}
	
	public ArrayList<PainterListener> getHighestList(){
		return highestList;
	}
	
	public ArrayList<PainterListener> getMiddleList(){
		return middleList;
	}
	
	public ArrayList<PainterListener> getDeepestList(){
		return deepestList;
	}
	
	public void setPossiblePixelPerUnits( PossiblePixelPerUnits possiblePixelPerUnits ){
		this.possiblePixelPerUnits = possiblePixelPerUnits;
	}
	
	public void addPixelPerUnitChangeListener( PixelPerUnitChangeListener listener ){
		this.pixelPerUnitChangeListenerList.add(listener);
	}
	
	public void addCursorPositionChangeListener( CursorPositionChangeListener positionChangeListener ){
		positionChangeListenerList.add(positionChangeListener);
	}
	
	public void revalidateAndRepaintCoreCanvas(){
		coreCanvas.revalidate();
		coreCanvas.repaint();
	}
	
	public void repaintCoreCanvas(){
		coreCanvas.repaint();
	}
	
	public Value getWorldTranslate(){
		return worldTranslate;
	}
	
	public void setWorldTranslate(TranslateValue worldTranslate){
		this.worldTranslate = worldTranslate;
	}
	
	public void setWorldTranslateX( double x ){
		this.worldTranslate.setX(x);
	}
	
	public void setWorldTranslateY( double y){
		this.worldTranslate.setY(y);
	}
	
	public void setWorldTranslate( double x, double y){
		setWorldTranslateX(x);
		setWorldTranslateY(y);
	}
	
	public double getWorldTranslateX(){
		return this.worldTranslate.getX();
	}
	
	public double getWorldTranslateY(){
		return this.worldTranslate.getY();
	}
		
	public PixelPerUnitValue getPixelPerUnit(){
		return possiblePixelPerUnits.getActualPixelPerUnit();
	}

	public void firePixelPerUnitChangeListener(){
		for( PixelPerUnitChangeListener listener: pixelPerUnitChangeListenerList){
			listener.getPixelPerUnit( getPixelPerUnit());
		}
	}

	public double getPositionToMiddleX(){
		return this.positionToMiddle.getX();
	}
	
	public double getPositionToMiddleY(){
		return this.positionToMiddle.getY();
	}
	
	public void setPositionToMiddle( TranslateValue positionToMiddle ){
		this.positionToMiddle = positionToMiddle;
	}
	
	public void setWasTransferedToMiddle( boolean wasTransferedToMiddle ){
		this.wasTransferedToMiddle = wasTransferedToMiddle;
	}
	
	public boolean getWasTransferedToMiddle(){
		return this.wasTransferedToMiddle;
	}
	
	public Point getCoreCanvasLocationOnScreen(){
		return coreCanvas.getLocationOnScreen();
	}
	
	protected CoreCanvas getCoreCanvas(MCanvas canvas, Color background ){
		if( null == coreCanvas ){
			coreCanvas = new CoreCanvas( this, background );
		}
		return coreCanvas;
	}
	
	//
	// Figyelok kivulrol
	//
	
	/**
	 * Egermozgas figyelo hozzaadasa
	 */
	public void addMouseMotionListener( MouseMotionListener listener ){
		coreCanvas.addMouseMotionListener( listener );
	}
	
	/**
	 * Eger drag figyelo hozzaadasa
	 * A hatterben hozzaadja a listenert a coreCanvas ket mouse figyelojehez is:
	 * addMouseListener(listener)
	 * addMouseMotionListener(listener)
	 * @param listener
	 */
	public void addMouseInputListener( MouseInputListener listener ){
		coreCanvas.addMouseListener(listener);
		coreCanvas.addMouseMotionListener(listener);
	}

//	public void addKeyListener( KeyListener listener ){
//		System.err.println("HELLO");
//		coreCanvas.addKeyListener( listener );
//	}

	//
	//PERMANENT - Under list
	//
	public void addPainterListenerToDeepest(PainterListener painterListener, Level position) {
		//if(!this.deepestList.contains(painterListener)){
			if (position.equals(Level.ABOVE)) {
				deepestList.add(painterListener);
			} else {
				deepestList.add(0, painterListener);
			}
			coreCanvas.invalidate();
		//}
	}

	public void addPainterListenerToDeepest(PainterListener painterListener) {
		//if(!this.deepestList.contains(painterListener)){
			addPainterListenerToDeepest(painterListener, Level.ABOVE);
		//}
	}
	
	public void removePainterListenerFromDeepest( PainterListener painterListener ){
		deepestList.remove(painterListener);
		coreCanvas.invalidate();
	}
	
	public void removePainterListenersFromDeepest(){
		deepestList.clear();
		coreCanvas.invalidate();
	}

	//
	//PERMANENT - Middle list
	//
	public void addPainterListenerToMiddle(PainterListener painterListener, Level position) {
		//if(!this.middleList.contains(painterListener)){
			if (position.equals(Level.ABOVE)) {
				this.middleList.add(painterListener);
			} else {
				this.middleList.add(0, painterListener);
			}
			coreCanvas.invalidate();
		//}
	}

	public void addPainterListenerToMiddle(PainterListener painterListener) {
		//if(!this.middleList.contains(painterListener)){
			this.addPainterListenerToMiddle(painterListener, Level.ABOVE);
		//}
	}
	
	public void removePainterListenerFromMiddle( PainterListener painterListener ){
		middleList.remove(painterListener);
		coreCanvas.invalidate();
	}
	
	public void removePainterListenersFromMiddle(){
		middleList.clear();
		coreCanvas.invalidate();
	}
	
	//
	//PERMANENT - Above list
	//
	public void addPainterListenerToHighest(PainterListener painterListener, Level position) {
		//if(!this.highestList.contains(painterListener)){
			if (position.equals(Level.ABOVE)) {
				this.highestList.add(painterListener);
			} else {
				this.highestList.add(0, painterListener);
			}
			coreCanvas.invalidate();
		//}
	}

	public void addPainterListenerToHighest(PainterListener painterListener) {
		//if(!this.highestList.contains(painterListener)){
			this.addPainterListenerToHighest(painterListener, Level.ABOVE);
		//}
	}
	
	public void removePainterListenerFromHighest( PainterListener painterListener ){
		highestList.remove(painterListener);
		coreCanvas.invalidate();
	}
	
	public void removePainterListenersFromHighest(){
		highestList.clear();
		coreCanvas.invalidate();
	}
	
	//
	//TEMPORARY list
	//
	public void addPainterListenerToTemporary(PainterListener painterListener, Level position) {
		
		//if(!this.temporaryList.contains(painterListener)){			
			if (position.equals(Level.ABOVE)){
				this.temporaryList.add(painterListener);
			} else {			
				this.temporaryList.add(0, painterListener);
			}
		//}
	}

	public void addPainterListenerToTemporary(PainterListener painterListener ) {
		if(!this.temporaryList.contains(painterListener)){
			this.addPainterListenerToTemporary(painterListener, Level.ABOVE);
		}
	}
	
	public void removePainterListenerFromTemporary( PainterListener painterListener ){
		temporaryList.remove(painterListener);
	}
	
	public void removePainterListenersFromTemporary(){
		temporaryList.clear();
	}
	
	//---------
	// Mozgatas
	//---------
	public void moveX( int pixel ){
		
		double originalXTranslate = getWorldTranslate().getX();
		double possibleXTranslate = originalXTranslate;
		
		if( pixel > 0){
			possibleXTranslate -= getWorldXLengthByPixel(pixel + 1);
		}else{
			possibleXTranslate += getWorldXLengthByPixel(-pixel + 1);
		}

		setWorldTranslateX( possibleXTranslate );
			
		if( null != boundSize ){
			if( getWorldSize().getXMax() > getBoundSize().getXMax() ||	getWorldSize().getXMin() < getBoundSize().getXMin() ){
				setWorldTranslateX( originalXTranslate );
			}
		}
			
		coreCanvas.invalidate();
		coreCanvas.repaint();
	}
	
	public void moveY( int pixel ){

		double originalYTranslate = getWorldTranslateY();
		double possibleYTranslate = originalYTranslate;
		
		if( pixel > 0){
			possibleYTranslate += getWorldYLengthByPixel(pixel + 1);
		}else{
			possibleYTranslate -= getWorldYLengthByPixel(-pixel + 1);
		}
		
		setWorldTranslateY( possibleYTranslate );
		if( null != boundSize ){
			if( getWorldSize().getYMax() > getBoundSize().getYMax() ||	getWorldSize().getYMin() < getBoundSize().getYMin() ){
				setWorldTranslateY( originalYTranslate );
			}
		}	
			
		coreCanvas.invalidate();
		coreCanvas.repaint();
	}
	
	public void moveXY(int pixelX, int pixelY){
		
		double originalXTranslate = getWorldTranslateX();
		double possibleXTranslate = originalXTranslate;
		
		if( pixelX > 0){
			possibleXTranslate -= getWorldXLengthByPixel(pixelX);
		}else{
			possibleXTranslate += getWorldXLengthByPixel(-pixelX);
		}

		setWorldTranslateX( possibleXTranslate );
			
		//Ha van hatar definialva
		if( null != boundSize ){
			//Akkor megnezi, hogy tul ment-e a hataron
			if( getWorldSize().getXMax() > getBoundSize().getXMax() || getWorldSize().getXMin() < getBoundSize().getXMin() ){
				
				//Ha igen, akkor visszavonja a muveletet
				setWorldTranslateX( originalXTranslate );
			}
		}
		
		double originalYTranslate = getWorldTranslateY();
		double possibleYTranslate = originalYTranslate;
		
		if( pixelY > 0){
			possibleYTranslate += getWorldYLengthByPixel(pixelY);
		}else{
			possibleYTranslate -= getWorldYLengthByPixel(-pixelY);
		}
			
		setWorldTranslateY( possibleYTranslate );
			
		//Ha van hatar definialva
		if( null != boundSize ){
				
			//Akkor megnezi, hogy tul ment-e a hataron
			if( getWorldSize().getYMax() > getBoundSize().getYMax() || getWorldSize().getYMin() < getBoundSize().getYMin() ){
					
				//Ha igen, akkor visszavonja a muveletet
				setWorldTranslateY( originalYTranslate );
			}
		}

		coreCanvas.invalidate();
		coreCanvas.repaint();
	}
	
	
	public void moveDown(int pixel){		
		moveY(pixel);				
	}

	public void moveUp(int pixel){
		moveY(-pixel);		
	}

	public void moveRight(int pixel){	
		moveX(pixel);
	}
	
	public void moveLeft(int pixel){
		moveX(-pixel);
	}

	//-------------------
	//
	// Koordinata kezeles
	//
	//-------------------
	
	/**
	 * Visszaadja a kepernyo-koordinatarendszerben (origo a bal felso sarok) talalhato
	 * pixel x poziciojanak valos X koordinatajat
	 * @param pixel kepernyo-koordinatarendszerben levo pixel x koordinataja
	 * @return valos X koordinata
	 */
	public double getWorldXByPixel( int pixel ){
		return getWorldXLengthByPixel( pixel ) - getWorldTranslateX();
	}
	
	/**
	 * Visszaadja a kepernyo-koordinatarendszerben (origo a bal felso sarok) talalhato
	 * pixel y poziciojanak valos Y koordinatajat
	 * @param pixel kepernyo-koordinatarendszerben levo pixel y koordinataja
	 * @return valos Y koordinata
	 */
	public double getWorldYByPixel( int pixel ){
			return getWorldYLengthByPixel( getViewableSize().height - pixel ) - getWorldTranslateY();
	}
//TODO itt meg leht hogy dolgom van
	
	/**
	 * Visszaadja az adott pixelszam unit hosszat
	 * 
	 * @param pixel
	 * @return
	 */
	public double getWorldXLengthByPixel( int pixel ){
		return (pixel) / getPixelPerUnit().getX();
	}
	
	public double getWorldYLengthByPixel( int pixel ){
		return (pixel) / getPixelPerUnit().getY();
	}
	
	
	
	public int getPixelXLengthByWorld( double length ){
		double doubleLength = getPixelPerUnit().getX() * length;
		return (int)Math.round( doubleLength );
	}
	
	public int getPixelYLengthByWorld( double length ){
		double doubleLength = getPixelPerUnit().getY() * length;
		return (int)Math.round( doubleLength );
	}
	
	//Mas ne hasznalja
	public int getPixelXPositionByWorldBeforeTranslate( double x ){
		return getPixelXLengthByWorld( x );
	}
	
	//Mas ne hasznalja
	public int getPixelYPositionByWorldBeforeTranslate( double y ){
		return getPixelYLengthByWorld( y );
	}
	
	public double getMouseXPositionByWorld( double x ){
		return getPixelXLengthByWorld( x + getWorldTranslateX() );
	}
	
	public double getMouseYPositionByWorld( double y ){
		return getViewableSize().height - getPixelYLengthByWorld( y + getWorldTranslateY() );
	}
	
	//------------------------
	//
	//
	//------------------------
	
	/**
	 * Visszaadja teljes vilag meretet
	 * @return
	 */
	public SizeValue getWorldSize(){
		
		double width = getViewerWidth();
		double height = getViewerHeight();
		double xMin = getWorldXByPixel(0);
		double xMax = getWorldXByPixel((int)width-1);
		double yMax = getWorldYByPixel(0);
		double yMin = getWorldYByPixel((int)height-1);
			
		SizeValue size = new SizeValue(xMin, yMin, xMax, yMax);
	return size;

	}

	public SizeValue getBoundSize(){
		return this.boundSize;
	}
	
	public void setBoundSize( SizeValue boundSize ){
		this.boundSize = boundSize;
	}
	
//	public void setWorldSize( SizeValue worldSize ){
//		this.worldSize = worldSize;
//	}

	/**
	 * Visszaadja a teljes vilagbol lathato resz meretet
	 * @return
	 */
	public Dimension getViewableSize(){
		return coreCanvas.getPreferredSize();
	}
	
	public int getWidth() {
		return getPreferredSize().width;		
	}

	public int getHeight() {
		return getPreferredSize().height;
	}


	/**
	 * Visszaadja a lathato vilag border nelkuli szelesseget
	 * Ha a vilag kilog a befoglalo panel szelessegebol, akkor a befoglalo panel
	 * szelessege a meghatarozo
	 * Ha a vilag lotyogne a befoglalo panel szelessegeben, akkor a vilag
	 * szelessege a meghatarozo
	 * @return
	 */
	public double getViewerWidth() {
		
		//Ez a szelesseg lehetne a befoglalo panel szelessege (ha nem korlatoznank)
		double possibleWidth = super.getWidth();

		//Akkor a befoglalo panel szelessege a mervado
		return possibleWidth - ( getInsets().right + getInsets().left);

	}
	
	/**
	 * Visszaadja a lathato vilag border nelkuli magassagat
	 * Ha a vilag kilog a befoglalo panel magassagabol, akkor a befoglalo panel
	 * magassaga a meghatarozo
	 * Ha a vilag lotyogne a befoglalo panel magassagaban, akkor a vilag
	 * magassaga a meghatarozo
	 * @return
	 */
	public double getViewerHeight() {
		
		//Ez a magassag lehetne a befoglalo panel magassaga (ha nem korlatoznank)
		double possibleHeight = super.getHeight();

		//Akkor a befoglalo panel magassaga a mervado
		return possibleHeight - ( getInsets().top + getInsets().bottom );		
	}
	
	/**
	 * Az egerkurzor mozgatasakor a kurzor vilag koordinatajanak atadasa 
	 * 
	 * @author akoel
	 *
	 */
	class MousePositionListener implements MouseMotionListener{
		private MCanvas canvas;
		
		public MousePositionListener( MCanvas canvas ){
			this.canvas = canvas;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			
			for( CursorPositionChangeListener listener : positionChangeListenerList){
				listener.getWorldPosition( canvas.getWorldXByPixel(e.getX()), canvas.getWorldYByPixel(e.getY()));
			}			
		}
	}
	
	/**
	 * Az eger kozepso gombjanak benyomasaval vegzett kepernyo mozgas figyelese
	 * 
	 * @author akoel
	 *
	 */
	class WheelMoveListener implements MouseInputListener{
		private int startX;
		private int startY;
		private boolean canBeDragged = false;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			if( e.getButton() == MouseEvent.BUTTON2){
				canBeDragged = true;
				startX = e.getX();
				startY = e.getY();
			}else{
				canBeDragged = false;
			}
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {			
		}
		@Override
		public void mouseEntered(MouseEvent e) {			
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mouseDragged(MouseEvent e) {
		
			if( canBeDragged ){
				int dX = startX - e.getX();
				int dY = startY - e.getY();	
				moveXY(dX, dY);
				startX = e.getX();
				startY = e.getY();			
			}
			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
		}
		
	}
	
	public void fireMouseMoved(){
		
		int x = MouseInfo.getPointerInfo().getLocation().x-coreCanvas.getLocationOnScreen().x;
		int y = MouseInfo.getPointerInfo().getLocation().y-coreCanvas.getLocationOnScreen().y;
		
		MouseEvent me = new MouseEvent(coreCanvas, 0, 0, MouseEvent.BUTTON1, x, y, 1, false);
		for(MouseMotionListener ml: coreCanvas.getMouseMotionListeners()){
		    ml.mouseMoved(me);
		}
	}
	
	public void fireMouseMoved( int x, int y ){
	
		MouseEvent me = new MouseEvent(coreCanvas, 0, 0, 0, x, y, 1, false);
		for(MouseMotionListener ml: coreCanvas.getMouseMotionListeners()){
		    ml.mouseMoved(me);
		}
	}
	
	/**
	 * Az eger kormanya altal letrehozott zoom figyelo osztaly
	 */
	class WheelZoomListener implements MouseWheelListener{
		public void mouseWheelMoved(MouseWheelEvent e){
			double xCenter, yCenter;
			xCenter = getWorldXByPixel(e.getX());
			yCenter = getWorldYByPixel(e.getY());

	      //Felfele tekeres -> ZoomIn
	      if (e.getWheelRotation() < 0){
	    	  zoomIn(xCenter, yCenter, e.getX(), e.getY() );

	      //Lefele tekeres -> ZoomOut
	      }else{
	    	  zoomOut(xCenter, yCenter, e.getX(), e.getY() );

	      }
	      
		}//mouseWheelMoved(MouseWheelEvent e)

	}//class WheelZoomListener

	 public void zoomIn(double xCenter, double yCenter, int xPoint, int yPoint){
		 
		 if( possiblePixelPerUnits.doNextZoomIn() ){
			 
			 //Ertesiti a pixelPerUnit valtozasfigyelot
			 firePixelPerUnitChangeListener();
			 
			 Value rate = possiblePixelPerUnits.getActualRate();		 
		 
			 double possibleXTranslate = getWorldTranslateX() - getWorldXLengthByPixel(xPoint) * (rate.getX()-1);
			 double possibleYTranslate = getWorldTranslateY() - getWorldYLengthByPixel(getViewableSize().height-yPoint) * (rate.getY()-1);
		 
			 setWorldTranslateX( possibleXTranslate );
			 setWorldTranslateY( possibleYTranslate );
		 
			// this.getParent().repaint();
			// coreCanvas.invalidate();
			// coreCanvas.repaint();
			// revalidate();
			 
			 this.getParent().repaint();
			 this.revalidateAndRepaintCoreCanvas();

			 revalidate();
			 
		 }
	 }	
	 
	 public void zoomOut(double xCenter, double yCenter, int xPoint, int yPoint){
		 
		 if( possiblePixelPerUnits.doNextZoomOut() ){
		 
			 Value rate = possiblePixelPerUnits.getActualRate();		 
		 
			 double originalXTranslate = getWorldTranslateX();
			 double possibleXTranslate = originalXTranslate + getWorldXLengthByPixel(xPoint) * (rate.getX()-1)/rate.getX();
		 
			 double originalYTranslate = getWorldTranslateY();
			 double possibleYTranslate = originalYTranslate + getWorldYLengthByPixel(getViewableSize().height-yPoint) * (rate.getY()-1)/rate.getY();

			 setWorldTranslate( possibleXTranslate, possibleYTranslate );
		 
			 boolean ok = true;
		 
			 //Szukseges a vizsgalat mert van szel definialva
			 if( null != boundSize ){
			 			 
				 boolean overlapLeft = false;
				 boolean overlapBottom = false;
			 
				 //Eloszor letolom az egeszet a bal also sarokba, ha a bal also koordinatak esetleg tullognanak
				 if( getWorldSize().getXMin() < getBoundSize().getXMin() ){
					 setWorldTranslateX( possibleXTranslate - (getBoundSize().getXMin() - getWorldSize().getXMin() ) );
					 overlapLeft = true;
				 }
			 
				 if( getWorldSize().getYMin() < getBoundSize().getYMin() ){
					 setWorldTranslateY( possibleYTranslate - (getBoundSize().getYMin() - getWorldSize().getYMin()) );
					 overlapBottom = true;
				 }
			 
				 //Ha a jobb felso sarok igy is kilog, akkor a zoom nem megengedett
				 if( overlapLeft && getWorldSize().getXMax() > getBoundSize().getXMax() ){
					 ok = false;
				 }else if( overlapBottom && getWorldSize().getYMax() > getBoundSize().getYMax()){
					 ok = false;
				 }
			 			 
				 if( ok ){
			 
					 boolean overlapRight = false;
					 boolean overlapTop = false;
				 
					 if( getWorldSize().getXMax() > getBoundSize().getXMax() ){
						 setWorldTranslateX( possibleXTranslate + (getWorldSize().getXMax() - getBoundSize().getXMax()) );
						 overlapRight = true;
					 }
			 
					 if( getWorldSize().getYMax() > getBoundSize().getYMax() ){
						 setWorldTranslateY( possibleYTranslate + (getWorldSize().getYMax() - getBoundSize().getYMax()) );
						 overlapTop = true;
					 }
				 
					 //Ha a bal also sarok igy is kilog, akkor a zoom nem megengedett
					 if( overlapRight && getWorldSize().getXMin() < getBoundSize().getXMin() ){
						 ok = false;
					 }else if( overlapTop && getWorldSize().getYMin() < getBoundSize().getYMin() ){
						 ok = false;
					 }
				 }
				
			 }

			 //Es ha igy sem fer bele e hatarba, akkor visszavonom a zoom muveletet
			 if( !ok ){
			 
				 //Visszaallitom az eredeti PixelPerUnit erteket
				 //Nem a fuggvenyen keresztul hivom meg, mert akkor a PixelPerUnitChangeListener meghivodna, de erre nincs szukseg,
				 //hiszen nem tortent PixelPerUnit valtozas
				 //pixelPerUnit.setX( originalXPPU );
				 //pixelPerUnit.setY( originalYPPU );
				 possiblePixelPerUnits.doNextZoomIn();

				 //Visszaallitom az eredeti eltolas erteket
				 setWorldTranslate( originalXTranslate, originalYTranslate );

				 //Lehetseges a kert valtozas
			 }else{
			 
				 //Ertesiti a pixelPerUnit valtozasfigyelot
				 firePixelPerUnitChangeListener();

			 }
		 
			 this.getParent().repaint();
			 this.revalidateAndRepaintCoreCanvas();

			 revalidate();
		 }
		  
	 }
	
	
	/**
	 * A rajzolo felulet maga
	 * 
	 * @author akoel
	 *
	 */
	class CoreCanvas extends JPanel {

		private static final long serialVersionUID = 5336269435310911828L;

		private MCanvas parent;
		private BufferedImage offImage;

		public CoreCanvas(MCanvas parent, Color background) {
			super();

			this.parent = parent;
			
			this.setBackground(background);
			
			// Eger kormanyaval letrehozott zoom figyelese
			addMouseWheelListener(new WheelZoomListener());
		    
			//Eger kormanyaval letrehozott move figyeles a koordinatarendszer mozgatasahoz
			WheelMoveListener myWheelMoveListener = new WheelMoveListener();
			addMouseListener(myWheelMoveListener);
			addMouseMotionListener(myWheelMoveListener);
			
			//Az egerpozicio figyelese
			addMouseMotionListener( new MousePositionListener(parent) );
		}

		  /**
		   * Amikor meghivodik a repaint()-update()-paint() harmas akkor, hogy
		   * ne torlodjon a canvas, at kell irni az update metodust
		   */
//		  public void update(Graphics g) {
//		    paint(g);
//		  }

		  /**
		   * Az canvas ervenytelenitesekor torolni kell a hatter kepet is
		   */
		  public void invalidate() {		  
		    super.invalidate();
		    offImage = null;	    
		  }
		  
		  public void paintComponent(Graphics g) {

			if( !getWasTransferedToMiddle() ){
				setWasTransferedToMiddle( true );
				setWorldTranslate( new TranslateValue( 
						( getWorldXLengthByPixel( (int)(getViewerWidth()/2) ) - getPositionToMiddleX() ), 
						( getWorldYLengthByPixel( (int)(getViewerHeight()/2) ) - getPositionToMiddleY() )  
						) );
			}
			
			Graphics2D offg2;
		    Graphics2D g2 = (Graphics2D) g;
		    int width = this.getWidth();
		    int height = this.getHeight();

		    super.paintComponent(g);

			if (offImage == null) {

				//Uj Canvas keszitese
				offImage = (BufferedImage) createImage(width, height);

				//Az uj canvas grafikai objektumanak elkerese
				offg2 = (Graphics2D) offImage.getGraphics();
				
				offg2.setColor( getBackground() );
				offg2.fillRect(0, 0, width, height);
				
				offg2.scale(1,-1);

				//Most tolom el a koordinatarendszert
				offg2.translate( ( getPixelXLengthByWorld(parent.getWorldTranslateX()) ), ( getPixelYLengthByWorld( parent.getWorldTranslateY()) - getHeight() ) );
				
				if (null != deepestList) {
					for (PainterListener painter : deepestList) {
						painter.paintByWorldPosition(parent, new MGraphics(parent, offg2));
						painter.paintByCanvasAfterTransfer(parent, offg2);
					}
				}

				if (null != middleList) {
					for (PainterListener painter : middleList) {
						painter.paintByWorldPosition(parent, new MGraphics(parent, offg2));
						painter.paintByCanvasAfterTransfer(parent, offg2);
					}
				}

				if (null != highestList) {
					for (PainterListener painter : highestList) {
						painter.paintByWorldPosition(parent, new MGraphics(parent, offg2));
						painter.paintByCanvasAfterTransfer(parent, offg2);
					}
				}
				
			}
			if (offImage != null) {
				
				//Kirajzolja  a bufferelt kepet
				g2.drawImage(offImage, 0, 0, this);
				
				g2.scale(1,-1);
				
				g2.translate( getPixelXLengthByWorld( parent.getWorldTranslateX()), getPixelYLengthByWorld( parent.getWorldTranslateY()) - getHeight() );
				
				if (null != temporaryList) {
					for (PainterListener painter : temporaryList) {
						painter.paintByWorldPosition(parent, new MGraphics(parent, g2));
						painter.paintByCanvasAfterTransfer(parent, g2);
					}
				}

				if (null != temporaryList) {
					temporaryList.clear();
				}
			}	
		}

		/**
		 * A lathato vilag merete
		 */
		public Dimension getPreferredSize() {
			double pixelWidth, pixelHeight;

			// Felveszi a szulo ablak meretet csokkentve a keret meretevel
			pixelWidth = parent.getViewerWidth();
			pixelHeight = parent.getViewerHeight();
	
			return new Dimension( (int)(pixelWidth), (int)(pixelHeight) );

		}	

	}
}
