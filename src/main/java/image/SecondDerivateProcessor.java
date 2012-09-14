package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvLaplace;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Removes areas with invalid second derivates. The second derivate is computed
 * with a Laplacian filter
 * 
 * @author PilgerstorferP
 * 
 */
public class SecondDerivateProcessor implements ImageProcessor {
	private int laplaceSize;
	private IValidator secondDerivate;
	private int dilSize;

	/**
	 * laplaceSize and dilSize have to be odd
	 * 
	 * @param laplaceSize
	 *            the size of the Laplacian filter
	 * @param secondDerivate
	 *            defines which second derivates are valid
	 * @param dilSize
	 *            the size of the dilate operation applied after validity check
	 */
	public SecondDerivateProcessor(int laplaceSize,
			IValidator secondDerivate, int dilSize) {
		this.laplaceSize = laplaceSize;
		this.secondDerivate = secondDerivate;
		this.dilSize = dilSize;
	}

	@Override
	public void process(Image image) {
		IplImage gray = image.getGray();
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();

		cvLaplace(gray, temp, laplaceSize);

		secondDerivate.validate(temp, temp);

		new DilateProcessor(dilSize).process(temp, null);

		cvAnd(img, temp, img, null);
	}

}
