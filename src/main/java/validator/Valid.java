package validator;


public class Valid implements IntValidator, DoubleValidator {

	@Override
	public boolean isValid(int value) {
		return true;
	}
	
	@Override
	public boolean isValid(double value) {
		return true;
	}
}
