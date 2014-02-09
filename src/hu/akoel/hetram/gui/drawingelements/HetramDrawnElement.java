package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public abstract class HetramDrawnElement extends DrawnBlock{

	private static final long serialVersionUID = 4295764049731352586L;
	
//	private double baseX;
//	private double baseY;
	
	public HetramDrawnElement(Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
		
//		this.baseX = x1;
//		this.baseY = y1;
	}

	public HetramDrawnElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );

//		this.baseX = x1;
//		this.baseY = y1;

	}

	/**
	 * Visszaadja azz indulo X erteket
	 * 
	 * @return
	 */
/*	public double getBaseX() {
		return baseX;
	}
*/
	/**
	 * Visszaadja az indulo Y erteket
	 * @return
	 */
/*	public double getBaseY() {
		return baseY;
	}
*/

}
