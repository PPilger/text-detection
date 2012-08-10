import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.*;


public class MedianProcessor implements ImageProcessor {
	private int size;
	
	public MedianProcessor(int size) {
		this.size = size;
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		cvSmooth(img, img, CV_MEDIAN, size);
	}
	
}
