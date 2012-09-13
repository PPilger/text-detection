package validator;

/**
 * An integer has to be greater or equal than a specified minimum value to be
 * valid
 * 
 * @author PilgerstorferP
 * 
 */
public class IMinimum implements IValidator {
	private int min;

	public IMinimum(int min) {
		this.min = min;
	}

	public int getMin() {
		return min;
	}

	public boolean isValid(int value) {
		return min <= value;
	}
}
