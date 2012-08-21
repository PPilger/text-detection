import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvLaplace;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class SecondDerivateEraseProcessor implements ImageProcessor {
	private int threshold;
	private int border;
	
	public SecondDerivateEraseProcessor(int threshold, int border) {
		this.threshold = threshold;
		this.border = border;
	}

	@Override
	public void process(ImageCollection images) {
		IplImage gray = images.getGray();
		IplImage processed = images.getProcessed();
		IplImage temp = images.getTemp();
		
		cvLaplace(gray, temp, 3);

		cvThreshold(temp, temp, threshold, 255, CV_THRESH_BINARY);
		
		new DilateProcessor(border).process(temp, null);
		
		cvAnd(processed, temp, processed, null);
	}
	
}
