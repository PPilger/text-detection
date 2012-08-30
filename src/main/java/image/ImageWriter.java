package image;

import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ImageWriter {
	
	public void write(IplImage img, String filename) {
		cvSaveImage(filename, img);
	}
}
