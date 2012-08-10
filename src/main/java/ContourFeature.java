import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ContourFeature extends Feature {
	private CvContour contour;
	
	public static ContourFeature create(CvContour contour) {
		CvMemStorage mem = cvCreateMemStorage(0);
		CvBox2D box = cvMinAreaRect2(contour, mem);
		return new ContourFeature(box, contour);
	}

	private ContourFeature(CvBox2D box, CvContour contour) {
		super(box);
		
		this.contour = contour;
	}

	public boolean insideOf(ContourFeature other) {
		CvPoint ipoint = new CvPoint(cvGetSeqElem(contour, 0));
		CvPoint2D32f dpoint = cvPoint2D32f(ipoint.x(), ipoint.y());
		double res = cvPointPolygonTest(other.contour, dpoint, 0);
		return res > 0;
	}

	@Override
	public void draw(IplImage img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, 1, 0);
		cvDrawCircle(img, cvPosition(), 1, color, 2, 0, 0);
	}
}
