package validator;

import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvScalarAll;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * An integer has to be inside the specified range [min, max] (incl.) to be
 * valid
 * 
 * @author PilgerstorferP
 * 
 */
public class IInterval implements IValidator {
	private int min;
	private int max;

	public IInterval(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public boolean isValid(int value) {
		return min <= value && value <= max;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvInRangeS(src, cvScalarAll(min), cvScalarAll(max), dst);
	}
}
