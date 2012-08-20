import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.*;


public class MedianProcessor implements ImageProcessor {
	private int size;
	
	public MedianProcessor(int size) {
		this.size = size;
	}
	
	public void process(IplImage processed) {
		cvSmooth(processed, processed, CV_MEDIAN, size);
	}

	@Override
	public void process(ImageCollection images) {
		process(images.getProcessed());
	}
	
}
