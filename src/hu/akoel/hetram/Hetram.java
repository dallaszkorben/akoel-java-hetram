package hu.akoel.hetram;

import hu.akoel.hetram.gui.MainPanel;

import java.util.Locale;

import javax.swing.SwingUtilities;

public class Hetram{

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		final String version = "2.0.0";
		
		SwingUtilities.invokeLater( new Runnable(){

			@Override
			public void run() {
				new MainPanel( version );				
			}			
		});		
	}	
}


