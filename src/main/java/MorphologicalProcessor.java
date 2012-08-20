import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class MorphologicalProcessor implements ImageProcessor {
	private int size;

	public MorphologicalProcessor() {
		this(3);
	}

	public MorphologicalProcessor(int size) {
		this.size = size;
	}

	public IplConvKernel getStructuringElement() {
		return cvCreateStructuringElementEx(size, size, size / 2, size / 2,
				CV_SHAPE_RECT, null);
	}
	
	public abstract void process(IplImage img);
	
	public void process(ImageCollection images) {
		process(images.getProcessed());
	}
}
