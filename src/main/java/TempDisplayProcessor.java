import static com.googlecode.javacv.cpp.opencv_highgui.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class TempDisplayProcessor implements ImageProcessor {
	private String title;

	public TempDisplayProcessor(String title) {
		this.title = title;
	}

	@Override
	public void process(IplImage img, IplImage colorImg, IplImage temp) {
		cvShowImage(title, temp);
		cvResizeWindow(title, Math.min(temp.width(), 1200), Math.min(temp.height(), 800));
		cvWaitKey();
	}

}
