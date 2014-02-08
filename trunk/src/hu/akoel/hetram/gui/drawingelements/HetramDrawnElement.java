package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public class HetramDrawnElement extends DrawnBlock{

	private static final long serialVersionUID = 4295764049731352586L;

	public static enum TYPE{
		BUILDINGSTRUCTURE,
		OPENEDGE,
		SYMMETRICEDGE
	}
	
	private TYPE type;
	
	public HetramDrawnElement(Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth, TYPE type) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
		
		this.type = type;
	}

	public HetramDrawnElement( Status status, double x1, double y1, TYPE type ){
		super( status, x1, y1 );
		
		this.type = type;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
	

}
