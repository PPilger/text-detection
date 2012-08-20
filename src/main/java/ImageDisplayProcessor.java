import static com.googlecode.javacv.cpp.opencv_highgui.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageDisplayProcessor implements ImageProcessor {
	private String title;

	public ImageDisplayProcessor(String title) {
		this.title = title;
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		cvShowImage(title, img);
		cvResizeWindow(title, Math.min(img.width(), 1200), Math.min(img.height(), 800));
		cvWaitKey();
	}

}
