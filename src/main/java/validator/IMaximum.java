package validator;

import static com.googlecode.javacv.cpp.opencv_core.CV_CMP_LE;
import static com.googlecode.javacv.cpp.opencv_core.cvCmpS;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * An integer has to be lower or equal than a specified maximum value to be
 * valid
 * 
 * @author PilgerstorferP
 * 
 */
public class IMaximum implements IValidator {
	private int max;

	public IMaximum(int max) {
		this.max = max;
	}

	public int getMax() {
		return max;
	}

	public boolean isValid(int value) {
		return value <= max;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvCmpS(src, max, dst, CV_CMP_LE);
	}
}
