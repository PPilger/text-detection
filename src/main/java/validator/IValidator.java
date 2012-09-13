package validator;

public interface IValidator {
	/**
	 * Checks if value is valid or not.
	 * 
	 * @param value
	 * @return true if value is valid, false otherwise
	 */
	public boolean isValid(int value);
}
