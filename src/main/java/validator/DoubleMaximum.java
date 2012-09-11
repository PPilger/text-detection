package validator;

/**
 * A double has to be lower or equal than a specified maximum value to be valid
 * 
 * @author PilgerstorferP
 * 
 */
public class DoubleMaximum implements DoubleValidator {
	private double max;

	public DoubleMaximum(double max) {
		this.max = max;
	}

	public double getMax() {
		return max;
	}

	public boolean isValid(double value) {
		return value <= max;
	}
}
