package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Does a dilate image operation.
 * 
 * @author PilgerstorferP
 * 
 */
public class DilateProcessor extends MorphologicalProcessor {

	/**
	 * size has to be odd!
	 * 
	 * @param size
	 *            size of the structuring element
	 */
	public DilateProcessor(int size) {
		super(size);
	}

	public void process(IplImage processed, IplImage temp) {
		cvDilate(processed, processed, getStructuringElement(), 1);
	}
}
