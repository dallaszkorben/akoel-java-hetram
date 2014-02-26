package hu.akoel.mgu.drawnblock;

import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;

import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;

import javax.swing.event.MouseInputListener;

public class DrawnBlockMouseListener implements MouseInputListener{
	
	private boolean drawnStarted = false;
	private DrawnBlock drawnBlockToDraw = null;
	private DrawnBlockCanvas canvas;
	private DrawnBlockFactory drawnBlockFactory;
	
	public DrawnBlockMouseListener( DrawnBlockCanvas canvas ){
		this.canvas = canvas;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		//Ha a baloldali egergombot nyomtam es meg nem kezdtem el rajzolni, akkor elkezdi a rajzolast
		if( e.getButton() == MouseEvent.BUTTON1 && !drawnStarted && null != drawnBlockFactory && canvas.isEnabledDrawn() ){
			
			//A kurzor pozicioja
//			secondaryStartCursorPosition.setX( secondaryCursor.getX() );
//			secondaryStartCursorPosition.setY( secondaryCursor.getY() );

			//Jelzem, hogy elkezdodott a rajzolas
			drawnStarted = true;
			
			//A szerkesztendo DrawnBlock legyartasa
			SecondaryCursor secondaryCursor = canvas.getSecondaryCursor();
			drawnBlockToDraw = drawnBlockFactory.getNewDrawnBlock( Status.INPROCESS, secondaryCursor.getX(), secondaryCursor.getY() ); 
					
			//Atmeneti listaba helyezi a most rajzolas alatt levo DrawnBlock-ot
			canvas.addTemporaryDrawnBlock( drawnBlockToDraw );

		//Ha jobboldali egergombot nyomok miutan mar elkezdtem a rajzot
		}else if( e.getButton() == MouseEvent.BUTTON3 && drawnStarted ){
			
			//Abbahagyja a rajzolast
			drawnStarted = false;
			
			//Ujrarajzoltatom a Canvas-t az elkezdett DrawnBlock nelkul
			canvas.revalidateAndRepaintCoreCanvas();
		}		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		//Ha elindult mar egy rajzolasi folyamat
		if( drawnStarted ){
			
			//Jelzi, hogy meg nem indult el a kovetkezo DrawnBlock rajzolasa
			drawnStarted = false;
			
			if( drawnBlockToDraw.getX1() != drawnBlockToDraw.getX2() || drawnBlockToDraw.getY1() != drawnBlockToDraw.getY2()){

				//A lehelyezendo DrawnBlokk statusza NORMAL lesz
				drawnBlockToDraw.setStatus( Status.NORMAL );
			
				//Hozzaadom a statikusan kirajzolando DrawnBlock-ok listajahoz
				canvas.addDrawnBlock( drawnBlockToDraw );

			}
			
			//Az ujabb DrawnBlock meg nem letezik
			drawnBlockToDraw = null;
			
			//Azert kell, hogy az elengedes pillanataban ne tunjon el a masodlagos kurzor
			canvas.addTemporarySecondaryCursor( canvas.getSecondaryCursor() );
			
			//Ujrarajzoltatom a Canvas-t az uj statikus DrawnBlock-kal egyutt
			canvas.revalidateAndRepaintCoreCanvas();
							
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
//Visszakeri a fokuszt amivel a			
canvas.setFocusable(true);			
canvas.requestFocusInWindow();
		
		//Meghatarozza a masodlagos kurzor aktualis erteket
		findOutCursorPosition( e );

		//Kirajzolja a masodlagos kurzort
		canvas.addTemporarySecondaryCursor( canvas.getSecondaryCursor() );
		
		canvas.repaintCoreCanvas();

	}

	@Override
	public void mouseExited(MouseEvent e) {		
		canvas.revalidateAndRepaintCoreCanvas();		
	}

	@Override
	public void mouseDragged(MouseEvent e) {		
		
		 SecondaryCursor secondaryCursor = canvas.getSecondaryCursor();
		
		//Meghatarozza a masodlagos kurzor aktualis erteket
		findOutCursorPosition( e );

		//Ha mar elkezdtem rajzolni
		if( drawnStarted ){

			// DrawnBlock meretet megvaltoztatja - automatikusan sorba rendezi a koordinatakat x1 <= x2, y1 <= y2			
			drawnBlockToDraw.changeSize( secondaryCursor.getX(), secondaryCursor.getY() );
		
			//Elhelyezni a temporary listaban a most szerkesztendo DrawnBlock-ot
			canvas.addTemporaryDrawnBlock( drawnBlockToDraw );
		}

		//Elhelyezi a temporary listaban a masodlagos kurzort
		canvas.addTemporarySecondaryCursor( secondaryCursor );
		
		//Kirajzolja az elhelyezett szerkesztedno DrawnBlock-ot es a masodlagos kurzort
		canvas.repaintCoreCanvas();
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		//Meghatarozza a masodlagos kurzor aktualis erteket
		findOutCursorPosition( e );
		
		//Kirajzolja a masodlagos kurzort
		canvas.addTemporarySecondaryCursor( canvas.getSecondaryCursor() );
		
		//Kirajzolja a masodlagos kurzort
		canvas.repaintCoreCanvas();

	}	
	
	public class Arrange{
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
	public void findOutCursorPosition( MouseEvent e ){			
		
		BigDecimal tmpX1, tmpX2, tmpY1, tmpY2;
		
		//Kurzor poziciojanak kerekitese a megadott pontossagra
		BigDecimal x = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldXByPixel( e.getX() ) );
		BigDecimal y = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldYByPixel( e.getY() ) );
		
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
		int delta = canvas.getSnapDelta();
		
		BigDecimal dx = new BigDecimal( canvas.getWorldXLengthByPixel( delta ) );
		BigDecimal dy = new BigDecimal( canvas.getWorldXLengthByPixel( delta ) );
		
		BigDecimal minDX = new BigDecimal( Double.MAX_VALUE );
		BigDecimal minDY = new BigDecimal( Double.MAX_VALUE );
		Arrange arrange = new Arrange();
		
		SecondaryCursor secondaryCursor = canvas.getSecondaryCursor();
		 
		//--------------------------------------------------------
		//
		// Ha engedelyezett az oldal kiterjeszteshez valo igazitas
		//
		//--------------------------------------------------------
		if( canvas.getNeededSideExtentionSnap() ){

			@SuppressWarnings("unchecked")
			Iterator<DrawnBlock> it = (Iterator<DrawnBlock>) canvas.iterator();
			DrawnBlock db;
			while( it.hasNext() ){
				db = it.next();
			
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
//							( ( db.getX1() - x ) < minDX ) || 
//							( ( db.getX1() - x ) == minDX &&  y >= db.getY1() && y <= db.getY2() ) 
					){
						minDX = db.getX1().subtract( x );
						arrange.addDrawnBlockToArrangeX( db, db.getX1() );
//						minDX = db.getX1() - x;
//						arrange.addDrawnBlockToArrangeX( db, db.getX1() );							
					}
					
				//!!! Bal oldalrol kozeliti a DrawnBlock jobboldalat !!!
				}else if( db.getX2().subtract( x ).compareTo(new BigDecimal("0" )) > 0 && db.getX2().subtract( x ).compareTo( dx ) < 0 ){
				//}else if( ( db.getX2() - x ) > 0 && ( db.getX2() - x ) < dx ){
						
					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
					if( 
							( db.getX2().subtract( x ).compareTo( minDX ) < 0 ) ||
							( db.getX2().subtract( x ).compareTo( minDX ) == 0 &&  y.compareTo( db.getY1()) >= 0 && y.compareTo( db.getY2() ) <= 0 )
//							( ( db.getX2() - x ) < minDX ) ||
//							( ( db.getX2() - x ) == minDX &&  y >= db.getY1() && y <= db.getY2() )
					){
						minDX = db.getX2().subtract( x );
						arrange.addDrawnBlockToArrangeX( db, db.getX2() );							
//						minDX = db.getX2() - x;
//						arrange.addDrawnBlockToArrangeX( db, db.getX2() );							
					}
					
				//Jobb oldalrol kozeliti a DrawnBlock jobb oldalat
				}else if( x.subtract( db.getX2() ).compareTo( new BigDecimal("0" )) > 0 && x.subtract( db.getX2() ).compareTo( dx ) < 0 ){
//				}else if( ( x - db.getX2() ) > 0 && ( x - db.getX2() ) < dx ){

					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
					if(
							( x.subtract( db.getX2() ).compareTo( minDX ) < 0 ) ||
							( x.subtract( db.getX2() ).compareTo( minDX ) == 0 &&  y.compareTo( db.getY1() ) >= 0 && y.compareTo( db.getY2() ) <= 0 )
//							( ( x - db.getX2() ) < minDX ) ||
//							( ( x - db.getX2() ) == minDX &&  y >= db.getY1() && y <= db.getY2() )
					){
						minDX = x.subtract( db.getX2() );
						arrange.addDrawnBlockToArrangeX( db, db.getX2() );			
//						minDX = x - db.getX2();
//						arrange.addDrawnBlockToArrangeX( db, db.getX2() );							
					}
				
				//!!! Jobb oldalrol kozeliti a DrawnBlock bal oldalat !!!
				}else if( x.subtract( db.getX1() ).compareTo( new BigDecimal("0")) > 0 && x.subtract( db.getX1() ).compareTo( dx ) < 0 ){
//				}else if( ( x - db.getX1() ) > 0 && ( x - db.getX1() ) < dx ){

					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor Y koordinataja a fuggoleges oldalra esik
					if( 
							( x.subtract( db.getX1() ).compareTo( minDX ) < 0 ) ||
							( x.subtract( db.getX1() ).compareTo( minDX ) == 0 &&  y.compareTo( db.getY1() ) >= 0 && y.compareTo( db.getY2() ) <= 0 )
//							( ( x - db.getX1() ) < minDX ) ||
//							( ( x - db.getX1() ) == minDX &&  y >= db.getY1() && y <= db.getY2() )
					){
						minDX = x.subtract( db.getX1() );
						arrange.addDrawnBlockToArrangeX( db, db.getX1() );
//						minDX = x - db.getX1();
//						arrange.addDrawnBlockToArrangeX( db, db.getX1() );							
					}						
				}
				
				//Fentrol kozeliti a DrawnBlock tetejet
				if( y.subtract( db.getY2() ).compareTo( new BigDecimal("0")) > 0 && y.subtract( db.getY2() ).compareTo( dy ) < 0 ){
//				if( ( y - db.getY2() ) > 0 && ( y - db.getY2() ) < dy ){						
					
					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
					if(
							( y.subtract( db.getY2() ).compareTo( minDY ) < 0 ) ||
							( y.subtract( db.getY2() ).compareTo( minDY ) == 0 &&  x.compareTo( db.getX1() ) >= 0 && x.compareTo( db.getX2() ) <= 0 )
//							( ( y - db.getY2() ) < minDY ) ||
//							( ( y - db.getY2() ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
					){
						minDY = y.subtract( db.getY2() );
						arrange.addDrawnBlockToArrangeY( db, db.getY2() );
//						minDY = y - db.getY2();
//						arrange.addDrawnBlockToArrangeY( db, db.getY2() );							
					}
				
				//!!! Fentrol kozeliti a DrawBlock aljat !!!
				}else if( y.subtract( db.getY1() ).compareTo( new BigDecimal("0")) > 0 && y.subtract( db.getY1() ).compareTo( dy ) < 0 ){
//				}else if( ( y - db.getY1() ) > 0 && ( y - db.getY1() ) < dy ){						
					
					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
					if( 
							( y.subtract( db.getY1() ).compareTo( minDY ) < 0 )||
							( y.subtract( db.getY1() ).compareTo( minDY ) == 0 &&  x.compareTo( db.getX1() ) >= 0 && x.compareTo( db.getX2() ) <= 0 )
//							( ( y - db.getY1() ) < minDY ) ||
//							( ( y - db.getY1() ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
					){
						minDY = y.subtract( db.getY1() );
						arrange.addDrawnBlockToArrangeY( db, db.getY1() );
//						minDY = y - db.getY1();
//						arrange.addDrawnBlockToArrangeY( db, db.getY1() );							
					}						
				
				//Alulrol kozeliti a DrawnBlock aljat
				}else if( db.getY1().subtract( y ).compareTo(new BigDecimal("0")) > 0 && db.getY1().subtract( y ).compareTo(dy) < 0 ){
//				}else if( ( db.getY1() - y ) > 0 && ( db.getY1() - y ) < dy ){						

					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
					if( 
							( db.getY1().subtract( y ).compareTo( minDY ) < 0 ) ||
							( db.getY1().subtract( y ).compareTo( minDY ) == 0 &&  x.compareTo( db.getX1() ) >= 0 && x.compareTo( db.getX2() ) <= 0 )
//							( ( db.getY1() - y ) < minDY ) ||
//							( ( db.getY1() - y ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
					){
						minDY = db.getY1().subtract( y );
						arrange.addDrawnBlockToArrangeY( db, db.getY1() );			
//						minDY = db.getY1() - y;
//						arrange.addDrawnBlockToArrangeY( db, db.getY1() );							
					}						
				
				//!!! Alulrol kozeliti a DrawnBlock tetejet !!!
				}else if( db.getY2().subtract( y ).compareTo(new BigDecimal("0")) > 0 && db.getY2().subtract(y).compareTo(dy) < 0 ){
//				}else if( ( db.getY2() - y ) > 0 && ( db.getY2() - y ) < dy ){						

					//Ha ez kozelebb van, mint az eddigi legkozelebbi VAGY
					//pontaosan ugyan olyan tavolsagban, de a kurzor X koordinataja a vizszintes oldalra esik
					if( 
							( db.getY2().subtract( y ).compareTo( minDY ) < 0 ) ||
							( db.getY2().subtract( y ).compareTo( minDY ) == 0 &&  x.compareTo(db.getX1()) >= 0 && x.compareTo( db.getX2() ) <= 0 )
							
//							( ( db.getY2() - y ) < minDY ) ||
//							( ( db.getY2() - y ) == minDY &&  x >= db.getX1() && x <= db.getX2() )
					){
						minDY = db.getY2().subtract( y );
						arrange.addDrawnBlockToArrangeY( db, db.getY2() );
//						minDY = db.getY2() - y;
//						arrange.addDrawnBlockToArrangeY( db, db.getY2() );							

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
		
		if( canvas.getNeededGridSnap() ){
			
			BigDecimal xStart = new BigDecimal( String.valueOf(Math.round( ( x.doubleValue() ) / canvas.getGrid().getDeltaGridX() ) * canvas.getGrid().getDeltaGridX() ) );
			BigDecimal yStart = new BigDecimal( String.valueOf(Math.round( ( y.doubleValue() ) / canvas.getGrid().getDeltaGridY() ) * canvas.getGrid().getDeltaGridY() ) );
//			double xStart = Math.round( ( x ) / myGrid.getDeltaGridX() ) * myGrid.getDeltaGridX();
//			double yStart = Math.round( ( y ) / myGrid.getDeltaGridY() ) * myGrid.getDeltaGridY();

			//Ha ez kozelebb van, mint az eddigi legkozelebbi
			
			if( xStart.subtract( x ).abs().compareTo( dx ) < 0 && xStart.subtract( x ).compareTo( minDX ) < 0 ){
				minDX = xStart.subtract( x ).abs(); 
				arrange.addDrawnBlockToArrangeX( null, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( xStart ) );				
				//arrange.addDrawnBlockToArrangeX( null, xStart );
			}
			
			if( yStart.subtract( y ).abs().compareTo( dy ) < 0 && yStart.subtract( y ).compareTo( minDY ) < 0 ){
				minDY = yStart.subtract( y ).abs(); 
				arrange.addDrawnBlockToArrangeY( null, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( yStart ) );
				//arrange.addDrawnBlockToArrangeY( null, yStart );
			}	
			
//			if( Math.abs( xStart - x ) < dx && Math.abs( xStart - x ) < minDX ){
//				minDX = Math.abs( xStart - x );
//				arrange.addDrawnBlockToArrangeX( null, xStart );				
//			}
//			if( Math.abs( yStart - y ) < dy && Math.abs( yStart - y ) < minDY ){
//				minDY = Math.abs( yStart - y );
//				arrange.addDrawnBlockToArrangeY( null, yStart );
//			}	
		
		}
		
		
		//-------------------------------------------------
		//
		// Ha engedelyezett az oldal osztasra valo igazitas
		//
		//-------------------------------------------------
		if( canvas.getNeededSideDivisionSnap() ){
		
			Block cursorBlock = new Block( x.subtract( dx ), y.subtract( dy ) );
			cursorBlock.setWidth( dx.add(dx) );
			cursorBlock.setHeight( dy.add(dx) );
				
			BigDecimal sideDivision = new BigDecimal( String.valueOf( canvas.getSnapSideDivision() ) );
			
			int cycle = ( new BigDecimal("1").divide( sideDivision, 10, RoundingMode.HALF_UP ) ).intValue();
			
			@SuppressWarnings("unchecked")
			Iterator<DrawnBlock> it = (Iterator<DrawnBlock>) canvas.iterator();
			DrawnBlock db;
			while( it.hasNext() ){
				db = it.next();
			
				//Ha megfelelo kozelsegben van
				if( db.intersects( cursorBlock ) ){
					
					//
					//Akkor pontosabban is megvizsgalom
					//
					
					//A kurzor vizszintesen megfeleloen kozel van a jobboldali fuggoleges-hez, de meg semmit nem tudunk a fuggoleges elhelyezkedeserol
					if( db.getStopX().compareTo( cursorBlock.getStartX() ) > 0 && db.getStopX().compareTo( cursorBlock.getStopX() ) < 0  ){
						
						for( int i = 1; i < cycle; i++ ){						
							
							BigDecimal possibleNewPosition = db.getY1().add( sideDivision.multiply(db.getHeight() ).multiply( new BigDecimal(i) ) );
							if( possibleNewPosition.subtract(y).abs().compareTo(dy) < 0){
								
								arrange.addDrawnBlockToArrangeX( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( db.getStopX() ) );
								arrange.addDrawnBlockToArrangeY( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( possibleNewPosition ) );
								
								break;
							}						
						}	
					//A kurzor vizszintesen megfeleloen kozel van a baloldali fuggoleges-hez, de meg semmit nem tudunk a fuggoleges elhelyezkedeserol
					}else if( db.getStartX().compareTo( cursorBlock.getStartX() ) > 0 && db.getStartX().compareTo( cursorBlock.getStopX() ) < 0  ){
						
						for( int i = 1; i < cycle; i++ ){						
							
							BigDecimal possibleNewPosition = db.getY1().add( sideDivision.multiply(db.getHeight() ).multiply( new BigDecimal(i) ) );
							if( possibleNewPosition.subtract(y).abs().compareTo(dy) < 0){
								
								arrange.addDrawnBlockToArrangeX( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( db.getStartX() ) );
								arrange.addDrawnBlockToArrangeY( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( possibleNewPosition ) );
								
								break;
							}						
						}						

					//A kurzor fuggolegesen megfeleloen kozel van az also vizszinteshez-hez
					}else if( db.getStartY().compareTo( cursorBlock.getStartY() ) > 0 && db.getStartY().compareTo( cursorBlock.getStopY() ) < 0  ){
						
						for( int i = 1; i < cycle; i++ ){
							
							BigDecimal possibleNewPosition = db.getX1().add( sideDivision.multiply(db.getWidth() ).multiply( new BigDecimal(i) ) );
							if( possibleNewPosition.subtract(x).abs().compareTo(dx) < 0){
								
								arrange.addDrawnBlockToArrangeY( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( db.getStartY() ) );
								arrange.addDrawnBlockToArrangeX( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( possibleNewPosition ) );
								break;
							}
						}
					
					//A kurzor fuggolegesen megfeleloen kozel van az felso vizszinteshez-hez	
					}else if( db.getStopY().compareTo( cursorBlock.getStartY() ) > 0 && db.getStopY().compareTo( cursorBlock.getStopY() ) < 0  ){
						
						for( int i = 1; i < cycle; i++ ){
							
							BigDecimal possibleNewPosition = db.getX1().add( sideDivision.multiply(db.getWidth() ).multiply( new BigDecimal(i) ) );
							if( possibleNewPosition.subtract(x).abs().compareTo(dx) < 0){
								
								arrange.addDrawnBlockToArrangeY( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( db.getStopY() ) );
								arrange.addDrawnBlockToArrangeX( db, canvas.getRoundedBigDecimalWithPrecisionFormBigDecimal( possibleNewPosition ) );
								break;
							}
						}
					}			
				}
			}		
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
		
		//-----------------------------------
		//
		// Atfedesek elkerulesenek vezerlese
		//
		//-----------------------------------
		
		//
		// Ha meg nem kezdodott el a rajzolas, szabadon mozgo kurzor
		//
//TODO itt meg kellene oldani, hogy ha ket egymast erinto blokk koze kerulne, az nem OK				
		if( !drawnStarted && canvas.isEnabledDrawn() ){
			 
			//Megnezi, hogy az aktualis kurzor egy lehelyezett DrawnBlock-ra esik-e
			@SuppressWarnings("unchecked")
			Iterator<DrawnBlock> it = (Iterator<DrawnBlock>) canvas.iterator();
			DrawnBlock db;
			while( it.hasNext() ){
				db = it.next();
				
				//Beleesik a kurzor egy lehelyezett DrawnBlock belsejeben
				if( x.compareTo( db.getX1() ) > 0 && x.compareTo( db.getX2() ) < 0 && y.compareTo( db.getY1() ) > 0 && y.compareTo( db.getY2() ) < 0 ){
//				if( ( x > db.getX1() && x < db.getX2() ) && ( y > db.getY1() && y < db.getY2() ) ){

					//Ha az elobbi X poziciot hasznalom, akkor kivul kerulok
					if( ( secondaryCursor.getX().compareTo( db.getX1() ) <= 0 || secondaryCursor.getX().compareTo( db.getX2() ) >= 0 ) && ( y.compareTo( db.getY1() ) > 0 && y.compareTo( db.getY2() ) < 0 ) ){
//					if( ( secondaryCursor.getX() <= db.getX1() || secondaryCursor.getX() >= db.getX2() ) && ( y > db.getY1() && y < db.getY2() ) ){
						x = secondaryCursor.getX();
					
					//Ha az elobbi Y poziciot hasznalom, akkor kivul kerulok
					}else if( ( x.compareTo( db.getX1() ) > 0 && x.compareTo( db.getX2() ) < 0 ) && ( secondaryCursor.getY().compareTo( db.getY1() ) <= 0 || secondaryCursor.getY().compareTo( db.getY2() ) >= 0 ) ){
//					}else if( ( x > db.getX1() && x < db.getX2() ) && ( secondaryCursor.getY() <= db.getY1() || secondaryCursor.getY() >= db.getY2() ) ){
						
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
		}else if( canvas.isEnabledDrawn() ){
			
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
			
			
			
//			if( x.compareTo( secondaryStartCursorPosition.getX() ) <= 0 ){
//				tmpX1 = x;
//				tmpX2 = secondaryStartCursorPosition.getX();
//			}else{					
//				tmpX1 = secondaryStartCursorPosition.getX();
//				tmpX2 = x;
//			}
//			
//			if( y <= secondaryStartCursorPosition.getY() ){
//				tmpY1 = y;
//				tmpY2 = secondaryStartCursorPosition.getY();
//			}else{					
//				tmpY1 = secondaryStartCursorPosition.getY();
//				tmpY2 = y;
//			}

			
			// Vegig a lehelyezett DrawnBlock-okon
			@SuppressWarnings("unchecked")
			Iterator<DrawnBlock> it = (Iterator<DrawnBlock>) canvas.iterator();
			DrawnBlock db;
			while( it.hasNext() ){
				db = it.next();
				
				//Ha a most szerkesztett DrawnBlock fedesbe kerulne egy mar lehelyezett DrawnBlock-kal
				Block block = new Block(tmpX1, tmpY1);
				block.changeSize( tmpX2, tmpY2 );
				
				if( db.intersectsOrContains( block )){
//				if( db.intersectsOrContains( new Rectangle.Double( tmpX1, tmpY1, tmpX2-tmpX1, tmpY2-tmpY1 ) )){
				
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
		for( CursorPositionChangeListener listener : canvas.getSecondaryCursorPositionChangeListenerList() ) {
			listener.getWorldPosition( x.doubleValue(), y.doubleValue() );
		}		
		
		//A Masodlagos kurzor poziciojanak beallitasa
		secondaryCursor.setPosition( x, y );
	}
	
	
	
	
	
	public boolean isDrawnStarted(){
		return drawnStarted;
	}
	
	
	
	
	
	/**
	 * Egy DrawnBlock rajzolasat elvegzo factory megadasa
	 * @param drawnBlockFactory
	 */
	public void setDrawnBlockFactory( DrawnBlockFactory drawnBlockFactory ){
		this.drawnBlockFactory = drawnBlockFactory;
	}
	
	public DrawnBlockFactory getDrawnBlockFactory(){
		return this.drawnBlockFactory;
	}
		
	public DrawnBlockCanvas getCanvas(){
		return canvas;
	}
	
	/**
	 * Megjeleniti a kurzort es a szerkesztes alatt allo DrawnBlock-ot
	 * az atmeneti retegben
	 * 
	 */
	public void repaintSecondaryCursorAndDrawnBlockToDraw(){
		
		if( null != drawnBlockToDraw ){
			canvas.addTemporaryDrawnBlock( drawnBlockToDraw );
		}
		
		SecondaryCursor secondaryCursor = canvas.getSecondaryCursor();
		
		if( null != secondaryCursor ){
			canvas.addTemporarySecondaryCursor( secondaryCursor );
		}
		
		canvas.repaintCoreCanvas();
		
	}
}
