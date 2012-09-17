package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Does an erode image operation.
 * 
 * @author PilgerstorferP
 * 
 */
public class ErodeProcessor extends MorphologicalProcessor {

	/**
	 * size has to be odd!
	 * 
	 * @param size
	 *            size of the structuring element
	 */
	public ErodeProcessor(int size) {
		super(size);
	}

	public void process(IplImage processed, IplImage temp) {
		cvErode(processed, processed, getStructuringElement(), 1);
	}
}
