package hu.akoel.hetram.listeners;

import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

import hu.akoel.hetram.HetramCanvas;
import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.gui.MainPanel.Mode;
import hu.akoel.hetram.gui.MainPanel.QShow;
import hu.akoel.hetram.gui.drawingelements.ColoredPatternBuildingSturcturalElement;
import hu.akoel.hetram.gui.drawingelements.HetramBuildingStructureElement;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.hetram.gui.drawingelements.HomogeneousPatternBuildingStructuralElement;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.gui.drawingelements.RowPatternBuildingStructuralElement;
import hu.akoel.hetram.gui.drawingelements.SymmetricEdgeElement;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.mgu.CursorPositionChangeListener;
import hu.akoel.mgu.drawnblock.Block;
import hu.akoel.mgu.drawnblock.SecondaryCursor;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
import hu.akoel.mgu.drawnblock.DrawnBlockMouseListener;

public class HetramMouseListener extends DrawnBlockMouseListener{

	private HetramDrawnElement selectedElement = null;
	
	private HetramCanvas canvas;
	public HetramMouseListener(HetramCanvas canvas) {
		super(canvas);		
		this.canvas = canvas;
	}
		
	/**
	 * Elvegzi az osszes muveletet ahhoz, hogy torlodjon minden kivalasztott elem
	 * es ujra rajzolja a Canvas-t
	 */
	public void clearAllSelected(){
		
		clearAllSelected( true );
		
	}
	
	/**
	 * Elvegzi az osszes muveletet ahhoz, hogy torlodjon minden kivalasztott elem
	 * es parameterkent megadhato, hogy ujra rajzolja-e a Canvast
	 * Ha volt kivalasztott elem, melyet torolni kellet es nem volt szukseg a Canvas
	 * kozvetlen ujrarajzolsara, akkor true erteket ad vissza. 
	 * Ellenkezo esetben false-t
	 * 
	 * @param needDirectRevalidate
	 * @return
	 */
	private boolean clearAllSelected( boolean needDirectRevalidate){
		
		if( null != selectedElement ){
			selectedElement.setStatus( Status.NORMAL );
			selectedElement = null;
			
			//Torli a kivalasztott OPENEDGE-eket
			canvas.getMainPanel().getSelectedOpenEdgeForSumQList().clear();
			
			//Most hogy QShow.THERMICPOINT lett a QSHOW mar a termikus pontok hoaramai irodhatnak ki a kurzor alapjan
			canvas.getMainPanel().setQShow( QShow.THERMICPOINT );
						
			if( needDirectRevalidate ){
				canvas.revalidateAndRepaintCoreCanvas();				
			}else{
				return true;
			}
		}
		return false;
	}
	
	public HetramDrawnElement getSelectedElement(){
		return selectedElement;
	}
	
	/**
	 * Kivalaszt egy elemet es SELECTED formaban ujrarajzoltatja
	 */
	@Override
	public void mouseClicked(MouseEvent e) {	
		boolean needRevalidate = false;
		
		needRevalidate = clearAllSelected( false );

		//Kurzor poziciojanak kerekitese a megadott pontossagra
		BigDecimal x = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldXByPixel( e.getX() ) );
		BigDecimal y = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldYByPixel( e.getY() ) );
	
		int delta = canvas.getSnapDelta();
		BigDecimal dx = new BigDecimal( canvas.getWorldXLengthByPixel( delta ) );
		BigDecimal dy = new BigDecimal( canvas.getWorldXLengthByPixel( delta ) );		
		Block cursorBlock = new Block( x.subtract( dx ), y.subtract( dy ) );
		cursorBlock.setWidth( dx.add(dx) );
		cursorBlock.setHeight( dy.add(dx) );
		
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
			
				//Ha Rajzolo modban vagyok
				//if( canvas.isEnabledDrawn() ){
				if( canvas.getMainPanel().getMode().equals(Mode.DRAWING) ){
				
					//Akkor a kivalasztott elem tulajdonsagait betolti a mezokbe
					selectedElement = element;
					selectedElement.setStatus(Status.SELECTED);
					
					if( selectedElement instanceof HetramBuildingStructureElement ){
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setStructureElementLambda( ((HetramBuildingStructureElement)selectedElement).getLambda() );

						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setStructureElementLineColor( ((HetramBuildingStructureElement)selectedElement).getNormalColor());
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setStructureElementBackgroundColor( ((HetramBuildingStructureElement)selectedElement).getBackgroundColor() );
						
						if( selectedElement instanceof ColoredPatternBuildingSturcturalElement ){
							canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().selectPatternTypeColorSelector();
						}else if( selectedElement instanceof HomogeneousPatternBuildingStructuralElement ){
							canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().selectPatternTypeHomogenSelector();
							canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().selectHomogeneusPattern( ((HomogeneousPatternBuildingStructuralElement)selectedElement).getHomogeneusPatternInterface().getForm() );
							
						}else if( selectedElement instanceof RowPatternBuildingStructuralElement ){
							canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().selectPatternTypeRowSelector();
							canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().selectRowPattern( ((RowPatternBuildingStructuralElement)selectedElement).getRowPatternInterface().getForm() );
						}
						
					}else if( selectedElement instanceof OpenEdgeElement ){
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setOpenEdgeAlphaBegin( ((OpenEdgeElement)selectedElement).getAlphaStart());
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setOpenEdgeAlphaEnd( ((OpenEdgeElement)selectedElement).getAlphaEnd());
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setOpenEdgeTemperature( ((OpenEdgeElement)selectedElement).getTemperature());
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setOpenEdgeColor( ((OpenEdgeElement)selectedElement).getNormalColor());
						
					}else if( selectedElement instanceof SymmetricEdgeElement ){
						canvas.getMainPanel().getSettingTabbedPanel().getElementSettingTab().setSymmetricEdgeColor( ((SymmetricEdgeElement)selectedElement).getNormalColor());
					}
					
					//Jelzem, hogy szukseges az ujrarajzolas
					needRevalidate = true;
				
					break;

				//Ha Analizis modban vagyok
				}else if( canvas.getMainPanel().getMode().equals(Mode.ANALYSIS) && element instanceof OpenEdgeElement ){
			
					selectedElement = element;

					//Hoaram osszegzo listahoz ad
					canvas.getMainPanel().getSelectedOpenEdgeForSumQList().add( (OpenEdgeElement) selectedElement );

					//A kivalasztott OPENEDGE osszesitett bemeno aramat kell megjeleniteni 
					canvas.getMainPanel().setQShow( QShow.OPENEDGE );
					
//Az OPENEDGE vonalában a hőmérsékletek megjelenítése
//??Táblázat/grafikon/file??					
canvas.getMainPanel().showThermicGraph( (OpenEdgeElement) selectedElement );	
					
					//Jelzem, hogy szukseges az ujrarajzolas
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
		
		//Ha a rajzolas elkezdodott es van kivalasztott elem, akkor az NORMAL statuszra allitja
		if( isDrawnStarted() && null != selectedElement ){
			selectedElement.setStatus(Status.NORMAL);
			selectedElement = null;
			canvas.revalidateAndRepaintCoreCanvas();
		}
		
	}
	
	/**
	 * Meghatarozza a masodlagos kurzor aktualis erteket
	 * Azt vizsgalja, hogy a pont pozicionalhato-e egyaltalan
	 * az adott helyre
	 * 
	 * @param e
	 */
	public void findOutCursorPosition( MouseEvent e ){
		super.findOutCursorPosition(e);
		
		BigDecimal x = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldXByPixel( e.getX() ) );
		BigDecimal y = canvas.getRoundedBigDecimalWithPrecision( canvas.getWorldYByPixel( e.getY() ) );
		
		//Analizis modban csak termikus pont lehetseges vagy OPENEDGE
		if( canvas.getMainPanel().getMode().equals(Mode.ANALYSIS)){
			
			ThermicPointList tpl = canvas.getMainPanel().getTermicPointList();
			int size = tpl.getSize();
			
			double tmpDist;
			double distance = 1000;
			int pos = 0;
			for( int i = 0; i < size; i++ ){
				ThermicPoint tp = tpl.get(i);
				
				tmpDist = Math.abs(tp.getPosition().getX().doubleValue() - x.doubleValue() )  + Math.abs( tp.getPosition().getY().doubleValue() - y.doubleValue() );
			
				if( tmpDist < distance ){
					distance = tmpDist;
					pos = i;
				}				
			}
			
			BigDecimalPosition position = tpl.get(pos).getPosition();
			SecondaryCursor sc = canvas.getSecondaryCursor();
			sc.setPosition( position.getX(), position.getY());
			
		}
	}

}
