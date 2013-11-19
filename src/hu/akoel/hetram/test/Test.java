package hu.akoel.hetram.test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

import hu.akoel.hetram.DThermicConnector;
import hu.akoel.hetram.Position;
import hu.akoel.hetram.ThermicConnector;
import hu.akoel.hetram.ThermicPoint;
import hu.akoel.hetram.ThermicPoint.Orientation;
import hu.akoel.hetram.ThermicPointList;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.crossline.CrossLine;
import hu.akoel.mgu.grid.Grid;
import hu.akoel.mgu.scale.Scale;
import hu.akoel.mgu.scale.ScaleChangeListener;
import hu.akoel.mgu.values.DeltaValue;
import hu.akoel.mgu.values.LengthValue;
import hu.akoel.mgu.values.PixelPerUnitValue;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.TranslateValue;
import hu.akoel.mgu.values.Value;

public class Test extends JFrame {

	private static final long serialVersionUID = 7824210627357949349L;

	ThermicPointList thermicPointList;

	// GCanvas parameterei
	private MCanvas myCanvas;
	private Color background = Color.black;
	private PossiblePixelPerUnits possiblePixelPerUnits = new PossiblePixelPerUnits(
			new PixelPerUnitValue(1, 1));
	private TranslateValue positionToMiddle = new TranslateValue(0, 0);

	// Grid parameterei
	private Grid myGrid;
	private Color gridColor = Color.green;
	private int gridWidth = 1;
	private DeltaValue gridDelta = new DeltaValue(0.1, 0.1);
	private Grid.PainterPosition gridPosition = Grid.PainterPosition.DEEPEST;
	private Grid.Type gridType = Grid.Type.DOT;

	// CrossLine parameterei
	private CrossLine myCrossLine;
	private PositionValue crossLinePosition = new PositionValue(0, 0);
	private Color crossLineColor = Color.red;
	private int crossLineWidthInPixel = 1;
	private Value crossLineLength = new LengthValue(0.05, 0.05);
	private CrossLine.PainterPosition crossLinePainterPosition = CrossLine.PainterPosition.DEEPEST;

	// Scale parameterei
	private Scale myScale;
	private double pixelPerCm = 42.1;
	private Scale.UNIT unit = Scale.UNIT.m;
	private double startScale = 10;
	private double rate = 1.2;

	private double lambda1 = 0.1;
	private double lambda2 = 0.45;
	private double lambda3 = 0.45;
	private double alfaE = 24;
	private double alfaI = 8;
	private double temperatureE = -13;
	private double temperatureI = 20;

	public static void main(String[] args) {
		new Test();
	}

	public Test() {

		thermicPointList = getResult();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Proba");
		this.setUndecorated(false);
		this.setSize(700, 600);
		this.createBufferStrategy(1);

		myCanvas = new MCanvas(BorderFactory.createLoweredBevelBorder(), background, possiblePixelPerUnits, positionToMiddle);
		myCanvas.addPainterListenerToHighest(new PainterListener() {

			@Override
			public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {
				double minimumTemperature = 0;
				double maximumTemperature = 0;
				double deltaTemperature;

				if (null != thermicPointList) {

					DecimalFormat numericFormat = new DecimalFormat("#.##");
					Font font = new Font("Default", Font.PLAIN, 14);
					FontRenderContext frc = g2.getFontRenderContext();

					// Megkeresi a minimalais es maximalis homersekletet
					for (int j = 0; j < thermicPointList.getSize(); j++) {
						minimumTemperature = Math.min(minimumTemperature, thermicPointList.get(j).getActualTemperature());
						maximumTemperature = Math.max(maximumTemperature, thermicPointList.get(j).getActualTemperature());
					}
					deltaTemperature = maximumTemperature - minimumTemperature;

					// Megkeresi a legnagyobb Delta-t ami a nyilak 100%-a lesz
					double delta = 0;
					ThermicConnector c;
					for (int j = 0; j < thermicPointList.getSize(); j++) {

						c = thermicPointList.get(j).getNorthThermicConnector();
						if (c instanceof DThermicConnector) {
							delta = Math.max(delta,	((DThermicConnector) c).getDelta());
						}

						c = thermicPointList.get(j).getEastThermicConnector();
						if (c instanceof DThermicConnector) {
							delta = Math.max(delta,	((DThermicConnector) c).getDelta());
						}
					}

					// Vegig a Termikus Pontokon
					for (int j = 0; j < thermicPointList.getSize(); j++) {

						// A pont geometriai elhelyezkedese
						Position position = thermicPointList.get(j).getPosition();

						double dNorth = 0;
						double dEast = 0;
						double dSouth = 0;
						double dWest = 0;
						
						c = thermicPointList.get(j).getNorthThermicConnector();
						if (c instanceof DThermicConnector) {
							if( !((DThermicConnector) c).isLoop() ){				
								dNorth = ((DThermicConnector) c).getDelta() / 2;
							}
						}

						c = thermicPointList.get(j).getEastThermicConnector();
						if (c instanceof DThermicConnector) {
							if( !((DThermicConnector) c).isLoop() ){				
								dEast = ((DThermicConnector) c).getDelta() / 2;
							}
						}

						c = thermicPointList.get(j).getSouthThermicConnector();
						if (c instanceof DThermicConnector) {
							if( !((DThermicConnector) c).isLoop() ){
								dSouth = ((DThermicConnector) c).getDelta() / 2;
							}
						}

						c = thermicPointList.get(j).getWestThermicConnector();
						if (c instanceof DThermicConnector) {
							if( !((DThermicConnector) c).isLoop() ){
								dWest = ((DThermicConnector) c).getDelta() / 2;
							}
						}

						double xStart = position.getX() - dWest;
						double yStart = position.getY() - dSouth;

						//g2.setStroke(new BasicStroke(1));
						g2.setColor(getRedBluByPercent((thermicPointList.get(j).getActualTemperature() - minimumTemperature) / deltaTemperature));
						g2.fillRectangle(xStart, yStart, dWest + dEast, dSouth + dNorth);

						//g2.setStroke(new BasicStroke(5));
						//g2.drawLine(position.getX(), position.getY(), position.getX(), position.getY());
						
						g2.setColor(Color.white);
						TextLayout textLayout = new
						TextLayout(String.valueOf( numericFormat.format(
						thermicPointList.get( j ).getActualTemperature() ) ),
						font, frc );
						g2.drawFont( textLayout, position.getX(),
						position.getY());
					}
				}
			}

			@Override
			public void paintByViewer(MCanvas canvas, Graphics2D g2) {
			}
		});

		myGrid = new Grid(myCanvas, gridType, gridColor, gridWidth,	gridPosition, gridDelta);

		myCrossLine = new CrossLine(myCanvas, crossLinePosition,
				crossLineColor, crossLineWidthInPixel, crossLineLength,
				crossLinePainterPosition);

		myScale = new Scale(myCanvas, pixelPerCm, unit, startScale, rate);
		myScale.addScaleChangeListener(new ScaleChangeListener() {

			@Override
			public void getScale(Value scale) {
				DecimalFormat df = new DecimalFormat("#.00");

				if (scale.getX() < 1.0) {
					// canvasControl.setStatusPanelXScale( "xM=" +
					// df.format(1/scale.getX() ) + ":1" );
				} else {
					// canvasControl.setStatusPanelXScale( "xM=1:" +
					// df.format(scale.getX() ) );
				}

				if (scale.getY() < 1.0) {
					// canvasControl.setStatusPanelYScale( "yM=" +
					// df.format(1/scale.getY() ) + ":1" );
				} else {
					// canvasControl.setStatusPanelYScale( "yM=1:" +
					// df.format(scale.getY() ) );
				}
			}
		});

		this.getContentPane().setLayout(new BorderLayout(10, 10));
		this.getContentPane().add(myCanvas, BorderLayout.CENTER);
		this.setVisible(true);

		

		
//		myCanvas.revalidateAndRepaintCoreCanvas();

/*		for (int i = 0; i < thermicPointList.getSize(); i++) {

			System.out.println(thermicPointList.get(i));
		}
*/		
	}

	private Color getRedBluByPercent(double percent) {

		int red = 0;
		int blue = 0;
		int maxLength = 255;

		int value = (int) Math.round(percent * maxLength);

		blue = 255 - value;
		red = value;

		return new Color(red, 0, blue);

	}

	private ThermicPointList getResult() {
		
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

		ThermicPoint T71 = new ThermicPoint(new Position(0.3, 0));
		T71.connectTo(T61, Orientation.WEST, lambda2);

		ThermicPoint T81 = new ThermicPoint(new Position(0.35, 0));
		T81.connectTo(T71, Orientation.WEST, lambda2);

		ThermicPoint T91 = new ThermicPoint(new Position(0.4, 0));
		T91.connectTo(T81, Orientation.WEST, lambda2);

		ThermicPoint T101 = new ThermicPoint(new Position(0.45, 0));
		T101.connectTo(T91, Orientation.WEST, lambda2);
		T101.connectTo(Orientation.EAST, alfaI, temperatureI);

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

		ThermicPoint T72 = new ThermicPoint(new Position(0.3, 0.05));
		T72.connectTo(T62, Orientation.WEST, lambda2);
		T72.connectTo(T71, Orientation.SOUTH, lambda2);

		ThermicPoint T82 = new ThermicPoint(new Position(0.35, 0.05));
		T82.connectTo(T72, Orientation.WEST, lambda2);
		T82.connectTo(T81, Orientation.SOUTH, lambda2);

		ThermicPoint T92 = new ThermicPoint(new Position(0.4, 0.05));
		T92.connectTo(T82, Orientation.WEST, lambda2);
		T92.connectTo(T91, Orientation.SOUTH, lambda2);

		ThermicPoint T102 = new ThermicPoint(new Position(0.45, 0.05));
		T102.connectTo(T92, Orientation.WEST, lambda2);
		T102.connectTo(T101, Orientation.SOUTH, lambda2);
		T102.connectTo(Orientation.EAST, alfaI, temperatureI);

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

		ThermicPoint T73 = new ThermicPoint(new Position(0.3, 0.1));
		T73.connectTo(T63, Orientation.WEST, lambda2);
		T73.connectTo(T72, Orientation.SOUTH, lambda2);

		ThermicPoint T83 = new ThermicPoint(new Position(0.35, 0.1));
		T83.connectTo(T73, Orientation.WEST, lambda3);
		T83.connectTo(T82, Orientation.SOUTH, lambda2);

		ThermicPoint T93 = new ThermicPoint(new Position(0.4, 0.1));
		T93.connectTo(T83, Orientation.WEST, lambda3);
		T93.connectTo(T92, Orientation.SOUTH, lambda2);

		ThermicPoint T103 = new ThermicPoint(new Position(0.45, 0.1));
		T103.connectTo(T93, Orientation.WEST, lambda3);
		T103.connectTo(T102, Orientation.SOUTH, lambda2);
		T103.connectTo(Orientation.EAST, alfaI, temperatureI);

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

		ThermicPoint T74 = new ThermicPoint(new Position(0.3, 0.15));
		T74.connectTo(T64, Orientation.WEST, lambda2);
		T74.connectTo(T73, Orientation.SOUTH, lambda3);

		ThermicPoint T84 = new ThermicPoint(new Position(0.35, 0.15));
		T84.connectTo(T74, Orientation.WEST, lambda3);
		T84.connectTo(T83, Orientation.SOUTH, lambda3);

		ThermicPoint T94 = new ThermicPoint(new Position(0.4, 0.15));
		T94.connectTo(T84, Orientation.WEST, lambda3);
		T94.connectTo(T93, Orientation.SOUTH, lambda3);

		ThermicPoint T104 = new ThermicPoint(new Position(0.45, 0.15));
		T104.connectTo(T94, Orientation.WEST, lambda3);
		T104.connectTo(T103, Orientation.SOUTH, lambda3);
		T104.connectTo(Orientation.EAST, alfaI, temperatureI);

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

		ThermicPoint T75 = new ThermicPoint(new Position(0.3, 0.2));
		T75.connectTo(T65, Orientation.WEST, lambda2);
		T75.connectTo(T74, Orientation.SOUTH, lambda2);

		ThermicPoint T85 = new ThermicPoint(new Position(0.35, 0.2));
		T85.connectTo(T75, Orientation.WEST, lambda2);
		T85.connectTo(T84, Orientation.SOUTH, lambda2);

		ThermicPoint T95 = new ThermicPoint(new Position(0.4, 0.2));
		T95.connectTo(T85, Orientation.WEST, lambda2);
		T95.connectTo(T94, Orientation.SOUTH, lambda2);

		ThermicPoint T105 = new ThermicPoint(new Position(0.45, 0.2));
		T105.connectTo(T95, Orientation.WEST, lambda2);
		T105.connectTo(T104, Orientation.SOUTH, lambda2);
		T105.connectTo(Orientation.EAST, alfaI, temperatureI);

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

		ThermicPoint T76 = new ThermicPoint(new Position(0.3, 0.25));
		T76.connectTo(T66, Orientation.WEST, lambda2);
		T76.connectTo(T75, Orientation.SOUTH, lambda2);
		T76.connectTo(T71, Orientation.NORTH, lambda2);

		ThermicPoint T86 = new ThermicPoint(new Position(0.35, 0.25));
		T86.connectTo(T76, Orientation.WEST, lambda2);
		T86.connectTo(T85, Orientation.SOUTH, lambda2);
		T86.connectTo(T81, Orientation.NORTH, lambda2);

		ThermicPoint T96 = new ThermicPoint(new Position(0.4, 0.25));
		T96.connectTo(T86, Orientation.WEST, lambda2);
		T96.connectTo(T95, Orientation.SOUTH, lambda2);
		T96.connectTo(T91, Orientation.NORTH, lambda2);

		ThermicPoint T106 = new ThermicPoint(new Position(0.45, 0.25));
		T106.connectTo(T96, Orientation.WEST, lambda2);
		T106.connectTo(T105, Orientation.SOUTH, lambda2);
		T106.connectTo(T101, Orientation.NORTH, lambda2);
		T106.connectTo(Orientation.EAST, alfaI, temperatureI);

		ThermicPointList list = new ThermicPointList(60);

		list.add(T11);
		list.add(T21);
		list.add(T31);
		list.add(T41);
		list.add(T51);
		list.add(T61);
		list.add(T71);
		list.add(T81);
		list.add(T91);
		list.add(T101);

		list.add(T12);
		list.add(T22);
		list.add(T32);
		list.add(T42);
		list.add(T52);
		list.add(T62);
		list.add(T72);
		list.add(T82);
		list.add(T92);
		list.add(T102);

		list.add(T13);
		list.add(T23);
		list.add(T33);
		list.add(T43);
		list.add(T53);
		list.add(T63);
		list.add(T73);
		list.add(T83);
		list.add(T93);
		list.add(T103);

		list.add(T14);
		list.add(T24);
		list.add(T34);
		list.add(T44);
		list.add(T54);
		list.add(T64);
		list.add(T74);
		list.add(T84);
		list.add(T94);
		list.add(T104);

		list.add(T15);
		list.add(T25);
		list.add(T35);
		list.add(T45);
		list.add(T55);
		list.add(T65);
		list.add(T75);
		list.add(T85);
		list.add(T95);
		list.add(T105);

		list.add(T16);
		list.add(T26);
		list.add(T36);
		list.add(T46);
		list.add(T56);
		list.add(T66);
		list.add(T76);
		list.add(T86);
		list.add(T96);
		list.add(T106);

		list.solve(0.001);

		return list;
	}

}
