import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageDisplayProcessor extends ImageDisplay implements ImageProcessor {

	public ImageDisplayProcessor(String title) {
		super(title);
	}

	public ImageDisplayProcessor(String title, int width, int height) {
		super(title, width, height);
	}

	@Override
	public void process(IplImage img, IplImage colorImg, IplImage temp) {
		super.show(img);
	}
}
