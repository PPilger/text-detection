import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.Map;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class CloseProcessor extends MorphologicalProcessor {
	public CloseProcessor() {
		super();
	}

	public CloseProcessor(int size) {
		super(size);
	}
	
	public void process(IplImage processed, IplImage temp) {
		cvMorphologyEx(processed, processed, null, getStructuringElement(), CV_MOP_CLOSE, 1);
	}
}
