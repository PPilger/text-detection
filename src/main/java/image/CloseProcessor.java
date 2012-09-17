package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_CLOSE;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMorphologyEx;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Does a close image operation.
 * 
 * @author PilgerstorferP
 * 
 */
public class CloseProcessor extends MorphologicalProcessor {

	/**
	 * size has to be odd!
	 * 
	 * @param size
	 *            size of the structuring element
	 */
	public CloseProcessor(int size) {
		super(size);
	}

	public void process(IplImage processed, IplImage temp) {
		cvMorphologyEx(processed, processed, null, getStructuringElement(),
				CV_MOP_CLOSE, 1);
	}
}
