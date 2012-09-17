package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_OPEN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Does an open image operation.
 * 
 * @author PilgerstorferP
 * 
 */
public class OpenProcessor extends MorphologicalProcessor {

	/**
	 * size has to be odd!
	 * 
	 * @param size
	 *            size of the structuring element
	 */
	public OpenProcessor(int size) {
		super(size);
	}

	public void process(IplImage processed, IplImage temp) {
		cvMorphologyEx(processed, processed, null, getStructuringElement(),
				CV_MOP_OPEN, 1);
	}
}
