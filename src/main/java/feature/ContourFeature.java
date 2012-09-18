package feature;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint2D32f;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CLOCKWISE;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexHull2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMinAreaRect2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvPointPolygonTest;
import math.Vector;

import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;

/**
 * This feature is based on a contour in an image.
 * 
 * @author PilgerstorferP
 * 
 */
public class ContourFeature extends Feature {

	private static CvMemStorage mem = cvCreateMemStorage(0);
	private CvContour contour;
	private int[] hull;

	public static ContourFeature create(CvContour contour) {
		CvBox2D box = cvMinAreaRect2(contour, mem);

		return new ContourFeature(box, contour);
	}

	private ContourFeature(CvBox2D box, CvContour contour) {
		super(box);

		this.contour = contour;
	}

	private static double invSqrt2 = 1 / Math.sqrt(2);

	/**
	 * @param other
	 * @return true if this feature is inside the other or false otherwise
	 */
	public boolean insideOf(ContourFeature other) {
		// if the other feature is smaller, this feature cant be inside the
		// other
		if (getWidth() >= other.getWidth() || getHeight() >= other.getHeight()) {
			return false;
		}

		Vector v0 = getCenter();
		Vector v1 = other.getCenter();
		double dx = v0.x - v1.x;
		dx = dx < 0 ? -dx : dx; // equals dx = Math.abs(dx);
		double dy = v0.y - v1.y;
		dy = dy < 0 ? -dy : dy; // equals dy = Math.abs(dy);

		// width/sqrt(2) as upper bound approximation for the bounding sphere
		// radius
		// max(dx, dy) as lower bound approximation for the distance
		if (Math.max(getWidth(), other.getWidth()) * invSqrt2 < Math
				.max(dx, dy)) {
			return false;
		}

		// test if one point of the contour is inside the other one, or not.
		CvPoint ipoint = new CvPoint(cvGetSeqElem(contour, 0));
		CvPoint2D32f dpoint = cvPoint2D32f(ipoint.x(), ipoint.y());

		double result = cvPointPolygonTest(other.contour, dpoint, 0);

		// if result is > 0, the point is inside (=0 means on the contour, <0
		// means outside)
		return result > 0;
	}

	@Override
	public int[] getConvexHull() {
		if (hull == null) {
			CvSeq cvHull = cvConvexHull2(contour, mem, CV_CLOCKWISE, 1);

			hull = new int[cvHull.total() * 2];

			for (int i = 0, j = 0; i < cvHull.total(); i++, j += 2) {
				CvPoint p = new CvPoint(cvGetSeqElem(cvHull, i));
				hull[j] = p.x();
				hull[j + 1] = p.y();
			}
		}
		return hull;
	}

	@Override
	public void draw(CvArr img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, 1, 0);

		Vector center = getCenter();
		CvPoint cvCenter = cvPoint((int) Math.round(center.x),
				(int) Math.round(center.y));

		cvDrawCircle(img, cvCenter, 1, color, 2, 0, 0);
	}

	@Override
	public void fill(CvArr img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, -1, 0);
	}

	@Override
	public double getRating() {
		return 1;
	}
}
