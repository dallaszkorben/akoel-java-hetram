package hu.akoel.mgu.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;

import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.SizeValue;
import hu.akoel.mgu.values.TranslateValue;

public class SpriteCanvas extends MCanvas{

	private static final long serialVersionUID = -4187978793520279190L;
	
	private ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
	private ArrayList<Sprite> temporarySpriteList = new ArrayList<Sprite>();
	
	private SpriteInFocusListener spriteInFocusListener = new SpriteInFocusListener();
	private SpriteDragListener spriteDragListener = new SpriteDragListener();
	
	private boolean needFocus = true;

	public SpriteCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle );		
		commonConstructor();
	}
	
	public SpriteCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle, SizeValue boundSize ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle, boundSize );
		commonConstructor();
	}

	private void commonConstructor(){
		
		//Azt figyeli, hogy egy Sprite fokuszba kerult-e
		this.addMouseMotionListener(spriteInFocusListener);
		
		//A kozepso reteget hasznaljuk a sprite-ok megjelenitesere
		addPainterListenerToMiddle(new SpritePainterListener(), Level.ABOVE );
		
		//Egy SPrite mozgatasat figyeli
		this.addMouseInputListener( spriteDragListener );
		
	}
	
	
	
	
/*	
	public void fireMouseDragged(){		
		int x = MouseInfo.getPointerInfo().getLocation().x-getCoreCanvasLocationOnScreen().x;
		int y = MouseInfo.getPointerInfo().getLocation().y-getCoreCanvasLocationOnScreen().y;		
		MouseEvent me = new MouseEvent(coreCanvas, 11, 0, 0, x, y, 1, false);		
		spriteDragListener.mouseDragged(me);	
	}
*/	
	
	
	
	
	public void setNeedFocus( boolean needFocus){
		this.needFocus = needFocus;
	}
	
	public boolean needFocus(){
		return needFocus;
	}
	
	public void addSprite( Sprite sprite ){

		if( !this.spriteList.contains( sprite ) ){
			this.spriteList.add( sprite );
		}		
	}
	
	public void addSprites( HashSet<Sprite> spriteList ){
		this.spriteList.addAll( spriteList );
	}
	
	public void removeSprite( Sprite sprite ){
		this.spriteList.remove( sprite );
	}
	
	public void removeSprites( HashSet<Sprite> spriteList ){
		this.spriteList.removeAll( spriteList );
	}
	
	public void addTemporarySprite( Sprite sprite ){
		
		//Temporary reteget hasznaljuk a fokus megjelenitesre
		//Minden megjelenites utan torlodik a listener, ezert kell mindig hozzaadni
		addPainterListenerToTemporary(new TemporarySpritePainterListener(), Level.UNDER);	
		
		if( !temporarySpriteList.contains(sprite)){
			temporarySpriteList.add(sprite);
		}	
	}
	
	public void addTemporarySprites( HashSet<Sprite> spriteList ){
		
		//Temporary reteget hasznaljuk a fokus megjelenitesre
		//Minden megjelenites utan torlodik a listener, ezert kell mindig hozzaadni
		addPainterListenerToTemporary(new TemporarySpritePainterListener(), Level.UNDER);	

		temporarySpriteList.addAll( spriteList );
	}
	
	public void zoomIn(double xCenter, double yCenter, int xPoint, int yPoint){
		super.zoomIn(xCenter, yCenter, xPoint, yPoint);
		
		fireMouseMoved();
		spriteDragListener.loadSpriteToTemporary();
		repaintCoreCanvas();
	}
	
	public void zoomOut(double xCenter, double yCenter, int xPoint, int yPoint){
		super.zoomOut(xCenter, yCenter, xPoint, yPoint);
		
		//Azert kell, hogy a zoom utan kovetkezo ujrarajzolas miatt eltuno fokuszban levo Sprite ujra bekeruljon a temporary listaba

		fireMouseMoved();
		spriteDragListener.loadSpriteToTemporary();
		repaintCoreCanvas();
	}
	
	
	/**
	 * 
	 * Sprite mozgatasaert felelos osztaly
	 * 
	 * @author akoel
	 *
	 */
	class SpriteDragListener implements MouseInputListener{
		private PositionValue originalPosition;
		private DeltaValue initialDelta;

		private boolean dragOneSpriteStarted = false;
		private boolean dragAllSpriteStarted = false;
		private Sprite sprite;
		private HashSet<Sprite> moveableSpriteList;
		
		public void loadSpriteToTemporary(){
			if( dragOneSpriteStarted ){
				addTemporarySprite(sprite);	
			}
		}
		

		
		@Override
		public void mousePressed(MouseEvent e) {
			
			//Mar egy elindult Drag-Replace folyamatot nem indithatok ujra el
			if( dragOneSpriteStarted || dragAllSpriteStarted ){
				return;
			}
			
			//
			//Ha a bal-eger gombot nyomtam le a Shift-tel - A teljes kapcsolati rendszert akarom athelyezni
			//
			if( e.getButton() == MouseEvent.BUTTON1 && e.isShiftDown() ){
				
				double xValue = getWorldXByPixel(e.getX() );
				double yValue = getWorldYByPixel(e.getY());
				
				//Akkor meg kell nezni, hogy van-e alatta sprite
				for( Sprite sprite: spriteList){
					
					SizeValue boundBox = sprite.getBoundBoxAbsolute();
						
					//Igen, van alatta sprite
					if( 
							xValue >= boundBox.getXMin() &&
							xValue <= boundBox.getXMax() &&
							yValue >= boundBox.getYMin() &&
							yValue <= boundBox.getYMax()
					){
									
						//Rogton letiltom a fokusz mukodeset TODO erre azt hiszem nincs szukseg
						setNeedFocus(false);						
						
						//Elteszem a mozgatando sprite-ot, hogy a drag muvelet tudja mivel dolgozzon
						this.sprite = sprite;
												
						//El kell menteni az eredeti poziciojat
						originalPosition = sprite.getPosition();
					
						//Megallapitani a kulonbseget a Sprite pozicioja es a kurzor kozott
						initialDelta = new DeltaValue( xValue - originalPosition.getX(), yValue - originalPosition.getY() );
						
						moveableSpriteList = new HashSet<Sprite>();
						moveableSpriteList = getConnectedSprites(sprite, moveableSpriteList);					
						dragAllSpriteStarted = true;
					
						//Eltavolitom a permanens listabol
						removeSprites(moveableSpriteList);
						
						//El kell helyezni az atmeneti taroloba az uj pozicioval						
						//sprite.setPosition(new PositionValue(xValue-initialDelta.getX(), yValue-initialDelta.getY()));
//						sprite.setPosition( new PositionValue(originalPosition.getX(), originalPosition.getY()) );
						
						addTemporarySprites(moveableSpriteList);	
						
						revalidateAndRepaintCoreCanvas();
						
						break;

					}					
				}	
			//
			//Ha a bal-eger gombot nyomtam le - At akarom helyezni a Sprite-ot
			//
			}else if( e.getButton() == MouseEvent.BUTTON1 ){
				
				double xValue = getWorldXByPixel(e.getX() );
				double yValue = getWorldYByPixel(e.getY());
				
				//Akkor meg kell nezni, hogy van-e alatta sprite
				for( Sprite sprite: spriteList){
					
					SizeValue boundBox = sprite.getBoundBoxAbsolute();
						
					//Igen, van alatta sprite
					if( 
							xValue >= boundBox.getXMin() &&
							xValue <= boundBox.getXMax() &&
							yValue >= boundBox.getYMin() &&
							yValue <= boundBox.getYMax()
					){
									
						//Rogton letiltom a fokusz mukodeset TODO erre azt hiszem nincs szukseg
						setNeedFocus(false);						
						
						//Elteszem a mozgatando sprite-ot, hogy a drag muvelet tudja mivel dolgozzon
						this.sprite = sprite;
												
						//El kell menteni az eredeti poziciojat
						originalPosition = sprite.getPosition();
					
						//Megallapitani a kulonbseget a Sprite pozicioja es a kurzor kozott
						initialDelta = new DeltaValue( xValue - originalPosition.getX(), yValue - originalPosition.getY() );
						
						//Eltavolitom a permanens listabol
						removeSprite(sprite);
						
						//El kell helyezni az atmeneti taroloba az uj pozicioval						
						//sprite.setPosition(new PositionValue(xValue-initialDelta.getX(), yValue-initialDelta.getY()));
						sprite.setPosition( new PositionValue(originalPosition.getX(), originalPosition.getY()) );

						
						addTemporarySprite(sprite);	
						
						revalidateAndRepaintCoreCanvas();
						
						dragOneSpriteStarted = true;
						
						break;

					}					
				}				
			}
			
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {	

			if( dragAllSpriteStarted ){
				
				//visszahelyezni a Sprite-okat a vegleges taroloba
				addSprites( moveableSpriteList );
				
				//Engedelyezem a fokusz mukodeset
				setNeedFocus(true);
				
				//Es ujra kell rajzoltatni a Permanens lista elemeit
				revalidateAndRepaintCoreCanvas();
			
				//Es mivel fokuszbanlehet meg tovabbra is, ezert egy eger-mozgast is szimulalni kell
				fireMouseMoved();
				
				//Es jelzem, hogy vege a dragg-nek. MINDENKEPPEN
				dragAllSpriteStarted = false;
				
			}else if( dragOneSpriteStarted ){
						
				//Ha nem csatlakozik mas elemhez de nincs engedelyezve kapcsolat nelkuli lehelyezesre
				if( !sprite.isConnected() && !sprite.isEnableToPlaceWithoutConnection() ){

					//Akkor vissza kell helyezni az elozo pozicioba
					//Visszairom az eredeti poziciot
//					//sprite.setPosition( originalPosition );
					
					//General egy Drag muveletet vissza az eredeti pozicioba
					MouseEvent me = new MouseEvent(coreCanvas, 11, 0, 0, Math.round((float)getMouseXPositionByWorld(originalPosition.getX() + initialDelta.getX())), Math.round((float)getMouseYPositionByWorld(originalPosition.getY() + initialDelta.getY())), 1, false);									
					spriteDragListener.mouseDragged(me);					
									
				}
					
				//El kell helyezni a vegleges taroloba az uj pozicioval			
				addSprite(sprite);
			
				//Engedelyezem a fokusz mukodeset
				setNeedFocus(true);
				
				//Es ujra kell rajzoltatni a Permanens lista elemeit
				revalidateAndRepaintCoreCanvas();
			
				//Es mivel fokuszbanlehet meg tovabbra is, ezert egy eger-mozgast is szimulalni kell
				fireMouseMoved();
				
				//Es jelzem, hogy vege a dragg-nek. MINDENKEPPEN
				dragOneSpriteStarted = false;
			}
			
			
			
		}
		
		@Override
		public void mouseExited(MouseEvent e) {	
						
			//Ha elindult mar a drag es igy hagyom el a Canvas-t
			if( dragOneSpriteStarted || dragAllSpriteStarted ){
				
				//mouseReleased( e );
				
			//Csak siman kisetalt a kurzor a kepernyorol, akkor minden Sprite-rol toroljuk a fokuszt
			}else{
				
				boolean needToReprint = false;
				
				//Vegig az osszes Sprite-on
				for( Sprite sprite: spriteList){
					
					//Ha az adot Sprite fokuszban volt
					if( sprite.isInFocus() ){
						
						//Kiszedjuk a fokuszbol
						sprite.setInFocus(false);
						
						//Es jelzem, hogy legalabb egy elem volt fokuszban
						needToReprint = true;						
							
					}										
				}
				
				if( needToReprint ){			
				
					//Azert kell, hogy az esetlegesen fokuszban levo Sprite-rol eltunjon a fokusz
					repaintCoreCanvas();

				}			
			}						
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			
			//
			//Sprite csoport mozgatasa
			//
			if( dragAllSpriteStarted ){

				//Uj pozicioi kiszamitasa
				double xCursorPosition = getWorldXByPixel(e.getX() );
				double yCursorPosition = getWorldYByPixel(e.getY());

				double xNewSpritePosition = xCursorPosition - initialDelta.getX();
				double yNewSpritePosition = yCursorPosition - initialDelta.getY();
				
				double xDelta = xNewSpritePosition - sprite.getPosition().getX();
				double yDelta = yNewSpritePosition - sprite.getPosition().getY();

				//A mozgatando Sprite Elozetes uj pozicioba helyezese
//				sprite.setPosition(new PositionValue(xCursorPosition-initialDelta.getX(), yCursorPosition-initialDelta.getY()));
/*				Magnet draggedMagnet = null;
				for( Magnet magnet: sprite.getMagnetList() ){
					if( null != magnet.getConnectedTo() ){
						draggedMagnet = magnet;
						break;
					}
				}
*/								
//				draggedMagnet.blabla( draggedMagnet, new HashSet<Sprite>(), moveableSpriteList );				
	
				
				//Az osszes Sprite-ot a mozgatando listabol a megfelelo helyre pozicionalok
				for( Sprite sprite: moveableSpriteList ){
				
					//A mozgatando Sprite Elozetes uj pozicioba helyezese
					sprite.setPosition(new PositionValue(
							sprite.getPosition().getX() + xDelta, 
							sprite.getPosition().getY() + yDelta
							)
					);
				}
			
				//Torlom a Sprite csoport minden kapcsolatat ami nem a csoporton belul kottetett
				//Tulajdonkeppen a lehetseges kapcsolatokrol van szo
				for( Sprite sprite: moveableSpriteList ){
					for( Magnet magnet: sprite.getMagnetList() ){
						Magnet pairMagnet = magnet.getConnectedTo();
						
						//Kivulre mutato kapcsolat
						if( null != pairMagnet && !moveableSpriteList.contains( pairMagnet.getParent() ) ){
							
							//Torolni kell a kapcsolatot
							magnet.setConnectedTo(null);
						}
					}
				}
				
				//Poziciok szukseg szerinti korrigalasa a magnes alapjan
				doArangeBlockPositionByMagnet(moveableSpriteList);
				
				//A mozgatando Sprite uj pozicioval elhelyezese az atmeneti taroloban
				addTemporarySprites(moveableSpriteList);
				
				//A Permanens es atmeneti taroloban levo Sprite-ok ujrarajzolasa szukseges
				revalidateAndRepaintCoreCanvas();
				
			/**
			 *  Egy Sprite mozgatas 
			 * 
			 *  Azert teszunk kulonbseget az egy illetve csoportos Sprite mozgatasa kozott, mert az egy Sprite mozgatasakor meg kell vizsgalni
			 *  hogy lehetseges-e az illeto Sprite kapcsolatainak megszuntetese anelkul, hogy serulne-e az onalloan lehelyezheto/nem lehelyezheto Sprite szabaly
			 *  Vagyis, ha pld a kapcsolati rendszerben letezik egy db onalloan lehelyezheto Sprite es a tobbi hozzakapcsolodo Sprite az csak kapcsolatban
			 *  helyezheto le, akkor ha egy kozbenso Sprite-ot kivanunk elmozgatni akkor a ket reszre szakitott rendszer egyik fele ugy letezne tovabb, hogy \
			 *  nincs onalloan lehelyezheto eleme, vagyis ellentmondas alakulna ki 
			 */
			}else if( dragOneSpriteStarted ){
				
				//
				//Azt vizsgalja, hogy az adott Sprite elmozdithato-e egyaltalan a helyerol
				//
				boolean moveable = true;
				int number;
				HashSet<Sprite> checkedSpriteList;
				
				//vegig a Sprite minden magnesen
				for( Magnet magnet: sprite.getMagnetList() ){
					
					//Hogyha van kapcsolata az aktualis magnet-nek
					if( null != magnet.getConnectedTo() ){
						number = 0;
						checkedSpriteList = new HashSet<Sprite>();
						checkedSpriteList.add(sprite);
						
						//Akkor megnezi, hogy hagy db onalloan elhelyezheto Sprite letezik Sprite adott magnes feloli oldalan
						number = getNumbersOfEnableToPlaceWithoutConnection(magnet, number, checkedSpriteList );
						
						//Ha a kerdeses Sprite-on kivul nem letezik onalloan lehelyezheto Sprite a magnes feloli oldalon
						if( number == 0 ){
							
							//Akkor megtiltja az mozgatni kivant Sprite elmozditasat
							moveable = false;
							break;
						}
					}
				}
				if( !moveable ){
					return;
				}
				
				
				//Uj pozicioi kiszamitasa
				double xCursorPosition = getWorldXByPixel(e.getX() );
				double yCursorPosition = getWorldYByPixel(e.getY());
				
				//A mozgatando Sprite Elozetes uj pozicioba helyezese
				sprite.setPosition(new PositionValue(xCursorPosition-initialDelta.getX(), yCursorPosition-initialDelta.getY()));
				
				boolean needToRepaintPermanent = false;
				

				//Eloszor is torlom a mozgatot Sprite minden kapcsolatat					
				if( sprite.isConnected() ){

					//Az osszes magnesen vegig megyek
					for( Magnet magnet: sprite.getMagnetList()){
						
						//Es megszuntetem a kapcsolatait
						magnet.setConnectedTo(null);
					}
						
					//Es mivel egesz biztos, hogy volt kapcsolata amit torloltem (sprite.isConnected()), jelzem, hogy ujra kell rajzolni a permanens listat
					needToRepaintPermanent = true;
				
				}
							
				//Pozicio szukseg szerinti korrigalasa a magnes alapjan
				needToRepaintPermanent = doArangeSpritePositionByMagnet( sprite ) || needToRepaintPermanent;
	
				//A mozgatando Sprite uj pozicioval elhelyezese az atmeneti taroloban
				addTemporarySprite(sprite);
				
				//A Permanens es atmeneti taroloban levo Sprite-ok ujrarajzolasa szukseges
				if( needToRepaintPermanent ){
					revalidateAndRepaintCoreCanvas();
				}else{
					//Csak az atmeneti tarolo ujrarajzolasa az uj pozicioban levo Sprite miatt
					repaintCoreCanvas();
				}
			}			
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {}
		
		@Override
		public void mouseEntered(MouseEvent e) {}
		
		@Override
		public void mouseClicked(MouseEvent e) {}
		
	}
	
	/**
	 * Mozgatott Sprite-csoport Poziciojanak korrigalasa a Magnesek alapjan
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean doArangeBlockPositionByMagnet( HashSet<Sprite> moveableSpriteList ){
		
		boolean needToRepaintPermanent = false;
		boolean hasBeenFoundPairForTheBlock = false;
		
		//Vegig megyek az osszes mozgatott Sprite-on
		for( Sprite sprite: moveableSpriteList ){		
		
			//Vegig megyek a Sprite minden magnesen
			for( Magnet draggedMagnet: sprite.getMagnetList() 	){
			
				//Ha a Sprite Magnet-je foglalt, akkor azt mar nem vizsgalom tovabb
				if( null != draggedMagnet.getConnectedTo() ){
					continue;
				}
				
				//Az aktualis magnes pozicioja
				double magnetXRange = getWorldXLengthByPixel( draggedMagnet.getRangeInPixel().getX() );
				double magnetYRange = getWorldYLengthByPixel( draggedMagnet.getRangeInPixel().getY() );
			
				//Az aktualis Magnet hatotavolsaga
				double xMagnetPosition = sprite.getPosition().getX() + draggedMagnet.getRelativePositionToSpriteZeroPoint().getX();
				double yMagnetPosition = sprite.getPosition().getY() + draggedMagnet.getRelativePositionToSpriteZeroPoint().getY();
			
				//boolean hasMagnetConnection = false;
			
				//Vegig az osszes permanens Sprite-on
				for( Sprite possibleToConnectSprite: spriteList){
				
					SizeValue boundBox = possibleToConnectSprite.getBoundBoxAbsolute();
					
					//Ha Van a vonzaskorzetben egyaltalan Sprite
					if( 
						( 
						(xMagnetPosition - magnetXRange >= boundBox.getXMin() &&
						xMagnetPosition - magnetXRange <= boundBox.getXMax() ) ||
						( xMagnetPosition + magnetXRange >= boundBox.getXMin() &&
						xMagnetPosition + magnetXRange <= boundBox.getXMax() ) ||
						( boundBox.getXMin()  >= xMagnetPosition - magnetXRange &&
						boundBox.getXMin() <= xMagnetPosition + magnetXRange &&
						boundBox.getXMax() >= xMagnetPosition - magnetXRange &&
						boundBox.getXMax() <= xMagnetPosition + magnetXRange)
						
						)&&
								
						( 
						(yMagnetPosition - magnetYRange >= boundBox.getYMin() &&
						yMagnetPosition - magnetYRange <= boundBox.getYMax() ) ||
						( yMagnetPosition + magnetYRange >= boundBox.getYMin() &&
						yMagnetPosition + magnetYRange <= boundBox.getYMax() ) ||
						( boundBox.getYMin()  >= yMagnetPosition - magnetYRange &&
						boundBox.getYMin() <= yMagnetPosition + magnetYRange &&
						boundBox.getYMax() >= yMagnetPosition - magnetYRange &&
						boundBox.getYMax() <= yMagnetPosition + magnetYRange)
						)
					){
					
						//Akkor vegig megyek a megtalalt Sprite Magnet-jeint
						for( Magnet possibleToConnectMagnet : possibleToConnectSprite.getMagnetList() ){
						
							MagnetType possibleToConnectType = possibleToConnectMagnet.getType();
							double possibleToConnectXPosition = possibleToConnectSprite.getPosition().getX() + possibleToConnectMagnet.getRelativePositionToSpriteZeroPoint().getX();
							double possibleToConnectYPosition = possibleToConnectSprite.getPosition().getY() + possibleToConnectMagnet.getRelativePositionToSpriteZeroPoint().getY();
						
							//Es megnezem, hogy a ket magnet kompatibilis-e es megfelelo pozicioban van-e valamint nincs-e racsatlakoztatva mas
							if( 
								draggedMagnet.getPossibleMagnetTypeToConnect().contains( possibleToConnectType ) &&
								possibleToConnectMagnet.getPossibleMagnetTypeToConnect().contains( draggedMagnet.getType() ) &&
								( Math.abs( draggedMagnet.getDirection() - possibleToConnectMagnet.getDirection() ) == 180.0 ) &&
								possibleToConnectXPosition >= xMagnetPosition - magnetXRange &&
								possibleToConnectXPosition <= xMagnetPosition + magnetXRange &&
								possibleToConnectYPosition >= yMagnetPosition - magnetYRange &&
								possibleToConnectYPosition <= yMagnetPosition + magnetYRange &&
								( null == possibleToConnectMagnet.getConnectedTo() || possibleToConnectMagnet.getConnectedTo().equals(draggedMagnet) )
															
							){
							
								//Ha mar talaltam part a Blokk-nak, vagyis nem ez az elso 
								if( hasBeenFoundPairForTheBlock ){
								
									//Akkor meg kell nezni, hogy mozgatas nelkul osszekapcsolhato-e a ketto
									PositionValue possibleNewPosition = draggedMagnet.getPossibleSpritePosition(possibleToConnectMagnet);
								
									//Ha nem kapcsolhato ossze, akkor zarja a ciklust
									if( !possibleNewPosition.equals(sprite.getPosition())){

										//zarja a ciklust
										continue;										
									}

								}
								
								//A mozgatott Sprite vizsgalt magneset osszekoti az osszekapcsolhato magnessel								
								draggedMagnet.setConnectedTo( possibleToConnectMagnet );
								
								//Ehhez az atmozgatott Sprite-hoz igazitja az osszes tobbi kapcsolt Sprite-ot
								draggedMagnet.rRepositionBlockByConnectedMagnet( draggedMagnet, moveableSpriteList, new HashSet<Sprite>() );							
								
								hasBeenFoundPairForTheBlock = true;
								needToRepaintPermanent = true;
//								hasMagnetConnection = true;
								break;
							
							}								
						}
					}
				
					//if( hasMagnetConnection ){
					//	break;
					//}
				}
			}
			
			//if( hasConnection){
			//	break;
			//}					
							
		}
		
		return needToRepaintPermanent;
	}
	
	
	/**
	 * Mozgatott Sprite Poziciojanak korrigalasa a Magnes alapjan
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean doArangeSpritePositionByMagnet( Sprite sprite ){
		
		boolean needToRepaintPermanent = false;
		boolean hasBeenFoundPairForTheSprite = false;
		
		//Vegig megyek a mozgatott Sprite magnesein
		for( Magnet draggedMagnet: sprite.getMagnetList() 	){
			
			//Az aktualis magnes tulajdonsagai
			double magnetXRange = getWorldXLengthByPixel( draggedMagnet.getRangeInPixel().getX() );
			double magnetYRange = getWorldYLengthByPixel( draggedMagnet.getRangeInPixel().getY() );
			
			//Megnezem, hogy az aktualis magnes hatotavolsagaban, van-e egyaltalan masik sprite
			double xMagnetPosition = sprite.getPosition().getX() + draggedMagnet.getRelativePositionToSpriteZeroPoint().getX();
			double yMagnetPosition = sprite.getPosition().getY() + draggedMagnet.getRelativePositionToSpriteZeroPoint().getY();
			
			boolean hasMagnetConnection = false;
			
			//Vegig az osszes permanens Sprite-on
			for( Sprite possibleToConnectSprite: spriteList){
				
				SizeValue boundBox = possibleToConnectSprite.getBoundBoxAbsolute();
					
				//Ha Van a vonzaskorzetben egyaltalan Sprite
				if( 
						( 
						(xMagnetPosition - magnetXRange >= boundBox.getXMin() &&
						xMagnetPosition - magnetXRange <= boundBox.getXMax() ) ||
						( xMagnetPosition + magnetXRange >= boundBox.getXMin() &&
						xMagnetPosition + magnetXRange <= boundBox.getXMax() ) ||
						( boundBox.getXMin()  >= xMagnetPosition - magnetXRange &&
						boundBox.getXMin() <= xMagnetPosition + magnetXRange &&
						boundBox.getXMax() >= xMagnetPosition - magnetXRange &&
						boundBox.getXMax() <= xMagnetPosition + magnetXRange)
						
						)&&
								
						( 
						(yMagnetPosition - magnetYRange >= boundBox.getYMin() &&
						yMagnetPosition - magnetYRange <= boundBox.getYMax() ) ||
						( yMagnetPosition + magnetYRange >= boundBox.getYMin() &&
						yMagnetPosition + magnetYRange <= boundBox.getYMax() ) ||
						( boundBox.getYMin()  >= yMagnetPosition - magnetYRange &&
						boundBox.getYMin() <= yMagnetPosition + magnetYRange &&
						boundBox.getYMax() >= yMagnetPosition - magnetYRange &&
						boundBox.getYMax() <= yMagnetPosition + magnetYRange)
						)
				){
					
					//Akkor vegig megyek a megtalalt Sprite Magnet-jeint
					for( Magnet possibleToConnectMagnet : possibleToConnectSprite.getMagnetList() ){
						
						MagnetType possibleToConnectType = possibleToConnectMagnet.getType();
						double possibleToConnectXPosition = possibleToConnectSprite.getPosition().getX() + possibleToConnectMagnet.getRelativePositionToSpriteZeroPoint().getX();
						double possibleToConnectYPosition = possibleToConnectSprite.getPosition().getY() + possibleToConnectMagnet.getRelativePositionToSpriteZeroPoint().getY();
						
						//Es megnezem, hogy a ket magnet kompatibilis-e es megfelelo pozicioban van-e valamint nincs-e racsatlakoztatva mas
						if( 
								draggedMagnet.getPossibleMagnetTypeToConnect().contains( possibleToConnectType ) &&
								possibleToConnectMagnet.getPossibleMagnetTypeToConnect().contains( draggedMagnet.getType() ) &&
								( Math.abs( draggedMagnet.getDirection() - possibleToConnectMagnet.getDirection() ) == 180.0 ) &&
								possibleToConnectXPosition >= xMagnetPosition - magnetXRange &&
								possibleToConnectXPosition <= xMagnetPosition + magnetXRange &&
								possibleToConnectYPosition >= yMagnetPosition - magnetYRange &&
								possibleToConnectYPosition <= yMagnetPosition + magnetYRange &&
								( null == possibleToConnectMagnet.getConnectedTo() || possibleToConnectMagnet.getConnectedTo().equals(draggedMagnet) )
								
								
						){
							
							//Ha mar talaltam part a Sprite-nak, vagyis nem ez az elso 
							if( hasBeenFoundPairForTheSprite ){
								
								//Akkor meg kell nezni, hogy mozgatas nelkul osszekapcsolhato-e a ketto
								PositionValue possibleNewPosition = draggedMagnet.getPossibleSpritePosition(possibleToConnectMagnet);
								
								//Ha nem kapcsolhato ossze, akkor zarja a ciklust
								if( !possibleNewPosition.equals(sprite.getPosition())){

									//zarja a ciklust
									continue;										
								}

							}
							
							//A mozgatott Sprite vizsgalt magneset osszekoti az osszekapcsolhato magnessel
							draggedMagnet.setConnectedTo( possibleToConnectMagnet );
						
							hasBeenFoundPairForTheSprite = true;
							needToRepaintPermanent = true;
							hasMagnetConnection = true;
							break;
							
						}								
					}
				}
				
				if( hasMagnetConnection ){
					break;
				}
			}
			
			//if( hasConnection){
			//	break;
			//}					
							
		}
		
		return needToRepaintPermanent;
	}
	
	
	
	
	/**
	 * A prameterkent megadott Sprite-hoz kapcsolt Sprite-ok listajat adja vissza
	 * 
	 * @param spriteList
	 * @return
	 */
	private HashSet<Sprite> getConnectedSprites( Sprite sprite, HashSet<Sprite> spriteList ){
		
		//Ha a Sprite mar volt a listaban, akkor visszater
		if( spriteList.contains(sprite)){
			return spriteList;
		}
		
		//Eloszor is az adott Sprite-ot behelyezi a listaba
		spriteList.add( sprite );
		
		//Vegig a Sprite magnesein 
		for( Magnet m: sprite.getMagnetList()){
			
			Magnet pairMagnet = m.getConnectedTo();
			
			//Ha kapcsolodik a magnese egy masik Sprite-hoz
			if( null != pairMagnet ){
				
				//Akkor megnezi, hogy arrafele mennyi Sprite van
				spriteList = getConnectedSprites( pairMagnet.getParent(), spriteList );
				
			}					
		}
		
		return spriteList;
		
	}
	
	/**
	 * A parameterkent megadott Magnet fele megszamolja a az onalloan lehelyezheto Sprite-ok szamat rekurziv modon
	 * 
	 * @param magnet
	 * @param number
	 * @param spriteList
	 * @return
	 */
	private int getNumbersOfEnableToPlaceWithoutConnection( Magnet magnet, int number, HashSet<Sprite> spriteList ){
		
		Magnet pair = magnet.getConnectedTo();
		
		//Nincs a magnet-nek parja
		if( null == pair ){
			return number;
		}
		
		//A magnes masik oldalan levo Sprite
		Sprite pairParent = pair.getParent();
		
		//Ha mar vizsgaltam ezt a Sprite-to
		if( spriteList.contains(pairParent)){
			return number;
		}
		
		spriteList.add( pairParent);
		
		//Ha lehelyezheto onalloan, akkor noveli a szmalalot
		if( pairParent.isEnableToPlaceWithoutConnection() ){
			number++;
		}
		
		//vegig az o magnesein
		for( Magnet m: pairParent.getMagnetList()){
			
			//Megszamolja, hogy arrafele hany onalloan lehelyezheto Sprite van 
			number = getNumbersOfEnableToPlaceWithoutConnection(m, number, spriteList);			
		}
		
		return number;
		
	}
	
	/**
	 * Azt figyeli, hogy egy Sprite fokuszba kerult-e
	 * @author akoel
	 *
	 */
	class SpriteInFocusListener implements MouseMotionListener{
		
		@Override
		public void mouseMoved(MouseEvent e) {
	
			if( needFocus() ){
			
				double xValue = getWorldXByPixel(e.getX() );			
				double yValue = getWorldYByPixel(e.getY());
				boolean needToPrint = false;

				for( Sprite sprite: spriteList){
				
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
	
	/**
	 * Sprite-ok kirajzolasaert felelos osztaly
	 * 			
	 * @author akoel
	 *
	 */
	class SpritePainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			for( Sprite sprite: spriteList){
				//if( sprite.isConnected() )
				//	sprite.drawConnected(g2);
				//else
					sprite.draw(g2);
			}			
		}
		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
		
	}
	
	/**
	 * Az atmeneti retegben elhelyezett sprite-ok kirajzolasaert felelos
	 * 
	 * @author akoel
	 *
	 */
	class TemporarySpritePainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
			for( Sprite sprite: temporarySpriteList){
				sprite.drawFocus(g2);
			}
			temporarySpriteList.clear();
		}
		

		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
		
	}
}
