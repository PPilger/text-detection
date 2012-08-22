import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;


import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ThresholdProcessor extends SimpleImageProcessor {
	private int threshold;

	public ThresholdProcessor(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvThreshold(img, img, threshold, 255, CV_THRESH_BINARY);
	}
}
