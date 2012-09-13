package validator;

import static com.googlecode.javacv.cpp.opencv_core.CV_CMP_LE;
import static com.googlecode.javacv.cpp.opencv_core.cvCmpS;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A double has to be lower or equal than a specified maximum value to be valid
 * 
 * @author PilgerstorferP
 * 
 */
public class DMaximum implements DValidator {
	private double max;

	public DMaximum(double max) {
		this.max = max;
	}

	public double getMax() {
		return max;
	}

	@Override
	public boolean isValid(double value) {
		return value <= max;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvCmpS(src, max, dst, CV_CMP_LE);
	}
}
