package image;

import static com.googlecode.javacv.cpp.opencv_core.cvSet;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Sets every pixel from the image black, where the mask is not black.
 * 
 * @author PilgerstorferP
 * 
 */
public class EraseProcessor extends SimpleImageProcessor {
	private IplImage mask;

	/**
	 * @param mask
	 *            the binary-mask used for this operation
	 */
	public EraseProcessor(IplImage mask) {
		this.mask = mask;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvSet(img, CvScalar.BLACK, mask);
	}
}
