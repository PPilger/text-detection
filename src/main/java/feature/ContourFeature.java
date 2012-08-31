package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import application.TextDetection;
import math.Vector2D;

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

	private static double invSqrt2 = 1 / Math.sqrt(2);

	public boolean insideOf(ContourFeature other) {
		if (width() >= other.width() || height() >= other.height()) {
			return false;
		}

		Vector2D v0 = position();
		Vector2D v1 = other.position();
		double dx = v0.x-v1.x;
		dx = dx < 0 ? -dx : dx;
		double dy = v0.y-v1.y;
		dy = dy < 0 ? -dy : dy;

		// width/sqrt(2) as upper bound approximation for the bounding sphere radius
		// max(dx, dy) as lower bound approximation for the distance
		if (Math.max(width(), other.width()) * invSqrt2 < Math.max(dx, dy)) {
			return false;
		}

		CvPoint ipoint = new CvPoint(cvGetSeqElem(contour, 0));
		CvPoint2D32f dpoint = cvPoint2D32f(ipoint.x(), ipoint.y());
		double result = cvPointPolygonTest(other.contour, dpoint, 0);
		return result > 0;
	}

	// OPENCV-implementation of cvPointPolygonTest for integers
	/*
	 * private int pointPolygonTest(CvPoint point) { // the fastest
	 * "pure integer" branch int px = point.x(), py = point.y(); int v0x, v0y;
	 * int vx, vy; CvPoint v; int counter = 0; int total = contour.total(); v =
	 * new CvPoint(cvGetSeqElem(contour, total - 1)); vx = v.x(); vy = v.y();
	 * 
	 * for (int i = 0; i < total; i++) { int dist; v0x = vx; v0y = vy; v = new
	 * CvPoint(cvGetSeqElem(contour, i)); vx = v.x(); vy = v.y();
	 * 
	 * if ((v0y <= py && vy <= py) || (v0y > py && vy > py) || (v0x < px && vx <
	 * px)) { if (py == vy && (px == vx || (py == v0y && ((v0x <= px && px <=
	 * vx) || (vx <= px && px <= v0x))))) { return 0; } continue; }
	 * 
	 * dist = (py - v0y) * (vx - v0x) - (px - v0x) * (vy - v0y); if (dist == 0)
	 * { return 0; } if (vy < v0y) { dist = -dist; } counter += dist > 0 ? 1 :
	 * 0; }
	 * 
	 * return counter % 2 == 0 ? -1 : 1; }
	 */

	@Override
	public void draw(CvArr img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, 1, 0);
		cvDrawCircle(img, cvPosition(), 1, color, 2, 0, 0);
	}

	@Override
	public void fill(CvArr img, CvScalar color) {
		cvDrawContours(img, contour, color, color, -1, -1, 0);
	}
}
