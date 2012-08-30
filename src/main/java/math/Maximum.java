package math;

public class Maximum<T extends Number> extends Interval<T> {
	public Maximum(T max) {
		super(max, max);
	}

	public boolean isValid(T value) {
		return value.doubleValue() <= getMax().doubleValue();
	}
}
