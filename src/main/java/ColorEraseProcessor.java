import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ColorEraseProcessor implements ImageProcessor {
	private int rMin;
	private int rMax;
	private int gMin;
	private int gMax;
	private int bMin;
	private int bMax;
	private int border;
	private boolean and;

	public ColorEraseProcessor(int rMin, int rMax, int gMin, int gMax,
			int bMin, int bMax, int border, boolean and) {
		this.rMin = rMin;
		this.rMax = rMax;
		this.gMin = gMin;
		this.gMax = gMax;
		this.bMin = bMin;
		this.bMax = bMax;
		this.border = border;
		this.and = and;
	}

	@Override
	public void process(ImageCollection images) {
		IplImage processed = images.getProcessed();
		IplImage temp = images.getTemp();
		
		IplImage red = cvCloneImage(images.getRed());
		IplImage green = cvCloneImage(images.getGreen());
		IplImage blue = cvCloneImage(images.getBlue());

		cvInRangeS(red, cvScalarAll(rMin), cvScalarAll(rMax), red);
		cvInRangeS(green, cvScalarAll(gMin), cvScalarAll(gMax), green);
		cvInRangeS(blue, cvScalarAll(bMin), cvScalarAll(bMax), blue);

		cvSetZero(temp);
		
		if(and) {
			cvAnd(red, green, temp, null);
			cvAnd(temp, blue, temp, null);
		} else {
			cvOr(red, green, temp, null);
			cvOr(temp, blue, temp, null);
		}
		
		new DilateProcessor(border).process(temp, null);

		cvSet(processed, CvScalar.BLACK, temp);
	}

}
