package image;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.cvConvert;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseMemStorage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_NONE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import validator.IValidator;

import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Removes objects with an invalid perimeter.
 * 
 * @author PilgerstorferP
 * 
 */
public class PerimeterProcessor extends SimpleImageProcessor {
	private IValidator perimeter;

	public PerimeterProcessor(IValidator perimeter) {
		super();
		this.perimeter = perimeter;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		CvMemStorage mem = cvCreateMemStorage(0);
		CvSeq contour = new CvSeq();

		cvConvert(img, temp);
		cvFindContours(temp, mem, contour, sizeof(CvContour.class),
				CV_RETR_LIST, CV_CHAIN_APPROX_NONE);

		for (; contour != null; contour = contour.h_next()) {
			if (!perimeter.isValid(contour.total())) {
				// remove object
				cvDrawContours(img, contour, CvScalar.BLACK, CvScalar.BLACK, 0,
						CV_FILLED, 8);
			}
		}
		
		cvReleaseMemStorage(mem);
	}

}
