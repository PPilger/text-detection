package math;

public class Minimum<T extends Number> extends Interval<T> {
	public Minimum(T min) {
		super(min, min);
	}

	public boolean isValid(T value) {
		return getMin().doubleValue() <= value.doubleValue();
	}
}
