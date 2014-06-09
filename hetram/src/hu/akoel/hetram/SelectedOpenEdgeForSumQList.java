package hu.akoel.hetram;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.connectors.IThermicConnector;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
import hu.akoel.hetram.gui.StatusLine;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;

public class SelectedOpenEdgeForSumQList {
	private Collection<OpenEdgeElement> list = new ArrayList<OpenEdgeElement>();
	
	public void add( OpenEdgeElement openEdgeElement ){
		this.list.add( openEdgeElement );
	}
	
	public void clear(){
		this.list.clear();
	}
	
	public void drawOpenEdge( DrawnBlockCanvas canvas, MGraphics g2 ){
		Status status;
		
		for( OpenEdgeElement openEdgeElement: list){
	
			status = openEdgeElement.getStatus();
			openEdgeElement.setStatus( Status.SELECTED );
			openEdgeElement.draw(g2);
			openEdgeElement.setStatus( status );
			
		}
	}
	
	public void writeOpenEdge( ThermicPointList thermicPointList, StatusLine statusLine ){
			
		BigDecimal x1, x2, y1, y2;
		for( OpenEdgeElement openEdgeElement: list){
			
			ThermicPoint tp;
			BigDecimalPosition position;
			
			x1= openEdgeElement.getX1();
			x2= openEdgeElement.getX2();
			y1= openEdgeElement.getY1();
			y2= openEdgeElement.getY2();
			
			//-----------
			//
			//Vertikalis
			//
			//-----------
			if( x1.equals(x2) ){
			
				double eastCurrent = 0;
				double westCurrent = 0;
				
				IThermicConnector cE;
				IThermicConnector cW;

				for( int i = 0;  i < thermicPointList.getSize(); i++ ){
					tp = thermicPointList.get( i );
					
					position = tp.getPosition();
					IThermicConnector tc;
					
					//
					// EAST
					//
					tc = tp.getEastThermicConnector();
					if( tc instanceof OpenEdgeThermicConnector ){
						OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
						
						//Ez a termikus pont kapcsolodik EAST fele a kivalasztott OPENEDGEELEMENT-hez
						if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
							eastCurrent += tp.getEastCurrent();
						}
						
					}
					
					//
					// WEST
					//
					tc = tp.getWestThermicConnector();
					if( tc instanceof OpenEdgeThermicConnector ){
						OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
						
						//Ez a termikus pont kapcsolodik WEST fele a kivalasztott OPENEDGEELEMENT-hez
						if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
							westCurrent += tp.getWestCurrent();
						}
						
					}					
						
				}

				statusLine.setVerticalOpenEdgeSumQ( eastCurrent, westCurrent );
				
			//-------------
			//
			//Horizontalis				
			//
			//-------------
			}if( y1.equals(y2) ){
			
				double northCurrent = 0;
				double southCurrent = 0;

				for( int i = 0;  i < thermicPointList.getSize(); i++ ){
					tp = thermicPointList.get( i );
					
					position = tp.getPosition();
					IThermicConnector tc;
					
					//
					// NORTH
					//
					tc = tp.getNorthThermicConnector();
					if( tc instanceof OpenEdgeThermicConnector ){
						OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
						
						//Ez a termikus pont kapcsolodik NORTH fele a kivalasztott OPENEDGEELEMENT-hez
						if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
							northCurrent += tp.getNorthCurrent();
						}
						
					}
					
					//
					// SOUTH
					//
					tc = tp.getSouthThermicConnector();
					if( tc instanceof OpenEdgeThermicConnector ){
						OpenEdgeThermicConnector oetc = (OpenEdgeThermicConnector)tc;
						
						//Ez a termikus pont kapcsolodik SOUTH fele a kivalasztott OPENEDGEELEMENT-hez
						if( openEdgeElement.equals( oetc.getOpenEdgeElement() ) ){
							southCurrent += tp.getSouthCurrent();
						}
						
					}
							
				}

				statusLine.setHorizontalOpenEdgeSumQ( northCurrent, southCurrent );
				
			}
			
		}
	}	
}
