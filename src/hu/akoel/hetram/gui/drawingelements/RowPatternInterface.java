package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.hetram.accessories.Displacement;
import hu.akoel.hetram.gui.ElementSettingTab.ROW_PATTERN;

import java.awt.Graphics2D;

/**
 * Vegyunk egy peldat. Ha a kitoltendo mintazat V akar lenni, akkor
 * HORIZONTAL iranyultsag eseten  VVVVVVVVVVV
 * VERTICAL iranyultsag eseten pedig <
 *                                   <
 *                                   <
 *                                   <
 *                                   <
 *                                   <
 * Alakzatot kivanjuk latni.
 * @author akoel
 *
 */
public interface RowPatternInterface {

	public ROW_PATTERN getType();
	
	/**
	 * A pattern fuggoleges es vizszintes oldalhosszanak aranya
	 * Ha a kitoltendo alakzat HORIZONTAL iranyultsagu, akkor a minta magassaga 
	 * az alakzatmagassaga, es a szelessege pedig a magassag es a fuggveny altal
	 * visszaadott ertek hanyadosa. Pld ha a visszaadott ertek 2 akkor a minta 
	 * vizszintes hossza a magassag felevel egyenlo
	 * VERTICAL iranyultsag eseten az alakzat
	 * szelessege adja a minta szelesseget, a magassaga pedig ezen ertek es a
	 * figgveny visszaadott ertekenek hanyadosa. A 2 erteknel maradva a minta 
	 * magassaga az alakzat szelessegenek felevel lesz egyenlo
	 * 
	 * @return
	 */
	public double getHeightPerWidth();
	
	/**
	 * 
	 * @param g2 grafikus eszkoz
	 * @param orientation HORIZONTAL/VERTICAL
	 * @param shift eltolas pixelben
	 * HORIZONTAL iranyultsag eseten az Y tengely 0 pontja itt van
	 * innen kezdjuk a rajzolast a shift + patternHeight -ig, majd
	 * a shift - patternHeight-tol shift-ig 
	 * VERTICAL esetben pedig az X tengely 0 pontja van itt
	 * innen kezdjuk a rajzolast a shift + patternWidth -ig, majd
	 * a shift - patternWidth-tol a shift -ig 
	 * @param patternWidth a minta szelessege pixelben
	 * @param PatternHeight a minta magassaga pixelben
	 */
	public void drawPattern( Graphics2D g2, Displacement orientation, int shift, int patternWidth, int patternHeight );
}
