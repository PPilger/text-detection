import static com.googlecode.javacv.cpp.opencv_core.cvNot;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class InvertProcessor implements ImageProcessor {

	@Override
	public void process(IplImage img, IplImage colorImg) {
		cvNot(img, img);
	}
}
