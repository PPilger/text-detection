import static com.googlecode.javacv.cpp.opencv_core.cvNot;


import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class InvertProcessor implements ImageProcessor {

	@Override
	public void process(ImageCollection images) {
		IplImage processed = images.getProcessed();
		
		cvNot(processed, processed);
	}
}
