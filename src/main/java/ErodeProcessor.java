import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.Map;

import com.googlecode.javacv.cpp.opencv_core.*;

public class ErodeProcessor extends MorphologicalProcessor {
	public ErodeProcessor() {
		super();
	}

	public ErodeProcessor(int size) {
		super(size);
	}
	
	public void process(IplImage processed) {
		cvErode(processed, processed, getStructuringElement(), 1);
	}
}
