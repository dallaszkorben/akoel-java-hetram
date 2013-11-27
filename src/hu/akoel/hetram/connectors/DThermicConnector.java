package hu.akoel.hetram.connectors;


public abstract class DThermicConnector implements ThermicConnector{

	private double current;
	private double lambda;
	private double delta;
	
	public DThermicConnector( double delta, double lambda ){
		this.delta = delta;
		this.lambda = lambda;
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public double getDelta() {
		return delta;
	}

	public double getCurrent(){
		return current;
	}
	
	public void setCurrent( double current ){
		this.current = current;
	}
}
