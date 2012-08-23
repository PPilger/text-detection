import static com.googlecode.javacv.cpp.opencv_core.*;

public class EraseProcessor extends SimpleImageProcessor {
	private IplImage mask;

	public EraseProcessor(IplImage mask) {
		this.mask = mask;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvSet(img, CvScalar.BLACK, mask);
	}
}
