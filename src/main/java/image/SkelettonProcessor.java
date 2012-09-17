package image;

import static com.googlecode.javacv.cpp.opencv_core.CV_CMP_EQ;
import static com.googlecode.javacv.cpp.opencv_core.CV_CMP_GT;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_core.cvCmp;
import static com.googlecode.javacv.cpp.opencv_core.cvCmpS;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_DIST_L1;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_SHAPE_CROSS;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateStructuringElementEx;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDistTransform;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

/**
 * Makes a skeleton transformation and removes all objects with an invalid
 * thickness.
 * 
 * @author PilgerstorferP
 * 
 */
public class SkelettonProcessor implements ImageProcessor {
	private IValidator thickness;

	public SkelettonProcessor(IValidator thickness) {
		this.thickness = thickness;
	}

	@Override
	public void process(Image image) {
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();

		IplImage centers = IplImage.create(temp.cvSize(), IPL_DEPTH_8U, 1);
		IplImage distance = IplImage.create(temp.cvSize(), IPL_DEPTH_8U, 1);

		// do a distance transformation
		cvDistTransform(img, distance, CV_DIST_L1, 3, null, null, 0);

		// get the centers of all objects
		IplConvKernel strel = cvCreateStructuringElementEx(3, 3, 1, 1,
				CV_SHAPE_CROSS, null);
		cvDilate(distance, temp, strel, 1);
		cvCmp(distance, temp, centers, CV_CMP_EQ);

		// set the centers to the distance-value (distance to the next 0)
		cvCmpS(centers, 0, temp, CV_CMP_GT);
		cvAnd(distance, temp, centers, null);

		// set all valid centers to white
		thickness.validate(centers, img);

		// remove all pixel with distance null (no center)
		cvCmpS(centers, 0, temp, CV_CMP_EQ);
		cvSet(img, CvScalar.BLACK, temp);
	}
}
