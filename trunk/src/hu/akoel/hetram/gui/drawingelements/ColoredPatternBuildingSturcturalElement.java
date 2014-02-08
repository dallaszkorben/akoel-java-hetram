package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class ColoredPatternBuildingSturcturalElement extends HetramDrawnElement{

	private final Stroke NORMAL_STROKE = new BasicStroke(1);
	
	private final Color SELECTED_COLOR = Color.red;
	private final Color SELECTED_BACKGROUND = Color.yellow;
	private final Stroke SELECTED_STROKE = new BasicStroke(1);

	//private static final Color INPROCESS_COLOR = Color.yellow;
	//private static final Color INPROCESS_BACKGROUND = Color.gray;
	private final Stroke INPROCESS_STROKE = new BasicStroke(3);
	
	private final Color INFOCUS_COLOR = Color.yellow;
	//private static final Color INFOCUS_BACKGROUND = Color.gray;
	private final Stroke INFOCUS_STROKE = new BasicStroke(1);
	
	private static final long serialVersionUID = -8868971968355924643L;

	private double lambda;

	private ColoredPatternBuildingSturcturalElement(Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength,	java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth, TYPE.BUILDINGSTRUCTURE);
	}

	private ColoredPatternBuildingSturcturalElement( Status status, double x1, double y1 ){
		super( status, x1, y1, TYPE.BUILDINGSTRUCTURE );
	}
	
	public ColoredPatternBuildingSturcturalElement(Status status, double x1, double y1, double lambda, Color color, Color background ) {
		super( status, x1, y1, TYPE.BUILDINGSTRUCTURE );
		
		this.lambda = lambda;		
		
		setNormal( color, NORMAL_STROKE, background );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, background );
		setInprocess( color, INPROCESS_STROKE, background );
		
		refreshStatus();
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
}
