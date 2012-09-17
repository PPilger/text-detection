package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_core.cvMax;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSobel;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Removes areas with invalid first derivatives. The first derivative is
 * computed using a vertical and a horizontal sobel filter (the maximum of these
 * two).
 * 
 * @author PilgerstorferP
 * 
 */
public class FirstDerivativeProcessor implements ImageProcessor {
	private IValidator firstDerivative;
	private int dilSize;

	/**
	 * dilSize has to be odd
	 * 
	 * @param firstDerivative
	 *            defines which second derivatives are valid
	 * @param dilSize
	 *            the size of the dilate operation applied after validity check
	 */
	public FirstDerivativeProcessor(IValidator firstDerivative, int dilSize) {
		this.firstDerivative = firstDerivative;
		this.dilSize = dilSize;
	}

	@Override
	public void process(Image image) {
		IplImage gray = image.getGray();
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();
		IplImage vTemp = temp.clone();

		cvSobel(gray, temp, 1, 0, 3); // horizontal derivative
		cvSobel(gray, vTemp, 0, 1, 3); // vertical derivative
		cvMax(temp, vTemp, temp);

		firstDerivative.validate(temp, temp);

		new DilateProcessor(dilSize).process(temp, null);

		cvAnd(img, temp, img, null);
	}
}
