package hu.akoel.hetram.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColorSelector extends JComboBox{

	private static final long serialVersionUID = -5968583501958576573L;
	
	ImageIcon[] images;
	String[] petStrings = {"Bird", "Cat", "Dog", "Rabbit", "Pig"};
	    
	public ColorSelector(){
		super();
		
		 images = new ImageIcon[petStrings.length];
	        Integer[] intArray = new Integer[petStrings.length];
	        for (int i = 0; i < petStrings.length; i++) {
	            intArray[i] = new Integer(i);
images[i] = null;	            
//	            images[i] = createImageIcon("images/" + petStrings[i] + ".gif");
//	            if (images[i] != null) {
//	                images[i].setDescription(petStrings[i]);
//	            }
	        }
	
	        for( int i = 0; i < petStrings.length; i++){
	        	this.addItem(intArray[i]);
	        }
	        
	        ComboBoxRenderer renderer= new ComboBoxRenderer();
	        renderer.setPreferredSize(new Dimension(100, 20));
	        this.setRenderer(renderer);
	        this.setMaximumRowCount(6);
	 
	}
	
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		private Font uhOhFont;
		
		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		/*
		 * This method finds the image and text corresponding
		 * to the selected value and returns the label, set up
		 * to display the text and image.
		 */
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {

			//Get the selected index. (The index param isn't
			//always valid, so just use the value.)
			int selectedIndex = ((Integer)value).intValue();

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			//Set the icon and text.  If icon was null, say so.
			ImageIcon icon = images[selectedIndex];
			String pet = petStrings[selectedIndex];
			setIcon(icon);
			if (icon != null) {
				setText(pet);
				setFont(list.getFont());
			} else {
				setUhOhText(pet + " (no image available)",	list.getFont());
			}

			return this;
		}
		
		//Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
		
	}
	
}
