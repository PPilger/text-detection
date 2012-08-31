package image;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_core.*;
import math.Vector2D;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Image {
	private IplImage color;
	private IplImage gray;
	private IplImage img;
	private IplImage temp;
	private IplImage red;
	private IplImage green;
	private IplImage blue;
	private IplImage rgRatio;
	private IplImage rbRatio;
	private IplImage gbRatio;

	public Image(String filename) {
		this(cvLoadImage(filename));
	}

	public Image(IplImage img) {
		if (img.nChannels() != 1) {
			this.color = img;
			this.gray = IplImage.create(color.cvSize(), IPL_DEPTH_8U, 1);
			cvCvtColor(color, gray, CV_BGR2GRAY);
		} else {
			this.color = img;
			this.gray = color.clone();
		}

		this.img = gray.clone();
		this.temp = gray.clone();
	}

	public void setROI(int x, int y, int width, int height) {
		setROI(x, y, width, height);
	}

	public void setROI(CvRect rect) {
		cvSetImageROI(color, rect);
		cvSetImageROI(gray, rect);
		cvSetImageROI(img, rect);
		cvSetImageROI(temp, rect);
		if (red != null) {
			cvSetImageROI(red, rect);
			cvSetImageROI(green, rect);
			cvSetImageROI(blue, rect);
		}
		if (rgRatio != null){
			cvSetImageROI(rgRatio, rect);
			cvSetImageROI(rbRatio, rect);
			cvSetImageROI(gbRatio, rect);
		}
	}
	
	public void resetROI() {
		cvResetImageROI(color);
		cvResetImageROI(gray);
		cvResetImageROI(img);
		cvResetImageROI(temp);
		cvResetImageROI(red);
		cvResetImageROI(green);
		cvResetImageROI(blue);
		cvResetImageROI(rgRatio);
		cvResetImageROI(rbRatio);
		cvResetImageROI(gbRatio);
	}

	public void initRGB() {
		this.red = gray.clone();
		this.green = gray.clone();
		this.blue = gray.clone();
		cvSplit(color, blue, green, red, null);
	}

	public void initRatios() {
		if(red == null) {
			initRGB();
		}
		this.rgRatio = IplImage.create(gray.cvSize(), IPL_DEPTH_32F, 1);
		this.rbRatio = cvCloneImage(rgRatio);
		this.gbRatio = cvCloneImage(rgRatio);

		cvDiv(red, green, rgRatio, 1);
		cvDiv(red, blue, rbRatio, 1);
		cvDiv(green, blue, gbRatio, 1);
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

	public IplImage getRgRatio() {
		if (rgRatio == null) {
			initRatios();
		}
		return rgRatio;
	}

	public IplImage getRbRatio() {
		if (rbRatio == null) {
			initRatios();
		}
		return rbRatio;
	}

	public IplImage getGbRatio() {
		if (gbRatio == null) {
			initRatios();
		}
		return gbRatio;
	}

	public static void write(IplImage img, String filename) {
		cvSaveImage(filename, img);
	}

	public static CvRect clip(IplImage img, Vector2D min, Vector2D max) {
		int xmin = Image.clipX(img, min.x);
		int xmax = Image.clipX(img, max.x);
		int ymin = Image.clipY(img, min.y);
		int ymax = Image.clipY(img, max.y);
		return cvRect(xmin, ymin, xmax - xmin, ymax - ymin);
	}

	public static CvRect clip(IplImage img, double xmin, double ymin, double xmax, double ymax) {
		int xminI = Image.clipX(img, xmin);
		int xmaxI = Image.clipX(img, xmax);
		int yminI = Image.clipY(img, ymin);
		int ymaxI = Image.clipY(img, ymax);
		return cvRect(xminI, yminI, xmaxI - xminI, ymaxI - yminI);
	}

	public static int clipX(IplImage img, double x) {
		return (int) Math.round(Math.min(Math.max(x, 0), img.width() - 1));
	}

	public static int clipY(IplImage img, double y) {
		return (int) Math.round(Math.min(Math.max(y, 0), img.height() - 1));
	}

	public void process(ImageProcessor processor) {
		processor.process(this);
	}
}
