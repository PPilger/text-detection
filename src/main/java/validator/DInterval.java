package validator;

import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A double has to be inside the specified range [min, max] (incl.) to be valid
 * 
 * @author PilgerstorferP
 * 
 */
public class DInterval implements DValidator {
	private double min;
	private double max;

	/**
	 * Creates an Interval around the value x with the specified delta
	 * 
	 * @param x
	 * @param delta
	 *            the radius of the interval; has to be positive
	 * @return Interval around the value x
	 */
	public static DInterval around(double x, double delta) {
		return new DInterval(x - delta, x + delta);
	}

	public DInterval(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	@Override
	public boolean isValid(double value) {
		return min <= value && value <= max;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvInRangeS(src, cvScalarAll(min), cvScalarAll(max), dst);
	}
}
