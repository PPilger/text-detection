package image;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import java.io.File;

import math.Vector;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Image {
	// permanent images, should not be changed
	private IplImage color; // a three channel image
	private IplImage gray; // the grayscale image of color

	// working images, are used by image processors
	private IplImage img; // input and output of image processors
	private IplImage temp; // temporary image for image processors

	// optional images, are only created if needed (in the getter methods)
	private IplImage red;
	private IplImage green;
	private IplImage blue;

	/**
	 * Creates a new image by loading it from the specified file
	 * 
	 * @param filename
	 */
	public Image(String filename) {
		this(cvLoadImage(filename));
	}

	/**
	 * Creates a new image by using img as the color image
	 * 
	 * @param img
	 *            an 8U1C or 8U3C image (8bit unsigned and 1 or 3 channels)
	 */
	public Image(IplImage img) {
		if (img.nChannels() != 1) {
			// color image
			this.color = img;
			this.gray = IplImage.create(color.cvSize(), IPL_DEPTH_8U, 1);
			cvCvtColor(color, gray, CV_BGR2GRAY);
		} else {
			// grayscale image
			this.color = img;
			this.gray = color;
		}

		this.img = gray.clone();
		this.temp = gray.clone();
	}

	private void initRGB() {
		this.red = gray.clone();
		this.green = gray.clone();
		this.blue = gray.clone();
		cvSplit(color, blue, green, red, null);
	}

	public int getWidth() {
		return img.width();
	}

	public int getHeight() {
		return img.height();
	}

	public IplImage getColor() {
		return color;
	}

	public IplImage getGray() {
		return gray;
	}

	public IplImage getImg() {
		return img;
	}

	public IplImage getTemp() {
		return temp;
	}

	public IplImage getRed() {
		if (red == null) {
			initRGB();
		}
		return red;
	}

	public IplImage getGreen() {
		if (green == null) {
			initRGB();
		}
		return green;
	}

	public IplImage getBlue() {
		if (blue == null) {
			initRGB();
		}
		return blue;
	}

	/**
	 * The image gets processed by the specified processor
	 * 
	 * @param processor
	 */
	public void process(ImageProcessor processor) {
		processor.process(this);
	}

	/**
	 * Writes the image to a the specified file. May create non existent folders
	 * specified in filename.
	 * 
	 * @param img
	 * @param filename
	 */
	public static void write(IplImage img, String filename) {
		File f = new File(filename);
		if (f.getParentFile() != null) {
			f.getParentFile().mkdirs();
		}
		cvSaveImage(filename, img);
	}

	/**
	 * Clips the (axis aligned) rectangle specified by the points min and max to
	 * the image size.
	 * 
	 * @param img
	 * @param min
	 * @param max
	 * @return the clipped rectangle
	 */
	public static CvRect clip(IplImage img, Vector min, Vector max) {
		int xmin = Image.clipX(img, min.x);
		int xmax = Image.clipX(img, max.x);
		int ymin = Image.clipY(img, min.y);
		int ymax = Image.clipY(img, max.y);
		return cvRect(xmin, ymin, xmax - xmin, ymax - ymin);
	}

	private static int clipX(IplImage img, double x) {
		return (int) Math.round(Math.min(Math.max(x, 0), img.width() - 1));
	}

	private static int clipY(IplImage img, double y) {
		return (int) Math.round(Math.min(Math.max(y, 0), img.height() - 1));
	}
}
