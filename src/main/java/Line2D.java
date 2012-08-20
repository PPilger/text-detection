import static com.googlecode.javacv.cpp.opencv_core.cvDrawLine;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Line2D {
	private Vector2D position;
	private double angle;

	public Line2D(Vector2D position, double angle) {
		this.position = position;
		this.angle = angle;
	}

	public Line2D(double r, double normalAngle) {
		this.position = new Vector2D(Math.cos(normalAngle) * r, Math.sin(normalAngle) * r);
		this.angle = normalAngle + Math.PI / 2;
		System.out.println("1: " + position.x + " "+ position.y + " " + angle);
	}

	public void draw(IplImage img, CvScalar color) {
		double r = position.length();
		double x = r / Math.sin(angle);
		double y = r / Math.cos(angle);
		
		CvPoint p0 = cvPoint((int) Math.round(x), 0);
		CvPoint p1 = cvPoint(0, (int) Math.round(y));
		
		cvDrawLine(img, p0, p1, color, 2, 0, 0);
	}
}
