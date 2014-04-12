package hu.akoel.hetram.gui.drawingelements;

public class HomogeneousPatternFactory {
	private HomogeneousPatternInterface homogeneousPatternInterface;
	
	public HomogeneousPatternFactory( HomogeneousPatternInterface homogeneousPatternInterface ){
		this.homogeneousPatternInterface = homogeneousPatternInterface;
	}
	
	public HomogeneousPatternInterface getHomogeneousPattern(){
		return this.homogeneousPatternInterface;
	}

}
