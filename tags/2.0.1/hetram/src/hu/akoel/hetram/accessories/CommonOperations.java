package hu.akoel.hetram.accessories;

import java.text.DecimalFormat;

public class CommonOperations {
	
	private static DecimalFormat df10 = new DecimalFormat("###.##########");
	private static DecimalFormat df3 = new DecimalFormat("###.###");
	private static DecimalFormat df2 = new DecimalFormat("###.##");
	
	public static double get10Decimals( double val ){
		return Double.valueOf(df10.format(val));
	}
	
	public static double get3Decimals( double val ){
		return Double.valueOf(df3.format(val));
	}
	
	public static double get2Decimals( double val ){
		return Double.valueOf( df2.format( val ) );
	}

}
