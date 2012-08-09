import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OpenProcessor extends MorphologicalProcessor {
	public OpenProcessor() {
		super();
	}

	public OpenProcessor(int size) {
		super(size);
	}

	@Override
	public void process(IplImage img) {
		cvMorphologyEx(img, img, null, getStructuringElement(), CV_MOP_OPEN, 1);
	}
}
