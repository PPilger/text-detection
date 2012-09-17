package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Sets every pixel from the image black, where the mask is zero.
 * 
 * @author PilgerstorferP
 * 
 */
public class MaskProcessor extends SimpleImageProcessor {
	private IplImage mask;

	/**
	 * @param mask
	 *            the binary-mask used for this operation
	 */
	public MaskProcessor(IplImage mask) {
		this.mask = mask;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvAnd(img, mask, img, null);
	}
}
