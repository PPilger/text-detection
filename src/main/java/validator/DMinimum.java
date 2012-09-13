package validator;

import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A double has to be greater or equal than a specified minimum value to be
 * valid
 * 
 * @author PilgerstorferP
 * 
 */
public class DMinimum implements DValidator {
	private double min;

	public DMinimum(double min) {
		this.min = min;
	}

	public double getMin() {
		return min;
	}

	@Override
	public boolean isValid(double value) {
		return min <= value;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvCmpS(src, min, dst, CV_CMP_GE);
	}
}
