package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvLaplace;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Removes areas with invalid second derivatives. The second derivative is
 * computed with a Laplacian filter
 * 
 * @author PilgerstorferP
 * 
 */
public class SecondDerivativeProcessor implements ImageProcessor {
	private int laplaceSize;
	private IValidator secondDerivative;
	private int dilSize;

	/**
	 * laplaceSize and dilSize have to be odd
	 * 
	 * @param laplaceSize
	 *            the size of the Laplacian filter
	 * @param secondDerivative
	 *            defines which second derivatives are valid
	 * @param dilSize
	 *            the size of the dilate operation applied after validity check
	 */
	public SecondDerivativeProcessor(int laplaceSize,
			IValidator secondDerivative, int dilSize) {
		this.laplaceSize = laplaceSize;
		this.secondDerivative = secondDerivative;
		this.dilSize = dilSize;
	}

	@Override
	public void process(Image image) {
		IplImage gray = image.getGray();
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();

		cvLaplace(gray, temp, laplaceSize);

		secondDerivative.validate(temp, temp);

		new DilateProcessor(dilSize).process(temp, null);

		cvAnd(img, temp, img, null);
	}

}
