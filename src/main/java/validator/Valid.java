package validator;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;


public class Valid implements IValidator, DValidator {

	@Override
	public boolean isValid(int value) {
		return true;
	}
	
	@Override
	public boolean isValid(double value) {
		return true;
	}

	@Override
	public void validate(IplImage src, IplImage dst) {
		cvSet(dst, CvScalar.WHITE);
	}
}
