package image;

import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_core.cvOr;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class BinaryColorProcessor implements ImageProcessor {
	private IValidator red;
	private IValidator green;
	private IValidator blue;
	private boolean and;

	public BinaryColorProcessor(IValidator red, IValidator green,
			IValidator blue, boolean and) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.and = and;
	}

	@Override
	public void process(Image image) {
		IplImage img = image.getImg();
		IplImage red = cvCloneImage(image.getRed());
		IplImage green = cvCloneImage(image.getGreen());
		IplImage blue = cvCloneImage(image.getBlue());

		this.red.validate(red, red);
		this.green.validate(green, green);
		this.blue.validate(blue, blue);

		if (and) {
			cvAnd(red, green, img, null);
			cvAnd(img, blue, img, null);
		} else {
			cvOr(red, green, img, null);
			cvOr(img, blue, img, null);
		}
	}
}
