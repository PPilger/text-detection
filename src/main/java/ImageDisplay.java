import static com.googlecode.javacv.cpp.opencv_highgui.cvResizeWindow;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class ImageDisplay {
	int width;
	int height;
	String title;

	public ImageDisplay(String title) {
		this.title = title;
		this.width = -1;
		this.height = -1;
	}

	public ImageDisplay(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}
	
	public void show(IplImage img) {
		width = (width == -1) ? img.width() : Math.min(img.width(), width);
		height = (height == -1) ? img.height() : Math.min(img.height(), height);

		cvShowImage(title, img);

		cvResizeWindow(title, width, height);
		cvWaitKey();
	}
}
