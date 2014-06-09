package hu.akoel.mgu.drawnblock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas.Precision;


//public abstract class DrawnBlock extends java.awt.geom.Rectangle2D.Double implements Cloneable{
public abstract class DrawnBlock extends Block{
	
//	private static final long serialVersionUID = -3676065835228116831L;

	public static enum Status{
		NORMAL,
		SELECTED,
		INFOCUS,
		INPROCESS
	};
	
	private static final Color NORMAL_COLOR = Color.white;
	private static final Stroke NORMAL_STROKE = new BasicStroke(1);
	private static final Color NORMAL_BACKGROUND = Color.black;
	
	private static final Color SELECTED_COLOR = Color.red;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	private static final Color SELECTED_BACKGROUND = Color.black;
	
	private static final Color INFOCUS_COLOR = Color.yellow;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(1);
	private static final Color INFOCUS_BACKGROUND = Color.black;
	
	private static final Color INPROCESS_COLOR = Color.red;
	private static final Stroke INPROCESS_STROKE = new BasicStroke(3);
	private static final Color INPROCESS_BACKGROUND = Color.black;
	
	private Status status;
	
	private Color color;
	private Color backgroundColor;
	private Stroke stroke;
	private TexturePaint texturePaint;
		
	private Color normalColor;
	private Stroke normalStroke; 
	private Color normalBackgroundColor;
	private TexturePaint normalTexturePaint;
	
	private Color selectedColor;
	private Stroke selectedStroke;
	private Color selectedBackgroundColor;
	private TexturePaint selectedTexturePaint;
	
	private Color infocusColor;
	private Stroke infocusStroke;
	private Color infocusBackgroundColor;
	private TexturePaint infocusTexturePaint;
	
	private Color inprocessColor;
	private Stroke inprocessStroke;
	private Color inprocessBackgroundColor;
	private TexturePaint inprocessTexturePaint;

	public DrawnBlock( Status status, BigDecimal x1, BigDecimal y1 ){
		super( x1, y1 );
				
		this.status = status;
		
		setNormal( NORMAL_COLOR, NORMAL_STROKE, NORMAL_BACKGROUND );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, INFOCUS_BACKGROUND );
		setInprocess( INPROCESS_COLOR, INPROCESS_STROKE, INPROCESS_BACKGROUND );
				
		refreshStatus();
	}
	
	public DrawnBlock( Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth ){

		super( x1, y1 );
		
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		
/*		if( null != minLength ){
			setX2( x1 + minLength );
		}
		
		if( null != minWidth ){
			setY2( y1 + minWidth );
		}
*/		
		this.status = status;
		
		setNormal( NORMAL_COLOR, NORMAL_STROKE, NORMAL_BACKGROUND );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, SELECTED_BACKGROUND );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, INFOCUS_BACKGROUND );
		setInprocess( INPROCESS_COLOR, INPROCESS_STROKE, INPROCESS_BACKGROUND );
				
		refreshStatus();
	}
	
	public DrawnBlock( Precision precision, Element xmlElement ){
		super( new BigDecimal("0"), new BigDecimal("0") );
		this.status = Status.NORMAL;
		
		NodeList mainNodeList = xmlElement.getChildNodes();
		for (int i = 0; i < mainNodeList.getLength(); i++) {
			
			Node mainNode = mainNodeList.item( i );
			
			if (mainNode.getNodeType() == Node.ELEMENT_NODE) {
				Element drawnBlockElement = (Element) mainNode;

				//Ha egy POSITION elemrol van szo
				if( drawnBlockElement.getNodeName().equals( "position" )){

					//X1, Y1
					this.reset( new BigDecimal( drawnBlockElement.getAttribute("x1") ), new BigDecimal( drawnBlockElement.getAttribute("y1") ) );
					
					//X2, Y2
					this.changeSize( new BigDecimal( drawnBlockElement.getAttribute("x2") ), new BigDecimal( drawnBlockElement.getAttribute("y2") ) );					

this.setScale( precision.getScale());
//System.err.println(precision.getScale() + " - " + this.getX1().scale());
				//Ha egy DESCRIPTOR elemrol van szo
				}else if( drawnBlockElement.getNodeName().equals( "descriptor" ) ){
					
					NodeList descriptorList = drawnBlockElement.getChildNodes();
					for (int j = 0; j < descriptorList.getLength(); j++) {
						
						Node descriptorNode = descriptorList.item( j );
						
						if (descriptorNode.getNodeType() == Node.ELEMENT_NODE) {
							Element descriptorElement = (Element) descriptorNode;
							
							//Ha egy NORMAL elemrol van szo
							if( descriptorElement.getNodeName().equals( "normal" )){

								NodeList normalList = descriptorElement.getChildNodes();
								for (int k = 0; k < normalList.getLength(); k++) {
									
									Node normalNode = normalList.item( k );
									
									if (normalNode.getNodeType() == Node.ELEMENT_NODE) {
										Element normalElement = (Element) normalNode;
										
										//Ha egy COLOR elemrol van szo
										if( normalElement.getNodeName().equals( "color" )){
											Color color = new Color(Integer.valueOf( normalElement.getAttribute("red") ), Integer.valueOf( normalElement.getAttribute("green") ), Integer.valueOf( normalElement.getAttribute("blue") ) );
											setNormalColor( color );
										//Ha egy BACKGROUND elemrol van szo
										}else if( normalElement.getNodeName().equals( "background" )){
											Color background = new Color(Integer.valueOf( normalElement.getAttribute("red") ), Integer.valueOf( normalElement.getAttribute("green") ), Integer.valueOf( normalElement.getAttribute("blue") ) );
											setNormalBackgroundColor(background);
										//Ha egy STROKE elemrol van szo
										}else if( normalElement.getNodeName().equals( "stroke" )){
											Stroke stroke = new BasicStroke( Float.valueOf( normalElement.getAttribute("linewidth") ) );
											setNormalStroke(stroke);
										}
									}
								}
								
							//Ha egy SELECTED elemrol van szo
							}else if( descriptorElement.getNodeName().equals( "selected" )){
								
								NodeList selectedList = descriptorElement.getChildNodes();
								for (int k = 0; k < selectedList.getLength(); k++) {
									
									Node selectedNode = selectedList.item( k );
									
									if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
										Element selectedElement = (Element) selectedNode;
										
										//Ha egy COLOR elemrol van szo
										if( selectedElement.getNodeName().equals( "color" )){
											Color color = new Color(Integer.valueOf( selectedElement.getAttribute("red") ), Integer.valueOf( selectedElement.getAttribute("green") ), Integer.valueOf( selectedElement.getAttribute("blue") ) );
											setSelectedColor( color );
										//Ha egy BACKGROUND elemrol van szo
										}else if( selectedElement.getNodeName().equals( "background" )){
											Color background = new Color(Integer.valueOf( selectedElement.getAttribute("red") ), Integer.valueOf( selectedElement.getAttribute("green") ), Integer.valueOf( selectedElement.getAttribute("blue") ) );
											setSelectedBackgroundColor(background);
										//Ha egy STROKE elemrol van szo
										}else if( selectedElement.getNodeName().equals( "stroke" )){
											Stroke stroke = new BasicStroke( Float.valueOf( selectedElement.getAttribute("linewidth") ) );
											setSelectedStroke(stroke);
										}
									}
								}
								
							//Ha egy INFOCUS elemrol van szo
							}else if( descriptorElement.getNodeName().equals( "infocus" )){

								NodeList infocusList = descriptorElement.getChildNodes();
								for (int k = 0; k < infocusList.getLength(); k++) {
									
									Node infocusNode = infocusList.item( k );
									
									if (infocusNode.getNodeType() == Node.ELEMENT_NODE) {
										Element infocusElement = (Element) infocusNode;
										
										//Ha egy COLOR elemrol van szo
										if( infocusElement.getNodeName().equals( "color" )){
											Color color = new Color(Integer.valueOf( infocusElement.getAttribute("red") ), Integer.valueOf( infocusElement.getAttribute("green") ), Integer.valueOf( infocusElement.getAttribute("blue") ) );
											setInfocusColor( color );
										//Ha egy BACKGROUND elemrol van szo
										}else if( infocusElement.getNodeName().equals( "background" )){
											Color background = new Color(Integer.valueOf( infocusElement.getAttribute("red") ), Integer.valueOf( infocusElement.getAttribute("green") ), Integer.valueOf( infocusElement.getAttribute("blue") ) );
											setInfocusBackgroundColor(background);
										//Ha egy STROKE elemrol van szo
										}else if( infocusElement.getNodeName().equals( "stroke" )){
											Stroke stroke = new BasicStroke( Float.valueOf( infocusElement.getAttribute("linewidth") ) );
											setInfocusStroke(stroke);
										}
									}
								}
								
							//Ha egy INPROCESS elemrol van szo
							}else if( descriptorElement.getNodeName().equals( "inprocess" )){

								NodeList inprocesList = descriptorElement.getChildNodes();
								for (int k = 0; k < inprocesList.getLength(); k++) {
									
									Node inprocessNode = inprocesList.item( k );
									
									if (inprocessNode.getNodeType() == Node.ELEMENT_NODE) {
										Element inprocessElement = (Element) inprocessNode;
										
										//Ha egy COLOR elemrol van szo
										if( inprocessElement.getNodeName().equals( "color" )){
											Color color = new Color(Integer.valueOf( inprocessElement.getAttribute("red") ), Integer.valueOf( inprocessElement.getAttribute("green") ), Integer.valueOf( inprocessElement.getAttribute("blue") ) );
											setInprocessColor( color );
										//Ha egy BACKGROUND elemrol van szo
										}else if( inprocessElement.getNodeName().equals( "background" )){
											Color background = new Color(Integer.valueOf( inprocessElement.getAttribute("red") ), Integer.valueOf( inprocessElement.getAttribute("green") ), Integer.valueOf( inprocessElement.getAttribute("blue") ) );
											setInprocessBackgroundColor(background);
										//Ha egy STROKE elemrol van szo
										}else if( inprocessElement.getNodeName().equals( "stroke" )){
											Stroke stroke = new BasicStroke( Float.valueOf( inprocessElement.getAttribute("linewidth") ) );
											setInprocessStroke(stroke);
										}
									}
								}								
							}							
						}
					}					
				}
			}			
		}
		
		refreshStatus();
		
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
		
		if( status.equals( Status.NORMAL ) ){
			color = getNormalColor();
			backgroundColor = getNormalBackgroundColor();
			stroke = getNormalStroke();
			texturePaint = getNormalTexturePaint();
		}else if( status.equals( Status.SELECTED ) ){
			color = getSelectedColor();
			backgroundColor = getSelectedBackgroundColor();
			stroke = getSelectedStroke();
			texturePaint = getSelectedTexturePaint();
		}else if( status.equals( Status.INFOCUS ) ){
			color = getInfocusColor();
			backgroundColor = getInfocusBackgroundColor();
			stroke = getInfocusStroke();
			texturePaint = getInfocusTexturePaint();
		}else if( status.equals( Status.INPROCESS ) ){
			color = getInprocessColor();
			backgroundColor = getInprocessBackgroundColor();
			stroke = getInprocessStroke();
			texturePaint = getInprocessTexturePaint();
		}
	}
	
	public void refreshStatus(){
		setStatus( status );
	}	
	
	public boolean enabledToChange( BigDecimal x, BigDecimal y ){
		BigDecimal w;
		BigDecimal h;
		
		if( x.compareTo( this.getStartX() ) < 0 ){
			w =  this.getStartX().subtract( x );
		}else{
			w = x.subtract( this.getStartX() );
		}
		
		if( y.compareTo( this.getStartY() ) < 0 ){
			h = this.getStartY().subtract( y );
		}else{
			h = y.subtract( this.getStartY() );
		}
		
		//
		// MAX
		//
		// Ketiranyu korlatozas van megadva
		if( null != maxWidth && null != maxLength ){

			if( !( ( h.compareTo(maxWidth) <= 0 && w.compareTo(maxLength) <= 0 ) || ( w.compareTo(maxWidth) <= 0 && h.compareTo(maxLength) <= 0) ) ){
			
				return false;
			}
				
		// Csak az egyik oldalnak van megadva korlatozas
		} else if( null != maxWidth ){
//System.err.println( (h.compareTo(maxWidth) > 0) + ", " + (w.compareTo( maxWidth) > 0 ) );
			if( h.compareTo(maxWidth) > 0 && w.compareTo( maxWidth) > 0){
			
				return false;			
			}
					
		// Csak az egyik oldalnak van megadva korlatozas
		} else if( null != maxLength ){

			if( h.compareTo(maxLength) > 0 && w.compareTo(maxLength) > 0 ){
			
				return false;
			}
		}
		
		//
		// MIN
		//
		// Ketiranyu korlatozas van megadva
		if( null != minWidth && null != minLength ){

			if( ( ( h.compareTo(minWidth) < 0 || w.compareTo(minLength) < 0 ) && ( w.compareTo(minWidth) < 0 || h.compareTo(minLength) < 0) ) ){
		
				return false;
			}
				
		// Csak az egyik oldalnak van megadva korlatozas
		} else if( null != minWidth ){

			if( h.compareTo(minWidth) < 0 && w.compareTo( minWidth) < 0){
			
				return false;			
			}
					
		// Csak az egyik oldalnak van megadva korlatozas
		} else if( null != minLength ){

			if( h.compareTo(minLength) < 0 && w.compareTo(minLength) < 0 ){
			
				return false;
			}
		}
		
		
		return true;
		
	}

	public Color getColor(){
		return color;		
	}
	
	public Color getBackgroundColor(){
		return backgroundColor;
	}
	
	public Stroke getStroke(){
		return stroke;
	}
	
	public TexturePaint texturePaint(){
		return texturePaint;
	}
	
	
	
	public void setTexturalPaint( TexturePaint texturePaint ){
		this.texturePaint = texturePaint;
	}
	
	public void setNormalTexturalPaint( TexturePaint texturePaint ){
		this.normalTexturePaint = texturePaint;
	}
	
	public void setSelectedTexturalPaint( TexturePaint texturePaint ){
		this.selectedTexturePaint = texturePaint;
	}
	
	public void setInfocusTexturalPaint( TexturePaint texturePaint ){
		this.infocusTexturePaint = texturePaint;
	}

	public void setInprocessTexturalPaint( TexturePaint texturePaint ){
		this.inprocessTexturePaint = texturePaint;
	}

	
	
	public BigDecimal getMinLength() {
		return minLength;
	}

	public BigDecimal getMinWidth() {
		return minWidth;
	}

	public BigDecimal getMaxLength() {
		return maxLength;
	}

	public BigDecimal getMaxWidth() {
		return maxWidth;
	}

	
	//
	// Normal
	//
	public Color getNormalColor() {
		return normalColor;
	}

	public Stroke getNormalStroke() {
		return normalStroke;
	}

	public Color getNormalBackgroundColor() {
		return normalBackgroundColor;
	}
	
	public TexturePaint getNormalTexturePaint(){
		return normalTexturePaint;
	}

	public void setNormalColor( Color color ){
		this.normalColor = color;
	}
	
	public void setNormalBackgroundColor( Color color ){
		this.normalBackgroundColor = color;
	}
	
	public void setNormalStroke( Stroke stroke ){
		this.normalStroke = stroke;
	}
	
	//
	// Selected
	//
	public Color getSelectedColor() {
		return selectedColor;
	}

	public Stroke getSelectedStroke() {
		return selectedStroke;
	}

	public Color getSelectedBackgroundColor() {
		return selectedBackgroundColor;
	}
	
	public TexturePaint getSelectedTexturePaint(){
		return selectedTexturePaint;
	}

	public void setSelectedColor( Color color ){
		this.selectedColor = color;
	}
	
	public void setSelectedBackgroundColor( Color color ){
		this.selectedBackgroundColor = color;
	}
	
	public void setSelectedStroke( Stroke stroke ){
		this.selectedStroke = stroke;
	}

	//
	// Infocus
	//
	public Color getInfocusColor() {
		return infocusColor;
	}

	public Stroke getInfocusStroke() {
		return infocusStroke;
	}

	public Color getInfocusBackgroundColor() {
		return infocusBackgroundColor;
	}

	public TexturePaint getInfocusTexturePaint(){
		return infocusTexturePaint;
	}
	
	public void setInfocusColor( Color color ){
		this.infocusColor = color;
	}
	
	public void setInfocusBackgroundColor( Color color ){
		this.infocusBackgroundColor = color;
	}
	
	public void setInfocusStroke( Stroke stroke ){
		this.infocusStroke = stroke;
	}

	//
	// Inporcess
	//
	public Color getInprocessColor() {
		return inprocessColor;
	}

	public TexturePaint getInprocessTexturePaint(){
		return inprocessTexturePaint;
	}
	
	public Stroke getInprocessStroke() {
		return inprocessStroke;
	}

	public Color getInprocessBackgroundColor() {
		return inprocessBackgroundColor;
	}
	
	public void setInprocessColor( Color color ){
		this.inprocessColor = color;
	}
	
	public void setInprocessBackgroundColor( Color color ){
		this.inprocessBackgroundColor = color;
	}
	
	public void setInprocessStroke( Stroke stroke ){
		this.inprocessStroke = stroke;
	}
	
	

	//Normal
	public void setNormal( Color color, Stroke stroke, Color backgroundColor ){
		this.normalColor = color;
		this.normalStroke = stroke;
		this.normalBackgroundColor = backgroundColor;
		
		refreshStatus();
	}
/*
	public void setNormal( Color color, Stroke stroke, TexturePaint texturePaint ){
		this.normalColor = color;
		this.normalStroke = stroke;
		this.normalTexturePaint = texturePaint;
		
		refreshStatus();
	}
*/	
	//Selected
	public void setSelected( Color color, Stroke stroke, Color backgroundColor ){
		this.selectedColor = color;
		this.selectedStroke = stroke;
		this.selectedBackgroundColor = backgroundColor;
		
		refreshStatus();
	}
/*
	public void setSelected( Color color, Stroke stroke, TexturePaint texturePaint ){
		this.selectedColor = color;
		this.selectedStroke = stroke;
		this.selectedTexturePaint = texturePaint;
		
		refreshStatus();
	}
*/
	//InFocus
	public void setInfocus( Color color, Stroke stroke, Color backgroundColor ){
		this.infocusColor = color;
		this.infocusStroke = stroke;
		this.infocusBackgroundColor = backgroundColor;
		
		refreshStatus();
	}
/*
	public void setInfocus( Color color, Stroke stroke, TexturePaint texturePaint ){
		this.infocusColor = color;
		this.infocusStroke = stroke;
		this.infocusTexturePaint = texturePaint;
		
		refreshStatus();
	}
*/	
	//InProcess
	public void setInprocess( Color color, Stroke stroke, Color backgroundColor ){
		this.inprocessColor = color;
		this.inprocessStroke = stroke;
		this.inprocessBackgroundColor = backgroundColor;
		
		refreshStatus();
	}
/*
	public void setInprocess( Color color, Stroke stroke, TexturePaint texturePaint ){
		this.inprocessColor = color;
		this.inprocessStroke = stroke;
		this.inprocessTexturePaint = texturePaint;
		
		refreshStatus();
	}
*/
	public void draw( MGraphics g2 ){
		double x1 = getX1().doubleValue();
		double y1 = getY1().doubleValue();
		double x2 = getX2().doubleValue();
		double y2 = getY2().doubleValue();		
		
		if( null != texturePaint ){
			g2.setPaint( texturePaint );
		}else{
		
//		if( null != backgroundColor ){
			g2.setColor( backgroundColor );
		}
		g2.fillRectangle( x1, y1, x2, y2 );
		
		g2.setStroke( stroke );
		g2.setColor( color );
		g2.drawRectangle( x1, y1, x2, y2 );
	}
	
	public boolean intersectsOrContains( Block block2 ){
		BigDecimal tx1 = getX1();
		BigDecimal ty1 = getY1();
		BigDecimal tx2 = getX2();
		BigDecimal ty2 = getY2();
		
		BigDecimal rx1 = block2.getX1();
		BigDecimal ry1 = block2.getY1();
		BigDecimal rx2 = block2.getX2();
		BigDecimal ry2 = block2.getY2();	
		
		if( 
				(
						(rx1.compareTo( tx1 ) >= 0 && rx2.compareTo( tx2 ) <= 0 && ( ( rx1.compareTo( tx1 ) != 0 || rx2.compareTo( tx1 ) != 0 || tx1.compareTo(tx2) == 0 ) && ( rx1.compareTo(tx2) != 0 || rx2.compareTo(tx2) != 0 || tx1.compareTo(tx2) == 0)  ) ) ||
						(rx1.compareTo(tx1) < 0 && rx2.compareTo(tx1) > 0 ) ||
						(rx1.compareTo(tx2) < 0 && rx2.compareTo( tx2 ) > 0 ) ||
						(rx1.compareTo(tx1) < 0 && rx2.compareTo(tx2) > 0)
				) &&
				
				(
						(ry1.compareTo(ty1) >= 0 && ry2.compareTo(ty2) <= 0 && ( ( ry1.compareTo( ty1 ) != 0 || ry2.compareTo( ty1 ) != 0 || ty1.compareTo(ty2) == 0 ) && ( ry1.compareTo(ty2) != 0 || ry2.compareTo(ty2) != 0 || ty1.compareTo(ty2) == 0)  ) ) ||
						(ry1.compareTo(ty1) < 0 &&  ry2.compareTo(ty1) > 0 ) ||
						(ry1.compareTo(ty2) < 0 &&  ry2.compareTo(ty2) > 0 ) ||
						(ry1.compareTo(ty1) < 0 &&  ry2.compareTo(ty2) > 0)
				) 
				
		){
			return true;
		}
		
		return false;
		
	}
	
	public boolean intersects( Block block2 ) {

		BigDecimal tx1 = getX1();
		BigDecimal ty1 = getY1();
		BigDecimal tx2 = getX2();
		BigDecimal ty2 = getY2();
		BigDecimal tWidth = getWidth();
		BigDecimal tHeight = getHeight();
		
		BigDecimal rx1 = block2.getX1();
		BigDecimal ry1 = block2.getY1();
		BigDecimal rx2 = block2.getX2();
		BigDecimal ry2 = block2.getY2();
		BigDecimal rWidth = block2.getWidth();
		BigDecimal rHeight = block2.getHeight();
		
		if( tx1.compareTo( rx1 ) == -1 ) tx1 = rx1;
		if( ty1.compareTo( ry1 ) == -1 ) ty1 = ry1;
		if( tx2.compareTo( rx2 ) == 1 ) tx2 = rx2;
		if( ty2.compareTo( ry2 ) == 1 ) ty2 = ry2;
	
		tx2 = tx2.subtract( tx1 );
		ty2 = ty2.subtract( ty1 );
		
		//Biztos, hogy keresztezi vagy tartalmazza az egyik a masikat
		if( ty2.compareTo(new BigDecimal("0")) >= 0 && tx2.compareTo( new BigDecimal("0")) >= 0 ){
			tx2 = tx2.add( tx1 );
			ty2 = ty2.add( ty1 );
			
			//Valamelyik tartalmazza a masikat
			if(
					( tx2.subtract( tx1 ).compareTo( tWidth ) == 0 || tx2.subtract( tx1 ).compareTo( rWidth ) == 0 ) &&
					( ty2.subtract( ty1 ).compareTo( tHeight ) == 0 || ty2.subtract( ty1 ).compareTo( rHeight ) == 0 )
			){
				return false;

			//Csak keresztezesrol van szo
			}else{
				return true;
			}
		}else{
			return false;
		}
					
	}
	
	public String toString(){
		return new String( "(" + this.getX1() + ", " + this.getY1() + ") (" + this.getX2() + ", " + this.getY2() +")" );
	}
	
	public Element getXMLElement( Document document ){
		Attr attr;
		
		Element drawnBlockElement = document.createElement("drawnblock");		

		//Position
		Element positionElement = document.createElement( "position" );
		drawnBlockElement.appendChild( positionElement );
		
		attr = document.createAttribute("x1");
		attr.setValue( getX1().toPlainString());
		positionElement.setAttributeNode(attr);
		
		attr = document.createAttribute("y1");
		attr.setValue( getY1().toPlainString());
		positionElement.setAttributeNode(attr);

		attr = document.createAttribute("x2");
		attr.setValue( getX2().toPlainString());
		positionElement.setAttributeNode(attr);
		
		attr = document.createAttribute("y2");
		attr.setValue( getY2().toPlainString());
		positionElement.setAttributeNode(attr);
//System.err.println(getX1() + ", " + getY1() + " | " + getX2() + ", " + getY2());		
		
		DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols();
		decimalSymbol.setDecimalSeparator('.');		

		//Descriptor
		Element descriptorElement = document.createElement( "descriptor" );
		drawnBlockElement.appendChild( descriptorElement );
		
		//
		//Normal Descriptor
		//
		Element normalDescriptorElement = document.createElement( "normal" );
		descriptorElement.appendChild( normalDescriptorElement );
		
		//Color - Normal - Descriptor
		Element colorNormalDescriptorElement = document.createElement( "color" );
		normalDescriptorElement.appendChild( colorNormalDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getNormalColor().getGreen() ) );
		colorNormalDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getNormalColor().getRed() ) );
		colorNormalDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getNormalColor().getBlue() ) );
		colorNormalDescriptorElement.setAttributeNode(attr);	
		
		//Background - Normal - Descriptor
		Element backgroundNormalDescriptorElement = document.createElement( "background" );
		normalDescriptorElement.appendChild( backgroundNormalDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getNormalBackgroundColor().getGreen() ) );
		backgroundNormalDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getNormalBackgroundColor().getRed() ) );
		backgroundNormalDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getNormalBackgroundColor().getBlue() ) );
		backgroundNormalDescriptorElement.setAttributeNode(attr);	

		//Stroke - Normal - Descriptor
		Element strokeNormalDescriptorElement = document.createElement( "stroke" );
		normalDescriptorElement.appendChild( strokeNormalDescriptorElement );
				
		attr = document.createAttribute("linewidth");
		attr.setValue( String.valueOf( ((BasicStroke)getNormalStroke()).getLineWidth() ) );
		strokeNormalDescriptorElement.setAttributeNode(attr);	

		//
		//Selected Descriptor
		//
		Element selectedDescriptorElement = document.createElement( "selected" );
		descriptorElement.appendChild( selectedDescriptorElement );
		
		//Color - Normal - Descriptor
		Element colorSelectedDescriptorElement = document.createElement( "color" );
		selectedDescriptorElement.appendChild( colorSelectedDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getSelectedColor().getGreen() ) );
		colorSelectedDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getSelectedColor().getRed() ) );
		colorSelectedDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getSelectedColor().getBlue() ) );
		colorSelectedDescriptorElement.setAttributeNode(attr);	
		
		//Background - Normal - Descriptor
		Element backgroundSelectedDescriptorElement = document.createElement( "background" );
		selectedDescriptorElement.appendChild( backgroundSelectedDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getSelectedBackgroundColor().getGreen() ) );
		backgroundSelectedDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getSelectedBackgroundColor().getRed() ) );
		backgroundSelectedDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getSelectedBackgroundColor().getBlue() ) );
		backgroundSelectedDescriptorElement.setAttributeNode(attr);	

		//Stroke - Normal - Descriptor
		Element strokeSelectedDescriptorElement = document.createElement( "stroke" );
		selectedDescriptorElement.appendChild( strokeSelectedDescriptorElement );
				
		attr = document.createAttribute("linewidth");
		attr.setValue( String.valueOf( ((BasicStroke)getSelectedStroke()).getLineWidth() ) );
		strokeSelectedDescriptorElement.setAttributeNode(attr);	
		
		//
		//Infocus Descriptor
		//
		Element infocusDescriptorElement = document.createElement( "infocus" );
		descriptorElement.appendChild( infocusDescriptorElement );
		
		//Color - Normal - Descriptor
		Element colorInfocusDescriptorElement = document.createElement( "color" );
		infocusDescriptorElement.appendChild( colorInfocusDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getInfocusColor().getGreen() ) );
		colorInfocusDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getInfocusColor().getRed() ) );
		colorInfocusDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getInfocusColor().getBlue() ) );
		colorInfocusDescriptorElement.setAttributeNode(attr);	
		
		//Background - Normal - Descriptor
		Element backgroundInfocusDescriptorElement = document.createElement( "background" );
		infocusDescriptorElement.appendChild( backgroundInfocusDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getInfocusBackgroundColor().getGreen() ) );
		backgroundInfocusDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getInfocusBackgroundColor().getRed() ) );
		backgroundInfocusDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getInfocusBackgroundColor().getBlue() ) );
		backgroundInfocusDescriptorElement.setAttributeNode(attr);	

		//Stroke - Normal - Descriptor
		Element strokeInfocusDescriptorElement = document.createElement( "stroke" );
		infocusDescriptorElement.appendChild( strokeInfocusDescriptorElement );
				
		attr = document.createAttribute("linewidth");
		attr.setValue( String.valueOf( ((BasicStroke)getInfocusStroke()).getLineWidth() ) );
		strokeInfocusDescriptorElement.setAttributeNode(attr);	
		
		//
		//Inprocess Descriptor
		//
		Element inprocessDescriptorElement = document.createElement( "inprocess" );
		descriptorElement.appendChild( inprocessDescriptorElement );
		
		//Color - Normal - Descriptor
		Element colorInprocessDescriptorElement = document.createElement( "color" );
		inprocessDescriptorElement.appendChild( colorInprocessDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getInprocessColor().getGreen() ) );
		colorInprocessDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getInprocessColor().getRed() ) );
		colorInprocessDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getInprocessColor().getBlue() ) );
		colorInprocessDescriptorElement.setAttributeNode(attr);	
		
		//Background - Normal - Descriptor
		Element backgroundInprocessDescriptorElement = document.createElement( "background" );
		inprocessDescriptorElement.appendChild( backgroundInprocessDescriptorElement );
				
		attr = document.createAttribute("green");
		attr.setValue( String.valueOf( getInprocessBackgroundColor().getGreen() ) );
		backgroundInprocessDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("red");
		attr.setValue( String.valueOf( getInprocessBackgroundColor().getRed() ) );
		backgroundInprocessDescriptorElement.setAttributeNode(attr);	

		attr = document.createAttribute("blue");
		attr.setValue( String.valueOf( getInprocessBackgroundColor().getBlue() ) );
		backgroundInprocessDescriptorElement.setAttributeNode(attr);	

		//Stroke - Normal - Descriptor
		Element strokeInprocessDescriptorElement = document.createElement( "stroke" );
		inprocessDescriptorElement.appendChild( strokeInprocessDescriptorElement );
				
		attr = document.createAttribute("linewidth");
		attr.setValue( String.valueOf( ((BasicStroke)getInprocessStroke()).getLineWidth() ) );
		strokeInprocessDescriptorElement.setAttributeNode(attr);					
		
		return drawnBlockElement;
	}
}
