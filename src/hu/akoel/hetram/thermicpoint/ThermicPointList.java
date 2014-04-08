package hu.akoel.hetram.thermicpoint;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.accessories.ColorTransient;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
import hu.akoel.hetram.connectors.AThermicPointThermicConnector;
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

	public static enum CURRENT_TYPE {
		VECTORPAIR, VECTOR, TRAJECTORY
	}

	public ThermicPointList(Collection<ThermicPoint> thermicPointCollection) { 
		
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
		// public ThermicPoint getThermicPointByPosition( BigDecimal x,
		// BigDecimal y ){

		if (this.getSize() > 0) {

			IThermicConnector c;
			double dy1, dy2, dx1, dx2;
			// BigDecimal dy1, dy2, dx1, dx2;

			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				BigDecimalPosition ps = this.get(j).getPosition();
				Position position = new Position(ps.getX().doubleValue(), ps.getY().doubleValue());
				// dy1 = new BigDecimal("0");
				// dy2 = new BigDecimal("0");
				// dx1 = new BigDecimal("0");
				// dx2 = new BigDecimal("0");

				dy1 = 0;
				dy2 = 0;
				dx1 = 0;
				dx2 = 0;

				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					// dy2 =
					// ((YThermicPointThermicConnector)c).getDelta().divide( new
					// BigDecimal(2) );
					dy2 = ((YThermicPointThermicConnector) c).getDelta().doubleValue() / 2;
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					// dx2 =
					// ((XThermicPointThermicConnector)c).getDelta().divide( new
					// BigDecimal(2) );
					dx2 = ((XThermicPointThermicConnector) c).getDelta().doubleValue() / 2;
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					// dy1 =
					// ((YThermicPointThermicConnector)c).getDelta().divide( new
					// BigDecimal(2) );
					dy1 = ((YThermicPointThermicConnector) c).getDelta().doubleValue() / 2;
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					// dx1 =
					// ((XThermicPointThermicConnector)c).getDelta().divide( new
					// BigDecimal(2) );
					dx1 = ((XThermicPointThermicConnector) c).getDelta().doubleValue() / 2;
				}

				// if( y.compareTo( position.getY().add( dy2 ) ) <= 0 &&
				// y.compareTo( position.getY().subtract( dy1 ) ) >= 0 &&
				// x.compareTo( position.getX().add( dx2 ) ) <= 0 &&
				// x.compareTo( position.getX().subtract( dx1 ) ) >= 0 ){
				if (y <= position.getY() + dy2 && y >= position.getY() - dy1 && x <= position.getX() + dx2 && x >= position.getX() - dx1) {
					return this.get(j);
				}
			}
		}
		return null;
	}

	/*
	 * public Double getTemperatureByPosition( double x, double y ){
	 * 
	 * ThermicPoint tp = getThermicPointByPosition(x, y); if( null == tp ){
	 * return null; }else{ return tp.getActualTemperature(); } }
	 */

	/*
	 * public void setCalculationListener(CalculationListener
	 * calculationListener) { this.calculationListener = calculationListener; }
	 */
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

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					delta = ((XThermicPointThermicConnector) c).getDelta();
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					delta = ((XThermicPointThermicConnector) c).getDelta();
				}

				// A pont geometriai elhelyezkedese
				BigDecimalPosition position = this.get(j).getPosition();

				int fontSize = canvas.getPixelXLengthByWorld(delta.doubleValue()) / 4;
				Font font = new Font("Default", Font.PLAIN, fontSize);
				FontRenderContext frc = g2.getFontRenderContext();

				g2.setColor(Color.white);
				TextLayout textLayout = new TextLayout(String.valueOf(canvas.getRoundedBigDecimalWithPrecision(this.get(j).getActualTemperature())), font, frc);
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
					maxDeltaY = Math.max(maxDeltaY, Math.abs(((YThermicPointThermicConnector) c).getDelta().doubleValue()));
					// maxDeltaY = Math.max( maxDeltaY, Math.abs(
					// ((YThermicPointThermicConnector)c).getDelta() ));
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					maxDeltaY = Math.max(maxDeltaY, Math.abs(((XThermicPointThermicConnector) c).getDelta().doubleValue()));
					// maxDeltaY = Math.max( maxDeltaY, Math.abs(
					// ((YThermicPointThermicConnector)c).getDelta() ));
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					maxDeltaY = Math.max(maxDeltaY, Math.abs(((YThermicPointThermicConnector) c).getDelta().doubleValue()));
					// maxDeltaY = Math.max( maxDeltaY, Math.abs(
					// ((YThermicPointThermicConnector)c).getDelta() ));
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					maxDeltaX = Math.max(maxDeltaX, Math.abs(((XThermicPointThermicConnector) c).getDelta().doubleValue()));
					// maxDeltaX = Math.max( maxDeltaX, Math.abs(
					// ((XThermicPointThermicConnector)c).getDelta() ));
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

						vY = position.getY() + yLengthPercentage * ((YThermicPointThermicConnector) c).getDelta().doubleValue();

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

						vY = position.getY() - yLengthPercentage * ((YThermicPointThermicConnector) c).getDelta().doubleValue();

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

						vX = position.getX() + xLengthPercentage * ((XThermicPointThermicConnector) c).getDelta().doubleValue();

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

						vX = position.getX() - xLengthPercentage * ((XThermicPointThermicConnector) c).getDelta().doubleValue();

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
					// vector = vector.getVector( maxLength );

					Path2D.Double trajektoriaPath = new Path2D.Double();
					trajektoriaPath.moveTo(position.getX() - vector.x / 2, position.getY() - vector.y / 2);
					trajektoriaPath.lineTo(position.getX() + vector.x / 2, position.getY() + vector.y / 2);
					// trajektoriaPath.moveTo( position.getX() - ( vX -
					// position.getX() ) / 2, position.getY() - ( vY -
					// position.getY() ) / 2 );
					// trajektoriaPath.lineTo( position.getX() + ( vX -
					// position.getX() ) / 2, position.getY() + ( vY -
					// position.getY() ) / 2 );

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

			// Megkeresi a legnagyobb Delta-t ami a nyilak 100%-a lesz
			double delta = 0;
			IThermicConnector cNorth, cEast, cSouth, cWest;
			IThermicConnector tc;
			ThermicPoint tP;

			for (int j = 0; j < this.getSize(); j++) {

				cNorth = this.get(j).getNorthThermicConnector();
				if (cNorth instanceof AThermicPointThermicConnector) {
					delta = Math.max(delta, ((AThermicPointThermicConnector) cNorth).getDelta().doubleValue());
				}

				cEast = this.get(j).getEastThermicConnector();
				if (cEast instanceof AThermicPointThermicConnector) {
					delta = Math.max(delta, ((AThermicPointThermicConnector) cEast).getDelta().doubleValue());
				}
			}

			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				// A pont geometriai elhelyezkedese
				BigDecimalPosition ps = this.get(j).getPosition();
				Position position = new Position(ps.getX().doubleValue(), ps.getY().doubleValue());

				double dNorth = 0;
				double dEast = 0;
				double dSouth = 0;
				double dWest = 0;

				cNorth = this.get(j).getNorthThermicConnector();
				if (cNorth instanceof AThermicPointThermicConnector) {
					dNorth = ((AThermicPointThermicConnector) cNorth).getDelta().doubleValue() / 2;
				}

				cEast = this.get(j).getEastThermicConnector();
				if (cEast instanceof AThermicPointThermicConnector) {
					dEast = ((AThermicPointThermicConnector) cEast).getDelta().doubleValue() / 2;
				}

				cSouth = this.get(j).getSouthThermicConnector();
				if (cSouth instanceof AThermicPointThermicConnector) {
					dSouth = ((AThermicPointThermicConnector) cSouth).getDelta().doubleValue() / 2;
				}

				cWest = this.get(j).getWestThermicConnector();
				if (cWest instanceof AThermicPointThermicConnector) {
					dWest = ((AThermicPointThermicConnector) cWest).getDelta().doubleValue() / 2;
				}

				// double xStart = position.getX() - dWest;
				// double yStart = position.getY() - dSouth;

				double xStart = position.getX();
				double yStart = position.getY();

				// A Termikus Ponthoz tartozo szin
				// g2.setColor(getRedBluByPercent((this.get(j).getActualTemperature()
				// - minimumTemperature) / deltaTemperature));
				g2.setColor(colorTransient.getColor((this.get(j).getActualTemperature() - minimumTemperature) / deltaTemperature));

				//
				// A teljes negyzet negyedekre valo felbontasa azert szukseges,
				// mert
				// Ha van az adott negyedet meghatarozo iranyokba mutato
				// Termikus Konnektor az meg nem jelenti azt,
				// hogy az adott negyed belul van a fizikai keresztmetszeten
				// Praktikusan ilyen helyzet a negativ falsarok -> L. Itt van
				// mondjuk Kelet es Eszak iranyba is Termikus Konnektor
				// ennek ellenere a negativ sarok pontban E-K negyedre megsem
				// kell rajzolni, hiszen fizikailag az mar nem
				// a keresztmetszet resze
				//

				//
				// E-K negyzet kirajzolasa
				//
				// Ha van Eszakra es Keletre is mutato Termikus konnektor meg
				// nem jelenti azt, hogy az adott negyed belul van a
				// keresztmetszeten
				if (dEast > 0 && dNorth > 0) {

					// Meg kell nezni hogy a tole Keletre levo Termikus Pont
					// rendelkezik-e Eszak fele mutato Termikus Konnektorral
					// Meg kene nezni, a tole Eszakra levo Termikus Pontot is
					tP = ((XThermicPointThermicConnector) cEast).getEastThermicPoint();

					// Ha rendelkezik D-fele mutato Termikus Konnektorral
					tc = tP.getNorthThermicConnector();
					if (tc instanceof AThermicPointThermicConnector) {
						g2.fillRectangle(xStart, yStart, xStart + dEast, yStart + dNorth);
					}

				}

				//
				// D-K negyzet kirajzolasa
				//
				// Ha van Keletre es Delre is mutato Termikus konnektor meg nem
				// jelenti azt, hogy az adott negyed belul van a
				// keresztmetszeten
				if (dEast > 0 && dSouth > 0) {

					// Meg kell nezni hogy a tole Keletre levo Termikus Pont
					// rendelkezik-e Del fele mutato Termikus Konnektorral
					// Meg kene nezni, a tole Delre levo Termikus Pontot is
					tP = ((XThermicPointThermicConnector) cEast).getEastThermicPoint();

					// Ha rendelkezik D-fele mutato Termikus Konnektorral
					tc = tP.getSouthThermicConnector();
					if (tc instanceof AThermicPointThermicConnector) {
						g2.fillRectangle(xStart, yStart, xStart + dEast, yStart - dSouth);
					}

				}

				//
				// D-NY negyzet kirajzolasa
				//
				// Ha van Delre es Nyugatra is mutato Termikus konnektor meg nem
				// jelenti azt, hogy az adott negyed belul van a
				// keresztmetszeten
				if (dSouth > 0 && dWest > 0) {

					// Meg kell nezni hogy a tole Nyugatra levo Termikus Pont
					// rendelkezik-e Del fele mutato Termikus Konnektorral
					// Meg kene nezni, a tole Delre levo Termikus Pontot is
					tP = ((XThermicPointThermicConnector) cWest).getWestThermicPoint();

					// Ha rendelkezik D-fele mutato Termikus Konnektorral
					tc = tP.getSouthThermicConnector();
					if (tc instanceof AThermicPointThermicConnector) {
						g2.fillRectangle(xStart, yStart, xStart - dWest, yStart - dSouth);
					}

				}

				//
				// E-NY negyzet kirajzolasa
				//
				// Ha van Delre es Nyugatra is mutato Termikus konnektor meg nem
				// jelenti azt, hogy az adott negyed belul van a
				// keresztmetszeten
				if (dNorth > 0 && dWest > 0) {

					// Meg kell nezni hogy a tole Nyugatra levo Termikus Pont
					// rendelkezik-e Eszak fele mutato Termikus Konnektorral
					// Meg kene nezni, a tole Eszakra levo Termikus Pontot is
					tP = ((XThermicPointThermicConnector) cWest).getWestThermicPoint();

					// Ha rendelkezik E-fele mutato Termikus Konnektorral
					tc = tP.getNorthThermicConnector();
					if (tc instanceof AThermicPointThermicConnector) {
						g2.fillRectangle(xStart, yStart, xStart - dWest, yStart + dNorth);
					}

				}

				// g2.fillRectangle(xStart, yStart, xStart + dWest + dEast,
				// yStart + dSouth + dNorth);

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

				} while (!(difference < minDifference && difference > 0) && !mainPanel.needToStopCalculation());

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
/*		
		OpenEdgeThermicConnector oeN = list[position].getExtraNorthOpenEdgeConnector();
		OpenEdgeThermicConnector oeE = list[position].getExtraEastOpenEdgeConnector();
		OpenEdgeThermicConnector oeS = list[position].getExtraSouthOpenEdgeConnector();
		OpenEdgeThermicConnector oeW = list[position].getExtraWestOpenEdgeConnector();
*/
		double dYNormal = 0; // Meroleges
		double dXNormal = 0; // Meroleges

		double dX = 0; // Az vizsgalt pontban szamolt szelesseg
		double dY = 0; // Az vizsgalt pontban szamolt magassag
		double dNX = 0; // A vizsgalt ponttol E-ra levo pont szelessege
		double dSX = 0; // A vizsgalt ponttol D-re levo pont szelessege
		double dEY = 0; // A vizsgalt ponttol K-re levo pont szelessege
		double dWY = 0; // A vizsgalt ponttol NY-ra levo pont szelessege

		//
		// Vertikalis hoaram iranyara meroleges szelessegek megallapitasa
		//
		if (cE instanceof XThermicPointThermicConnector && cW instanceof XThermicPointThermicConnector ) {
			dX = ((XThermicPointThermicConnector) cE).getDelta().doubleValue();		
		}else if (cE instanceof XThermicPointThermicConnector ) {
			dX = ((XThermicPointThermicConnector) cE).getDelta().doubleValue() / 2;
		}else if (cW instanceof XThermicPointThermicConnector) {
			dX = ((XThermicPointThermicConnector) cW).getDelta().doubleValue() / 2;
		}

		//
		// Horizontalis hoaram iranyara meroleges szelessegek megallapitasa
		//
		if (cN instanceof YThermicPointThermicConnector && cS instanceof YThermicPointThermicConnector) {
			dY = ((YThermicPointThermicConnector) cN).getDelta().doubleValue();
		}else if (cN instanceof YThermicPointThermicConnector) {
			dY = ((YThermicPointThermicConnector) cN).getDelta().doubleValue() / 2;
		}else if (cS instanceof YThermicPointThermicConnector) {
			dY = ((YThermicPointThermicConnector) cS).getDelta().doubleValue() / 2;
		}

		// ---------
		//
		// NORTH
		//
		// ---------

		// Termikus Pont-Termikus Pont
		if (cN instanceof YThermicPointThermicConnector) {

			YThermicPointThermicConnector ntc = (YThermicPointThermicConnector) cN;

			IThermicConnector etc = ntc.getNorthThermicPoint().getEastThermicConnector();
			IThermicConnector wtc = ntc.getNorthThermicPoint().getWestThermicConnector();
			if (etc instanceof XThermicPointThermicConnector) {
				dNX += ((XThermicPointThermicConnector) etc).getDelta().doubleValue() / 2;
			}
			if (wtc instanceof XThermicPointThermicConnector) {
				dNX += ((XThermicPointThermicConnector) wtc).getDelta().doubleValue() / 2;
			}
			dXNormal = Math.min(dNX, dX);

			ThermicPointList.this.get(position).setNorthCurrent(dXNormal * (ntc.getLambda() / ntc.getDelta().doubleValue()) * (ThermicPointList.this.get(position).getActualTemperature() - ntc.getNorthThermicPoint().getActualTemperature()));
/*
if( null != oeN ){		
	ThermicPointList.this.get(position).addExtraNorthCurrent( dX * oeN.getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - oeN.getAirTemperature()) );
}
*/			
			// Termikus Pont - Szabad felszin
		} else if (cN instanceof OpenEdgeThermicConnector) {

			ThermicPointList.this.get(position).setNorthCurrent(dX * ((OpenEdgeThermicConnector) cN).getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - ((OpenEdgeThermicConnector) cN).getAirTemperature()));

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

			YThermicPointThermicConnector stc = (YThermicPointThermicConnector) cS;

			IThermicConnector etc = stc.getSouthThermicPoint().getEastThermicConnector();
			IThermicConnector wtc = stc.getSouthThermicPoint().getWestThermicConnector();
			if (etc instanceof XThermicPointThermicConnector) {
				dSX += ((XThermicPointThermicConnector) etc).getDelta().doubleValue() / 2;
			}
			if (wtc instanceof XThermicPointThermicConnector) {
				dSX += ((XThermicPointThermicConnector) wtc).getDelta().doubleValue() / 2;
			}
			dXNormal = Math.min(dSX, dX);

			// //South tavolsaga
			// dYDirection = stc.getDelta().doubleValue();

			ThermicPointList.this.get(position).setSouthCurrent(dXNormal * (stc.getLambda() / stc.getDelta().doubleValue()) * (ThermicPointList.this.get(position).getActualTemperature() - stc.getSouthThermicPoint().getActualTemperature()));
/*
if( null != oeS ){		
	ThermicPointList.this.get(position).addExtraSouthCurrent( dX * oeS.getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - oeS.getAirTemperature()) );
}
*/			
			// Termikus Pont - Szabad felszin
		} else if (cS instanceof OpenEdgeThermicConnector) {

			ThermicPointList.this.get(position).setSouthCurrent(dX * ((OpenEdgeThermicConnector) cS).getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - ((OpenEdgeThermicConnector) cS).getAirTemperature()));

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

			XThermicPointThermicConnector etc = (XThermicPointThermicConnector) cE;

			IThermicConnector ntc = etc.getEastThermicPoint().getNorthThermicConnector();
			IThermicConnector stc = etc.getEastThermicPoint().getSouthThermicConnector();
			if (ntc instanceof YThermicPointThermicConnector) {
				dEY += ((YThermicPointThermicConnector) ntc).getDelta().doubleValue() / 2;
			}
			if (stc instanceof YThermicPointThermicConnector) {
				dEY += ((YThermicPointThermicConnector) stc).getDelta().doubleValue() / 2;
			}
			dYNormal = Math.min(dEY, dY);

			ThermicPointList.this.get(position).setEastCurrent(dYNormal * (etc.getLambda() / etc.getDelta().doubleValue()) * (ThermicPointList.this.get(position).getActualTemperature() - etc.getEastThermicPoint().getActualTemperature()));
/*
if( null != oeE ){
	ThermicPointList.this.get(position).addExtraEastCurrent( dY * oeE.getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - oeE.getAirTemperature()));
}
*/			
			// Termikus Pont - Szabad felszin
		} else if (cE instanceof OpenEdgeThermicConnector) {

			ThermicPointList.this.get(position).setEastCurrent(dY * ((OpenEdgeThermicConnector) cE).getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - ((OpenEdgeThermicConnector) cE).getAirTemperature()));

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

			XThermicPointThermicConnector wtc = (XThermicPointThermicConnector) cW;

			IThermicConnector ntc = wtc.getWestThermicPoint().getNorthThermicConnector();
			IThermicConnector stc = wtc.getWestThermicPoint().getSouthThermicConnector();
			if (ntc instanceof YThermicPointThermicConnector) {
				dWY += ((YThermicPointThermicConnector) ntc).getDelta().doubleValue() / 2;
			}
			if (stc instanceof YThermicPointThermicConnector) {
				dWY += ((YThermicPointThermicConnector) stc).getDelta().doubleValue() / 2;
			}
			dYNormal = Math.min(dWY, dY);

			ThermicPointList.this.get(position).setWestCurrent(dYNormal * (wtc.getLambda() / wtc.getDelta().doubleValue()) * (ThermicPointList.this.get(position).getActualTemperature() - wtc.getWestThermicPoint().getActualTemperature()));
/*
if( null != oeW ){
	ThermicPointList.this.get(position).addExtraEastCurrent( dY * oeW.getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - oeW.getAirTemperature()));
}
*/			
			// Termikus Pont - Szabad felszin
		} else if (cW instanceof OpenEdgeThermicConnector) {

			ThermicPointList.this.get(position).setWestCurrent(dY * ((OpenEdgeThermicConnector) cW).getAlpha() * (ThermicPointList.this.get(position).getActualTemperature() - ((OpenEdgeThermicConnector) cW).getAirTemperature()));

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

OpenEdgeThermicConnector oeN;
OpenEdgeThermicConnector oeE;
OpenEdgeThermicConnector oeS;
OpenEdgeThermicConnector oeW;
		
		for (int i = 0; i < position; i++) {

			nevezo = 0;
			szamlalo = 0;

			cN = list[i].getNorthThermicConnector();
			cE = list[i].getEastThermicConnector();
			cS = list[i].getSouthThermicConnector();
			cW = list[i].getWestThermicConnector();
/*
oeN = list[i].getExtraNorthOpenEdgeConnector();
oeE = list[i].getExtraEastOpenEdgeConnector();
oeS = list[i].getExtraSouthOpenEdgeConnector();
oeW = list[i].getExtraWestOpenEdgeConnector();
*/			
			// ---------
			//
			// NORTH
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cN instanceof YThermicPointThermicConnector) {				

				ntc = (YThermicPointThermicConnector) cN;
				
				double deltalabda = list[i].getNorthDeltaNormal() * ntc.getLambda() / list[i].getNorthDelta();
				szamlalo += deltalabda * ntc.getNorthThermicPoint().getActualTemperature();
				nevezo += deltalabda;

			// Termikus Pont - Szabad felszin
			} else if (cN instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cN).getAlpha() * list[i].getNorthDeltaNormal();
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cN).getAirTemperature();
				nevezo += alphadelta;

			}
/*
if( null != oeN ){
	double alphadelta = oeN.getAlpha() * list[i].getNorthDeltaNormal();
	szamlalo += alphadelta * oeN.getAirTemperature();
	nevezo += alphadelta;
}
*/
			
			// ---------
			//
			// SOUTH
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cS instanceof YThermicPointThermicConnector) {

				stc = (YThermicPointThermicConnector) cS;

				double deltalabda = list[i].getSouthDeltaNormal() * stc.getLambda() / list[i].getSouthDelta();
				szamlalo += deltalabda * stc.getSouthThermicPoint().getActualTemperature();
				nevezo += deltalabda;

				// Termikus Pont - Szabad felszin
			} else if (cS instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cS).getAlpha() * list[i].getSouthDeltaNormal();
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cS).getAirTemperature();
				nevezo += alphadelta;

			}
/*
if( null != oeS ){
	double alphadelta = oeS.getAlpha() * list[i].getSouthDeltaNormal();
	szamlalo += alphadelta * oeS.getAirTemperature();
	nevezo += alphadelta;
}
*/
			
			// ---------
			//
			// EAST
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cE instanceof XThermicPointThermicConnector) {

				etc = (XThermicPointThermicConnector) cE;

				double deltalabda = list[i].getEastDeltaNormal() * etc.getLambda() / list[i].getEastDelta();
				szamlalo += deltalabda * etc.getEastThermicPoint().getActualTemperature();
				nevezo += deltalabda;

			// Termikus Pont - Szabad felszin
			} else if (cE instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cE).getAlpha() * list[i].getEastDeltaNormal();
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cE).getAirTemperature();
				nevezo += alphadelta;

			}
/*
if( null != oeE ){
	double alphadelta = oeE.getAlpha() * list[i].getEastDeltaNormal();
	szamlalo += alphadelta * oeE.getAirTemperature();
	nevezo += alphadelta;
}
*/
			// ---------
			//
			// WEST
			//
			// ---------

			// Termikus Pont-Termikus Pont
			if (cW instanceof XThermicPointThermicConnector) {

				wtc = (XThermicPointThermicConnector) cW;

				double deltalabda = list[i].getWestDeltaNormal() * wtc.getLambda() / list[i].getWestDelta();
				szamlalo += deltalabda * wtc.getWestThermicPoint().getActualTemperature();
				nevezo += deltalabda;

				// Termikus Pont - Szabad felszin
			} else if (cW instanceof OpenEdgeThermicConnector) {

				double alphadelta = ((OpenEdgeThermicConnector) cW).getAlpha() * list[i].getWestDeltaNormal();
				szamlalo += alphadelta * ((OpenEdgeThermicConnector) cW).getAirTemperature();
				nevezo += alphadelta;

			}
/*			
if( null != oeW ){
	double alphadelta = oeW.getAlpha() * list[i].getWestDeltaNormal();
	szamlalo += alphadelta * oeW.getAirTemperature();
	nevezo += alphadelta;
}
*/
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
