package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BLUR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This processor can mask out regions with low density.
 * 
 * @author PilgerstorferP
 */
public class DensityProcessor extends SimpleImageProcessor {
	private int areaSize;
	private int dilSize;
	private IValidator density;

	/**
	 * areaSize and dilSize have to be odd numbers
	 * 
	 * @param areaSize
	 *            the size of the area that is taken into account for the
	 *            density calculation
	 * @param dilSize
	 *            the size of the dilation of the density levels
	 * @param density
	 *            defines the valid densities
	 */
	public DensityProcessor(int areaSize, int dilSize, IValidator density) {
		this.areaSize = areaSize;
		this.dilSize = dilSize;
		this.density = density;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		// calculate the density (average pixel value in the area around a
		// pixel)
		cvSmooth(img, temp, CV_BLUR, areaSize, areaSize, 0, 0);

		// dilate the density, so that parts at the edge of a region also get
		// ranked with a high density
		new DilateProcessor(dilSize).process(temp, null);

		// remove everything from img, where the density is lower than min
		cvAnd(temp, img, temp, null);
		density.validate(temp, img);
	}
}
