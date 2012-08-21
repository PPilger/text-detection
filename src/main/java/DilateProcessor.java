import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.Map;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.*;

public class DilateProcessor extends MorphologicalProcessor {
	
	public DilateProcessor() {
		super();
	}

	public DilateProcessor(int size) {
		super(size);
	}
	
	public void process(IplImage processed, IplImage temp) {
		cvDilate(processed, processed, getStructuringElement(), 1);
	}
}
