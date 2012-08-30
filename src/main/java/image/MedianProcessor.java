package image;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.*;


public class MedianProcessor extends SimpleImageProcessor {
	private int size;
	
	public MedianProcessor(int size) {
		this.size = size;
	}
	
	public void process(IplImage processed, IplImage temp) {
		cvSmooth(processed, processed, CV_MEDIAN, size);
	}
}
