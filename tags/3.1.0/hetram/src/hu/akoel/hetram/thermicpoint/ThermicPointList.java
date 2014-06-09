package hu.akoel.hetram.thermicpoint;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.accessories.ColorTransient;
import hu.akoel.hetram.accessories.CommonOperations;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
import hu.akoel.hetram.connectors.SymmetricEdgeThermicConnector;
import hu.akoel.hetram.connectors.IThermicConnector;
import hu.akoel.hetram.connectors.XThermicPointThermicConnector;
import hu.akoel.hetram.connectors.YThermicPointThermicConnector;
import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.hetram.gui.MainPanel.Mode;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import javax.swing.SwingUtilities;

public class ThermicPointList {

	// Gauss iteracio kezdeti erteke
	private static final double DEFAULT_TEMPERATURE = -1;
	private ThermicPoint[] list;

	private int position = 0;
	private SolveThread solveThread = null;
	private CURRENT_TYPE currentType = CURRENT_TYPE.TRAJECTORY;
	private ColorTransient colorTransient;
	
	private BigDecimal dx;
	private BigDecimal dy;
	private double dxdouble;
	private double dydouble;

	public static enum CURRENT_TYPE {
		VECTORPAIR, VECTOR, TRAJECTORY
	}

	public ThermicPointList(Collection<ThermicPoint> thermicPointCollection, BigDecimal dx, BigDecimal dy ) {		
		
		this.dx = dx;
		this.dy = dy;
		this.dxdouble = dx.doubleValue();
		this.dydouble = dy.doubleValue();
		
		colorTransient = new ColorTransient();

		list = new ThermicPoint[thermicPointCollection.size()];

		for (ThermicPoint tp : thermicPointCollection) {
			add(tp);
		}
	}

	/**
	 * Termikus Pont hozzaadasa a listahoz
	 * 
	 * @param thermicPoint
	 */
	private void add(ThermicPoint thermicPoint) {

		// A Termikus pont lista-poziciojanak beallitasa
		thermicPoint.setPositionInTheList(position);

		// A Termikus pont Kezdeti erteke - Gauss iteracio kezdeti erteke
		thermicPoint.setActualTemperature(DEFAULT_TEMPERATURE);

		// A Termikus Pont elhelyezese a listaban
		list[position] = thermicPoint;

		// Lista mutatojanak novelese
		position++;
	}

	public void setCurrentType(CURRENT_TYPE currentType) {
		this.currentType = currentType;
	}

	public CURRENT_TYPE getCurrentType() {
		return this.currentType;
	}

	/**
	 * Visszaadja a parameterkent megadott poziciohoz tartozo Termikus Pontot
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public ThermicPoint getThermicPointByPosition(double x, double y) {

		if (this.getSize() > 0) {

			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				BigDecimalPosition ps = this.get(j).getPosition();
				Position position = new Position(ps.getX().doubleValue(), ps.getY().doubleValue());

				if (y <= position.getY() + dydouble/2 && y >= position.getY() - dydouble/2 && x <= position.getX() + dxdouble/2 && x >= position.getX() - dxdouble/2) {
					return this.get(j);
				}
			}
		}
		return null;
	}

	/**
	 * Egy kitoltott korrel reprezentalja az egyes ThermicPoint-okat
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawPoint(MCanvas canvas, MGraphics g2, Color thermicPointColor, double thermicPointRadius) {

		// Termikus pontok megjelenitese
		for (int j = 0; j < this.getSize(); j++) {

			// A pont geometriai elhelyezkedese
			BigDecimalPosition position = this.get(j).getPosition();

			g2.setColor(thermicPointColor);
			g2.fillOval(position.getX().doubleValue(), position.getY().doubleValue(), thermicPointRadius);

		}
	}

	/**
	 * Az egyes ThermicPoint-ok homersekletet irja ki a pontok fole
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawPointTemperatureByFont(DrawnBlockCanvas canvas, MGraphics g2) {

		// Ha vannak termikus pontjaim
		if (this.getSize() > 0) {

			IThermicConnector c;

			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				BigDecimal delta = new BigDecimal(10);

/*				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					delta = ((XThermicPointThermicConnector) c).getDelta();
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					delta = ((XThermicPointThermicConnector) c).getDelta();
				}
*/
				// A pont geometriai elhelyezkedese
				BigDecimalPosition position = this.get(j).getPosition();

				int fontSize = canvas.getPixelXLengthByWorld(dydouble) / 4;
				Font font = new Font("Default", Font.PLAIN, fontSize);
				FontRenderContext frc = g2.getFontRenderContext();

				g2.setColor(Color.white);
				TextLayout textLayout = new TextLayout(String.valueOf( CommonOperations.get3Decimals(this.get(j).getActualTemperature()) ), font, frc);
				// TextLayout textLayout = new TextLayout(String.valueOf(
				// CommonOperations.get2Decimals( this.get( j
				// ).getActualTemperature() ) ), font, frc );
				g2.drawFont(textLayout, position.getX().doubleValue(), position.getY().doubleValue());
				
			}
		}
	}

	/**
	 * Nyilakkal reprezentalja az egyes ThermicPoint-ok kozott fellepo hoaramot
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawCurrent(MCanvas canvas, MGraphics g2) {

		double maximumCurrent = 0;
		double maxDeltaY = 0;
		double maxDeltaX = 0;

		// Ha vannak termikus pontjaim
		if (this.getSize() > 0) {

			IThermicConnector c;

			//
			// Maximalis hoaram szamitasa a maximalis hosszusagu nyil
			// megallapitasahoz
			//
			for (int j = 0; j < this.getSize(); j++) {

				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					maxDeltaY = Math.max(maxDeltaY, Math.abs(dydouble));
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					maxDeltaX = Math.max(maxDeltaY, Math.abs(dxdouble));
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					maxDeltaY = Math.max(maxDeltaY, Math.abs(dydouble));
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					maxDeltaX = Math.max(maxDeltaX, Math.abs(dxdouble));
				}

				if (null != this.get(j).getEastCurrent())
					maximumCurrent = Math.max(maximumCurrent, Math.abs(this.get(j).getEastCurrent()));
				if (null != this.get(j).getWestCurrent())
					maximumCurrent = Math.max(maximumCurrent, Math.abs(this.get(j).getWestCurrent()));
				if (null != this.get(j).getNorthCurrent())
					maximumCurrent = Math.max(maximumCurrent, Math.abs(this.get(j).getNorthCurrent()));
				if (null != this.get(j).getSouthCurrent())
					maximumCurrent = Math.max(maximumCurrent, Math.abs(this.get(j).getSouthCurrent()));

			}

			double maxLength = Math.max(maxDeltaX, maxDeltaY) * 0.9;
			double vY = 0;
			double vX = 0;
			double yLengthPercentage = 0;
			double xLengthPercentage = 0;

			//
			// Nyilak elhelyezese a Termikus pontokba
			//
			for (int j = 0; j < this.getSize(); j++) {

				// A pont geometriai elhelyezkedese
				// BigDecimalPosition position = this.get(j).getPosition();
				BigDecimalPosition ps = this.get(j).getPosition();
				Position position = new Position(ps.getX().doubleValue(), ps.getY().doubleValue());

				vX = position.getX();
				vY = position.getY();
				yLengthPercentage = 0;
				Double current;
				double arrowLength;

				// ----------
				//
				// NORTH
				//
				// ----------
				current = this.get(j).getNorthCurrent();
				c = this.get(j).getNorthThermicConnector();

				// Ebbol a pontbol mutat NORTH fele
				if (current > 0) {

					yLengthPercentage = Math.abs(current / maximumCurrent);

					if (c instanceof YThermicPointThermicConnector) {

						vY = position.getY() + yLengthPercentage * dydouble;

					// Szabad feluletu pont
					} else if (c instanceof OpenEdgeThermicConnector) {

						vY = position.getY() + yLengthPercentage * maxDeltaY;
					}

				}

				// ---------------
				//
				// SOUTH
				//
				// ---------------
				current = this.get(j).getSouthCurrent();
				c = this.get(j).getSouthThermicConnector();

				// Ebbol a pontbol mutat SOUTH fele
				if (current > 0) {

					yLengthPercentage = Math.abs(current / maximumCurrent);

					if (c instanceof YThermicPointThermicConnector) {

						vY = position.getY() - yLengthPercentage * dydouble;

						// Szabad feluletu pont
					} else if (c instanceof OpenEdgeThermicConnector) {

						vY = position.getY() - yLengthPercentage * maxDeltaY;

					}

				}

				// ----------
				//
				// EAST
				//
				// ----------
				current = this.get(j).getEastCurrent();
				c = this.get(j).getEastThermicConnector();

				// Ebbol a pontbol mutat EAST fele
				if (current > 0) {

					xLengthPercentage = Math.abs(current / maximumCurrent);

					// Normal termikus pont
					if (c instanceof XThermicPointThermicConnector) {

						vX = position.getX() + xLengthPercentage * dxdouble;

						// Szabad feluletu pont
					} else if (c instanceof OpenEdgeThermicConnector) {

						vX = position.getX() + xLengthPercentage * maxDeltaX;

					}

				}

				// ----------
				//
				// WEST
				//
				// ----------
				current = this.get(j).getWestCurrent();
				c = this.get(j).getWestThermicConnector();

				// Ebbol a pontbol mutat WEST fele
				if (current > 0) {

					xLengthPercentage = Math.abs(current / maximumCurrent);

					// Normal termikus pont
					if (c instanceof XThermicPointThermicConnector) {

						vX = position.getX() - xLengthPercentage * dxdouble;

						// Szabad feluletu pont
					} else if (c instanceof OpenEdgeThermicConnector) {

						vX = position.getX() - xLengthPercentage * maxDeltaX;
					}

				}

				// ----------------------
				//
				// MEGJELENITES
				//
				// ----------------------

				// g2.setColor( getWhiteBlack( yLengthPercentage ) );
				g2.setColor(Color.white);
				g2.setStroke(new BasicStroke(1));

				//
				// Vektropar kirajzolasa
				//
				if (currentType.equals(CURRENT_TYPE.VECTORPAIR)) {

					// Fuggoleges nyil szara
					g2.drawLine(position.getX(), position.getY(), position.getX(), vY);

					// Fuggoleges nyil hegye
					arrowLength = (vY - position.getY()) / 4;
					g2.drawLine(position.getX(), vY, position.getX() + arrowLength / 2, vY - arrowLength);
					g2.drawLine(position.getX(), vY, position.getX() - arrowLength / 2, vY - arrowLength);

					// Vizszintes nyil szara
					g2.drawLine(position.getX(), position.getY(), vX, position.getY());

					// Vizszintes nyil hegye
					arrowLength = (vX - position.getX()) / 4;
					g2.drawLine(vX, position.getY(), vX - arrowLength, position.getY() + arrowLength / 2);
					g2.drawLine(vX, position.getY(), vX - arrowLength, position.getY() - arrowLength / 2);

					//
					// Vektor kirajzolas
					//
				} else if (currentType.equals(CURRENT_TYPE.VECTOR)) {

					// Vektor iranyanak kirajzolasa
					g2.drawLine(position.getX(), position.getY(), vX, vY);

					// Vektor nyil hegye
					arrowLength = Math.sqrt((vX - position.getX()) * (vX - position.getX()) + (vY - position.getY()) * (vY - position.getY())) / 4;
					Path2D.Double path = new Path2D.Double();
					path.moveTo(vX - arrowLength / 2, vY - arrowLength);
					path.lineTo(vX, vY);
					path.lineTo(vX + arrowLength / 2, vY - arrowLength);
					AffineTransform at = new AffineTransform();

					double theta = Math.atan2((vY - position.getY()), (vX - position.getX()));
					at.rotate(theta - Math.PI / 2d, vX, vY);
					path.transform(at);
					g2.drawPath(path);

					//
					// Trajektoria kirajzolsa
					//
				} else if (currentType.equals(CURRENT_TYPE.TRAJECTORY)) {

					Vector2D vector = new Vector2D(vX - position.getX(), vY - position.getY());

					// Minden trajektoria vonal egyforma hosszu
					vector = vector.getVector( maxLength );

					Path2D.Double trajektoriaPath = new Path2D.Double();
					trajektoriaPath.moveTo(position.getX() - vector.x / 2, position.getY() - vector.y / 2);
					trajektoriaPath.lineTo(position.getX() + vector.x / 2, position.getY() + vector.y / 2);

					AffineTransform trajektoriaAT = new AffineTransform();

					trajektoriaAT.rotate(Math.PI / 2d, position.getX(), position.getY());
					trajektoriaPath.transform(trajektoriaAT);
					g2.drawPath(trajektoriaPath);
				}
			}
		}
	}

	/**
	 * Szinekkel reprezentalja az egyes ThermicPoint pontok homersekletet a
	 * pontok geometriai poziciojaban es korulotte delta tavolsagban
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawTemperatureByColor(MCanvas canvas, MGraphics g2) {
		double minimumTemperature = 0;
		double maximumTemperature = 0;
		double deltaTemperature;

		// Ha vannak termikus pontjaim
		if (this.getSize() > 0) {

			// Megkeresi a minimalais es maximalis homersekletet
			for (int j = 0; j < this.getSize(); j++) {
				minimumTemperature = Math.min(minimumTemperature, this.get(j).getActualTemperature());
				maximumTemperature = Math.max(maximumTemperature, this.get(j).getActualTemperature());
			}
			deltaTemperature = maximumTemperature - minimumTemperature;

			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				// A pont geometriai elhelyezkedese
				BigDecimalPosition ps = this.get(j).getPosition();
				//Position position = new Position(ps.getX().doubleValue(), ps.getY().doubleValue());

				BigDecimal xStart = ps.getX().subtract(this.dx.divide( new BigDecimal("2"),RoundingMode.HALF_UP) );
				BigDecimal yStart = ps.getY().subtract(this.dy.divide( new BigDecimal("2"),RoundingMode.HALF_UP) );

				// A Termikus Ponthoz tartozo szin
				// g2.setColor(getRedBluByPercent((this.get(j).getActualTemperature()
				// - minimumTemperature) / deltaTemperature));
				g2.setColor(colorTransient.getColor((this.get(j).getActualTemperature() - minimumTemperature) / deltaTemperature));
				g2.fillRectangle( xStart.doubleValue(), yStart.doubleValue(), xStart.add(dx).doubleValue(), yStart.add(dy).doubleValue() );

			}
		}
	}

	private abstract class SolveThread extends Thread {
		MainPanel mainPanel;
		double minDifference;

		public SolveThread(MainPanel mainPanel, double minDifference) {
			super();
			this.mainPanel = mainPanel;
			this.minDifference = minDifference;
		}

	}

	private class UpdateProgressThread extends Thread {
		MainPanel mainPanel;
		double difference;

		public UpdateProgressThread(MainPanel mainPanel, double difference) {
			super();
			this.mainPanel = mainPanel;
			this.difference = difference;
		}

		@Override
		public void run() {

			// Atvaltok rendes modba
			if (difference <= 1.0) {

				if (mainPanel.getProgressBar().isIndeterminate()) {
					mainPanel.getProgressBar().setIndeterminate(false);
				}

				mainPanel.getProgressBar().setStringPainted(true);
				mainPanel.getProgressBar().setValue((int) (mainPanel.getCalculationPrecision() / difference * 100));
				mainPanel.getProgressBar().setString(String.format("%.2f", Math.min(100.00, mainPanel.getCalculationPrecision() / difference * 100)));

			}
		}
	}

	/**
	 * 
	 * A ProgressBar-t alapallapotba allitja
	 * 
	 * @author akoel
	 * 
	 */
	private class ClearProgressThread extends Thread {
		MainPanel mainPanel;

		public ClearProgressThread(MainPanel mainPanel) {
			super();
			this.mainPanel = mainPanel;
		}

		@Override
		public void run() {

			// Progressbar: normal nulla hosszu, nincs kijelzes
			mainPanel.getProgressBar().setIndeterminate(false);
			mainPanel.getProgressBar().setStringPainted(false);
			mainPanel.getProgressBar().setValue(0);
			// mainPanel.getProgressBar().setString(null);
		}

	}

	/**
	 * A sokismeretlenes egyenletrendszer megoldasa eredmenye a Termikus Pontok
	 * homerseklete es a termikus pontok kozotti hoaram
	 * 
	 * @param minDifference
	 */
	public void solve(MainPanel mainPanel, double minDifference) {

		solveThread = new SolveThread(mainPanel, minDifference) {

			double difference = -1;

			public void run() {

				// Addig vegzi az iteraciot, amig a Termikus Pontok iteraciot
				// megelozo
				// homersekletenek es az iteraciot koveto homersekletenek
				// kulonbsege kisebb
				// nem lesz a parameterkent megadott engedelyezett elteresnel
				do {

					//
					// Elvegez egy iteraciot az egyenletrendszeren
					//
					oneStepToCalculateTemperature();

					//
					// Kiszamolja a pontossagot
					//
					difference = -1;
					for (int i = 0; i < getSize(); i++) {
						difference = Math.max(difference, list[i].getTempDifference());
					}

					//
					// Kijelzi az elorehaladottsagot
					//
					Thread progressThread = new UpdateProgressThread(mainPanel, difference);
					SwingUtilities.invokeLater(progressThread);

				} while (!(difference < minDifference && difference >= 0) && !mainPanel.needToStopCalculation());

				// Ha megallitottam a futast a Stop gombbal
				if (mainPanel.needToStopCalculation()) {

					// Mukodesi mod valtas - Rajzolsa
					mainPanel.setMode(Mode.DRAWING);

					// ProgressBar alapallapotba allitasa
					SwingUtilities.invokeLater(new ClearProgressThread(mainPanel));

				} else {

					//
					// Q szamitasa a kiszamitott T-k alapjan
					//
					for (int i = 0; i < getSize(); i++) {
						onePointCalculateCurrent(i);
					}

					// Ha nem igy rajzoltatom ujra az eredmenyt, akkor nem
					// jelenik meg
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {

							// Feltolti a Kiszamitott Termikus pontokat, es
							// megjeleniti a szinkoddal
							mainPanel.setThermicPointList(ThermicPointList.this);
						}
					});

					// Mukodesi mod valtas - Elemzes
					mainPanel.setMode(Mode.ANALYSIS);
				}

				// Szamitas gomb funciojanak visszaallitasa eredeti Calculation
				// funkciora
				mainPanel.getSettingTabbedPanel().getControlSettingTab().getCalculateButton().setBackground(Color.green);
				mainPanel.getSettingTabbedPanel().getControlSettingTab().getCalculateButton().setText("Számít");
			}
		}; // Thread

		// Elinditom a szalat
		solveThread.start();

	}

	/**
	 * Egy Termikus pont hoaramainak szamitasa
	 * 
	 * @param position
	 */
	private void onePointCalculateCurrent(int position) {

		IThermicConnector cN = list[position].getNorthThermicConnector();
		IThermicConnector cE = list[position].getEastThermicConnector();
		IThermicConnector cS = list[position].getSouthThermicConnector();
		IThermicConnector cW = list[position].getWestThermicConnector();

		YThermicPointThermicConnector ntc;
		YThermicPointThermicConnector stc;
		XThermicPointThermicConnector etc;
		XThermicPointThermicConnector wtc;
		
		double tf; //Felszini homerseklet Szabad felszin eseten
		//
		// Vertikalis hoaram iranyara meroleges szelessegek megallapitasa
		//

		// ---------
		//
		// NORTH
		//
		// ---------

		// Termikus Pont-Termikus Pont
		if (cN instanceof YThermicPointThermicConnector) {

			ntc = (YThermicPointThermicConnector) cN;

			ThermicPointList.this.get(position).setNorthCurrent(dxdouble * (ntc.getLambda() / dydouble) * (ThermicPointList.this.get(position).getActualTemperature() - ntc.getNorthThermicPoint().getActualTemperature()));
		
		// Termikus Pont - Szabad felszin
		} else if (cN instanceof OpenEdgeThermicConnector) {

			//Meg kell keresnem a D-i termikus pontot, ha van
			if (cS instanceof YThermicPointThermicConnector) {	

				stc = (YThermicPointThermicConnector) cS;
				tf = ( 3 * ThermicPointList.this.get(position).getActualTemperature() - stc.getSouthThermicPoint().getActualTemperature() ) / 2;

			//Ha D-fele nincs termikus pont - elvileg nem lehet
			}else{
				
				//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
				tf = ThermicPointList.this.get(position).getActualTemperature();
			}
			
			ThermicPointList.this.get(position).setNorthCurrent(
					dxdouble * 
					((OpenEdgeThermicConnector) cN).getAlpha() * 
					(tf - ((OpenEdgeThermicConnector) cN).getAirTemperature())
			);

		// Termikus Pont - szimmetrikus kapcsolat
		} else if (cN instanceof SymmetricEdgeThermicConnector) {

			ThermicPointList.this.get(position).setNorthCurrent(0.0);

		// Hiba-Nem lehet, hogy egy pontot nem zar le Connector
		} else {
			throw new Error("Nem szabad elofordulnia ennek az esetnek.\n North fele nincs kapcsolata a kovetkezo termikus pontnak: " + cN);
		}
		

		// ---------
		//
		// SOUTH
		//
		// ---------

		// Termikus Pont-Termikus Pont
		if (cS instanceof YThermicPointThermicConnector) {

			stc = (YThermicPointThermicConnector) cS;

			ThermicPointList.this.get(position).setSouthCurrent(dxdouble * (stc.getLambda() / dydouble) * (ThermicPointList.this.get(position).getActualTemperature() - stc.getSouthThermicPoint().getActualTemperature()));
			
		// Termikus Pont - Szabad felszin
		} else if (cS instanceof OpenEdgeThermicConnector) {

			//Meg kell keresnem a E-i termikus pontot, ha van
			if (cN instanceof YThermicPointThermicConnector) {	

				ntc = (YThermicPointThermicConnector) cN;
				tf = ( 3 * ThermicPointList.this.get(position).getActualTemperature() - ntc.getNorthThermicPoint().getActualTemperature() ) / 2;

			//Ha E-fele nincs termikus pont - elvileg nem lehet
			}else{
				
				//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
				tf = ThermicPointList.this.get(position).getActualTemperature();
			}
			
			ThermicPointList.this.get(position).setSouthCurrent(
					dxdouble * 
					((OpenEdgeThermicConnector) cS).getAlpha() * 
					(tf - ((OpenEdgeThermicConnector) cS).getAirTemperature())
			);

		// Termikus Pont - szimmetrikus kapcsolat
		} else if (cS instanceof SymmetricEdgeThermicConnector) {

			ThermicPointList.this.get(position).setSouthCurrent(0.0);

		// Hiba-Nem lehet, hogy egy pontot nem zar le Connector
		} else {
			throw new Error("Nem szabad elofordulnia ennek az esetnek.\n South fele nincs kapcsolata a kovetkezo termikus pontnak: " + cS);
		}

		// ---------
		//
		// EAST
		//
		// ---------

		// Termikus Pont-Termikus Pont
		if (cE instanceof XThermicPointThermicConnector) {

			etc = (XThermicPointThermicConnector) cE;

			ThermicPointList.this.get(position).setEastCurrent(dydouble * (etc.getLambda() / dxdouble) * (ThermicPointList.this.get(position).getActualTemperature() - etc.getEastThermicPoint().getActualTemperature()));
			
		// Termikus Pont - Szabad felszin
		} else if (cE instanceof OpenEdgeThermicConnector) {

			//Meg kell keresnem a NY-i termikus pontot, ha van
			if (cW instanceof XThermicPointThermicConnector) {	

				wtc = (XThermicPointThermicConnector) cW;
				tf = ( 3 * ThermicPointList.this.get(position).getActualTemperature() - wtc.getWestThermicPoint().getActualTemperature() ) / 2;

			//Ha NY-fele nincs termikus pont - elvileg nem lehet
			}else{
				
				//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
				tf = ThermicPointList.this.get(position).getActualTemperature();
			}
			
			ThermicPointList.this.get(position).setEastCurrent(
					dydouble * 
					((OpenEdgeThermicConnector) cE).getAlpha() * 
					(tf - ((OpenEdgeThermicConnector) cE).getAirTemperature())
			);

		// Termikus Pont - szimmetrikus kapcsolat
		} else if (cE instanceof SymmetricEdgeThermicConnector) {

			ThermicPointList.this.get(position).setEastCurrent(0.0);

		// Hiba-Nem lehet, hogy egy pontot nem zar le Connector
		} else {
			throw new Error("Nem szabad elofordulnia ennek az esetnek.\n East fele nincs kapcsolata a kovetkezo termikus pontnak: " + cE);
		}

		// ---------
		//
		// WEST
		//
		// ---------

		// Termikus Pont-Termikus Pont
		if (cW instanceof XThermicPointThermicConnector) {

			wtc = (XThermicPointThermicConnector) cW;

			ThermicPointList.this.get(position).setWestCurrent(dydouble * (wtc.getLambda() / dxdouble) * (ThermicPointList.this.get(position).getActualTemperature() - wtc.getWestThermicPoint().getActualTemperature()));
			
		// Termikus Pont - Szabad felszin
		} else if (cW instanceof OpenEdgeThermicConnector) {

			//Meg kell keresnem a K-i termikus pontot, ha van
			if (cE instanceof XThermicPointThermicConnector) {	

				etc = (XThermicPointThermicConnector) cE;
				tf = ( 3 * ThermicPointList.this.get(position).getActualTemperature() - etc.getEastThermicPoint().getActualTemperature() ) / 2;

			//Ha K-fele nincs termikus pont - elvileg nem lehet
			}else{
				
				//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
				tf = ThermicPointList.this.get(position).getActualTemperature();
			}
			
			ThermicPointList.this.get(position).setWestCurrent(
					dydouble * 
					((OpenEdgeThermicConnector) cW).getAlpha() * 
					(tf - ((OpenEdgeThermicConnector) cW).getAirTemperature()));

		// Termikus Pont - szimmetrikus kapcsolat
		} else if (cW instanceof SymmetricEdgeThermicConnector) {

			ThermicPointList.this.get(position).setWestCurrent(0.0);

		// Hiba-Nem lehet, hogy egy pontot nem zar le Connector
		} else {
			throw new Error("Nem szabad elofordulnia ennek az esetnek.\n West fele nincs kapcsolata a kovetkezo termikus pontnak: " + cW);
		}
	}

	/**
	 * 
	 * Egy iteracio elvegzese a lista teljes allomanyan
	 * 
	 */
	private void oneStepToCalculateTemperature() {

		double nevezo = 0;
		double szamlalo = 0;

		YThermicPointThermicConnector ntc;
		YThermicPointThermicConnector stc;
		XThermicPointThermicConnector etc;
		XThermicPointThermicConnector wtc;
		
		IThermicConnector cN;
		IThermicConnector cE;
		IThermicConnector cS;
		IThermicConnector cW;
	
		for (int i = 0; i < position; i++) {

			nevezo = 0;
			szamlalo = 0;

			cN = list[i].getNorthThermicConnector();
			cE = list[i].getEastThermicConnector();
			cS = list[i].getSouthThermicConnector();
			cW = list[i].getWestThermicConnector();
	
			// ---------
			//
			// NORTH
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cN instanceof YThermicPointThermicConnector) {				

				ntc = (YThermicPointThermicConnector) cN;
				
				double deltalabda = dxdouble * ntc.getLambda() / dydouble;
				szamlalo += deltalabda * ntc.getNorthThermicPoint().getActualTemperature();
				nevezo += deltalabda;

			// Termikus Pont - Szabad felszin
			} else if (cN instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cN).getAlpha() * dxdouble;
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cN).getAirTemperature();
				nevezo += 1.5*alphadelta;
				
				//Meg kell keresnem a D-i termikus pontot, ha van
				if (cS instanceof YThermicPointThermicConnector) {	

					stc = (YThermicPointThermicConnector) cS;
					szamlalo += 0.5 * alphadelta * stc.getSouthThermicPoint().getActualTemperature();

				//Ha D-fele nincs termikus pont
				}else{
					
					//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
					szamlalo += 0.5 * alphadelta * list[i].getActualTemperature();
									
				}

			}


			
			// ---------
			//
			// SOUTH
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cS instanceof YThermicPointThermicConnector) {

				stc = (YThermicPointThermicConnector) cS;

				double deltalabda = dxdouble * stc.getLambda() / dydouble;			
				szamlalo += deltalabda * stc.getSouthThermicPoint().getActualTemperature();
				nevezo += deltalabda;

			// Termikus Pont - Szabad felszin
			} else if (cS instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cS).getAlpha() * dxdouble;
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cS).getAirTemperature();
				nevezo += 1.5*alphadelta;

				//Meg kell keresnem az E-i termikus pontot, ha van
				if (cN instanceof YThermicPointThermicConnector) {	

					ntc = (YThermicPointThermicConnector) cN;
					szamlalo += 0.5 * alphadelta * ntc.getNorthThermicPoint().getActualTemperature();

				//Ha E-fele nincs termikus pont
				}else{
					
					//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
					szamlalo += 0.5 * alphadelta * list[i].getActualTemperature();
				
				}
			}


			
			// ---------
			//
			// EAST
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cE instanceof XThermicPointThermicConnector) {

				etc = (XThermicPointThermicConnector) cE;

				double deltalabda = dydouble * etc.getLambda() / dxdouble;
				szamlalo += deltalabda * etc.getEastThermicPoint().getActualTemperature();
				nevezo += deltalabda;
			
			// Termikus Pont - Szabad felszin
			} else if (cE instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cE).getAlpha() * dydouble;
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cE).getAirTemperature();
				nevezo += 1.5*alphadelta;

				//Meg kell keresnem a NY-i termikus pontot, ha van
				if (cW instanceof XThermicPointThermicConnector) {	

					wtc = (XThermicPointThermicConnector) cW;
					szamlalo += 0.5 * alphadelta * wtc.getWestThermicPoint().getActualTemperature();

				//Ha NY-fele nincs termikus pont
				}else{
					
					//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
					szamlalo += 0.5 * alphadelta * list[i].getActualTemperature();

				}
			}



			// ---------
			//
			// WEST
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cW instanceof XThermicPointThermicConnector) {

				wtc = (XThermicPointThermicConnector) cW;

				double deltalabda = dydouble * wtc.getLambda() / dxdouble;				
				szamlalo += deltalabda * wtc.getWestThermicPoint().getActualTemperature();
				nevezo += deltalabda;
			
			// Termikus Pont - Szabad felszin
			} else if (cW instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cW).getAlpha() * dydouble;				
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cW).getAirTemperature();
				nevezo += 1.5*alphadelta;

				//Meg kell keresnem a K-i termikus pontot, ha van
				if (cE instanceof XThermicPointThermicConnector) {	

					etc = (XThermicPointThermicConnector) cE;
					szamlalo += 0.5 * alphadelta * etc.getEastThermicPoint().getActualTemperature();

				//Ha K-fele nincs termikus pont
				}else{
					
					//Akkor ugy veszem mintha lenne, de a homerseklete egyenlo a vizsgalt pont homersekletevel
					szamlalo += 0.5 * alphadelta * list[i].getActualTemperature();
					
				}
			}
		
			list[i].setActualTemperature(szamlalo / nevezo);

		}

	}

	/**
	 * Visszaadja a lista meretet
	 * 
	 * @return
	 */	
	public int getSize() {
		return position;
	}

	/**
	 * Visszaadja az adott lista-pozicioban levo Termikus Pontot
	 * 
	 * @param position
	 * @return
	 */
	public ThermicPoint get(int position) {
		return list[position];
	}

	public String toString() {
		String out = new String();

		for (int j = 0; j < this.getSize(); j++) {
			out += this.get(j) + "\n";
		}
		return out;
	}
}

class Vector2D {
	public double x;
	public double y;
	double theta;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
		this.theta = Math.atan2(y, x);
	}

	public double getTheta() {
		return theta;
	}

	/*
	 * public double getLength(){ return Math.sqrt( x*x + y*y ); }
	 */
	public Vector2D getVector(double length) {
		return new Vector2D(length * Math.cos(theta), length * Math.sin(theta));
	}

}
