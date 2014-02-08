package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public abstract class HetramDrawnElement extends DrawnBlock{

	private static final long serialVersionUID = 4295764049731352586L;
	
	public HetramDrawnElement(Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);

	}

	public HetramDrawnElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );

	}	

}
