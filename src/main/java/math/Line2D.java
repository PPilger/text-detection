package math;

import static com.googlecode.javacv.cpp.opencv_core.cvDrawLine;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Line2D {
	private Vector2D position;
	private Angle180 angle;

	public Line2D(Vector2D position, double angle) {
		this.position = position;
		this.angle = new Angle180(angle);
	}

	public Line2D(double r, double normalAngle) {
		this.position = new Vector2D(Math.cos(normalAngle) * r,
				Math.sin(normalAngle) * r);
		this.angle = new Angle180(normalAngle + Math.PI / 2);
	}

	public void draw(IplImage img, CvScalar color) {
		CvPoint p[] = new CvPoint[4];

		int w = img.width();
		int h = img.height();

		double x0;
		double x1;
		double y0;
		double y1;
		if (angle.absRadians() > Math.PI / 4) {
			double k = Math.tan(Math.PI / 2 - angle.getRadians());
			double d = position.x - k * position.y;

			y0 = -d / k;
			y1 = (w - d) / k;
			x0 = d;
			x1 = k * h + d;
		} else {
			double k = Math.tan(angle.getRadians());
			double d = position.y - k * position.x;

			x0 = -d / k;
			x1 = (h - d) / k;
			y0 = d;
			y1 = k * w + d;
		}

		int i = 0;
		if (0 <= x0 && x0 <= w) {
			p[i] = cvPoint((int) Math.round(x0), 0);
			i++;
		}
		if (0 <= x1 && x1 <= w) {
			p[i] = cvPoint((int) Math.round(x1), h);
			i++;
		}
		if (0 <= y0 && y0 <= h) {
			p[i] = cvPoint(0, (int) Math.round(y0));
			i++;
		}
		if (0 <= y1 && y1 <= h) {
			p[i] = cvPoint(w, (int) Math.round(y1));
			i++;
		}
		if(i > 1) {
			cvDrawLine(img, p[0], p[1], color, 1, 0, 0);
		}

		/*
		 * double r = position.length(); double x = r / Math.sin(angle); double
		 * y = r / Math.cos(angle);
		 * 
		 * CvPoint p0 = cvPoint((int) Math.round(x), 0); CvPoint p1 = cvPoint(0,
		 * (int) Math.round(y));
		 * 
		 * cvDrawLine(img, p0, p1, color, 2, 0, 0);
		 */
	}
}
