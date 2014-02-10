package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.math.BigDecimal;

public class ColoredPatternBuildingSturcturalElement extends HetramBuildingStructureElement{

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

	public ColoredPatternBuildingSturcturalElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double lambda, Color lineColor, Color backgroundColor ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth, lambda, lineColor, backgroundColor );
		
		setNormal( lineColor, NORMAL_STROKE, backgroundColor );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, backgroundColor );
		setInprocess( lineColor, INPROCESS_STROKE, backgroundColor );
		
		refreshStatus();
	}
	
	public ColoredPatternBuildingSturcturalElement(Status status, BigDecimal x1, BigDecimal y1, double lambda, Color lineColor, Color backgroundColor ) {
		super( status, x1, y1, lambda, lineColor, backgroundColor );
		
		setNormal( lineColor, NORMAL_STROKE, backgroundColor );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, backgroundColor );
		setInprocess( lineColor, INPROCESS_STROKE, backgroundColor );
		
		refreshStatus();
	}
	
}
