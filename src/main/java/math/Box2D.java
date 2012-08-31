package math;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoxPoints;

public class Box2D {
	public Vector2D[] corners;
	public Vector2D min;
	public Vector2D max;

	public Box2D(double x, double y, double width, double height, double angle) {
		this(new CvBox2D(cvPoint2D32f(x, y), cvSize2D32f(width, height),
				(float) Angle180.radToDeg(angle)));
	}

	public Box2D(CvBox2D box) {
		corners = new Vector2D[4];

		float[] coords = new float[8];
		cvBoxPoints(box, coords);

		corners[0] = new Vector2D(coords[0], coords[1]);
		corners[1] = new Vector2D(coords[2], coords[3]);
		corners[2] = new Vector2D(coords[4], coords[5]);
		corners[3] = new Vector2D(coords[6], coords[7]);

		double xmin = corners[0].x;
		double ymin = corners[0].y;
		double xmax = 0;
		double ymax = 0;
		
		for(Vector2D corner : corners) {
			if(corner.x < xmin) {
				xmin = corner.x;
			} else if(xmax < corner.x) {
				xmax = corner.x;
			}

			if(corner.y < ymin) {
				ymin = corner.y;
			} else if(ymax < corner.y) {
				ymax = corner.y;
			}
		}
		
		min = new Vector2D(xmin, ymin);
		max = new Vector2D(xmax, ymax);
	}

	public Vector2D min() {
		double xmin = corners[0].x;
		double ymin = corners[0].y;
		
		for(Vector2D corner : corners) {
			if(corner.x < xmin) {
				xmin = corner.x;
			}

			if(corner.y < ymin) {
				ymin = corner.y;
			}
		}
		
		return new Vector2D(xmin, ymin);
	}

	public Vector2D max() {
		double xmax = 0;
		double ymax = 0;
		
		for(Vector2D corner : corners) {
			if(xmax < corner.x) {
				xmax = corner.x;
			}

			if(ymax < corner.y) {
				ymax = corner.y;
			}
		}
		
		return new Vector2D(xmax, ymax);
	}

	private boolean inside(double value, double bound0, double bound1) {
		return Math.min(bound0, bound1) < value
				&& value < Math.max(bound0, bound1);
	}

	public boolean lineIntersection(Vector2D p0, Vector2D q0, Vector2D p1,
			Vector2D q1) {
		double k0 = (q0.y - p0.y) / (q0.x - p0.x);
		double k1 = (q1.y - p1.y) / (q1.x - p1.x);
		double d0 = p0.y - k0 * p0.x;
		double d1 = p1.y - k1 * p1.x;

		if (k0 == k1 || (Double.isInfinite(k0) && Double.isInfinite(k1))) {
			return false;
		}

		double x;
		if (Double.isInfinite(k0)) {
			x = p0.x;
			double y = k1 * x + d1;

			if (!inside(y, p0.y, p1.y)) {
				return false;
			}
		} else if (Double.isInfinite(k1)) {
			x = p1.x;
			double y = k0 * x + d0;

			if (!inside(y, p0.y, p1.y)) {
				return false;
			}
		} else {
			x = (d1 - d0) / (k0 - k1);
		}

		return inside(x, p0.x, q0.x) && inside(x, p1.x, q1.x);
	}

	public double distance(Box2D other) {
		double distance = Double.POSITIVE_INFINITY;

		Vector2D[][] corners = new Vector2D[2][];
		corners[0] = this.corners;
		corners[1] = other.corners;

		// distance is 0 if the two boxes intersect
		for (int i = 0; i < 4; i++) {
			int in = (i + 1) % 4;

			for (int j = 0; j < 4; j++) {
				int jn = (j + 1) % 4;

				if (lineIntersection(this.corners[i], this.corners[in],
						other.corners[j], other.corners[jn])) {
					return 0;
				}
			}
		}

		// calculate the distance of the two boxes
		for (int i = 0; i < 2; i++) {
			int o = 1 - i;

			double[] edges = new double[4];
			edges[0] = corners[o][0].distance(corners[o][1]);
			edges[1] = corners[o][1].distance(corners[o][2]);
			edges[2] = corners[o][2].distance(corners[o][3]);
			edges[3] = corners[o][3].distance(corners[o][0]);

			for (int iCorner = 0; iCorner < 4; iCorner++) {
				double[] distances = new double[4];
				distances[0] = corners[i][iCorner].distance(corners[o][0]);
				distances[1] = corners[i][iCorner].distance(corners[o][1]);
				distances[2] = corners[i][iCorner].distance(corners[o][2]);
				distances[3] = corners[i][iCorner].distance(corners[o][3]);

				int count = 0;

				for (int iEdge0 = 0; iEdge0 < 4; iEdge0++) {
					int iEdge1 = (iEdge0 + 1) % 4;
					
					// //////////////////////////////////////////////////////////////
					// Formula to calculate the distance between a point and an
					// edge
					// given: points P, Q, R
					// result: distance from P to the edge QR
					//
					// point X ... if you put a line normal to QR through P,
					// X is the intersection-point of the line and QR
					//
					// formula: x = sqrt(a² - ((c² + a² - b²) / (2c))²)
					//
					// variables:
					// a ... |PQ|
					// b ... |PR|
					// c ... |QR|
					// aa, bb, cc ... squares of a, b and c
					// c0 ... |XQ|
					// x ... |PX|
					// //////////////////////////////////////////////////////////////

					double a = distances[iEdge0];
					double b = distances[iEdge1];
					double c = edges[iEdge0];
					double aa = a * a, bb = b * b, cc = c * c;

					distance = Math.min(distance, a);
					distance = Math.min(distance, b);

					double c0 = cc + aa - bb;
					if (c0 < 0) {
						// the value x does not exist, X is not on the edge
						continue;
					}
					c0 /= 2 * c;
					if (c0 > c) {
						// the value x does not exist, X is not on the edge
						continue;
					}

					double x = Math.sqrt(aa - c0 * c0);
					distance = Math.min(distance, x);
					
					count++;
				}

				// if 4 normals (for each edge) through the point exist,
				// the point is inside the box
				if (count == 4) {
					return 0;
				}
			}
		}

		return distance;
	}

	public void draw(CvArr img, CvScalar color) {
		CvPoint pp0 = cvPoint((int) corners[0].x, (int) corners[0].y);
		CvPoint pp1 = cvPoint((int) corners[1].x, (int) corners[1].y);
		CvPoint pp2 = cvPoint((int) corners[2].x, (int) corners[2].y);
		CvPoint pp3 = cvPoint((int) corners[3].x, (int) corners[3].y);

		cvDrawLine(img, pp0, pp1, color, 1, 0, 0);
		cvDrawLine(img, pp1, pp2, color, 1, 0, 0);
		cvDrawLine(img, pp2, pp3, color, 1, 0, 0);
		cvDrawLine(img, pp3, pp0, color, 1, 0, 0);
	}
}
