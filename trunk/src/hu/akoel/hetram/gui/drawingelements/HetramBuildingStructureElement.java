package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.math.BigDecimal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class HetramBuildingStructureElement extends HetramDrawnElement{
	
	private final Stroke NORMAL_STROKE = new BasicStroke(1);
	
	private final Color SELECTED_COLOR = Color.red;
//	private final Color SELECTED_BACKGROUND = Color.yellow;
	private final Stroke SELECTED_STROKE = new BasicStroke(1);

	//private static final Color INPROCESS_COLOR = Color.yellow;
	//private static final Color INPROCESS_BACKGROUND = Color.gray;
	private final Stroke INPROCESS_STROKE = new BasicStroke(3);
	
	private final Color INFOCUS_COLOR = Color.yellow;
	//private static final Color INFOCUS_BACKGROUND = Color.gray;
	private final Stroke INFOCUS_STROKE = new BasicStroke(1);
		
	private double lambda;

	private HetramBuildingStructureElement( Status status, BigDecimal x1, BigDecimal y1 ){
		super( status, x1, y1 );
	}	
	
	public HetramBuildingStructureElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double lambda, Color lineColor, Color backgroundColor ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth );
	
		commonConstructor(lambda, lineColor, backgroundColor);

	}

	public HetramBuildingStructureElement( Status status, BigDecimal x1, BigDecimal y1, double lambda, Color lineColor, Color backgroundColor ){
		super( status, x1, y1 );
		
		commonConstructor(lambda, lineColor, backgroundColor);

	}

	public HetramBuildingStructureElement( Element xmlElement ){
		super( xmlElement );
		
		NodeList mainNodeList = xmlElement.getChildNodes();
		for (int i = 0; i < mainNodeList.getLength(); i++) {
			
			Node mainNode = mainNodeList.item( i );
			
			if (mainNode.getNodeType() == Node.ELEMENT_NODE) {
				Element mainElement = (Element) mainNode;
				
				//Ha egy APLHASTART elemrol van szo
				if( mainElement.getNodeName().equals( "extrainfo" )){
					
					NodeList extrainfoNodeList = mainElement.getChildNodes();
					for (int j = 0; j < extrainfoNodeList.getLength(); j++) {
						
						Node extrainfoNode = extrainfoNodeList.item( j );
						
						if (extrainfoNode.getNodeType() == Node.ELEMENT_NODE) {
							Element extrainfoElement = (Element) extrainfoNode;

							//Ha egy APLHASTART elemrol van szo
							if( extrainfoElement.getNodeName().equals( "lambda" )){
								this.lambda = Double.valueOf( extrainfoElement.getTextContent() );
							}							
						}
					} 
				}
			}
		}
	}
	
	
	private void commonConstructor( double lambda, Color lineColor, Color backgroundColor ){
		this.lambda = lambda;
		
		setNormal( lineColor, NORMAL_STROKE, backgroundColor );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, backgroundColor );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, backgroundColor );
		setInprocess( lineColor, INPROCESS_STROKE, backgroundColor );
	}
	
	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public Element getXMLElement( Document document ){
		Element element = super.getXMLElement(document);

		//Buildingsturcure
		Element extrainfoElement = document.createElement( "extrainfo" );
		element.appendChild( extrainfoElement );

		//Buildingsturcture - lambda
		Element lambdaElement = document.createElement( "lambda" );		
		lambdaElement.appendChild(document.createTextNode( Double.toString( this.lambda ) ) );
		extrainfoElement.appendChild( lambdaElement );
		
		return element;		
	}
}
