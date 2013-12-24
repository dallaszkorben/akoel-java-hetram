package hu.akoel.hetram.connectors;

/**
 * Egy masik Termikus Pont-hoz valo csatlakozast biztosito konnektor
 * Minden Termikus Pont-hoz 4 konnektor csatlakoztathato. Egy nem el-kent 
 * funkcionalo Termikus Pont-hoz 4 masik Termikus Pont csatlakozik. 
 * Mivel ezek értelemszerüen vertikális és horizontalis irányúak is lehetnek
 * ezert ennek az Abstract osztalynak a peldanyai fognak az irányultság tulajdonsággal rendelkezni
 * 
 * @author akoel
 *
 */
public abstract class AThermicPointThermicConnector implements IThermicConnector{

	private double lambda;
	private double delta;
	
	public AThermicPointThermicConnector( double delta, double lambda ){
		this.delta = delta;
		this.lambda = lambda;
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public double getDelta() {
		return delta;
	}

}
