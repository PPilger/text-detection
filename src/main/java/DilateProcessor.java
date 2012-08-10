import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.*;

public class DilateProcessor extends MorphologicalProcessor {
	
	public DilateProcessor() {
		super();
	}

	public DilateProcessor(int size) {
		super(size);
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		cvDilate(img, img, getStructuringElement(), 1);
	}
}
