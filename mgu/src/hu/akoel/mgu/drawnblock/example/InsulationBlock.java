package hu.akoel.mgu.drawnblock.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.math.BigDecimal;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public class InsulationBlock extends DrawnBlock{
	
	private static final Color NORMAL_COLOR = Color.green;
	private static final Stroke NORMAL_STROKE = new BasicStroke(3);
	private static final Color NORMAL_BACKGROUND = new Color( 0, 100, 0 );
	
	private static final Color SELECTED_COLOR = Color.cyan;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	private static final Color SELECTED_BACKGROUND = new Color( 0, 100, 0 );
	
	private static final Color INFOCUS_COLOR = Color.magenta;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(1);
	private static final Color INFOCUS_BACKGROUND = new Color( 0, 0, 100 );
	
	private static final Color INPROCESS_COLOR = Color.green;
	private static final Stroke INPROCESS_STROKE = new BasicStroke(3);
	private static final Color INPROCESS_BACKGROUND = Color.black;
	
	private InsulationBlock(Status status, BigDecimal x1, BigDecimal y1) {
		super(status, x1, y1);
		
		setNormal( NORMAL_COLOR, NORMAL_STROKE, NORMAL_BACKGROUND );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, INFOCUS_BACKGROUND );
		setInprocess( INPROCESS_COLOR, INPROCESS_STROKE, INPROCESS_BACKGROUND );
		
		refreshStatus();
	}

	public InsulationBlock( Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth );
		
		setNormal( NORMAL_COLOR, NORMAL_STROKE, NORMAL_BACKGROUND );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, INFOCUS_BACKGROUND );
		setInprocess( INPROCESS_COLOR, INPROCESS_STROKE, INPROCESS_BACKGROUND );
		
		refreshStatus();
	}

}
