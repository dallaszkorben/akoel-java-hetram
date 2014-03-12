package hu.akoel.hetram.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.math.BigDecimal;

import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.drawnblock.SecondaryCursor;

public class DrawSecondaryCursor implements SecondaryCursor{

	private Color color = Color.black;
	private Stroke stroke = new BasicStroke(1);
	
	private BigDecimal positionX;
	private BigDecimal positionY;
	
	private MCanvas canvas;

	public DrawSecondaryCursor( MCanvas canvas ) {
		this.canvas = canvas;
		
		positionX = new BigDecimal("0");
		positionY = new BigDecimal("0");
	}

	public void setPosition(BigDecimal x, BigDecimal y) {
		positionX = x;
		positionY = y;
	}

	public BigDecimal getX() {
		return positionX;
	}

	public BigDecimal getY() {
		return positionY;
	}

	@Override
	public int getPixelXPositionByWorldBeforeTranslate(BigDecimal x) {
		return canvas.getPixelXPositionByWorldBeforeTranslate( x.doubleValue() );
	}

	@Override
	public int getPixelYPositionByWorldBeforeTranslate(BigDecimal y) {
		return canvas.getPixelYPositionByWorldBeforeTranslate( y.doubleValue() );
	}

	public void draw(Graphics2D g2) {
		int x, y;

		if (null != positionX && null != positionY) {

			x = getPixelXPositionByWorldBeforeTranslate(positionX);
			y = getPixelYPositionByWorldBeforeTranslate(positionY);

			g2.setColor( color );
			g2.setStroke(stroke );
			g2.drawLine(x, y - 8, x, y + 8);
			g2.drawLine(x - 8, y, x + 8, y);
		}

	}

	public String toString() {
		return new String(positionX.toPlainString() + ", "
				+ positionY.toPlainString());
	}
	
}
