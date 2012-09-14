package image;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * For most of the processors the two images img and temp suffice. So this class
 * defines a simpler process method, where img and temp are already stored in
 * variables.
 * 
 * @author PilgerstorferP
 * 
 */
public abstract class SimpleImageProcessor implements ImageProcessor {
	public abstract void process(IplImage img, IplImage temp);

	public void process(Image image) {
		process(image.getImg(), image.getTemp());
	}
}
