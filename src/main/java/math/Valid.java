package math;

public class Valid<T extends Number> implements Validator<T> {
	
	@Override
	public boolean isValid(T value) {
		return true;
	}
}
