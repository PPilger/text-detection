package image;

import static com.googlecode.javacv.cpp.opencv_core.cvMax;
import static com.googlecode.javacv.cpp.opencv_core.cvSub;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Creates a binary image by removing the background of the image. The
 * background is calculated using a median filter.
 * 
 * @author PilgerstorferP
 * 
 */
public class BackgroundProcessor implements ImageProcessor {
	private int medianSize;
	private IValidator difference;

	/**
	 * smmothingSize has to be odd
	 * 
	 * @param medianSize
	 *            the size of the median filter used for determining the
	 *            background
	 * @param difference
	 *            defines the valid differences between the background and the
	 *            image
	 */
	public BackgroundProcessor(int medianSize, IValidator difference) {
		this.medianSize = medianSize;
		this.difference = difference;
	}

	@Override
	public void process(Image image) {
		IplImage gray = image.getGray();
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();

		// calculate the background image
		cvSmooth(gray, temp, CV_MEDIAN, medianSize);

		// calculate the difference of original and background image
		cvSub(temp, gray, img, null);
		cvSub(gray, temp, temp, null);
		cvMax(temp, img, img);

		// make img to a binary image
		difference.validate(img, img);
	}
}
