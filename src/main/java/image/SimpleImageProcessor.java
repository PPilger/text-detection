package image;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class SimpleImageProcessor implements ImageProcessor {
	public abstract void process(IplImage img, IplImage temp);

	public void process(Image image) {
		process(image.getImg(), image.getTemp());
	}
}
