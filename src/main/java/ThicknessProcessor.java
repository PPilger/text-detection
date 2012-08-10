import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class ThicknessProcessor implements ImageProcessor {
	private int minThickness;
	private int maxThickness;

	public ThicknessProcessor(int maxThickness) {
		this(1, maxThickness);
	}
	
	public ThicknessProcessor(int minThickness, int maxThickness) {
		this.minThickness = minThickness;
		this.maxThickness = maxThickness;
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		IplImage centers = IplImage.create(img.cvSize(), IPL_DEPTH_8U, 1);
		IplImage distance = IplImage.create(img.cvSize(), IPL_DEPTH_8U, 1);
		IplImage temp = IplImage.create(img.cvSize(), IPL_DEPTH_8U, 1);

		cvDistTransform(img, distance, CV_DIST_L1, 3, null, null, 0);

		// get the centers of all objects
		IplConvKernel strel = cvCreateStructuringElementEx(3, 3, 1, 1,
				CV_SHAPE_CROSS, null);
		cvDilate(distance, temp, strel, 1);
		cvCmp(distance, temp, centers, CV_CMP_EQ);

		// set the centers to the distance-value (distance to the next 0)
		cvSetZero(temp);
		cvSet(temp, CvScalar.ONE, centers);
		cvMul(distance, temp, centers, 1);

		// set all valid centers to white
		cvSetZero(img);
		cvCmpS(centers, maxThickness, img, CV_CMP_LT);
		cvCmpS(centers, minThickness, temp, CV_CMP_LT);
		cvSet(img, CvScalar.BLACK, temp);
	}
}
