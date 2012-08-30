package image;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class MaskProcessor extends SimpleImageProcessor {
	private IplImage mask;
	
	public MaskProcessor(IplImage mask) {
		this.mask = mask;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvAnd(img, mask, img, null);
	}

}
