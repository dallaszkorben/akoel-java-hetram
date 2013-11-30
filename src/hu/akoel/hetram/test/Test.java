package hu.akoel.hetram.test;

import hu.akoel.hetram.gui.MainPanel;

import java.util.Locale;

public class Test{

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		new MainPanel();
	}	
}


