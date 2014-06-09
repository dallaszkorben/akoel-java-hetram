package hu.akoel.mgu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColorSelector extends JComboBox<Integer>{

	private static final long serialVersionUID = -5968583501958576573L;

	private static final int PREFERED_WIDTH = 50;
	private static final int PREFERRED_HEIGHT = 12;
	private static final int ICON_WIDTH = 30;
	private static final int ICON_HEIGHT = 10;
	
	BufferedImage colorBufferedImage; 
	Graphics2D g2;
	ImageIcon icon;
	
	ImageIcon[] images;
	
	private static enum DefaultColor implements ColorForSelectorInterface{
		BLACK( Color.black, "Black"),
		WHITE( Color.white, "White"),
		
		
		
	
		
		
		BLUE( Color.blue, "Blue"),
		DEEPSKYBLUE( new Color(0, 191, 255), "DeepSkyBlue" ),		
		LIGHTSKYBLUE( new Color(135, 206, 250), "LightSkyBlue" ),
		TURQUOISE( new Color(64, 224, 208), "Turquoise" ),
		AQUAMARINE( new Color(127, 255, 212), "Aquamarine" ),		
		CYAN( Color.cyan, "Cyan"), 
		
		SEEREEN( new Color(46, 139, 87), "SeeGreen" ),
		YELLOGREEN( new Color(154, 205, 50), "YellowGreen" ),	
		GREEN( Color.green, "Green"),
		LIGHTGREEN( new Color(144, 238, 144), "LightGreen" ),
		
		
		YELLOW( Color.yellow, "Yellow"),
		LIGHTGOLDENYELLOW( new Color(250, 250, 210), "LightGoldenYellow" ),	
		
		
		BRAUN( new Color(184, 92, 0), "Braun" ),		
		CORAL( new Color(255, 127, 80), "Coral" ),
		ORANGE( new Color(255, 165, 0), "Orange" ),
		
		PINK( new Color(255, 192, 203), "Pink" ),
		PLUM( new Color(221, 160, 221), "Plum" ),
		BLUEVIOLET( new Color(138, 43, 226), "BlueViolet" ),
		MAGENTA( Color.magenta, "Magenta" ),
		
		RED( Color.red, "Red"),
			
		;
		
		private Color color;
		private String name;
		
		DefaultColor( Color color, String name ){
			this.color = color;
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public Color getColor(){
			return color;
		}
		
	}
	
	private ColorForSelectorInterface[] colorList;
	    
	public ColorSelector(){
		super();
		
		commonConstructor( DefaultColor.values() );
	}
	
	public ColorSelector( ColorForSelectorInterface[] cl ){
		super();
		
		commonConstructor( cl );
	}
	
	private void commonConstructor( ColorForSelectorInterface[] colorList ){
		
		this.colorList = colorList;
		
		//Most gyartja le a szineket
		images = new ImageIcon[ colorList.length ];
		Integer[] intArray = new Integer[ colorList.length ];
        for (int i = 0; i < colorList.length; i++) {
        	intArray[i] = new Integer(i);
        	this.addItem( intArray[i] );
        	
        	colorBufferedImage = new BufferedImage( ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_RGB); 
			g2 = colorBufferedImage.createGraphics();
			g2.setColor( colorList[ i ].getColor() );
			g2.fillRect( 0, 0, ICON_WIDTH, ICON_HEIGHT );
			icon = new ImageIcon( colorBufferedImage );
			images[ i ] = icon;
        }
	
        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension( PREFERED_WIDTH, PREFERRED_HEIGHT));
        this.setRenderer(renderer);
        this.setMaximumRowCount(21);
	}
	

	/**
	 * Beallitja hogy a megadott szin legyen kivalasztva a listabol
	 * Ha nem talalja a szint, akkor marad az utljara kivalasztotton
	 * 
	 * @param color
	 */
	public void setSelectedItem( Color color ){
		for( int i = 0; i < colorList.length; i++ ){
			if( color.equals( colorList[i].getColor() ) ){
				this.setSelectedIndex( i );
				break;
			}
		}
	}
	
	/**
	 * Visszaadja a kivalasztott szin Color objektumat
	 * 
	 * @return
	 */
	public Color getSelectedColor(){
		return colorList [this.getSelectedIndex()].getColor();
	}
	
	/**
	 * Visszaadja a kivalasztott szin nevet
	 * 
	 * @return
	 */
	public String getSelectedName(){
		return colorList [this.getSelectedIndex()].getName();
	}
	
	/**
	 * Szineket mint szinek jeleniti meg a ComboBox-ban
	 * 
	 * @author afoldvarszky
	 *
	 */
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		
		private static final long serialVersionUID = -1604564045340814374L;

		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			//Az eppen kivalasztott elem sorszama
			int selectedIndex = ((Integer)value).intValue();

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			icon = images[ selectedIndex ];
			setIcon(icon);

			return this;
		}		
	}	
}
