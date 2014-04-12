package hu.akoel.mgu.sprite;

import hu.akoel.mgu.MGraphics;

import java.awt.Color;
import java.awt.Stroke;


public class RectangleElement extends SpriteElement{
	
	private double x;
	double y;
	double width;
	double height;
	private Color normalColor;
	private Stroke normalStroke;
	private Color focusColor;
	private Stroke focusStroke;
	private Color connectedColor;
	private Stroke connectedStroke;
	
	public RectangleElement( double x, double y, double width, double height, Color color, Stroke stroke ){

		commonConstructor(x, y, width, height, color, stroke, color, stroke, color, stroke );

	}

	public RectangleElement( double x, double y, double width, double height, Color normalColor, Stroke normalStroke, Color focusColor, Stroke focusStroke ){

		commonConstructor(x, y, width, height, normalColor, normalStroke, focusColor, focusStroke, normalColor, normalStroke );

	}

	public RectangleElement( double x, double y, double width, double height, Color normalColor, Stroke normalStroke, Color focusColor, Stroke focusStroke, Color connectedColor, Stroke connectedStroke ){

		commonConstructor(x, y, width, height, normalColor, normalStroke, focusColor, focusStroke, connectedColor, connectedStroke);

	}
	
	private void commonConstructor(double x, double y, double width, double height, Color normalColor, Stroke normalStroke, Color focusColor, Stroke focusStroke, Color connectedColor, Stroke connectedStroke){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.normalColor = normalColor;
		this.normalStroke = normalStroke;		
		
		this.focusColor = focusColor;
		this.focusStroke = focusStroke;		
		
		this.connectedColor = connectedColor;
		this.connectedStroke = connectedStroke;	
	}
	
	@Override
	public void draw(MGraphics g2) {
		g2.setColor(normalColor);
		g2.setStroke(normalStroke);
		g2.drawRectangle(x + getPositionX(), y + getPositionY(), x + getPositionX() + width, y + getPositionY() + height);		
	}

	@Override
	public void drawFocus(MGraphics g2) {
		g2.setColor(focusColor);
		g2.setStroke(focusStroke);
		g2.drawRectangle(x + getPositionX(), y + getPositionY(), x + getPositionX() + width, y + getPositionY() + height);		
	}

	@Override
	public void drawConnected(MGraphics g2) {
		g2.setColor(connectedColor);
		g2.setStroke(connectedStroke);
		g2.drawRectangle(x + getPositionX(), y + getPositionY(), x + getPositionX() + width, y + getPositionY() + height);				
	}
	

	
}
