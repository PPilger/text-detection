package image;

import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Converts an image to a binary image
 * 
 * @author PilgerstorferP
 * 
 */
public class BinaryProcessor extends SimpleImageProcessor {
	private IValidator value;

	/**
	 * @param value defines the range that is set to WHITE (the rest is set to BLACK)
	 */
	public BinaryProcessor(IValidator value) {
		this.value = value;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		value.validate(img, img);
	}
}
