package miscellanous;

public class Maximum<T extends Number> implements Validator<T> {
	private T max;
	
	public Maximum(T max) {
		this.max = max;
	}
	
	public T getMax() {
		return max;
	}

	public boolean isValid(T value) {
		return value.doubleValue() <= max.doubleValue();
	}
}
