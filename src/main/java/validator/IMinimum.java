package validator;

import static com.googlecode.javacv.cpp.opencv_core.CV_CMP_GE;
import static com.googlecode.javacv.cpp.opencv_core.cvCmpS;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * An integer has to be greater or equal than a specified minimum value to be
 * valid
 * 
 * @author PilgerstorferP
 * 
 */
public class IMinimum implements IValidator {
	private int min;

	public IMinimum(int min) {
		this.min = min;
	}

	public int getMin() {
		return min;
	}

	public boolean isValid(int value) {
		return min <= value;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvCmpS(src, min, dst, CV_CMP_GE);
	}
}
