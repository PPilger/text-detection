package image;

import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_core.cvSetZero;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Erases thick parts in the image. This is done by removing the opened image
 * from the original (the difference between the opened image and the original
 * is the result).
 * 
 * @author PilgerstorferP
 * 
 */
public class ThicknessProcessor extends SimpleImageProcessor {
	private int openSize;
	private int dilSize;

	/**
	 * @param openSize
	 *            the size of the open operator
	 * @param dilSize
	 *            the size of the dilate operator used to dilate the opened
	 *            region in order to remove artifacts on the edge of thick
	 *            objects.
	 */
	public ThicknessProcessor(int openSize, int dilSize) {
		this.openSize = openSize;
		this.dilSize = dilSize;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvSetZero(temp);
		cvSet(temp, CvScalar.WHITE, img);

		new OpenProcessor(openSize).process(temp, null);
		new DilateProcessor(dilSize).process(temp, null);

		cvSet(img, CvScalar.BLACK, temp);
	}

}
