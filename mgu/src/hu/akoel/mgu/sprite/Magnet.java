package hu.akoel.mgu.sprite;

import java.util.ArrayList;
import java.util.HashSet;

import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.RangeValueInPixel;

public class Magnet {

	private Sprite parent;
	private PositionValue ralativePositionToSpriteZeroPoint;
	private RangeValueInPixel rangeInPixel;
	private double direction;
	private MagnetType type;	
	private HashSet<MagnetType> possibleMagnetTypeToConnect = new HashSet<>();
	private ArrayList<SpriteElement> elements = new ArrayList<SpriteElement>();
	private Magnet magnetToConnected;
		
	public Magnet( Sprite parent, MagnetType type, double direction, RangeValueInPixel rangeInPixel, PositionValue relativePositionToSpriteZero ){
		this.parent = parent;
		this.type = type;
		this.direction = direction;
		this.rangeInPixel = rangeInPixel;
		this.ralativePositionToSpriteZeroPoint = relativePositionToSpriteZero;
	}
	
	Sprite getParent(){
		return parent;
	}
	
	public HashSet<MagnetType> getPossibleMagnetTypeToConnect(){
		return possibleMagnetTypeToConnect;
	}
	
	public PositionValue getRelativePositionToSpriteZeroPoint() {
		return new PositionValue(ralativePositionToSpriteZeroPoint.getX(), ralativePositionToSpriteZeroPoint.getY());
	}

	public void setRelativePositionToSpriteZeroPoint(PositionValue realtivePositionToSpriteZeroPoint) {
		this.ralativePositionToSpriteZeroPoint.setX(realtivePositionToSpriteZeroPoint.getX());
		this.ralativePositionToSpriteZeroPoint.setY(realtivePositionToSpriteZeroPoint.getY());
	}
	
	/**
	 * A parameterkent megadott MagnetType-okhoz kapcsolodhat ez a Magnet
	 * 
	 * @param possibleConnectionTo
	 */
	public void addPossibleMagnetTypeToConnect( MagnetType possibleConnectionTo ){
		this.possibleMagnetTypeToConnect.add(possibleConnectionTo);
	}
	
	public PositionValue getPosition(){
		return new PositionValue(
				parent.getPosition().getX() + getRelativePositionToSpriteZeroPoint().getX(),
				parent.getPosition().getY() + getRelativePositionToSpriteZeroPoint().getY()
		);
	}
	
	/**
	 * A Magnet grafikai megjelenitesenek egyik eleme
	 * 
	 * @param element
	 */
	public void addElement( SpriteElement element ){
		elements.add(element);
	}
	
	/**
	 * Visszaadja a Magnet Sprite-janak lehetseges poziciojat ha a parameterkent megadott
	 * Magnes-hez kapcsolodna
	 * 
	 * @param magnetToConnect
	 * @return
	 */
	public PositionValue getPossibleSpritePosition( Magnet magnetToConnect ){

		//A bazis Sprite pozicioja - Ellenoldal
		PositionValue baseSpritePostion = magnetToConnect.parent.getPosition();
		
		//Kozos magnes pozicioja
		PositionValue magnetPosition = new PositionValue(
				baseSpritePostion.getX() + magnetToConnect.getRelativePositionToSpriteZeroPoint().getX(), 
				baseSpritePostion.getY() + magnetToConnect.getRelativePositionToSpriteZeroPoint().getY() 
		);
		
		//Ennek a magnesnek a szulo-Sprite pozicioja
		PositionValue parentSpritePosition = new PositionValue( 
				magnetPosition.getX() - this.getRelativePositionToSpriteZeroPoint().getX(), 
				magnetPosition.getY() - this.getRelativePositionToSpriteZeroPoint().getY() 
		);

		return parentSpritePosition;
	}
	
	public PositionValue getPossibleSpritePosition( Magnet magnetBase, Magnet magnetToConnect ){

		//A bazis Sprite pozicioja - Ellenoldal
		PositionValue baseSpritePostion = magnetToConnect.parent.getPosition();
		
		//Kozos magnes pozicioja
		PositionValue magnetPosition = new PositionValue(
				baseSpritePostion.getX() + magnetToConnect.getRelativePositionToSpriteZeroPoint().getX(), 
				baseSpritePostion.getY() + magnetToConnect.getRelativePositionToSpriteZeroPoint().getY() 
		);
		
		//Ennek a magnesnek a szulo-Sprite pozicioja
		PositionValue parentSpritePosition = new PositionValue( 
				magnetPosition.getX() - magnetBase.getRelativePositionToSpriteZeroPoint().getX(), 
				magnetPosition.getY() - magnetBase.getRelativePositionToSpriteZeroPoint().getY() 
		);

		return parentSpritePosition;
	}
	
	/**
	 * Rekurziv Blokk Repozicion egy mar kapcsolt Magnet alapjan
	 * A magnetToBeReposition Magnet Sprite-janak atpozicionalasa ahhoz a Sprite-hoz, melynek Magnet-jehez van kotve
	 * Mivel Rekurzivan hajtja vegre a feladatot, ezert ez minden olyan Sprite-tal elvegzi, mely a parameterkent
	 * megadott magnetToBeReposition Magnet Sprit-jahoz kapcsolodik es szerepel a listToReposition listaban
	 * 
	 * @param magnetToBeReposition
	 * @param repositionedList
	 * @param listToReposition
	 * @return
	 */
	HashSet<Sprite> rRepositionBlockByConnectedMagnet( Magnet magnetToBeReposition, HashSet<Sprite> listToReposition, HashSet<Sprite> repositionedList ){
		PositionValue parentSpritePosition;
		
		Sprite spriteToBeReposition = magnetToBeReposition.getParent();		
		Magnet magnetBase = magnetToBeReposition.getConnectedTo(); 
		
		//Ha az ujrapozicionalando Magnet-nek a Sprite-ja mar ujrapozicionalt vagy nem talalhato a Sprite listaban, melyek atpozicionalasat el kell vegezni
		if( repositionedList.contains( spriteToBeReposition ) ){
			
			//Akkor zarom a ciklust
			return repositionedList;
		}
		
		//Ha az alap Magnet csatlakozik egy Sprite Magnet-jehez
//		if( null != connectedTo ){
			
			//Megszerzem hogy az ujrapozicionalando Magnet Sprite-janak mi lehetne a pozicioja
			parentSpritePosition = getPossibleSpritePosition(magnetToBeReposition, magnetBase);
			
			//Az ujrappzicionalando Sprite poziciojanak igazitasa ahhoz a Sprite-hoz amihez csatlakozik
			spriteToBeReposition.setPosition(parentSpritePosition);
			
			repositionedList.add( spriteToBeReposition );
			
//		}
		
		//Es most hogy atpozicionaltuk a magnetBase Magnet-jenek Sprite-jat,
		//Megkeressuk a magnetBase Sprite-janak tobbi Magnet-jet
		for( Magnet otherMagnet: spriteToBeReposition.getMagnetList() ){
			
			//Ha a sorra vett Magnet nem az alapul szolgalo magnetBase
			if( !otherMagnet.equals(magnetToBeReposition) ){
				
				Magnet otherMagnetToConnect = otherMagnet.getConnectedTo();
				
				//Ha a sorra vett Magnet csatlakoztatva van egy masik magnet-hez
				if( null != otherMagnetToConnect){
					
					rRepositionBlockByConnectedMagnet( otherMagnetToConnect, listToReposition, repositionedList );
					
				}
				
				
			}
		}
		
		return repositionedList;
	
	}
	
	
	/**
	 * Ehhez a Magnet-hez probalja kapcsolni a parameterkent megadott Magnet-et, ha ez lehetseges.
	 * Ezek utan a Magnet szulo Sprite-jat a helyere mozgatja, majd ezek utan
	 * minden a parameterkent megadott listaban szereplo Sprite-ot is a megfelelo helyere is pozicional  
	 * 
	 * @param magnetToConnect
	 * @param listToArrange
	 */
	public void setConnectedTo( Magnet magnetToConnect ){
		
		//Ha meg akarom szuntentni a magnes kapcsolatat
		if( null == magnetToConnect ){
			
			//Ha volt kapcsolata ennek a magnesnek
			if( null != this.magnetToConnected ){
				
				//akkor a parjanak a kapcsolatat, ami feltehetoleg ez a magnes, is torli
				this.magnetToConnected.magnetToConnected = null;
			
				//es torli ennek a magnesnek a kapcsolatat
				this.magnetToConnected = magnetToConnect;
			}
			
		//Ha ezt a Magnet-et csatlakoztathatom a masikhoz es a masikat is csatlakoztathatom ehhez
		}else if( 
				this.getPossibleMagnetTypeToConnect().contains(magnetToConnect.getType()) &&
				magnetToConnect.getPossibleMagnetTypeToConnect().contains(this.getType() )
		){
			//A mozgatott Sprite Magnet-jenek kotese a bazis Sprite Magnet-jehez
			this.magnetToConnected = magnetToConnect;
			
			//A bazis Sprite Magnet-jenek kotese a Mozgatott Sprite Magnet-jehz
			magnetToConnect.magnetToConnected = this;
	
			PositionValue parentSpritePosition = getPossibleSpritePosition(magnetToConnect);
			
			//Mozgatott Sprite poziciojanak igazitasa az allo Sprite-hoz
			parent.setPosition(parentSpritePosition);
		}
	}
	
	public Magnet getConnectedTo(){
		return magnetToConnected;
	}
	
	public MagnetType getType(){
		return type;
	}
	
	public double getDirection(){
		return direction;
	}
	
	public RangeValueInPixel getRangeInPixel(){
		return rangeInPixel;
	}
	
	
	public void draw( MGraphics g2){
		for( SpriteElement element: elements){
			element.setPositionX(parent.getPosition().getX() + getRelativePositionToSpriteZeroPoint().getX() );
			element.setPositionY(parent.getPosition().getY() + getRelativePositionToSpriteZeroPoint().getY() );
			element.draw(g2);
		}
	}
	
	public void drawConnected( MGraphics g2){
		for( SpriteElement element: elements){
			element.setPositionX(parent.getPosition().getX() + getRelativePositionToSpriteZeroPoint().getX() );
			element.setPositionY(parent.getPosition().getY() + getRelativePositionToSpriteZeroPoint().getY() );
			element.drawConnected(g2);
		}
	}
	
	public void drawFocus( MGraphics g2){
		for( SpriteElement element: elements){
			element.setPositionX(parent.getPosition().getX() + getRelativePositionToSpriteZeroPoint().getX() );
			element.setPositionY(parent.getPosition().getY() + getRelativePositionToSpriteZeroPoint().getY() );
			element.drawFocus(g2);
		}
	}
	
	
	public String toString(){
		return "Type: " + getType() + " Direction: " + getDirection() + " Range in pixel: " + rangeInPixel;
	}
}
