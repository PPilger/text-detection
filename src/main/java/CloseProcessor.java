import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CloseProcessor extends MorphologicalProcessor {
	public CloseProcessor() {
		super();
	}

	public CloseProcessor(int size) {
		super(size);
	}

	@Override
	public void process(IplImage img, IplImage colorImage, IplImage temp) {
		cvMorphologyEx(img, img, null, getStructuringElement(), CV_MOP_CLOSE, 1);
	}
}
