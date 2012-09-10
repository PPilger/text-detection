package image;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class MedianProcessor extends SimpleImageProcessor {
	private int size;
	
	public MedianProcessor(int size) {
		this.size = size;
	}
	
	public void process(IplImage img, IplImage temp) {
		cvSmooth(img, temp, CV_MEDIAN, size);
	}
}
