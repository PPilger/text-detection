import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class ColorToGrayProcessor implements ImageProcessor {

	@Override
	public void process(ImageCollection images) {
		IplImage color = images.getColor();
		IplImage processed = images.getProcessed();
		
		cvCvtColor(color, processed, CV_BGR2GRAY);
	}

}
