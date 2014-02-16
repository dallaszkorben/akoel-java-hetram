package hu.akoel.hetram.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class PatternSelector extends JComboBox<Integer>{

	private static final long serialVersionUID = -5968583501958576573L;

	private static final int PREFERED_WIDTH = 50;
	private static final int PREFERRED_HEIGHT = 12;
	private static final int ICON_WIDTH = 30;
	private static final int ICON_HEIGHT = 10;
	
	BufferedImage colorBufferedImage; 
	Graphics2D g2;
	ImageIcon icon;
	
	private ArrayList<ImageIcon> images = new ArrayList<ImageIcon>();	
	private ArrayList<RawPatternSelectorItem> patternFactoryList = new ArrayList<RawPatternSelectorItem>();
	    
	public PatternSelector(){
		super();		
		commonConstructor( new RawPatternSelectorItem[0] );
	}

	public PatternSelector( RawPatternSelectorItem[] patternFactoryList ){
		super();
		commonConstructor(patternFactoryList);
	}

	private void commonConstructor( RawPatternSelectorItem[] patternFactoryList ){
		
		for( RawPatternSelectorItem patternFactory: patternFactoryList ){
			addItem( patternFactory );
		}
			
        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension( PREFERED_WIDTH, PREFERRED_HEIGHT));
        this.setRenderer(renderer);
        this.setMaximumRowCount(6);
	}
	
	public void addItem( RawPatternSelectorItem patternFactory ){

		colorBufferedImage = new BufferedImage( ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_RGB); 
		g2 = colorBufferedImage.createGraphics();

		g2.setColor( Color.lightGray );
		g2.fillRect( 0, 0, ICON_WIDTH, ICON_HEIGHT );
		g2.setColor( Color.black );
		patternFactory.drawImageIcon( g2, ICON_WIDTH, ICON_HEIGHT );

		icon = new ImageIcon( colorBufferedImage );
		this.patternFactoryList.add( patternFactory );
		this.images.add( icon );
        this.addItem( this.images.size() -1 );
		
	}
	

	
	/**
	 * Szineket mint szinek jeleniti meg a ComboBox-ban
	 * 
	 * @author afoldvarszky
	 *
	 */
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		
		private static final long serialVersionUID = -1604564045340814374L;
		
//		private Font uhOhFont;
		
		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			if( null != value ){
			
			//Az eppen kivalasztott elem sorszama
			int selectedIndex = ((Integer)value).intValue();

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			icon = images.get( selectedIndex );
			setIcon(icon);

			//Letrehozza a szin ikont
//			BufferedImage colorBufferedImage = new BufferedImage( ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_RGB); 
//			Graphics2D g2 = colorBufferedImage.createGraphics();
//			g2.setColor( Color.red );
//			g2.fillRect( 0, 0, ICON_WIDTH, ICON_HEIGHT );
//			ImageIcon icon = new ImageIcon( colorBufferedImage );
			
/*			String pet = petStrings[selectedIndex];

			if (icon != null) {
				setText(pet);
				setFont(list.getFont());
			} else {
				setUhOhText(pet + " (no image available)",	list.getFont());
			}
*/
		}
			return this;
		}
		
/*		//Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
*/
		
	}
	
}
