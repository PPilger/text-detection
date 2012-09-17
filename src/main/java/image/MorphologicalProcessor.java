package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_RECT;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateStructuringElementEx;

import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

/**
 * A base class for all morphological operations. Provides the structuring
 * element.
 * 
 * @author PilgerstorferP
 * 
 */
public abstract class MorphologicalProcessor extends SimpleImageProcessor {
	private IplConvKernel strel;

	/**
	 * Creates a morphological processor with a rectangular structuring element.
	 * size has to be odd!
	 * 
	 * @param size
	 *            size of the structuring element
	 */
	public MorphologicalProcessor(int size) {
		this.strel = cvCreateStructuringElementEx(size, size, size / 2,
				size / 2, CV_SHAPE_RECT, null);
	}

	public IplConvKernel getStructuringElement() {
		return strel;
	}
}
