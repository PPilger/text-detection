package image;
import static com.googlecode.javacv.cpp.opencv_core.cvAdd;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
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

	public void show(IplImage... images) {
		width = (width == -1) ? images[0].width() : Math.min(images[0].width(), width);
		height = (height == -1) ? images[0].height() : Math.min(images[0].height(), height);

		IplImage img = cvCloneImage(images[0]);
		for(int i = 1; i < images.length; i++) {
			cvAdd(img, images[i], img, null);
		}
		
		cvShowImage(title, img);

		cvResizeWindow(title, width, height);
		cvWaitKey();
	}
}
