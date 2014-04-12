package hu.akoel.mgu;

import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.ZoomRateValue;
import hu.akoel.mgu.values.Value;

import java.util.ArrayList;

public class PossiblePixelPerUnits {
	private PixelPerUnitValue actualPixelPerUnit;
	private PixelPerUnitValue maxPixelPerUnit;
	private PixelPerUnitValue minPixelPerUnit;
	private ZoomRateValue actualRate;
		
	private ArrayList<PixelPerUnitValue> inPPU = new ArrayList<PixelPerUnitValue>();
	private ArrayList<PixelPerUnitValue> outPPU = new ArrayList<PixelPerUnitValue>();
	
	private ArrayList<PixelPerUnitValue> possiblePPUList = new ArrayList<PixelPerUnitValue>();
	private int pointerForPossiblePPUs = -1;
	
	public PossiblePixelPerUnits( ArrayList<PixelPerUnitValue> possiblePPUs, int pointerForPossiblePPUs ){
		this.possiblePPUList = possiblePPUs;
		this.pointerForPossiblePPUs = pointerForPossiblePPUs;
		this.actualPixelPerUnit = possiblePPUs.get(pointerForPossiblePPUs);
		this.actualRate = new ZoomRateValue(1,1);
	}
	
	/**
	 * 
	 * A parameterkent megadott pixelPerUnit ertekre allitja a meretaranyt.
	 * Ennek valtoztatasa a parameterkent megadott rate mertekeben lehet egy lepesben a szinten parameterkent
	 * megadott minimalis es maximalis ertekek kozott
	 * 
	 * 
	 * @param actualPixelPerUnit
	 * @param minPixelPerUnit
	 * @param maxPixelPerUnit
	 * @param rate
	 */
	public PossiblePixelPerUnits( PixelPerUnitValue actualPixelPerUnit, ZoomRateValue rate, PixelPerUnitValue minPixelPerUnit, PixelPerUnitValue maxPixelPerUnit ){
		commonConstructor( actualPixelPerUnit, rate, minPixelPerUnit, maxPixelPerUnit);
	}
	
	/**
	 * 
	 * A parameterkent megadott pixelPerUnit ertekre allitja a meretaranyt.
	 * Ennek valtoztatasa a parameterkent megadott rate mertekeben lehet egy lepesben
	 * 
	 * @param actualPixelPerUnit
	 * @param rate
	 */
	public PossiblePixelPerUnits( PixelPerUnitValue actualPixelPerUnit, ZoomRateValue rate ){
		commonConstructor( actualPixelPerUnit, rate, null, null );

	}
	
	/**
	 * 
	 * A parameterkent megadott meretaranyt allitja be
	 * Mivel nincs rate megadva, ezert nem engedelyezett a nagyitas se a kicsinyites
	 * 
	 * @param actualPixelPerUnit
	 * @param minPixelPerUnit
	 * @param maxPixelPerUnit
	 */
	public PossiblePixelPerUnits( PixelPerUnitValue actualPixelPerUnit){
		commonConstructor( actualPixelPerUnit, null, null, null );		
	}
	
	private void commonConstructor( PixelPerUnitValue actualPixelPerUnit, ZoomRateValue rate, PixelPerUnitValue minPixelPerUnit, PixelPerUnitValue maxPixelPerUnit){
		this.actualPixelPerUnit = actualPixelPerUnit;
		this.maxPixelPerUnit = maxPixelPerUnit;
		this.minPixelPerUnit = minPixelPerUnit;	
		this.actualRate = rate;
	}
	
	public PixelPerUnitValue getActualPixelPerUnit(){
		return actualPixelPerUnit;
	}
	
	public Value getActualRate(){
		return actualRate;
	}
	
	
	/**
	 * Elmenti es visszaadja a kovetkezo lehetsegez pixelPerUnit erteket nagyitas eseten
	 * 
	 * @return
	 */
	public boolean doNextZoomIn(){
		double possibleX;
		double possibleY;
		
		//Elore definialva vannak a lehetseges meretaranyok
		if( possiblePPUList.size() != 0 ){
		
			//Van meg lehetoseg nagyitani a lista alapjan
			if( pointerForPossiblePPUs > 0 ){
				pointerForPossiblePPUs--;
				PixelPerUnitValue newPPU = possiblePPUList.get(pointerForPossiblePPUs);
				actualRate = new ZoomRateValue( newPPU.getX()/actualPixelPerUnit.getX(), newPPU.getY()/actualPixelPerUnit.getY() );
				actualPixelPerUnit = newPPU;
			}else{
				return false;
			}
			
		//Ha nincs meretarany valto
		}else if( null == actualRate ){
			
			//Akkor nem nagyithatok
			return false;
		
		//Nem jottem meg kifele, vagyis vagy az alapon allok, vagy meg csak befele mentem
		}else if( outPPU.isEmpty() ){
			
			possibleX = actualPixelPerUnit.getX() * actualRate.getX();
			possibleY = actualPixelPerUnit.getY() * actualRate.getY();
			
			//Csak ha lehetseges a kert nagyitas, nem korlatozza a maximalis ertek
			if( 					
					(null == maxPixelPerUnit || (
					maxPixelPerUnit.getX() > possibleX &&
					maxPixelPerUnit.getY() > possibleY ) )
			){
			
				//Elmentem az aktualis erteket
				inPPU.add( new PixelPerUnitValue(actualPixelPerUnit.getX(), actualPixelPerUnit.getY() ) );
			
				//Az aktualis ertek lesz a nagyitasi ertek
				actualPixelPerUnit.setX( possibleX );
				actualPixelPerUnit.setY( possibleY );

			//Nem lehetseges a kert nagyitas
			}else{
				return false;
			}
			
		//Ha viszont mar jottem valaha kifele
		}else{

			//Tehat akkor az utolso kifele erteket torlom es visszadnom
			actualPixelPerUnit = outPPU.remove( outPPU.size() - 1 );
			
		}
		
		return true;
	}
	
	/**
	 * Elmenti es visszaadja a kovetkezo lehetseges pixelPerUnit erteket kicsinyites eseten
	 * 
	 * @return
	 */
	public boolean doNextZoomOut(){
		double possibleX;
		double possibleY;
		
		//Elore definialva vannak a lehetseges meretaranyok
		if( possiblePPUList.size() != 0 ){
		
			//Van meg lehetoseg kicsinyiteni a lista alapjan
			if( pointerForPossiblePPUs < possiblePPUList.size() - 1 ){
				pointerForPossiblePPUs++;
				PixelPerUnitValue newPPU = possiblePPUList.get(pointerForPossiblePPUs);
				actualRate = new ZoomRateValue( actualPixelPerUnit.getX()/newPPU.getX(), actualPixelPerUnit.getY()/newPPU.getY() );
				actualPixelPerUnit = newPPU;
			}else{
				return false;
			}
			
		//Ha nincs meretarany valto
		}else if( null == actualRate ){
			
			//Akkor nem kicsinyithetek
			return false;
			
		//Nem mentem meg befele, vagyis vagy az alapon allok, vagy meg csak kifele mentem
		} else if( inPPU.isEmpty() ){

			possibleX = actualPixelPerUnit.getX() / actualRate.getX();
			possibleY = actualPixelPerUnit.getY() / actualRate.getY();
			
			//Csak ha lehetseges a kert kicsinyites, nem korlatozza a minimalis ertek
			if( 
					(null == minPixelPerUnit || ( 
					minPixelPerUnit.getX() < possibleX &&
					minPixelPerUnit.getY() < possibleY ) )					
			){

				//Es jelzem, hogy van befele mozgas
				outPPU.add( new PixelPerUnitValue(actualPixelPerUnit.getX(), actualPixelPerUnit.getY() ) );
			
				//Tehat akkor batran ranagyithatok
				actualPixelPerUnit.setX( possibleX );
				actualPixelPerUnit.setY( possibleY );
			
			//Nem lehetseges a kert kicsinyites
			}else{
				return false;
			}

		//Ha viszont mar mentem valaha befele
		}else{

			//Tehat akkor az utolso kifele erteket torlom es visszadnom
			actualPixelPerUnit = inPPU.remove( inPPU.size() - 1 );	
						
		}
		
		return true;
	}
}
