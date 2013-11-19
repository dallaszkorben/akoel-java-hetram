package hu.akoel.hetram;

public abstract class DThermicConnector implements ThermicConnector{

	private double lambda;
	private double delta;
	private boolean isLoop = false;
	
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

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

}
