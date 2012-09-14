package math;

import static com.googlecode.javacv.cpp.opencv_core.cvDrawLine;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Represents a line with infinite length.
 * 
 * @author PilgerstorferP
 * 
 */
public class InfiniteLine {
	private Vector position;
	private Angle180 angle;

	public InfiniteLine(Vector position, double angle) {
		this.position = position;
		this.angle = new Angle180(angle);
	}

	/**
	 * @param r
	 *            the distance of the line to the origin
	 * @param normalAngle
	 *            the angle normal to the line
	 */
	public InfiniteLine(double r, double normalAngle) {
		this.position = new Vector(Math.cos(normalAngle) * r,
				Math.sin(normalAngle) * r);
		this.angle = new Angle180(normalAngle + Math.PI / 2);
	}

	/**
	 * Draws the line onto the image img
	 * 
	 * @param img
	 *            a square image
	 * @param color
	 *            the color of the line
	 */
	public void draw(IplImage img, CvScalar color) {
		int w = img.width();
		int h = img.height();

		// calculate the intersections of the line with the image edges
		double x0; // intersection on the bottom edge
		double x1; // intersection on the top edge
		double y0; // intersection on the left edge
		double y1; // intersection on the right edge
		{
			double rad = angle.getRadians();
			if (Math.PI / 4 < rad && rad < Math.PI * 3 / 4) {
				double k = Math.tan(Math.PI / 2 - angle.getRadians());
				double d = position.x - k * position.y;

				// solve y = k * x + d
				y0 = -d / k;
				y1 = (w - d) / k;
				x0 = d;
				x1 = k * h + d;
			} else {
				double k = Math.tan(angle.getRadians());
				double d = position.y - k * position.x;

				// solve y = k * x + d
				x0 = -d / k;
				x1 = (h - d) / k;
				y0 = d;
				y1 = k * w + d;
			}
		}

		// determine the valid intersection points (on the border of the image)
		CvPoint[] p = new CvPoint[4];
		int i = 0;
		{
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
		}

		if (i > 1) {
			cvDrawLine(img, p[0], p[1], color, 1, 0, 0);
		}
	}
}
