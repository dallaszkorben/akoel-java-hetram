package hu.akoel.hetram;

import java.awt.Color;

import javax.swing.border.Border;

import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.values.TranslateValue;

public class HetramCanvas extends DrawnBlockCanvas{

	public HetramCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle);
	}

	private static final long serialVersionUID = -6015123637668801411L;

	/**
	 * Egy DrawnBlock rajzolasat elvegzo factory megadasa
	 * @param drawnBlockFactory
	 */
	public void setDrawnBlockFactory( HetramDrawnElementFactory drawnBlockFactory ){
		super.setDrawnBlockFactory(drawnBlockFactory);
	}
	

	
	
	
	
	
	/**
	 * Hozzaad a megjelenitendo listahoz egy DrawnBlock-ot
	 * 
	 * @param drawnElement
	 */
	public void addDrawnBlock( HetramDrawnElement drawnElement ){
		super.addDrawnBlock(drawnElement);
	}
	
	/**
	 * Eltavolit egy DrawnBlock elemet a megjelenitendo DrawnBlock listabol
	 * 
	 * @param drawnBlock
	 */
	public void removeDrawnBlock( HetramDrawnElement drawnElement ){
		super.removeDrawnBlock(drawnElement);
	}
		
	/**
	 * Hozzaad egy DrawnBlock elemet a Temporary listahoz atmeneti megjelenitesre
	 * 
	 * @param drawnBlock
	 */
	public void addTemporaryDrawnBlock( HetramDrawnElement drawnElement ){
		super.addTemporaryDrawnBlock(drawnElement);
	}
	

}
