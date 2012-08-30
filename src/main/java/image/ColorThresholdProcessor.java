package image;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;


import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ColorThresholdProcessor implements ImageProcessor {
	private int rThreshold;
	private int gThreshold;
	private int bThreshold;
	private boolean and;

	public ColorThresholdProcessor(int rThreshold,
			int gThreshold, int bThreshold, boolean and) {
		this.rThreshold = rThreshold;
		this.gThreshold = gThreshold;
		this.bThreshold = bThreshold;
		this.and = and;
	}

	@Override
	public void process(Image image) {
		IplImage processed = image.getImg();
		IplImage temp = image.getTemp();
		IplImage red = cvCloneImage(image.getRed());
		IplImage green = cvCloneImage(image.getGreen());
		IplImage blue = cvCloneImage(image.getBlue());
		
		cvThreshold(red, red, rThreshold, 255, CV_THRESH_BINARY);
		cvThreshold(green, green, gThreshold, 255, CV_THRESH_BINARY);
		cvThreshold(blue, blue, bThreshold, 255, CV_THRESH_BINARY);

		if (and) {
			cvAnd(red, green, temp, null);
			cvAnd(temp, blue, temp, null);
		} else {
			cvOr(red, green, temp, null);
			cvOr(temp, blue, temp, null);
		}

		cvSetZero(processed);
		cvSet(processed, CvScalar.WHITE, temp);
	}

}