import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class TempDisplayProcessor extends ImageDisplayProcessor {

	public TempDisplayProcessor(String title) {
		super(title);
	}

	public TempDisplayProcessor(String title, int width, int height) {
		super(title, width, height);
	}

	@Override
	public void process(IplImage img, IplImage colorImg, IplImage temp) {
		super.process(temp, null, null);
	}

}
