package hu.akoel.hetram;

import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
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
			needRevalidate = true;
			//canvas.addTemporaryDrawnBlock(selectedElement);
			//canvas.repaintCoreCanvas();

		}

		//Kurzor poziciojanak kerekitese a megadott pontossagra
		BigDecimal x = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldXByPixel( e.getX() ) );
		BigDecimal y = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldYByPixel( e.getY() ) );
		
		if( canvas.isEnabledDrawn() ){
			//SecondaryCursor cursor = canvas.getSecondaryCursor();
			
			for( HetramDrawnElement element: canvas.getDrawnBlockList()){
				
				if( 
						element.getX1().compareTo( x ) < 0 && 
						element.getX2().compareTo( x ) > 0 &&
						element.getY1().compareTo( y ) < 0 &&
						element.getY2().compareTo( y ) > 0){
				
					selectedElement = element;
					selectedElement.setStatus(Status.SELECTED);
					needRevalidate = true;
					//canvas.addTemporaryDrawnBlock(selectedElement);
					//canvas.repaintCoreCanvas();		
					
					break;
					
				}
				
			}
		}
		
		if( needRevalidate ){
			canvas.revalidateAndRepaintCoreCanvas();
		}

	}


}
