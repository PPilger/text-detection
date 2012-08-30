package feature;
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
		double result = cvPointPolygonTest(other.contour, dpoint, 0);
		return result > 0;
	}

	// OPENCV-implementation of cvPointPolygonTest for integers
	/*private int pointPolygonTest(CvPoint point) {
		// the fastest "pure integer" branch
		int px = point.x(), py = point.y();
		int v0x, v0y;
		int vx, vy;
		CvPoint v;
		int counter = 0;
		int total = contour.total();
		v = new CvPoint(cvGetSeqElem(contour, total - 1));
		vx = v.x();
		vy = v.y();

		for (int i = 0; i < total; i++) {
			int dist;
			v0x = vx;
			v0y = vy;
			v = new CvPoint(cvGetSeqElem(contour, i));
			vx = v.x();
			vy = v.y();

			if ((v0y <= py && vy <= py) || (v0y > py && vy > py)
					|| (v0x < px && vx < px)) {
				if (py == vy
						&& (px == vx || (py == v0y && ((v0x <= px && px <= vx) || (vx <= px && px <= v0x))))) {
					return 0;
				}
				continue;
			}

			dist = (py - v0y) * (vx - v0x) - (px - v0x) * (vy - v0y);
			if (dist == 0) {
				return 0;
			}
			if (vy < v0y) {
				dist = -dist;
			}
			counter += dist > 0 ? 1 : 0;
		}

		return counter % 2 == 0 ? -1 : 1;
	}*/

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
