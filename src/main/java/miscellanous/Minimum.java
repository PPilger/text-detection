package miscellanous;

public class Minimum<T extends Number> implements Validator<T> {
	private T min;
	
	public Minimum(T min) {
		this.min = min;
	}
	
	public T getMin() {
		return min;
	}

	public boolean isValid(T value) {
		return min.doubleValue() <= value.doubleValue();
	}
}
