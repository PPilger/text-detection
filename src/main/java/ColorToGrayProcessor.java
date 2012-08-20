import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class ColorToGrayProcessor implements ImageProcessor {

	@Override
	public void process(IplImage img, IplImage colorImg) {
		cvCvtColor(colorImg, img, CV_BGR2GRAY);
	}

}
