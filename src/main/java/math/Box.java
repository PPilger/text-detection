package math;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoxPoints;

import java.util.Arrays;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;

public class Box {
	private Vector[] corners;
	private Vector min;
	private Vector max;

	private Vector center;
	private double width;
	private double height;
	private Angle180 angle;

	public Box(Vector center, double width, double height,
			Angle180 angle) {
		this(new CvBox2D(cvPoint2D32f(center.x, center.y), cvSize2D32f(width,
				height), (float) angle.getDegrees()));
	}
	
	public Box(double xcenter, double ycenter, double width, double height,
			double angle) {
		this(new CvBox2D(cvPoint2D32f(xcenter, ycenter), cvSize2D32f(width,
				height), (float) Math.toDegrees(angle)));
	}

	public Box(CvBox2D box) {
		this.corners = new Vector[4];

		float[] coords = new float[8];
		cvBoxPoints(box, coords);

		corners[0] = new Vector(coords[0], coords[1]);
		corners[1] = new Vector(coords[2], coords[3]);
		corners[2] = new Vector(coords[4], coords[5]);
		corners[3] = new Vector(coords[6], coords[7]);

		double xmin = corners[0].x;
		double ymin = corners[0].y;
		double xmax = 0;
		double ymax = 0;

		for (Vector corner : corners) {
			if (corner.x < xmin) {
				xmin = corner.x;
			} else if (xmax < corner.x) {
				xmax = corner.x;
			}

			if (corner.y < ymin) {
				ymin = corner.y;
			} else if (ymax < corner.y) {
				ymax = corner.y;
			}
		}

		this.min = new Vector(xmin, ymin);
		this.max = new Vector(xmax, ymax);

		this.center = new Vector(box.center().x(), box.center().y());
		this.width = box.size().width();
		this.height = box.size().height();
		this.angle = new Angle180(Math.toRadians(box.angle()));

		if (width < height) {
			double temp = width;
			this.width = height;
			this.height = temp;
			this.angle.rotate(Math.PI / 2);
		}
	}
	
	public Box makeBorder(double border) {
		return new Box(center, width + border, height + border, angle);
	}

	public Vector[] getCorners() {
		return corners;
	}

	public Vector getMin() {
		return min;
	}

	public Vector getMax() {
		return max;
	}

	public Vector getCenter() {
		return center;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getArea() {
		return width * height;
	}

	public Angle180 getAngle() {
		return angle;
	}

	public CvPoint getCvCenter() {
		return cvPoint((int) Math.round(center.x), (int) Math.round(center.y));
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

	@Override
	public String toString() {
		return center + ", " + width + "/" + height + ", " + angle.getDegrees()
				+ "°";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(corners);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box other = (Box) obj;
		if (!Arrays.equals(corners, other.corners))
			return false;
		return true;
	}
	
	public boolean intersects(Box other) {
		int ip = 3;
		for (int i = 0; i < 4; ip = i, i++) {
			Line line0 = new Line(this.corners[ip], this.corners[i]);

			int jp = 3;
			for (int j = 0; j < 4; jp = j, j++) {
				Line line1 = new Line(other.corners[jp], other.corners[j]);

				if (line0.intersects(line1)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public double distance(Box other) {
		double distance = Double.POSITIVE_INFINITY;

		Vector[][] corners = new Vector[2][];
		corners[0] = this.corners;
		corners[1] = other.corners;

		// distance is 0 if the two boxes intersect
		if(this.intersects(other)) {
			return 0;
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

				int iEdge0 = 3;
				for (int iEdge1 = 0; iEdge1 < 4; iEdge0 = iEdge1, iEdge1++) {
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

	public double intersectionArea(Box other) {
		Rotation rotation = new Rotation(-angle.getRadians());
		int[] corners0 = Vector.asIntArray(corners);
		int[] corners1 = Vector.asIntArray(other.corners);

		// the rectangles are transformed so that the first one is axis aligned
		// with one point at (0/0)
		{
			rotation.rotate(corners0);
			rotation.rotate(corners1);

			int xmin = corners0[0];
			int ymin = corners0[1];

			for (int i = 2; i < corners0.length; i += 2) {
				xmin = xmin < corners0[i] ? xmin : corners0[i];
				ymin = ymin < corners0[i + 1] ? ymin : corners0[i + 1];
			}

			for (int i = 0; i < corners1.length; i += 2) {
				corners1[i] -= xmin;
				corners1[i + 1] -= ymin;
			}
		}

		// src array is initialized with the corners of the other box
		// src and dst array can contain twice as many points as corners to
		// handle the worst case of intersection
		int[] srcCorners = Arrays.copyOf(corners1, corners1.length * 2);
		int srcLength = corners1.length;

		int[] dstCorners = new int[corners1.length * 2];
		int dstLength = 0;

		EdgeClipper[] clipperArr = { new Top(), new Left(), new Right(),
				new Bottom() };

		for (EdgeClipper clipper : clipperArr) {
			int x0 = srcCorners[srcLength - 2];
			int y0 = srcCorners[srcLength - 1];

			for (int i = 0; i < srcLength; i += 2) {
				int x1 = srcCorners[i];
				int y1 = srcCorners[i + 1];

				if (clipper.inside(x0, y0)) {
					// add corner
					dstCorners[dstLength] = x0;
					dstCorners[dstLength + 1] = y0;
					dstLength += 2;
				}

				if (clipper.inside(x0, y0) ^ clipper.inside(x1, y1)) {
					// add intersection point
					int[] ipoint = clipper.intersection(x0, y0, x1, y1);
					dstCorners[dstLength] = ipoint[0];
					dstCorners[dstLength + 1] = ipoint[1];
					dstLength += 2;
				}

				x0 = x1;
				y0 = y1;
			}

			// if less than 3 points are present after clipping, the
			// intersection-area is 0
			if (dstLength < 3 * 2) {
				return 0;
			}

			// prepare src and dst for the next iteration
			int[] temp0 = srcCorners;
			srcCorners = dstCorners;
			dstCorners = temp0;

			srcLength = dstLength;
			dstLength = 0;
		}

		// calculate area
		int area;
		{
			area = 0;

			int x0 = srcCorners[srcLength - 2];
			int y0 = srcCorners[srcLength - 1];

			for (int i = 0; i < srcLength; i += 2) {
				int x1 = srcCorners[i];
				int y1 = srcCorners[i + 1];

				area += (x0 - x1) * (y0 + y1);

				x0 = x1;
				y0 = y1;
			}

			area /= 2;
		}

		return area;
	}

	private interface EdgeClipper {
		public boolean inside(int x, int y);

		public int[] intersection(int x0, int y0, int x1, int y1);
	}

	private class Left implements EdgeClipper {
		@Override
		public boolean inside(int x, int y) {
			return x >= 0;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = 0;
			p[1] = (int) Math.round(y0 - x0 * (y1 - y0) / (double) (x1 - x0));
			// division by 0 is not possible if one of the points is inside and
			// the other one outside

			return p;
		}
	}

	private class Bottom implements EdgeClipper {
		@Override
		public boolean inside(int x, int y) {
			return y >= 0;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = (int) Math.round(x0 - y0 * (x1 - x0) / (double) (y1 - y0));
			p[1] = 0;

			return p;
		}
	}

	private class Right implements EdgeClipper {
		private int width = (int) Math.round(Box.this.width);

		@Override
		public boolean inside(int x, int y) {
			return x <= width;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = width;
			p[1] = (int) Math.round(y0 + (width - x0) * (y1 - y0)
					/ (double) (x1 - x0));

			return p;
		}
	}

	private class Top implements EdgeClipper {
		private int height = (int) Math.round(Box.this.height);

		@Override
		public boolean inside(int x, int y) {
			return y <= height;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = (int) Math.round(x0 + (height - y0) * (x1 - x0)
					/ (double) (y1 - y0));
			p[1] = height;

			return p;
		}
	}
}
