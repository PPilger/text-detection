package miscellanous;

public class Interval<T extends Number> implements Validator<T> {
	private T min;
	private T max;

	public Interval(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	public boolean isValid(T value) {
		return min.doubleValue() <= value.doubleValue()
				&& value.doubleValue() <= max.doubleValue();
	}
}
