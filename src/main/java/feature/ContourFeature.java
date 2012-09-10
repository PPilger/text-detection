package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import math.Vector;

public class ContourFeature extends Feature {

	private static CvMemStorage mem = cvCreateMemStorage(0);
	private CvContour contour;
	private int[] corners;

	public static ContourFeature create(CvContour contour) {
		CvBox2D box = cvMinAreaRect2(contour, mem);
		return new ContourFeature(box, contour);
	}

	private ContourFeature(CvBox2D box, CvContour contour) {
		super(box);

		this.contour = contour;
	}

	private static double invSqrt2 = 1 / Math.sqrt(2);

	public boolean insideOf(ContourFeature other) {
		if (getWidth() >= other.getWidth() || getHeight() >= other.getHeight()) {
			return false;
		}

		Vector v0 = getCenter();
		Vector v1 = other.getCenter();
		double dx = v0.x - v1.x;
		dx = dx < 0 ? -dx : dx;
		double dy = v0.y - v1.y;
		dy = dy < 0 ? -dy : dy;

		// width/sqrt(2) as upper bound approximation for the bounding sphere
		// radius
		// max(dx, dy) as lower bound approximation for the distance
		if (Math.max(getWidth(), other.getWidth()) * invSqrt2 < Math.max(dx, dy)) {
			return false;
		}

		CvPoint ipoint = new CvPoint(cvGetSeqElem(contour, 0));
		CvPoint2D32f dpoint = cvPoint2D32f(ipoint.x(), ipoint.y());
		double result = cvPointPolygonTest(other.contour, dpoint, 0);
		return result > 0;
	}
	
	@Override
	public int[] getICorners() {
		if (corners == null) {
			CvSeq hull = cvConvexHull2(contour, mem, CV_CLOCKWISE, 1);

			corners = new int[hull.total() * 2];

			for (int i = 0, j = 0; i < hull.total(); i++, j+=2) {
				CvPoint p = new CvPoint(cvGetSeqElem(hull, i));
				corners[j] = p.x();
				corners[j + 1] = p.y();
			}
		}
		return corners;
	}

	@Override
	public void draw(CvArr img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, 1, 0);
		cvDrawCircle(img, getCvCenter(), 1, color, 2, 0, 0);
	}

	@Override
	public void fill(CvArr img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, -1, 0);
	}
}
