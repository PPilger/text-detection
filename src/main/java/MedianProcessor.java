import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.*;


public class MedianProcessor implements ImageProcessor {

	@Override
	public void process(IplImage img) {
		cvSmooth(img, img, CV_MEDIAN, 3);
	}
	
}
