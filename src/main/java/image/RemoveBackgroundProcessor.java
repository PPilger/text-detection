package image;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class RemoveBackgroundProcessor implements ImageProcessor {
	private int size;
	private int variance;
	
	public RemoveBackgroundProcessor(int size, int variance) {
		this.size = size;
		this.variance = variance;
	}

	@Override
	public void process(Image image) {
		IplImage gray = image.getGray();
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();
		cvSmooth(gray, temp, CV_MEDIAN, size);
		
		cvSub(temp, gray, img, null);
		cvSub(gray, temp, temp, null);
		cvMax(temp, img, img);

		cvThreshold(img, img, variance, 255, CV_THRESH_BINARY);
	}
}
