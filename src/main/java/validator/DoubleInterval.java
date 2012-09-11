package validator;

/**
 * A double has to be inside the specified range [min, max] (incl.) to be valid
 * 
 * @author PilgerstorferP
 * 
 */
public class DoubleInterval implements DoubleValidator {
	private double min;
	private double max;

	public DoubleInterval(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public boolean isValid(double value) {
		return min <= value && value <= max;
	}
}
