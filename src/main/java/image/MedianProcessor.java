package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class MedianProcessor extends SimpleImageProcessor {
	private int medianSize;
	
	/**
	 * medianSize has to be odd!
	 * @param medianSize size of the median filter
	 */
	public MedianProcessor(int medianSize) {
		this.medianSize = medianSize;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvSmooth(img, img, CV_MEDIAN, medianSize);
	}
}
