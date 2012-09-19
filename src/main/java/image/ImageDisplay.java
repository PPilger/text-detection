package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAdd;
import static com.googlecode.javacv.cpp.opencv_core.cvAndS;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSetZero;
import static com.googlecode.javacv.cpp.opencv_highgui.cvResizeWindow;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * An object that shows images in a window
 * 
 * @author PilgerstorferP
 * 
 */
public class ImageDisplay {
	int width;
	int height;
	String title;

	/**
	 * @param title
	 *            title of the window
	 */
	public ImageDisplay(String title) {
		this.title = title;
		this.width = -1;
		this.height = -1;
	}

	/**
	 * @param title
	 *            title of the window
	 * @param width
	 *            maximum width
	 * @param height
	 *            maximum height
	 */
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

	/**
	 * Displays all pixels of img where mask is not zero. All other pixels are displayed black.
	 * @param img
	 * @param mask an 8U1C image (8 bit unsigned, 1 channel)
	 */
	public void show(IplImage img, IplImage mask) {
		width = (width == -1) ? img.width() : Math.min(img.width(), width);
		height = (height == -1) ? img.height() : Math.min(img.height(), height);

		IplImage temp = img.clone();
		cvSetZero(temp);
		cvAndS(img, CvScalar.WHITE, temp, mask);

		cvShowImage(title, temp);

		cvResizeWindow(title, width, height);
		cvWaitKey();
	}

	/**
	 * Adds all forwarded images together and displays the result.
	 * 
	 * @param images
	 *            have to be of the same type
	 */
	public void show(IplImage... images) {
		width = (width == -1) ? images[0].width() : Math.min(images[0].width(),
				width);
		height = (height == -1) ? images[0].height() : Math.min(
				images[0].height(), height);

		IplImage img = cvCloneImage(images[0]);
		for (int i = 1; i < images.length; i++) {
			cvAdd(img, images[i], img, null);
		}

		cvShowImage(title, img);

		cvResizeWindow(title, width, height);
		cvWaitKey();
	}
}
