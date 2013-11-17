package hu.akoel.hetram.test;

import hu.akoel.hetram.Position;
import hu.akoel.hetram.ThermicPoint;
import hu.akoel.hetram.ThermicPoint.Orientation;
import hu.akoel.hetram.ThermicPointList;

public class Test {

	public static void main(String[] args) {
		new Test();
	}

	public Test() {
		double lambda1 = 0.45;
		double lambda2 = 0.03;
		double alfaE = 24;
		double alfaI = 8;
		double temperatureE = 0;
		double temperatureI = 20;

		// 1. sor
		ThermicPoint T11 = new ThermicPoint(new Position(0, 0.));
		T11.connectTo(Orientation.WEST, alfaE, temperatureE);

		ThermicPoint T21 = new ThermicPoint(new Position(0.05, 0));
		T21.connectTo(T11, Orientation.WEST, lambda1);

		ThermicPoint T31 = new ThermicPoint(new Position(0.1, 0));
		T31.connectTo(T21, Orientation.WEST, lambda1);

		ThermicPoint T41 = new ThermicPoint(new Position(0.15, 0));
		T41.connectTo(T31, Orientation.WEST, lambda2);

		ThermicPoint T51 = new ThermicPoint(new Position(0.2, 0));
		T51.connectTo(T41, Orientation.WEST, lambda2);

		ThermicPoint T61 = new ThermicPoint(new Position(0.25, 0));
		T61.connectTo(T51, Orientation.WEST, lambda2);
		T61.connectTo(Orientation.EAST, alfaI, temperatureI);

		// 2. sor
		ThermicPoint T12 = new ThermicPoint(new Position(0, 0.05));
		T12.connectTo(Orientation.WEST, alfaE, temperatureE);
		T12.connectTo(T11, Orientation.SOUTH, lambda1);

		ThermicPoint T22 = new ThermicPoint(new Position(0.05, 0.05));
		T22.connectTo(T12, Orientation.WEST, lambda1);
		T22.connectTo(T21, Orientation.SOUTH, lambda1);

		ThermicPoint T32 = new ThermicPoint(new Position(0.1, 0.05));
		T32.connectTo(T22, Orientation.WEST, lambda1);
		T32.connectTo(T31, Orientation.SOUTH, lambda1);

		ThermicPoint T42 = new ThermicPoint(new Position(0.15, 0.05));
		T42.connectTo(T32, Orientation.WEST, lambda2);
		T42.connectTo(T41, Orientation.SOUTH, lambda2);

		ThermicPoint T52 = new ThermicPoint(new Position(0.2, 0.05));
		T52.connectTo(T42, Orientation.WEST, lambda2);
		T52.connectTo(T51, Orientation.SOUTH, lambda2);

		ThermicPoint T62 = new ThermicPoint(new Position(0.25, 0.05));
		T62.connectTo(T52, Orientation.WEST, lambda2);
		T62.connectTo(T61, Orientation.SOUTH, lambda2);
		T62.connectTo(Orientation.EAST, alfaI, temperatureI);
		

		// 3. sor
		ThermicPoint T13 = new ThermicPoint(new Position(0, 0.1));
		T13.connectTo(T12, Orientation.SOUTH, lambda1);
		T13.connectTo(Orientation.WEST, alfaE, temperatureE);

		ThermicPoint T23 = new ThermicPoint(new Position(0.05, 0.1));
		T23.connectTo(T13, Orientation.WEST, lambda1);
		T23.connectTo(T22, Orientation.SOUTH, lambda1);

		ThermicPoint T33 = new ThermicPoint(new Position(0.1, 0.1));
		T33.connectTo(T23, Orientation.WEST, lambda1);
		T33.connectTo(T32, Orientation.SOUTH, lambda1);

		ThermicPoint T43 = new ThermicPoint(new Position(0.15, 0.1));
		T43.connectTo(T33, Orientation.WEST, lambda2);
		T43.connectTo(T42, Orientation.SOUTH, lambda2);

		ThermicPoint T53 = new ThermicPoint(new Position(0.2, 0.1));
		T53.connectTo(T43, Orientation.WEST, lambda2);
		T53.connectTo(T52, Orientation.SOUTH, lambda2);

		ThermicPoint T63 = new ThermicPoint(new Position(0.25, 0.1));
		T63.connectTo(T53, Orientation.WEST, lambda2);
		T63.connectTo(T62, Orientation.SOUTH, lambda2);
		T63.connectTo(Orientation.EAST, alfaI, temperatureI);

		// 4. sor
		ThermicPoint T14 = new ThermicPoint(new Position(0, 0.15));
		T14.connectTo(T13, Orientation.SOUTH, lambda1);
		T14.connectTo(Orientation.WEST, alfaE, temperatureE);

		ThermicPoint T24 = new ThermicPoint(new Position(0.05, 0.15));
		T24.connectTo(T14, Orientation.WEST, lambda1);
		T24.connectTo(T23, Orientation.SOUTH, lambda1);

		ThermicPoint T34 = new ThermicPoint(new Position(0.1, 0.15));
		T34.connectTo(T24, Orientation.WEST, lambda1);
		T34.connectTo(T33, Orientation.SOUTH, lambda1);

		ThermicPoint T44 = new ThermicPoint(new Position(0.15, 0.15));
		T44.connectTo(T34, Orientation.WEST, lambda2);
		T44.connectTo(T43, Orientation.SOUTH, lambda2);

		ThermicPoint T54 = new ThermicPoint(new Position(0.2, 0.15));
		T54.connectTo(T44, Orientation.WEST, lambda2);
		T54.connectTo(T53, Orientation.SOUTH, lambda2);

		ThermicPoint T64 = new ThermicPoint(new Position(0.25, 0.15));
		T64.connectTo(T54, Orientation.WEST, lambda2);
		T64.connectTo(T63, Orientation.SOUTH, lambda2);
		T64.connectTo(Orientation.EAST, alfaI, temperatureI);

		// 5. sor
		ThermicPoint T15 = new ThermicPoint(new Position(0, 0.2));
		T15.connectTo(T14, Orientation.SOUTH, lambda1);
		T15.connectTo(Orientation.WEST, alfaE, temperatureE);

		ThermicPoint T25 = new ThermicPoint(new Position(0.05, 0.2));
		T25.connectTo(T15, Orientation.WEST, lambda1);
		T25.connectTo(T24, Orientation.SOUTH, lambda1);

		ThermicPoint T35 = new ThermicPoint(new Position(0.1, 0.2));
		T35.connectTo(T25, Orientation.WEST, lambda1);
		T35.connectTo(T34, Orientation.SOUTH, lambda1);

		ThermicPoint T45 = new ThermicPoint(new Position(0.15, 0.2));
		T45.connectTo(T35, Orientation.WEST, lambda2);
		T45.connectTo(T44, Orientation.SOUTH, lambda2);

		ThermicPoint T55 = new ThermicPoint(new Position(0.2, 0.2));
		T55.connectTo(T45, Orientation.WEST, lambda2);
		T55.connectTo(T54, Orientation.SOUTH, lambda2);

		ThermicPoint T65 = new ThermicPoint(new Position(0.25, 0.2));
		T65.connectTo(T55, Orientation.WEST, lambda2);
		T65.connectTo(T64, Orientation.SOUTH, lambda2);
		T65.connectTo(Orientation.EAST, alfaI, temperatureI);

		// 6. sor
		ThermicPoint T16 = new ThermicPoint(new Position(0, 0.25));
		T16.connectTo(T15, Orientation.SOUTH, lambda1);
		T16.connectTo(T11, Orientation.NORTH, lambda1);
		T16.connectTo(Orientation.WEST, alfaE, temperatureE);		

		ThermicPoint T26 = new ThermicPoint(new Position(0.05, 0.25));
		T26.connectTo(T16, Orientation.WEST, lambda1);
		T26.connectTo(T25, Orientation.SOUTH, lambda1);
		T26.connectTo(T21, Orientation.NORTH, lambda1);

		ThermicPoint T36 = new ThermicPoint(new Position(0.1, 0.25));
		T36.connectTo(T26, Orientation.WEST, lambda1);
		T36.connectTo(T35, Orientation.SOUTH, lambda1);
		T36.connectTo(T31, Orientation.NORTH, lambda1);

		ThermicPoint T46 = new ThermicPoint(new Position(0.15, 0.25));
		T46.connectTo(T36, Orientation.WEST, lambda2);
		T46.connectTo(T45, Orientation.SOUTH, lambda2);
		T46.connectTo(T41, Orientation.NORTH, lambda2);

		ThermicPoint T56 = new ThermicPoint(new Position(0.2, 0.25));
		T56.connectTo(T46, Orientation.WEST, lambda2);
		T56.connectTo(T55, Orientation.SOUTH, lambda2);
		T56.connectTo(T51, Orientation.NORTH, lambda2);

		ThermicPoint T66 = new ThermicPoint(new Position(0.25, 0.25));
		T66.connectTo(T56, Orientation.WEST, lambda2);
		T66.connectTo(T65, Orientation.SOUTH, lambda2);
		T66.connectTo(T61, Orientation.NORTH, lambda2);
		T66.connectTo(Orientation.EAST, alfaI, temperatureI);

		ThermicPointList list = new ThermicPointList(36);
		
		list.add( T11 );
		list.add( T21 );
		list.add( T31 );
		list.add( T41 );
		list.add( T51 );
		list.add( T61 );
		
		list.add( T12 );
		list.add( T22 );
		list.add( T32 );
		list.add( T42 );
		list.add( T52 );
		list.add( T62 );
		
		list.add( T13 );
		list.add( T23 );
		list.add( T33 );
		list.add( T43 );
		list.add( T53 );
		list.add( T63 );
		
		list.add( T14 );
		list.add( T24 );
		list.add( T34 );
		list.add( T44 );
		list.add( T54 );
		list.add( T64 );
		
		list.add( T15 );
		list.add( T25 );
		list.add( T35 );
		list.add( T45 );
		list.add( T55 );
		list.add( T65 );
		
		list.add( T16 );
		list.add( T26 );
		list.add( T36 );
		list.add( T46 );
		list.add( T56 );
		list.add( T66 );
		
		for( int i = 1; i < 100; i ++ ){
			list.doIteration();
			//System.err.println( T11.toString() );
		}
		for( int i = 0; i < list.getSize(); i++ ){
			System.out.println( list.get(i) );
		}
		
	}
}
