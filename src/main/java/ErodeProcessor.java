import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.*;

public class ErodeProcessor extends MorphologicalProcessor {
	public ErodeProcessor() {
		super();
	}

	public ErodeProcessor(int size) {
		super(size);
	}

	@Override
	public void process(IplImage img) {
		cvErode(img, img, getStructuringElement(), 1);
	}
}
