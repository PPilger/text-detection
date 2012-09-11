package validator;

/**
 * A double has to be greater or equal than a specified minimum value to be
 * valid
 * 
 * @author PilgerstorferP
 * 
 */
public class DoubleMinimum implements DoubleValidator {
	private double min;

	public DoubleMinimum(double min) {
		this.min = min;
	}

	public double getMin() {
		return min;
	}

	public boolean isValid(double value) {
		return min <= value;
	}
}
