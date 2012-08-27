import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.HashMap;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class MorphologicalProcessor extends SimpleImageProcessor {
	private IplConvKernel strel;

	public MorphologicalProcessor() {
		this(3);
	}

	public MorphologicalProcessor(int size) {
		this.strel = cvCreateStructuringElementEx(size, size, size / 2,
				size / 2, CV_SHAPE_RECT, null);
	}

	public IplConvKernel getStructuringElement() {
		return strel;
	}
}
