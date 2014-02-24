package hu.akoel.hetram;

import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.mgu.drawnblock.Block;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
import hu.akoel.mgu.drawnblock.DrawnBlockMouseListener;

public class HetramMouseListener extends DrawnBlockMouseListener{

	private HetramDrawnElement selectedElement = null;
	
	private HetramCanvas canvas;
	public HetramMouseListener(HetramCanvas canvas) {
		super(canvas);		
		this.canvas = canvas;
	}
		
	public HetramDrawnElement getSelectedElement(){
		return selectedElement;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {	
		boolean needRevalidate = false;
		
		if( null != selectedElement ){
			selectedElement.setStatus( Status.NORMAL );
			selectedElement = null;
			needRevalidate = true;

		}

		//Kurzor poziciojanak kerekitese a megadott pontossagra
		BigDecimal x = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldXByPixel( e.getX() ) );
		BigDecimal y = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldYByPixel( e.getY() ) );
		
		int delta = canvas.getSnapDelta();
		BigDecimal dx = new BigDecimal( canvas.getWorldXLengthByPixel( delta ) );
		BigDecimal dy = new BigDecimal( canvas.getWorldXLengthByPixel( delta ) );		
		Block cursorBlock = new Block( x.subtract( dx ), y.subtract( dy ) );
		cursorBlock.setWidth( dx.add(dx) );
		cursorBlock.setHeight( dy.add(dx) );
		
		if( canvas.isEnabledDrawn() ){
			//SecondaryCursor cursor = canvas.getSecondaryCursor();
			
			for( HetramDrawnElement element: canvas.getDrawnBlockList()){
				
				BigDecimal minY = cursorBlock.getY1().max( element.getY1() );
				BigDecimal maxY = cursorBlock.getY2().min( element.getY2() );
				
				BigDecimal minX = cursorBlock.getX1().max( element.getX1() );
				BigDecimal maxX = cursorBlock.getX2().min( element.getX2() );
				
				if(
						
								
						( cursorBlock.getX1().compareTo(element.getX1() ) < 0 &&
						cursorBlock.getX2().compareTo(element.getX2() ) > 0 &&
						minY.compareTo(maxY) < 0 
						) ||
						
						
						( cursorBlock.getY1().compareTo(element.getY1() ) < 0 &&
						cursorBlock.getY2().compareTo(element.getY2() ) > 0 &&
						minX.compareTo(maxX) < 0
						) ||
						
						(element.getX1().compareTo( x ) < 0 && 
						element.getX2().compareTo( x ) > 0 &&
						element.getY1().compareTo( y ) < 0 &&
						element.getY2().compareTo( y ) > 0) ){
				
					selectedElement = element;
					selectedElement.setStatus(Status.SELECTED);
					needRevalidate = true;
					
					break;
					
				}				
			}
		}
		
		if( needRevalidate ){
			canvas.revalidateAndRepaintCoreCanvas();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		if( isDrawnStarted() && null != selectedElement ){
			selectedElement.setStatus(Status.NORMAL);
			selectedElement = null;
			canvas.revalidateAndRepaintCoreCanvas();
		}
		
	}

}
