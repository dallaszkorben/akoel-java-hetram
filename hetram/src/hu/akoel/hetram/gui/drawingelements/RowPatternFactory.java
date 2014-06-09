package hu.akoel.hetram.gui.drawingelements;

public  class RowPatternFactory {

	private RowPatternInterface rowPatternInterface;
	
	public RowPatternFactory( RowPatternInterface rowPatternInterface ){
		this.rowPatternInterface = rowPatternInterface;
	}
	public RowPatternInterface getRowPattern(){
		return rowPatternInterface;
	}

}
